package com.geniihut.payrulerattendance.helpers.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by macmini3 on 8/13/15.
 */
public class LoginDialog extends AlertDialog {
    @InjectView(R.id.tvTitle)
    public TextView tvTitle;
    @InjectView(R.id.etUserName)
    public EditText etUserName;
    @InjectView(R.id.etPassword)
    public EditText etPassword;
    @InjectView(R.id.btnSubmit)
    public View btnSubmit;
    @InjectView(R.id.btnCancel)
    public View btnCancel;

    public LoginDialog(Context context, String title) {
        super(context);
        View alertView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        setView(alertView);
        ButterKnife.inject(this, alertView);

        tvTitle.setText(title);
        btnSubmit.setOnClickListener(mDefaultOnClickListener);
        btnCancel.setOnClickListener(mDefaultOnClickListener);
    }

    private View.OnClickListener mDefaultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
