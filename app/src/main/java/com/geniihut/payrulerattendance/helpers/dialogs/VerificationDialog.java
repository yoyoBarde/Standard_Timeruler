package com.geniihut.payrulerattendance.helpers.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.geniihut.payrulerattendance.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by macmini4 on 8/13/15.
 */
public class VerificationDialog extends AlertDialog {
    public interface VerificationCallBack {
        public void onVerified();
    }
    @InjectView(R.id.etPinCode)
    EditText pincode;

    VerificationCallBack mCallBack;
    String pin;

    public VerificationDialog(Context context, String pin,VerificationCallBack callBack) {
        super(context);
        View alertView = getLayoutInflater().inflate(R.layout.dialog_verification, null);
        setView(alertView);
        ButterKnife.inject(this,alertView);
        this.pin = pin;
        this.mCallBack = callBack;
//        pincode.setText(pin);
    }

    @OnClick(R.id.btnCancel)
    public void onClickCancel() {
        dismiss();
    }

    @OnClick(R.id.btnSubmit)
    public void onClickOk() {
        if (pincode.getText().toString().isEmpty()) {
            pincode.setError("Required!");
        } else if (pin.equals(pincode.getText().toString())) {
            //Do something
            if(mCallBack!=null)
                mCallBack.onVerified();
            dismiss();
        } else {
            pincode.setError("Incorrect PIN Code");
        }
    }

}
