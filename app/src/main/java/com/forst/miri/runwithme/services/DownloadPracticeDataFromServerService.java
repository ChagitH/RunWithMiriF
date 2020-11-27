package com.forst.miri.runwithme.services;

import android.os.AsyncTask;

import com.forst.miri.runwithme.miscellaneous.FetchPracticesFromCloud;
import com.forst.miri.runwithme.objects.User;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

public class DownloadPracticeDataFromServerService extends GcmTaskService {
    public DownloadPracticeDataFromServerService() {
    }

    public static final String TASK_TAG_API_19_SCHEDULED_DOWNLOAD_PRACTICE_DATA_SERVER = "download_practice_data_task_scheduled_api_19";


    @Override
    public int onRunTask(TaskParams taskParams) {

        User user = User.createUserFromSharedPreferences(this, true);
        if(user != null) {
            new FetchPracticesFromCloud(this, user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
