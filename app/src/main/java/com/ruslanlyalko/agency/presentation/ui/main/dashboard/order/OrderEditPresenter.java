package com.ruslanlyalko.agency.presentation.ui.main.dashboard.order;

import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.base.BasePresenter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class OrderEditPresenter extends BasePresenter<OrderEditView> {

    private final User mUser;
    private final Order mOrder;
    private List<User> mUsers;

    OrderEditPresenter(User user, Order order, Date date) {
        if (user == null)
            throw new RuntimeException("User can't be empty");
        mUser = user;
        if (order == null) {
            order = new Order();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, 16);
            c.set(Calendar.MINUTE, 0);
            order.setDate(c.getTime());
            order.setChildrenFrom(5);
            order.setChildrenTo(8);
            order.setChildrenCount(9);
        }
        mOrder = order;
    }

    public void onViewReady() {
        getView().showDate(mOrder.getDate());
        getView().showReportData(mOrder);
        getView().showSpinnerUsersData(mUser, getDataManager().getAllUsers());
    }

    public void onSave(final float duration, final int childrenCount,
                       final int childrenFrom, final int childrenTo,
                       final int income, final int expense,
                       final String place, final String description,
                       final String name, final String phone, final boolean taxi,
                       final String userName) {
        mOrder.setDuration(duration);
        mOrder.setChildrenCount(childrenCount);
        mOrder.setChildrenFrom(childrenFrom);
        mOrder.setChildrenTo(childrenTo);
        mOrder.setIncome(income);
        mOrder.setExpense(expense);
        mOrder.setPlace(place);
        mOrder.setDescription(description);
        mOrder.setName(name);
        mOrder.setPhone(phone);
        mOrder.setUserName(userName);
        mOrder.setUserId(getUserIdByName(userName));
        mOrder.setUpdatedAt(new Date());
        mOrder.setTaxi(taxi);
        getView().showProgress();
        getDataManager().saveOrder(mOrder)
                .addOnSuccessListener(aVoid -> {
                    if (getView() == null) return;
                    getView().afterSuccessfullySaving();
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().showWrongDateOnMobileError();
                    getView().hideProgress();
                });
    }

    private String getUserIdByName(final String userName) {
        for (User user : mUsers) {
            if (user.getName().equals(userName))
                return user.getKey();
        }
        throw new RuntimeException("No such user!");
    }

    public User getUser() {
        return mUser;
    }

    public Order getOrder() {
        return mOrder;
    }

    public void setReportDate(final Date date) {
        mOrder.setDate(date);
        getView().showDate(date);
    }

    public void setUsers(final List<User> users) {
        mUsers = users;
    }
}
