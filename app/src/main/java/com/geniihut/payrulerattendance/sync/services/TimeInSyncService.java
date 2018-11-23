package com.geniihut.payrulerattendance.sync.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by macmini3 on 9/16/15.
 */
public class TimeInSyncService extends Service {
    private static final String TAG = TimeInSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static TimeInSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new TimeInSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
