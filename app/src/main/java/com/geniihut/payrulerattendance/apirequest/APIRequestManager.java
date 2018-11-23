package com.geniihut.payrulerattendance.apirequest;


import android.graphics.Bitmap;

import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.model.User;

import java.util.HashMap;
import java.util.Map;

public class APIRequestManager {

    /*geniihut official up*/
//    public static final String IP = "122.52.127.161:8081";
//    public static final String FOLDER_NAME = "";
//    public static String API_SECURE_URL = "https://122.52.127.161:8081/geniihutapi";

    /*geniihut official local*/
//    public static final String IP = "10.224.1.20";
//    public static final String FOLDER_NAME = "";
//  public static String API_SECURE_URL = "http://10.224.1.20/payrulerapi";

    /*geniihut wifi test*/
    public static final String IP = "122.52.120.232:8072";
//    public static final String IP = "122.52.127.161:8081";
//    public static final String FOLDER_NAME = "testapi";z
    public static final String FOLDER_NAME = "payrulerapi";
    public static String API_SECURE_URL = "https://122.52.127.161:8081/testapi";

    /*geniihut wifi joshua*/
//    public static final String IP = "10.224.1.112";
//    public static final String FOLDER_NAME = "payrulerapi";
//    public static String API_SECURE_URL = "http://10.224.1.112/payrulerapi";

    /*maam kat hotspot*/
//    public static final String IP = "172.20.10.13";
//    public static final String FOLDER_NAME = "payrulerapi";
//    public static String API_SECURE_URL = "http://172.20.10.13/payrulerapi";

    static public final String API_VERSION = "v1/api";
    static public final String API_ID = "payruler_api_8915e07fc6c4b11";
    public static final String API_SECURE_FORMAT = "https://%s/%s";

    //AUTHENTICATION AND USER
    static private final String POST_SYNC_CONFIRMATION_AND_LOGIN = "%s/%s/login_sync";

    //SYNC
    static private final String POST_SYNC_LOGS = "%s/%s/sync";

    //AUTHENTICATION AND USER
    static public void postSyncConfirmationAndLogin(String idno, String pin,
                                                    final RequestResponseListener listener, String tag) {
        String urlPath = String
                .format(POST_SYNC_CONFIRMATION_AND_LOGIN,
                        API_SECURE_URL,
                        API_VERSION);
        Map<String, String> params = new HashMap<String, String>();
        params.put(User.IDNO, idno);
        params.put(User.PIN, pin);
        AppVolleyRequest.postRequest(urlPath, params, listener, tag);
    }

    //SYNC
    static public void postSyncLogs(String idno, String date, String time, String inout, String latitude, String longitude, String pin, byte[] bitmap, final RequestResponseListener listener, String tag) {
        String urlPath = String
                .format(POST_SYNC_LOGS,
                        API_SECURE_URL,
                        API_VERSION);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TimeIn.IDNO, idno);
        params.put(TimeIn.DATE, date);
        params.put(TimeIn.TIME, time);
        params.put(TimeIn.INOUT, inout);
        params.put(TimeIn.LATITUDE, latitude);
        params.put(TimeIn.LONGITUDE, longitude);
        params.put(User.PIN, pin);
        if(bitmap == null){
            bitmap = AppUtils.getBitmapAsByteArray(Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888));
        }
        AppVolleyRequest.postMultiPartRequest(urlPath, params, bitmap, listener, tag);
    }


}
