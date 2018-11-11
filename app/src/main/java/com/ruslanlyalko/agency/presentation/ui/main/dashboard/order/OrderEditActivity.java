package com.ruslanlyalko.agency.presentation.ui.main.dashboard.order;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BaseActivity;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;
import com.ruslanlyalko.agency.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

public class OrderEditActivity extends BaseActivity<OrderEditPresenter> implements OrderEditView {

    private static final String KEY_USER = "user";
    private static final String KEY_REPORT = "report";
    private static final String KEY_DATE = "date";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.text_date) TextView mTextDate;
    @BindView(R.id.text_time) TextView mTextTime;
    @BindView(R.id.spinner_duration) Spinner mSpinnerDuration;
    @BindView(R.id.spinner_children_count) Spinner mSpinnerChildrenCount;
    @BindView(R.id.input_place) TextInputEditText mInputPlace;
    @BindView(R.id.switch_taxi) Switch mSwitchTaxi;
    @BindView(R.id.input_name) TextInputEditText mInputName;
    @BindView(R.id.input_phone) TextInputEditText mInputPhone;
    @BindView(R.id.input_income) TextInputEditText mInputIncome;
    @BindView(R.id.input_expense) TextInputEditText mInputExpense;
    @BindView(R.id.input_description) TextInputEditText mInputDescription;
    @BindView(R.id.slider_children_age) MultiSlider mSliderChildrenAge;
    @BindView(R.id.text_children_age) TextView mTextChildrenAge;
    @BindView(R.id.spinner_users) Spinner mSpinnerUsers;

    public static Intent getLaunchIntent(final Context activity, User user, Date date) {
        Intent intent = new Intent(activity, OrderEditActivity.class);
        intent.putExtra(KEY_USER, user);
        intent.putExtra(KEY_DATE, date);
        return intent;
    }

    public static Intent getLaunchIntent(final Context activity, User user, Order order) {
        Intent intent = new Intent(activity, OrderEditActivity.class);
        intent.putExtra(KEY_USER, user);
        intent.putExtra(KEY_REPORT, order);
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

    @OnClick(R.id.button_save)
    public void onSaveClick() {
        float duration = getFloat(mSpinnerDuration.getSelectedItem().toString().replace("h", ""));
        int childrenFrom = mSliderChildrenAge.getThumb(0).getValue();
        int childrenTo = mSliderChildrenAge.getThumb(1).getValue();
        int childrenCount = getInt(mSpinnerChildrenCount.getSelectedItem().toString());
        String userName = mSpinnerUsers.getSelectedItem().toString();
        int income = getInt(mInputIncome.getText().toString());
        int expense = getInt(mInputExpense.getText().toString());
        boolean taxi = mSwitchTaxi.isChecked();
        getPresenter().onSave(
                duration,
                childrenCount,
                childrenFrom,
                childrenTo,
                income,
                expense,
                mInputPlace.getText().toString(),
                mInputDescription.getText().toString(),
                mInputName.getText().toString(),
                mInputPhone.getText().toString(),
                taxi,
                userName
        );
    }

    private float getFloat(final String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getInt(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonSave.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonSave.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void afterSuccessfullySaving() {
        showMessage(getString(R.string.text_report_saved));
        onBackPressed();
    }

    @Override
    public void afterSuccessfullyRangeSaving(final int count) {
        showMessage(getString(R.string.text_report_saved_workloads, count));
        onBackPressed();
    }

    @Override
    public void showReportData(final Order order) {
        String[] durations = getResources().getStringArray(R.array.durations);
        for (int i = 0; i < durations.length; i++) {
            if (getFloat(durations[i].replace("h", "")) == order.getDuration()) {
                mSpinnerDuration.setSelection(i);
                break;
            }
        }
        String[] childrenCount = getResources().getStringArray(R.array.childrenCount);
        for (int i = 0; i < childrenCount.length; i++) {
            if (getInt(childrenCount[i]) == order.getChildrenCount()) {
                mSpinnerChildrenCount.setSelection(i);
                break;
            }
        }
        mSliderChildrenAge.getThumb(0).setValue(order.getChildrenFrom());
        mSliderChildrenAge.getThumb(1).setValue(order.getChildrenTo());
        mInputPlace.setText(order.getPlace());
        mInputName.setText(order.getName());
        mInputPhone.setText(order.getPhone());
        mInputIncome.setText(String.valueOf(order.getIncome()));
        mInputExpense.setText(String.valueOf(order.getExpense()));
        mInputDescription.setText(order.getDescription());
        mSwitchTaxi.setChecked(order.getTaxi());
    }

    @Override
    public void showDate(Date date) {
        mTextDate.setText(DateUtils.toStringDate(date));
        mTextTime.setText(DateUtils.toStringTime(date));
    }

    @Override
    public void showSpinnerUsersData(final User user1, final MutableLiveData<List<User>> usersData) {
        mSpinnerUsers.setVisibility(user1.getIsAdmin() ? View.VISIBLE : View.GONE);
        usersData.observe(this, users -> {
            if (users == null) return;
            getPresenter().setUsers(users);
            List<String> list = new ArrayList<>();
            for (User user : users) {
                if (user1.getKey().equals(user.getKey()))
                    list.add(0, user.getName());
                else list.add(user.getName());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerUsers.setAdapter(dataAdapter);
            if (!TextUtils.isEmpty(getPresenter().getOrder().getUserName())) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(getPresenter().getOrder().getUserName())) {
                        mSpinnerUsers.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void showWrongDateOnMobileError() {
        showError(getString(R.string.error_check_date));
    }

    @OnClick({R.id.text_date, R.id.text_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_date:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getPresenter().getOrder().getDate());
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().setReportDate(newDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog.show(getFragmentManager(), "to");
                break;
            case R.id.text_time:
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(getPresenter().getOrder().getDate());
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((view, hours, minutes, is24) -> {
                    Date newDate = DateUtils.getDate(calendar1.getTime(), hours, minutes);
                    getPresenter().setReportDate(newDate);
                }, calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE), true);
                timePickerDialog.show(getFragmentManager(), "to");
                break;
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_order_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new OrderEditPresenter(intent.getParcelableExtra(KEY_USER), intent.getParcelableExtra(KEY_REPORT), (Date) intent.getSerializableExtra(KEY_DATE)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(TextUtils.isEmpty(getPresenter().getOrder().getKey())
                ? R.string.title_new_workload : R.string.title_edit_workload);
//        String[] statuses = getResources().getStringArray(R.array.hours);
//        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_status, statuses);
//        mSpinnerDuration.setAdapter(adapter);
        mSliderChildrenAge.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) ->
                mTextChildrenAge.setText(getString(R.string.text_children_age, multiSlider.getThumb(0).getValue(), multiSlider.getThumb(1).getValue())));
        getPresenter().onViewReady();
    }
}
