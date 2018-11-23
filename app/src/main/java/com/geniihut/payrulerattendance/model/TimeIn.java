package com.geniihut.payrulerattendance.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;

import java.io.Serializable;

/**
 * Created by macmini4 on 8/12/15.
 */
public class TimeIn implements TimeInContract.Columns, Parcelable{

    public static final String ACTION_IN = "IN";
    public static final String ACTION_OUT = "OUT";

    private long _id;
    private String idno;
    private String date;
    private String time;
    private String inout;
    private String latitude;
    private String longitude;
    private String dateTime;
    private Bitmap image;
    
    private TimeIn(Parcel in) {
        _id = in.readLong();
        idno = in.readString();
        date = in.readString();
        time = in.readString();
        inout = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        dateTime = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<TimeIn> CREATOR
            = new Parcelable.Creator<TimeIn>() {
        public TimeIn createFromParcel(Parcel in) {
            return new TimeIn(in);
        }

        public TimeIn[] newArray(int size) {
            return new TimeIn[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(idno);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(inout);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(dateTime);
        dest.writeParcelable(image, flags);
    }

    public TimeIn() {
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInout() {
        return inout;
    }

    public void setInout(String inout) {
        this.inout = inout;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
