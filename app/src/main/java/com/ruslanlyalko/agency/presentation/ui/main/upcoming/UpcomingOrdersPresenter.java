package com.ruslanlyalko.agency.presentation.ui.main.upcoming;

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
public class UpcomingOrdersPresenter extends BasePresenter<UpcomingOrdersView> {

    private User mUser;

    UpcomingOrdersPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        if (mUser.getIsAdmin())
            getView().showOrders(getDataManager().getAllOrders());
        else
            getView().showOrders(getDataManager().getAllMyOrders());
    }

    public void setReports(final List<Order> orders) {
        List<Order> listVacationOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getDate().after(new Date())) {
                listVacationOrders.add(order);
            }
        }
        getView().setReportsToAdapter(listVacationOrders);
    }

    public void onReportPhoneClicked(final Order order) {
        getView().dialNumber(order.getName(), order.getPhone());
    }

    public void onReportDeleteClicked(final Order order) {
        order.setUpdatedAt(new Date());
        getDataManager().saveOrder(order)
                .addOnSuccessListener(aVoid -> getDataManager().removeOrder(order));
    }

    public void onReportLongClicked(final Order order) {
        if (order.getDate().before(DateUtils.get1DaysAgo().getTime())) return;
        getView().editReport(mUser, order);
    }

    public void onFabClicked() {
        getView().startAddReportScreen(mUser, DateUtils.get1DaysForward().getTime());
    }

    public void onFilterClicked() {
    }

    public User getUser() {
        return mUser;
    }
}
