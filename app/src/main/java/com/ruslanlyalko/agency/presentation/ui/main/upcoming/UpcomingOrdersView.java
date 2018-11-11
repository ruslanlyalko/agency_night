package com.ruslanlyalko.agency.presentation.ui.main.upcoming;

import android.arch.lifecycle.MutableLiveData;
import android.util.SparseIntArray;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UpcomingOrdersView extends BaseView<UpcomingOrdersPresenter> {

    void editReport(User user, Order order);

    void dialNumber(String name, String phone);

    void showOrders(MutableLiveData<List<Order>> vacationReportsData);

    void showReportsByYear(final Date firstWorkingDate, SparseIntArray years);

    void setReportsToAdapter(List<Order> list);

    void startAddReportScreen(User user, Date date);
}
