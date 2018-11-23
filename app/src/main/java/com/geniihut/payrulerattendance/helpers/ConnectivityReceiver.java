package com.geniihut.payrulerattendance.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.helpers.events.ConnectivityEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by macmini3 on 8/31/15.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        ConnectivityEvent event = new ConnectivityEvent();
        event.setExtraInfo(extras.getString(ConnectivityManager.EXTRA_EXTRA_INFO, null));
        event.setIsFailOver(extras.getBoolean(ConnectivityManager.EXTRA_IS_FAILOVER, false));
        event.setHasNoConnectivity(extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
        event.setOtherNetworkInfo((NetworkInfo) extras.getParcelable(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO));
        event.setReason(extras.getString(ConnectivityManager.EXTRA_REASON, null));
        EventBus.getDefault().post(event);
    }
}
