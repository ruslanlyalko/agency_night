package com.ruslanlyalko.agency.presentation.ui.main.dashboard.report;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ReportEditView extends BaseView<ReportEditPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();

    void afterSuccessfullyRangeSaving(int count);

    void showReportData(Order order);

    void showHoliday(String holiday);

    void errorCantBeZero();

    void errorCantBeMoreThan16();

    void errorCantHasTwoEqualsProjects();

    void showDateState(boolean dateStateOneDay);

    void showDateFrom(Date date);

    void showDateTo(Date date);

    void addProject(String title);

    void changeProject(String title, int position);

    void showWrongDateOnMobileError();
}
