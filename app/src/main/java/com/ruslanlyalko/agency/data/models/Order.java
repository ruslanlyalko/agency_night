package com.ruslanlyalko.agency.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Order extends BaseModel implements Parcelable {

    private String phone;
    private String name;
    private Date date;
    private String place;
    private float duration;
    private int childrenCount;
    private int childrenFrom;
    private int childrenTo;
    private boolean taxi;
    private String description;
    private int income;
    private int expense;
    private String userId;
    private String userName;
    private Date updatedAt;

    public Order() {
        date = new Date();
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(final float duration) {
        this.duration = duration;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(final int income) {
        this.income = income;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(final int expense) {
        this.expense = expense;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(final int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public int getChildrenFrom() {
        return childrenFrom;
    }

    public void setChildrenFrom(final int childrenFrom) {
        this.childrenFrom = childrenFrom;
    }

    public int getChildrenTo() {
        return childrenTo;
    }

    public void setChildrenTo(final int childrenTo) {
        this.childrenTo = childrenTo;
    }

    public boolean getTaxi() {
        return taxi;
    }

    public void setTaxi(final boolean taxi) {
        this.taxi = taxi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return getDuration() == order.getDuration() &&
                getChildrenCount() == order.getChildrenCount() &&
                getChildrenFrom() == order.getChildrenFrom() &&
                getChildrenTo() == order.getChildrenTo() &&
                getTaxi() == order.getTaxi() &&
                getIncome() == order.getIncome() &&
                getExpense() == order.getExpense() &&
                Objects.equals(getPhone(), order.getPhone()) &&
                Objects.equals(getName(), order.getName()) &&
                Objects.equals(getDate(), order.getDate()) &&
                Objects.equals(getPlace(), order.getPlace()) &&
                Objects.equals(getDescription(), order.getDescription()) &&
                Objects.equals(getUserId(), order.getUserId()) &&
                Objects.equals(getUserName(), order.getUserName()) &&
                Objects.equals(getUpdatedAt(), order.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPhone(), getName(), getDate(), getPlace(), getDuration(), getChildrenCount(), getChildrenFrom(), getChildrenTo(), getTaxi(), getDescription(), getIncome(), getExpense(), getUserId(), getUserName(), getUpdatedAt());
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.phone);
        dest.writeString(this.name);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.place);
        dest.writeFloat(this.duration);
        dest.writeInt(this.childrenCount);
        dest.writeInt(this.childrenFrom);
        dest.writeInt(this.childrenTo);
        dest.writeByte(this.taxi ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeInt(this.income);
        dest.writeInt(this.expense);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected Order(Parcel in) {
        super(in);
        this.phone = in.readString();
        this.name = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.place = in.readString();
        this.duration = in.readFloat();
        this.childrenCount = in.readInt();
        this.childrenFrom = in.readInt();
        this.childrenTo = in.readInt();
        this.taxi = in.readByte() != 0;
        this.description = in.readString();
        this.income = in.readInt();
        this.expense = in.readInt();
        this.userId = in.readString();
        this.userName = in.readString();
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {return new Order(source);}

        @Override
        public Order[] newArray(int size) {return new Order[size];}
    };
}
