package com.geniihut.payrulerattendance.logs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.model.User;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by macmini4 on 8/13/15.
 */
public class ViewLogsDialog extends AlertDialog {
    @InjectView(R.id.btnDelete)
    View btnDelete;
    @InjectView(R.id.btnClose)
    View btnClose;
    @InjectView(R.id.vName)
    View vName;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvTime)
    TextView tvTime;
    @InjectView(R.id.tvLat)
    TextView tvLat;
    @InjectView(R.id.tvLong)
    TextView tvLong;
    @InjectView(R.id.tvAction)
    TextView tvAction;
    @InjectView(R.id.ivImage)
    public ImageView ivImage;
    @InjectView(R.id.llImage)
    public View llImage;

    User mUser;
    TimeIn mTimeIn;

    String mAction;
//    LocationChangeEvent mLocationChangeEvent;

    public ViewLogsDialog(final Context context, TimeIn timeIn, User user, boolean isNameVisible) {
        super(context);
        mUser = user;
        mTimeIn = timeIn;
        View alertView = getLayoutInflater().inflate(R.layout.dialog_time_in_out_view, null);
        setView(alertView);
        ButterKnife.inject(this, alertView);
        tvTime.setText(mTimeIn.getDateTime());
        tvLat.setText(mTimeIn.getLatitude());
        tvLong.setText(mTimeIn.getLongitude());
        tvAction.setText(mTimeIn.getInout());
        if(mTimeIn.getImage() == null) llImage.setVisibility(View.GONE);
        else ivImage.setImageBitmap(mTimeIn.getImage());
        btnDelete.setVisibility(View.GONE);
        if(!isNameVisible){
            vName.setVisibility(View.GONE);
        }else{
            tvName.setText(mUser.getFullName());
        }
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VerificationDialog dialog = new VerificationDialog(context, mUser.getPin(), new VerificationDialog.VerificationCallBack() {
//                    @Override
//                    public void onVerified() {
//                        mTimeIn.setInout("DELETE");
//                        EventBus.getDefault().post(mTimeIn);
//                        dismiss();
//                    }
//                });
//                dialog.show();
//
//            }
//        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}
