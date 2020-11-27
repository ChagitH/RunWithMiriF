package com.forst.miri.runwithme.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.fragments.SettingsFragment;
import com.forst.miri.runwithme.miscellaneous.LocationFilter.FilteredLocation;
import com.forst.miri.runwithme.miscellaneous.LocationFilter.FilteredLocationManager;
import com.forst.miri.runwithme.miscellaneous.PlayAudioTask;
import com.forst.miri.runwithme.miscellaneous.Sensors.TemperatureSensor;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.GeneralAudio;
import com.forst.miri.runwithme.objects.Logger;
import com.forst.miri.runwithme.objects.PersistentPracticeData;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.Rate;
import com.forst.miri.runwithme.objects.User;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by chagithazani on 11/28/17.
 */

public class LocationService extends Service implements /*GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, */OnDataPointListener, FilteredLocationManager.internalLocationMessageCallback { //} implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String NOTIFICATION_CHANNEL_ID = "NO_CH_ID";
    public static final String NOTIFICATION_FROM_SERVICE ="Not_from_loc_ser";
    private static final long MINIMUM_TIME_CONSIDERED_AS_FULL_TRAINING = 1000 * 60 * 10; // 10 minutes



//    private static float LOCATION_ACCURACY_TOLERANCEY = 10;//  //version 36 was with 5 - not sure it was good. 22.4.18 changing to 10



    private static int FOREGROUND_ID = 9654;
    private String TAG = LocationService.class.getSimpleName();

    private Boolean mIsPaused = false;
    private FilteredLocation mFilteredLatLong = null;
    public static final int MIN_LOCATIONS_FOR_STARTING = 6;
    private static final float Q = 6;
//    private int locationsCounter = 0;

    private PowerManager.WakeLock mWakeLock;
    private ArrayList<UpdateCallback> updateCallbackArray = null;
    //private PracticeData mPracticeData = null;
    IBinder mBinder = new LocalBinder();
    // Flag that indicates if a request is underway.
    private boolean mInProgress, mFirstLocationMaybeOld = true;
    private Location mPreviousLocation = null;
    private PersistentPracticeData mPerssistantPracticeData;
    private Practice mPractice = null;
    private Map<Float, String> practiceAudio = null;
    private PlayAudioTask playAudioTask = null;
    private ArrayList<PlayAudioTask> audioTasks = null;
    private ArrayDeque<Float> deque = null;

    //13.12.08 add voice every 0.5KM
    private Map<Float, String> routineAudio = null;
//    private PlayAudioTask playRoutineAudioTask = null;
//    private ArrayDeque<Float> routineDeque = null;


    private TemperatureSensor tempSensor;

    //4.12.18 moved from RunFragmentWithService by Chagit
    public GoogleApiClient mGoogleApiClient;
//    public static final int REQUEST_OAUTH = 1001;

    //todo =========================================================================================
    //private LocationManager locationManager = null;
    //private KalmanLocationManager kalmanLocationManager;
//    private LocationListener locationListener = null;
//    private FilteredLocationManager filteredLocationManager = null;
    //todo =========================================================================================

    private boolean firstTime = true;
//    private boolean runActuallyStarted = false;
    private long startTimeStamp = -1;
    private long mDurationFromTimer = -1;
    private Timer mTimer = null;
    User mUser = null;
    private Practice.PracticeType mPracticeType = null;




    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }


    public interface UpdateCallback{
        public void update(long duration, long rate, float distance);
        public void reportError(String error);
        public void countDown(String count);
        public void stopAnimation();
        public void updateDuration(long durationFromTimer);
    }

