package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chagithazani on 10/18/17.
 */

public class FetchPracticesFromCloud extends AsyncTask {
    private User mUser;
    private Context mContext;
    private int page = 1;
    private GetResponseCallback responseCallback = null;

    public FetchPracticesFromCloud(Context context, User user){
        mUser = user;
        mContext = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        responseCallback = new GetResponseCallback() {
            @Override
            public void requestStarted() {
                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestStarted !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! requestStarted ");
            }

            @Override
            public void requestCompleted(String response) {
//          public void requestCompleted(String response) {
                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestCompleted !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! response = \n" + response);
                JSONArray practices = null;

                SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(mContext);
                if(page == 1) {
//                    PracticeDataTempStorageHelper.eraseAllNoElevationPracticeData(mContext);
                    //db.clearAllNoElevationPractices();
                    db.clearAllFullPractices();
                }


//                    Gson gson = new Gson();
//                    Type listType = new TypeToken<List<PracticeData>>(){}.getType();
//
//                    List<PracticeData> practices =  gson.fromJson(response, listType);
//
//                    for(PracticeData pData : practices){
//                        //PracticeData pData = PracticeData.fromJson(dataObject);
//                        db.addPractice(pData);
//                    }

                try {
                    JSONObject responseObject  = new JSONObject(response);
                    int totalNumOfPages = responseObject.getInt("last_page");
                    practices = responseObject.getJSONArray("data");
//                    practices = new JSONArray(response);
                    for (int x = 0; x < practices.length(); x++) {
                        JSONObject pDataObj = practices.getJSONObject(x);
                        PracticeData pData = PracticeData.fromJson(mContext, pDataObj);
//                        if (pData.getPositiveSlope() == MinimalLocation.NO_ELEVATION) {
//                            //PracticeDataTempStorageHelper.saveNoElevationPracticeData(mContext, pData);
//                            //db.addNoElevationPractice(pData, null);
//                        } else {
                            db.addPractice(pData, null);
//                        }

                    }

                    page += 1;
                    if ( page <= totalNumOfPages)
                    NetworkCenterHelper.getAllUserPractices(mContext, mUser, responseCallback, page);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@ all practices saved  local db !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@!");
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestEndedWithError !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! requestEndedWithError = " + error);
                if (error != null) error.printStackTrace();
            }
        };

        NetworkCenterHelper.getAllUserPractices(mContext, mUser, responseCallback, page);
//                new GetResponseCallback() {
//            @Override
//            public void requestStarted() {
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestStarted !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! requestStarted ");
//            }
//
//            @Override
//            public void requestCompleted(String response) {
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestCompleted !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! response = \n" + response);
//                JSONArray practices = null;
////                try {
//                    //practices = new JSONArray(response);
//                    SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(mContext);
//                    PracticeDataTempStorageHelper.eraseAllNoElevationPracticeData(mContext);
//                    //db.clearAllNoElevationPractices();
//                    db.clearAllFullPractices();
////                    Gson gson = new Gson();
////                    Type listType = new TypeToken<List<PracticeData>>(){}.getType();
////
////                    List<PracticeData> practices =  gson.fromJson(response, listType);
////
////                    for(PracticeData pData : practices){
////                        //PracticeData pData = PracticeData.fromJson(dataObject);
////                        db.addPractice(pData);
////                    }
//
//                try {
//                    practices = new JSONArray(response);
//                    for (int x = 0 ; x < practices.length() ; x++){
//                        JSONObject pDataObj = practices.getJSONObject(x);
//                        PracticeData pData = PracticeData.fromJson(mContext, pDataObj);
//                        if(pData.getPositiveSlope() == MinimalLocation.NO_ELEVATION){
//                            PracticeDataTempStorageHelper.saveNoElevationPracticeData(mContext, pData);
//                            //db.addNoElevationPractice(pData, null);
//                        } else {
//                            db.addFullPractice(pData, null);
//                        }
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@ all practices saved  local db !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@!");
//            }
//
//            @Override
//            public void requestEndedWithError(VolleyError error) {
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestEndedWithError !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! requestEndedWithError = " + error);
//                if(error != null) error.printStackTrace();
//            }
//        }
//        );

//        NetworkCenterHelper.getAllUserPractices(mContext, mUser, new GetResponseCallback() {
//            @Override
//            public void requestStarted() {
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestStarted !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! requestStarted ");
//            }
//
//            @Override
//            public void requestCompleted(String response) {
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestCompleted !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! response = \n" + response);
//                JSONArray practices = null;
////                try {
//                //practices = new JSONArray(response);
//                SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(mContext);
//                PracticeDataTempStorageHelper.eraseAllNoElevationPracticeData(mContext);
//                //db.clearAllNoElevationPractices();
//                db.clearAllFullPractices();
////                    Gson gson = new Gson();
////                    Type listType = new TypeToken<List<PracticeData>>(){}.getType();
////
////                    List<PracticeData> practices =  gson.fromJson(response, listType);
////
////                    for(PracticeData pData : practices){
////                        //PracticeData pData = PracticeData.fromJson(dataObject);
////                        db.addPractice(pData);
////                    }
//
//                try {
//                    practices = new JSONArray(response);
//                    for (int x = 0 ; x < practices.length() ; x++){
//                        JSONObject pDataObj = practices.getJSONObject(x);
//                        PracticeData pData = PracticeData.fromJson(mContext, pDataObj);
//                        if(pData.getPositiveSlope() == MinimalLocation.NO_ELEVATION){
//                            PracticeDataTempStorageHelper.saveNoElevationPracticeData(mContext, pData);
//                            //db.addNoElevationPractice(pData, null);
//                        } else {
//                            db.addFullPractice(pData, null);
//                        }
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@ all practices saved  local db !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@!");
//            }
//
//            @Override
//            public void requestEndedWithError(VolleyError error) {
//                Log.i("@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@requestEndedWithError !@@@@@@@@@@@@@@@@@!@@@@@@@@@@@@@@@@@! requestEndedWithError = " + error);
//                if(error != null) error.printStackTrace();
//            }
//        });


        return null;
    }
}
