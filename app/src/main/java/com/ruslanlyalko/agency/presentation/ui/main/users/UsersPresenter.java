package com.ruslanlyalko.agency.presentation.ui.main.users;

import com.ruslanlyalko.agency.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UsersPresenter extends BasePresenter<UsersView> {

    UsersPresenter() {
    }

    public void onViewReady() {
        getView().showUsers(getDataManager().getAllUsers());
    }

    public void onAddClicked() {
        getView().starUserAddScreen();
    }
}