//    public interface PracticeDataCallback{
//        public void practiceDataReady(PracticeData practiceData);
//    }

    public void addUpdateCallback(UpdateCallback callback){
        if(updateCallbackArray == null){
            updateCallbackArray = new ArrayList<>();
        }
        updateCallbackArray.add(callback);
    }

    public void removeUpdateCallback(UpdateCallback callback){
        if(updateCallbackArray != null){
            updateCallbackArray.remove(callback);
        }
    }


    public LocationService() {
    }



    @Override
    public void onCreate() {
        super.onCreate();

        mUser = User.createUserFromSharedPreferences(LocationService.this, true);
        mInProgress = false;

        int planNum = 0;
        if(mPractice != null) {
//            User user = User.createUserFromSharedPreferences(this, true);
            if (mUser != null) planNum = mUser.getProgramId();

        }

        mPerssistantPracticeData = new PersistentPracticeData(mPractice != null ? mPractice.getPracticeNum() : 0 , planNum ,  -100,  this);

        tempSensor = new TemperatureSensor(this, new TemperatureSensor.TemperatureListener() {
            @Override
            public void tempChanged(float temp) {
                mPerssistantPracticeData.setTemperature(temp);
                tempSensor.destroyTemperatureSensor();
                tempSensor = null;

                final String cDEGREE  = " \u2103";
                Log.d(getClass().getName(), " Temp = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = " + temp +  cDEGREE);
            }
        });

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
//        Log.i(TAG , " ---- - - - - - - - - - onStartCommand #1");
//        Log.i(TAG , " ---- - - - - - - - - - checking mPerssistantPracticeData.time = " + mPerssistantPracticeData.getDuration());

        Crashlytics.log(Log.INFO,"LocationService","onStartCommand 1 ");
        Crashlytics.logException(new Throwable("LocationService onStartCommand 1"));


        if(intent == null) stopSelf();



        startForeground(FOREGROUND_ID,getNotification());

        //timer
        startTimeStamp = System.currentTimeMillis();
        startTimer();

        Bundle extras = intent != null ? intent.getExtras() : null;
        if (extras != null) {
            mPractice = (Practice)extras.getSerializable(Practice.PRACTICE_KEY);
        }

        /*
        if part of plan or wants to run WITH audio time , must register to listen to changes, to play lesson audio
         */

        audioTasks = new ArrayList<>();
        if(mPractice != null) {

            mPerssistantPracticeData.setPracticeNum(mPractice.getPracticeNum());
            int planNum = 0;
            User user = User.createUserFromSharedPreferences(this, true);
            if (user != null) planNum = user.getProgramId();
            mPerssistantPracticeData.setPracticePlan(planNum);
            practiceAudio = mPractice.getDelaysAndAudioUrls();
            mPracticeType = mPractice.getPracticeType();

        } else if( SettingsFragment.getListenToAudioDuringRun(this)){
            practiceAudio = GeneralAudio.getDelaysAndAudioUrls(this);
        }

        if(practiceAudio != null) {
            SortedSet<Float> keys = new TreeSet<Float>(practiceAudio.keySet());
            deque = new ArrayDeque<Float>(keys.size());
            deque.addAll(keys);



            addUpdateCallback(new UpdateCallback() {

                /*
                 this method will be called only if this run is part of practice
                 if practice is practice by time, will stop listening to updates.
                */
//                Float key = null;


                @Override
                public void update(long duration, long rate, float distance) {
                    updateAudioListener(Practice.PracticeType.DISTANCE, 0);
                }

                @Override
                public void reportError(String error) {
                    //do nothing
                }

                @Override
                public void countDown(String count) {
                    //do nothing
                }

                @Override
                public void stopAnimation() {

                }

                @Override
                public void updateDuration(long durationFromTimer) {
                    updateAudioListener(Practice.PracticeType.TIME, durationFromTimer);
                }
            });
        }


        Log.i(TAG , " ---- - - - - - - - - - onStartCommand # 2");

        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);

        /*
        WakeLock is reference counted so we don't want to create multiple WakeLocks. So do a check before initializing and acquiring.
        This will fix the "java.lang.Exception: WakeLock finalized while still held: MyWakeLock" error that you may find.
        */
        if (this.mWakeLock == null) {
            this.mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock:");
        }

        if (!this.mWakeLock.isHeld()) {
            this.mWakeLock.acquire();
        }

        /* REMOVED by Guy 8/11/2018
        if(!servicesConnected() || mGoogleApiClient.isConnected())//todo removed on 14.3.2018 || mInProgress)
            return START_STICKY;
        */

        /* REMOVED by Guy 9/11/2018
        setUpLocationClientIfNeeded();//todo removed on 14.3.2018
        if(!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress){
            mInProgress = true;
            mGoogleApiClient.connect();
        }*///todo removed on 14.3.2018


        //startLocationUpdates();

        Crashlytics.log(Log.INFO,"LocationService","onStartCommand ** Before connecting to Google Client ** timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));
        Crashlytics.logException(new Throwable("LocationService onStartCommand ** Before connecting to Google Client ** timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())));
        Logger.log("LocationService onStartCommand ** Before connecting to Google Client ** timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)  // Required for SensorsApi calls
                // Optional: specify more APIs used with additional calls to addApi
                .useDefaultAccount()
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();


//4.12.18 moved to onConnected
//        // Connected to Google Fit Client.
        Fitness.SensorsApi.add(
                mGoogleApiClient,
                new SensorRequest.Builder()
                        //.setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                        //.setDataType(DataType.TYPE_DISTANCE_DELTA)
                        //.setDataType(DataType.TYPE_LOCATION_TRACK)
                        .setAccuracyMode(SensorRequest.ACCURACY_MODE_HIGH)
                        .setSamplingRate(1, TimeUnit.SECONDS)  //2.12.18 Chagit changed to 1 sec until onDataPoint will be called the second time.
                        .build(), this);

        if (mFilteredLatLong == null) {
            SharedPreferences sp = getSharedPreferences(MainActivity.MIN_ACCURACY_TAG, Context.MODE_PRIVATE);
            int minAccuracy = sp.getInt(MainActivity.MIN_ACCURACY_TAG, 100);
            Logger.log( "LocationService -------- minAccuracy = " + minAccuracy);
            mFilteredLatLong = new FilteredLocation(Q, minAccuracy);
        }

//        Crashlytics.log(Log.INFO,"LocationService","onStartCommand 2 timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));
//        Crashlytics.logException(new Throwable("LocationService onStartCommand 2 timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())));
        Logger.log("LocationService onStartCommand onStartCommand 2 timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));

        return START_STICKY;

    }

    private Float key = null;

    private void updateAudioListener(Practice.PracticeType timeOrDistanceUpdate, long durationFromTimer){
        if (firstTime) {
            firstTime = false;
            key = deque.removeFirst();
            playRecordingAsynchronous(practiceAudio.get(key));
        } else {
            if (!deque.isEmpty()) { // deque / lesson not over yet
                /*
                mPractice != null because if mPractice == null it means audio strips need to go by distance
                can remove because added in Practice.getPracticeType that 0 is DISTANCE
                 */
//                if (mPractice != null && Practice.getPracticeType(mPerssistantPracticeData.getPracticeNum()) == Practice.PracticeType.TIME ) {
                if (mPracticeType != null && mPracticeType == Practice.PracticeType.TIME ) {
                    if(timeOrDistanceUpdate == Practice.PracticeType.TIME) {
                        float whenToPlayAudioInMinutes = deque.peekFirst();
                        float whenToPlayAudioInMillies = getMillisFromMinutes(whenToPlayAudioInMinutes);
                        Log.i(TAG, " ---------- time in millies mPerssistantPracticeData.getDuration() = " + mPerssistantPracticeData.getDuration() + "---------- time in millies durationFromTimer = " + durationFromTimer + " ----------- whenToPlayAudioInMillies = " + whenToPlayAudioInMillies + " ----------- deque.size() = " + deque.size());
                        Logger.log("LocationService.updateAudioListener() ---------- time in millies ppd.getDuration() = " + mPerssistantPracticeData.getDuration()  + " ----------- whenToPlayAudioInMillies = " + whenToPlayAudioInMillies + " ----------- deque.size() = " + deque.size());

//                    if (mPerssistantPracticeData.getDuration() >= whenToPlayAudioInMillies) { //reached the point to play the recording
                        if (durationFromTimer >= whenToPlayAudioInMillies) { //reached the point to play the recording
                            key = deque.removeFirst();
                            playRecordingAsynchronous(practiceAudio.get(key));
                        } // nothing to do if point not reached yet
                    }
                } else if(timeOrDistanceUpdate == Practice.PracticeType.DISTANCE){
                    float whenToPlayAudioInKm = deque.peekFirst();
                    float whenToPlayAudioInMeters = whenToPlayAudioInKm * 1000;
                    Logger.log("LocationService.updateAudioListener() ---------- distanceInMeters = " + mPerssistantPracticeData.getDistance() + " ----------- whenToPlayAudioInMeters = " + whenToPlayAudioInMeters + " ----------- deque.size() = " + deque.size());
                    Log.i(TAG, " ---------- distanceInMeters = " + mPerssistantPracticeData.getDistance() + " ----------- whenToPlayAudioInMeters = " + whenToPlayAudioInMeters + " ----------- deque.size() = " + deque.size());
                    if (mPerssistantPracticeData.getDistance() >= whenToPlayAudioInMeters) { //reached the point to play the recording
                        key = deque.removeFirst();
                        playRecordingAsynchronous(practiceAudio.get(key));
                    } // nothing to do if point not reached yet
                }
            }


        }
    }

    private int dataPointCounter = 0;
    // Added by Guy . 31w/10/2018
    @Override
    public void onDataPoint(DataPoint dataPoint) {

//        Crashlytics.log(Log.INFO,"LocationService","onDataPoint() timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));
//        Crashlytics.logException(new Throwable("LocationService onDataPoint() timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())));
        Logger.log("LocationService onDataPoint() paused = " + mIsPaused + " timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));

        if (mIsPaused) return;

        if(dataPointCounter <= 5){
            Logger.log("LocationService onDataPoint() dataPointCounter = " + dataPointCounter);// from 0.17
            if(dataPointCounter == 5){ //second location - change sample rate and continue. will be used as previous location
                Fitness.SensorsApi.remove(mGoogleApiClient, this);
                Fitness.SensorsApi.add(
                        mGoogleApiClient,
                        new SensorRequest.Builder()
                                //.setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                                //.setDataType(DataType.TYPE_DISTANCE_DELTA)
                                //.setDataType(DataType.TYPE_LOCATION_TRACK)
                                .setAccuracyMode(SensorRequest.ACCURACY_MODE_HIGH)
                                .setSamplingRate(5, TimeUnit.SECONDS)  // sample once per 5 seconds
                                .build(), this);
                dataPointCounter++;
            } else {
                dataPointCounter++;

                //ignore first location
                if(dataPointCounter == 1) {
                    if (this.updateCallbackArray != null) {
                        synchronized (updateCallbackArray) {
                            for (UpdateCallback callback : updateCallbackArray) {
                                callback.stopAnimation();
                            }
                        }
                    }
                    return;
                }
            }
        }


        try {
            Location l = new Location("GPS");
            l.setLongitude(dataPoint.getValue(Field.FIELD_LONGITUDE).asFloat());
            l.setLatitude(dataPoint.getValue(Field.FIELD_LATITUDE).asFloat());
            l.setAccuracy(dataPoint.getValue(Field.FIELD_ACCURACY).asFloat());
            l.setTime(System.currentTimeMillis());

            //Logger.log(" OnDataPoint-l-Long: " + l.getLongitude() +  " OnDataPoint-l-Lat: "+l.getLatitude() + " OnDataPoint-l-Accuracy: "+l.getAccuracy());


            Location filteredLocation = mFilteredLatLong.process(l);

            if (filteredLocation != null) {

                //Logger.log(this, " After Smoothing : onDataPoint  " + " OnDataPoint-Long: " + filteredLocation.getLongitude() + " OnDataPoint-Lat: " + filteredLocation.getLatitude()+ " OnDataPoint-Acc: " + filteredLocation.getAccuracy());

                saveAndProcessLocation(filteredLocation);

            } else {
                Crashlytics.logException(new Throwable("onDataPoint filtered location is NULL! "));
                Logger.log("onDataPoint filtered location is NULL!");
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Logger.log("LocationService onDataPoint() EXCEPTION! " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()) + e.getMessage());
        }
        //Log.d("DataPoint-Distance", dataPoint.getValue(Field.FIELD_DISTANCE).asString());
        // Log.d("DataPoint-Steps", dataPoint.getValue(Field.FIELD_STEPS).asString());
    }




    private long getMillisFromMinutes(Float minutes){
        long wholeMinutes = (int) (minutes / 1);
        double afterPointMinutes = (double)(minutes % 1);
        //double ratio = ((double)afterPointMinutes/(double)60);
        //double newAfterPonitMinutes = afterPointMinutes * ratio;
        double numOfSecs = afterPointMinutes * 100;
        Log.d(TAG, " Calculate minutes: minutes = " + minutes + " wholeMinutes = " + wholeMinutes + " afterPointMinutes = " + afterPointMinutes + " numOfSecs = " + numOfSecs);
        long milliesFromMins = wholeMinutes * 60000;
        double milliesFromSecs = numOfSecs * 1000;
        return (long) milliesFromSecs + milliesFromMins;
    }

    private void playRecordingAsynchronous(String url) {
        if(deque.isEmpty() && mPractice != null) mPerssistantPracticeData.setPracticeEnded(true);
        Log.d(getClass().getName() , " playRecordingAsynchronous url = " + url + " ----------------------------------------------------------------------------------------" );
        //if(playAudioTask == null) {
        if(audioTasks == null){
            audioTasks = new ArrayList<>();
        }



        playAudioTask = new PlayAudioTask(getApplicationContext(), new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(getClass().getName() , " ---------- on Compleation . deque.isEmpty() =  " + deque.isEmpty() + " ----------------------------------------------------------------------------------------" );
                if (deque.isEmpty()) {
                    if(mPractice != null) {
                        mPerssistantPracticeData.setPracticeEnded(true);
//                        User user = User.createUserFromSharedPreferences(LocationService.this, true);
                        if(mUser != null && !mUser.isInTrial()) {
                            Logger.log("LocationService.playRecordingAsynchronous().MediaPlayer.OnCompletionListener() Practice number " + mPerssistantPracticeData.getPracticeNum() + " will be deleted");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Practice.eraseLocally(getApplicationContext());//, mPractice.getPracticeNum()); 27.3.018
                                }
                            }).start();
                        }

                    }
                }
                playAudioTask.cancel(true);
            }
        });

        audioTasks.add(playAudioTask);
        Log.i("LocationService", " *- *- *- *- *- *- *- *- *- *- *- *- added to audioTasks. Size =  = " + audioTasks.size());
        playAudioTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }




    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void resume(){
        if(playAudioTask != null) playAudioTask.resumeAudio();
        //startLocationUpdates();
        startTimer();
        resumeLocationUpdates();
    }

    public void pause(){
        if(playAudioTask != null) playAudioTask.pauseAudio();
        stopTimer();
        pauseLocationUpdates();
    }

    private void startTimer(){

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //durationFromTimer = System.currentTimeMillis() - startTimeStamp;
                mDurationFromTimer += 1000;
                mPerssistantPracticeData.setDuration(mDurationFromTimer);
                Log.i("TIMER ** TIMER **", " *-*-*-*-*-*-*-*-*-*-*-*- durationFromTimer = " + mDurationFromTimer);
                if (updateCallbackArray != null) {
                    synchronized (updateCallbackArray) {
                        for (UpdateCallback callback : updateCallbackArray) {
                            callback.updateDuration(mDurationFromTimer);
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    private void stopTimer(){
        if(mTimer != null){
            try {
                mTimer.cancel();
                mTimer = null;
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public void resumeLocationUpdates() {
        mIsPaused = false;

    }

    public void pauseLocationUpdates() {

        mIsPaused = true;

        mPreviousLocation = null;


    }



    public void stop(){
        if(playAudioTask != null)  playAudioTask.stopAudio();

        Log.d(getClass().getName(), " IS practice == null && deque is empty ???????????????????????????");
        //if (mPractice != null && deque.isEmpty()) { removed on 12.7.18 Miri asked to do so by phone
        if (mPractice != null && mPerssistantPracticeData.getDuration() >= MINIMUM_TIME_CONSIDERED_AS_FULL_TRAINING) {//added on 12.7.18 Miri asked to do so by phone
            mPerssistantPracticeData.setPracticeEnded(true);
            User user = User.createUserFromSharedPreferences(getApplicationContext(), true);
            if (user != null && !user.isInTrial()) {
                //added on 19.8.2018 to avoid practice download problems
                user.setPracticeNum(getApplicationContext(), mPerssistantPracticeData.getPracticeNum() + 1);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Practice.eraseLocally(getApplicationContext());

                    }
                }).start();
            }
        }

        Crashlytics.log(Log.WARN,"LocationLOG","STOP");
        Crashlytics.logException(new Throwable("stop()"));
        if(mGoogleApiClient != null)  {
            Fitness.SensorsApi.remove( mGoogleApiClient, this )
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                mGoogleApiClient.disconnect();
                                if(LocationService.this != null) Logger.log("LocationService.stop() googleClientApi disconnected !");
                            }
                        }
                    });
//            mGoogleApiClient.disconnect();
        }

        boolean someThreadsNotCanceled = true;
        while(someThreadsNotCanceled){
            someThreadsNotCanceled = false;
            for (PlayAudioTask audioTask : audioTasks){
                Log.i("LocationService", " in stop() - audioTask = " + audioTask.toString());
                if( ! audioTask.isCancelled()) {
                    Log.i("LocationService", " in stop() - audioTask NOT canceled = "+ audioTask.toString());
                    audioTask.cancel(true);
                    someThreadsNotCanceled = true;
                }
            }
        }
        //stopLocationUpdates();
    }



    private void saveAndProcessLocation(Location location){
        if (mPreviousLocation == null) {
            mPreviousLocation = location;
        }
        float sectionDistance = location.distanceTo(mPreviousLocation);
        //if(sectionDistance > 0){ // user is in movement
        mPerssistantPracticeData.addLocation(location);
        long sectionDuration = location.getTime() - mPreviousLocation.getTime();
//        Logger.log("LocationService.saveAndProcessLocation() sectionDuration = " + sectionDuration);
        Logger.log("LocationService.saveAndProcessLocation() sectionDistance = " + sectionDistance);


        //mPerssistantPracticeData.addToDuration(sectionDuration);


        mPerssistantPracticeData.addToDistance(sectionDistance);
        Rate rate = new Rate(mPerssistantPracticeData.getDistance(), location.getTime(), sectionDuration, sectionDistance);
        mPerssistantPracticeData.addRate(rate);

        mPreviousLocation = location;


        if (this.updateCallbackArray != null) {
            synchronized (updateCallbackArray) {
                for (UpdateCallback callback : updateCallbackArray) {
                    callback.update(mPerssistantPracticeData.getDuration(), rate.getRateInMillies(), mPerssistantPracticeData.getDistance());
                }
            }
        }
        //}
    }


    @Override
    public void sendMessage(String message) {
        if (this.updateCallbackArray != null) {
            synchronized (updateCallbackArray) {
                for (UpdateCallback callback : updateCallbackArray) {
                    callback.reportError(message);
                }
            }
        }
    }

    @Override
    public void sendCountDown(String count) {
        if (this.updateCallbackArray != null) {
            synchronized (updateCallbackArray) {
                for (UpdateCallback callback : updateCallbackArray) {
                    callback.countDown(count);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Service Destroyed! # 1");

        stopForeground(true);

        stop(); //instead of stopLocationUpdates();
        stopTimer();


        if (this.mWakeLock != null) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }


        if(updateCallbackArray != null){
            for(UpdateCallback callback : updateCallbackArray){
                callback = null;
            }
        }
        super.onDestroy();

        Log.v(TAG, "Service Destroyed! # 2  - - - THE END ! - - - ");
    }

    public Notification getNotification()  {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//  | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,  notificationIntent, 0);


        Notification notification;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.training_message_icon);

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //This only needs to be run on Devices on Android O and above
            NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = getString(R.string.app_name); //user visible
            String description = getString(R.string.out_on_a_run_heb); //user visible
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{0, 1000});
            mNotificationManager.createNotificationChannel(mChannel);

            notification = new Notification.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.out_on_a_run_heb))
                    .setTicker(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
        } else {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.out_on_a_run_heb))
                    .setTicker(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

        }

        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;


        return notification;
    }

}