package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by chagithazani on 12/11/17.
 */

public class UserProperties implements Serializable {

    private String trainingReminderSoundPath = null, trainingReminderSoundName = null, exerciseReminderSoundPath = null, exerciseReminderSoundName = null, waterReminderSoundPath = null, waterReminderSoundName = null;
    private String waterNotificationFrom = null, waterNotificationUntill = null;
    private int waterNotificationInterval = 0;
    private ArrayList<String> trainingSchedule = new ArrayList<>();
    private ArrayList<Integer> trainingRemindersBefore = new ArrayList<>();

    private ArrayList<String> exerciseSchedule = new ArrayList<>();
    private ArrayList<Integer> exerciseRemindersBefore = new ArrayList<>();
    private boolean waterNotificationOn = false;

    public static final String USER_PROPERTIES_KEY = "userProperties_key";

    ///////////////////////// server json keys//////////////////////////////////////////////////
    public static final String NOTIFICATION_SCHEDULE_WHEN_KEY = "when";
    public static final String NOTIFICATION_SCHEDULE_DAY_KEY = "day";
    public static final String NOTIFICATION_SCHEDULE_TYPE_KEY = "type";
    public static final String NOTIFICATION_SCHEDULE_ARRAY_KEY = "days";
    public static final String TYPE_TRAINING = "1";
    public static final String TYPE_EXERCISE = "2";
    public static final String NOTIFICATION_BEFORE_TRAINING_KEY1 = "before_training";
    public static final String NOTIFICATION_BEFORE_TRAINING_KEY2 = "more_before_training";
    public static final String NOTIFICATION_BEFORE_EXERCISE_KEY1 = "before_exercise";
    public static final String NOTIFICATION_BEFORE_EXERCISE_KEY2 = "more_before_exercise";
    public static final String NOTIFICATION_WATER_FROM_KEY = "water_from";
    public static final String NOTIFICATION_WATER_TO_KEY = "water_to";
    public static final String NOTIFICATION_WATER_INTERVAL_KEY = "water_every_time";
    public static final String NOTIFICATION_WATER_AT_ALL_KEY = "water";
    ///////////////////////// end of server json keys///////////////////////////////////////////

    public static final String TRAINING_REMINDER_SOUND_PATH_KEY = "trainingReminderSoundPathKey";
    public static final String TRAINING_REMINDER_SOUND_NAME_KEY = "trainingReminderSoundNAMEKey";
    public static final String TRAINING_REMINDERS_ARRAY_KEY = "trainingReminderArrayKey";
    public static final String TRAINING_SCHEDULE_ARRAY_KEY = "trainingScheduleArrayKey";

    public static final String EXERCISE_REMINDER_SOUND_PATH_KEY = "exerciseReminderSoundPathKey";
    public static final String EXERCISE_REMINDER_SOUND_NAME_KEY = "exerciseReminderSoundNAMEKey";
    public static final String EXERCISE_REMINDERS_ARRAY_KEY = "exerciseReminderArrayKey";
    public static final String EXERCISE_SCHEDULE_ARRAY_KEY = "exerciseScheduleArrayKey";

    public static final String WATER_REMINDER_SOUND_PATH_KEY = "waterReminderSoundPathKey";
    public static final String WATER_REMINDER_SOUND_NAME_KEY = "waterReminderSoundNAMEKey";
    public static final String WATER_REMINDER_FROM_KEY = "waterReminderFrom";
    public static final String WATER_REMINDER_UNTIL_KEY = "waterReminderUntil";
    public static final String WATER_REMINDER_INTERVAL_KEY = "waterReminderInterval";

//    public static final String TRAINING_NOTIFICATION = "678";
//    public static final String EXERCISE_NOTIFICATION = 789;
//    public static final String WATER_NOTIFICATION = 890;

    public UserProperties(Context context){
        //NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(context != null) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, soundUri);
            String title = ringtone.getTitle(context);
            setTrainingReminderSoundPath(soundUri.toString());
            setTrainingReminderSoundName(title);
            setExerciseReminderSoundPath(soundUri.toString());
            setExerciseReminderSoundName(title);
            setWaterReminderSoundPath(soundUri.toString());
            setWaterReminderSoundName(title);
        }

        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");

        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");


//            if(trainingChosenRingtoneName == null){
//                trainingChosenRingtonePath = soundUri.toString();
//                trainingChosenRingtoneName = title;
//            }
//            if(exerciseChosenRingtoneName == null){
//                exerciseChosenRingtonePath = soundUri.toString();
//                exerciseChosenRingtoneName = title;
//            }
//            if(waterChosenRingtoneName == null){
//                waterChosenRingtonePath = soundUri.toString();
//                waterChosenRingtoneName = title;
//            }
    }

