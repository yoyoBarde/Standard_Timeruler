package com.geniihut.payrulerattendance.helpers.googleplayservices;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by macmini3 on 4/9/15.
 */
public class RequestLocationSettingsResult {
    public interface RequestLocationSettingsResultCallback {
        public void onSuccess();

        public void onSettingsChangeUnavailable();

        public void onSettingsResolutionRequired(Status status);
    }
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private RequestLocationSettingsResultCallback mCallback;

    public RequestLocationSettingsResult(GoogleApiClient googleApiClient,
                                         LocationRequest... locationRequest) {
        mGoogleApiClient = googleApiClient;
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        for (LocationRequest request : locationRequest) {
            builder.addLocationRequest(request);
        }
        mLocationSettingsRequest = builder.build();
    }

    private ResultCallback<LocationSettingsResult> mPendingResultCallback = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    if (mCallback != null) {
                        mCallback.onSuccess();
                    }
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    mCallback.onSettingsResolutionRequired(status);
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    if (mCallback != null) {
                        mCallback.onSettingsChangeUnavailable();
                    }
                    break;
            }
        }
    };

    public PendingResult<LocationSettingsResult> checkLocationSettings() {
        PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);
        pendingResult.setResultCallback(mPendingResultCallback);
        return pendingResult;
    }

    public void setCallback(RequestLocationSettingsResultCallback callback) {
        mCallback = callback;
    }
}
