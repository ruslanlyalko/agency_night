package com.ruslanlyalko.agency.presentation.ui.main.dashboard.order;

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
public interface OrderEditView extends BaseView<OrderEditPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();

    void showReportData(Order order);

    void showDate(Date date);

    void showSpinnerUsersData(final User user1, MutableLiveData<List<User>> usersData);

    void showUnSaved();
}
