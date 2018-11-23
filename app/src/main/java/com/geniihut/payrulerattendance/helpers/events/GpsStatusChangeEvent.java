package com.geniihut.payrulerattendance.helpers.events;

import android.location.LocationManager;

/**
 * Created by macmini3 on 8/13/15.
 */
public class GpsStatusChangeEvent {

    private int event;
    private LocationManager manager;
    private int gpsSignal;

    public GpsStatusChangeEvent() {
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public LocationManager getManager() {
        return manager;
    }

    public void setManager(LocationManager manager) {
        this.manager = manager;
    }

    public int getGpsSignal() {
        return gpsSignal;
    }

    public void setGpsSignal(int gpsSignal) {
        this.gpsSignal = gpsSignal;
    }
}
