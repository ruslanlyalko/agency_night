package com.ruslanlyalko.agency.presentation.ui.main.users.details;

import android.util.SparseIntArray;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserDetailsPresenter extends BasePresenter<UserDetailsView> {

    private User mUser;

    UserDetailsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showUserDetails(mUser);
        getView().showReports(getDataManager().getVacationReports(mUser));
    }

    public void setReports(final List<Order> orders) {
        SparseIntArray mYears = new SparseIntArray();
        for (Order order : orders) {
            int yearInd = DateUtils.getYearIndex(order.getDate(), mUser.getFirstWorkingDate());
            int value = mYears.get(yearInd);
            value = value + 1;
            mYears.append(yearInd, value);
        }
        getView().showReportsByYear(mUser.getFirstWorkingDate(), mYears);
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        getView().showUserDetails(mUser);
    }
}
