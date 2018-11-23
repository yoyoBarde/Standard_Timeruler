package com.geniihut.payrulerattendance.sync.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by macmini3 on 9/1/15.
 */
public class UserSyncService extends Service{
    private static final String TAG = UserSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static UserSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new UserSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "Service destroyed");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
