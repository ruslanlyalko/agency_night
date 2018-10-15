package com.ruslanlyalko.agency.presentation.ui.main.dashboard;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class DashbaordPresenter extends BasePresenter<DashboardView> {

    private User mUser;
    private Date mDate = new Date();
    private List<Order> mOrders = new ArrayList<>();

    DashbaordPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().showReportsOnCalendar(getDataManager().getAllMyOrders());
    }

    private List<Order> getReportsForCurrentDate() {
        List<Order> result = new ArrayList<>();
        for (Order r : mOrders) {
            if (r.getDate().after(DateUtils.getStart(mDate))
                    && r.getDate().before(DateUtils.getEnd(mDate))) {
                result.add(r);
            }
        }
        return result;
    }

    public void fetchReportsForDate(Date date) {
        mDate = DateUtils.getDate(date, 1, 1);
        getView().showReports(getReportsForCurrentDate());
    }

    public void onReportDeleteClicked(final Order order) {
        order.setUpdatedAt(new Date());
        getDataManager().saveOrder(order)
                .addOnSuccessListener(aVoid -> getDataManager().removeOrder(order)
                        .addOnCompleteListener(task -> {
                            if (getView() == null) return;
                            getView().showReports(getReportsForCurrentDate());
                        }))
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().showWrongDateOnMobileError();
                });
    }

    public void onReportLongClicked(final Order order) {
        if (order.getDate().before(DateUtils.get1DaysAgo().getTime())) return;
        getView().editReport(mUser, order);
    }

    public void onFabClicked() {
        getView().startAddReportScreen(mUser, mDate);
    }

    public Date getDate() {
        return mDate;
    }

    public List<Order> getOrders() {
        return mOrders;
    }

    public void setOrders(final List<Order> orders) {
        mOrders = orders;
        getView().showReports(getReportsForCurrentDate());
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        if (mUser.getIsBlocked()) {
            getAuth().signOut();
            getView().showErrorAndStartLoginScreen();
        }
    }
}
