package com.forst.miri.runwithme.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.application.RunWithMiriApplication;
import com.forst.miri.runwithme.fragments.AboutFragment;
import com.forst.miri.runwithme.fragments.AgreementFragment;
import com.forst.miri.runwithme.fragments.AllRunsDataFragment;
import com.forst.miri.runwithme.fragments.EndOfTrainingFragment;
import com.forst.miri.runwithme.fragments.ExerciseFragment;
import com.forst.miri.runwithme.fragments.HelpFragment;
import com.forst.miri.runwithme.fragments.HoorayFragment;
import com.forst.miri.runwithme.fragments.ListenFragment;
import com.forst.miri.runwithme.fragments.MainFragment;
import com.forst.miri.runwithme.fragments.ParentFragment;
import com.forst.miri.runwithme.fragments.PersonalDetailsFragment;
import com.forst.miri.runwithme.fragments.PostRunFragment;
import com.forst.miri.runwithme.fragments.PracticeDataFragment;
import com.forst.miri.runwithme.fragments.QuestionsFragment;
import com.forst.miri.runwithme.fragments.RunFragmentWithService;
import com.forst.miri.runwithme.fragments.SetDayAndTimePracticeFragment;
import com.forst.miri.runwithme.fragments.SettingsFragment;
import com.forst.miri.runwithme.fragments.TipsFragment;
import com.forst.miri.runwithme.fragments.TrainingPlanFragment;
import com.forst.miri.runwithme.fragments.TrainingPlanRegistrationFragment;
import com.forst.miri.runwithme.fragments.WatchFragment;
import com.forst.miri.runwithme.interfaces.PracticeDownloadEndedCallback;
import com.forst.miri.runwithme.interfaces.UserChangedCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.SQLiteDatabaseHandler;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.GeneralAudio;
import com.forst.miri.runwithme.objects.Logger;
import com.forst.miri.runwithme.objects.PersistentPracticeData;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.User;
import com.forst.miri.runwithme.services.AudioDownloadJobService;
import com.forst.miri.runwithme.services.AudioDownloadService;
import com.forst.miri.runwithme.services.LocationService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soundcloud.android.crop.Crop;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , UserChangedCallback, PracticeDownloadEndedCallback{

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 878;
    //public static final String FRAGMET_ID_KEY = "fragmentidkey__";
    public static final String FRAGMET_TAG_KEY = "fragmenttagkey__";
    private static final String FRAGMENT_KEY ="fragment_key" ;
    public static final String SERVICE_IS_RUNNING = "service_is_running";
    private static final String FROM_PPD_MAIN_A = "from_ppd_main_activity";
    public static final String FROM_NOTIFICATION = "com.chagit.hazani.run.with.miri.10235";
    public static final String MIN_ACCURACY_TAG = "min_accuracy";
    public static String RUN_WITH_MIRI_KEY = "runwithmiriornot";
    private ActionBarDrawerToggle toggle;
//    private StorageReference mStorageRef;
    private ParentFragment mCurrentFragment;
    private CircleImageView menuRoundImage;
    private Button loginLogoutButton;
//    private Button sendLogButton;
    private TextView menuNameTextView;
    //private int mCurrentFragmentId = R.layout.fragment_main;
    private String mCurrentFragmentTag = MainFragment.FRAGMENT_TAG;;
    private boolean isFirstTimeOpened = false;
    private boolean isOpenedViaLocationServiceNotification = false;



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = null;
        Fabric.with(this, new Crashlytics());
//        Appsee.start();

        if(savedInstanceState != null){
            //user = (User)savedInstanceState.getSerializable(CONNECTED_USER); //25.7.2018 TransactionTooLargeException on Firebase. Can be cause
            //mCurrentFragmentId = savedInstanceState.getInt(FRAGMET_ID_KEY);
            mCurrentFragmentTag = savedInstanceState.getString(FRAGMET_TAG_KEY);
            mCurrentFragment = getFragment(mCurrentFragmentTag);
            //Log.d(getClass().toString(), " mCurrentFragment = " + (mCurrentFragment != null ? mCurrentFragment.getFragmentName() : " - null -  "));
            //eyereplaceFragment(mCurrentFragmentId, null);


        } else {
            isFirstTimeOpened = true;


            Bundle b = getIntent().getExtras();
            if(b != null){
                if(b.getSerializable(User.USER_KEY) != null) {
                    user = (User) b.getSerializable(User.USER_KEY); //from LoginActivity or RegistrationActivity
                    //mCurrentFragmentId = b.getInt(FRAGMET_ID_KEY);
                    mCurrentFragmentTag = b.getString(FRAGMET_TAG_KEY);
                } else { //from Notification
                    String notificationGroup = b.getString(RunWithMiriApplication.NOTIFICATION_GROUP);
                    if (notificationGroup != null ){
                        if(notificationGroup.matches(RunWithMiriApplication.TRAINING)){
                            mCurrentFragmentTag = TrainingPlanFragment.FRAGMENT_TAG;
                        } else if(notificationGroup.matches(RunWithMiriApplication.EXERCISE)){
                            mCurrentFragmentTag = ExerciseFragment.FRAGMENT_TAG;
                        }
                    }
                }
            }

        }

        if (user == null) {
            user = User.createUserFromSharedPreferences(this, false);
        }

        overridePendingTransition(R.anim.slide_in, R.anim.nothing);



        getRuntimePermissions();
        //getUserDataFromServerIfCan();

        //final User
        if( user != null) { // already connected
            // 1. save user
            ((RunWithMiriApplication) getApplication()).setConnectedUser(user);
            //((RunWithMiriApplication) getApplication()).addUserChangeCallback(this);
            //fetchAudioIfNeeded(null);
            ((RunWithMiriApplication) getApplication()).addUserChangeCallback(new UserChangedCallback() {
                @Override
                public void userWasChanged(User user) {
                    setUpUser(user);
                }
            });
            //make full transparent statusBar
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            }
            if (Build.VERSION.SDK_INT >= 19) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            setContentView(R.layout.activity_main);

            FrameLayout mainFrame = (FrameLayout) findViewById(R.id.fragment_frame);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mainFrame.getLayoutParams();
            int margin = getResources().getDimensionPixelSize(R.dimen.white_rect_pading);
            params.setMargins(margin, getStatusBarHeight()+(margin/2), margin,margin);
            mainFrame.setLayoutParams(params);

            //Changes direction to RTL. all NavigationDrawer elements are set to Start, and then RTL sets them to open from right.
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);



            Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
            toolbar.bringToFront();
            // enlarge hamburger icon
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                if(toolbar.getChildAt(i) instanceof ImageButton){
                    toolbar.getChildAt(i).setScaleX(1.5f);
                    toolbar.getChildAt(i).setScaleY(1.5f);
                }
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View headerView = navigationView.getHeaderView(0);

            menuRoundImage = (CircleImageView) headerView.findViewById(R.id.side_menu_circle_image);


            menuNameTextView = (TextView)headerView.findViewById(R.id.side_menu_tv_name);

            loginLogoutButton = (Button)headerView.findViewById(R.id.side_menu_button_login_logout);
