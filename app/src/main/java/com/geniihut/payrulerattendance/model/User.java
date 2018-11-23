package com.geniihut.payrulerattendance.model;

import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by macmini4 on 3/26/15.
 */
public class User implements Serializable, UserContract.Columns {

    private long _id;
    private String idno;
    private String firstName;
    private String lastName;
    private String middleName;
    private String pin;
    private String systemUser;

    public User() {
    }

    public String getFullName() {
        String fullName ="";
        if(getFirstName()!=null) fullName = getFirstName();
        if(getMiddleName()!=null) fullName = fullName + " " + getMiddleName();
        if(getLastName()!=null) fullName = fullName + " " + getLastName();
        return fullName;

    }

    public static User create(JSONObject json) throws JSONException {
        User user = new User();
        if (json.has(IDNO)) {
            user.setIdno(json.getString(IDNO));
        }
        if (json.has(LNAME)) {
            user.setLastName(json.getString(LNAME));
        }
        if (json.has(FNAME)) {
            user.setFirstName(json.getString(FNAME));
        }
        if (json.has(MNAME)) {
            user.setMiddleName(json.getString(MNAME));
        }
        if (json.has(PIN)) {
            user.setPin(json.getString(PIN));
        }
        if (json.has(SYSTEM_USER)) {
            user.setSystemUser(json.getString(SYSTEM_USER));
        }

        return user;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(String systemUser) {
        this.systemUser = systemUser;
    }



}
