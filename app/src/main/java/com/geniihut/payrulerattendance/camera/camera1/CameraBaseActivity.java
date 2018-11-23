package com.geniihut.payrulerattendance.camera.camera1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.home.HomeActivity;
import com.geniihut.payrulerattendance.logs.ViewLogsDialog;
import com.geniihut.payrulerattendance.logs.ViewLogsDialogOffline;
import com.geniihut.payrulerattendance.model.OfflineData;
import com.geniihut.payrulerattendance.settings.Settings;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.CustomDigitalClock;
import com.geniihut.payrulerattendance.helpers.LocationHelper;
import com.geniihut.payrulerattendance.helpers.events.GpsProviderEnabledEvent;
import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.OfflineDBHELPER;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public abstract class CameraBaseActivity extends AppCompatActivity {
    @InjectView(R.id.digitalClock)
    CustomDigitalClock mDigitalClock;

    public static final String TAG = CameraBaseActivity.class.getSimpleName();
    public static final String EXTRA_TIME_IN = "extra_time_in";
    public static final String EXTRA_USER = "user";
    final int resultCode = 1101;
    public String mAction;

    public User mUser;

    public String mLongitude;
    public String mLatitude;
    public ProgressDialog progressDialog;
    public boolean isWaitingLocation = false;
    private Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResources());
        ButterKnife.inject(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mUser = (User) getIntent().getExtras().getSerializable("user");
            mLongitude = getIntent().getExtras().getString("long");
            mLatitude = getIntent().getExtras().getString("lat");
            Long time = getIntent().getExtras().getLong("time", -999);
            if (time != -999) {
                AppUtils.logE(this,"mDigitalClock " + mDigitalClock);
                if (mDigitalClock != null) {
                    mDigitalClock.setIsRealTime(true);
                    mDigitalClock.setDate(time);
                    mDigitalClock.setFormat(CustomDigitalClock.FORMAT_LANDSCAPE);
                }
            }

        }
