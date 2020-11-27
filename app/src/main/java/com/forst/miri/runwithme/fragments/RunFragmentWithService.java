package com.forst.miri.runwithme.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.PracticeDataTempStorageHelper;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Logger;
import com.forst.miri.runwithme.objects.PersistentPracticeData;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.services.DownloadPracticeService;
import com.forst.miri.runwithme.services.LocationService;
import com.forst.miri.runwithme.services.PracticeDownloadJobService;
import com.forst.miri.runwithme.services.PracticeSaveDataJobService;
import com.forst.miri.runwithme.services.PracticeSaveDataService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

// JUST FOR GIT.
// 007


public class RunFragmentWithService extends ParentFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FRAGMENT_TAG = RunFragmentWithService.class.getSimpleName();


    public static final String IS_RUNNING_WITH_MIRI_KEY = "is_running_with_miri"; //}, PracticeDataCreatedCallback {
    public static final String IS_RUNNING_DIALOG_OPEN = "is_running_dialog_open";
    private static final String DID_PRESS_START_RUNNING = "already_started_runing";
    //private static final String CONTEXT = "CONTEXT";
    private static final String WHILE_RUNNING_DIALOG = "while_running_di";
    private static final String TASK_TAG_API_19_SCHDULED_SAVE_TO_SERVER = "task_scheduled_save_data_api_19";
    private static final String TASK_TAG_API_19_SCHDULED_GET_ELEVATION_DATA = "task_scheduled_get_elevation_data_api_19";
    private static final String TASK_TAG_API_19_SCHDULED_DOWNLOAD_FROM_SERVER = "task_scheduled_download_practice_api_19";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9987;
    //private static final String WHILE_RUNNING_FRAGMENT = ;


    public static int PRACTICE_ELEVATION_DATA_JOB_SERVICE_ID = 9101;
    public static int PRACTICE_SAVE_DATA_JOB_SERVICE_ID = 9102;
    public static int PRACTICE_DOWNLOAD_JOB_SERVICE_ID = 9103;
    public static final String ACTIVITY_KEY = "activity_for_google_fit_key";

    public static GoogleApiClient mGoogleApiClient;
    public static final int REQUEST_OAUTH = 1001;


    //private GoogleApiClient mGoogleApiClient;
    //private boolean googleClientConnected = false;

    public static int ACCESS_FINE_LOCATION_REQUEST_CODE = 111;
    public static int GET_LOCATION_REQUEST_CODE = 222;

    private boolean runningWithMiri = false;
    private boolean pressedStartToRun = false;
    private Button bStartRunWithMiri, bStartRunAlone;
    private LinearLayout bBack;
    private Practice mPractice = null;
    private CircleImageView circleImageView;
    private TextView tvHello , tvTimeToStart, tvPracticeNum;
    private Context mContext;
    private Dialog workingDialog = null;
    //private PracticeData mPracticeData = null;
    private boolean isRunDialogOpen = false;


    public RunFragmentWithService() {        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.run_with_miri_run_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null && !savedInstanceState.isEmpty()){
            //this.mPractice = (Practice)savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
            runningWithMiri = savedInstanceState.getBoolean(IS_RUNNING_WITH_MIRI_KEY);
            isRunDialogOpen = savedInstanceState.getBoolean(IS_RUNNING_DIALOG_OPEN);
            pressedStartToRun = savedInstanceState.getBoolean(DID_PRESS_START_RUNNING);
        } else {
            Bundle bundle = getArguments();
            if(bundle != null) {
                boolean serviceRunning = bundle.getBoolean(MainActivity.SERVICE_IS_RUNNING);
                if(serviceRunning){
                    startAnimation();
                }
//                else {
//                    this.mPractice = (Practice) bundle.getSerializable(Practice.PRACTICE_KEY);
//                    //runningWithMiri = bundle.getBoolean(IS_RUNNING_WITH_MIRI_KEY);
//                }
            }
        }

        mPractice = Practice.createFromSharedPreferences(getContext());

        getRuntimePermissions();

        View rootView;
        rootView = inflater.inflate(R.layout.run_with_miri_run_layout, container, false);
        bStartRunWithMiri = (Button) rootView.findViewById(R.id.runwith_fragment_button_start_run_with_miri);