//            sendLogButton = (Button)headerView.findViewById(R.id.side_menu_button_send_log);

            if(savedInstanceState == null && !isMyServiceRunning(LocationService.class)) { //this should happen only when app is opened for first time, and not when state is restored, because that causes it to find a practice that is NOW being recorded
                checkIfPersistentPracticeDataWaitingToBeSaved();
            }

            //if service is running, app will open with WhileRunFragment open
            if(isMyServiceRunning(LocationService.class)){
                Bundle b = new Bundle();
                b.putBoolean(SERVICE_IS_RUNNING, true);
                replaceFragment(RunFragmentWithService.FRAGMENT_TAG, b, false);

            } else
                //15.1.18  test fix bug of multi fragments added to stack
                if(isFirstTimeOpened && !isOpenedViaLocationServiceNotification) {
                    isFirstTimeOpened = false;
                    try {
                        //replaceFragment(mCurrentFragmentId, null);
                        replaceFragment(mCurrentFragmentTag, null, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        replaceFragment(MainFragment.FRAGMENT_TAG, null, false);

                    }
                }


            RunFragmentWithService.scheduleNetTasks(this);
            downloadGeneralAudio();
            getMinAcuuracy();
        } else {
            //2. open LoginActivity
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

    }

    private void getMinAcuuracy(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("minAccuracy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer minAccuracy = dataSnapshot.getValue(Integer.class);
                SharedPreferences sp = getSharedPreferences(MIN_ACCURACY_TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(MIN_ACCURACY_TAG, minAccuracy);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void downloadGeneralAudio() {
        if (!GeneralAudio.doesExist(this) ) {
            //download general audio. scheduling so if failed, will try again
            if (Build.VERSION.SDK_INT >= 21) {
                //schedule task -
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

                JobInfo downloadJob = new JobInfo.Builder(99887766, new ComponentName(this, AudioDownloadJobService.class))
                        .setMinimumLatency(0)
                        .setOverrideDeadline(6000)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .build();

                if( ! isJobScheduled(jobScheduler, 99887766)) {
                    int r2 = jobScheduler.schedule(downloadJob);
                    Log.d(RunFragmentWithService.class.getName(), "**************** ******************* result of scheduling Save to server is " + r2 + " ******************* ******************* *******************");
                } else {
                    Log.d(RunFragmentWithService.class.getName(), "**************** ******************* JOB NOT SCHEDULED> IT WAS ALREADY SCHEDULED!!!!  ******************* ******************* *******************");

                }

            } else { // for api 19

                final GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this);
                new Runnable() {
                    @Override
                    public void run() {
                         OneoffTask downloadTask = new OneoffTask.Builder()
                                .setService(AudioDownloadService.class)
                                .setExecutionWindow(0, 240) //earliest time to start 4 minutes after, maximum 6 hours after training ended
                                .setTag("99887766")
                                .setPersisted(true)
                                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                                .build();
                        mGcmNetworkManager.cancelTask("99887766", AudioDownloadService.class);//to avoid double tasking
                        mGcmNetworkManager.schedule(downloadTask);
                    }
                }.run();

            }

        }

    }

    private boolean isJobScheduled(JobScheduler jobScheduler, int jobId) {
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
    /*
    will check if persistentPracticeData is present.
    If yes, it means a practice/run was terminated before saved, and it should be saved
     */
    private void checkIfPersistentPracticeDataWaitingToBeSaved() {
//        PersistentPracticeData ppd = PersistentPracticeData.createPersistentPracticeData(this);
        PersistentPracticeData ppd = PersistentPracticeData.createPersistentPracticeDataIfNotSavedYet(this);
        if(ppd != null) {
            saveAndPresentPracticeData(ppd);
        }
    }

    private void saveAndPresentPracticeData(PersistentPracticeData ppd) {

        boolean pdCreated = RunFragmentWithService.createAndWritePracticeData(this, ppd);


        if ( pdCreated) {

            Bundle b = new Bundle();
            b.putBoolean(FROM_PPD_MAIN_A, true);


            //open PostRunFragment
            replaceFragment(HoorayFragment.FRAGMENT_TAG, b, false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null && intent.getExtras() != null){
            String notificationGroup = intent.getStringExtra(RunWithMiriApplication.NOTIFICATION_GROUP);
            if(notificationGroup != null){
                if(notificationGroup.matches(RunWithMiriApplication.TRAINING)){
                    mCurrentFragmentTag = TrainingPlanFragment.FRAGMENT_TAG;
                } else if(notificationGroup.matches(RunWithMiriApplication.EXERCISE)){
                    mCurrentFragmentTag = ExerciseFragment.FRAGMENT_TAG;
                }
                replaceFragment(mCurrentFragmentTag, null, false);
                if(notificationGroup.matches(RunWithMiriApplication.GENERAL)){
                    String notificationBody = intent.getStringExtra(RunWithMiriApplication.NOTIFICATION_MESSAGE);
                    Dialogs.getAlertDialog(MainActivity.this, notificationBody, null).show();
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        loginLogoutButton.setText(getString(R.string.logout_heb));
        loginLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectedUser.getInstance() == null){
                    login();
                } else {
                    //logout
                    login(); // because she has nothing to do here without being loged in
                    logout(ConnectedUser.getInstance());
                    //loginLogoutButton.setText(getString(R.string.login));
                }
            }
        });

//        sendLogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri logUri = Logger.getLogUri(MainActivity.this);
//                if (logUri != null){
//                    try {
//                        Intent intent = new Intent();
//                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.run_with_email)});
//                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Log");
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        intent.setAction(Intent.ACTION_SEND);
//                        intent.putExtra(Intent.EXTRA_STREAM, logUri);
////                        intent.setData(logUri);
//                        intent.setType("text/plain");
////                        intent.setType("text/*");
//                        startActivity(Intent.createChooser(intent, getString(R.string.send_log_heb)));
//                    } catch (Exception ex){
//                        ex.printStackTrace();
//                    }
//                } else {
//                    Log.d(PostRunFragment.class.getName()," =================== logUri == null ===============================");
//                }
//            }
//        });

        setUpUser(ConnectedUser.getInstance());
    }


    private void setUpUser(User user){
        if(user != null ) {
            menuNameTextView.setText(user.getFirstName() + " " + user.getLastName());
            Bitmap im = user.getImage();
            if( im != null) {
                menuRoundImage.setVisibility(View.VISIBLE);
                menuRoundImage.setImageBitmap(im);
            } else {
                menuRoundImage.setVisibility(View.INVISIBLE);
            }
        } else{
            menuRoundImage.setVisibility(View.INVISIBLE);
            menuNameTextView.setText("");
        }
    }

    private void login() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        this.finish();
    }


    // for setting status bar fully transparent. not 100% works yet.
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    
    @Override
    public void onResume(){
        super.onResume();
//        try {
//            replaceFragment(mCurrentFragmentId, null);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            replaceFragment(R.layout.fragment_main, null);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

//    private ParentFragment getFragment(int mCurrentFragmentId) {
//        Fragment f = getSupportFragmentManager().findFragmentById(mCurrentFragmentId);
//        return ((f != null && f instanceof ParentFragment) ? (ParentFragment) f : null);
//    }

    private ParentFragment getFragment(String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return ((f != null && f instanceof ParentFragment) ? (ParentFragment) f : null);
    }

//    public void removeFragment(String fragmentTag) {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
//
//        if(f != null && f instanceof ParentFragment && ((ParentFragment)f).getFragmentName().equals(fragmentTag)) {
//            fragmentTransaction.remove(f);
//            fragmentTransaction.commitNow();
//        }
//
//    }

    private void replaceFragment(Fragment fragment, Bundle b ,boolean removeAllPrevious){

        if(b != null ) {
            fragment.setArguments(b);
        }

        if(fragment == null) return;

        this.mCurrentFragment = (ParentFragment) fragment;

        this.mCurrentFragmentTag = ((ParentFragment) fragment).getFragmentName();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(removeAllPrevious){
            getSupportFragmentManager().popBackStackImmediate(MainFragment.FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if(((ParentFragment) fragment).getFragmentName() == MainFragment.FRAGMENT_TAG){
            try {
                getSupportFragmentManager().popBackStackImmediate(MainFragment.FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }catch (IllegalStateException ex){
                ex.printStackTrace();
            }
            fragmentTransaction.replace(R.id.fragment_frame, this.mCurrentFragment, this.mCurrentFragment.getFragmentName());
        } else {
            fragmentTransaction.replace(R.id.fragment_frame, this.mCurrentFragment, this.mCurrentFragment.getFragmentName()).addToBackStack(this.mCurrentFragment.getFragmentName());

        }

        Log.d(getClass().toString(), "replaceFragment to ---------- " + this.mCurrentFragment.getFragmentName());

//        fragmentTransaction.addToBackStack(null); //todo t------------- esting 19.6.2018
        fragmentTransaction.commitAllowingStateLoss();

    }


    public void replaceFragment(String fragTag, /*, Object o*/Bundle b, boolean removeAllPrevious){

        if(isFirstTimeOpened)isFirstTimeOpened = false;
        if(fragTag == null) fragTag = MainFragment.FRAGMENT_TAG;
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragTag);

        if(f != null && !(f instanceof ParentFragment)) {
            return; // fix bug ava.lang.ClassCastException: com.google.android.gms.common.api.internal.zzdb cannot be cast to com.forst.miri.runwithme.fragments.ParentFragment
        }

        this.mCurrentFragmentTag = fragTag;

        if (f == null) {
            if(fragTag.equalsIgnoreCase(MainFragment.FRAGMENT_TAG))
                f = new MainFragment();
            else if (fragTag.equalsIgnoreCase(RunFragmentWithService.FRAGMENT_TAG))
                f = new RunFragmentWithService();
            else if (fragTag.equalsIgnoreCase(ExerciseFragment.FRAGMENT_TAG))
                f = new ExerciseFragment();
            else if (fragTag.equalsIgnoreCase(TrainingPlanRegistrationFragment.FRAGMENT_TAG))
                f = new TrainingPlanRegistrationFragment();
            else if (fragTag.equalsIgnoreCase(EndOfTrainingFragment.FRAGMENT_TAG))
                f = new EndOfTrainingFragment();
            else if (fragTag.equalsIgnoreCase(AllRunsDataFragment.FRAGMENT_TAG))
                f = new AllRunsDataFragment();
            else if (fragTag.equalsIgnoreCase(PostRunFragment.FRAGMENT_TAG))
                f = new PostRunFragment();
            else if (fragTag.equalsIgnoreCase(ListenFragment.FRAGMENT_TAG))
                f = new ListenFragment();
            else if (fragTag.equalsIgnoreCase(WatchFragment.FRAGMENT_TAG))
                f = new WatchFragment();
            else if (fragTag.equalsIgnoreCase(HoorayFragment.FRAGMENT_TAG))
                f = new HoorayFragment();
            else if (fragTag.equalsIgnoreCase(SetDayAndTimePracticeFragment.FRAGMENT_TAG))
                f = new SetDayAndTimePracticeFragment();
            else if (fragTag.equalsIgnoreCase(TrainingPlanFragment.FRAGMENT_TAG)) {
                if(ConnectedUser.getInstance() != null && ConnectedUser.getInstance().IsPaidRegistrationToRunWithMiri()) {
                    f = new TrainingPlanFragment();
                } else {
                    replaceFragment(TrainingPlanRegistrationFragment.FRAGMENT_TAG, b, false);
                }
            }
            else if (fragTag.equalsIgnoreCase(AboutFragment.FRAGMENT_TAG))
                f = new AboutFragment();
            else if (fragTag.equalsIgnoreCase(HelpFragment.FRAGMENT_TAG))
                f = new HelpFragment();
            else if (fragTag.equalsIgnoreCase(PersonalDetailsFragment.FRAGMENT_TAG))
                f = new PersonalDetailsFragment();
            else if (fragTag.equalsIgnoreCase(TipsFragment.FRAGMENT_TAG))
                f = new TipsFragment();
            else if(fragTag.equalsIgnoreCase(PracticeDataFragment.FRAGMENT_TAG))
                f = new PracticeDataFragment();
            else if(fragTag.equalsIgnoreCase(AgreementFragment.FRAGMENT_TAG))
                f = new AgreementFragment();
            else if(fragTag.equalsIgnoreCase(QuestionsFragment.FRAGMENT_TAG))
                f = new QuestionsFragment();
            else if(fragTag.equalsIgnoreCase(SettingsFragment.FRAGMENT_TAG))
                f = new SettingsFragment();
            else f = new MainFragment(); // default
        }

        replaceFragment(f, b , removeAllPrevious);
    }

    public static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

//            if(mCurrentFragmentId == R.layout.fragment_main){
//                finish();
//            } else {
                FragmentManager fm = getSupportFragmentManager();
                Log.d("back stack entry", fm.getBackStackEntryCount() + "");
                Log.d(getClass().getName() ,"back stack entry count: " + fm.getBackStackEntryCount() + "");
                Log.d(getClass().getName() ,"fragments: " + fm.getFragments() + "");
//                if(fm.getBackStackEntryCount() == 1 || mCurrentFragmentTag == MainFragment.FRAGMENT_TAG){
//                if(mCurrentFragmentTag == MainFragment.FRAGMENT_TAG){
//                    finish();
//                } else {
//                    fm.popBackStackImmediate(null, 0);
                    try {
                        fm.popBackStackImmediate();
                        int count = fm.getBackStackEntryCount();
                        if(count > 0) {
                            FragmentManager.BackStackEntry bse = fm.getBackStackEntryAt(count - 1);
                            this.mCurrentFragment = (ParentFragment) fm.findFragmentByTag(bse.getName());
                            this.mCurrentFragmentTag = this.mCurrentFragment.getFragmentName();
                        } else {
                            this.mCurrentFragmentTag = MainFragment.FRAGMENT_TAG;
                            this.mCurrentFragment = (ParentFragment) getSupportFragmentManager().findFragmentByTag(this.mCurrentFragmentTag);
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }

//                }

        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        onBackPressed();
//        return true;
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        //int fragID = R.layout.fragment_main;
        String fragTag = MainFragment.FRAGMENT_TAG;

        if (id == R.id.nav_personal_data) {
            fragTag = PersonalDetailsFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_time) {
            fragTag = SetDayAndTimePracticeFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_tips) {
            fragTag = TipsFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_help) {
            fragTag = HelpFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_about_us) {
            fragTag =  AboutFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_agreement) {
            fragTag =  AgreementFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_questions) {
            fragTag =  QuestionsFragment.FRAGMENT_TAG;
        } else if (id == R.id.nav_settings) {
            fragTag =  SettingsFragment.FRAGMENT_TAG;
        }

        replaceFragment(fragTag, null, false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.RIGHT);
        return true;
    }

    private void shareApp() {
        String shareBody = getString(R.string.share_recomendation_i_run_with_heb) + "\n" + " https://play.google.com/store/apps/details?id=com.forst.miri.runwithme";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_recomendation_heb));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        Intent chooserIntent = Intent.createChooser(sharingIntent, getString(R.string.share_heb));
        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooserIntent);
        }
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    //private boolean writeExternalPermissionGranted = false;
    private boolean getRuntimePermissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},RunFragmentWithService.ACCESS_FINE_LOCATION_REQUEST_CODE); //todo 21.12.2017 changed. not tested yet. maybe fixed excessive storage requests
            //writeExternalPermissionGranted = false;
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //writeExternalPermissionGranted = true;
                //enableGUI();
                if(ConnectedUser.getInstance().getImage() != null && mCurrentFragment != null) {
                    mCurrentFragment.storagePermissionGranted();
                    menuRoundImage.setImageBitmap(ConnectedUser.getInstance().getImage());
                }

            } else {
                //todo: open a dialog and explain that you cannot use tha app without using its storage. in the ok button of dialog put code to request permmission - that way user cannot be without permission. in the cancel button- exit app
            }
        } else if (requestCode == RunFragmentWithService.ACCESS_FINE_LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ( mCurrentFragment instanceof RunFragmentWithService){
                    ((RunFragmentWithService)mCurrentFragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK &&
                       (requestCode == SetDayAndTimePracticeFragment.TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE ||
                        requestCode == SetDayAndTimePracticeFragment.EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE ||
                        requestCode == SetDayAndTimePracticeFragment.WATER_NOTIFICATION_RINGTON_REQUEST_CODE)){
            if(mCurrentFragment != null && mCurrentFragment instanceof SetDayAndTimePracticeFragment) {
                Log.d(this.getClass().getName(), "Sending to SetDayAndTimePracticeFragment TBC...");
                ((SetDayAndTimePracticeFragment) mCurrentFragment).onActivityResult(requestCode, resultCode, intent);
            }
        } else if (requestCode == RunFragmentWithService.REQUEST_OAUTH) {// && resultCode == RESULT_OK) { // Added By Guy 31/10/2018

            String dataFromIntent = null;
            if(intent != null){
                Bundle b = intent.getExtras();
            }
            if(resultCode == RESULT_OK) {
//                Crashlytics.logException(new Throwable("requestCode == REQUEST_OAUTH  resultCode == RESULT_OK "));
//                Crashlytics.log(Log.INFO,"onActivityResult "," requestCode == REQUEST_OAUTH  resultCode == RESULT_OK ");
//                Logger.log("MainActivity.onActivityResult  requestCode == REQUEST_OAUTH  resultCode == RESULT_OK ");
                try {
                    RunFragmentWithService.mGoogleApiClient.connect();
                } catch (Exception ex){
                    Logger.log(" onActivityResult() problem with connecting to GoogleClient EXCEPTION:" + ex.getMessage());
                    Crashlytics.logException(ex);
                    Dialogs.showErrorDialog(this, getString(R.string.google_connection_problem_heb) + '\n' + ex.getMessage(), null);
                }
            } else {
                Crashlytics.logException(new Throwable("requestCode == REQUEST_OAUTH  resultCode != RESULT_OK result code = " + resultCode));
                Crashlytics.log(Log.INFO,"onActivityResult "," requestCode == REQUEST_OAUTH  resultCode != RESULT_OK result code = " + resultCode);
                Logger.log("MainActivity.onActivityResult  requestCode == REQUEST_OAUTH  resultCode != RESULT_OK result code = " + resultCode);
                Dialogs.showErrorDialog(this, getString(R.string.google_connection_problem_heb), null);
            }
        }
//        else if (requestCode == PostRunFragment.PICTURE_REQUEST_AFTER_RUN || requestCode == PostRunFragment.CROP_REQUEST) {
//            if(mCurrentFragment instanceof  PostRunFragment) ((PostRunFragment)mCurrentFragment).onActivityResult(requestCode, resultCode, intent);
//
//        }
        else if (requestCode == Crop.REQUEST_CROP) {
            if(mCurrentFragment instanceof  PersonalDetailsFragment) {
                ((PersonalDetailsFragment)mCurrentFragment).onActivityResult(requestCode, resultCode, intent);
            } else if(mCurrentFragment instanceof  PostRunFragment) {
                ((PostRunFragment)mCurrentFragment).onActivityResult(requestCode, resultCode, intent);
            }

        }
        else if (requestCode == PersonalDetailsFragment.UPDATE_DETAILS_PICTURE_REQUEST ) {
            if(mCurrentFragment instanceof  PersonalDetailsFragment) ((PersonalDetailsFragment)mCurrentFragment).onActivityResult(requestCode, resultCode, intent);

        }
        else if (requestCode == PostRunFragment.PICTURE_REQUEST_AFTER_RUN) {
            if(mCurrentFragment instanceof  PostRunFragment) ((PostRunFragment)mCurrentFragment).onActivityResult(requestCode, resultCode, intent);

        }
//        else if (requestCode == PostRunFragment.PICTURE_REQUEST_AFTER_RUN || requestCode == UCrop.REQUEST_CROP) { //todo <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <-
//            if(mCurrentFragment instanceof  MainFragment) ((MainFragment)mCurrentFragment).onActivityResult(requestCode, resultCode, intent);//todo <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <-
//
//        }

    }

//    public void addLocation(LocationAndMore lam){
//        if (locations == null) locations = new ArrayList<LocationAndMore>();
//        locations.add(lam);
//    }
//
//    public ArrayList<LocationAndMore> getLocations(){
//        if (locations == null) locations = new ArrayList<LocationAndMore>();
//        return locations;
//    }

//    public boolean isPayedUser(){
//        return payedUser;
//    }

    @Override
    public void userWasChanged(User user) {
        //update ui
        updateUI(user);

        //todo : fetch all its practices data and save locally

        // todo: register in push server One Signal
//
//        //download next lesson
//        fetchAudioForNextLesson(user.getPracticeNum());
    }

    private void updateUI(User user) {
        //mCurrentFragmentId = R.id.main_fragment;
        mCurrentFragmentTag = MainFragment.FRAGMENT_TAG;
        replaceFragment(mCurrentFragmentTag, null, false);

    }


    //
//    did not help for showing toggle on api 19
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }


    private PracticeDownloadEndedCallback practiceDownloadCallback = null;

