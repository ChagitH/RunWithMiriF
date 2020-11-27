package com.forst.miri.runwithme.interfaces;

/**
 * Created by chagithazani on 2/8/18.
 */

public abstract class GetElevationResponseCallback implements GetResponseCallback {
    protected int start;
    protected int end;
    public GetElevationResponseCallback(int start, int end){
        this.start = start;
        this.end = end;
    }
}