//        bStartRunWithMiri.setShadowLayer();
        bStartRunAlone = (Button) rootView.findViewById(R.id.runwith_fragment_button_start_run_alone);
        tvPracticeNum = (TextView) rootView.findViewById(R.id.runwith_fragment_train_num_textview);



        tvHello = (TextView) rootView.findViewById(R.id.run_fragment_tv_hello);

        tvTimeToStart = (TextView) rootView.findViewById(R.id.run_fragment_tv_time_to_start);


        circleImageView = (CircleImageView) rootView.findViewById(R.id.run_fragment_small_image_circle);

        bBack = (LinearLayout) rootView.findViewById(R.id.run_run_button_back);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        outState.putBoolean(IS_RUNNING_WITH_MIRI_KEY, runningWithMiri);
        outState.putBoolean(IS_RUNNING_DIALOG_OPEN, isRunDialogOpen);
        outState.putBoolean(DID_PRESS_START_RUNNING, pressedStartToRun);
        //outState.putParcelable(WHILE_RUNNING_DIALOG, whileRunDialogFragment);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mPractice != null){
            updateFragmentWithPractice(mPractice);
        } else {
            tvPracticeNum.setText(getString(R.string.no_practice_ready_heb));
        }


        bStartRunWithMiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runWithMiri();
            }
        });

        bStartRunAlone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAlone();
            }
        });


        String textTimeToStart = getString(R.string.time_to_start_the_practice_heb);
        tvTimeToStart.setText(textTimeToStart);

        if(ConnectedUser.getInstance() != null) {

            String textHello = UIHelper.getGreeting(mContext) + " " + ConnectedUser.getInstance().getFirstName();
            tvHello.setText(textHello);

            if (ConnectedUser.getInstance().getImage() != null) {
                circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
            } else {
                circleImageView.setVisibility(View.INVISIBLE);
            }
        }

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        setRetainInstance(true);

