package com.ruslanlyalko.agency.presentation.ui.main.my_vacations;

import android.util.SparseIntArray;

import com.ruslanlyalko.agency.data.models.Report;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyVacationsPresenter extends BasePresenter<MyVacationsView> {

    private User mUser;

    MyVacationsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showReports(getDataManager().getAllMyReports());
    }

    public void setReports(final List<Report> reports) {
        List<Report> listVacationReports = new ArrayList<>();
        SparseIntArray years = new SparseIntArray();
        for (Report report : reports) {
            if (report.getStatus().startsWith("Day")
                    || report.getStatus().startsWith("Vacation")
                    || report.getStatus().startsWith("Sick")) {
                listVacationReports.add(report);
                int yearInd = DateUtils.getYearIndex(report.getDate(), mUser.getFirstWorkingDate());
                int value = years.get(yearInd);
                value = value + 1;
                years.append(yearInd, value);
            }
        }
        getView().setReportsToAdapter(listVacationReports);
        getView().showReportsByYear(mUser.getFirstWorkingDate(), years);
    }
}