//        mTimeIn = new com.geniihut.payrulerattendance.model.TimeIn();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public Bitmap getResizedBitmap(final Bitmap image, final int maxSize) {

        final int width = image.getWidth();
        final int height = image.getHeight();
        int newWidth;
        int newHeight;
        if (width > height) {
            newWidth = maxSize;
            newHeight = (height * newWidth) / width;
        } else {
            newHeight = maxSize;
            newWidth = (width * newHeight) / height;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);

        return resizedBitmap;
    }

    public int getLayoutResources() {
        return R.layout.activity_camera_base;
    }

    protected void save(Bitmap img) {
        mImage = getResizedBitmap(img, 100);
        String dateTime;

        if(isNetworkAvailable()) {
            if (Settings.getInstance().isSystemTimeUsed()) {
                dateTime = AppUtils.millisecondsDateUTCToLocal(Calendar.getInstance().getTime().getTime());
                saveTimeIn(dateTime, "", "");
            } else if (mLatitude != null) {
                dateTime = AppUtils.millisecondsDateUTCToLocal(mDigitalClock.getDate());
                saveTimeIn(dateTime, mLatitude, mLongitude);
            } else {
                isWaitingLocation = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("Waiting Location...");
                        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isWaitingLocation = false;
                            }
                        });
                        progressDialog.show();
                    }
                });
            }
        }
        else{
            String currentDate =  getCurrentDate();
            String currentTime = getCurrentTime();
             SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
            String userID=  sharedPref.getString("userID","noID");
            String InorOUT = sharedPref.getString("mAction","noAction");
            Log.e(TAG,InorOUT+"  "+userID+" "+currentTime+" "+currentDate);
            OfflineData myOfflineData = new OfflineData(currentDate,currentTime,InorOUT,userID,"none",mImage);
            saveToDDB(myOfflineData);
            Intent intent = new Intent(this,HomeActivity.class);
            intent.putExtra("OFFLINEDATA",   myOfflineData);

            setResult(resultCode,intent);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    finish();



                }
            }, 2000);








        }

    }






    public String getCurrentTime(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

        return df.format(c.getTime());
    }
    public String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c.getTime());


    }
    public void saveToLocal(){
        OfflineDBHELPER myOfflineDBHELPER=new OfflineDBHELPER(this);
        List<OfflineData> offlineDataList = myOfflineDBHELPER.getData();
        for(int i = 0;i<offlineDataList.size();i++){
            Log.e(TAG,offlineDataList.get(i).getUserID()+offlineDataList.get(i).getLoginTime()+offlineDataList.get(i).getTimeInOrOut());
        }

    }
    public void saveToDDB(OfflineData myOfflineData) {
        Intent intent = getIntent();
        OfflineDBHELPER myOfflineDBHELPER= new OfflineDBHELPER(this);
        Boolean ifsaved = myOfflineDBHELPER.addOFFlineDATA(myOfflineData);


        Log.e(TAG, "image - "+myOfflineData.getImage().toString()+"userID - "+myOfflineData.getUserID()+
                " Logintime - "+myOfflineData.getLoginTime()+" TimeInOUT - "+myOfflineData.getTimeInOrOut()+ " - SQLITE-DabaseSaved");
//        saveToLocal();


    }

    protected void saveTimeIn(String dateTime, String latitude, String longitude) {

        Log.e(TAG,"saving database");

        SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        Boolean FaceIsPresent=  sharedPref.getBoolean("FaceIsPresent",false);
            TimeIn timeIn = new TimeIn();
            timeIn.setIdno(mUser.getIdno());
            timeIn.setDateTime(dateTime);
            timeIn.setDate(AppUtils.getDateInputOutput(timeIn.getDateTime(), AppConstants.DATE_FORMAT_LOCAL_TIME, AppConstants.DATE_FORMAT_API_INPUT));
            timeIn.setTime(AppUtils.getDateInputOutput(timeIn.getDateTime(), AppConstants.DATE_FORMAT_LOCAL_TIME, AppConstants.TIME_FORMAT_API_INPUT));
            timeIn.setInout(mAction);
            timeIn.setLatitude(latitude);
            timeIn.setLongitude(longitude);
            timeIn.setImage(mImage);
            if(FaceIsPresent) {
                Log.e(TAG,"attendance successfuly saved in database");

             //   timeIn.setLatitude("Naaynawong gwapo");
              //  timeIn.setLongitude("10.3234475");
                TimeInContract.TimeIn.insert(this, null, timeIn);

            final MediaPlayer mp = MediaPlayer.create(this, R.raw.thankyou);
            mp.start();

        }
                else{

            Log.e(TAG,"Attendance not saved to database");


        }
        //        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME_IN, timeIn);
        intent.putExtra(EXTRA_USER, mUser);
        setResult(RESULT_OK, intent);
        finish();


    }

    public void onEvent(Location event) {
//        Log.e(TAG, "Location");
        mLatitude = Double.toString(event.getLatitude());
        mLongitude = Double.toString(event.getLongitude());
        if (isWaitingLocation) {
            isWaitingLocation = false;
//            APIRequestManager.postSyncLogs(mUser.getIdno(), "", "", mTimeIn.getInout().toLowerCase(Locale.US), mTimeIn.getLatitude(), mTimeIn.getLongitude(), mUser.getPin(), AppUtils.getBitmapAsByteArray(mTimeIn.getImage()), getPostSyncLogsListener(mTimeIn), "TimeInCam");
            String dateTime = AppUtils.millisecondsDateUTCToLocal(event.getTime());
            saveTimeIn(dateTime, mLatitude, mLongitude);
        }
    }

    public void onEvent(GpsProviderEnabledEvent event) {
        switch (event.getGpsProviderEnabled()) {
            case LocationHelper.GPS_PROVIDER_ENABLED: {
                Log.e(TAG, "ENAble");
            }
            break;

            case LocationHelper.GPS_PROVIDER_DISABLED: {
//                showGpsDisabledDialog();
                Log.e(TAG, "DisAble");
            }
            break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}