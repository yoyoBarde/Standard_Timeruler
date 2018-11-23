package com.geniihut.payrulerattendance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.geniihut.payrulerattendance.apirequest.APIRequestManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by macmini3 on 6/22/15.
 */
public class IPActivity extends ActionBarActivity {
    @InjectView(R.id.etIP)
    protected TextView mEtIP;
    @InjectView(R.id.tvIP)
    protected TextView mTvIP;
    @InjectView(R.id.etFolderName)
    protected TextView mEtFolderName;

    public static final String PAYRULER_IP = "payruler_IP";

    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mSharedPreferences = getSharedPreferences(PAYRULER_IP, MODE_WORLD_READABLE);
        String IP = mSharedPreferences.getString("ip", null);
        String folderName = mSharedPreferences.getString("folder", null);
        if (IP != null) {
            mEtIP.setText(IP);
        }
        if(folderName!=null){
            mEtFolderName.setText(folderName);
        }
        setIP();
        mTvIP.setText(APIRequestManager.API_SECURE_URL);
    }

    @OnClick(R.id.btnSave)
    public void save(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("ip", mEtIP.getText().toString());
        editor.putString("folder", mEtFolderName.getText().toString());
        editor.commit();
        Toast.makeText(this, "IP Saved!", Toast.LENGTH_SHORT).show();
        setIP();
        finish();

    }
    private void setIP() {
        SharedPreferences prefs = getSharedPreferences("payruler_IP", MODE_WORLD_READABLE);
        String IP = prefs.getString("ip", null);
        String folderName = prefs.getString("folder", null);
        if (IP != null && !IP.equalsIgnoreCase("")) {
            Log.e("IP", IP);
        }else {
            Log.e("IP", "null");
            IP = APIRequestManager.IP;
        }
        if (folderName != null && !folderName.equalsIgnoreCase("")) {
            Log.e("Folder", IP);
        }else {
            Log.e("Folder", "null");
            folderName = APIRequestManager.FOLDER_NAME;
        }
        APIRequestManager.API_SECURE_URL = String.format(APIRequestManager.API_SECURE_FORMAT, IP,folderName);
    }
}
