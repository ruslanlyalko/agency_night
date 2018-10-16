package com.ruslanlyalko.agency.data;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ruslanlyalko.agency.data.Config.DB_ORDERS;
import static com.ruslanlyalko.agency.data.Config.DB_USERS;
import static com.ruslanlyalko.agency.data.Config.FIELD_DATE_TIME;
import static com.ruslanlyalko.agency.data.Config.FIELD_TOKEN;
import static com.ruslanlyalko.agency.data.Config.FIELD_USER_ID;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class DataManagerImpl implements DataManager {

    private static final String TAG = "DataManager";
    private static DataManagerImpl mInstance;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private MutableLiveData<User> mCurrentUserLiveData;
    private MutableLiveData<List<Order>> mAllMyReportsListMutableLiveData;
    private MutableLiveData<List<User>> mAllUsersListLiveData;

    private DataManagerImpl() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static DataManager newInstance() {
        if (mInstance == null)
            mInstance = new DataManagerImpl();
        return mInstance;
    }

    @Override
    public Task<Void> saveUser(final User user) {
        if (user.getKey() == null) {
            throw new RuntimeException("user can't be empty");
        }
        return mFirestore.collection(DB_USERS)
                .document(user.getKey())
                .set(user);
    }

    @Override
    public MutableLiveData<User> getMyUser() {
        if (mCurrentUserLiveData != null) return mCurrentUserLiveData;
        String key = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (TextUtils.isEmpty(key)) {
            Log.w(TAG, "getMyUser: user is not logged in");
            return mCurrentUserLiveData;
        }
        mCurrentUserLiveData = new MutableLiveData<>();
        mFirestore.collection(DB_USERS).document(key).addSnapshotListener((documentSnapshot, e) -> {
            Log.d(TAG, "getMyUser:onDataChange, Key:" + key);
            if (e == null && mCurrentUserLiveData != null && documentSnapshot != null)
                mCurrentUserLiveData.postValue(documentSnapshot.toObject(User.class));
        });
        return mCurrentUserLiveData;
    }

    @Override
    public MutableLiveData<User> getUser(final String key) {
        final MutableLiveData<User> userLiveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(key)) {
            Log.w(TAG, "getUser has wrong argument");
            return userLiveData;
        }
        mFirestore.collection(DB_USERS).document(key).addSnapshotListener((documentSnapshot, e) -> {
            Log.d(TAG, "getUser:onDataChange, Key:" + key);
            if (e == null && mCurrentUserLiveData != null && documentSnapshot != null)
                userLiveData.postValue(documentSnapshot.toObject(User.class));
        });
        return userLiveData;
    }

    @Override
    public MutableLiveData<List<User>> getAllUsers() {
        if (mAllUsersListLiveData != null) return mAllUsersListLiveData;
        mAllUsersListLiveData = new MutableLiveData<>();
        mFirestore.collection(DB_USERS).addSnapshotListener((snapshots, e) -> {
            Log.d(TAG, "getAllUsers:onDataChange");
            if (e == null && mCurrentUserLiveData != null && snapshots != null) {
                List<User> list = new ArrayList<>();
                for (DocumentSnapshot dc : snapshots.getDocuments()) {
                    list.add(dc.toObject(User.class));
                }
                mAllUsersListLiveData.postValue(list);
            }
        });
        return mAllUsersListLiveData;
    }

    @Override
    public Task<Void> changePassword(final String newPassword) {
        if (mAuth.getCurrentUser() == null) return null;
        return mAuth.getCurrentUser().updatePassword(newPassword);
    }

    @Override
    public void updateToken() {
        if (mAuth.getCurrentUser() == null) return;
        mFirestore.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_TOKEN, FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void logout() {
        if (mAuth.getCurrentUser() == null) return;
        mFirestore.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_TOKEN, null);
        mCurrentUserLiveData = null;
        mAllMyReportsListMutableLiveData = null;
        mAuth.signOut();
    }

    @Override
    public void clearCache() {
        mCurrentUserLiveData = null;
        mAllMyReportsListMutableLiveData = null;
    }

    @Override
    public Task<Void> saveOrder(final Order newOrder) {
        return mFirestore.collection(DB_ORDERS)
                .document(newOrder.getKey())
                .set(newOrder);
    }

    @Override
    public Task<Void> removeOrder(final Order order) {
        return mFirestore.collection(DB_ORDERS)
                .document(order.getKey())
                .delete();
    }

    @Override
    public MutableLiveData<List<Order>> getAllMyOrders() {
        if (mAllMyReportsListMutableLiveData != null) return mAllMyReportsListMutableLiveData;
        String userId = mAuth.getUid();
        mAllMyReportsListMutableLiveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(userId)) {
            Log.w(TAG, "getAllMyOrders user is not logged in");
            return mAllMyReportsListMutableLiveData;
        }
        mFirestore.collection(DB_ORDERS)
                .whereEqualTo(FIELD_USER_ID, userId)
                .orderBy(FIELD_DATE_TIME)
                .addSnapshotListener((snapshots, e) -> {
                    Log.d(TAG, "getAllMyOrders:onDataChange");
                    if (e == null && mCurrentUserLiveData != null && snapshots != null) {
                        List<Order> list = new ArrayList<>();
                        for (DocumentSnapshot dc : snapshots.getDocuments()) {
                            list.add(dc.toObject(Order.class));
                        }
                        mAllMyReportsListMutableLiveData.postValue(list);
                    }
                });
        return mAllMyReportsListMutableLiveData;
    }

    @Override
    public MutableLiveData<List<Order>> getUpcomingOrders(final User user) {
        final MutableLiveData<List<Order>> result = new MutableLiveData<>();
        mFirestore.collection(DB_ORDERS)
                .whereEqualTo(FIELD_USER_ID, user.getKey())
                .whereGreaterThan(FIELD_DATE_TIME, DateUtils.getStart(new Date()).getTime())
                .orderBy(FIELD_DATE_TIME)
                .addSnapshotListener((snapshots, e) -> {
                    Log.d(TAG, "getUpcomingOrders:onDataChange");
                    if (e == null && mCurrentUserLiveData != null && snapshots != null) {
                        List<Order> list = new ArrayList<>();
                        for (DocumentSnapshot dc : snapshots.getDocuments()) {
                            list.add(dc.toObject(Order.class));
                        }
                        result.postValue(list);
                    }
                });
        return result;
    }
}
