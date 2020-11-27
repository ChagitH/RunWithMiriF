package com.forst.miri.runwithme.interfaces;

import com.forst.miri.runwithme.objects.PracticeData;

/**
 * Created by chagithazani on 9/7/17.
 */

public interface PracticeDataCreatedCallback {
    public void PracticeDataCreated(PracticeData pData);
    public void requestEndedWithError(Exception e);
}
