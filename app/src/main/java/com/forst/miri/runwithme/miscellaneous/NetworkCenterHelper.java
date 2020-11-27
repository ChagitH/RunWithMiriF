package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.User;
import com.forst.miri.runwithme.objects.UserProperties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chagithazani on 9/17/17.
 */

public class NetworkCenterHelper {

    public static void savePracticeDataToBackend(PracticeData practiceData, Context context, User connectedUser, boolean isRunningWithMiri, PostResponseCallback callback) {

        if(connectedUser == null) return;

        String url = new String(context.getString(R.string.server_base) + "/api/trainings");

        NetworkCenter.postInstance().requestPost(context, url, callback , getPracticeDataParams(practiceData, connectedUser, isRunningWithMiri), getAuthorizationHeader(connectedUser));
    }

    public static void updatePracticeDataToBackend(PracticeData practiceData, Context context, User connectedUser, boolean isRunningWithMiri, PostResponseCallback callback) {

        if(connectedUser == null) return;

        String url = new String(context.getString(R.string.server_base) + "/api/trainings/" + practiceData.getServerId());

        Map<String,String> params = getPracticeDataParams(practiceData, connectedUser, isRunningWithMiri);
        params.put("_method", "put");

        NetworkCenter.postInstance().requestPost(context, url, callback , params , getAuthorizationHeader(connectedUser));
        //NetworkCenter.putInstance().requestPut(context, url, callback , getPracticeDataParams(practiceData, connectedUser, isRunningWithMiri), getAuthorizationHeader(connectedUser));
    }

    public static Map<String,String> getPracticeDataParams(PracticeData practiceData, User connectedUser, boolean isRunningWithMiri){

        Map<String,String> params = new HashMap<String, String>();

        params.put(PracticeData.TIMESTAMP_KEY, String.valueOf(practiceData.getTimeStamp()));
        params.put(PracticeData.ALL_LOCATIONS_KEY,practiceData.getLocationsAsJson());
        params.put(PracticeData.ALL_RATES_KEY,practiceData.getAllRatesAsJson());
        params.put(PracticeData.RATES_PER_KM_KEY,practiceData.getRatesPerKMAsJson());
        params.put(PracticeData.DISTANCE_KEY,String.valueOf(practiceData.getDistanceInMeters()));
        params.put(PracticeData.DURATION_KEY,String.valueOf(practiceData.getDuration()));
        params.put(PracticeData.CALORIES_KEY, String.valueOf(practiceData.getKcl()));
        params.put(PracticeData.AVE_RATE_KEY,String.valueOf(practiceData.getAvgRate()));
        params.put(PracticeData.MAX_RATE_KEY,String.valueOf(practiceData.getBestRateForKmMiliies()));
        params.put(PracticeData.ELEVATION_KEY,String.valueOf(practiceData.getPositiveSlope()));
        params.put(PracticeData.TEMPERATURE_KEY,String.valueOf(practiceData.getTemperature()));
        params.put(PracticeData.SPEED_KEY,String.valueOf(practiceData.getSpeed()));
        if(isRunningWithMiri && connectedUser.getProgramId() > 0) {
            String id = String.valueOf(connectedUser.getUserProgramId());
            params.put(User.PRACTICE_PROGRAM_ID_KEY, id);
            params.put(PracticeData.PRACTICE_NUM_KEY,String.valueOf(practiceData.getNumOfLessonInPlan()));
        }

        return  params;
    }

    public static void resetPassword(Context context, String email, PostResponseCallback callback) {

        if(email == null){
            if(callback != null) callback.requestEndedWithError(new VolleyError(context.getString(R.string.email_is_empty_heb)));
            return;
        }

        String url = new String(context.getString(R.string.server_base) + "/api/password/email");

        Map<String,String> params = new HashMap<String, String>();
        params.put(User.EMAIL_KEY, email);

        NetworkCenter.postInstance().requestPost(context, url, callback , params, null);
    }


    public static void updateUserDetails(Context context, User user,  Map<String,String> params, PostResponseCallback callback) {

        String url = new String(context.getString(R.string.server_base) + "/api/users/update");

        NetworkCenter.postInstance().requestPost(context, url, callback , params, getAuthorizationHeader(user));
    }


    public static void getConnectedUser(Context context, User user, GetResponseCallback callback){
        String url = new String(context.getString(R.string.server_base) + "/api/users");

        NetworkCenter.getInstance().requestGet(context, url, null, getAuthorizationHeader(user), callback);
    }

    private static Map<String,String> getAuthorizationHeader(User connectedUser){
        Map<String,String> headers = new HashMap<String, String>();
//        headers.put("content-type","multipart/form-data");
//        headers.put("cache-control","no-cache");
        String credentials = connectedUser.getServerId() + ":" + connectedUser.getAuthorizationToken();
        //String credentials = "91"+":"+"eyJpdiI6IkNsTVFWU21MZWljeUVJVFJ0XC9EcHF3PT0iLCJ2YWx1ZSI6ImkxUmt6NTZNYWxyUzR5MFlXc2k1ZW5uVGtSZjJKUFg0bzlpSkhRbnQ1QjA9IiwibWFjIjoiODQ4ZGUwYWE0ODk4MGQyNTgzNTJlNzM1YWIwZGVhMDQxYjUzYjYzODdmZjQ2ODgyODJjY2NiZTEwOTc4ZjIwOSJ9";
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        headers.put("Accept", "application/json");
        return headers;

    }


