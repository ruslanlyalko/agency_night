package com.ruslanlyalko.agency.presentation.ui.main.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseFragment;
import com.ruslanlyalko.agency.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.adapter.OrdersAdapter;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.order.OrderEditActivity;
import com.ruslanlyalko.agency.presentation.utils.ColorUtils;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;
import com.ruslanlyalko.agency.presentation.view.OnReportClickListener;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DashboardFragment extends BaseFragment<DashboardPresenter> implements DashboardView {

    private static final int RC_REPORT = 1001;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.recycler_orders) RecyclerView mRecyclerOrders;
    @BindView(R.id.text_placeholder) TextView mTextPlaceholder;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;
    @BindView(R.id.text_month) TextSwitcher mTextMonth;
    @BindView(R.id.layout_orders) RelativeLayout mLayoutOrders;

    private Date mPrevDate = new Date();
    private String mPrevDateStr = "";
    private OrdersAdapter mOrdersAdapter;
    private float mOldX;
    private float mOldY;
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        private static final int MIN_DISTANCE = 150;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            float x2 = event.getX();
            float y2 = event.getY();
            float deltaX = x2 - mOldX;
            float deltaY = y2 - mOldY;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOldX = event.getX();
                    mOldY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int days = (int) deltaX / MIN_DISTANCE;
                    int weeks = (int) deltaY / MIN_DISTANCE;
                    Date dateMove = getPresenter().getDate();
                    dateMove = DateUtils.addDay(dateMove, Math.max(-1, Math.min(1, days)));
                    dateMove = DateUtils.addWeek(dateMove, Math.max(-1, Math.min(1, weeks)));
                    mCalendarView.setCurrentDate(dateMove);
                    break;
                case MotionEvent.ACTION_UP:
                    int days1 = (int) deltaX / MIN_DISTANCE;
                    int weeks1 = (int) deltaY / MIN_DISTANCE;
                    Date dateClicked = getPresenter().getDate();
                    dateClicked = DateUtils.addDay(dateClicked, Math.max(-1, Math.min(1, days1)));
                    dateClicked = DateUtils.addWeek(dateClicked, Math.max(-1, Math.min(1, weeks1)));
                    if (!DateUtils.dateEquals(getPresenter().getDate(), dateClicked)) {
                        mCalendarView.setCurrentDate(dateClicked);
                        getPresenter().fetchReportsForDate(dateClicked);
                        setNewDate(dateClicked);
                    }
//                    else {
//                        v.performClick();
//                    }
                    break;
            }
            return true;
        }
    };

    public static DashboardFragment newInstance() {
        Bundle args = new Bundle();
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupCalendar() {
        mCalendarView.setUseThreeLetterAbbreviation(true);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.displayOtherMonthDays(true);
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(final Date dateClicked) {
                getPresenter().fetchReportsForDate(dateClicked);
            }

            @Override
            public void onMonthScroll(final Date firstDayOfNewMonth) {
                setNewDate(firstDayOfNewMonth);
            }
        });
    }

    private void setNewDate(Date newDate) {
        String month = DateUtils.getMonth(newDate);
        if (month.equals(mPrevDateStr)) return;
        if (TextUtils.isEmpty(mPrevDateStr) && month.equals(DateUtils.getMonth(new Date()))) return;
        if (newDate.before(mPrevDate)) {
            Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
            mTextMonth.setInAnimation(in);
            mTextMonth.setOutAnimation(out);
        } else {
            Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
            Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
            mTextMonth.setInAnimation(in);
            mTextMonth.setOutAnimation(out);
        }
        mTextMonth.setText(month);
        mPrevDateStr = month;
        mPrevDate = newDate;
    }

    @Override
    public void showUser(final MutableLiveData<User> userData) {
        userData.observe(this, user -> {
            if (user == null) return;
            getPresenter().setUser(user);
            mOrdersAdapter.setUser(user);
        });
    }

    @Override
    public void showReportsOnCalendar(final MutableLiveData<List<Order>> reportsData) {
        reportsData.observe(this, reports -> {
            if (reports == null) return;
            getPresenter().setOrders(reports);
            showCalendarsEvents();
        });
    }

    @Override
    public void showReports(List<Order> orders) {
        showFab();
        mOrdersAdapter.setData(orders);
        mTextPlaceholder.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorAndStartLoginScreen() {
        Toast.makeText(getContext(), R.string.error_blocked, Toast.LENGTH_LONG).show();
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        getBaseActivity().finish();
    }

    @Override
    public void editReport(final User user, final Order order) {
        startActivityForResult(OrderEditActivity.getLaunchIntent(getContext(), user, order), RC_REPORT);
    }

    @Override
    public void startAddReportScreen(final User user, final Date date) {
        startActivityForResult(OrderEditActivity.getLaunchIntent(getContext(), user, date), RC_REPORT);
    }

    @Override
    public void showWrongDateOnMobileError() {
        showError(getString(R.string.error_check_date));
    }

    @Override
    public void dialNumber(final String name, final String phone) {
        Uri number = Uri.parse("tel:" + phone);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
        if (!TextUtils.isEmpty(name)) {
            showMessage(name);
        }
    }

    void showCalendarsEvents() {
        mCalendarView.removeAllEvents();
        List<Order> orders = getPresenter().getOrders();
        for (Order order : orders) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(),
                    ColorUtils.getTextColorByStatus(getResources(), order.getUserId().equals(getPresenter().getUser().getKey()))), order.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupAdapters() {
        mOrdersAdapter = new OrdersAdapter(new OnReportClickListener() {
            @Override
            public void onPhoneClicked(final View view, final int position) {
                if (mOrdersAdapter.getData().size() > position)
                    getPresenter().onReportPhoneClicked(mOrdersAdapter.getData().get(position));
            }

            @Override
            public void onReportClicked(final View view, final int position) {
                if (mOrdersAdapter.getData().size() > position)
                    getPresenter().onReportLongClicked(mOrdersAdapter.getData().get(position));
            }

            @Override
            public void onReportRemoveClicked(final View view, final int position) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setMessage(R.string.text_delete);
                build.setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    if (mOrdersAdapter.getData().size() > position)
                        getPresenter().onReportDeleteClicked(mOrdersAdapter.getData().get(position));
                    dialog.dismiss();
                });
                build.setNegativeButton(R.string.action_cancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                build.show();
            }
        });
        mRecyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerOrders.setAdapter(mOrdersAdapter);
        mLayoutOrders.setOnTouchListener(mOnTouchListener);
//        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerOrders, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrdersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_dashboard;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new DashboardPresenter());
    }

    @Override
    protected void onViewReady(final Bundle state) {
        setToolbarTitle(R.string.app_name);
        getBaseActivity().showFab();
        setupAdapters();
        setupCalendar();
        getPresenter().onViewReady();
    }

    @Override
    public void onFabClicked() {
        getPresenter().onFabClicked();
    }

    @OnClick(R.id.title)
    public void onClick() {
        mCalendarView.setCurrentDate(new Date());
        getPresenter().fetchReportsForDate(new Date());
        setNewDate(new Date());
    }
}
