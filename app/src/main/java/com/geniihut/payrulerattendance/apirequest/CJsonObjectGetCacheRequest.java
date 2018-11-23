package com.geniihut.payrulerattendance.apirequest;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.geniihut.payrulerattendance.helpers.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CJsonObjectGetCacheRequest extends JsonObjectRequest {
	private Priority lowPriority = Priority.IMMEDIATE;

	public CJsonObjectGetCacheRequest(String url, JSONObject jobj,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(url, jobj, listener, errorListener);
		 this.setShouldCache(true);
		this.setRetryPolicy(new DefaultRetryPolicy(
				AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
	}

	public CJsonObjectGetCacheRequest(int i, String url, JSONObject jobj,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(i, url, jobj, listener, errorListener);
		 this.setShouldCache(true);
		// this.setRetryPolicy(new DefaultRetryPolicy(
		// AppConstants.REQUEST_SET_RETRY_POLICY_MILLI,
		// DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
		// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		this.setRetryPolicy(new DefaultRetryPolicy(
				AppConstants.REQUEST_SET_RETRY_POLICY_MILLI, 1, 1.0f));
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		String jsonString = new String(response.data);
		Response<JSONObject> responseCache = null;
		try {
			responseCache = Response.success(new JSONObject(jsonString),
					CJsonObjectGetCacheRequest.parseIgnoreCacheHeaders(response));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseCache;
	}

	@Override
	public Priority getPriority() {
		return lowPriority;
	}

	public void setPriority(Priority priority) {
		lowPriority = priority;
	}

	public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
		long now = System.currentTimeMillis();

		Map<String, String> headers = response.headers;
		long serverDate = 0;
		String serverEtag = null;
		String headerValue;

		headerValue = headers.get("Date");
		if (headerValue != null) {
			serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
		}

		serverEtag = headers.get("ETag");

		final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache
															// will be hit, but
															// also refreshed on
															// background
		final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache
														// entry expires
														// completely
		final long softExpire = now + cacheHitButRefreshed;
		final long ttl = now + cacheExpired;

		Cache.Entry entry = new Cache.Entry();
		entry.data = response.data;
		entry.etag = serverEtag;
		entry.softTtl = softExpire;
		entry.ttl = ttl;
		entry.serverDate = serverDate;
		entry.responseHeaders = headers;

		return entry;
	}

}
