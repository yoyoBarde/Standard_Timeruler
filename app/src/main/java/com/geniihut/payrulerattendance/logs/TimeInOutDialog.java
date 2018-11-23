package com.geniihut.payrulerattendance.logs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.LocationHelper;
import com.geniihut.payrulerattendance.helpers.events.GpsStatusChangeEvent;
import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.model.User;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by macmini4 on 8/13/15.
 */
public class TimeInOutDialog extends DialogFragment {
    @InjectView(R.id.tvTitle)
    public TextView tvTitle;
    @InjectView(R.id.etPinCode)
    public EditText pincode;
    @InjectView(R.id.btnSubmit)
    public CardView login;
    @InjectView(R.id.btnCancel)
    public CardView cancel;
    @InjectView(R.id.tvOk)
    public TextView tvOk;
    @InjectView(R.id.tvTime)
    public TextView tvTime;
    @InjectView(R.id.tvLat)
    public TextView tvLat;
    @InjectView(R.id.tvLong)
    public TextView tvLong;
    @InjectView(R.id.ivImage)
    public ImageView ivImage;

    User mUser;
    String mAction;
    Bitmap bitmap;

    public static String ACTION = "ACTION";
    public static String USER = "USER";

    private Location mLocation;

//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        Activity owner = getOwnerActivity();
//        if (owner != null && owner instanceof BaseGPSActivity) {
//            ((BaseGPSActivity)owner).showGpsDisabledDialog();
//        }
//    }


//    protected TimeInOutDialog(Context context, String action, User user) {
////        super(context);
//
//        mUser = user;
//        mAction = action;
//
//    }

//    @Override
//    public void show() {
//        super.show();
//        EventBus.getDefault().register(this);
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void resizeDialog() {
        Dialog dialog = getDialog();
        if (dialog != null) {
//            Rect displayRectangle = new Rect();
//            Window window = getActivity().getWindow();
//            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//            (int)(displayRectangle.width()*0.9f)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        resizeDialog();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View alertView = inflater.inflate(R.layout.dialog_time_in_out, null);
        ButterKnife.inject(this, alertView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable(USER);
            mAction = bundle.getString(ACTION);
        }


        final String time_in = getString(R.string.dialog_title_time_in);
        String time_out = getString(R.string.dialog_title_time_out);
        tvTitle.setText(time_in.equalsIgnoreCase(mAction) ? time_in : time_out);
//        pincode.setText("526626");
//        pincode.setText("496338");
        tvOk.setText(mAction);
        if (mAction.equalsIgnoreCase("OUT"))
            login.setCardBackgroundColor(getResources().getColor(R.color.time_in_btn_out));
        else login.setCardBackgroundColor(getResources().getColor(R.color.time_in_btn_in));
        login.setOnClickListener(new View.OnClickListener() {
            private static final String TAG ="TimeInOUtDialog" ;

            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (pincode.getText().toString().isEmpty()) {
                    pincode.setError("Required!");
                    flag = false;
                }
                if (bitmap == null) {
                    AppUtils.toastLong("No Image");
                    flag = false;
                }

                if (!mUser.getPin().equals(pincode.getText().toString())) {
                    flag = false;
                    pincode.setError("Incorrect PIN Code");
                }

                if (flag) {
                    //Do something
                    if (mLocation == null) {
                        AppUtils.toastLong("Wating Location");
                    } else {
                        Log.e(TAG,"Ambot");

                        TimeIn timeIn = new TimeIn();
                        timeIn.setInout(mAction);
                        timeIn.setIdno(mUser.getIdno());
                        timeIn.setDateTime(AppUtils.millisecondsDateUTCToLocal(mLocation.getTime()));
                        timeIn.setDate(AppUtils.getDateInputOutput(timeIn.getDateTime(), AppConstants.DATE_FORMAT_LOCAL_TIME, AppConstants.DATE_FORMAT_API_INPUT));
                        timeIn.setTime(AppUtils.getDateInputOutput(timeIn.getDateTime(), AppConstants.DATE_FORMAT_LOCAL_TIME, AppConstants.TIME_FORMAT_API_INPUT));
                        timeIn.setLatitude(mLocation.getLatitude() + "");
                        timeIn.setLongitude(mLocation.getLongitude() + "");
                        timeIn.setImage(bitmap);
//                        AppUtils.toastLong("time" +mAction);
                        EventBus.getDefault().post(timeIn);

                        Log.e(TAG,"Ambot");
                        dismiss();
                    }

//                                Intent intent = new Intent(HomeActivity.this,ViewLogsActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("timeIn",timeIn);
//                                intent.pxutEtras(bundle);
//                                startActivity(intent);
//                                alertDialog.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return alertView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    public void onEvent(Location event) {
//        AppUtils.toastShort("event" + event.getLocalTime());
        mLocation = event;
        tvTime.setText(AppUtils.millisecondsDateUTCToLocal(event.getTime()));
        tvLat.setText(mLocation.getLatitude() + "");
        tvLong.setText(mLocation.getLongitude() + "");
    }

    public void onEvent(GpsStatusChangeEvent event) {
        switch (event.getGpsSignal()) {
            case LocationHelper.GPS_SIGNAL_AVAILABLE: {

            }
            break;

            case LocationHelper.GPS_SIGNAL_UNAVAILABLE: {
//                tvTime.setText(getContext().getString(R.string.gps_time_default));
//                tvLat.setText("");
//                tvLong.setText("");
            }
            break;
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 999;

    @OnClick(R.id.ivImage)
    public void onClickImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap scaledBitmap = resize(imageBitmap, 100, 100);
            Log.e("TAG", "Sc W" + scaledBitmap.getWidth() + " H" + scaledBitmap.getHeight());
            ivImage.setImageBitmap(scaledBitmap);
            bitmap = scaledBitmap;
//            Log.e("bitmapsize", AppUtils.sizeOf(bitmap)+"");
        }
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}
