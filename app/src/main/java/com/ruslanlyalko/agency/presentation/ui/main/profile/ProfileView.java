package com.ruslanlyalko.agency.presentation.ui.main.profile;

import android.arch.lifecycle.MutableLiveData;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProfileView extends BaseView<ProfilePresenter> {

    void startProfileEditScreen();

    void showUser(MutableLiveData<User> myUserData);

    void populateUser(User user);

    void showLoginScreen();
}
