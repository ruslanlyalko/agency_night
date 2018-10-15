package com.ruslanlyalko.agency.presentation.ui.main.users;

import android.arch.lifecycle.MutableLiveData;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UsersView extends BaseView<UsersPresenter> {

    void showUsers(MutableLiveData<List<User>> users);

    void starUserAddScreen();
}
