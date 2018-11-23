package com.geniihut.payrulerattendance.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.camera.camera1.CameraBaseActivity;
import com.geniihut.payrulerattendance.camera.visionapi.FaceTrackerActivity;
import com.geniihut.payrulerattendance.logs.ViewLogsDialog;
import com.geniihut.payrulerattendance.logs.ViewLogsDialogNoFace;
import com.geniihut.payrulerattendance.logs.ViewLogsDialogOffline;
import com.geniihut.payrulerattendance.model.OfflineData;
import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.new_camera.NewCameraSamsung;
import com.geniihut.payrulerattendance.settings.SettingsFragment;
import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.settings.Settings;
import com.geniihut.payrulerattendance.apirequest.APIRequestManager;
import com.geniihut.payrulerattendance.apirequest.RequestResponseListener;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.ConnectivityReceiver;
import com.geniihut.payrulerattendance.helpers.CustomDigitalClock;
import com.geniihut.payrulerattendance.helpers.LocationHelper;
import com.geniihut.payrulerattendance.helpers.events.ConnectivityEvent;
import com.geniihut.payrulerattendance.helpers.events.GpsProviderEnabledEvent;
import com.geniihut.payrulerattendance.helpers.events.GpsStatusChangeEvent;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.ntp.NTPClient;
import com.geniihut.payrulerattendance.sync.SyncUtils;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class HomeActivity extends AppCompatActivity {
        @InjectView(R.id.llButtonsInOUT)
        protected View llButtonsInOUT;
        @InjectView(R.id.btnSubmit)
        protected View mBtnSubmit;
        @InjectView(R.id.etUserName)
        protected EditText mEtUserName;
        @InjectView(R.id.etPassword)
        protected EditText mEtPassword;
        @InjectView(R.id.llCoordinates)
        protected View mLlCoordinates;
        @InjectView(R.id.tvLat)
        protected TextView mTvLat;
        @InjectView(R.id.tvLong)
        protected TextView mTvLong;
        @InjectView(R.id.tvAddress)
        protected TextView mTvAddress;
        @InjectView(R.id.ivSettings)
        protected ImageView ivSettings;
        @InjectView(R.id.offlineTV)
        protected TextView tvOffline;

    //    @InjectView(R.id.tvDate)
    //    protected TextView mTvDate;
    //    @InjectView(R.id.tvTime)
    //    protected TextView tvTitle;

        //    TextClock mTextClock;
        CustomDigitalClock mDigitalClock;

        public static final String TAG = HomeActivity.class.getSimpleName();
        public static User gagongUser;
        public static TimeIn gagongTimeIn;
        private ConnectivityReceiver mConnectivityReceiver;
        private static LocationHelper mLocationHelper;
        private String mLongitude;
        private String mLatitude;
        private boolean isGettingTime = false;
        private Handler syncHandler = null;
        private Runnable mSyncTicker;
        public Boolean FaceDetected;
        private Dialog myDialog;
        final Context context = this;
        private User myUser;
        private User user;
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onCreate(Bundle savedInstanceState)  {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            ButterKnife.inject(this);
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();



    //        new AndroidBug5497Workaround(this);
            mDigitalClock = (CustomDigitalClock) findViewById(R.id.digitalClock);
            mConnectivityReceiver = new ConnectivityReceiver();
            mLocationHelper = new LocationHelper(this, (LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
            setIP();

            getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    checkTime();
                    checkIsPhotos();
                    ivSettings.setVisibility(View.VISIBLE);
                }
            });

            myDialog = new Dialog(getApplicationContext());


            checkTime();
            checkIsPhotos();
            syncTimer();
            setAutoCompleteUsername();
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            checkTime();
            syncTimer();
            setAutoCompleteUsername();
        }

        @Override
        protected void onPause() {
            super.onPause();


            Log.e(TAG, "onDestroy");

        }

        @Override
        protected void onDestroy() {
            super.onDestroy();




            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
            unregisterReceiver(mConnectivityReceiver);
            mLocationHelper.stopRequest();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            mLocationHelper.onActivityResult(requestCode, resultCode, data);
            if (data != null & requestCode == 1 && resultCode == RESULT_OK) {
                com.geniihut.payrulerattendance.model.TimeIn timeIn = data.getParcelableExtra(CameraBaseActivity.EXTRA_TIME_IN);
                com.geniihut.payrulerattendance.model.User user = (User) data.getSerializableExtra(CameraBaseActivity.EXTRA_USER);
                showTimeInOutDialog(timeIn, user);
            }

            if(resultCode==1101){
                OfflineData myOfflineData= data.getExtras().getParcelable("OFFLINEDATA");
            Log.e(TAG,myOfflineData.getLoginTime()+myOfflineData.getLoginDate());
                offlineDataDisplayTobePushed(myOfflineData);
            }
        }


    public void offlineDataDisplayTobePushed(OfflineData myOfflineData) {
        final ViewLogsDialogOffline viewLogDialogOffline = new ViewLogsDialogOffline(this, myOfflineData);

viewLogDialogOffline.show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {




                viewLogDialogOffline.dismiss();

            }
        }, 5000);




    }


        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            String format = null;
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                format = CustomDigitalClock.FORMAT_PORTRAIT;
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                format = CustomDigitalClock.FORMAT_LANDSCAPE;
            }
            if (mDigitalClock != null && format != null) {
                mDigitalClock.setFormat(format);
            }
        }

        @Override
        public void onBackPressed() {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }

        @OnClick(R.id.btnSubmit)
        public void submit(CardView view) {

            if(isNetworkAvailable()) {
                boolean flag = true;
                if (mEtUserName.getText().toString().isEmpty()) {
                    flag = false;
                    mEtUserName.setError("Required.");
                }
                if (mEtPassword.getText().toString().isEmpty()) {
                    flag = false;
                    mEtPassword.setError("Required.");
                }
                if (!mDigitalClock.isHasTime() && !Settings.getInstance().isSystemTimeUsed()) {
                    flag = false;
                    getTimeInNetwork();
                    AppUtils.toastShort("Waiting real time");
                }
                if (flag) {

                    String idno = mEtUserName.getText().toString().trim();
                    String password = mEtPassword.getText().toString();
                    User user = UserContract.User.getUser(this, idno);
                    myUser = UserContract.User.getUser(this, idno);
                    if (user != null) {
                        if (AppUtils.isNetworkAvailable(this)) {
                            APIRequestManager.postSyncConfirmationAndLogin(idno, password, getLoginRequestListener(), AppConstants.REQUEST_TAG_LOGIN);
                        } else {
                            if (user.getPin().equals(password)) {
                                //success

                                goToCameraIfNeeded(user);
                            } else {
                                mEtPassword.setError("Incorrect Password");
                            }
                        }
                    } else {
    //                if (AppUtils.isNetworkAvailable(this)) {
                        APIRequestManager.postSyncConfirmationAndLogin(idno, password, getLoginRequestListener(), AppConstants.REQUEST_TAG_LOGIN);
    //                } else {
    //                    mEtUserName.setError("Invalid username and password");
    //                    mEtPassword.setError("Invalid username and password");
    //                }
                    }
                }
            }
            else{
                String idno = mEtUserName.getText().toString().trim();
                 user = UserContract.User.getUser(this, idno);
                myUser = UserContract.User.getUser(this, idno);

                Log.e(TAG,"No wifi");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_no_internet,null);

                builder.setView(dialogView)
                        .setTitle("No Wifi Connection")
                        .setMessage("No wifi connection detected do you wish to proceed?")
                        .setPositiveButton("proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                goToCameraIfNeeded(user);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(dialogView.getContext(),"Turn on your connection",Toast.LENGTH_SHORT).show();
                            }
                        });

                builder.create();
                builder.show();



            }
        }

        @OnClick(R.id.btnIn)
        public void onClickIn(CardView view) {
            mAction = TimeIn.ACTION_IN;
            submit(view);
        }

        @OnClick(R.id.btnOut)
        public void onClickOut(CardView view) {
            mAction = TimeIn.ACTION_OUT;
            submit(view);

    }

        @OnClick(R.id.ivSettings)
        public void setting(View view) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                SettingsFragment settingsFragment = new SettingsFragment();
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, settingsFragment)
                        .addToBackStack(getString(R.string.title_fragment_settings))
                        .commit();
            }
        }

        public void syncTimer() {
            if (syncHandler == null) syncHandler = new Handler();
            if (mSyncTicker == null) {
                mSyncTicker = new Runnable() {
                    public void run() {
                        if (Settings.getInstance().isAutoSyncLogs()) {
                            SyncUtils.requestSyncTimeIn(null);
                        }
                        if (syncHandler == null)
                            syncHandler = new Handler();
    //                    syncHandler.postAtTime(mSyncTicker, (5 * 60 * 1000));
                        syncHandler.postDelayed(mSyncTicker, (5 * 60 * 1000));
                        checkTime();
                    }
                };
                syncHandler.postDelayed(mSyncTicker, (5 * 60 * 1000));
            }
        }

        public void checkTime() {
            if (Settings.getInstance().isSystemTimeUsed()) {
    //            Log.e(TAG, "onResume isSystemTimeUsed");
                mLlCoordinates.setVisibility(View.GONE);
                mDigitalClock.setIsRealTime(false);
                mDigitalClock.setDate(Calendar.getInstance().getTime().getTime());
            } else if (!mDigitalClock.isRealTime() || mDigitalClock.isNeededToSyncTime()) {
                mDigitalClock.setHasTime(false);
                mLlCoordinates.setVisibility(View.VISIBLE);
                checkGPSIsEnabled();
                getTimeInNetwork();

            }
        }

        public void checkIsPhotos() {
            if (Settings.getInstance().isTakeAPhoto()) {
                llButtonsInOUT.setVisibility(View.GONE);
                mBtnSubmit.setVisibility(View.VISIBLE);
            } else {
                llButtonsInOUT.setVisibility(View.VISIBLE);
                mBtnSubmit.setVisibility(View.GONE);
            }
        }

        private RequestResponseListener getLoginRequestListener() {
            return new RequestResponseListener() {
                ProgressDialog progressDialog;

                @Override
                public void requestStarted() {
                    progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            AppApplication.getInstance().cancelPendingRequests(AppConstants.REQUEST_TAG_LOGIN);
                        }
                    });
                    progressDialog.setMessage("Getting credentials...");
                    progressDialog.show();
                }

                @Override
                public void requestCompleted(JSONObject response) {
                    Log.e(TAG, "getLoginRequestListener " + response.toString());
                    progressDialog.dismiss();
                    try {

                        if (response.has("status") && response.getString("status").equalsIgnoreCase("1")) {
                            //TODO
                            User user = User.create(response);
                            if (UserContract.User.getUser(HomeActivity.this, user.getIdno()) == null) {
                                long _id = Long.parseLong(UserContract.User.insert(HomeActivity.this, null, user).getLastPathSegment());
                                Log.e(TAG, "getLoginRequestListener _id = " + _id);
                                user.set_id(_id);
                            } else {
                                UserContract.User.update(HomeActivity.this, null, user);
                            }
                            goToCameraIfNeeded(user);
                        } else {
                            AppUtils.toastShort("Invalid username/password.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setAutoCompleteUsername();
                }

                @Override
                public void requestEndedWithError(VolleyError error) {
                    Log.e(TAG, error.toString());
                    AppUtils.toastVolleyError(error);
                    progressDialog.dismiss();

    //                User user = new User();
    //                user.setIdno("000001");
    //                user.setFirstName("first");
    //                user.setLastName("last");
    //                user.setMiddleName("middle");
    //                user.setPin("854870");
    //                user.setSystemUser("t");
    //
    //                UserContract.User.insert(HomeActivity.this, null, user);
                }
            };
        }

        private void showTimeInOutDialog(com.geniihut.payrulerattendance.model.TimeIn timeIn, User user) {
    //        final Timer timer = new Timer();
    //        final ViewLogsDialog viewLogDialog = new ViewLogsDialog(this, timeIn, user, true);
    //        viewLogDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
    //            @Override
    //            public void onDismiss(DialogInterface dialog) {
    //                timer.cancel();
    //            }
    //        });
    //        TimerTask timerTask = new TimerTask() {
    //            @Override
    //            public void run() {
    //                viewLogDialog.dismiss();
    //            }
    //        };
    //        timer.schedule(timerTask, 5000);
    //
    //        viewLogDialog.show();

            SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
            Boolean FaceIsPresent=  sharedPref.getBoolean("FaceIsPresent",false);
            Log.e(TAG,"ShowtimeDialog"+FaceIsPresent.toString());

            final Timer timer = new Timer();
            final ViewLogsDialog viewLogDialog = new ViewLogsDialog(this, timeIn, user, true);
            final ViewLogsDialogNoFace viewLogsDialogNoFace = new ViewLogsDialogNoFace(this);

            viewLogDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    timer.cancel();
                }
            });

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    viewLogDialog.dismiss();
                }
            };
            timer.schedule(timerTask,5000);


            if(FaceIsPresent) {

                        viewLogDialog.show();
                    }
                    else if(FaceIsPresent==false){
                            viewLogsDialogNoFace.show();
                        System.out.println("asdasd");
                        Toast.makeText(context, "No Face Detected Please try again!", Toast.LENGTH_LONG).show();

                    }
            viewLogsDialogNoFace.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Log.e(TAG,"onDismiss");
                    goToCameraIfNeeded(myUser);
                }
            });



        }

        private void goToCameraIfNeeded(User user) {
    //        Intent intent;
    //        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
    //            intent = new Intent(this, Camera2Activity.class);
    //        } else {
    //            intent = new Intent(this, CameraActivity.class);
    //        }
            if (Settings.getInstance().isTakeAPhoto()) {
                Intent intent = new Intent(this, FaceTrackerActivity.class);

                Bundle bundle = new Bundle();
                if (mLongitude != null) {
                    bundle.putString("lat", mLatitude);
                    bundle.putString("long", mLongitude);
                }

                bundle.putLong("time", mDigitalClock.getDate());
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                String userID = mEtUserName.getText().toString();
                String passWord = mEtPassword.getText().toString();
                intent.putExtra("userID",userID);
                intent.putExtra("passWord",passWord);


                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userID",userID);
                editor.putString("userPassword",passWord);
                editor.apply();

                startActivityForResult(intent, 1);




            } else {

                save(user);
            }
            mEtUserName.setText("");
            mEtPassword.setText("");

        }

        public void onEvent(com.geniihut.payrulerattendance.model.TimeIn timeIn) {
    //        ViewLogsDialog timeInOutViewDialog = new ViewLogsDialog(this, timeIn, new User());
    //        timeInOutViewDialog.show();
        }

        public void onEvent(Location event) {
            mLatitude = Double.toString(event.getLatitude());
            mLongitude = Double.toString(event.getLongitude());

            String errorMessage = "";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(
                        event.getLatitude(),
                        event.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException e) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
                Log.e(TAG, errorMessage, e);
                mTvAddress.setText(errorMessage);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + event.getLatitude() +
                        ", Longitude = " +
                        event.getLongitude(), illegalArgumentException);
                mTvAddress.setText(errorMessage);
            }

            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG, errorMessage);
                    mTvAddress.setText(errorMessage);
                }
    //            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                mTvAddress.setText(addressFragments.get(0).toString());

                Log.i(TAG, getString(R.string.address_found));
    //            deliverResultToReceiver(Constants.SUCCESS_RESULT,
    //                    TextUtils.join(System.getProperty("line.separator"),
    //                            addressFragments));
            }

            mTvLat.setText(mLatitude);
            mTvLong.setText(mLongitude);
            if (mDigitalClock != null && !mDigitalClock.isHasTime()) {
                if (mLocationHelper.isGsp()) {
                    mDigitalClock.setIsRealTime(true);
                    mDigitalClock.setDate(event.getTime());
                } else {
                    getTimeInNetwork();
                }
            }
            if (isWaitingLocation) {
                isWaitingLocation = false;
    //            APIRequestManager.postSyncLogs(mUser.getIdno(), "", "", mTimeIn.getInout().toLowerCase(Locale.US), mTimeIn.getLatitude(), mTimeIn.getLongitude(), mUser.getPin(), AppUtils.getBitmapAsByteArray(mTimeIn.getImage()), getPostSyncLogsListener(mTimeIn), "TimeInCam");
                String dateTime = AppUtils.millisecondsDateUTCToLocal(event.getTime());
                saveTimeIn(dateTime, mLatitude, mLongitude);
            }
    //        String date = AppUtils.millisecondsDateUTCToLocal(event.getTime());
    //        mTvDate.setText(AppUtils.getDateInputOutput(date, AppConstants.DATE_FORMAT_LOCAL_TIME, AppConstants.DATE_FORMAT_DISPLAY));
    //        tvTitle.setText(AppUtils.getDateInputOutput(date, AppConstants.DATE_FORMAT_LOCAL_TIME, AppConstants.TIME_FORMAT_API_INPUT));
        }

        public void onEvent(GpsProviderEnabledEvent event) {
            switch (event.getGpsProviderEnabled()) {
                case LocationHelper.GPS_PROVIDER_ENABLED: {

                }
                break;

                case LocationHelper.GPS_PROVIDER_DISABLED: {
                    if (!Settings.getInstance().isSystemTimeUsed()) {
                        checkGPSIsEnabled();
                    }
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
                setDefaultTextViews();
            }
                break;
            }
        }

        public void onEvent(ConnectivityEvent event) {
            mLocationHelper.startRequest(event);
            if (event.hasNoConnectivity()) {
                setDefaultTextViews();
            } else {
                getTimeInNetwork();
            }
        }

        private void getTimeInNetwork() {
            if (!Settings.getInstance().isSystemTimeUsed())
                if (AppUtils.isNetworkAvailable(this) && mDigitalClock != null && (!mDigitalClock.isRealTime() || mDigitalClock.isNeededToSyncTime()) && !isGettingTime) {
    //                Log.e(TAG, "getting real time");
    //            mDigitalClock.setHasTime(false);
                    final Handler retryHandler = new Handler();
                    isGettingTime = true;

                    final NTPClient ntpClient = new NTPClient();

                    ntpClient.getRealTime(new NTPClient.NTPClientCallBack() {
                        @Override
                        public void onReceive(long ms) {
    //                        Log.e(TAG, "real time");
                            mDigitalClock.setDate(ms);
                            isGettingTime = false;
                            mDigitalClock.setHasTime(true);
                            mDigitalClock.setIsRealTime(true);
                            retryHandler.removeCallbacksAndMessages(null);
                        }
                    });
                    retryHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ntpClient.cancel();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isGettingTime = false;
                                    checkTime();
                                }
                            }, 1000);
                        }
                    }, 15000);
                }
        }

        private void setDefaultTextViews() {
            mTvLat.setText("");
            mTvLong.setText("");
            mTvAddress.setText("");
    //        mTvDate.setText(getString(R.string.date_default));
    //        tvTitle.setText(getString(R.string.time_default));
        }

        /**********************************
         * IP
         **********************************/

        private void setIP() {
            if (hasIP()) {
            } else {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_folder_name, null, false);
                final EditText etFolderName = ButterKnife.findById(view, R.id.etInput);
                final EditText etCompanyIP = ButterKnife.findById(view, R.id.etCompanyIP);
                final View btnSubmit = ButterKnife.findById(view, R.id.btnSubmit);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etFolderName.getText().toString().isEmpty()) {
                            etFolderName.setError("Required!");
                        } else {
    //                        setIP(editText.getText().toString(),etCompanyIP.getText().toString().trim());
    //                        alertDialog.dismiss();
                            setURL(etFolderName.getText().toString().trim(), etCompanyIP.getText().toString().trim());
                            APIRequestManager.postSyncConfirmationAndLogin("  ", "  ", getCheckAPIRequestListener(alertDialog, etFolderName, etCompanyIP), "CHECK");

                        }
                    }
                });
                alertDialog.show();
            }
        }

        private void setIP(String folderName, String ip) {
            SharedPreferences.Editor editor = getSharedPreferences("payruler_IP", MODE_WORLD_READABLE).edit();
            editor.putString("folder", folderName);
            editor.putString("ip", ip);
            editor.commit();
            hasIP();
        }

        private boolean hasIP() {
            SharedPreferences prefs = getSharedPreferences("payruler_IP", MODE_WORLD_READABLE);
            String IP = prefs.getString("ip", null);
            String folderName = prefs.getString("folder", null);
            boolean flag = true;
            if (IP == null || IP.equalsIgnoreCase("")) {
                IP = APIRequestManager.IP;
            }
            if (folderName != null && !folderName.equalsIgnoreCase("")) {
                Log.e("Folder", IP);
                APIRequestManager.API_SECURE_URL = String.format(APIRequestManager.API_SECURE_FORMAT, IP, folderName);
            } else {
                Log.e("Folder", "null");
                flag = false;
            }
            return flag;
        }

        private void setURL(String folderName, String IP) {
            if (IP == null || IP.equalsIgnoreCase("")) {
                IP = APIRequestManager.IP;
            }
            if (folderName != null && !folderName.equalsIgnoreCase("")) {
                Log.e("Folder", IP);
                APIRequestManager.API_SECURE_URL = String.format(APIRequestManager.API_SECURE_FORMAT, IP, folderName);
            }
        }

        private RequestResponseListener getCheckAPIRequestListener(final AlertDialog alertDialog, final EditText etFolderName, final EditText etCompanyIp) {
            return new RequestResponseListener() {
                ProgressDialog progressDialog;

                @Override
                public void requestStarted() {
                    progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            AppApplication.getInstance().cancelPendingRequests("CHECK");
                        }
                    });
                    progressDialog.setMessage("Checking...");
                    progressDialog.show();
                }

                @Override
                public void requestCompleted(JSONObject response) { // if API response meaning the API exist
                    setIP(etFolderName.getText().toString().trim(), etCompanyIp.getText().toString().trim());
                    alertDialog.dismiss();
                    progressDialog.dismiss();
                }

                @Override
                public void requestEndedWithError(VolleyError error) {
                    if (error != null &&
                            (error instanceof NoConnectionError ||
                                    (error.networkResponse != null && error.networkResponse.statusCode == 404))) {
                        if (!etCompanyIp.getText().toString().trim().isEmpty()) {
                            etCompanyIp.setError("Invalid Company IP/FolderName.");
                            etFolderName.setError("Invalid Company IP/FolderName.");
                        } else {
                            etFolderName.setError("Invalid FolderName.");
                        }
                    } else {
                        AppUtils.toastVolleyError(error);
                    }
                    progressDialog.dismiss();
                }
            };
        }

        /**********************************
         * IP end
         **********************************/


        boolean isWaitingLocation = false;
        User mUser;
        String mAction;
        ProgressDialog progressDialog;

        protected void save(User user) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            mUser = user;
            String dateTime;
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
                        progressDialog = new ProgressDialog(HomeActivity.this);
                        progressDialog.setMessage("Waiting Location...");
                        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isWaitingLocation = false;
                                mUser = null;
                                mAction = null;
                            }
                        });
                        progressDialog.show();
                    }
                });
            }
        }

        protected void saveTimeIn(String dateTime, String latitude, String longitude) {
            Log.d(TAG,"SaveTimeIn");
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
                timeIn.setImage(null);
    //        for (int i = 0; i < 1500; i++) {
            if(FaceIsPresent) {
                Log.d(TAG,"save have face");
                TimeInContract.TimeIn.insert(this, null, timeIn);
    //        }
            }
            else{
                Log.d(TAG,"not save no face");
            }

         showTimeInOutDialog(timeIn, mUser);
            mUser = null;
            mAction = null;

            final MediaPlayer mp = MediaPlayer.create(this, R.raw.thankyou);
            mp.start();
        }

    //    public void onEvent(Location event) {
    ////        Log.e(TAG, "Location");
    //        mLatitude = Double.toString(event.getLatitude());
    //        mLongitude = Double.toString(event.getLongitude());
    //        if (isWaitingLocation) {
    //            isWaitingLocation = false;
    ////            APIRequestManager.postSyncLogs(mUser.getIdno(), "", "", mTimeIn.getInout().toLowerCase(Locale.US), mTimeIn.getLatitude(), mTimeIn.getLongitude(), mUser.getPin(), AppUtils.getBitmapAsByteArray(mTimeIn.getImage()), getPostSyncLogsListener(mTimeIn), "TimeInCam");
    //            String dateTime = AppUtils.millisecondsDateUTCToLocal(event.getTime());
    //            saveTimeIn(dateTime, mLatitude, mLongitude);
    //        }
    //    }


        /**********************************
         * IP end
         **********************************/

        /**********************************
         * GPS DIALOG Start
         **********************************/
        android.support.v7.app.AlertDialog mGPSDialog;

        public void initGpsDisabledDialog() {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Please turn on GPS to find current location.";

            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("SETTINGS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            });

            mGPSDialog = builder.create();
            mGPSDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPossitive = mGPSDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    btnPossitive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(action));
                        }
                    });
                }
            });
        }
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        private void showGPSDialog() {
            if (!mGPSDialog.isShowing()) {
                mGPSDialog.show();
            }
        }

        private void dissmissGPSDialog() {
            mGPSDialog.dismiss();
        }

        private void checkGPSIsEnabled() {
            if (mGPSDialog == null) initGpsDisabledDialog();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDialog();
            } else {
                dissmissGPSDialog();
            }
        }





        /**********************************
         * GPS DIALOG END
         **********************************/

        /**********************************
         * WORKAROUND
         **********************************/


        public class AndroidBug5497Workaround {

            // For more information, see https://code.google.com/p/android/issues/detail?id=5497
            // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    //        public static void assistActivity (Activity activity) {
    //            new AndroidBug5497Workaround(activity);
    //        }

            private View mChildOfContent;
            private int usableHeightPrevious;
            private FrameLayout.LayoutParams frameLayoutParams;

            private AndroidBug5497Workaround(Activity activity) {
                FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
                mChildOfContent = content.getChildAt(0);
                mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }
                });
                frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
            }

            private void possiblyResizeChildOfContent() {
                int usableHeightNow = computeUsableHeight();
                if (usableHeightNow != usableHeightPrevious) {
                    int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                    int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                    if (heightDifference > (usableHeightSansKeyboard / 4)) {
                        // keyboard probably just became visible
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    } else {
                        // keyboard probably just became hidden
                        frameLayoutParams.height = usableHeightSansKeyboard;
                    }
                    mChildOfContent.requestLayout();
                    usableHeightPrevious = usableHeightNow;
                }
            }

            private int computeUsableHeight() {
                Rect r = new Rect();
                mChildOfContent.getWindowVisibleDisplayFrame(r);
                return (r.bottom - r.top);
            }
        }

        private void setAutoCompleteUsername() {

            Cursor userCursor = getContentResolver().query(
                    UserContract.User.CONTENT_URI, null, null, null, null, null
            );
            userCursor.moveToFirst();
            ArrayList<String> names = new ArrayList<String>();
            while(!userCursor.isAfterLast()) {
                names.add(userCursor.getString(userCursor.getColumnIndex(UserContract.Columns.IDNO)));
                userCursor.moveToNext();
            }
            userCursor.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_multichoice, names);

            AutoCompleteTextView userAuto = (AutoCompleteTextView)
                    findViewById(R.id.etUserName);
            userAuto.setAdapter(adapter);
        }
}

