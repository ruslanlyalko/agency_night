package com.ruslanlyalko.agency.presentation.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.multibackstack.BackStackActivity;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.DashboardFragment;
import com.ruslanlyalko.agency.presentation.ui.main.profile.ProfileFragment;
import com.ruslanlyalko.agency.presentation.ui.main.settings.SettingsFragment;
import com.ruslanlyalko.agency.presentation.ui.main.upcoming.UpcomingOrdersFragment;
import com.ruslanlyalko.agency.presentation.ui.main.users.UsersFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BackStackActivity<MainPresenter> implements MainView {

    private static final int TAB_PROFILE = 0;
    private static final int TAB_DASHBOARD = 1;
    private static final int TAB_VACATION = 2;
    private static final int TAB_USERS = 3;
    private static final int TAB_SETTINGS = 4;
    private static final String STATE_CURRENT_TAB_ID = "current_tab_id";
    private static final String KEY_SETTINGS = "settings";
    @BindView(R.id.bottom_app_bar) BottomAppBar mBottomAppBar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.image_menu) AppCompatImageView mImageMenu;
    @BindView(R.id.bottom_sheet) LinearLayout mLayoutBottomSheet;
    @BindView(R.id.image_logo) ImageView mImageLogo;
    @BindView(R.id.text_title) TextView mTextTitle;
    @BindView(R.id.text_subtitle) TextView mTextSubtitle;
    @BindView(R.id.text_letters) TextView mTextLetters;
    @BindView(R.id.navigation_list) NavigationView mNavigationList;
    @BindView(R.id.touch_outside) View mTouchOutSide;
    @BindView(R.id.layout_profile) RelativeLayout mLayoutProfile;
    private BottomSheetBehavior mSheetBehavior;
    private float mOldY;
    private Fragment mCurFragment;
    private int mCurTabId = TAB_DASHBOARD;

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        private static final int MIN_DISTANCE = 100;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOldY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float y2 = event.getY();
                    float deltaY = y2 - mOldY;
//                    if (deltaY > MIN_DISTANCE) {
//                        // top2bottom
//                    } else
                    if (deltaY < (0 - MIN_DISTANCE)) {
                        onHomeClicked();
                    }
