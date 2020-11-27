package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.interfaces.DatabaseUpdatedCallback;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.interfaces.PracticeDataCreatedCallback;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.User;

import java.util.List;

/**
 * Created by chagithazani on 11/8/17.
 */

/*
  Task is responsible to get Google Elevation data and save it locally for each PracticeData Object
 */
public class PracticeDataFetchHeightsTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private boolean result = true;
    private SavePracticeDataToBackendCallback callback = null;

    public PracticeDataFetchHeightsTask(Context context, SavePracticeDataToBackendCallback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        SQLiteDatabaseHandler databaseHandler = new SQLiteDatabaseHandler(this.context);
        //List<PracticeData>  practicesWithNoElevation = databaseHandler.getNoElevationPractices();
//        List<PracticeData>  practicesWithNoElevation = PracticeDataTempStorageHelper.getNoElevationPracticeDataList(context);
        List<PracticeData>  practicesWithNoElevation = databaseHandler.getNoElevationPractices();

        Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% num of practices without elevation %%%%%%%%%%%%%%%%%%%%%%%%%% " + (practicesWithNoElevation != null ? practicesWithNoElevation.size() : "NULL"));
        for(PracticeData practiceData : practicesWithNoElevation){
            Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PracticeDataFetchHeightsTask  %%%%%%%%%%%%%%%%%%%%%%%%%% " + practiceData.getDate().toString());
            //get elevation data
            practiceData.getGoogleElevationData(context, new PracticeDataCreatedCallback() {
                @Override
                public void PracticeDataCreated(final PracticeData pData) {
                    if(pData != null) {
                        Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Elevation Saved To PracticeData %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + pData.getDate().toString());

                        /*
                        If PracticeData was already saved To Server, it will now be updated.
                        If PracticeData was NOT saved to Server, it will NOT be saved now to server.
                         */
                        if(pData.getServerId() != null){
                            Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PracticeData will be updated in server %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + pData.getDate().toString());

                            User user = User.createUserFromSharedPreferences(context, true);
                            if(user == null){
                                result = false;
                            } else {
                                NetworkCenterHelper.updatePracticeDataToBackend(pData, context, user, pData.isPartOfPlan(), new PostResponseCallback() {
                                    @Override
                                    public void requestStarted() {
                                    }

                                    @Override
                                    public void requestCompleted(String response) {
                                        Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PracticeData WAS updated in server %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + pData.getDate().toString());

                                        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                                        sqLiteDatabaseHandler.updatePractice(pData, new DatabaseUpdatedCallback() {
                                            @Override
                                            public void updateFailedWithException(Exception e) {
                                                Log.d(PracticeData.class.getName(), " PracticeData NOt saved to full practices table ");
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void updateEndedWithNoException() {
                                                Log.d(PracticeData.class.getName(), " PracticeData YES saved to full practices table ");

                                                //PracticeDataTempStorageHelper.deleteNoElevationPracticeData(context, pData);
                                            }
                                        });

//                                        sqLiteDatabaseHandler.addPractice(pData, new DatabaseUpdatedCallback() {
//                                            @Override
//                                            public void updateFailedWithException(Exception e) {
//                                                Log.d(PracticeData.class.getName(), " PracticeData NOt saved to full practices table ");
//                                                e.printStackTrace();
//                                            }
//
//                                            @Override
//                                            public void updateEndedWithNoException() {
//                                                Log.d(PracticeData.class.getName(), " PracticeData YES saved to full practices table ");
//
//                                                PracticeDataTempStorageHelper.deleteNoElevationPracticeData(context, pData);
//                                            }
//                                        });

                                        if (callback != null) callback.practiceDataSaved();
                                    }

                                    @Override
                                    public void requestEndedWithError(VolleyError error) {
                                        if(error != null)error.printStackTrace();
                                        Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PracticeData WAS NOT updated in server ERROR %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + pData.getDate().toString());
                                        result = false;
                                        if (callback != null) callback.practiceDataSaveFailed();
                                    }
                                });
                            }
                        }

                    } else {
                        Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Elevation WAS Probably NOT Saved To PracticeData  pd is null %%%%%%%%%%%");
                        result = false;
                        if(callback != null) callback.practiceDataSaveFailed();
                    }
                }

                @Override
                public void requestEndedWithError(Exception e) {
                    if (e != null) {
                        Log.d(PracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Elevation requestEndedWithError %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + e.getMessage());
                        e.printStackTrace();
                    }
                    result = false;
                }
            });
        }
        return result;


    }


    public interface SavePracticeDataToBackendCallback{
        public void practiceDataSaved();
        public void practiceDataSaveFailed();
    }

}
