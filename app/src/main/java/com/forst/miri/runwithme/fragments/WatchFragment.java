package com.forst.miri.runwithme.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchFragment extends ParentFragment {


    public static final String FRAGMENT_TAG = WatchFragment.class.getSimpleName();


    //private static final String KEY_VIDEO_TIME = "youtube_video_time";
    private Practice mPractice = null;
    private LinearLayout bNext, bBack;
    private CircleImageView circleImageView;
    private TextView tvHello , tvTimeToStart, tvPracticeNum;

    private YouTubeThumbnailView thumbnailView;



    @Override
    public int getResourceID() {
        return R.layout.run_with_miri_watch_layout;
    }


    public WatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        mPractice = Practice.createFromSharedPreferences(getContext());

        View rootView;
        rootView = inflater.inflate(R.layout.run_with_miri_watch_layout, container, false);

        thumbnailView = (YouTubeThumbnailView)rootView.findViewById(R.id.watch_fragment_imageView_thumbnail);

        bNext = (LinearLayout) rootView.findViewById(R.id.run_watch_button_next);
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        bBack = (LinearLayout) rootView.findViewById(R.id.run_watch_button_back);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        tvPracticeNum = (TextView) rootView.findViewById(R.id.runwith_fragment_train_num_textview);
        tvHello = (TextView) rootView.findViewById(R.id.run_fragment_tv_hello);
        tvTimeToStart = (TextView) rootView.findViewById(R.id.run_fragment_tv_time_to_start);
        circleImageView = (CircleImageView) rootView.findViewById(R.id.run_fragment_small_image_circle);

        return rootView;
    }

    private void next() {
        if(getActivity() != null) {
            ((MainActivity) getActivity()).replaceFragment(RunFragmentWithService.FRAGMENT_TAG, null, false);
        }
    }

    private void back() {
        if(getActivity() != null) {
            ((MainActivity) getActivity()).onBackPressed();
        }
    }

    @Override
    public void storagePermissionGranted() {

    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public void onStart() {
        super.onStart();

            if (mPractice != null) {
                updateFragmentWithPractice(mPractice);
            } else {

                tvPracticeNum.setText(getString(R.string.no_practice_ready_heb));
            }

        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getActivity());

        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            if(getActivity() != null) result.getErrorDialog(getActivity(), 0).show();
        } else {
            setThumb();

        }

        String textHello = UIHelper.getGreeting(getContext()) + " " + ConnectedUser.getInstance().getFirstName();
        tvHello.setText(textHello);

        String textTimeToStart = getString(R.string.time_to_start_the_practice_heb);
        tvTimeToStart.setText(textTimeToStart);


        if (ConnectedUser.getInstance().getImage() != null) {
            circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
        } else {
            circleImageView.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isAttached = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    private void setThumb() {

        try {
            thumbnailView.initialize(getString(R.string.google_api_key), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    thumbnailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), getString(R.string.google_api_key), getString(R.string.pre_run_exercise_video_address), 0, true, false);
                            startActivity(intent);
                        }
                    });
                    if (getActivity() != null && isAttached && getResources() != null) {
                        youTubeThumbnailLoader.setVideo(getString(R.string.pre_run_exercise_video_address));
                    }
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    final String errorMessage = youTubeInitializationResult.toString();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex){
            ex.printStackTrace();
        }


    }

    private void updateFragmentWithPractice(Practice practice) {
        tvPracticeNum.setText(getString(R.string.practice_heb) + " " + String.valueOf(practice.getPracticeNum()));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        outState.putBoolean("attachment", isAttached);
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            mPractice = (Practice) savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
            isAttached = savedInstanceState.getBoolean("attachment");
        }
    }


}
