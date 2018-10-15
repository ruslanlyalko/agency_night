package com.ruslanlyalko.agency.presentation.ui.main.dashboard;

import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.Report;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseFragment;
import com.ruslanlyalko.agency.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.adapter.ReportsAdapter;
import com.ruslanlyalko.agency.presentation.ui.main.dashboard.report.ReportEditActivity;
import com.ruslanlyalko.agency.presentation.utils.ColorUtils;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;
import com.ruslanlyalko.agency.presentation.view.OnReportClickListener;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WorkloadFragment extends BaseFragment<WorkloadPresenter> implements WorkloadView {

    private static final int RC_REPORT = 1001;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;

    private ReportsAdapter mReportsAdapter;

    public static WorkloadFragment newInstance() {
        Bundle args = new Bundle();
        WorkloadFragment fragment = new WorkloadFragment();
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
    public void showReportsOnCalendar(final MutableLiveData<List<Report>> reportsData) {
        reportsData.observe(this, reports -> {
            if (reports == null) return;
            getPresenter().setReports(reports);
            showCalendarsEvents();
        });
    }

    @Override
    public void showReports(List<Report> reports) {
        if (reports == null || reports.isEmpty())
            showFab();
        else
            hideFab();
        mReportsAdapter.setData(reports);
    }

    @Override
    public void showErrorAndStartLoginScreen() {
        Toast.makeText(getContext(), R.string.error_blocked, Toast.LENGTH_LONG).show();
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        getBaseActivity().finish();
    }

    @Override
    public void editReport(final User user, final Report report) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), user, report), RC_REPORT);
    }

    @Override
    public void startAddReportScreen(final User user, final Date date) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), user, date), RC_REPORT);
    }

    @Override
    public void showWrongDateOnMobileError() {
        showError(getString(R.string.error_check_date));
    }

    void showCalendarsEvents() {
        mCalendarView.removeAllEvents();
        List<Report> reports = getPresenter().getReports();
        for (Report report : reports) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(),
                    ColorUtils.getTextColorByStatus(getResources(), report.getStatus())), report.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

    private void setupAdapters() {
        mReportsAdapter = new ReportsAdapter(new OnReportClickListener() {
            @Override
            public void onReportClicked(final View view, final int position) {
                if (mReportsAdapter.getData().size() > position)
                    getPresenter().onReportLongClicked(mReportsAdapter.getData().get(position));
            }

            @Override
            public void onReportRemoveClicked(final View view, final int position) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setMessage(R.string.text_delete);
                build.setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    if (mReportsAdapter.getData().size() > position)
                        getPresenter().onReportDeleteClicked(mReportsAdapter.getData().get(position));
                    dialog.dismiss();
                });
                build.setNegativeButton(R.string.action_cancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                build.show();
            }
        });
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
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
        mReportsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_workload;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new WorkloadPresenter());
    }

    @Override
    protected void onViewReady(final Bundle state) {
        setToolbarTitle(R.string.app_name);
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
