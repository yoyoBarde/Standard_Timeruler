/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.geniihut.payrulerattendance.camera.visionapi;

        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.media.MediaPlayer;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.util.SparseArray;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.geniihut.payrulerattendance.R;
        import com.geniihut.payrulerattendance.camera.camera1.CameraBaseActivity;
        import com.geniihut.payrulerattendance.helpers.Exif;
        import com.geniihut.payrulerattendance.logs.ViewLogsDialog;
        import com.geniihut.payrulerattendance.model.OfflineData;
        import com.geniihut.payrulerattendance.model.TimeIn;
        import com.geniihut.payrulerattendance.model.User;
        import com.geniihut.payrulerattendance.sync.DBHelper;
        import com.geniihut.payrulerattendance.sync.OfflineDBHELPER;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GoogleApiAvailability;
        import com.google.android.gms.vision.CameraSource;
        import com.google.android.gms.vision.Frame;
        import com.google.android.gms.vision.Tracker;
        import com.google.android.gms.vision.face.Face;
        import com.google.android.gms.vision.face.FaceDetector;
        import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

        import java.io.IOException;
        import java.io.InputStream;
        import java.text.DateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

        import butterknife.InjectView;
        import butterknife.OnClick;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public class FaceTrackerActivity extends CameraBaseActivity {

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private final String TAG = FaceTrackerActivity.TAG + "." + GraphicFaceTracker.class.getSimpleName();

        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
//            Log.e(TAG, "onNewItem");
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
//            Log.e(TAG, "onUpdate");
//            Log.e(TAG, "onUpdate getDetectedItemsCount:" + detectionResults.getDetectedItems().size());
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
//            Log.e(TAG, "onMissing");
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
//            Log.e(TAG, "onDone");
            mOverlay.remove(mFaceGraphic);
        }
    }

    @InjectView(R.id.preview)
    protected CameraSourcePreview mPreview;
    @InjectView(R.id.faceOverlay)
    protected GraphicOverlay mGraphicOverlay;
    @InjectView(R.id.tvOfflinemode)
    protected TextView tvOffline;
    private static final String TAG = FaceTrackerActivity.class.getSimpleName();
    private static final String TAG1 = "GAGO";
    private Boolean ifnaayNawong = null;
    private Boolean ReturnHolder = null;
    private Bitmap GlobalImageBitmap;
    private CameraSource mCameraSource = null;
    private String InorOUTPREF = "defaultAction";
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        if(isNetworkAvailable()){
            tvOffline.setVisibility(View.INVISIBLE);
        }
        else
            tvOffline.setVisibility(View.VISIBLE);
        createCameraSource();

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
//        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//        if (rc == PackageManager.PERMISSION_GRANTED) {
//            createCameraSource();
//        } else {
//            requestCameraPermission();
//        }
    }



    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
