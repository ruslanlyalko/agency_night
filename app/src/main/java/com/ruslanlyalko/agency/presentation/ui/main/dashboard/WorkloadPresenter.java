package com.ruslanlyalko.agency.presentation.ui.main.dashboard;

import com.ruslanlyalko.agency.data.models.Report;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class WorkloadPresenter extends BasePresenter<WorkloadView> {

    private User mUser;
    private Date mDate = new Date();
    private List<Report> mReports = new ArrayList<>();

    WorkloadPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().showReportsOnCalendar(getDataManager().getAllMyReports());
    }

    private List<Report> getReportsForCurrentDate() {
        List<Report> result = new ArrayList<>();
        for (Report r : mReports) {
            if (r.getDate().after(DateUtils.getStart(mDate))
                    && r.getDate().before(DateUtils.getEnd(mDate))) {
                result.add(r);
            }
        }
        return result;
    }

    public void fetchReportsForDate(Date date) {
        mDate = DateUtils.getDate(date, 1, 1);
        getView().showReports(getReportsForCurrentDate());
    }

    public void onReportDeleteClicked(final Report report) {
        report.setUpdatedAt(new Date());
        getDataManager().saveReport(report)
                .addOnSuccessListener(aVoid -> getDataManager().removeReport(report)
                        .addOnCompleteListener(task -> {
                            if (getView() == null) return;
                            getView().showReports(getReportsForCurrentDate());
                        }))
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().showWrongDateOnMobileError();
                });
    }

    public void onReportLongClicked(final Report report) {
        if (report.getDate().before(DateUtils.get1DaysAgo().getTime())) return;
        getView().editReport(mUser, report);
    }

    public void onFabClicked() {
        getView().startAddReportScreen(mUser, mDate);
    }

    public Date getDate() {
        return mDate;
    }

    public List<Report> getReports() {
        return mReports;
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
        getView().showReports(getReportsForCurrentDate());
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        if (mUser.getIsBlocked()) {
            getAuth().signOut();
            getView().showErrorAndStartLoginScreen();
        }
    }
}
