package com.ruslanlyalko.agency.presentation.ui.main.users.edit;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserEditPresenter extends BasePresenter<UserEditView> {

    private final User mUser;

    UserEditPresenter(User user) {
        if (user == null)
            throw new RuntimeException("User can't be empty");
        mUser = user;
    }

    public void onViewReady() {
        getView().showUserData(mUser);
    }

    public void onSave(String name, String phone, String comments, boolean isBlocked) {
        getView().showProgress();
        mUser.setName(name);
        mUser.setPhone(phone);
        mUser.setComments(comments);
        mUser.setIsBlocked(isBlocked);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid -> {
                    if (getView() == null) return;
                    getView().afterSuccessfullySaving(mUser);
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().hideProgress();
                });
    }

    public User getUser() {
        return mUser;
    }
}
