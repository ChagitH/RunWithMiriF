package com.forst.miri.runwithme.services;

import android.os.AsyncTask;

import com.forst.miri.runwithme.miscellaneous.PracticeDataSaveToBackendTask;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by chagithazani on 11/12/17.
 * Service that is scheduled to save practices to backend.
 * Uses PracticeDataSaveToBackendTask.java to implement the job.
 */

public class PracticeSaveDataService extends GcmTaskService {

    private static final String TASK_TAG_API_19_SCHEDULED_DOWNLOAD_PRACTICE_SERVER = "download_practice_task_scheduled_api_19";
    private static final String TASK_TAG_API_19_SCHDULED_GET_ELEVATION_DATA = "get_and_save_elevation_data_task_scheduled_api_19";


    PracticeDataSaveToBackendTask practiceSaveTask;

    @Override
    public int onRunTask(TaskParams taskParams) {
            practiceSaveTask = new PracticeDataSaveToBackendTask(this, new PracticeDataSaveToBackendTask.SavePracticeDataToBackendCallback() {
                @Override
                public void practiceDataSaved() {

//                        new DownloadUserPlanDataTask(PracticeSaveDataService.this,  new DownloadUserPlanDataTask.GetUserCallback() {
//                            @Override
//                            public void userPlanDataUpdated(User user) {
//
//                                GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(PracticeSaveDataService.this);
//                                OneoffTask task = new OneoffTask.Builder()
//                                        .setService(DownloadPracticeService.class)
//                                        .setExecutionWindow(0, 21600)
//                                        .setTag(TASK_TAG_API_19_SCHEDULED_DOWNLOAD_PRACTICE_SERVER)
//                                        .setPersisted(true)
//                                        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
//                                        .build();
//
//                                mGcmNetworkManager.schedule(task);
//                            }
//
//                            @Override
//                            public void userPlanDataFetchFailed(String error) {
//                                Log.d(getClass().getName(), "**************** ******************* userDataFetchFailed error is " + error + "******************* ******************* *******************");
//                            }
//                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    new Runnable() {
                        @Override
                        public void run() {
                            GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(PracticeSaveDataService.this);
                            OneoffTask task1 = new OneoffTask.Builder()
                                    .setService(PracticeGetElevationDataService.class)
                                    .setExecutionWindow(0, 21600) //earliest time to start 0 minutes after, maximum 6 hours after scheduled
                                    .setTag(TASK_TAG_API_19_SCHDULED_GET_ELEVATION_DATA)
                                    .setPersisted(true)
                                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                                    .build();

                            mGcmNetworkManager.cancelTask(TASK_TAG_API_19_SCHDULED_GET_ELEVATION_DATA, PracticeGetElevationDataService.class);//to avoid double tasking
                            mGcmNetworkManager.schedule(task1);
                        }
                    }.run();

                    }

                @Override
                public void practiceDataSaveFailed() {

                }
            });



//            {
//                @Override
//                public void practiceDataSaved() {
//                    if(ConnectedUser.getInstance() != null) {
//                        new DownloadUserPlanDataTask(PracticeSaveDataService.this, ConnectedUser.getInstance(), new DownloadUserPlanDataTask.GetUserCallback() {
//                            @Override
//                            public void userPlanDataUpdated(User user) {
//                                GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(PracticeSaveDataService.this);
//
//                                OneoffTask task = new OneoffTask.Builder()
//                                        .setService(DownloadPracticeService.class)
//                                        .setExecutionWindow(0, 1)
//                                        .setTag(TASK_TAG_API_19_SCHEDULED_DOWNLOAD_PRACTICE_SERVER)
//                                        .setPersisted(true)
//                                        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
//                                        .build();
//
//                                mGcmNetworkManager.schedule(task);
//                            }
//
//                            @Override
//                            public void userPlanDataFetchFailed(String error) {
//                                Log.d("PracticeGetElevationDataService", "**************** ******************* userDataFetchFailed error is " + error + "******************* ******************* *******************");
//                            }
//                        }).execute();
//                    }
//                }
//
//                @Override
//                public void practiceDataSaveFailed() {
//                    Log.d("PracticeGetElevationDataService", "**************** ******************* practiceDataSaveFailed ******************* ******************* *******************");
//                }
//            }) ;
            practiceSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return GcmNetworkManager.RESULT_RESCHEDULE;

    }
}
