package com.forst.miri.runwithme.services;


import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.forst.miri.runwithme.miscellaneous.PracticeDataSaveToBackendTask;

import java.util.List;

/**
 * Created by chagithazani on 11/8/17.
 * Service that is scheduled to save practices to backend.
 * Uses PracticeDataSaveToBackendTask.java to implement the job.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PracticeSaveDataJobService extends JobService {

    private static final int PRACTICE_DOWNLOAD_JOB_SERVICE_ID = 9102;
    private static final int PRACTICE_ELEVATION_DATA_JOB_SERVICE_ID = 9105;

    //public PracticeGetElevationDataJobService(Context co)
    PracticeDataSaveToBackendTask practiceSaveTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        practiceSaveTask = new PracticeDataSaveToBackendTask(this, new PracticeDataSaveToBackendTask.SavePracticeDataToBackendCallback() {
            @Override
            public void practiceDataSaved() {

//                new DownloadUserPlanDataTask(PracticeSaveDataJobService.this, new DownloadUserPlanDataTask.GetUserCallback() {
//                    @Override
//                    public void userPlanDataUpdated(User user) {
//                        if(user != null && user.IsRegisteredToRunWithMiri()) {
//                            JobScheduler jobScheduler = (JobScheduler) PracticeSaveDataJobService.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
//                            // if in plan - schedule also practice download
//                            JobInfo job = new JobInfo.Builder(PRACTICE_DOWNLOAD_JOB_SERVICE_ID, new ComponentName(PracticeSaveDataJobService.this, PracticeDownloadJobService.class))
//                                    .setMinimumLatency(0) //Milliseconds before which this job will not be considered for execution. 4 minutes
//                                    .setOverrideDeadline(21600000) // 6 hours
//                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                                    .setPersisted(true)
//                                    .build();
//                            int r = jobScheduler.schedule(job);
//                            //Log.d(getClass().getName(), "**************** ******************* result of scheduling Download is " + r + "******************* ******************* *******************");
//
//                            jobFinished(jobParameters, false);
//                        }
//                    }
//
//                    @Override
//                    public void userPlanDataFetchFailed(String error) {
//                        Log.d(getClass().getName(), "**************** ******************* userDataFetchFailed error is " + error+ "******************* ******************* *******************");
//                        jobFinished(jobParameters, true);
//                    }
//                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                JobScheduler jobScheduler = (JobScheduler) PracticeSaveDataJobService.this.getSystemService(Context.JOB_SCHEDULER_SERVICE);

                if(! isJobScheduled(jobScheduler, PRACTICE_ELEVATION_DATA_JOB_SERVICE_ID)) {
                    JobInfo job1 = new JobInfo.Builder(PRACTICE_ELEVATION_DATA_JOB_SERVICE_ID, new ComponentName(PracticeSaveDataJobService.this, PracticeGetElevationDataJobService.class))
                            .setMinimumLatency(0) //Milliseconds before which this job will not be considered for execution - 0 minutes
                            .setOverrideDeadline(21600000) // max 6 hours
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setPersisted(true)
                            .build();
                    int r1 = jobScheduler.schedule(job1);
                    Log.d(getClass().getName(), "**************** ******************* result of scheduling GET ELEVATION is " + r1 + "******************* ******************* *******************");
                } else {

                    Log.d(getClass().getName(), "**************** *******************  PRACTICE ELEVATION JOB ALREADY SCHEDULED  ******************* ******************* *******************");
                }

            }

            @Override
            public void practiceDataSaveFailed() {

            }
        });



        practiceSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return true;
    }

    private boolean isJobScheduled(JobScheduler jobScheduler, int jobId) {
        List<JobInfo> allJobs = jobScheduler.getAllPendingJobs();
        for (JobInfo ji : allJobs){
            if(ji.getId() == jobId){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}







