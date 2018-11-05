package com.ruslanlyalko.agency.presentation.ui.main.my_orders;

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
public class MyOrdersPresenter extends BasePresenter<MyOrdersView> {

    private User mUser;

    MyOrdersPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showOrders(getDataManager().getAllMyOrders());
    }

    public void setReports(final List<Order> orders) {
        List<Order> listVacationOrders = new ArrayList<>();
//        SparseIntArray years = new SparseIntArray();
        for (Order order : orders) {
            if (order.getDate().after(new Date())) {
                listVacationOrders.add(order);
//                int yearInd = DateUtils.getYearIndex(order.getDate(), mUser.getFirstWorkingDate());
//                int value = years.get(yearInd);
//                value = value + 1;
//                years.append(yearInd, value);
            }
        }
        getView().setReportsToAdapter(listVacationOrders);
//        getView().showReportsByYear(mUser.getFirstWorkingDate(), years);
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
}