//    public UserProperties(UserProperties userProperties) {
//        if(userProperties == null) return;
//        this.setTrainingReminderSoundPath(userProperties.getTrainingReminderSoundPath());
//        this.setTrainingReminderSoundPath(userProperties.getTrainingReminderSoundPath());
//        this.setTrainingSchedule(userProperties.getTrainingSchedule());
//    }


    public UserProperties() {
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");
        this.trainingSchedule.add("-");

        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
        exerciseSchedule.add("-");
    }

    ////////////////////////////////////////////////// training ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getTrainingReminderSoundPath() {
        return trainingReminderSoundPath;
    }

    public void setTrainingReminderSoundPath(String trainingReminderSoundPath) {
        this.trainingReminderSoundPath = trainingReminderSoundPath;
    }

    public String getTrainingReminderSoundName() {
        return trainingReminderSoundName;
    }

    public void setTrainingReminderSoundName(String trainingReminderSoundName) {
        this.trainingReminderSoundName = trainingReminderSoundName;
    }

    public ArrayList<String> getTrainingSchedule() {
        return trainingSchedule;
    }

    public ArrayList<Integer> getTrainingRemindersBefore() {
        return trainingRemindersBefore;
    }

    public void addTrainingReminder(int minsBefore){
        this.trainingRemindersBefore.add(minsBefore);
    }

    public void clearTrainingReminder(){
        this.trainingRemindersBefore.clear();
    }

    private void setTrainingSchedule(String builtStr){
        trainingSchedule = new ArrayList<String>();
        if(builtStr == null || builtStr.isEmpty()){
            trainingSchedule.add("-");
            trainingSchedule.add("-");
            trainingSchedule.add("-");
            trainingSchedule.add("-");
            trainingSchedule.add("-");
            trainingSchedule.add("-");
            trainingSchedule.add("-");
            return;
        } else {
            String[] days = builtStr.split(",");
            //StringTokenizer st = new StringTokenizer(builtStr,",",false);
            for(int i = 0 ; i < days.length ; i ++){
                trainingSchedule.add(days[i]);
            }
//            while (st.hasMoreElements()) {
//                trainingReminders.add(st.nextToken());
//            }

        }
    }

    public void setTrainingSchedule(ArrayList<String> schedule){
        if (this.trainingSchedule == null){
            this.trainingSchedule = new ArrayList<String>();
        } else {
            this.trainingSchedule.clear();
        }
        this.trainingSchedule.addAll(schedule);
        //make sure Array is exactly 7 days. no more. no less.
        while(this.trainingSchedule.size() < 7){
            this.trainingSchedule.add("-");
        }
        while(this.trainingSchedule.size() > 7){
            this.trainingSchedule.remove(trainingSchedule.size()-1);
        }
    }

    private void setTrainingRemindersBefore(String builtStr) {
        trainingRemindersBefore = new ArrayList<Integer>();

        if(builtStr == null || builtStr.isEmpty()){
           return;
        } else {
            String[] reminders = builtStr.split(",");
            //StringTokenizer st = new StringTokenizer(builtStr,",",false);
            for(int i = 0 ; i < reminders.length ; i ++){
                trainingRemindersBefore.add(Integer.parseInt(reminders[i]));
            }
//            while (st.hasMoreElements()) {
//                trainingReminders.add(st.nextToken());
//            }

        }
    }

    private void setTrainingRemindersBefore(ArrayList<Integer> reminders) {
        if (this.trainingRemindersBefore == null){
            this.trainingRemindersBefore = new ArrayList<Integer>();
        } else {
            this.trainingRemindersBefore.clear();
        }
        this.trainingRemindersBefore.addAll(reminders);
    }
    ////////////////////////////////////////////////// end of training ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////// exercise ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getExerciseReminderSoundPath() {
        return exerciseReminderSoundPath;
    }

    public void setExerciseReminderSoundPath(String exerciseReminderSoundPath) {
        this.exerciseReminderSoundPath = exerciseReminderSoundPath;
    }

    public String getExerciseReminderSoundName() {
        return exerciseReminderSoundName;
    }

    public void setExerciseReminderSoundName(String exerciseReminderSoundName) {
        this.exerciseReminderSoundName = exerciseReminderSoundName;
    }

    public ArrayList<String> getExerciseSchedule() {
        return exerciseSchedule;
    }

    public ArrayList<Integer> getExerciseRemindersBefore() {
        return exerciseRemindersBefore;
    }

    public void addExerciseReminder(int minsBefore){
        this.exerciseRemindersBefore.add(minsBefore);
    }

    public void clearExerciseReminders(){
        this.exerciseRemindersBefore.clear();
    }
    private void setExerciseSchedule(String builtStr){
        exerciseSchedule = new ArrayList<String>();
        if(builtStr == null || builtStr.isEmpty()){
            exerciseSchedule.add("-");
            exerciseSchedule.add("-");
            exerciseSchedule.add("-");
            exerciseSchedule.add("-");
            exerciseSchedule.add("-");
            exerciseSchedule.add("-");
            exerciseSchedule.add("-");
            return;
        } else {
            String[] days = builtStr.split(",");
            //StringTokenizer st = new StringTokenizer(builtStr,",",false);
            for(int i = 0 ; i < days.length ; i ++){
                exerciseSchedule.add(days[i]);
            }
        }
    }

    public void setExerciseSchedule(ArrayList<String> schedule){
        if (this.exerciseSchedule == null){
            this.exerciseSchedule = new ArrayList<String>();
        } else {
            this.exerciseSchedule.clear();
        }
        this.exerciseSchedule.addAll(schedule);
        //make sure Array is exactly 7 days. no more. no less.
        while(this.exerciseSchedule.size() < 7){
            this.exerciseSchedule.add("-");
        }
        while(this.exerciseSchedule.size() > 7){
            this.exerciseSchedule.remove(exerciseSchedule.size()-1);
        }
    }

    private void setExerciseRemindersBefore(String builtStr) {

        exerciseRemindersBefore = new ArrayList<Integer>();

        if(builtStr == null || builtStr.isEmpty()){
            return;
        } else {
            String[] reminders = builtStr.split(",");
            //StringTokenizer st = new StringTokenizer(builtStr,",",false);
            for(int i = 0 ; i < reminders.length ; i ++){
                exerciseRemindersBefore.add(Integer.parseInt(reminders[i]));
            }
        }
    }

    private void setExerciseRemindersBefore(ArrayList<Integer> reminders) {
        if (this.exerciseRemindersBefore == null){
            this.exerciseRemindersBefore = new ArrayList<Integer>();
        } else {
            this.exerciseRemindersBefore.clear();
        }
        this.exerciseRemindersBefore.addAll(reminders);
    }
    ////////////////////////////////////////////////// end of exercise ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////// water ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getWaterReminderSoundPath() {
        return waterReminderSoundPath;
    }

    public void setWaterReminderSoundPath(String waterReminderSoundPath) {
        this.waterReminderSoundPath = waterReminderSoundPath;
    }

    public String getWaterReminderSoundName() {
        return waterReminderSoundName;
    }

    public void setWaterReminderSoundName(String waterReminderSoundName) {
        this.waterReminderSoundName = waterReminderSoundName;
    }

    public void setWaterNotificationFrom(String from){
        this.waterNotificationFrom = formateTimeString(from);
    }

    public String getWaterNotificationFrom(){
        return this.waterNotificationFrom;
    }

    public void setWaterNotificationUntil(String until){
        this.waterNotificationUntill = formateTimeString(until);
    }

    public String getWaterNotificationUntil(){
        return this.waterNotificationUntill;
    }

    public void setWaterNotificationInterval(int interval){
        this.waterNotificationInterval = interval;
    }

    public int getWaterNotificationInterval(){
        return this.waterNotificationInterval;
    }

