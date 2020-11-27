package com.forst.miri.runwithme.services;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.application.RunWithMiriApplication;
import com.forst.miri.runwithme.exceptions.UserException;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.miscellaneous.DownloadPracticeTask;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.objects.User;
import com.forst.miri.runwithme.objects.UserProperties;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationExtender extends NotificationExtenderService {

//    public static final String TRAINING = "training" ;
//    public static final String EXERCISE = "exercise" ;
//    public static final String NOTIFICATION_GROUP  = "notification_group" ;

//    NotificationManager mNotificationManager = null;
//
    @Override
    protected boolean onNotificationProcessing(final OSNotificationReceivedResult notification) {

        Log.d(getClass().getName(), "  ######################################## notification received !!!!" );
//        mNotificationManager = (NotificationManager) NotificationExtender.this.getSystemService(Context.NOTIFICATION_SERVICE);

        OverrideSettings overrideSettings = new OverrideSettings();

        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {

                String notificationID = null;
                if(notification.payload != null) {
                    JSONObject data = notification.payload.additionalData;
                    try {
                        notificationID = data.getString("action");
                    } catch (JSONException jsonEx) {
                        jsonEx.printStackTrace();
                    }
                }

//                if (Build.VERSION.SDK_INT >= 23) {
//
//                    StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
//                    for (StatusBarNotification sbNotification : notifications) {
//
//                        Notification notification1 = sbNotification.getNotification();
//
//                        if (notification1 != null && notification1.getGroup() != null && notification1.getGroup().equalsIgnoreCase(notificationID)) {
//                            mNotificationManager.cancel(sbNotification.getId());
//                            notification1.deleteIntent
//                        }
//                    }
//
//                }

                UserProperties properties = null;
                Bitmap bigIcon = null;
                String ringPath = null;
                User user = User.createUserFromSharedPreferences(getApplicationContext(), true);//ConnectedUser.getInstance();
                String groupName = "";
                if(user != null) properties = user.getProperties();
                if(properties != null) {
                    if (notificationID != null && notificationID.matches(RunWithMiriApplication.TRAINING)) {
                        ringPath = properties.getTrainingReminderSoundPath();
                        bigIcon = BitmapFactory.decodeResource(getResources(), R.drawable.training_message_icon);
                        groupName = "training";
                        if(notification.payload != null) {
                            notification.payload.notificationID = "trainingID";
                        }
                        Log.i("OneSignalExample", ".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-..-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-training");

                    } else if (notificationID != null && notificationID.matches(RunWithMiriApplication.EXERCISE)) {
                        ringPath = properties.getExerciseReminderSoundPath();
                        bigIcon = BitmapFactory.decodeResource(getResources(), R.drawable.exercise_message_icon);
                        groupName = "exercise";
                        if(notification.payload != null) {
                            notification.payload.notificationID = "exerciseID";
                        }
                        Log.i("OneSignalExample", ".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-..-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-exercise");
                    } else if (notificationID != null && notificationID.matches("water")) {
                        ringPath = properties.getWaterReminderSoundPath();
                        bigIcon = BitmapFactory.decodeResource(getResources(), R.drawable.water_message_icon);
                        groupName = "water";
                        if(notification.payload != null) {
                            notification.payload.notificationID = "waterID";
                        }
                        Log.i("OneSignalExample", ".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-..-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-water");
                    }
                }

                if (notificationID != null && notificationID.matches(RunWithMiriApplication.PROGRAM_PURCHASE)) {

//                    //todo remove from Notification? Maybe just change text



                    //download updated user data
                    if(user != null) {
                        NetworkCenterHelper.getConnectedUser(NotificationExtender.this, user, new GetResponseCallback() {
                            @Override
                            public void requestStarted() {

                            }

                            @Override
                            public void requestCompleted(String response) {
                                try {
                                    JSONObject jsonUser = new JSONObject(response);
                                    final User localUser = new User(jsonUser, NotificationExtender.this);
                                    User.savePracticePlanDataLocally(NotificationExtender.this, localUser);
                                    if(NotificationExtender.this.getApplication() != null && NotificationExtender.this.getApplication() instanceof RunWithMiriApplication) {
                                        //((RunWithMiriApplication) NotificationExtender.this.getApplication()).setConnectedUser(localUser);
                                        ((RunWithMiriApplication) NotificationExtender.this.getApplication()).updateConnectedUserPlanProgram(localUser);
                                    }

                                    new DownloadPracticeTask(NotificationExtender.this, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Integer.valueOf(localUser.getPracticeNum()), Integer.valueOf(localUser.getProgramId()));
//
//                                    Intent intent = new Intent(NotificationExtender.this, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //todo
                                } catch (UserException e) {
                                    e.printStackTrace();
                                    //todo
                                }
                            }

                            @Override
                            public void requestEndedWithError(VolleyError error) {
                                if (error != null) error.printStackTrace();
                            }
                        });
                    }
                    Log.i("OneSignalExample", ".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-..-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.- PURCHASE DONE!!!!!");

                    builder.setTimeoutAfter(1);
                    builder.setAutoCancel(true);
                    return null;

                } else {
                    builder.setGroup(groupName);
                    builder.setGroupSummary(true);
                    builder.setGroupAlertBehavior(NotificationCompat.FLAG_GROUP_SUMMARY);
                    builder.setAutoCancel(true);

                    if (bigIcon != null) {
                        builder.setLargeIcon(bigIcon);
                    }
                    //notification.payload.groupKey = groupName;
                    if (ringPath != null) {
                        builder.setDefaults(builder.build().flags &= ~Notification.DEFAULT_SOUND);
                        try {
                            builder.setSound(Uri.parse(ringPath));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                user = null;

                return builder;

            }


        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);

        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

        return true;


        //return false;// Return true to stop the notification from displaying.
    }

}
