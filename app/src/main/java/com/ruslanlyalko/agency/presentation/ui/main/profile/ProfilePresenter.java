package com.ruslanlyalko.agency.presentation.ui.main.profile;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProfilePresenter extends BasePresenter<ProfileView> {

    private User mUser;

    ProfilePresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onEditClicked() {
        getView().startProfileEditScreen();
    }

    public void onLogoutClicked() {
        getDataManager().logout();
        getView().showLoginScreen();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        getView().populateUser(user);
    }
}
