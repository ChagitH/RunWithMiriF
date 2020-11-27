package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.forst.miri.runwithme.exceptions.UserException;
import com.forst.miri.runwithme.interfaces.UserUpdatePracticeNumCallback;
import com.forst.miri.runwithme.miscellaneous.SQLiteDatabaseHandler;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chagithazani on 8/21/17.
 */

public class User implements Serializable{

    public static final String USER_KEY = "a_user_key";
    //private static String USER_PROGRAM_ID = "user_program_id";
    private static String MY_PROGRAMS_PROGRAM_ID_KEY_SAVE_LOCAL = "program_id";
    private static String MY_PROGRAMS_TOTAL_AMOUNT_KEY = "amount";
    private static String MY_PROGRAMS_ADDITIONAL_KEY = "additional";
    public static String FIRST_NAME_KEY = "name";
    public static String LAST_NAME_KEY = "last_name";
    //public static String SEREVER_ID_KEY = "id";
    public static String ID_ID_KEY = "id";
    public static String PERSONAL_ID_KEY = "tz";
    public static String PASSWORD_KEY = "password";
    public static String CELLULAR_KEY = "cellular";
    public static String EMAIL_KEY = "email";
    public static String HEIGHT_KEY = "height";
    public static String WEIGHT_KEY = "weight";
    public static String YEAR_OF_BIRTH_KEY = "birth";
    public static String TOKEN_KEY = "token";
    //public static String PRACTICE_NUM_KEY = "used";
    public static String PHOTO_SERVER_KEY = "photo";
    public static String PHOTO_LOCAL_KEY = "photo_local";
    public static String PHOTO_BASE_KEY = "base";
    public static String MY_PROGRAMS_OBJECT_KEY = "myPrograms";
    public static String SETTINGS_OBJECT_KEY = "settings";
    public static String MY_PROGRAMS_USED_KEY = "used";
    public static String MY_PROGRAMS_EXPIRED_KEY = "expired";
    public static String PRACTICE_PROGRAM_ID_KEY = "user_program_id";
    public static String ONE_SIGNAL_REGISTRATION_ID = "registration_id";
    public static String LAST_TRAINING_DATE_KEY = "last_training_date_key";

    public static String MY_PROGRAMS_NAME_KEY_GET = "name";
    public static String MY_PROGRAMS_NAME_KEY_SAVE_LOCAL = "name_of_program";

    public static String IMAGE_KEY = "image";
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String NOTIFICATIONS_KEY = "notifications";

    public static String NEXT_TRAINING_DATE_KEY = "next_training_date_key";
    private Date nextTrainingDate;
    public Date getNextTrainingDate() {
        return nextTrainingDate;
    }
    public void setNextTrainingDate(Date nextTrainingDate, Context context) {
        this.nextTrainingDate = nextTrainingDate;
        if(context != null) writeToSahredPrefs(NEXT_TRAINING_DATE_KEY, nextTrainingDate, context);
    }



    transient private Bitmap image = null;
    private String firstName;
    private String lastName;
    private String personal_id;
    private String cellularNum;
    private String email;
    private String password;
    private int weight;
    private int yearOfBirth;
    private int height;
    private Date formatedExpirationOfPlan = null, lastTrainingDate = null;
    private int totalAmount;
    private String authorizationToken = null;
    private String serverPhotoName = null;//
    private String localPhotoUriString = null;
    transient private Uri localPhotoUri = null;
    private String server_id;
    private int programId = 0;
    private int userProgramId = -1; //the value to send to server when practice is saved in server
    private int practiceNum = 0;
    private int additional;
    private String /*expirationOfPlan_1,*/ planName;
    private UserProperties properties = null;

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

   ;

    public String getPersonal_id() {
        return personal_id;
    }

    public int getUserProgramId() {
        return userProgramId;
    }

    public void setUserProgramId(int userProgramId) {
        this.userProgramId = userProgramId;
    }

    public int getAdditional() {
        return additional;
    }