//                    else {
//                        v.performClick();
//                    }
                    break;
            }
            return false;
        }
    };

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static Intent getLaunchIntent(Context context, boolean startWithSettings) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_SETTINGS, startWithSettings);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        getPresenter().onFabClicked();
    }

    @Override
    public void showUser(final MutableLiveData<User> myUserData) {
        myUserData.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void showMenu(final User user) {
        mTextTitle.setText(user.getName());
        mTextSubtitle.setText(user.getEmail());
        mTextLetters.setText(getAbbreviation(user.getName()));
        mNavigationList.getMenu().clear();
        mLayoutProfile.setSelected(mCurTabId == TAB_PROFILE);
        mTextTitle.setSelected(mCurTabId == TAB_PROFILE);
        mNavigationList.inflateMenu(getPresenter().getUser().getIsAdmin() ? R.menu.menu_nav_admin : R.menu.menu_nav);
        mNavigationList.setCheckedItem(getMenuIdByTab(mCurTabId));
        mNavigationList.setNavigationItemSelectedListener(menuItem -> {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            switch (menuItem.getItemId()) {
                case R.id.action_workload:
                    onTabSelected(TAB_DASHBOARD);
                    return true;
                case R.id.action_vacations:
                    onTabSelected(TAB_VACATION);
                    return true;
                case R.id.action_users:
                    onTabSelected(TAB_USERS);
                    return true;
                case R.id.action_settings:
                    onTabSelected(TAB_SETTINGS);
                    return true;
                default:
                    return false;
            }
        });
        mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void fabClickedFragment() {
//        if (mCurTabId == TAB_DASHBOARD)
        onFabClickedFragment();
//        else onBackPressed();
    }

    private int getMenuIdByTab(final int tabId) {
        switch (tabId) {
            case TAB_PROFILE:
                return TAB_PROFILE;
            case TAB_DASHBOARD:
                return R.id.action_workload;
            case TAB_VACATION:
                return R.id.action_vacations;
            case TAB_USERS:
                return R.id.action_users;
            case TAB_SETTINGS:
                return R.id.action_settings;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void onTabSelected(int tabId) {
        if (mCurFragment != null) {
            pushFragmentToBackStack(mCurTabId, mCurFragment);
        }
        mCurTabId = tabId;
        Fragment fragment = popFragmentFromBackStack(mCurTabId);
        if (fragment == null) {
            fragment = rootTabFragment(mCurTabId);
        }
        replaceFragment(fragment);
    }

    @NonNull
    private Fragment rootTabFragment(int tabId) {
        switch (tabId) {
            case TAB_PROFILE:
                return ProfileFragment.newInstance();
            case TAB_DASHBOARD:
                return DashboardFragment.newInstance();
            case TAB_VACATION:
                return UpcomingOrdersFragment.newInstance(getPresenter().getUser());
            case TAB_USERS:
                return UsersFragment.newInstance();
            case TAB_SETTINGS:
                return SettingsFragment.newInstance();
            default:
                throw new IllegalArgumentException();
        }
    }

    public void toggleFab(final boolean isWorkloadTab) {
        if (isWorkloadTab) {
//            if (mBottomAppBar.getFabAlignmentMode() != BottomAppBar.FAB_ALIGNMENT_MODE_CENTER) {
//            mImageMenu.setVisibility(View.VISIBLE);
            //        hideFab();
//                mBottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
//                mFab.setImageResource(R.drawable.ic_add_fab);
//            }
        } else {
//            hideFab();
//            mBottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
//            mFab.setImageResource(R.drawable.ic_reply);
            //showFab();
        }
    }

    private String getAbbreviation(final String name) {
        if (TextUtils.isEmpty(name)) return "";
        String[] list = name.split(" ");
        String result = list[0].substring(0, 1);
        if (list.length > 1)
            result = result + list[1].substring(0, 1);
        return result.toUpperCase();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                onHomeClicked();
                return true;
            case R.id.action_vacations:
                onTabSelected(TAB_VACATION);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mBottomAppBar;
    }

    @Override
    public void onBackPressed() {
        if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
//            Pair<Integer, Fragment> pair = popFragmentFromBackStack();
//            if (pair != null) {
//                assert pair.first != null;
//                assert pair.second != null;
//                backTo(pair.first, pair.second);
//            } else
            if (mCurTabId != TAB_DASHBOARD) {
                onTabSelected(TAB_DASHBOARD);
            } else
                super.onBackPressed();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onHomeClicked() {
        showMenu(getPresenter().getUser());
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MainPresenter(intent.getBooleanExtra(KEY_SETTINGS, false)));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onViewReady(final Bundle state) {
        mImageMenu.setOnTouchListener(mOnTouchListener);
        mBottomAppBar.setOnTouchListener(mOnTouchListener);
        mBottomAppBar.setNavigationIcon(null);
        mSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View view, final int i) {
            }

            @Override
            public void onSlide(@NonNull final View view, final float v) {
                mTouchOutSide.setAlpha(v / 2f);
                getWindow().setStatusBarColor(adjustAlpha(ContextCompat.getColor(getContext(),
                        R.color.colorBackgroundTouchOutside), v / 2f));
                if (v > 0.001f)
                    mTouchOutSide.setVisibility(View.VISIBLE);
                else
                    mTouchOutSide.setVisibility(View.GONE);
            }
        });
        getPresenter().onViewReady();
        if (state == null) {
            mCurTabId = getPresenter().isStartWithSettings() ? TAB_SETTINGS : TAB_DASHBOARD;
            showFragment(rootTabFragment(mCurTabId));
        }
    }

    @Override
    public void showFab() {
        mFab.show();
        mFab.setEnabled(true);
    }

    @Override
    public void hideFab() {
        mFab.hide();
        mFab.setEnabled(false);
    }

    public void showFragment(@NonNull Fragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        if (mCurFragment != null && addToBackStack) {
            pushFragmentToBackStack(mCurTabId, mCurFragment);
        }
        replaceFragment(fragment);
    }

    private void backTo(int tabId, @NonNull Fragment fragment) {
        if (tabId != mCurTabId) {
            mCurTabId = tabId;
            //select tab
        }
        replaceFragment(fragment);
        getSupportFragmentManager().executePendingTransactions();
    }

    private void backToRoot() {
        if (isRootTabFragment(mCurFragment, mCurTabId)) {
            return;
        }
        resetBackStackToRoot(mCurTabId);
        Fragment rootFragment = popFragmentFromBackStack(mCurTabId);
        assert rootFragment != null;
        backTo(mCurTabId, rootFragment);
    }

    private boolean isRootTabFragment(@NonNull Fragment fragment, int tabId) {
        return fragment.getClass() == rootTabFragment(tabId).getClass();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_TAB_ID, mCurTabId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        mCurTabId = savedInstanceState.getInt(STATE_CURRENT_TAB_ID);
    }

    protected void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        tr.replace(R.id.container, fragment);
        tr.commitAllowingStateLoss();
        mCurFragment = fragment;
    }

    @OnClick(R.id.image_menu)
    public void onMenuClick() {
        onHomeClicked();
    }

    @OnClick(R.id.layout_profile)
    public void onProfileClick() {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        onTabSelected(TAB_PROFILE);
    }

    @OnClick(R.id.touch_outside)
    public void onTouchClick() {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
