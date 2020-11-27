package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.interfaces.DatabaseUpdatedCallback;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by chagithazani on 11/8/17.
 */

/*
  Task is responsible to get Google Elevation data and save it locally for each PracticeData Object
 */
public class PracticeDataSaveToBackendTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private boolean result = true;
    private SavePracticeDataToBackendCallback callback = null;

    public PracticeDataSaveToBackendTask(Context context, SavePracticeDataToBackendCallback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        SQLiteDatabaseHandler databaseHandler = new SQLiteDatabaseHandler(this.context);
        if(databaseHandler == null) return false;
        List<PracticeData> practicesToSave = databaseHandler.getNotSavedToDBPractices();

        if(practicesToSave == null){
            if (callback != null) callback.practiceDataSaved();
            return true;
        }

        for(final PracticeData practiceData : practicesToSave){
            Log.d(PracticeDataSaveToBackendTask.class.getName(), "$$$$$$$$$$$$$$$ PracticeDataSaveToBackendTask in For loop $$$$$$$$$$$$$$$$$$$$$$ size: " + practiceData.getLocations().size() + " timestamp " + practiceData.getTimeStamp());

            final User user = User.createUserFromSharedPreferences(context, true);
            if(user == null){
                result = false;
            } else {
                NetworkCenterHelper.savePracticeDataToBackend(practiceData, context, user, practiceData.isPartOfPlan(), new PostResponseCallback() {
                    @Override
                    public void requestStarted() {

                    }

                    @Override
                    public void requestCompleted(String response) {
                        Log.d(PracticeDataSaveToBackendTask.class.getName(), "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ PracticeDataSaveToBackendTask requestCompleted $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  response: " + response);

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(jsonObject != null) {
                            final PracticeData pData = PracticeData.fromJson(context, jsonObject);
                            Log.d(PracticeDataSaveToBackendTask.class.getName(), "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ PracticeDataSaveToBackendTask PracticeData created $$$$$$$$$$$$ size: " + pData.getLocations().size() + " timestamp " + pData.getTimeStamp());

                            //PracticeDataTempStorageHelper.deleteNotSavedPracticeData(context, pData, user.getEmail());
                            //PracticeDataTempStorageHelper.saveNoElevationPracticeData(context, pData);

                            final SQLiteDatabaseHandler databaseHandler = new SQLiteDatabaseHandler(context);
                            if(databaseHandler == null) return;
                            databaseHandler.updatePractice(pData, new DatabaseUpdatedCallback() {
                                @Override
                                public void updateFailedWithException(Exception e) {
                                    e.printStackTrace();
                                    if (databaseHandler != null) databaseHandler.close();
                                }

                                @Override
                                public void updateEndedWithNoException() {
                                    if (callback != null) callback.practiceDataSaved();
                                    if (databaseHandler != null) databaseHandler.close();
                                }
                            });



                        }
                    }

                    @Override
                    public void requestEndedWithError(VolleyError error) {
                        if(error != null) error.printStackTrace();
                        result = false;
                        if (callback != null) callback.practiceDataSaveFailed();
                    }
                });
            }
        }

        return result;


    }


    public interface SavePracticeDataToBackendCallback{
        public void practiceDataSaved();
        public void practiceDataSaveFailed();
    }

}