//        if(isRunDialogOpen){
//            startAnimation();
//        }
    }

    private void back() {
        ((MainActivity)getMyActivity()).onBackPressed();
    }


    private void updateFragmentWithPractice(Practice practice) {
        tvPracticeNum.setText(getString(R.string.practice_heb) + " " + String.valueOf(practice.getPracticeNum()));
    }

    private void noRuntimePermmision(String permmision){
        StringBuilder sb = new StringBuilder();
        sb.append(permmision).append("\n").append(getString(R.string.please_permit_heb));
        Dialogs.showYesNoErrorDialog(mContext, sb.toString(), new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Integer tag = (Integer)view.getTag();
                if(tag == Dialogs.YES){
                    getRuntimePermissions();
                } else {

                }
            }
        }).show();

    }

    private void runWithMiri() {
        //runningWithMiri = true;
        if (ConnectedUser.getInstance().isInTrial()){
        //if (!ConnectedUser.getInstance().IsRegisteredToRunWithMiri()){
            Dialogs.getAlertDialog(getMyActivity(), getString(R.string.you_are_not_registered_to_run_with_miri_please_register), getString(R.string.run_with_miri), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startRunning(true);
                }
            }).show();
            //return;
        } else {
            startRunning(true);
        }

    }

    private void runAlone() {
        //runningWithMiri = false;
        startRunning(false);
    }


    private void startRunning(boolean wantToRunWithMiri){
        if (pressedStartToRun == true) return;
        pressedStartToRun = true;
        runningWithMiri = wantToRunWithMiri;

        if (!isLocationPermissionGranted()) {
            Crashlytics.log(Log.INFO,"isLocationPermissionGranted","NO");
            noRuntimePermmision(getString(R.string.no_location_permit_heb));
            pressedStartToRun = false;
            return;
        }


        if (!isLocationServiceOn()) {
            Crashlytics.log(Log.INFO,"isLocationServiceOn","NO");
            Dialogs.getAlertDialog(getMyActivity(), getString(R.string.location_service_not_open), null , new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).show();
            pressedStartToRun = false;
            return;
        }

        if (!servicesConnected()) {
            Crashlytics.log(Log.INFO,"isGooglePlayServiceConnected","NO");
            Dialogs.getAlertDialog(getMyActivity(), getString(R.string.google_play_services_not_updated_heb), getString(R.string.sorry_heb)).show();
            pressedStartToRun = false;
            return;
        }

//        if (wantToRunWithMiri && !ConnectedUser.getInstance().IsRegisteredToRunWithMiri()){
//            Dialogs.getAlertDialog(getMyActivity(), getString(R.string.you_are_not_registered_to_run_with_miri_please_register), getString(R.string.sorry_heb) ).show();
//            //((MainActivity)getMyActivity()).replaceFragment(TrainingPlanRegistrationFragment.FRAGMENT_TAG, null, false);//R.layout.fragment_practice_plan_registration, null);
//            return;
//        }

        if(wantToRunWithMiri && ! Practice.isNextLessonNum(mContext, ConnectedUser.getInstance().getPracticeNum())){
            Dialogs.getAlertDialog(getMyActivity(), getString(R.string.voice_was_not_downloaded_try_later_heb), getString(R.string.sorry_heb)).show();
            pressedStartToRun = false;
            return;
        }



        StringBuilder sb = new StringBuilder();
        try {

            sb.append(getString(R.string.name_heb) + " ");
            sb.append(ConnectedUser.getInstance().getFirstName() + " " + ConnectedUser.getInstance().getLastName());
            sb.append(" \n");
            sb.append("\nPhone model: ").append(Build.MANUFACTURER).append(" ").append(android.os.Build.MODEL);
            sb.append("\nAndroid version: ").append(Build.VERSION.SDK_INT);
            sb.append("\nApp version: ").append(BuildConfig.VERSION_NAME);
            sb.append(" \n");

        }catch(Exception ex){
            ex.printStackTrace();
            Crashlytics.logException(ex);
        }

        Logger.deleteLog(getContext());
        Logger.log(sb.toString());
        // Change by Guy . 31/10/2018
        // REMOVED CODE FOR CALLING HAGIT SERVICE AND ADDED GOOGLE FITNESS

        /*
        //turnOnLocationRequest();
        Intent intent = new Intent(getContext(), LocationService.class);
        //intent.putExtra(Practice.NUM_OF_PRACTICE_KEY, getLessonNum());
        if(runningWithMiri && mPractice != null) {
            intent.putExtra(Practice.PRACTICE_KEY, mPractice);
        }
        getActivity().getApplicationContext().startService(intent);
        */

//        Intent intent = new Intent(getContext(), LocationService.class);
//        //intent.putExtra(Practice.NUM_OF_PRACTICE_KEY, getLessonNum());
//        if(runningWithMiri && mPractice != null) {
//            intent.putExtra(Practice.PRACTICE_KEY, mPractice);
//        }
//
////        intent.putExtra(ACTIVITY_KEY, getActivity());
//        if(getActivity() != null && getActivity().getApplicationContext() != null) {
//            getActivity().getApplicationContext().startService(intent);
//
//            Crashlytics.log(Log.INFO, "LocationLOG-onConnected", "Start Running");
//        }

        //Crashlytics.log(Log.WARN,"startRunning","GoogleFitness: client connect");

        //4.12.18 moved by Chagit to LocatinService.java to avoid google client move to background
        // GOOGLE FITNESS CODE
        // Create a Google Fit Client instance with default user account.

        Crashlytics.log(Log.INFO,"RunFragmentWithService","startRunning before connecting timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));
        Crashlytics.logException(new Throwable("startRunning before connecting timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())));
        Logger.log( "RunFragmentWithService startRunning before connecting timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));

        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(Fitness.SENSORS_API)  // Required for SensorsApi calls
                // Optional: specify more APIs used with additional calls to addApi
                .useDefaultAccount()
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

//
//        startAnimation(); 24.12.18 Chagit moved to onConnected. no need to start animation before connected...
    }


// 4.12.18 Chagit moved to LocationService

//    // Added by Guy . 31w/10/2018
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Start the service
        //turnOnLocationRequest();


        startAnimation();//24.12.18 chagit

        Crashlytics.log(Log.INFO,"RunFragmentWithService","onConnected before starting service timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));
        Crashlytics.logException(new Throwable("RunFragmentWithService\",\"onConnected before starting service timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())));
        Logger.log("RunFragmentWithService\",\"onConnected before starting service timestamp = " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()));

        Log.i("RunFragmentWithService", " * - * - * - * - * - * - * - * onConnected() 1 * - * - * - * - * - * - * - *");
        //if (mGoogleApiClient != null) mGoogleApiClient.disconnect();

        Intent intent = new Intent(getContext(), LocationService.class);
        //intent.putExtra(Practice.NUM_OF_PRACTICE_KEY, getLessonNum());
        if(runningWithMiri && mPractice != null) {
            intent.putExtra(Practice.PRACTICE_KEY, mPractice);
        }
        //intent.putExtra("Google_client", mGoogleApiClient);
        getActivity().getApplicationContext().startService(intent);

        Log.i("RunFragmentWithService", " * - * - * - * - * - * - * - * onConnected() 2 * - * - * - * - * - * - * - *");
        Crashlytics.log(Log.INFO,"LocationLOG-onConnected","Start Running");
        /*
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_LOCATION_SAMPLE, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_LOCATION_TRACK, FitnessOptions.ACCESS_READ)
                        .build();

        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(this.getContext(), fitnessOptions);

        Fitness.getSensorsClient(this.getContext(), gsa)
                .add(new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                        .setDataType(DataType.TYPE_DISTANCE_DELTA)
                        .setDataType(DataType.TYPE_LOCATION_TRACK)
                        .setSamplingRate(5, TimeUnit.SECONDS)  // sample once per minute
                        .build(), this);
        */
    }

    // Added by Guy . 31w/10/2018
    @Override
    public void onConnectionSuspended(int i) {
        Crashlytics.log(Log.INFO,"LocationLOG-onConnectionSuspended","Start Running");
        Crashlytics.logException(new Throwable("onConnectionSuspended"));
    }

    // Added by Guy . 31w/10/2018
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        pressedStartToRun = false;
        // Error while connecting. Try to resolve using the pending intent returned.
        Crashlytics.log(Log.INFO,"LocationLOG-onConnectionFailed",""+result.getErrorCode());
        Crashlytics.logException(new Throwable("onConnectionFailed"));
        Logger.log("LocationLOG-onConnectionFailed "+ result.getErrorCode());

        if (result.getErrorCode() == FitnessStatusCodes.SIGN_IN_REQUIRED ||result.getErrorCode() == FitnessStatusCodes.API_EXCEPTION ) {
            try {
                result.startResolutionForResult(this.getActivity(), REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Crashlytics.logException(e);
                Crashlytics.log(Log.INFO," LocationLOG-onConnectionFailed -EXCEPTION ",""+result.getErrorCode());
                Logger.log ("onConnectionFailed EXCEPTION" +  e.getMessage());
                Dialogs.showErrorDialog(getContext(), getString(R.string.google_connection_problem_heb), null);
            }
        } else{
            Crashlytics.logException(new Throwable("LocationLOG-onConnectionFailed result.getErrorCode() != FitnessStatusCodes.SIGN_IN_REQUIRED ||  FitnessStatusCodes.API_EXCEPTION"));
            Crashlytics.log(Log.INFO,"LocationLOG-onConnectionFailed","");
            Logger.log("LocationLOG-onConnectionFailed result.getErrorCode() != FitnessStatusCodes.SIGN_IN_REQUIRED || FitnessStatusCodes.API_EXCEPTION");
            Dialogs.showErrorDialog(getContext(), getString(R.string.google_connection_problem_heb), null);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_OAUTH && resultCode == RESULT_OK) {
//            mGoogleApiClient.connect();
//        }
//    }

    private boolean isLocationServiceOn() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return false;
        }
        return true;
    }

    private boolean servicesConnected() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        //googleAPI.makeGooglePlayServicesAvailable(getMyActivity());


        if(result != ConnectionResult.SUCCESS) {
            //googleAPI.getErrorDialog(getMyActivity(), result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            return false;
        }
        return true;
    }

    //private ServiceConnection mServiceConnection;

    private void stopRunning(){
        pressedStartToRun = false;
        Log.d(this.getFragmentName(), "+++++++++++ stop running() +++++++++++");
        Crashlytics.log(Log.INFO,"LocationLOG-ostopRunning","stopRunning");
        Crashlytics.logException(new Throwable("stopRunning"));
        workingDialog = Dialogs.showSpinningWheelDialog(getMyActivity());
        workingDialog.show();
        try {

            Intent intent = new Intent(getContext(), LocationService.class);
            getActivity().getApplicationContext().stopService(intent);

            PersistentPracticeData ppd = PersistentPracticeData.createPersistentPracticeData(getMyActivity());

            boolean pdCreated = createAndWritePracticeData(getMyActivity(), ppd);

            if ( pdCreated ) {
                // get elevation data and save to cloud
                //scheduleNetTasks(); //out of this thread. will schedule it from exit of fragment

//                if (saveRunAsPartOfPlan(mPractice != null ? mPractice.getPracticeNum() : 0)) {
//                    ConnectedUser.getInstance().setPracticeNum(getMyActivity(), mPractice.getPracticeNum() + 1);
//                }
                Bundle b = null;

                if (mPractice != null) {
                    b = new Bundle();
                    b.putBoolean(RunFragmentWithService.IS_RUNNING_WITH_MIRI_KEY, saveRunAsPartOfPlan(getActivity(), mPractice.getPracticeNum()));
                    b.putSerializable(Practice.PRACTICE_KEY, mPractice);

                }

                //open HoorayFragment
                ((MainActivity) getMyActivity()).replaceFragment(HoorayFragment.FRAGMENT_TAG, b, true);
            }

        } catch (Exception ex){
            ex.printStackTrace();
            Crashlytics.logException(ex);
        } finally {
            if(workingDialog != null && workingDialog.isShowing()) workingDialog.dismiss();
        }
    }

    static public boolean createAndWritePracticeData(final Context context, PersistentPracticeData ppd){
        if(ppd == null) return false;
        PracticeData practiceData = new PracticeData(/*context,*/ ppd);
//        boolean practicePartOfPlan = ppd.getPracticeEnded();
//        ppd.eraseData(); //31.10.18 fix lessons not saved correct num because PPD is empty

        ppd.setPpdSavedToPd(true);


        if ( practiceData != null && practiceData.getLocations() != null ){//&& practiceData.getLocations().size() >= 10) {

            //check if lesson num should stay - if lesson finished or removed, if lesson not finished
//            if(practiceData.getNumOfLessonInPlan() > 0) {
//                if (!wereAudioFilesDeleted(context, practiceData.getNumOfLessonInPlan())) {
//                    practiceData.setNumOfLessonInPlan(0);
//                    practiceData.setNumOfPlan(0);
//                }
//            }
//            25.4 I added boolean in ppd to set if practice was ended or not, insted of checking via if practice deleted

            practiceData.writeToSharedPreferences(context, null, true);
            writeToSPlocationReadyForPresentation(context, false);

            new BackgroundWorker(context, practiceData).start();

            return true;

        } else {
            return false;
        }

    }


    private static class BackgroundWorker extends Thread{

        private Context mContext = null;
        private PracticeData mPracticeData = null;

        BackgroundWorker (Context applicationContext, PracticeData practiceData){
//            Log.d(getName(), "-----------------------   BackgroundWorker() context = " + applicationContext);
            mContext = applicationContext;
//            Log.d(getName(), "-----------------------   BackgroundWorker() this.mContext = " + this.mContext);
            mPracticeData = practiceData;
        }

        @Override
        public void run() {
//            Log.d(getName(), "-----------------------   BackgroundWorker.run() mContext = " + mContext);
            if(mContext == null) return;

            mPracticeData.shrinkWhateverNeeded();
            //todo          work on it. I want it to be. not to remove it!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
           // mPracticeData.smoothLocations(); //removed on 28.8.2018 I do not like the smooth, on far runs it smooths to much

            mPracticeData.updateLocationsToSharedPreferences(mContext, null);

            writeToSPlocationReadyForPresentation(mContext, true);

//            Log.d(getName(), "-----------------------   BackgroundWorker.run() shrinked if needed = " + mContext);

//            User user = User.createUserFromSharedPreferences(mContext, true);
//            Log.d(getName(), "-----------------------   BackgroundWorker.run() user = " + user);
//            if(user != null) {
//                Log.d(getName(), "-----------------------   BackgroundWorker.run() saving to NOT SAVED");
                PracticeDataTempStorageHelper.saveNotSavedToBackendPracticeData(mContext, mPracticeData);//, user.getEmail());
//                Log.d(getName(), "-----------------------   BackgroundWorker.run() AFTER saving to NOT SAVED");
//            }

            //mPracticeData.writeToSharedPreferences(mContext, PracticeDataActivity.P_DATA_TO_PASS, true);
            //Log.d(getName(), "-----------------------   BackgroundWorker.run() wrote to shared prefrences = " + mContext);

            RunFragmentWithService.scheduleNetTasks(mContext);
//            Log.d(getName(), "-----------------------   BackgroundWorker.run() Scheduled net tasks");

        }
    }

    static private void writeToSPlocationReadyForPresentation(Context context, boolean readyOrNot) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PracticeData.LOCATIONS_READY_FOR_PRESENTATION_KEY, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(PracticeData.LOCATIONS_READY_FOR_PRESENTATION_KEY, readyOrNot).commit();
        }
    }
//    private void sendToPaymentScreen() {
//        ((MainActivity)getMyActivity()).replaceFragment(TrainingPlanRegistrationFragment.FRAGMENT_TAG, null);//R.layout.fragment_practice_plan_registration, null);
//    }

    private WhileRunDialogFragment whileRunDialogFragment = null;

    private void startAnimation() {
        if(whileRunDialogFragment == null) {
            whileRunDialogFragment = WhileRunDialogFragment.getAndSetArguments(new WhileRunDialogFragment.YesListener() {
                @Override
                public void onYes() {
                    stopRunning();
                    if (getFragmentManager() != null) {
                        Fragment fragment = getFragmentManager().findFragmentByTag(WHILE_RUNNING_DIALOG);
                        if (fragment != null) {
                            WhileRunDialogFragment dialog = (WhileRunDialogFragment) fragment;
                            getFragmentManager().beginTransaction().remove(dialog).commit();
                            getFragmentManager().popBackStack(WHILE_RUNNING_DIALOG, POP_BACK_STACK_INCLUSIVE);

                            isRunDialogOpen = false;
                        }
                    } else if(whileRunDialogFragment != null){
                        whileRunDialogFragment.dismiss();//todo test to fix bug when pressing back while almost strat running, causes run not to dismiss itself, because getFragmentManager() returns null
                    }

                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel parcel, int i) {

                }
            });
            ;
        }


        if(!whileRunDialogFragment.isVisible()) {
            if(MainActivity.isFragmentInBackstack(getFragmentManager(), WHILE_RUNNING_DIALOG)){
                getFragmentManager().popBackStackImmediate(WHILE_RUNNING_DIALOG, POP_BACK_STACK_INCLUSIVE);
            }

            whileRunDialogFragment.show(getFragmentManager(), WHILE_RUNNING_DIALOG);


            isRunDialogOpen = true;
        }
    }



    private boolean isLocationPermissionGranted (){
        if(Build.VERSION.SDK_INT >= 23){
            return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    private void getRuntimePermissions() {
        if(isLocationPermissionGranted ()) return;

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getMyActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Dialogs.getAlertDialog(getMyActivity(), getString(R.string.can_not_use_without_location_permmision_heb), getString(R.string.sorry_heb)).show();
                ((MainActivity)getMyActivity()).replaceFragment(MainFragment.FRAGMENT_TAG, null, false);//R.layout.fragment_main, null);
            }
        }
    }


    public boolean isRunningWithMiri() {
        return runningWithMiri && ConnectedUser.getInstance().IsRegisteredToRunWithMiri();
    }

    public Activity getMyActivity(){
        if(getActivity() == null && (mContext instanceof MainActivity)) return (MainActivity)mContext;
        return getActivity();
    }

    private static boolean wereAudioFilesDeleted(Context context, int lessonNum){
        return ! Practice.isNextLessonNum(context, lessonNum);
    }

    private boolean saveRunAsPartOfPlan(Context context, int lessonNum){
        //return isRunningWithMiri() ? (isRunningWithMiri() && wereAudioFilesDeleted(getMyActivity(), lessonNum)) : false;
        return PracticeData.isSavedPracticePartOfPlan(context, lessonNum);
    }





    static private boolean isJobScheduled(JobScheduler jobScheduler, int jobId) {
        if (Build.VERSION.SDK_INT >= 21) {
            List<JobInfo> allJobs = jobScheduler.getAllPendingJobs();
            for (JobInfo ji : allJobs) {
                if (ji.getId() == jobId) {
                    return true;
                }
            }
        }
        return false;
    }

    static public void scheduleNetTasks(Context context) {
        if(context == null ) return;
        if (Build.VERSION.SDK_INT >= 21) {
            //schedule task -
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            JobInfo savePracticeJob = new JobInfo.Builder(PRACTICE_SAVE_DATA_JOB_SERVICE_ID, new ComponentName(context, PracticeSaveDataJobService.class))
                    .setMinimumLatency(4000) //Milliseconds before which this job will not be considered for execution. 4 minutes
                    .setOverrideDeadline(21600000) // 6 hours
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();

            if( ! isJobScheduled(jobScheduler, PRACTICE_SAVE_DATA_JOB_SERVICE_ID)) {
                int r2 = jobScheduler.schedule(savePracticeJob);
                Log.d(RunFragmentWithService.class.getName(), "**************** ******************* result of scheduling Save to server is " + r2 + " ******************* ******************* *******************");
            } else {
                Log.d(RunFragmentWithService.class.getName(), "**************** ******************* JOB NOT SCHEDULED> IT WAS ALREADY SCHEDULED!!!!  ******************* ******************* *******************");

            }

            // if in plan - schedule also practice download
            JobInfo downloadJob = new JobInfo.Builder(PRACTICE_DOWNLOAD_JOB_SERVICE_ID, new ComponentName(context, PracticeDownloadJobService.class))
                    .setMinimumLatency(0)
                    .setOverrideDeadline(10000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();

            if( ! isJobScheduled(jobScheduler, PRACTICE_DOWNLOAD_JOB_SERVICE_ID)) {
                int r2 = jobScheduler.schedule(downloadJob);
                Log.d(RunFragmentWithService.class.getName(), "**************** ******************* result of scheduling Save to server is " + r2 + " ******************* ******************* *******************");
            } else {
                Log.d(RunFragmentWithService.class.getName(), "**************** ******************* JOB NOT SCHEDULED> IT WAS ALREADY SCHEDULED!!!!  ******************* ******************* *******************");

            }

        } else { // for api 19

            final GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(context);
            new Runnable() {
                @Override
                public void run() {

                    // schedule save to backend
                    OneoffTask savePracticeDataTask = new OneoffTask.Builder()
                            .setService(PracticeSaveDataService.class)
                            .setExecutionWindow(240, 21600) //earliest time to start 4 minutes after, maximum 6 hours after training ended
                            .setTag(TASK_TAG_API_19_SCHDULED_SAVE_TO_SERVER)
                            .setPersisted(true)
                            .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                            .build();
                    mGcmNetworkManager.cancelTask(TASK_TAG_API_19_SCHDULED_SAVE_TO_SERVER, PracticeSaveDataService.class);//to avoid double tasking
                    mGcmNetworkManager.schedule(savePracticeDataTask);


                    // schedule save to backend
                    OneoffTask downloadTask = new OneoffTask.Builder()
                            .setService(DownloadPracticeService.class)
                            .setExecutionWindow(0, 30) //earliest time to start 4 minutes after, maximum 6 hours after training ended
                            .setTag(TASK_TAG_API_19_SCHDULED_DOWNLOAD_FROM_SERVER)
                            .setPersisted(true)
                            .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                            .build();
                    mGcmNetworkManager.cancelTask(TASK_TAG_API_19_SCHDULED_DOWNLOAD_FROM_SERVER, DownloadPracticeService.class);//to avoid double tasking
                    mGcmNetworkManager.schedule(downloadTask);
                }
            }.run();

        }
    }

    private void practiceDataNotSaved(){
        if(workingDialog != null && workingDialog.isShowing()) workingDialog.dismiss();
        getMyActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialogs.showErrorDialog(getMyActivity(), getMyActivity().getString(R.string.data_was_not_saved_heb), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) getMyActivity()).replaceFragment(MainFragment.FRAGMENT_TAG, null, false);
                    }
                });
            }
        });
    }

//    private void savePracticeDataToLocalDatabase(Context appContext) {
//        new SavePracticeDataToLocalDB(appContext).execute("null");
//    }

//    private class SavePracticeDataToLocalDB extends AsyncTask<String, String, String> {
//        private Context context = null;
//
//        public SavePracticeDataToLocalDB(final Context appContext){
//            this.context = appContext;
//        }
//        @Override
//        protected String doInBackground(String... params) {
//
//            //SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(getMyActivity().getApplicationContext());
//            SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(this.context);
//            db.addPractice(mPracticeData);
//            db.close();
//            return null;
//        }
//
//
//    }

    private int getTemperature() {
        return 0;
    }



    private int getLessonNum(){
        return ConnectedUser.getInstance().IsRegisteredToRunWithMiri() ? ConnectedUser.getLessonNum() : 0;
    }

    @Override
    public void storagePermissionGranted() {
        if(ConnectedUser.getInstance().getImage() != null ) {
            Log.i(this.getClass().getName(), " storagePermissionGranted - getLocalPhotoUri() != null getImage() ? " + ConnectedUser.getInstance().getImage());
            circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
        }
    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

}
