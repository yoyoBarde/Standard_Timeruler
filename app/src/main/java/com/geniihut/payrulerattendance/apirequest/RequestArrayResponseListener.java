package com.geniihut.payrulerattendance.apirequest;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface RequestArrayResponseListener {
	public void requestStarted();
	public void requestCompleted(JSONArray response);
	public void requestEndedWithError(VolleyError error);
}
