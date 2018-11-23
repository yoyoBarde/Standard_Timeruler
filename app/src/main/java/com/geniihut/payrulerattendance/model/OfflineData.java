package com.geniihut.payrulerattendance.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class OfflineData implements Parcelable {
    private String LoginDate;
    private String LoginTime;
    private String TimeInOrOut;
    private String userID;
    private String userPassword;
    private Bitmap image;

    public OfflineData(String loginDate,String loginTime, String timeInOrOut, String userID, String userPassword, Bitmap image) {
        LoginTime = loginTime;
        LoginDate = loginDate;
        TimeInOrOut = timeInOrOut;
        this.userID = userID;
        this.userPassword = userPassword;
        this.image = image;
    }

    protected OfflineData(Parcel in) {
        LoginDate = in.readString();
        LoginTime = in.readString();
        TimeInOrOut = in.readString();
        userID = in.readString();
        userPassword = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<OfflineData> CREATOR = new Creator<OfflineData>() {
        @Override
        public OfflineData createFromParcel(Parcel in) {
            return new OfflineData(in);
        }

        @Override
        public OfflineData[] newArray(int size) {
            return new OfflineData[size];
        }
    };

    public String getLoginDate() {
        return LoginDate;
    }

    public void setLoginDate(String loginDate) {
        LoginDate = loginDate;
    }

    public String getLoginTime() {
        return LoginTime;
    }

    public void setLoginTime(String loginTime) {
        LoginTime = loginTime;
    }

    public String getTimeInOrOut() {
        return TimeInOrOut;
    }

    public void setTimeInOrOut(String timeInOrOut) {
        TimeInOrOut = timeInOrOut;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(LoginDate);
        parcel.writeString(LoginTime);
        parcel.writeString(TimeInOrOut);
        parcel.writeString(userID);
        parcel.writeString(userPassword);
        parcel.writeParcelable(image, i);
    }
}