//    public void setWaterNotificationOn(boolean waterNotification){
//        this.waterNotificationOn = waterNotification;
//    }

    public boolean isWaterNotificationOn(){
        return (this.waterNotificationFrom != null && this.waterNotificationUntill != null && this.waterNotificationInterval > 0);
    }
    ////////////////////////////////////////////////// end of water /////////////////////////////////////////////////////////////////////////////////////////


    private String formateTimeString(String timeStr){
        if (timeStr == null) return null;
        StringTokenizer st = new StringTokenizer(timeStr, ":", false);
        String hour = "00";
        String minute = "00";
        if(st.hasMoreTokens()) hour = st.nextToken();
        if(st.hasMoreTokens())  minute = st.nextToken();
        StringBuilder result = new StringBuilder();
        return result.append(hour).append(":").append(minute).toString();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        // This will serialize all fields that you did not mark with 'transient'
        // (Java's default behaviour)
        oos.defaultWriteObject();
        // Now, manually serialize all transient fields that you want to be serialized

    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        // Now, all again, deserializing - in the SAME ORDER!
        // All non-transient fields
        ois.defaultReadObject();
        // All other fields that you serialized

    }

    public void saveToSharedPreferences(Context context) {
        //first erase
        eraseLocally(context);

        //get sharedPreferences editor
        SharedPreferences prefs = context.getSharedPreferences(UserProperties.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // training
        editor.putString(TRAINING_REMINDER_SOUND_PATH_KEY, trainingReminderSoundPath);
        editor.putString(TRAINING_REMINDER_SOUND_NAME_KEY, trainingReminderSoundName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < trainingSchedule.size() ; i++) {
            sb.append(trainingSchedule.get(i));
            if(i < trainingSchedule.size() - 1 ){
                sb.append(",");
            }
        }
        editor.putString(TRAINING_SCHEDULE_ARRAY_KEY, sb.toString());

        sb = new StringBuilder();
          for (int i = 0 ; i < trainingRemindersBefore.size() ; i++) {

            sb.append(trainingRemindersBefore.get(i));
            if(i < trainingRemindersBefore.size() - 1 ){
                sb.append(",");
            }
        }
        editor.putString(TRAINING_REMINDERS_ARRAY_KEY, sb.toString());

        // exercise
        editor.putString(EXERCISE_REMINDER_SOUND_PATH_KEY, exerciseReminderSoundPath);
        editor.putString(EXERCISE_REMINDER_SOUND_NAME_KEY, exerciseReminderSoundName);
        sb = new StringBuilder();
        for (int i = 0 ; i < exerciseSchedule.size() ; i++) {
            sb.append(exerciseSchedule.get(i));
            if(i < exerciseSchedule.size() - 1 ){
                sb.append(",");
            }
        }
        editor.putString(EXERCISE_SCHEDULE_ARRAY_KEY, sb.toString());

        sb = new StringBuilder();
        for (int i = 0 ; i < exerciseRemindersBefore.size() ; i++) {

            sb.append(exerciseRemindersBefore.get(i));
            if(i < exerciseRemindersBefore.size() - 1 ){
                sb.append(",");
            }
        }
        editor.putString(EXERCISE_REMINDERS_ARRAY_KEY, sb.toString());

        //water
        editor.putString(WATER_REMINDER_SOUND_PATH_KEY, getWaterReminderSoundPath());
        editor.putString(WATER_REMINDER_SOUND_NAME_KEY, getWaterReminderSoundName());
        editor.putString(WATER_REMINDER_FROM_KEY, getWaterNotificationFrom());
        editor.putString(WATER_REMINDER_UNTIL_KEY, getWaterNotificationUntil());
        editor.putInt(WATER_REMINDER_INTERVAL_KEY, getWaterNotificationInterval());
        //editor.putBoolean(NOTIFICATION_WATER_AT_ALL_KEY, getWaterNotificationOn());

        editor.apply();
    }

    public void eraseLocally(Context context){
        //erase user from sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(UserProperties.class.getName(), Context.MODE_PRIVATE);
        if(prefs == null){
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static UserProperties readFromSharedPreferences(Context context){
        SharedPreferences prefs = context.getSharedPreferences(UserProperties.class.getName(), Context.MODE_PRIVATE);
        if (prefs == null) {
            return null;
        }
        UserProperties userProps = new UserProperties(context);

        userProps.setTrainingReminderSoundPath(prefs.getString(TRAINING_REMINDER_SOUND_PATH_KEY,null));
        userProps.setTrainingReminderSoundName(prefs.getString(TRAINING_REMINDER_SOUND_NAME_KEY,null));
        userProps.setTrainingSchedule(prefs.getString(TRAINING_SCHEDULE_ARRAY_KEY,null));
        userProps.setTrainingRemindersBefore(prefs.getString(TRAINING_REMINDERS_ARRAY_KEY,null));

        userProps.setExerciseReminderSoundPath(prefs.getString(EXERCISE_REMINDER_SOUND_PATH_KEY,null));
        userProps.setExerciseReminderSoundName(prefs.getString(EXERCISE_REMINDER_SOUND_NAME_KEY,null));
        userProps.setExerciseSchedule(prefs.getString(EXERCISE_SCHEDULE_ARRAY_KEY,null));
        userProps.setExerciseRemindersBefore(prefs.getString(EXERCISE_REMINDERS_ARRAY_KEY,null));


        userProps.setWaterReminderSoundPath(prefs.getString(WATER_REMINDER_SOUND_PATH_KEY,null));
        userProps.setWaterReminderSoundName(prefs.getString(WATER_REMINDER_SOUND_NAME_KEY,null));
        userProps.setWaterNotificationFrom(prefs.getString(WATER_REMINDER_FROM_KEY,null));
        userProps.setWaterNotificationUntil(prefs.getString(WATER_REMINDER_UNTIL_KEY,null));
        userProps.setWaterNotificationInterval(prefs.getInt(WATER_REMINDER_INTERVAL_KEY,0));
        //userProps.setWaterNotificationOn(prefs.getBoolean(NOTIFICATION_WATER_AT_ALL_KEY, false));

        return userProps;
    }


    public String isWaterNotificationOnIntVal() {
        if(isWaterNotificationOn()) return "1";
        else return "0";
    }
}
