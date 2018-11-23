package com.geniihut.payrulerattendance.helpers.events;

import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by macmini3 on 8/31/15.
 */
public class ConnectivityEvent {

    private String extraInfo;
    private boolean isFailOver;
    private NetworkInfo networkInfo;
    private boolean hasNoConnectivity;
    private NetworkInfo otherNetworkInfo;
    private String reason;

    public ConnectivityEvent() {
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isFailOver() {
        return isFailOver;
    }

    public void setIsFailOver(boolean isFailOver) {
        this.isFailOver = isFailOver;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public boolean hasNoConnectivity() {
        return hasNoConnectivity;
    }

    public void setHasNoConnectivity(boolean hasNoConnectivity) {
        this.hasNoConnectivity = hasNoConnectivity;
    }

    public NetworkInfo getOtherNetworkInfo() {
        return otherNetworkInfo;
    }

    public void setOtherNetworkInfo(NetworkInfo otherNetworkInfo) {
        this.otherNetworkInfo = otherNetworkInfo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
