package com.ruslanlyalko.agency.presentation.ui.main.profile.edit;

import android.arch.lifecycle.MutableLiveData;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProfileEditView extends BaseView<ProfileEditPresenter> {

    void showUser(MutableLiveData<User> user);

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();
}
