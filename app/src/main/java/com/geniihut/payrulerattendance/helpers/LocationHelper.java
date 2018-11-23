package com.geniihut.payrulerattendance.helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.geniihut.payrulerattendance.helpers.events.ConnectivityEvent;
import com.geniihut.payrulerattendance.helpers.events.GpsProviderEnabledEvent;
import com.geniihut.payrulerattendance.helpers.events.GpsStatusChangeEvent;
import com.geniihut.payrulerattendance.helpers.googleplayservices.GoogleApiLocation;
import com.geniihut.payrulerattendance.helpers.googleplayservices.RequestLocationSettingsResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStates;

import de.greenrobot.event.EventBus;

/**
 * Created by macmini3 on 8/13/15.
 */
public class LocationHelper {
    public static final String TAG = LocationHelper.class.getSimpleName();

    private Activity mActivity;

    private boolean isGsp = false;

    public LocationHelper(Activity activity, LocationManager locationManager) {
        mActivity = activity;
        this.locationManager = locationManager;
        initializeGPS();
        initializeNetwork();
    }

    public void startRequest(ConnectivityEvent event) {
        if (event.hasNoConnectivity()) {
//            Log.e(TAG, "gps");
            stopNetwork();
            startGPS();
            isGsp = true;
        } else {
//            Log.e(TAG, "wifi");
            stopGPS();
            startNetwork();
            isGsp = false;
        }
    }

    public void stopRequest() {
        stopGPS();
        stopNetwork();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //NETWORK
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_CODE_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    break;
                default:
                    break;
            }
        }
    }

    /**********************************
     * GPS
     **********************************/

    public static final int GPS_SIGNAL_UNAVAILABLE = 0;
    public static final int GPS_SIGNAL_AVAILABLE = 1;
    public static final int GPS_PROVIDER_DISABLED = 0;
    public static final int GPS_PROVIDER_ENABLED = 1;

    private LocationManager locationManager;
    private long lastRetrieveElapseTime;
    private int gpsSignal;

    private void initializeGPS() {

    }

    private LocationListener mLocationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpsSignal = GPS_SIGNAL_AVAILABLE;
            lastRetrieveElapseTime = SystemClock.elapsedRealtime();
//            EventBus.getDefault().post(new LocationChangeEvent(location, AppUtils.millisecondsDateUTCToLocal(location.getTime())));
//            Log.e(TAG, "GPS " + location.getLatitude() + " " + location.getLongitude());
            EventBus.getDefault().post(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            EventBus.getDefault().post(new GpsProviderEnabledEvent(LocationHelper.GPS_PROVIDER_ENABLED));
        }

        @Override
        public void onProviderDisabled(String provider) {
            EventBus.getDefault().post(new GpsProviderEnabledEvent(LocationHelper.GPS_PROVIDER_DISABLED));
        }
    };

    private GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            if ((SystemClock.elapsedRealtime() - lastRetrieveElapseTime) > 1000) {
                gpsSignal = GPS_SIGNAL_UNAVAILABLE;
            }
            GpsStatusChangeEvent gpsStatusChangeEvent = new GpsStatusChangeEvent();
            gpsStatusChangeEvent.setGpsSignal(gpsSignal);
            EventBus.getDefault().post(gpsStatusChangeEvent);
        }
    };

    public void startGPS() {
        this.locationManager.addGpsStatusListener(mGpsStatusListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListenerGPS);
    }

    public void stopGPS() {
        locationManager.removeGpsStatusListener(mGpsStatusListener);
        locationManager.removeUpdates(mLocationListenerGPS);
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public long getLastRetrieveElapseTime() {
        return lastRetrieveElapseTime;
    }

    public int getGpsSignal() {
        return gpsSignal;
    }

    /**********************************
     * NETWORK
     **********************************/

    public static int REQUEST_CODE_CHECK_SETTINGS = AppConstants.REQUEST_CODE_CHECK_SETTINGS;

    private GoogleApiLocation mGoogleApiLocation;
    private RequestLocationSettingsResult mRequestLocation;
    private boolean mIsFirstConnect;
    private boolean isResReqPrompted;

    private void initializeNetwork() {
        mGoogleApiLocation = new GoogleApiLocation(mActivity, mConnectionCallbacks, mOnConnectionFailedListener);
        mRequestLocation = new RequestLocationSettingsResult(mGoogleApiLocation.getGoogleApiClient(), mGoogleApiLocation.getLocationRequest());
        mRequestLocation.setCallback(mLocationSettingsCallback);
        mIsFirstConnect = true;
        isResReqPrompted = false;
    }

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
//            Log.e(TAG,"onConnected");
            if (mIsFirstConnect) {
                mRequestLocation.checkLocationSettings();
                mIsFirstConnect = false;
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
//            Log.e(TAG,"onConnectionSuspended");
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
//            Log.e(TAG,"onConnectionFailed");
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(mActivity, 1);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiLocation.onStart();
                }
            }
        }
    };

    private RequestLocationSettingsResult.RequestLocationSettingsResultCallback mLocationSettingsCallback = new RequestLocationSettingsResult.RequestLocationSettingsResultCallback() {
        @Override
        public void onSuccess() {
            mGoogleApiLocation.requestLocationUpdates(mLocationListenerNetwork);
        }

        @Override
        public void onSettingsChangeUnavailable() {
            mGoogleApiLocation.requestLocationUpdates(mLocationListenerNetwork);
        }

        @Override
        public void onSettingsResolutionRequired(Status status) {
            if (!isResReqPrompted) {
                try {
                    status.startResolutionForResult(
                            mActivity,
                            REQUEST_CODE_CHECK_SETTINGS);
                    isResReqPrompted = true;
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiLocation.requestLocationUpdates(mLocationListenerNetwork);
                }
            } else {
                mGoogleApiLocation.requestLocationUpdates(mLocationListenerNetwork);
            }
        }
    };

    private com.google.android.gms.location.LocationListener mLocationListenerNetwork = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            Log.e(TAG, "NETWORK" + location.getLatitude() + " " + location.getLongitude());

            EventBus.getDefault().post(location);


        }
    };

    public void startNetwork() {
        mGoogleApiLocation.onStart();
    }

    public void stopNetwork() {
        if (mGoogleApiLocation.getGoogleApiClient().isConnected()) {
            mGoogleApiLocation.removeLocationUpdates(mLocationListenerNetwork);
            mGoogleApiLocation.onStop();
            mIsFirstConnect = true;
        }
    }

    public boolean isGsp() {
        return isGsp;
    }

    public void setIsGsp(boolean isGsp) {
        this.isGsp = isGsp;
    }


}
