package com.geniihut.payrulerattendance.apirequest;

import android.content.Entity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.geniihut.payrulerattendance.helpers.AppConstants;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by macmini4 on 8/18/15.
 */
public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private static final String FILE_PART_NAME = "image";
    private static final String STRING_PART_NAME = "text";

    private final Response.Listener<String> mListener;
//    private final File mFilePart;
    byte[] mBitmap;
    private final HashMap<String,String>  mStringPart;

    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, byte[] bitmap, HashMap<String,String> stringParts)
    {
        super(Method.POST, url, errorListener);

        mListener = listener;
//        mFilePart = file;
        mBitmap = bitmap;
        mStringPart = stringParts;
        buildMultipartEntity();
        this.setRetryPolicy(new DefaultRetryPolicy(
                AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
    }

    private void buildMultipartEntity()
    {
//        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
        try
        {
            for(Map.Entry<String, String>  stringPart:mStringPart.entrySet()) {
                entity.addPart(stringPart.getKey(), new StringBody(stringPart.getValue()));
            }
            if(mBitmap != null)
            entity.addPart(FILE_PART_NAME, new ByteArrayBody(mBitmap, "timeruler.jpg"));
        }
        catch (UnsupportedEncodingException e)
        {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType()
    {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response)
    {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response)
    {
        mListener.onResponse(response);
    }
}
