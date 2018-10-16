package com.ruslanlyalko.agency.presentation.ui.main.dashboard.order;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.order.adapter.ProjectSelectable;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

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

    OrderEditPresenter(User user, Order order, Date date) {
        if (user == null)
            throw new RuntimeException("User can't be empty");
        mUser = user;
        if (order == null) {
            order = new Order();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, 16);
            c.set(Calendar.MINUTE, 0);
            order.setDate(c.getTime());
        }
        mOrder = order;
    }

    public void onViewReady() {
        getView().showDate(mOrder.getDate());
        getView().showReportData(mOrder);
    }

    public void onSave(final String status, final List<ProjectSelectable> data) {
        //validate
        getView().showProgress();
        mOrder.setUserId(mUser.getKey());
        mOrder.setUserName(mUser.getName());
        mOrder.setUpdatedAt(new Date());
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

    public User getUser() {
        return mUser;
    }

    public Order getOrder() {
        return mOrder;
    }

    public void setReportDate(final Date date) {
        mOrder.setDate(date);
        getView().showDate(date);
    }
}
