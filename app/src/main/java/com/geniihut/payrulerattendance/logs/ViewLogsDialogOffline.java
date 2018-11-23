package com.geniihut.payrulerattendance.logs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.model.OfflineData;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewLogsDialogOffline extends AlertDialog {

    @InjectView(R.id.offlineUsername)
    protected TextView offlineUsername;
    @InjectView(R.id.offlineTime)
    protected TextView offlineTime;
    @InjectView(R.id.offlineLat)
    protected  TextView offlineLat;
    @InjectView(R.id.offlineLong)
    protected  TextView offlineLong;
    @InjectView (R.id.offlineImage)
    protected ImageView offlineImage;

    public ViewLogsDialogOffline(final Context context,OfflineData myOfflineData) {
        super(context);
        View alertView = getLayoutInflater().inflate(R.layout.dialog_offline_save_offline_attendance, null);
        setView(alertView);
        ButterKnife.inject(this, alertView);

        offlineUsername.setText(myOfflineData.getUserID());
        offlineTime.setText(myOfflineData.getLoginDate()+" "+myOfflineData.getLoginTime());
        offlineLat.setText("");
        offlineLong.setText("");
        if(myOfflineData.getImage()!=null)
        offlineImage.setImageBitmap(myOfflineData.getImage());



    }
}
