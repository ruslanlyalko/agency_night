package com.ruslanlyalko.agency.presentation.ui.main.profile.edit;

import android.text.TextUtils;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProfileEditPresenter extends BasePresenter<ProfileEditView> {

    private User mUser;

    ProfileEditPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onSave(String phone, final String newPassword) {
        getView().showProgress();
        if (!TextUtils.isEmpty(newPassword)) {
            getDataManager().changePassword(newPassword)
                    .addOnFailureListener(e -> {
                        if (getView() == null) return;
                        getView().showError(e.getLocalizedMessage());
                        getView().hideProgress();
                    })
                    .addOnSuccessListener(aVoid -> {
                        saveUserData(phone);
                    });
            return;
        }
        saveUserData(phone);
    }

    private void saveUserData(String phone) {
        mUser.setPhone(phone);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid1 -> {
                    if (getView() == null) return;
                    getView().afterSuccessfullySaving();
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().hideProgress();
                });
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }
}
