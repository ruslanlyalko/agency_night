package com.ruslanlyalko.agency.presentation.ui.main.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.Order;
import com.ruslanlyalko.agency.presentation.utils.ColorUtils;
import com.ruslanlyalko.agency.presentation.utils.DateUtils;
import com.ruslanlyalko.agency.presentation.view.OnReportClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {

    private final OnReportClickListener mOnReportClickListener;
    private List<Order> mData = new ArrayList<>();
    private boolean mAllowEdit;

    public ReportsAdapter(@Nullable OnReportClickListener onReportClickListener) {
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
                .inflate(R.layout.item_report, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.layout_root) MaterialCardView mCardRoot;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_name) TextView mTextName;
        @BindView(R.id.text_date) TextView mTextDate;
        @BindView(R.id.image_delete) ImageView mImageDelete;
        @BindView(R.id.text_project_1) TextView mTextProject1;
        @BindView(R.id.text_project_2) TextView mTextProject2;
        @BindView(R.id.text_project_3) TextView mTextProject3;
        @BindView(R.id.text_project_4) TextView mTextProject4;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mContext = view.getContext();
        }

        void bind(final Order order) {
            mTextTitle.setTextColor(ContextCompat.getColor(mContext, ColorUtils.getTextColorByStatus(mTextDate.getResources(), order.getStatus())));
            mTextTitle.setText(order.getStatus());
            mTextName.setText(String.format("%s / %s", order.getUserName(), order.getUserDepartment()));
            mTextProject1.setVisibility(TextUtils.isEmpty(order.getP1()) ? GONE : VISIBLE);
            mTextProject2.setVisibility(TextUtils.isEmpty(order.getP2()) ? GONE : VISIBLE);
            mTextProject3.setVisibility(TextUtils.isEmpty(order.getP3()) ? GONE : VISIBLE);
            mTextProject4.setVisibility(TextUtils.isEmpty(order.getP4()) ? GONE : VISIBLE);
            mTextProject1.setText(getFormattedText(order.getP1(), order.getT1()));
            mTextProject2.setText(getFormattedText(order.getP2(), order.getT2()));
            mTextProject3.setText(getFormattedText(order.getP3(), order.getT3()));
            mTextProject4.setText(getFormattedText(order.getP4(), order.getT4()));
            mTextDate.setText(DateUtils.toStringDate(order.getDate()));
            mImageDelete.setVisibility(mOnReportClickListener != null
                    && (mAllowEdit || order.getDate().after(DateUtils.get1DaysAgo().getTime()))
                    ? VISIBLE : GONE);
        }

        private Spanned getFormattedText(final String name, final int time) {
            if (TextUtils.isEmpty(name)) return SpannableString.valueOf("");
            return Html.fromHtml("<b>" + name + "</b> " + time + "h");
        }

        @OnClick(R.id.layout_root)
        void onItemClick(View v) {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onReportClicked(v, getAdapterPosition());
        }

        @OnClick(R.id.image_delete)
        void onClicked(View view) {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onReportRemoveClicked(view, getAdapterPosition());
        }
    }
}