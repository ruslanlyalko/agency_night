package com.ruslanlyalko.agency.data;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface DataManager {

    //Users
    Task<Void> saveUser(User user);

    MutableLiveData<User> getMyUser();

    MutableLiveData<User> getUser(String key);

    MutableLiveData<List<User>> getAllUsers();

    Task<Void> changePassword(String newPassword);

    void updateToken();

    void logout();

    void clearCache();

    //Orders
    Task<Void> saveOrder(Order newOrder);

    Task<Void> removeOrder(Order order);

    MutableLiveData<List<Order>> getAllMyOrders();

    MutableLiveData<List<Order>> getAllOrders();

    MutableLiveData<List<Order>> getUpcomingOrders(final User user);
}
