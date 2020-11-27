package com.forst.miri.runwithme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainFragment extends ParentFragment{//} implements  AdapterView.OnItemSelectedListener {


    public static final String FRAGMENT_TAG = MainFragment.class.getSimpleName();


    public TextView tvHello;//, tvWhatDoYouWantToDO;
    private View vbGoOutRun, vbDailyPractice, vbAllMyPractices, vbMyPracticePlan;
    private CircleImageView roundImage;
    //private FrameLayout buttonsFrame;
    private View rootView;

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_main;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        vbDailyPractice = rootView.findViewById(R.id.main_activity_view_round_button_daily_practice);
        vbGoOutRun =  rootView.findViewById(R.id.main_activity_view_round_button_go_out_and_run);
        vbAllMyPractices =  rootView.findViewById(R.id.main_activity_view_round_button_all_my_practices);
        vbMyPracticePlan =  rootView.findViewById(R.id.main_activity_view_round_button_my_practice_plan);

        tvHello = (TextView) rootView. findViewById(R.id.main_activity_tv_hello);
        roundImage = (CircleImageView) rootView.findViewById(R.id.main_activity_circle_image);

        int statusBarHight = getStatusBarHeight();

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (ConnectedUser.getInstance() == null) return;
        tvHello.setText(UIHelper.getGreeting(getContext()) + " " + ConnectedUser.getInstance().getFirstName());

        if(ConnectedUser.getInstance().getImage() != null ) {
            roundImage.setImageBitmap(ConnectedUser.getInstance().getImage());
        }


        vbGoOutRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(ListenFragment.FRAGMENT_TAG , null, false);//R.layout.run_with_miri_listen_layout, null);

//                if(ConnectedUser.getInstance().IsRegisteredToRunWithMiri() || ConnectedUser.getInstance().isInTrial()) {
//                    activity.replaceFragment(ListenFragment.FRAGMENT_TAG , null, false);//R.layout.run_with_miri_listen_layout, null);
//                } else {
//                    Bundle b = new Bundle();
//                    b.putBoolean(RunFragmentWithService.IS_RUNNING_WITH_MIRI_KEY, false);
//                    activity.replaceFragment(RunFragmentWithService.FRAGMENT_TAG, b, false); //R.layout.fragment_run_with_miri, b);
//                }
            }
        });
        vbDailyPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(ExerciseFragment.FRAGMENT_TAG, null, false);//R.layout.fragment_exercise, null);
//                NetworkCenterHelper.getScheduledTraining(getContext(), ConnectedUser.getInstance(), new GetResponseCallback() {
//                    @Override
//                    public void requestStarted() {
//
//                    }
//
//                    @Override
//                    public void requestCompleted(String response) {
//                        Log.d(MainFragment.class.getName(), "-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<> response: " + response);
//                    }
//
//                    @Override
//                    public void requestEndedWithError(VolleyError error) {
//
//                    }
//                });
            }
        });

        vbMyPracticePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity)getActivity();
                if(ConnectedUser.getInstance().IsPaidRegistrationToRunWithMiri()) {
                    activity.replaceFragment(TrainingPlanFragment.FRAGMENT_TAG, null, false);//R.layout.fragment_training_plan, null);
                } else {
                    activity.replaceFragment(TrainingPlanRegistrationFragment.FRAGMENT_TAG, null, false);//R.layout.fragment_practice_plan_registration, null);
                }

            }
        });



        vbAllMyPractices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePhoto();
                MainActivity activity = (MainActivity)getActivity();
                Bundle b = new Bundle();
                b.putBoolean(AllRunsDataFragment.RE_FETCH_PDs, true);
                activity.replaceFragment(AllRunsDataFragment.FRAGMENT_TAG, b, false);
            }
        });


        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        if(result != ConnectionResult.SUCCESS) {
            googleAPI.getErrorDialog(getActivity(), result, RunFragmentWithService.PLAY_SERVICES_RESOLUTION_REQUEST).show();
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

    @Override
    public void storagePermissionGranted() {
        if(ConnectedUser.getInstance() != null && ConnectedUser.getInstance().getImage() != null ) {
            roundImage.setImageBitmap(ConnectedUser.getInstance().getImage());
        }
    }


}
