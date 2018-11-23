package com.geniihut.payrulerattendance.camera.camera2;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.camera.camera1.CameraBaseActivity;
import com.geniihut.payrulerattendance.model.User;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends CameraBaseActivity implements Camera2BasicFragment.Camera2BasicCallBack {

    Camera2BasicFragment mCamera2BasicFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        if (null == savedInstanceState) {
            mCamera2BasicFragment = Camera2BasicFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mCamera2BasicFragment,"Camera2Basic")
                    .commit();
        }
        else{
            mCamera2BasicFragment = (Camera2BasicFragment) getFragmentManager().findFragmentByTag("Camera2Basic");
        }
    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_camera2;
    }

    @Override
    public void onCapture(Bitmap bitmap, String action) {
        mAction = action;
//        ApiRequest(bitmap);
        save(bitmap);
    }

    @Override
    public User getUser() {
        return mUser;
    }
}
