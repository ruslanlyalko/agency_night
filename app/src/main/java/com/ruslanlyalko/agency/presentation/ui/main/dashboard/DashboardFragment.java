package com.ruslanlyalko.agency.presentation.ui.main.dashboard;

import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class DashboardFragment extends BaseFragment<DashboardPresenter> implements DashboardView {

    private static final int RC_REPORT = 1001;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;

    private OrdersAdapter mOrdersAdapter;

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
                setToolbarTitle(getString(R.string.app_name) + " (" + DateUtils.getMonth(firstDayOfNewMonth) + ")");
            }
        });
    }

    @Override
    public void showUser(final MutableLiveData<User> userData) {
        userData.observe(this, user -> {
            if (user == null) return;
            getPresenter().setUser(user);
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
                    ColorUtils.getTextColorByStatus(getResources(), null)), order.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

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
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mOrdersAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerReports, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_REPORT) {
//            getPresenter().fetchReportsForDate();
        }
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
        setToolbarTitle(getString(R.string.app_name));
    }
}
