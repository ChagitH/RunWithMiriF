package com.forst.miri.runwithme.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.forst.miri.runwithme.miscellaneous.FetchPracticesFromCloud;
import com.forst.miri.runwithme.objects.User;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DownloadPracticeDataFromServerJobService extends JobService {

    public static final String TASK_SCHEDULED_DOWNLOAD_PRACTICE_DATA_SERVER = "download_practice_data_task_scheduled_job";
    public static final int TASK_SCHEDULED_DOWNLOAD_PRACTICE_DATA_SERVER_id = 89967534;

    public DownloadPracticeDataFromServerJobService() {
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        User user = User.createUserFromSharedPreferences(this, true);
        if(user != null) {
            new FetchPracticesFromCloud(this, user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}



