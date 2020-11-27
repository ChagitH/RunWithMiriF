package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.forst.miri.runwithme.interfaces.PracticeDataCreatedCallback;
import com.forst.miri.runwithme.objects.PracticeData;

/**
 * Created by chagithazani on 11/8/17.
 */

/*
  Task is responsible to get Google Elevation data and save it locally for each PracticeData Object
 */
public class SpecificPracticeDataFetchHeightsTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private boolean result = true;
    private SavePracticeDataToBackendCallback callback = null;
    private PracticeData practiceData = null;

    public SpecificPracticeDataFetchHeightsTask(Context context, PracticeData pData, SavePracticeDataToBackendCallback callback){
        this.context = context;
        this.callback = callback;
        this.practiceData = pData;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        practiceData.getGoogleElevationData(context, new PracticeDataCreatedCallback() {
                @Override
                public void PracticeDataCreated(final PracticeData pData) {
//                    if(pData != null) {
//                        Log.d(SpecificPracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Elevation Saved To specific PracticeData %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + pData.getDate().toString());
//
//                        /*
//                        If PracticeData was already saved To Server, it will now be updated.
//                        If PracticeData was NOT saved to Server, it will NOT be saved now to server.
//                         */
//                        if(pData.isSavedToBackend()){
//                            Log.d(SpecificPracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  specific PracticeData will be updated in server %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + pData.getDate().toString());
//
//                            User user = User.createUserFromSharedPreferences(context);
//                            if(user == null){
//                                result = false;
//                            } else {
//                                NetworkCenterHelper.updatePracticeDataToBackend(pData, context, user, pData.isPartOfPlan(), new PostResponseCallback() {
//                                    @Override
//                                    public void requestStarted() {
//                                    }
//
//                                    @Override
//                                    public void requestCompleted(String response) {
//                                        Log.d(SpecificPracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% specific PracticeData WAS updated in server %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + pData.getDate().toString());
//
//                                        if (callback != null) callback.practiceDataSaved();
//                                    }
//
//                                    @Override
//                                    public void requestEndedWithError(VolleyError error) {
//                                        error.printStackTrace();
//                                        Log.d(SpecificPracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PracticeData WAS NOT updated in server ERROR %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + pData.getDate().toString());
//                                        result = false;
//                                        if (callback != null) callback.practiceDataSaveFailed();
//                                    }
//                                });
//                            }
//                        }
//                    } else {
//                        Log.d(SpecificPracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Elevation WAS Probably NOT Saved To PracticeData  pd is null %%%%%%%%%%%");
//                        result = false;
//                        if(callback != null) callback.practiceDataSaveFailed();
//                    }
                }

                @Override
                public void requestEndedWithError(Exception e) {
                    if (e != null) {
                        Log.d(SpecificPracticeDataFetchHeightsTask.class.getName(), "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Elevation requestEndedWithError %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + e.getMessage());
                        e.printStackTrace();
                    }
                    result = false;
                }
            });

        return result;


    }


    public interface SavePracticeDataToBackendCallback{
        public void practiceDataSaved();
        public void practiceDataSaveFailed();
    }

}
