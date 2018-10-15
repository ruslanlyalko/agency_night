package com.ruslanlyalko.agency.presentation.ui.main.dashboard.order;

import android.text.TextUtils;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.order.adapter.ProjectSelectable;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class OrderEditPresenter extends BasePresenter<OrderEditView> {

    private final User mUser;
    private final Order mOrder;
    private Date mDateTo;
    private boolean mDateStateOneDay = true;
    private boolean mAddProjectMode = true;
    private int mPosition;

    OrderEditPresenter(User user, Order order, Date date) {
        if (user == null)
            throw new RuntimeException("User can't be empty");
        mUser = user;
        if (order == null) {
            order = new Order();
            order.setDate(date);
            order.setStatus("");
        }
        mOrder = order;
        mDateTo = order.getDate();
        if (mOrder.getDate().after(DateUtils.get1DaysForward().getTime())
                && !(mOrder.getStatus().startsWith("Day") || mOrder.getStatus().startsWith("Vacation"))) {
            mOrder.setStatus("Vacations");
        }
    }

    public void onViewReady() {
        getView().showDateFrom(mOrder.getDate());
        getView().showDateTo(mDateTo);
        getView().showReportData(mOrder);
    }

    public void onSave(final String status, final List<ProjectSelectable> data) {
        boolean allowSaveData = !(status.startsWith("Day")
                || status.startsWith("Vacation")
                || status.startsWith("No")
                || status.startsWith("Sick"));
        if (data.size() > 0 && allowSaveData) {
            mOrder.setP1(data.get(0).getTitle());
            mOrder.setT1(data.get(0).getSpent());
        } else {
            mOrder.setP1("");
            mOrder.setT1(0);
        }
        if (data.size() > 1 && allowSaveData) {
            mOrder.setP2(data.get(1).getTitle());
            mOrder.setT2(data.get(1).getSpent());
        } else {
            mOrder.setP2("");
            mOrder.setT2(0);
        }
        if (data.size() > 2 && allowSaveData) {
            mOrder.setP3(data.get(2).getTitle());
            mOrder.setT3(data.get(2).getSpent());
        } else {
            mOrder.setP3("");
            mOrder.setT3(0);
        }
        if (data.size() > 3 && allowSaveData) {
            mOrder.setP4(data.get(3).getTitle());
            mOrder.setT4(data.get(3).getSpent());
        } else {
            mOrder.setP4("");
            mOrder.setT4(0);
        }
        //
        if (hasTwoSameProjects()) {
            getView().errorCantHasTwoEqualsProjects();
            return;
        }
        if (status.startsWith("Work") && getTotalHoursSpent(mOrder) == 0) {
            getView().errorCantBeZero();
            return;
        }
        if (status.startsWith("Work") && getTotalHoursSpent(mOrder) > 16) {
            getView().errorCantBeMoreThan16();
            return;
        }
        getView().showProgress();
        mOrder.setUserId(mUser.getKey());
        mOrder.setUserName(mUser.getName());
        mOrder.setUpdatedAt(new Date());
        mOrder.setStatus(status);
        if (!mDateStateOneDay) {
            saveFewReports();
            return;
        }
        mOrder.setKey(DateUtils.toString(mOrder.getDate(), "yyyyMMdd_'" + mUser.getKey() + "'"));
        getDataManager().saveOrder(mOrder)
                .addOnSuccessListener(aVoid -> {
                    if (getView() == null) return;
                    getView().afterSuccessfullySaving();
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().showWrongDateOnMobileError();
                    getView().hideProgress();
                });
    }

    private void saveFewReports() {
        Calendar start = Calendar.getInstance();
        start.setTime(mOrder.getDate());
        Calendar end = Calendar.getInstance();
        end.setTime(mDateTo);
        end.add(Calendar.DAY_OF_MONTH, 1);
        int count = 0;
        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                mOrder.setDate(date);
                mOrder.setKey(DateUtils.toString(mOrder.getDate(), "yyyyMMdd_'" + mUser.getKey() + "'"));
                getDataManager().saveOrder(mOrder);
                count++;
            }
        }
        getView().afterSuccessfullyRangeSaving(count);
    }

    private boolean hasTwoSameProjects() {
        List<String> names = new ArrayList<>();
        if (!TextUtils.isEmpty(mOrder.getP1())) {
            names.add(mOrder.getP1());
        }
        if (!TextUtils.isEmpty(mOrder.getP2())) {
            names.add(mOrder.getP2());
        }
        if (!TextUtils.isEmpty(mOrder.getP3())) {
            names.add(mOrder.getP3());
        }
        if (!TextUtils.isEmpty(mOrder.getP4())) {
            names.add(mOrder.getP4());
        }
        for (int i = 0; i < names.size() - 1; i++) {
            for (int j = i + 1; j < names.size(); j++) {
                if (i != j && names.get(i).equals(names.get(j)))
                    return true;
            }
        }
        return false;
    }

    private int getTotalHoursSpent(final Order order) {
        return order.getT1() + order.getT2() + order.getT3() + order.getT4();
    }

    public User getUser() {
        return mUser;
    }

    public Order getOrder() {
        return mOrder;
    }

    public void setReportDate(final Date date) {
        mOrder.setDate(date);
        getView().showDateFrom(date);
        if (date.after(mDateTo)) {
            setDateTo(date);
        }
    }

    public Date getDateTo() {
        return mDateTo;
    }

    public void setDateTo(final Date dateTo) {
        mDateTo = dateTo;
        getView().showDateTo(dateTo);
        if (dateTo.before(mOrder.getDate())) {
            setReportDate(mDateTo);
        }
    }

    public void toggleDateState() {
        if (mDateStateOneDay) {
            mDateStateOneDay = false;
        } else {
            mDateStateOneDay = true;
        }
        getView().showDateState(mDateStateOneDay);
    }

    public void setAddProjectMode() {
        mAddProjectMode = true;
    }

    public void setChangeProjectMode(final int position) {
        mAddProjectMode = false;
        mPosition = position;
    }

    public void changeProject(final String title) {
        if (mAddProjectMode) {
            getView().addProject(title);
        } else {
            getView().changeProject(title, mPosition);
        }
    }
}