//    public void fetchAudioIfNeeded(PracticeDownloadEndedCallback practiceDownloadEndedCallback) {
//        this.practiceDownloadCallback = practiceDownloadEndedCallback;
//        final int nextPracticeNum = ConnectedUser.getInstance().getPracticeNum();
//        final int planId = ConnectedUser.getInstance().getProgramId();
//        if(ConnectedUser.getInstance().IsRegisteredToRunWithMiri() && Practice.isNextLessonNum(this, nextPracticeNum) == false) {
//            new DownloadPracticeTask(MainActivity.this, MainActivity.this).execute(Integer.valueOf(nextPracticeNum) , Integer.valueOf(planId));
//        }
//    }

    // PracticeDownloadEndedCallback
    @Override
    public void practiceDownloadEnded(Practice practice) {
        if (practiceDownloadCallback != null) practiceDownloadCallback.practiceDownloadEnded(practice);

    }

    @Override
    public void practiceDownloadFailed() {
        if (practiceDownloadCallback != null) practiceDownloadCallback.practiceDownloadFailed();
    }

    private void logout(User user){
        eraseAllUserPracticesFromLocalDb();
//        PracticeDataTempStorageHelper.eraseAllNoElevationPracticeData(this);
        PracticeData.eraseFromSharedPreferences(this, null);
        PersistentPracticeData.erase(this);
        ((RunWithMiriApplication)getApplication()).setConnectedUser(null);
        Practice.eraseLocally(this);
        // remove from notification service
        ((RunWithMiriApplication)getApplication()).cancelNotifications();


        if(user != null) user.eraseLocally(this);
    }

    private void eraseAllUserPracticesFromLocalDb() {
        new DeleteAllPracticesFromLocalDataBase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    private class DeleteAllPracticesFromLocalDataBase extends AsyncTask< Void, Void, Void > {

        @Override
        protected Void doInBackground(Void... params) {

            SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(MainActivity.this);
            long start = System.currentTimeMillis();
            db.clearAllFullPractices(); //will be fetched from Backend
            //db.clearAllNoElevationPractices(); //will be fetched from Backend
//            PracticeDataTempStorageHelper.eraseAllNoElevationPracticeData(MainActivity.this);
            // practices that were not saved to backend yet, will be kept, and will be identified by user email

            Log.d(MainActivity.class.getName(), "********************** DeleteAllPracticesFromLocalDataBase duration = " + (System.currentTimeMillis()-start) + "**********************");
            db.close();
            return null;
        }

    }

    static final String CONNECTED_USER = "connected_user";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putSerializable(CONNECTED_USER, ConnectedUser.getInstance()); //25.7.2018 to Avoid TransactionTooLargeException
        //savedInstanceState.putInt(FRAGMET_ID_KEY, mCurrentFragmentId);
        savedInstanceState.putString(FRAGMET_TAG_KEY, mCurrentFragmentTag);
        super.onSaveInstanceState(savedInstanceState);
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        //super.onRestoreInstanceState(savedInstanceState);
//
//        User user = (User)savedInstanceState.getSerializable(CONNECTED_USER);
//        ((RunWithMiriApplication) getApplication()).setConnectedUser(user);
//        mCurrentFragmentId = savedInstanceState.getInt(FRAGMET_ID_KEY);
//        mCurrentFragment = getFragment(mCurrentFragmentId);
//        //eyereplaceFragment(mCurrentFragmentId, null);
//    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean doesHaveStoragePermission(Context context){
        if( Build.VERSION.SDK_INT >= M ) {
            return context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

}
