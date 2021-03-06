package com.ruslanlyalko.agency.presentation.ui.main.users.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruslanlyalko.agency.R;
import com.ruslanlyalko.agency.data.models.User;
import com.ruslanlyalko.agency.presentation.view.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements Filterable {

    private final OnItemClickListener mOnItemClickListener;
    private List<User> mData = new ArrayList<>();
    private List<User> mDataFiltered = new ArrayList<>();

    public UsersAdapter(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<User> getData() {
        return mDataFiltered;
    }

    public void setData(final List<User> data) {
        mData = data;
        mDataFiltered = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mDataFiltered.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDataFiltered = mData;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User user : mData) {
                        if (user.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredList.add(user);
                        }
                    }
                    mDataFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_logo) ImageView mImageLogo;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_subtitle) TextView mTextSubtitle;
        @BindView(R.id.text_letters) TextView mTextLetters;
        @BindView(R.id.image_edit) ImageView mImageEdit;
        @BindView(R.id.image_admin) ImageView mImageAdmin;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(final User user) {
            mTextTitle.setText(user.getName());
            mTextSubtitle.setText(user.getPhone());
            if (!user.getIsBlocked()) {
                mImageLogo.setImageResource(R.drawable.bg_oval_green);
            } else {
                mImageLogo.setImageResource(R.drawable.bg_oval_yellow);
            }
            mTextLetters.setText(getAbbreviation(user.getName()));
            mImageAdmin.setVisibility(user.getIsAdmin() ? View.VISIBLE : View.GONE);
        }

        private String getAbbreviation(final String name) {
            if (TextUtils.isEmpty(name)) return "";
            String[] list = name.split(" ");
            String result = list[0].substring(0, 1);
            if (list.length > 1)
                result = result + list[1].substring(0, 1);
            return result.toUpperCase();
        }

        @OnClick(R.id.layout_root)
        void onClicked(View view) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClicked(view, getAdapterPosition());
        }

        @OnLongClick(R.id.layout_root)
        boolean onLongClick(View v) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemLongClicked(v, getAdapterPosition());
            return true;
        }
    }
}