    public void setAdditional(int additional) {
        this.additional = additional;
    }



    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getLastTrainingDate() {

        return lastTrainingDate;
    }

//    public String getLastTrainingDateString() {
//        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
//
////        String formatedDate = null;
////        //try {
////
////            formatedDate = format.format(lastTrainingDate);
////        //} catch (FormatException e) {
////            //e.printStackTrace();
////            this.lastTrainingDate = null;
////        //}
//        return lastTrainingDate != null ? format.format(lastTrainingDate) : ""; // todo "" or null?
//    }

    public void setLastTrainingDate(Date date){
        this.lastTrainingDate = date;
    }

    public void setLastTrainingDate(String dateStr){
        if(dateStr == null) {
            this.lastTrainingDate = null;
        } else {
            Log.d(getClass().getName(), " dateStr: -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> " + dateStr);
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            try {
                this.lastTrainingDate = format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
                this.lastTrainingDate = null;
            }
        }
    }

    public Date getExpirationOfPlan() {
        return formatedExpirationOfPlan;
    }

    public String getExpirationOfPlanAsString() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        if(formatedExpirationOfPlan!= null){
            return format.format(formatedExpirationOfPlan);
        } else {
            return "";
        }

    }

    public void setExpirationOfPlan(String expirationOfPlan) {
        //this.expirationOfPlan_1 = expirationOfPlan;
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            formatedExpirationOfPlan = format.parse(expirationOfPlan);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setExpirationOfPlan(Date expirationOfPlan) {
        formatedExpirationOfPlan = expirationOfPlan;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }



    private ArrayList<UserUpdatePracticeNumCallback> mPracticeNumUpdateCallbacks;


    public User (JSONObject json, Context context)throws UserException{//}, boolean downloadPhoto, Context context, final GetResponseCallback photoReadyCallback) throws UserException{

        try {
             firstName = json.getString(FIRST_NAME_KEY);
            lastName = json.getString(LAST_NAME_KEY);
            personal_id = json.getString(PERSONAL_ID_KEY);
            //serverId is the id value that server returns. save it and sent it with the authentication.
            server_id = json.getString(ID_ID_KEY);
            cellularNum = json.getString(CELLULAR_KEY);
            email = json.getString(EMAIL_KEY);
            weight = json.getInt(WEIGHT_KEY);
            yearOfBirth = json.getInt(YEAR_OF_BIRTH_KEY);
            height = json.getInt(HEIGHT_KEY);
            if(json.has(MY_PROGRAMS_OBJECT_KEY) &&  ! json.isNull(MY_PROGRAMS_OBJECT_KEY) ){
                JSONObject programsJson = json.getJSONObject(MY_PROGRAMS_OBJECT_KEY);
                if(programsJson != null) {
                    practiceNum = programsJson.getInt(MY_PROGRAMS_USED_KEY);
                    setExpirationOfPlan(programsJson.getString(MY_PROGRAMS_EXPIRED_KEY));
                    totalAmount = programsJson.getInt(MY_PROGRAMS_TOTAL_AMOUNT_KEY);
                    planName = programsJson.getString(MY_PROGRAMS_NAME_KEY_GET);
                    additional = programsJson.getInt(MY_PROGRAMS_ADDITIONAL_KEY);
                    programId = programsJson.getInt(MY_PROGRAMS_PROGRAM_ID_KEY_SAVE_LOCAL);
                    userProgramId = programsJson.getInt(ID_ID_KEY);
                }
            }
            properties = new UserProperties(context);
            if(json.has(NOTIFICATIONS_KEY)){
                JSONArray notificationsJson = json.getJSONArray(NOTIFICATIONS_KEY);
                if(notificationsJson != null) {
                    ArrayList<String> trainingSchedule = new ArrayList<>(7);
                    trainingSchedule.add("-");
                    trainingSchedule.add("-");
                    trainingSchedule.add("-");
                    trainingSchedule.add("-");
                    trainingSchedule.add("-");
                    trainingSchedule.add("-");
                    trainingSchedule.add("-");

                    ArrayList<String> exerciseSchedule = new ArrayList<>(7);
                    exerciseSchedule.add("-");
                    exerciseSchedule.add("-");
                    exerciseSchedule.add("-");
                    exerciseSchedule.add("-");
                    exerciseSchedule.add("-");
                    exerciseSchedule.add("-");
                    exerciseSchedule.add("-");

                    for (int i = 0; i < notificationsJson.length(); i++) {
                        JSONObject jObj = (JSONObject) notificationsJson.get(i);
                        String isInstead = jObj.getString("instead");
                        if(isInstead != null && isInstead.equalsIgnoreCase("null")) {
                            int day = jObj.getInt("day");
                            String time = jObj.getString("when");
                            if (time != null && time.length() > 5) {
                                time = time.substring(0, 5);
                            }
                            int type = jObj.getInt("type");
                            if (type == 1) {
                                if (day >= 0 && day < trainingSchedule.size()) {
                                    trainingSchedule.remove(day);
                                    trainingSchedule.add(day, time);
                                }
                            } else if (type == 2) {
                                if (day >= 0 && day < exerciseSchedule.size()) {
                                    exerciseSchedule.remove(day);
                                    exerciseSchedule.add(day, time);
                                }
                            }
                        }
                    }
                    properties.setTrainingSchedule(trainingSchedule);
                    properties.setExerciseSchedule(exerciseSchedule);
                }
            }
            if(json.has(SETTINGS_OBJECT_KEY)){

                JSONObject settingsJson = json.getJSONObject(SETTINGS_OBJECT_KEY);

                int id = settingsJson.getInt("id");
                if(! settingsJson.isNull(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY1)) {
                    Object beforeExcercise1 = settingsJson.get(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY1);
                    if (beforeExcercise1 != null) {
                        int exR1 = Integer.parseInt((String)beforeExcercise1);
                        properties.addExerciseReminder(exR1);
                    }
                }
                if(! settingsJson.isNull(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY2)) {
                    Object beforeExcercise2 = settingsJson.get(UserProperties.NOTIFICATION_BEFORE_EXERCISE_KEY2);
                    if (beforeExcercise2 != null) {
                        int exR2 = Integer.parseInt((String)beforeExcercise2);
                        properties.addExerciseReminder(exR2);
                    }
                }

                if(! settingsJson.isNull(UserProperties.NOTIFICATION_BEFORE_TRAINING_KEY1)) {
                    Object beforeTraining1 = settingsJson.get(UserProperties.NOTIFICATION_BEFORE_TRAINING_KEY1);
                    if (beforeTraining1 != null) {
                        int bt1 = Integer.parseInt((String)beforeTraining1);
                        properties.addTrainingReminder(bt1);
                    }
                }

                if(! settingsJson.isNull(UserProperties.NOTIFICATION_BEFORE_TRAINING_KEY2)) {
                    Object beforeTraining2 = settingsJson.get(UserProperties.NOTIFICATION_BEFORE_TRAINING_KEY2);
                    if (beforeTraining2 != null) {
                        int bt2 = Integer.parseInt((String)beforeTraining2);
                        properties.addTrainingReminder(bt2);
                    }
                }

                if(! settingsJson.isNull(UserProperties.NOTIFICATION_WATER_AT_ALL_KEY)) {
                    Object waterNotification = settingsJson.get(UserProperties.NOTIFICATION_WATER_AT_ALL_KEY);
                    if (waterNotification != null) {
                        int waterInterval = 0;
                        String waterFrom = null;
                        String waterUntill = null;
                        try {
                            waterInterval = settingsJson.getInt(UserProperties.NOTIFICATION_WATER_INTERVAL_KEY);
                            waterFrom = settingsJson.getString(UserProperties.NOTIFICATION_WATER_FROM_KEY);
                            waterUntill = settingsJson.getString(UserProperties.NOTIFICATION_WATER_TO_KEY);
                            if((waterUntill != null && waterUntill.equalsIgnoreCase("null") )|| (waterFrom != null && waterFrom.equalsIgnoreCase("null"))) {
                                waterUntill = null;
                                waterFrom = null;
                                //waterInterval = 0;//?
                            }
                            properties.setWaterNotificationInterval(waterInterval);
                            properties.setWaterNotificationFrom(waterFrom);
                            properties.setWaterNotificationUntil(waterUntill);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if(json.has(TOKEN_KEY)) authorizationToken = json.getString(TOKEN_KEY);
            if(json.has(PHOTO_SERVER_KEY)) setServerPhotoName(json.getString(PHOTO_SERVER_KEY));
        } catch(Exception e){
            e.printStackTrace();
            throw new UserException(e.getMessage());
        }
    }

    public User (){
        properties = new UserProperties();
    }

    public void setServerPhotoName(String serverPhotoName){
        this.serverPhotoName = serverPhotoName;
    }


    public String getServerPhotoName() {
        return serverPhotoName;
    }


    private Uri getLocalPhotoUri() {
        if(localPhotoUri != null) return localPhotoUri;

        if(localPhotoUriString == null) return null;

        Uri uri = Uri.parse(localPhotoUriString);
        localPhotoUri = uri;
        return uri;
    }

    public void setLocalPhotoUri(Uri localPhotoUri, Context context, boolean saveToSharedPrefs) {
        if(localPhotoUri == null || context == null) return;
        this.localPhotoUri = localPhotoUri;
        this.localPhotoUriString = localPhotoUri.toString();
        if(saveToSharedPrefs) savePhotoPathToSharedPrefs(context);
        if(getLocalPhotoUri() != null && (this.image == null || saveToSharedPrefs)) {
            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), localPhotoUri);
                Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.getContentResolver(), localPhotoUri);
                int nh = (int) ( bitmap.getHeight() * (256.0 / bitmap.getWidth()) );
                setImage(Bitmap.createScaledBitmap(bitmap, 256, nh, true));
                //this.image =  Bitmap.createScaledBitmap(bitmap, 256, nh, true);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    private void writeObject(ObjectOutputStream oos) throws IOException {
        // This will serialize all fields that you did not mark with 'transient'
        // (Java's default behaviour)
        oos.defaultWriteObject();
        // Now, manually serialize all transient fields that you want to be serialized
        oos.writeInt(height);
        if(image!=null){
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            boolean success = image.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            if(success){
                oos.writeObject(byteStream.toByteArray());
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        // Now, all again, deserializing - in the SAME ORDER!
        // All non-transient fields
        ois.defaultReadObject();
        // All other fields that you serialized
        ois.readInt();
        try {
            byte[] imageByte = (byte[]) ois.readObject();
            if (imageByte != null && imageByte.length > 0) {
                image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            }
        }catch (Exception e){ //in case of no image sent - this throws a java.io.OptionalDataException
            e.printStackTrace();
        }
    }

    public Bitmap getImage() {

        return image;
    }

    public void setImage(Bitmap image) {
        if(image != null) {
            this.image = image;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return personal_id;
    }

    public void setId(String id) {
        this.personal_id = id;
    }

    public String getServerId() {
        return server_id;
    }

    public void setServerId(String id) {
        this.server_id = id;
    }

    public String getPhoneNum() {
        return cellularNum;
    }

    public void setPhoneNum(String cellularNum) {
        this.cellularNum = cellularNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public int getPracticeNum() {
        return practiceNum;
    }

    public void setPracticeNum(Context context, int practiceNum) {
        this.practiceNum = practiceNum;
        updateLocalThatPracticeNumChanged(context);
        if (mPracticeNumUpdateCallbacks != null){
            for(UserUpdatePracticeNumCallback callback : mPracticeNumUpdateCallbacks ){
                callback.userPracticeNumWasChanged(this);
            }
        }
    }

    public void setPracticeNum(int practiceNum) {
        this.practiceNum = practiceNum;
    }

    private void updateLocalThatPracticeNumChanged(Context context){
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //if(practiceNum > 0) { //why??
            editor.putInt(MY_PROGRAMS_USED_KEY, practiceNum);
        //}
         editor.apply();
    }

    private void updateAndSaveProgramDetails(JSONObject json, Context context){
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();




        try {
            if (json.has(MY_PROGRAMS_OBJECT_KEY) && !json.isNull(MY_PROGRAMS_OBJECT_KEY)) {
                JSONObject programsJson = json.getJSONObject(MY_PROGRAMS_OBJECT_KEY);
                if (programsJson != null) {
                    practiceNum = programsJson.getInt(MY_PROGRAMS_USED_KEY);
                    setExpirationOfPlan(programsJson.getString(MY_PROGRAMS_EXPIRED_KEY));
                    totalAmount = programsJson.getInt(MY_PROGRAMS_TOTAL_AMOUNT_KEY);
                    planName = programsJson.getString(MY_PROGRAMS_NAME_KEY_GET);
                    additional = programsJson.getInt(MY_PROGRAMS_ADDITIONAL_KEY);
                    programId = programsJson.getInt(MY_PROGRAMS_PROGRAM_ID_KEY_SAVE_LOCAL);
                    userProgramId = programsJson.getInt(ID_ID_KEY);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        editor.putInt(MY_PROGRAMS_USED_KEY, practiceNum);

        //}
        editor.apply();


    }

    public boolean IsRegisteredToRunWithMiri() {
        if(getPracticeNum() < 1) return false;
        if(getPracticeNum() > getTotalAmount()) return false; //todo: this is the problematic line - in the 2-5 and 5-10 plans it is false
        if(getExpirationOfPlan() == null) return false;
        if(getExpirationOfPlan().after(new Date()) && getPracticeNum() <= getTotalAmount()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsPaidRegistrationToRunWithMiri() {
        if(getPracticeNum() < 1) return false;
        if(getPracticeNum() > getTotalAmount()) return false;
        if(getProgramId() == Practice.TRIAL_PROGRAM_ID) return false;
        if(getExpirationOfPlan() == null) return false;
        if(getExpirationOfPlan().after(new Date()) && getPracticeNum() <= getTotalAmount()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInTrial() {
        if (getProgramId() == Practice.TRIAL_PROGRAM_ID) return true; // trial program
        if (getProgramId() == 0) return true; // no program
//        if(getPracticeNum() < 1) return true; //
        if(getPracticeNum() > getTotalAmount()) return true; //finished all practices
        if(getExpirationOfPlan() != null && getExpirationOfPlan().before(new Date())) return true; //program expired

        //else
        return false;
    }

    public void writeToSahredPrefs(String key, Date value, Context context){
        if(value == null) return;
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        if(prefs != null){
            String dateStr = UIHelper.formatDateAndTime(value);
            if(dateStr != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(key, dateStr);
                editor.commit();
            }
        }
    }

    public void saveLocally(Context context) {
        //first erase
        eraseLocally(context);

        //write user to sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FIRST_NAME_KEY, firstName);
        editor.putString(LAST_NAME_KEY, lastName);
        editor.putString(EMAIL_KEY, email);
        editor.putString(CELLULAR_KEY, cellularNum);
        editor.putString(PERSONAL_ID_KEY, personal_id);
        editor.putString(ID_ID_KEY, server_id);
        editor.putString(TOKEN_KEY, authorizationToken);
        editor.putInt(WEIGHT_KEY, weight);
        editor.putInt(HEIGHT_KEY, height);
        editor.putInt(YEAR_OF_BIRTH_KEY, yearOfBirth);
        if(practiceNum > 0) {
            savePracticePlanDataLocally(context, this);
        }
        if (localPhotoUri != null) {
            editor.putString(PHOTO_LOCAL_KEY, localPhotoUri.toString());
        }
        if(this.properties != null){
            this.properties.saveToSharedPreferences(context);
        }
        if(nextTrainingDate != null) {
            editor.putString(NEXT_TRAINING_DATE_KEY, UIHelper.formatDateAndTime(nextTrainingDate));
        }
        editor.commit();
    }

    public void eraseLocally(Context context){
        //erase user from sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        if(prefs == null){
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        if(this.properties != null){
            this.properties.eraseLocally(context);
        }
    }

    public static User createUserFromSharedPreferences(Context context, boolean minimal){
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        if (! prefs.contains(TOKEN_KEY)) { // == no user connected
            return null;
        }
        User user = new User();
        user.setFirstName(prefs.getString(FIRST_NAME_KEY,""));
        user.setLastName(prefs.getString(LAST_NAME_KEY,""));
        user.setEmail(prefs.getString(EMAIL_KEY,""));
        user.setPhoneNum(prefs.getString(CELLULAR_KEY,""));
        user.setId(prefs.getString(PERSONAL_ID_KEY,""));
        user.setAuthorizationToken(prefs.getString(TOKEN_KEY,""));
        user.setWeight(prefs.getInt(WEIGHT_KEY,0));
        user.setHeight(prefs.getInt(HEIGHT_KEY,0));
        user.setYearOfBirth(prefs.getInt(YEAR_OF_BIRTH_KEY,0));
        if(!minimal) {
            String uri = prefs.getString(PHOTO_LOCAL_KEY, "");
            if (uri.length() > 0) {
                user.setLocalPhotoUri(Uri.parse(uri), context, false);
            }
        }
        if(prefs.getInt(MY_PROGRAMS_USED_KEY,0) > 0) {
            user.setPracticeNum(context, prefs.getInt(MY_PROGRAMS_USED_KEY,0));
            user.setExpirationOfPlan(prefs.getString(MY_PROGRAMS_EXPIRED_KEY,""));
            user.setPlanName(prefs.getString(MY_PROGRAMS_NAME_KEY_SAVE_LOCAL,""));
            user.setTotalAmount(prefs.getInt(MY_PROGRAMS_TOTAL_AMOUNT_KEY,0));
            user.setAdditional(prefs.getInt(MY_PROGRAMS_ADDITIONAL_KEY,0));
            user.setProgramId(prefs.getInt(MY_PROGRAMS_PROGRAM_ID_KEY_SAVE_LOCAL,0));
            user.setUserProgramId(prefs.getInt(PRACTICE_PROGRAM_ID_KEY,0));
            user.setLastTrainingDate(prefs.getString(LAST_TRAINING_DATE_KEY,null));
        }
        user.setServerId(prefs.getString(ID_ID_KEY,""));
        UserProperties props = UserProperties.readFromSharedPreferences(context);
        user.setProperties(props == null ? new UserProperties(context) : props);
        if(prefs.contains(NEXT_TRAINING_DATE_KEY)) {
            user.setNextTrainingDate(UIHelper.formatStringToDateAndTime(prefs.getString(NEXT_TRAINING_DATE_KEY, "")), null);
        }
        return user;
    }

    public boolean isUserConnected(Context context){
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        return prefs.contains(TOKEN_KEY);

    }

//    public boolean isInTrainingPlan() {
//        return getPracticeNum() > 0;
//    }

    public void addOnPracticeNumUpdatedCallback(UserUpdatePracticeNumCallback callback) {
        if(this.mPracticeNumUpdateCallbacks == null) this.mPracticeNumUpdateCallbacks = new ArrayList<>();
        this.mPracticeNumUpdateCallbacks.add(callback);
    }

    public static String getNotificationUserId() {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        if(status != null){
            return status.getSubscriptionStatus().getUserId();
        } else {
            return null;
        }

//        boolean isEnabled = status.getPermissionStatus().getEnabled();
//
//        boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();
//        boolean subscriptionSetting = status.getSubscriptionStatus().getUserSubscriptionSetting();
//        String userID = status.getSubscriptionStatus().getUserId();
//        String pushToken = status.getSubscriptionStatus().getPushToken();
//
//        textView.setText("PlayerID: " + userID + "\nPushToken: " + pushToken);
    }

    public UserProperties getProperties() {
        return this.properties;
    }

    public void setProperties(UserProperties userProperties) {
        this.properties = userProperties;
    }

//    public static void replacePracticePlanData(Context context, User user) {
//
//        this.setPracticeNum(context, user.getPracticeNum());
//        this.setExpirationOfPlan(user.getExpirationOfPlan());
//        this.setPlanName(user.getPlanName());
//        this.setTotalAmount(user.getTotalAmount());
//        this.setAdditional(user.getAdditional());
//        this.setProgramId(user.getProgramId());
//        this.setUserProgramId(user.getUserProgramId());
//        this.setLastTrainingDate(user.getLastTrainingDate());
//
//        savePracticePlanDataLocally(context);
//    }

    public static void savePracticePlanDataLocally(Context context, User user) {
        if(user.getPracticeNum() > 0) {
            //write user to sharedPreferences
            SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt(MY_PROGRAMS_USED_KEY, user.getPracticeNum());
            editor.putString(MY_PROGRAMS_EXPIRED_KEY, user.getExpirationOfPlanAsString());
            editor.putInt(MY_PROGRAMS_ADDITIONAL_KEY, user.getAdditional());
            editor.putInt(MY_PROGRAMS_TOTAL_AMOUNT_KEY, user.getTotalAmount());
            editor.putInt(MY_PROGRAMS_PROGRAM_ID_KEY_SAVE_LOCAL, user.getProgramId());
            editor.putString(MY_PROGRAMS_NAME_KEY_SAVE_LOCAL, user.getPlanName());
            editor.putInt(PRACTICE_PROGRAM_ID_KEY, user.getUserProgramId() );
            //editor.putString(LAST_TRAINING_DATE_KEY, user.getLastTrainingDateString());


//todo: i need to add the next practice date.
            editor.apply();

        }

    }

    public PracticeData getLastPractice(Context context, String userEmail) {
        SQLiteDatabaseHandler dbHandler = new SQLiteDatabaseHandler(context);
        PracticeData pData =  dbHandler.getLastPracticeOfProgram(context, this.getProgramId(), userEmail);
        if(pData == null ) return null;
        return pData;

    }

    public void updateUserDetails(JSONObject json, final Context applicationContext) {
        try {
            firstName = json.getString(FIRST_NAME_KEY);
            lastName = json.getString(LAST_NAME_KEY);
            personal_id = json.getString(PERSONAL_ID_KEY);
            //serverId is the id value that server returns. save it and sent it with the authentication.
            server_id = json.getString(ID_ID_KEY);
            cellularNum = json.getString(CELLULAR_KEY);
            email = json.getString(EMAIL_KEY);
            weight = json.getInt(WEIGHT_KEY);
            yearOfBirth = json.getInt(YEAR_OF_BIRTH_KEY);
            height = json.getInt(HEIGHT_KEY);
            serverPhotoName = json.getString(PHOTO_SERVER_KEY);
//             NetworkCenterHelper.getPhotoFromServer(applicationContext, this, new GetResponseCallback() {
//                @Override
//                public void requestStarted() {
//
//                }
//
//                @Override
//                public void requestCompleted(String response) {
//                    User.this.saveLocally(applicationContext);
//                    LoginActivity.savePhotoToLocalDevice(User.this, applicationContext);
//                }
//
//                @Override
//                public void requestEndedWithError(VolleyError error) {
//
//                }
//            });

        } catch(Exception ex){
            ex.printStackTrace();
        }

    }

//    public void getPhotoFromServer(final Context context, final GetResponseCallback callback){
//        NetworkCenterHelper.getPhotoFromServer(context, this, new GetResponseCallback() {
//            @Override
//            public void requestStarted() {
//                if(callback != null) callback.requestStarted();
//            }
//
//            @Override
//            public void requestCompleted(String response) {
//                LoginActivity.savePhotoToLocalDevice(User.this, context);
//                User.this.savePhotoLocally(context);
//                if(callback != null) callback.requestCompleted(response);
//            }
//
//            @Override
//            public void requestEndedWithError(VolleyError error) {
//                if(error != null) error.printStackTrace();
//                if(callback != null) callback.requestEndedWithError(error);
//            }
//        });
//    }
//
    private void savePhotoPathToSharedPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(User.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (localPhotoUri != null) {
            editor.putString(PHOTO_LOCAL_KEY, localPhotoUri.toString());
        }
        editor.commit();
    }


}

