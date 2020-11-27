package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;

import de.hdodenhof.circleimageview.CircleImageView;


public class HoorayFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = HoorayFragment.class.getSimpleName();

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    private Practice mPractice = null;
    private boolean runningWithMiri = false;
    //private PracticeData pData = null;
    //private Button bNext;
    private CircleImageView circleImageView;
    private TextView tvName, tvHooray;
    private ImageView medalImageView;

    public HoorayFragment() {
        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_hooray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(savedInstanceState != null && !savedInstanceState.isEmpty()){
            runningWithMiri = savedInstanceState.getBoolean(RunFragmentWithService.IS_RUNNING_WITH_MIRI_KEY);
            if(runningWithMiri) this.mPractice = (Practice)savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
            //this.pData = (PracticeData)savedInstanceState.getParcelable(PracticeData.PRACTICE_DATA_KEY);
        } else {
            Bundle bundle = getArguments();
            if(bundle != null) {
                runningWithMiri = bundle.getBoolean(RunFragmentWithService.IS_RUNNING_WITH_MIRI_KEY);
                if (runningWithMiri)
                    this.mPractice = (Practice) bundle.getSerializable(Practice.PRACTICE_KEY);
                //this.pData = (PracticeData)bundle.getParcelable(PracticeData.PRACTICE_DATA_KEY);
            }
        }


        View root = inflater.inflate(R.layout.fragment_hooray, container, false);

        //bNext = (Button) root.findViewById(R.id.hooray_fragment_button_close);
        tvName = (TextView) root.findViewById(R.id.hooray_fragment_tv_name);
        tvHooray = (TextView) root.findViewById(R.id.hooray_fragment_tv_hooray);
        circleImageView = (CircleImageView) root.findViewById(R.id.hooray_fragment_image_circle);
        medalImageView = (ImageView) root.findViewById(R.id.hooray_fragment_image_medal);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(runningWithMiri)  outState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        outState.putBoolean(RunFragmentWithService.IS_RUNNING_WITH_MIRI_KEY, runningWithMiri);
        //outState.putParcelable(PracticeData.PRACTICE_DATA_KEY,pData);
    }

    @Override
    public void onStart() {
        super.onStart();
//        bNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                continueNext();
//            }
//        });
//        if(ConnectedUser.getInstance() != null) {
//
//            tvName.setText(ConnectedUser.getInstance().getFirstName() + " !");
//            if (ConnectedUser.getInstance().getImage() != null) {
//                circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
//            } else {
//                circleImageView.setVisibility(View.INVISIBLE);
//            }
//        }

        //if(this.pData == null) this.pData = PracticeData.readFromSharedPreferences(getContext());

        circleImageView.setVisibility(View.INVISIBLE);
        tvHooray.setVisibility(View.INVISIBLE);
        tvName.setVisibility(View.INVISIBLE);
        animate();
    }

    private void animate(){

        final Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    synchronized(this) {
                        this.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continueNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation grow = AnimationUtils.loadAnimation(getContext(), R.anim.grow_and_rotate);
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(ConnectedUser.getInstance() != null) {
                    if (ConnectedUser.getInstance().getImage() != null) {
                        circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
                        circleImageView.startAnimation(fadeIn);
                    } else {
                        circleImageView.setVisibility(View.INVISIBLE);
                    }
                    tvName.setText(ConnectedUser.getInstance().getFirstName());//4.3.2018 Miri asked to eliminate the !
                    tvName.startAnimation(fadeIn);

                }

                tvHooray.startAnimation(fadeIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        medalImageView.startAnimation(grow);
    }

    @Override
    public void storagePermissionGranted() {

    }




    private void continueNext(){
        Bundle b = new Bundle();
        //b.putParcelable(PracticeData.PRACTICE_DATA_KEY, pData);
        if(runningWithMiri) b.putSerializable(Practice.PRACTICE_KEY, mPractice);
        ((MainActivity)getActivity()).replaceFragment(PostRunFragment.FRAGMENT_TAG, b, false);
//        ((MainActivity)getActivity()).removeFragment(PostRunFragment.FRAGMENT_TAG);

//todo: this is supposed to make the HoorayFragment not appear again when the back button is pressed. It does not work.
//        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
//        if (fragment != null) {
//            try {
//                getFragmentManager().beginTransaction().remove(fragment).commit();
//                //getFragmentManager().popBackStack(FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                getFragmentManager().popBackStackImmediate(FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//        Fragment fragment = ((MainActivity)getActivity()).getSupportFragmentManager().findFragmentByTag(this.getTag());
//        if(fragment != null) {
//            ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//        }
    }
}
