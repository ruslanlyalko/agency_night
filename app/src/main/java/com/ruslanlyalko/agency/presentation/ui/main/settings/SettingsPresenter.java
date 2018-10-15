package com.ruslanlyalko.agency.presentation.ui.main.settings;

import android.text.TextUtils;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private User mUser;

    SettingsPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void setUser(final User user) {
        mUser = user;
        getView().populateUserSettings(user);
    }

    public void saveUserNotificationSettings(final String notificationHour) {
        if (mUser == null || TextUtils.isEmpty(notificationHour)) return;
        mUser.setNotificationHour(notificationHour.split(":")[0]);
        getDataManager().saveUser(mUser);
    }
}
