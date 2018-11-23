package com.geniihut.payrulerattendance.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

    private static final String AUTH_TOKEN_KEY = "auth_token";

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public Preferences(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
                Context.MODE_PRIVATE);
    }

    public void saveToPreferences(String preferenceName, String preferenceValue) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.commit();
    }

    public void setAuthToken(String token) {
        saveToPreferences(AUTH_TOKEN_KEY, token);
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(AUTH_TOKEN_KEY, "");
    }
}
