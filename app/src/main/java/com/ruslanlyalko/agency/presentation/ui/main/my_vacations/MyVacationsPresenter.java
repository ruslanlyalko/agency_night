package com.ruslanlyalko.agency.presentation.ui.main.my_vacations;

import android.util.SparseIntArray;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyVacationsPresenter extends BasePresenter<MyVacationsView> {

    private User mUser;

    MyVacationsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showReports(getDataManager().getAllMyReports());
    }

    public void setReports(final List<Order> orders) {
        List<Order> listVacationOrders = new ArrayList<>();
        SparseIntArray years = new SparseIntArray();
        for (Order order : orders) {
            if (order.getStatus().startsWith("Day")
                    || order.getStatus().startsWith("Vacation")
                    || order.getStatus().startsWith("Sick")) {
                listVacationOrders.add(order);
                int yearInd = DateUtils.getYearIndex(order.getDate(), mUser.getFirstWorkingDate());
                int value = years.get(yearInd);
                value = value + 1;
                years.append(yearInd, value);
            }
        }
        getView().setReportsToAdapter(listVacationOrders);
        getView().showReportsByYear(mUser.getFirstWorkingDate(), years);
    }
}
