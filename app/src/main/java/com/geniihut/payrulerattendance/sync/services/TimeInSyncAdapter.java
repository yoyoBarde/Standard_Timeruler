package com.geniihut.payrulerattendance.sync.services;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.VolleyError;
import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.apirequest.APIRequestManager;
import com.geniihut.payrulerattendance.apirequest.RequestResponseListener;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.events.SyncEvent;
import com.geniihut.payrulerattendance.sync.SyncUtils;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by macmini3 on 9/16/15.
 */
class TimeInSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = TimeInSyncAdapter.class.getSimpleName();

    private final ContentResolver mContentResolver;
    private final ArrayList<ContentProviderOperation> batch;
    private final SyncEvent mEvent;

    public TimeInSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        batch = new ArrayList<>();
        mEvent = new SyncEvent();
    }

    public TimeInSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        batch = new ArrayList<>();
        mEvent = new SyncEvent();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e(TAG, "onPerformSync extras = " + extras.toString());
//        String idno = extras.getString(TimeInContract.TimeIn.EXTRAS_IDNO, null);
//        Cursor cursor = TimeInContract.TimeIn.query(getContext(), null, idno, null, TimeInContract.TimeIn.SYNC_SORT_ORDER);
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                com.geniihut.payrulerattendance.model.TimeIn timeIn = TimeInContract.TimeIn.getTimeIn(cursor);
//                Log.e(TAG, "onPerformSync cursor:" + timeIn.get_id());
//            }while (cursor.moveToNext());
//        }
        if (!mEvent.isSyncInProgress()) {
            Log.e(TAG, "onPerformSync isSyncInProgress:false");
            String idno = extras.getString(TimeInContract.TimeIn.EXTRAS_IDNO, null);
            Cursor cursor = TimeInContract.TimeIn.query(getContext(), null, idno, null, TimeInContract.TimeIn.SYNC_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
//                Log.e(TAG, "onPerformSync cursor.moveToFirst");
                mEvent.setMax(cursor.getCount());
                mEvent.setProgress(0);
                mEvent.start();
                postSyncLogs(cursor);
            }
        } else {
            Log.e(TAG, "isSyncInProgress");
            final boolean isCancelSync = extras.getBoolean(SyncUtils.EXTRAS_CANCEL_SYNC, false);
            if (isCancelSync) {
                Log.e(TAG, "onPerformSync isCancelSync:true");
                AppApplication.getInstance().getRequestQueue().cancelAll(AppConstants.REQUEST_TAG_SYNC_LOGS);
                applyBatch();
                mEvent.end();
                EventBus.getDefault().post(mEvent);
            }
        }
    }

    private void applyBatch() {
        try {
            mContentResolver.applyBatch(TimeInContract.CONTENT_AUTHORITY, batch);
            batch.clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void postSyncLogs(Cursor cursor) {
        com.geniihut.payrulerattendance.model.TimeIn timeIn = TimeInContract.TimeIn.getTimeIn(cursor);

        String pin = UserContract.User.getUser(getContext(), timeIn.getIdno()).getPin();
        if (mEvent.isSyncInProgress()) {
            Log.e(TAG, "postSyncLogs isSyncInProgress:true");
            APIRequestManager.postSyncLogs(timeIn.getIdno(), timeIn.getDate(), timeIn.getTime(), timeIn.getInout().toLowerCase(Locale.US), timeIn.getLatitude(), timeIn.getLongitude(), pin, AppUtils.getBitmapAsByteArray(timeIn.getImage()), getPostSyncLogsListener(cursor, timeIn), AppConstants.REQUEST_TAG_SYNC_LOGS);
        } else {
//            Log.e(TAG, "postSyncLogs isSyncInProgress:false");
            applyBatch();
            EventBus.getDefault().post(mEvent);
        }
    }

    private RequestResponseListener getPostSyncLogsListener(final Cursor cursor, final com.geniihut.payrulerattendance.model.TimeIn timeIn) {
        return new RequestResponseListener() {
            @Override
            public void requestStarted() {

            }

            @Override
            public void requestCompleted(JSONObject response) {
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("1")) {
                        mEvent.setProgress(mEvent.getProgress() + 1);
                        EventBus.getDefault().post(mEvent);
                        batch.add(TimeInContract.TimeIn.newDelete(null, timeIn.get_id()));
                        if (cursor.moveToNext()) {
                            postSyncLogs(cursor);
                        } else {
                            applyBatch();
                            cursor.close();
                            mEvent.end();
                            EventBus.getDefault().post(mEvent);
                        }
                    }else{
                        applyBatch();
                        mEvent.end();
                        EventBus.getDefault().post(mEvent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    applyBatch();
                    mEvent.end();
                    EventBus.getDefault().post(mEvent);
                }
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                AppUtils.toastVolleyError(error);
                Log.e(TAG, "getPostSyncLogsListener error \n" + error.toString());
                applyBatch();
                mEvent.end();
                EventBus.getDefault().post(mEvent);
            }
        };
    }
}
