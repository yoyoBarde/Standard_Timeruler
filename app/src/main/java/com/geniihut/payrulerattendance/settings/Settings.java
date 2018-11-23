package com.geniihut.payrulerattendance.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.R;

/**
 * Created by macmini3 on 8/31/15.
 */
public class Settings {

    private static Settings mInstance;
    private static Context mContext;
    private static SharedPreferences mSharedPreferences;

    private Settings() {
        mInstance = this;
        mContext = AppApplication.getInstance();
//        mSharedPreferences = mContext.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static Settings getInstance() {
        if (mInstance == null) {
            mInstance = new Settings();
        }
        return mInstance;
    }

    public boolean isSystemTimeUsed(){
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_system_time), false);
    }
    public boolean isTakeAPhoto(){
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_picture), true);
    }

    public boolean isAppLockedInScreen(){
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_lock_app), false);
    }

    public boolean isStayAwake(){
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_stay_awake), false);
    }

    public boolean isAutoSyncLogs() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_auto_sync_logs), false);
    }
}

