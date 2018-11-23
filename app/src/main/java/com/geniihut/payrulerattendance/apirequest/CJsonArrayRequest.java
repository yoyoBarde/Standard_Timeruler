package com.geniihut.payrulerattendance.apirequest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.geniihut.payrulerattendance.helpers.AppConstants;

import org.json.JSONArray;
import org.json.JSONObject;

public class CJsonArrayRequest extends JsonArrayRequest {
	private Priority lowPriority = Priority.IMMEDIATE;

	public CJsonArrayRequest(String url, JSONArray jobj,
                             Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
		this.setRetryPolicy(new DefaultRetryPolicy(
				AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
	}

	public CJsonArrayRequest(int i, String url, JSONObject jobj,
                             Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
		// this.setRetryPolicy(new DefaultRetryPolicy(
		// AppConstants.REQUEST_SET_RETRY_POLICY_MILLI,
		// DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
		// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		this.setRetryPolicy(new DefaultRetryPolicy(
				AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
	}

	@Override
	public Priority getPriority() {
		return lowPriority;
	}

	public void setPriority(Priority priority) {
		lowPriority = priority;
	}

}
