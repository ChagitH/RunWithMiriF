package com.forst.miri.runwithme.application;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.interfaces.UserChangedCallback;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Logger;
import com.forst.miri.runwithme.objects.User;
import com.forst.miri.runwithme.services.LocationService;
import com.google.firebase.FirebaseApp;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by chagithazani on 9/6/17.
 */

public class RunWithMiriApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler {

    //private User connectedUser = null;
    //private FirebaseAnalytics mFirebaseAnalytics;
    private ArrayList<UserChangedCallback> callbacksArray= null;
    //uncaught exceptions
    private Thread.UncaughtExceptionHandler defaultUEH;
    public static final String TRAINING = "training" ;
    public static final String EXERCISE = "exercise" ;
    public static final String GENERAL = "general_message";
    public static final String NOTIFICATION_GROUP  = "notification_group" ;
    public static final String NOTIFICATION_MESSAGE = "notification_message";
    public static final String PROGRAM_PURCHASE = "purchase" ;


    //public User getConnectedUser(){
//        return ConnectedUser.getInstance();
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Logger.loadContext(base);
        MultiDex.install(this);
    }

    public void setConnectedUser(User user){
        ConnectedUser.setUser(user);
        if(callbacksArray != null) {
            for (UserChangedCallback callback : callbacksArray) {
                callback.userWasChanged(ConnectedUser.getInstance());
            }
        }
        if(user == null){
            cancelNotifications();
        } else {
            subscribeToNotifications();
        }
    }

    private void subscribeToNotifications() {
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
////                .setNotificationReceivedHandler(new RunWithMiriNotificationReceivedHandler())
//                .setNotificationOpenedHandler(new RunWithMiriNotificationOpenedHandler())
//                .init();
        OneSignal.setSubscription(true);
    }

    public void cancelNotifications() {
//        OneSignal.removeNotificationOpenedHandler();
//        OneSignal.removeNotificationReceivedHandler();
        OneSignal.clearOneSignalNotifications();
        OneSignal.setSubscription(false);
    }

    public void updateConnectedUserPlanProgram(User user){
        if(ConnectedUser.getInstance() != null && user != null) {
            ConnectedUser.getInstance().setPracticeNum(user.getPracticeNum());
            ConnectedUser.getInstance().setExpirationOfPlan(user.getExpirationOfPlan());
            ConnectedUser.getInstance().setTotalAmount(user.getTotalAmount());
            ConnectedUser.getInstance().setPlanName(user.getPlanName());
            ConnectedUser.getInstance().setAdditional(user.getAdditional());
            ConnectedUser.getInstance().setProgramId(user.getProgramId());
            ConnectedUser.getInstance().setUserProgramId(user.getUserProgramId());
        }
    }

    public void addUserChangeCallback(UserChangedCallback callback){
        if ( callbacksArray == null ) callbacksArray = new ArrayList<>();
        callbacksArray.add(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Obtain the FirebaseAnalytics instance.
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseApp.initializeApp(this);


        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
//                .setNotificationReceivedHandler(new RunWithMiriNotificationReceivedHandler())
                .setNotificationOpenedHandler(new RunWithMiriNotificationOpenedHandler())
                .init();



        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        // OneSignal.syncHashedEmail(userEmail);

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(this);

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        if(callbacksArray != null) callbacksArray.clear();
        if(ConnectedUser.getInstance() != null) ConnectedUser.getInstance().saveLocally(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        if(throwable != null) {
            Crashlytics.logException(throwable);
            Logger.log(throwable.getMessage() + '\n' + throwable.getLocalizedMessage());

        }

        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);

        defaultUEH.uncaughtException(thread, throwable);
        System.exit(0);
    }





    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private class RunWithMiriNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;


            if (actionType == OSNotificationAction.ActionType.Opened) {

                if (data != null) {
                    String notificationID = null;
                    try{
                        notificationID = data.getString("action");
                    } catch (JSONException jsonEx){
                        jsonEx.printStackTrace();
                    }


                    if (notificationID != null && notificationID.matches(TRAINING) || notificationID.matches(EXERCISE) || notificationID.matches(GENERAL)) {

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(NOTIFICATION_GROUP, notificationID);
                        if(notificationID.matches(GENERAL)){
                            String body = result.notification.payload.body;
//                            String message = null;
//                            try{
//                                message = data.getString("action");
//                            } catch (JSONException jsonEx){
//                                jsonEx.printStackTrace();
//                            }
                            if(body != null) {
                                intent.putExtra(NOTIFICATION_MESSAGE, body);
                            }
                        }
                        startActivity(intent);
                    }
                }



            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);



            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */

        }

    }
}
