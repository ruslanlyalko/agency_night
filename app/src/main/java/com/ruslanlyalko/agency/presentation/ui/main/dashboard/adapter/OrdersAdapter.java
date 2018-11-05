package com.ruslanlyalko.agency.presentation.ui.main.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;
import com.ruslanlyalko.agency.presentation.view.OnReportClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final OnReportClickListener mOnReportClickListener;
    private List<Order> mData = new ArrayList<>();
    private boolean mAllowEdit;

    public OrdersAdapter(@Nullable OnReportClickListener onReportClickListener) {
        mOnReportClickListener = onReportClickListener;
    }

    public void setAllowEdit(boolean allow) {
        mAllowEdit = allow;
    }

    public List<Order> getData() {
        return mData;
    }

    public void setData(final List<Order> data) {
        if (data.size() <= 1 && mData.size() <= 1) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(mData, data));
            mData = data;
            diffResult.dispatchUpdatesTo(this);
        } else {
            mData = data;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String getDuration(final float duration) {
        if (duration % 1 == 0) {
            return String.format(Locale.US, "%.0f", duration);
        }
        return String.format(Locale.US, "%.1f", duration);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.layout_root) MaterialCardView mCardRoot;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_income_expense) TextView mTextIncomeExpense;
        @BindView(R.id.text_date) TextView mTextDate;
        @BindView(R.id.image_delete) ImageView mImageDelete;
        @BindView(R.id.text_name_phone) TextView mTextNamePhone;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mContext = view.getContext();
        }

        void bind(final Order order) {
//            mTextTitle.setTextColor(ContextCompat.getColor(mContext, ColorUtils.getTextColorByStatus(mTextDate.getResources(), order.getStatus())));
            mTextTitle.setText(order.getPlace());
            mTextIncomeExpense.setText(String.format(Locale.US, "Children: %d, %d-%d years old", order.getChildrenCount(), order.getChildrenFrom(), order.getChildrenTo()));
            mTextNamePhone.setVisibility(TextUtils.isEmpty(order.getName()) && TextUtils.isEmpty(order.getPhone()) ? GONE : VISIBLE);
            mTextNamePhone.setText(getFormattedText(order.getName(), order.getPhone()));
            mTextDate.setText(
                    String.format(Locale.US, "%s [%sh]", DateUtils.toStringDateTime(mTextDate.getContext(), order.getDate()), getDuration(order.getDuration())));
            mImageDelete.setVisibility(mOnReportClickListener != null
                    //&& (mAllowEdit || order.getDate().after(DateUtils.get1DaysAgo().getTime()))
                    ? VISIBLE : GONE);
        }

        private Spanned getFormattedText(final String name, final String phone) {
            return Html.fromHtml("<b>" + name + "</b> " + phone);
        }

        @OnClick(R.id.layout_root)
        void onItemClick(View v) {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onReportClicked(v, getAdapterPosition());
        }

        @OnClick(R.id.text_name_phone)
        void onPhoneClick(View v) {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onPhoneClicked(v, getAdapterPosition());
        }

        @OnClick(R.id.image_delete)
        void onClicked(View view) {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onReportRemoveClicked(view, getAdapterPosition());
        }
    }
}