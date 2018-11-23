package com.geniihut.payrulerattendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver
{

    private static final String TAG = "AutoStart" ;

    @Override
    public void onReceive(Context context, Intent intent)
    {

      Log.e(TAG,"BootService");
       context.startService(new Intent(context, myService.class));


    }
    



}