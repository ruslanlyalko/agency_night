package com.ruslanlyalko.agency.presentation.ui.main;

import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MainPresenter extends BasePresenter<MainView> {

    private User mUser = new User();
    private boolean mStartWithSettings;

    MainPresenter(final boolean startWithSettings) {
        mStartWithSettings = startWithSettings;
    }

    public boolean isStartWithSettings() {
        return mStartWithSettings;
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onFabClicked() {
        getView().fabClickedFragment();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }
}
