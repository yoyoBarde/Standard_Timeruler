package com.geniihut.payrulerattendance.apirequest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.geniihut.payrulerattendance.helpers.AppConstants;

import java.util.Map;

public class CStringObjectRequest extends StringRequest {
	private Priority lowPriority = Priority.IMMEDIATE;

//	public CJsonObjectRequest(String url, JSONObject jobj,
//			Listener<JSONObject> listener, ErrorListener errorListener) {
//		super(url, jobj, listener, errorListener);
//		this.setRetryPolicy(new DefaultRetryPolicy(
//				AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
//	}

//	public CJsonObjectRequest(int i, String url, HashMap jobj,
//			Listener<JSONObject> listener, ErrorListener errorListener) {
//
////		super(i, url, jobj, listener, errorListener);
//		// this.setRetryPolicy(new DefaultRetryPolicy(
//		// AppConstants.REQUEST_SET_RETRY_POLICY_MILLI,
//		// DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//		// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//		this.setRetryPolicy(new DefaultRetryPolicy(
//				AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
//	}

    private Map<String, String> mParams;

    public CStringObjectRequest(String url, Listener<String> listener, ErrorListener errorListener, Map<String, String> mParams) {
        super(url, listener, errorListener);
        this.mParams = mParams;
        this.setRetryPolicy(new DefaultRetryPolicy(
                AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
    }

    public CStringObjectRequest(int method, String url, Listener<String> listener, ErrorListener errorListener, Map<String, String> mParams) {
        super(method, url, listener, errorListener);
        this.mParams = mParams;
        this.setRetryPolicy(new DefaultRetryPolicy(
                AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
    }

    @Override
    public Map<String, String> getParams() {
        return mParams;
    }
    @Override
	public Priority getPriority() {
		return lowPriority;
	}


    public void setPriority(Priority priority) {
		lowPriority = priority;
	}

}
