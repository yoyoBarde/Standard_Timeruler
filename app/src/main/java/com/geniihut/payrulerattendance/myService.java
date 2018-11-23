package com.geniihut.payrulerattendance;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class myService extends Service {
    private static final String TAG ="myService" ;

    public IBinder onBind(Intent intent){


        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        final MediaPlayer mp = MediaPlayer.create(this, R.raw.thankyou);
        mp.start();

        Log.e(TAG,"Service Running");
        Log.e(TAG,"Service Running");
        Log.e(TAG,"Service Running");
        Log.e(TAG,"Service Running");
        Log.e(TAG,"Service Running");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Intent mServiceIntent;
//        mServiceIntent = new Intent(this, mSensorSe);
        Intent broadcastIntent = new Intent(this, AutoStart.class);
        sendBroadcast(broadcastIntent);


    }
}
