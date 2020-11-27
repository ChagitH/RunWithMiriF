package com.forst.miri.runwithme.services;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.forst.miri.runwithme.miscellaneous.PracticeDataFetchHeightsTask;

/**
 * Created by chagithazani on 11/8/17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PracticeGetElevationDataJobService extends JobService {

    private static final int PRACTICE_DOWNLOAD_JOB_SERVICE_ID = 9102;

    //public PracticeGetElevationDataJobService(Context co)
    PracticeDataFetchHeightsTask practiceSaveTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        practiceSaveTask = new PracticeDataFetchHeightsTask(this, new PracticeDataFetchHeightsTask.SavePracticeDataToBackendCallback() {
            @Override
            public void practiceDataSaved() {
                Log.d(getClass().getName(), "**************** ******************* practiceDataSaved() - elevation was gotten ******************* ******************* *******************");
//                //if(ConnectedUser.getInstance() != null) {
//                new DownloadUserPlanDataTask(PracticeGetElevationDataJobService.this, new DownloadUserPlanDataTask.GetUserCallback() {
//                    @Override
//                    public void userPlanDataUpdated(User user) {
//                        if(user != null && user.IsRegisteredToRunWithMiri()) {
//                            JobScheduler jobScheduler = (JobScheduler) PracticeGetElevationDataJobService.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
//                            // if in plan - schedule also practice download
//                            JobInfo job = new JobInfo.Builder(PRACTICE_DOWNLOAD_JOB_SERVICE_ID, new ComponentName(PracticeGetElevationDataJobService.this, PracticeDownloadJobService.class))
//                                    .setMinimumLatency(0)
//                                    .setOverrideDeadline(0)
//                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                                    .setPersisted(true)
//                                    .build();
//                            int r = jobScheduler.schedule(job);
//                            Log.d(getClass().getName(), "**************** ******************* result of scheduling Download is " + r + "******************* ******************* *******************");
//                            jobFinished(jobParameters, false);
//                        }
//                    }
//
//                    @Override
//                    public void userPlanDataFetchFailed(String error) {
//                        Log.d(getClass().getName(), "**************** ******************* userDataFetchFailed error is " + error+ "******************* ******************* *******************");
//                        jobFinished(jobParameters, true);
//                    }
//                }).execute();
                //}
            }

            @Override
            public void practiceDataSaveFailed() {
                Log.d(getClass().getName(), "**************** ******************* practiceDataSaveFailed  NO ERROR TO PRESENT ******************* ******************* *******************");
                jobFinished(jobParameters, true);
            }
        });

        practiceSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}







