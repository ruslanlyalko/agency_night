package com.ruslanlyalko.agency.presentation.ui.main.settings;

import android.arch.lifecycle.MutableLiveData;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */
public interface SettingsView extends BaseView<SettingsPresenter> {

    void showUser(MutableLiveData<User> myUserDate);

    void populateUserSettings(User user);
}
