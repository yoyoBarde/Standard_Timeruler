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
public class ViewLogsDialogNoFace  extends AlertDialog {
    @InjectView(R.id.buttonClose)
    View buttonClose;
    public ViewLogsDialogNoFace(final Context context) {
        super(context);

        View alertView = getLayoutInflater().inflate(R.layout.dialog_time_in_out_no_face, null);
        setView(alertView);
        ButterKnife.inject(this, alertView);


        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }


}
