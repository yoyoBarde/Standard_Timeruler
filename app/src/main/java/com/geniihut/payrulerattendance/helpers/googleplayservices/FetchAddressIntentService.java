package com.geniihut.payrulerattendance.helpers.googleplayservices;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.geniihut.payrulerattendance.BuildConfig;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by macmini3 on 4/17/15.
 */

/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    public interface FetchAddressResultReceiverCallback {
        public void onFailure(String errorMessage);

        public void onSuccess(Address[] addresses);
    }

    public static class FetchAddressResultReceiver extends ResultReceiver {

        private FetchAddressResultReceiverCallback mCallback;

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public FetchAddressResultReceiver(Handler handler, FetchAddressResultReceiverCallback callback) {
            super(handler);
            mCallback = callback;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (mCallback != null) {
                switch (resultCode) {
                    case RESULT_FAILED:
                        String errorMessage = resultData.getString(KEY_RESULT_ERROR);
                        mCallback.onFailure(errorMessage);
                        break;
                    case RESULT_SUCCESS:
                        Address[] addresses = (Address[]) resultData.getParcelableArray(KEY_RESULT_RECEIVER);
                        mCallback.onSuccess(addresses);
                        break;
                }
            }
        }
    }

    private static final String TAG = "fetch-address-intent-service";
    private static final int RESULT_SUCCESS = 1;
    private static final int RESULT_FAILED = 0;
    private static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    private static final String KEY_RECEIVER = PACKAGE_NAME + ".KEY_RECEIVER";
    private static final String KEY_LATLNG = PACKAGE_NAME + ".KEY_LATLNG";
    private static final String KEY_RESULT_RECEIVER = KEY_RECEIVER + ".KEY_UPLOAD_RESULT_RECEIVER";
    private static final String KEY_RESULT_ERROR = KEY_RECEIVER + ".ERROR";
    private static final String KEY_MAX_NUMBER_ADDRESS_RESULT = "KEY_MAX_NUMBER_ADDRESS_RESULT";
    public static final String ERROR_MESSAGE_NO_LOCATION = "Invalid Location";
    public static final String ERROR_MESSAGE_NO_ADDRESS = "No Address Available";

    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link ResultReceiver} in * MainActivity to process content
     * sent from this service.
     * <p/>
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        mReceiver = intent.getParcelableExtra(KEY_RECEIVER);
        // Check if receiver was properly registered.
        if (mReceiver == null) {
            return;
        }

        // Get the location passed to this service through an extra.
        LatLng latLng = intent.getParcelableExtra(KEY_LATLNG);
        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (latLng == null) {
            deliverFailureResultToReceiver(RESULT_FAILED, ERROR_MESSAGE_NO_LOCATION);
            return;
        }

        //default number of max address result is 1
        int maxNumberAddressResult = intent.getIntExtra(KEY_MAX_NUMBER_ADDRESS_RESULT, 1);

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            deliverFailureResultToReceiver(RESULT_FAILED, ioException.toString());
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            deliverFailureResultToReceiver(RESULT_FAILED, illegalArgumentException.toString());
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            deliverFailureResultToReceiver(RESULT_FAILED, ERROR_MESSAGE_NO_ADDRESS);
        } else {
            deliverSuccessResultToReceiver(RESULT_SUCCESS, addresses.toArray(new Address[addresses.size()]));
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */

    private void deliverFailureResultToReceiver(int resultCode, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RESULT_ERROR, errorMessage);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverSuccessResultToReceiver(int resultCode, Address[] addresses) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(KEY_RESULT_RECEIVER, addresses);
        mReceiver.send(resultCode, bundle);
    }

    public static void startIntentService(Activity activity, FetchAddressResultReceiverCallback callback, LatLng latLng, int maxNumber) {
        FetchAddressResultReceiver receiver = new FetchAddressResultReceiver(new Handler(), callback);
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(activity, FetchAddressIntentService.class);
        intent.putExtra(KEY_RECEIVER, receiver);
        intent.putExtra(KEY_LATLNG, latLng);
        intent.putExtra(KEY_MAX_NUMBER_ADDRESS_RESULT, maxNumber);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        activity.startService(intent);
    }
}
