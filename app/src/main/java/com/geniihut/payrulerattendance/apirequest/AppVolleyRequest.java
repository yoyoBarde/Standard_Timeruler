package com.geniihut.payrulerattendance.apirequest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lorenaromero on 2/23/2015.
 */
public class AppVolleyRequest {
    public static final String TAG = AppVolleyRequest.class.getSimpleName();
    public static final String AUTHORIZATION = "Authorization";
    public static final String PAYRULER_API_KEY = "Payruler-Api-Key";
    public static final String PAYRULER_SESSION_KEY = "Payruler-Session-Key";

////    protected String mAccessToken;
//
//    public AppVolleyRequest() {
////        mAccessToken = HandyAppAgentApplication.getInstance().getAccessToken();
//    }


    public static void postRequest(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.POST, Request.Priority.IMMEDIATE, urlPath, params, null, listener, TAG);
    }

    public static void postRequestWithAccessToken(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.POST, Request.Priority.IMMEDIATE, urlPath, params, getHeaderAccestoken(), listener, TAG);
    }

    public static void getRequest(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.GET, Request.Priority.IMMEDIATE, urlPath, params, null, listener, TAG);
    }

    public static void getArrayRequest(String urlPath, JSONObject params, RequestArrayResponseListener listener) {
        sendRequestArray(Request.Method.GET, Request.Priority.IMMEDIATE, urlPath, params, null, listener);
    }

    public static void getRequestWithAccessToken(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.GET, Request.Priority.IMMEDIATE, urlPath, params, getHeaderAccestoken(), listener, TAG);
    }

    public static void putRequest(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.PUT, Request.Priority.IMMEDIATE, urlPath, params, null, listener, TAG);
    }

    public static void putRequestWithAccessToken(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.PUT, Request.Priority.IMMEDIATE, urlPath, params, getHeaderAccestoken(), listener, TAG);
    }

    public static void deleteRequest(String urlPath, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.DELETE, Request.Priority.IMMEDIATE, urlPath, null, null, listener, TAG);
    }

    public static void deleteRequest(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.DELETE, Request.Priority.IMMEDIATE, urlPath, params, null, listener, TAG);
    }

    public static void deleteRequestWithAccessToken(String urlPath, Map<String, String> params, RequestResponseListener listener, String TAG) {
        sendRequest(Request.Method.DELETE, Request.Priority.IMMEDIATE, urlPath, params, getHeaderAccestoken(), listener, TAG);
    }

    private static HashMap<String, String> getHeaderAccestoken() {
        HashMap<String, String> params = getHeaders();
        params.put(PAYRULER_SESSION_KEY, AppApplication.getAccessToken());
        return params;
    }

    private static HashMap<String, String> getHeaders() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PAYRULER_API_KEY, APIRequestManager.API_ID);
//        params.put("x-engine-id","88567757-7aa8-4a1b-bc9c-fbc7f5231829");
//        params.put("x-engine-secret","user-handy");
        return params;
    }

    public static void sendRequest(int requestMethod, Request.Priority priority, String urlPath, Map<String, String> params, final HashMap<String, String> headers, final RequestResponseListener listener, final String tag) {
        final HashMap<String, String> requestHeaders;
        if (headers != null)
            requestHeaders = headers;
        else {
            requestHeaders = getHeaders();
        }
//        urlPath = "http://52.74.51.130:9999/v1/login";
        if (params != null) {
            Log.e(TAG, tag + "\n" + urlPath + "\n" + requestHeaders.toString() + "\n" + params.toString());
        } else {
            Log.e(TAG, tag + "\n" + urlPath + "\n" + requestHeaders.toString());
        }
        listener.requestStarted();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (listener != null) {
                    try {
                        JSONObject json = new JSONObject(response);
                        listener.requestCompleted(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null)
                    listener.requestEndedWithError(error);
            }
        };


        CStringObjectRequest request = new CStringObjectRequest(requestMethod, urlPath,
                responseListener, errorListener, params) {

            @Override
            public HashMap<String, String> getHeaders() {
                return requestHeaders;
            }
        };

        request.setPriority(priority);
        AppApplication.getInstance().addToRequestQueue(request, tag);
    }

    public static void sendRequestArray(int requestMethod, Request.Priority priority, String urlPath, JSONObject params, final HashMap<String, String> headers, final RequestArrayResponseListener listener) {
        final HashMap<String, String> requestHeaders;
        if (headers != null)
            requestHeaders = headers;
        else {
            requestHeaders = getHeaders();
        }
        listener.requestStarted();
        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (listener != null)
                    listener.requestCompleted(response);
            }
        };
        Response.ErrorListener erorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null)
                    listener.requestEndedWithError(error);
            }
        };

        CJsonArrayRequest request = new CJsonArrayRequest(requestMethod, urlPath, params,
                responseListener, erorListener) {

            @Override
            public HashMap<String, String> getHeaders() {
                return requestHeaders;
            }
        };

        request.setPriority(priority);
        AppApplication.getInstance().addToRequestQueue(request);
    }

    public static void postMultiPartRequest(String urlPath, HashMap<String, String> params, byte[] bitmap, RequestResponseListener listener, String TAG) {
        sendMultiPartRequest(Request.Priority.IMMEDIATE, urlPath, params, getHeaderAccestoken(), bitmap, listener, TAG);
    }

    public static void sendMultiPartRequest(Request.Priority priority, String urlPath, HashMap<String, String> params, final HashMap<String, String> headers, final byte[] bitmap, final RequestResponseListener listener, final String tag) {
        final HashMap<String, String> requestHeaders;
        if (headers != null)
            requestHeaders = headers;
        else {
            requestHeaders = getHeaders();
        }
//        urlPath = "http://52.74.51.130:9999/v1/login";
        if (params != null) {
            Log.e(TAG, tag + "\n" + urlPath + "\n" + requestHeaders.toString() + "\n" + params.toString());
        } else {
            Log.e(TAG, tag + "\n" + urlPath + "\n" + requestHeaders.toString());
        }
        listener.requestStarted();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(TAG, tag + "\n" + response);
                if (listener != null) {
                    try {
                        JSONObject json = new JSONObject(response);
                        listener.requestCompleted(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null)
                    listener.requestEndedWithError(error);
            }
        };


        MultipartRequest request = new MultipartRequest(urlPath, errorListener, responseListener, bitmap, params) {

            @Override
            public HashMap<String, String> getHeaders() {
                return requestHeaders;
            }
        };

        AppApplication.getInstance().addToRequestQueue(request, tag);
    }
}
