package com.geniihut.payrulerattendance.apirequest;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface RequestResponseListener {
	public void requestStarted();
	public void requestCompleted(JSONObject response);
	public void requestEndedWithError(VolleyError error);
}
