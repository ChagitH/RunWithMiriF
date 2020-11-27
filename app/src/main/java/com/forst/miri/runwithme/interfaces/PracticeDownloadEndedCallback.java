package com.forst.miri.runwithme.interfaces;

import com.forst.miri.runwithme.objects.Practice;

import java.io.Serializable;

/**
 * Created by chagithazani on 10/8/17.
 */

public interface PracticeDownloadEndedCallback extends Serializable {
    public void practiceDownloadEnded(Practice practice);
    public void practiceDownloadFailed();
}