//    private void requestCameraPermission() {
//        Log.w(TAG, "Camera permission is not granted. Requesting permission");
//
//        final String[] permissions = new String[]{Manifest.permission.CAMERA};
//
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.CAMERA)) {
//            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
//            return;
//        }
//
//        final Activity thisActivity = this;
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(thisActivity, permissions,
//                        RC_HANDLE_CAMERA_PERM);
//            }
//        };
//
//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, listener)
//                .show();
//    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_face_tracker;
    }


    public void getBooleanFace() {


        if (ifnaayNawong) {
            Log.e(TAG, "naanawongbolean");
        } else
            Log.e(TAG, "Wala nawong");
        //showTimeInOutDialog(HomeActivity.gagongTimeIn, HomeActivity.gagongUser);


    }

    public void saveInfo() {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("mAction",InorOUTPREF);
        editor.putBoolean("FaceIsPresent", ifnaayNawong);
        editor.apply();
        Log.e(TAG, "preference saved" + ifnaayNawong.toString());

    }





    public class TakePicture extends AsyncTask<String,Void,String> {
        ImageView imgV;


        @Override
        protected String doInBackground(String... url) {

            try {
                mCameraSource.takePicture(mShutterCallback, mPictureCallback);
                Log.e(TAG,"successxx");
            } catch (Exception e){
                Log.e(TAG,"failed");
                e.printStackTrace();
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

        }

    }
    public void saveToDDB(String TimeINorOUT) {
//        Intent intent = getIntent();
//        String userID = intent.getStringExtra("userID");
//        String userPassword = intent.getStringExtra("passWord");
//        Calendar calendar = Calendar.getInstance();
//        String LogTime = DateFormat.getDateInstance().format(calendar.getTime());
//        OfflineDBHELPER myOfflineDBHELPER= new OfflineDBHELPER(this);
//        OfflineData myOfflineData = new OfflineData(LogTime,TimeINorOUT,userID,userPassword,GlobalImageBitmap);
//        Boolean ifsaved = myOfflineDBHELPER.addOFFlineDATA(myOfflineData);
//        Log.e(TAG, ifsaved.toString() + "SQLITE-DabaseSaved");

    }

    @OnClick(R.id.btnIn)
    public void onClickIn() {
        InorOUTPREF = "IN";
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("mAction",InorOUTPREF);
        editor.apply();

        if(isNetworkAvailable()) {
            disableButtons();
            TakePicture myTakepicture =  new TakePicture();
            myTakepicture.execute();


            mAction = TimeIn.ACTION_IN;
            //mCameraSource.takePicture(mShutterCallback, mPictureCallback);

            Log.e(TAG1, "FaceTrackerPictured");

           new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (ifnaayNawong) {
                        //showTimeInOutDialog(timeIn, mUser);
                        saveInfo();
                        Log.e(TAG, "naaynawong");


                    } else {
                        saveInfo();
                        Log.e(TAG, "walaynawong");
                    }
                }
            }, 3000);
        }
        else {
            TakePicture myTakepicture =  new TakePicture();
            myTakepicture.execute();

            saveToDDB("IN");
        }

    }



    @OnClick(R.id.btnOut)
    public void onClickOut() {
            InorOUTPREF="OUT";
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("mAction",InorOUTPREF);
        editor.apply();

        if(isNetworkAvailable()) {
            TakePicture myTakepicture =  new TakePicture();
            myTakepicture.execute();

            Log.e(TAG1, "FaceTrackerPicturedNot");
            disableButtons();
            mAction = TimeIn.ACTION_OUT;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (ifnaayNawong) {
                        saveInfo();
                        Log.e(TAG, "naaynawong");


                    } else {
                        saveInfo();
                        Log.e(TAG, "walaynawong");
                    }
                }
            }, 3000);

        }
        else{
            TakePicture myTakepicture =  new TakePicture();
            myTakepicture.execute();

saveToDDB("OUT");

        }
    }
    public void saveToLocal(String InOut){

        Log.e(TAG, "saved");
        saveToDDB(InOut);
        finish();
        OfflineDBHELPER myOfflineDBHELPER=new OfflineDBHELPER(this);
        List<OfflineData> offlineDataList = myOfflineDBHELPER.getData();

        for(int i = 0;i<offlineDataList.size();i++){
            Log.e(TAG,offlineDataList.get(i).getLoginTime()+offlineDataList.get(i).getTimeInOrOut());
        }

    }


    private void disableButtons() {
        Button btnIn = (Button) findViewById(R.id.btnIn);
        Button btnOut = (Button) findViewById(R.id.btnOut);
        btnIn.setEnabled(false);
        btnOut.setEnabled(false);
    }

    public void playNoFaceSound() {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.nofacedetected);
        mp.start();
    }

    private CameraSource.ShutterCallback mShutterCallback = new CameraSource.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    private CameraSource.PictureCallback mPictureCallback = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            int orientation = Exif.getOrientation(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            switch (orientation) {
                case 90:
                    bitmap = rotateImage(bitmap, 90);

                    break;
                case 180:
                    bitmap = rotateImage(bitmap, 180);

                    break;
                case 270:
                    bitmap = rotateImage(bitmap, 270);

                    break;
                case 0:
                    // if orientation is zero we don't need to rotate this

                default:
                    break;
            }
            //write your code here to save bitmap
            Bitmap image = detect(bitmap);
            GlobalImageBitmap = image;
            if (image != null) {
                save(image);
            } else {

                save(bitmap);
//                AppUtils.toastShort("No face detected");
//                playNoFaceSound();
//                finish();
            }
        }

    };

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private boolean isFacePresent() {
        if (mGraphicOverlay.isFacePresent()) {
            return true;
        }
//        AppUtils.toastShort("Face not found");
//        playNoFaceSound();
        return false;
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

//        detector.setProcessor(
//                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
//                        .build());
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new GraphicFaceTracker(mGraphicOverlay)));

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device
            Log.e(TAG, "Face detector dependencies are not  yet available.");
        }
