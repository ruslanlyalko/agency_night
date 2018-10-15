package com.ruslanlyalko.agency.data;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.data.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ruslanlyalko.agency.data.Config.DB_ORDERS;
import static com.ruslanlyalko.agency.data.Config.DB_USERS;
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
    private FirebaseDatabase mDatabase;
    private FirebaseFirestore mFirestore;
    private MutableLiveData<User> mCurrentUserLiveData;
    private MutableLiveData<List<Order>> mAllMyReportsListMutableLiveData;
    private MutableLiveData<List<User>> mAllUsersListLiveData;

    private DataManagerImpl() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
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
        return mDatabase.getReference(DB_USERS)
                .child(user.getKey())
                .setValue(user);
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
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_TOKEN)
                .setValue(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void logout() {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_TOKEN)
                .removeValue();
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
    public Task<Void> saveReport(final Order newOrder) {
        return mDatabase.getReference(DB_ORDERS)
                .child(newOrder.getKey())
                .setValue(newOrder);
    }

    @Override
    public Task<Void> removeReport(final Order order) {
        return mDatabase.getReference(DB_ORDERS)
                .child(order.getKey())
                .removeValue();
    }

    @Override
    public MutableLiveData<List<Order>> getAllMyReports() {
        if (mAllMyReportsListMutableLiveData != null) return mAllMyReportsListMutableLiveData;
        String userId = mAuth.getUid();
        mAllMyReportsListMutableLiveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(userId)) {
            Log.w(TAG, "getAllMyReports user is not logged in");
            return mAllMyReportsListMutableLiveData;
        }
        mDatabase.getReference(DB_ORDERS)
                .orderByChild(FIELD_USER_ID)
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllMyReports:onDataChange, userId:" + userId);
                        List<Order> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Order order = snap.getValue(Order.class);
                            if (order != null)
                                list.add(order);
                        }
                        if (mAllMyReportsListMutableLiveData != null)
                            mAllMyReportsListMutableLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mAllMyReportsListMutableLiveData;
    }

    @Override
    public MutableLiveData<List<Order>> getVacationReports(final User user) {
        final MutableLiveData<List<Order>> result = new MutableLiveData<>();
        mDatabase.getReference(DB_ORDERS)
                .orderByChild(FIELD_USER_ID)
                .equalTo(user.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getVacationReports:onDataChange");
                        List<Order> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Order order = snapReport.getValue(Order.class);
                            if (order == null) continue;
                            if (order.getStatus().startsWith("Day")
                                    || order.getStatus().startsWith("Vacation")
                                    || order.getStatus().startsWith("Sick"))
                                list.add(order);
                        }
                        Collections.sort(list, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                        result.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return result;
    }

    private int getTotalHoursSpent(final Order order) {
        return order.getT1() + order.getT2() + order.getT3() + order.getT4();
    }
}
