package com.geniihut.payrulerattendance;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.LocationHelper;
import com.geniihut.payrulerattendance.helpers.events.GpsProviderEnabledEvent;
import com.geniihut.payrulerattendance.helpers.events.GpsStatusChangeEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public abstract class BaseGPSActivity extends AppCompatActivity {
    @InjectView(R.id.app_bar)
    protected Toolbar mToolbar;
    //    @InjectView(R.id.tvGpsTime)
//    protected TextView mTvGpsTime;
    @InjectView(R.id.listview)
    protected ListView mListView;

    protected View activityView;
    protected AlertDialog mGpsDisabledDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResources());
        activityView = getLayoutInflater().inflate(getLayoutResources(), null);
        setContentView(activityView);
        ButterKnife.inject(this);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setIcon(R.drawable.ic_logo);
            getSupportActionBar().setTitle(getActivityTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        AppUtils.logE(this, "onStop");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        AppUtils.logE(this, "onDestroy");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    public void showGpsDisabledDialog() {
        if (mGpsDisabledDialog == null) {
            mGpsDisabledDialog = AppUtils.showGpsDisabledDialog(this);
        } else if (!mGpsDisabledDialog.isShowing()) {
            mGpsDisabledDialog.show();
        }
    }

    public void onEvent(GpsProviderEnabledEvent event) {
        switch (event.getGpsProviderEnabled()) {
            case LocationHelper.GPS_PROVIDER_ENABLED: {

            }
            break;

            case LocationHelper.GPS_PROVIDER_DISABLED: {
//                showGpsDisabledDialog();
            }
            break;
        }
    }

    public void onEvent(GpsStatusChangeEvent event) {
        switch (event.getGpsSignal()) {
            case LocationHelper.GPS_SIGNAL_AVAILABLE: {

            }
            break;

            case LocationHelper.GPS_SIGNAL_UNAVAILABLE: {
//                mTvGpsTime.setText(getString(R.string.gps_time_default));
            }
            break;
        }
    }

    public int getLayoutResources() {
        return R.layout.activity_base_gps;
    }

    public abstract String getRequestTag();

    public abstract String getActivityTitle();
}