    public static void getPhotoFromServer(Context context, User user, GetResponseCallback callback){
        if (user.getServerPhotoName() != null) {
            StringBuilder urlStr = new StringBuilder(context.getString(R.string.server_base)).append("/images/");
//            String photoName = "user" + user.getServerId() + ".png";
//            url.append(photoName);
            urlStr.append(user.getServerPhotoName());
            try {
                URL url = new URL(urlStr.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                user.setImage(myBitmap);
                if(callback != null) callback.requestCompleted(user.getFirstName());
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        } else {
            if(callback != null) callback.requestEndedWithError(new VolleyError("No Photo found by user"));
        }


   }

    public static void getAllUserPractices(Context context, User user, GetResponseCallback callback, int page){

        String url = new String(context.getString(R.string.server_base) + "/api/trainings" + (page > 0 ? "?page=" + page : ""));
        NetworkCenter.getInstance().requestGet(context, url, null, getAuthorizationHeader(user), callback);
    }

    public static void getAllUserPractices(Context context, User user, GetResponseCallback callback){
        getAllUserPractices(context, user, callback, 0);
    }

    public static void saveTrainingNotificationDays(Context context, User user, PostResponseCallback callback){
        if(user == null) return;
        UserProperties schedulingProps = user.getProperties();
        if(schedulingProps == null) return;

        JSONArray jsonArray = new JSONArray();
        ArrayList<String> trainingSchedule = schedulingProps.getTrainingSchedule();
        for(int i = 0 ; i < trainingSchedule.size() ; i++) {
            String time = trainingSchedule.get(i);
            if(! time.isEmpty() && !time.matches("-")) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(UserProperties.NOTIFICATION_SCHEDULE_WHEN_KEY, time);
                    jsonObject.put(UserProperties.NOTIFICATION_SCHEDULE_DAY_KEY, i); //i+1 removed 1.5.2018
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }

        String url = new String(context.getString(R.string.server_base) + "/api/notifications/create");
        Map<String,String> params = new HashMap<String, String>();
        params.put(UserProperties.NOTIFICATION_SCHEDULE_TYPE_KEY, UserProperties.TYPE_TRAINING);
        params.put(UserProperties.NOTIFICATION_SCHEDULE_ARRAY_KEY, jsonArray.toString());
        NetworkCenter.postInstance().requestPost(context, url, callback , params, getAuthorizationHeader(user));
    }

    public static void saveNotificationRemindersSettings(Context context, User user, PostResponseCallback callback){

        if(user == null) return;
        UserProperties props = user.getProperties();
        if(props == null) return;


        Map<String,String> params = new HashMap<String, String>();

        //Trainings
        ArrayList<Integer> reminders = props.getTrainingRemindersBefore();
        if(reminders != null) {
            if (reminders.size() > 0 && reminders.get(0) != null) {
                params.put(UserProperties.NOTIFICATION_BEFORE_TRAINING_KEY1, reminders.get(0).toString());
            }
            if (reminders.size() > 1 && reminders.get(1) != null) {
                params.put(UserProperties.NOTIFICATION_BEFORE_TRAINING_KEY2, reminders.get(1).toString());
            }
        }

        //exercise
        reminders = props.getExerciseRemindersBefore();
        if(reminders != null){
            if(reminders.size() > 0 && reminders.get(0) != null){
                params.put(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY1, reminders.get(0).toString());
            }
            if(reminders.size() > 1 && reminders.get(1) != null){
                params.put(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY2, reminders.get(1).toString());
            }
        }

        //water
        params.put(UserProperties.NOTIFICATION_WATER_AT_ALL_KEY, String.valueOf(props.isWaterNotificationOnIntVal()));
        if(props.isWaterNotificationOn()) {
            params.put(UserProperties.NOTIFICATION_WATER_FROM_KEY, props.getWaterNotificationFrom());
            params.put(UserProperties.NOTIFICATION_WATER_TO_KEY, props.getWaterNotificationUntil());
            params.put(UserProperties.NOTIFICATION_WATER_INTERVAL_KEY, String.valueOf(props.getWaterNotificationInterval()));
        }

        Log.d(NetworkCenterHelper.class.getName(), "Save notification settings ------------- params :");
        Log.d(NetworkCenterHelper.class.getName(), params.toString());

        String url = new String(context.getString(R.string.server_base) + "/api/settings");


        NetworkCenter.postInstance().requestPost(context, url, callback , params, getAuthorizationHeader(user));
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void saveExerciseNotificationDays(Context context, User user, PostResponseCallback callback){
        if(user == null) return;
        UserProperties schedulingProps = user.getProperties();
        if(schedulingProps == null) return;

        JSONArray jsonArray = new JSONArray();
        ArrayList<String> exerciseSchedule = schedulingProps.getExerciseSchedule();
        for(int i = 0 ; i < exerciseSchedule.size() ; i++) {
            String time = exerciseSchedule.get(i);
            if(! time.isEmpty() && !time.matches("-")) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(UserProperties.NOTIFICATION_SCHEDULE_WHEN_KEY, time);
                    jsonObject.put(UserProperties.NOTIFICATION_SCHEDULE_DAY_KEY, i+1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }

        String url = new String(context.getString(R.string.server_base) + "/api/notifications/create");
        Map<String,String> params = new HashMap<String, String>();
        params.put(UserProperties.NOTIFICATION_SCHEDULE_TYPE_KEY, UserProperties.TYPE_EXERCISE);
        params.put(UserProperties.NOTIFICATION_SCHEDULE_ARRAY_KEY, jsonArray.toString());
        NetworkCenter.postInstance().requestPost(context, url, callback , params, getAuthorizationHeader(user));
    }

//    public static void saveExerciseNotificationReminders(Context context, User user, PostResponseCallback callback){
//
//        if(user == null) return;
//        UserProperties schedulingProps = user.getProperties();
//        if(schedulingProps == null) return;
//        Map<String,String> params = new HashMap<String, String>();
//        ArrayList<Integer> reminders = schedulingProps.getExerciseRemindersBefore();
//        if(reminders == null) return;
//        if(reminders.size() > 0 && reminders.get(0) != null){
//            params.put(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY1, reminders.get(0).toString());
//        }
//        if(reminders.size() > 1 && reminders.get(1) != null){
//            params.put(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY2, reminders.get(1).toString());
//        }
//
//
//        String url = new String(context.getString(R.string.server_base) + "/api/settings");
//
//        NetworkCenter.postInstance().requestPost(context, url, callback , params, getAuthorizationHeader(user));
//    }
//
//    public static void saveWaterNotificationReminders(Context context, User user, PostResponseCallback callback){
//
//        if(user == null) return;
//        UserProperties props = user.getProperties();
//        if(props == null) return;
//
//        Map<String,String> params = new HashMap<String, String>();
//        params.put(UserProperties.NOTIFICATION_WATER_FROM_KEY, props.getWaterNotificationFrom());
//        params.put(UserProperties.NOTIFICATION_WATER_TO_KEY, props.getWaterNotificationUntil());
//        params.put(UserProperties.NOTIFICATION_WATER_INTERVAL_KEY, String.valueOf(props.getWaterNotificationInterval()));
//        params.put(UserProperties.NOTIFICATION_WATER_AT_ALL_KEY, "true" ); // todo String.valueOf(props.getWaterNotificationOn()));
//
//
//        String url = new String(context.getString(R.string.server_base) + "/api/settings");
//
//        NetworkCenter.postInstance().requestPost(context, url, callback , params, getAuthorizationHeader(user));
//    }


    public static void rescheduleTraining(Context context, User user, Date selectedDateTime, PostResponseCallback postResponseCallback) {
        if (selectedDateTime == null) selectedDateTime = new Date();
        String url = new String(context.getString(R.string.server_base) + "/api/notifications");

        int day = UIHelper.getDay(selectedDateTime);

        String date = UIHelper.formatDateForServerRequest(selectedDateTime);
        String time = UIHelper.formatDateToTimeOnly(selectedDateTime);

        Map<String,String> params = new HashMap<String, String>();
        params.put("when", time);
        params.put("day", date);
        params.put("type", "1");
        params.put("day", String.valueOf(day-1));


        NetworkCenter.postInstance().requestPost(context, url, postResponseCallback , params, getAuthorizationHeader(user));
    }

    public static void getScheduleOfTraining(Context context, User user, GetResponseCallback getResponseCallback) {
        String url = new String(context.getString(R.string.server_base) + "/api/notifications");

        NetworkCenter.getInstance().requestGet(context, url, null, getAuthorizationHeader(user), getResponseCallback);
    }

    public static void getNextScheduledTraining(Context context, User user, GetResponseCallback getResponseCallback) {
        String url = new String(context.getString(R.string.server_base) + "/api/notifications/next");

        NetworkCenter.getInstance().requestGet(context, url, null, getAuthorizationHeader(user), getResponseCallback);
    }

    public static void getPaymentUrl(Context context, User user, int program_id, PostResponseCallback postResponseCallback) {
//        String url = new String(context.getString(R.string.server_base) + "/api/my/programs");
        String url = new String("http://runapp.clap.co.il/api/link/programs");
        Map<String,String> params = new HashMap<String, String>();
        params.put("program_id", String.valueOf(program_id));
        NetworkCenter.postInstance().requestPost(context, url, postResponseCallback , params, getAuthorizationHeader(user));
    }
}
