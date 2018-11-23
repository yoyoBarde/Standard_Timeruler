package com.geniihut.payrulerattendance.camera.camera1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.helpers.LocationHelper;
import com.geniihut.payrulerattendance.helpers.events.GpsProviderEnabledEvent;

import java.io.ByteArrayOutputStream;

import butterknife.InjectView;
import butterknife.OnClick;

public class CameraActivity extends CameraBaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();

    @InjectView(R.id.btnIn)
    Button mBtnIn;
    @InjectView(R.id.btnOut)
    Button mBtnOut;

    Preview preview;
    Camera mCamera;
    int mOrientation = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                enableButtons(true);
            }
        });
        preview = new Preview(this, (SurfaceView) findViewById(R.id.surfaceView));
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                mCamera = Camera.open(findFrontFacingCamera());
                resetCam();

            } catch (RuntimeException ex) {
                Toast.makeText(this, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        if (mCamera != null) {
            mCamera.stopPreview();
            preview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        preview.setKeepScreenOn(false);
        super.onStop();
    }

    private void resetCam() {
        mCamera.startPreview();
        preview.setCamera(mCamera);
        enableButtons(true);
        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();
        if(mDisplay.getRotation()== Surface.ROTATION_0){
            mCamera.setDisplayOrientation(90);
            mOrientation = -90;
        }else if(mDisplay.getRotation()== Surface.ROTATION_270){ //width
            mCamera.setDisplayOrientation(180);
            mOrientation = 180;
        }else if(mDisplay.getRotation()== Surface.ROTATION_90){ 				//left
            mCamera.setDisplayOrientation(0);
            mOrientation = 0;
        }
//        Log.d("ORIENTATION_TEST", "getOrientation(): " + mDisplay.getOrientation() + " " + mDisplay.getRotation());
    }

//    private void refreshGallery(File file) {
//        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(Uri.fromFile(file));
//        sendBroadcast(mediaScanIntent);
//    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e(TAG, "data length" + data.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length,options);
//            if(temp == null){
//                InputStream is = new ByteArrayInputStream(data);
//                temp = BitmapFactory.decodeStream(is);
//            }
            if (temp == null) {
//                FileOutputStream output = null;
//                File file = new File(CameraActivity.this.getExternalFilesDir(null), "pic.png");
//                try {
//                    output = new FileOutputStream(file);
//                    output.write(data);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (null != output) {
//                        try {
//                            Log.e(TAG,"Save "+file.toString());
//                            output.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }


                Camera.Parameters parameters = mCamera.getParameters();
                int format = parameters.getPreviewFormat();
                Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                YuvImage yuvimage = new YuvImage(data, format, previewSize.width, previewSize.height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);
                byte[] jdata = baos.toByteArray();

                // Convert to Bitmap
                temp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
            }
//            ApiRequest(temp);
            save(temp);
            resetCam();
        }
    };

//    @Override
//    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // CREATE A MATRIX FOR THE MANIPULATION
//        Matrix matrix = new Matrix();
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight);
//        matrix.postRotate(mOrientation);
//        // "RECREATE" THE NEW BITMAP
//        Bitmap resizedBitmap = Bitmap.createBitmap(
//                bm, 0, 0, width, height, matrix, true);
//        return resizedBitmap;
//    }

    private int findFrontFacingCamera() {
        int cameraId = 0;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
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

    @OnClick(R.id.btnIn)
    public void onClickIn() {
        takePicture();
        mAction = "IN";
        enableButtons(false);
    }

    @OnClick(R.id.btnOut)
    public void onClickOut() {
        takePicture();
        mAction = "OUT";
        enableButtons(false);
    }

    public void enableButtons(boolean flag){
        mBtnIn.setEnabled(flag);
        mBtnOut.setEnabled(flag);
    }

    public void takePicture() {
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_camera;
    }
}