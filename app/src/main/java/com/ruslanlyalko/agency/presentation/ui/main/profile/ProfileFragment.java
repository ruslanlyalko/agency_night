package com.ruslanlyalko.agency.presentation.ui.main.profile;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseFragment;
import com.ruslanlyalko.agency.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.agency.presentation.ui.main.profile.edit.ProfileEditActivity;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ProfileFragment extends BaseFragment<ProfilePresenter> implements ProfileView {

    private static final String KEY_USER = "user";
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_email) TextView mTextEmail;
    @BindView(R.id.text_phone) TextView mTextPhone;
    @BindView(R.id.text_card) TextView mTextCard;
    @BindView(R.id.text_birthday) TextView mTextBirthday;
    @BindView(R.id.text_first) TextView mTextFirst;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public void startProfileEditScreen() {
        startActivity(ProfileEditActivity.getLaunchIntent(getContext()));
    }

    @Override
    public void showUser(final MutableLiveData<User> myUserData) {
        myUserData.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void populateUser(final User user) {
        mTextName.setText(user.getName());
        mTextEmail.setText(user.getEmail());
        if (TextUtils.isEmpty(user.getPhone())) {
            mTextPhone.setText(R.string.text_not_specified);
        } else {
            mTextPhone.setText(user.getPhone());
        }
        if (TextUtils.isEmpty(user.getCard())) {
            mTextCard.setText(R.string.text_not_specified);
        } else {
            mTextCard.setText(user.getCard());
        }
        mTextBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
        mTextFirst.setText(DateUtils.toStringStandardDate(user.getFirstWorkingDate()));
    }

    @Override
    public void showLoginScreen() {
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                getPresenter().onEditClicked();
                return true;
            case R.id.action_logout:
                getPresenter().onLogoutClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new ProfilePresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_profile);
        getBaseActivity().hideFab();
        getPresenter().onViewReady();
    }

    @OnClick({R.id.text_phone, R.id.text_card})
    public void onClick(View view) {
        User user = getPresenter().getUser();
        switch (view.getId()) {
            case R.id.text_phone:
                if (!TextUtils.isEmpty(user.getPhone())) {
                    copyToClipboard(user.getPhone());
                    showMessage(getString(R.string.text_copied));
                }
                break;
            case R.id.text_card:
                if (!TextUtils.isEmpty(user.getCard())) {
                    copyToClipboard(user.getCard());
                    showMessage(getString(R.string.text_copied));
                }
                break;
        }
    }

    @OnLongClick(R.id.text_card)
    public boolean onLongClick() {
        User user = getPresenter().getUser();
        if (!TextUtils.isEmpty(user.getCard())) {
            copyToClipboard(user.getCard() + "\n" + user.getName());
            showMessage(getString(R.string.text_copied));
        }
        return true;
    }
}
