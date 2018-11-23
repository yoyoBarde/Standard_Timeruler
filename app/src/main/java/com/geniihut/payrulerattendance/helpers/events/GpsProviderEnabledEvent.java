package com.geniihut.payrulerattendance.helpers.events;

/**
 * Created by macmini3 on 8/14/15.
 */
public class GpsProviderEnabledEvent {

    private int gpsProviderEnabled;

    public GpsProviderEnabledEvent(int gpsProviderEnabled) {
        this.gpsProviderEnabled = gpsProviderEnabled;
    }

    public int getGpsProviderEnabled() {
        return gpsProviderEnabled;
    }
}
