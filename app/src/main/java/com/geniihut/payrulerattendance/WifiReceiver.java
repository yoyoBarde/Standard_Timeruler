package com.geniihut.payrulerattendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();


        final MediaPlayer mp = MediaPlayer.create(context, R.raw.thankyou);



        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.e("WifiReceiver", "Have Wifi Connection");
            mp.start();

        }
        else {
            Log.e("WifiReceiver", "Don't have Wifi Connection");
            mp.start();
        }
        }
}