//        AppUtils.toastShort("Detector: " + detector.isOperational());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.e(TAG, "createCameraSource width:" + metrics.widthPixels + " height:" + metrics.heightPixels);

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(metrics.widthPixels, metrics.heightPixels)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15.0f)
                .build();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private Bitmap detect(Bitmap bitmap) {
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setProminentFaceOnly(true)
                .setTrackingEnabled(false)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        detector.release();

        Log.e(TAG, "detect \n" +
                "bitmap width:" + bitmap.getWidth() + " height:" + bitmap.getHeight() + "\n" +
                "face width:" + " height:");
        Log.e(TAG, "face size:" + faces.size());


        if (faces.size() == 0)
            ifnaayNawong = false;
        else if (faces.size() == 1)
            ifnaayNawong = true;
        if (faces.size() > 0) {
            Face face = faces.valueAt(0);
//            int x = Math.max((int) (face.getPosition().x - FaceGraphic.ID_X_OFFSET), 0);
//            int y = Math.max((int) (face.getPosition().y - FaceGraphic.ID_Y_OFFSET), 0);
//            int width = Math.min((int) (face.getWidth() - FaceGraphic.ID_X_OFFSET), bitmap.getWidth() - x);
//            int height = Math.min((int) (face.getHeight() + FaceGraphic.ID_Y_OFFSET), bitmap.getHeight() - y);

            int x = (int) face.getPosition().x;
            int y = (int) face.getPosition().y;
            int width = Math.min((int) (face.getWidth()), bitmap.getWidth() - x);
            int height = Math.min((int) (face.getHeight()), bitmap.getHeight() - y);
            if (x < 0) {
                width += x; // += x is negative
                x = 0;
            }
            if (y < 0) {
                height += y; // += y is negative
                y = 0;

            }
            Log.e(TAG, "Y = " + y + " height " + height + " = " + (y + height) + " bH " + bitmap.getHeight());
            Log.e(TAG, "x = " + x + " width " + width + " = " + (x + width) + " bW " + bitmap.getWidth());

            if (y + height > bitmap.getHeight()) {// to avoid java.lang.IllegalArgumentException: y + height must be <= bitmap.height()
                int difference = Math.abs(y + height - bitmap.getHeight()); // Get the difference of bitmap height and y+height
                int divide = difference / 2;                              // divide it to 2
                y += divide;                                            // add it to y and subtract it to height
                height -= divide;                                       // so we can get the center of the fucking face

            }

            if (x + width > bitmap.getWidth()) {// to avoid java.lang.IllegalArgumentException: x + width must be <= bitmap.getWidth()
                int difference = Math.abs(y + height - bitmap.getHeight()); // Get the difference of bitmap width and x + width
                int divide = difference / 2;                              // divide it to 2
                x += divide;                                            // add it to y and subtract it to height
                width -= divide;                                       // so we can get the center of the fucking face
            }


            return Bitmap.createBitmap(bitmap, x, y, width, height);
        }
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
