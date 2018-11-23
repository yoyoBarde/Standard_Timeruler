package com.geniihut.payrulerattendance;

import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.geniihut.payrulerattendance.apirequest.APIRequestManager;
import com.geniihut.payrulerattendance.helpers.MySSLSocketFactory;
import com.geniihut.payrulerattendance.helpers.Preferences;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.settings.Settings;
import com.geniihut.payrulerattendance.sync.SyncUtils;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.services.GenericAccountService;

import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mezakitchie on 3/3/2015.
 */
public class AppApplication extends Application {
    private static final String TAG = "VolleyPatterns";
    private DefaultHttpClient mHttpClient;
    private static AppApplication mInstance;
    private static RequestQueue mRequestQueue;
    private static User mUser;
    private static String mAccessToken;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        setIP();
        SyncUtils.createSyncAccount(this);
        setSync();
        Fabric.with(this, new Crashlytics());
//        mInstance.setUser(new User("0014248","Brad","Pitt","Dec 18, 1963","bradpitt@gmail.com","Mobile","Payroll","930972443","0732183279","121106462450","110504057984","02/03/2004",
//                "http://www.walldesk-hd.com/wp-content/uploads/2014/09/nice-pics-468-brad-pitt.jpg",
//                "http://img2-1.timeinc.net/people/i/2015/news/150119/brad-pitt-800.jpg"));
//        public User(String id, String firstName, String lastName, String birthDate, String division, String department, String tin, String sss, String hdmf, String philHealth, String dateHired) {
    }

    private void setIP() {
        SharedPreferences prefs = getSharedPreferences("payruler_IP", MODE_WORLD_READABLE);
        String IP = prefs.getString("ip", null);
        String folderName = prefs.getString("folder", null);
        if (IP == null || IP.equalsIgnoreCase("")) {
            IP = APIRequestManager.IP;
        }
        if (folderName != null && !folderName.equalsIgnoreCase("")) {
            APIRequestManager.API_SECURE_URL = String.format(APIRequestManager.API_SECURE_FORMAT, IP, folderName);
        }
    }

    private void setSync() {
        boolean isAutoSyncLogs = Settings.getInstance().isAutoSyncLogs();
        ContentResolver.setSyncAutomatically(GenericAccountService.getAccount(), TimeInContract.CONTENT_AUTHORITY, isAutoSyncLogs);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mHttpClient = getNewHttpClient();
            mRequestQueue = Volley.newRequestQueue(this, new HttpClientStack(
                    mHttpClient));
        }

        return mRequestQueue;
    }

    public DefaultHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static User getUser() {
        return AppApplication.mUser;
    }

    //
    public static void setUser(User user) {
        AppApplication.mUser = user;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static synchronized AppApplication getInstance() {
        return mInstance;
    }

    public static void setAccessToken(String accessToken) {
        AppApplication.mAccessToken = accessToken;
    }

    public static AppApplication get() {
        return mInstance;
    }

    public static String getAccessToken() {
        if (mAccessToken == null || mAccessToken.isEmpty()) {
            mAccessToken = (new Preferences(getInstance())).getAuthToken();
        }
        return mAccessToken;
    }
}
