package com.forst.miri.runwithme.interfaces;

/**
 * Created by chagithazani on 3/6/18.
 */

public interface DatabaseUpdatedCallback {
    public void updateFailedWithException(Exception e);
    public void updateEndedWithNoException();
}
