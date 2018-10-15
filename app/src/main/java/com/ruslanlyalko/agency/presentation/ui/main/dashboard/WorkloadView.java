package com.ruslanlyalko.agency.presentation.ui.main.dashboard;

import android.arch.lifecycle.MutableLiveData;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface WorkloadView extends BaseView<WorkloadPresenter> {

    void showUser(MutableLiveData<User> user);

    void showReportsOnCalendar(MutableLiveData<List<Order>> reports);

    void showReports(List<Order> orders);

    void showErrorAndStartLoginScreen();

    void editReport(User user, Order order);

    void startAddReportScreen(final User user, final Date date);

    void showWrongDateOnMobileError();
}
