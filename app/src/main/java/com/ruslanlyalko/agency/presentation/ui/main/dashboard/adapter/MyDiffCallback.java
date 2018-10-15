package com.ruslanlyalko.agency.presentation.ui.main.dashboard.adapter;

import android.support.v7.util.DiffUtil;

import com.ruslanlyalko.agency.data.models.Order;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 25.09.2018.
 */
class MyDiffCallback extends DiffUtil.Callback {

    private List<Order> mOldList;
    private List<Order> mNewList;

    public MyDiffCallback(final List<Order> oldList, final List<Order> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(final int i, final int i1) {
        return mOldList.get(i).getKey().equals(mNewList.get(i).getKey());
    }

    @Override
    public boolean areContentsTheSame(final int i, final int i1) {
        return mOldList.get(i).equals(mNewList.get(i));
    }
}
