package com.forst.miri.runwithme.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by chagithazani on 9/11/17.
 */

public interface GetResponseCallback {
    public void requestStarted();
    public void requestCompleted(String response);
    public void requestEndedWithError(VolleyError error);
}

