package com.forst.miri.runwithme.services;

import android.os.AsyncTask;
import android.util.Log;

import com.forst.miri.runwithme.miscellaneous.PracticeDataFetchHeightsTask;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by chagithazani on 11/12/17.
 */

public class PracticeGetElevationDataService extends GcmTaskService {

    private static final String TASK_TAG_API_19_SCHEDULED_DOWNLOAD_PRACTICE_SERVER = "download_practice_task_scheduled_api_19";

    PracticeDataFetchHeightsTask practiceFetchHeightsTask;

    @Override
    public int onRunTask(TaskParams taskParams) {
        practiceFetchHeightsTask = new PracticeDataFetchHeightsTask(this, new PracticeDataFetchHeightsTask.SavePracticeDataToBackendCallback() {
                @Override
                public void practiceDataSaved() {
                    Log.d(getClass().getName(), "**************** ******************* practiceDataSaved() - elevation was gotten ******************* ******************* *******************");
                }

                @Override
                public void practiceDataSaveFailed() {
                    Log.d(getClass().getName(), "**************** ******************* practiceDataSaveFailed ******************* ******************* *******************");
                }
            }) ;
        practiceFetchHeightsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return GcmNetworkManager.RESULT_RESCHEDULE;

    }
}
