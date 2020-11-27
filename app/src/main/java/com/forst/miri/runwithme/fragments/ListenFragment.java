package com.forst.miri.runwithme.fragments;


import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListenFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = ListenFragment.class.getSimpleName();
    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    private static String IS_PLAYING = "instructions_are_playing";
    //private static String FIRST_TIME = "instructions_already_played_once";
    private static String CURRENT_POSITION = "current_P" ;
    private LinearLayout bNext;
    private ProgressBar progressBarAudio;
    boolean areInstructionsPlaying = false;
    private MediaPlayer mp;
    private Practice mPractice = null;
    private CircleImageView circleImageView;
    private ImageButton bStartAudio;
    private TextView tvHello , tvTimeToStart, tvPracticeNum;
    private int mCurrentPosition = 0;
    private Handler mHandler = new Handler();
    private Thread playAsynchronusThread, drawProgressThread;
    //private boolean firstTime = false;

    public ListenFragment() {
        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.run_with_miri_listen_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null){

            //this.mPractice = (Practice)savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
            //if practice was already deleted from sharedprefrences, e.g. it was done and finished, then practice should be deleted from here also. but not passing it in savedState, makes mp not being set when rotated. so this is a patch
            if( ! Practice.isNextLessonNum(getContext(), ConnectedUser.getLessonNum())) this.mPractice = null;

            this.mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            this.areInstructionsPlaying = savedInstanceState.getBoolean(IS_PLAYING);

        }
        //else{// if (mPractice == null) { //6.3.2018 this causes the lesson to not update after lesson was finished and erased from sharedPreferences

        mPractice = Practice.createFromSharedPreferences(getContext());
        //}

        View rootView;
        rootView = inflater.inflate(R.layout.run_with_miri_listen_layout, container, false);


        bNext = (LinearLayout) rootView.findViewById(R.id.run_listen_button_next);
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMp();
                next();
            }
        });

        tvPracticeNum = (TextView) rootView.findViewById(R.id.runwith_fragment_train_num_textview);
        bStartAudio = (ImageButton) rootView.findViewById(R.id.runwith_listen_fragment_audio_bar_button);


        progressBarAudio = (ProgressBar) rootView.findViewById(R.id.runwith_fragment_audio_progress_bar);


        tvHello = (TextView) rootView.findViewById(R.id.run_fragment_tv_hello);
        tvTimeToStart = (TextView) rootView.findViewById(R.id.run_fragment_tv_time_to_start);
        circleImageView = (CircleImageView) rootView.findViewById(R.id.run_fragment_small_image_circle);


        if (mPractice != null) {
            updateFragmentWithPractice(mPractice);
        } else {
            tvPracticeNum.setText(getString(R.string.no_practice_ready_heb));
        }

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_line);
        progressBarAudio.setProgressDrawable(drawable);
        progressBarAudio.setRotation(180);


        //bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);

        String textHello = UIHelper.getGreeting(getContext()) + " " + ConnectedUser.getInstance().getFirstName();
        tvHello.setText(textHello);

        String textTimeToStart = getString(R.string.time_to_start_the_practice_heb);
        tvTimeToStart.setText(textTimeToStart);


        if (ConnectedUser.getInstance().getImage() != null) {
            circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
        } else {
            circleImageView.setVisibility(View.INVISIBLE);
        }
        if( getActivity() != null ) getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);//todo will this solve vol being very low? now it is controlled by user
        setUpMp();

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        if(mp != null && drawProgressThread != null) getActivity().runOnUiThread(drawProgressThread);
        if(bStartAudio != null ) bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);

    }

    private void setUpMp(){
        if(this.mPractice == null) return;

        //if(! areInstructionsPlaying ) {
            bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);

            try {
                Uri uri = Uri.parse(mPractice.getIntroductionUrl());
                mp = MediaPlayer.create(getContext(), uri);
                mp.seekTo(this.mCurrentPosition);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.d(ListenFragment.class.getName(), "DDDDDDDDDDDDD onCompletion DDDDDDDDDDD onCompletion DDDDDDDDDDDDDD onCompletion DDDDDDDDDD");
                        areInstructionsPlaying = false;
                        bStartAudio.setImageResource(R.drawable.purple_circle_play);
                        progressBarAudio.setProgress(0);
                    }
                });
                progressBarAudio.setMax(mp.getDuration());
                progressBarAudio.setProgress(mCurrentPosition);
                if (areInstructionsPlaying) playAudioInstructions(false);
            } catch (Exception e) {
                e.printStackTrace();
                Dialogs.getAlertDialog(getActivity(), getString(R.string.can_not_play_audio_heb), getString(R.string.sorry_heb)).show();
            }

       // }
    }

//    private void downloadPractice(PracticeDownloadEndedCallback callback) {
//        ((MainActivity) getActivity()).fetchAudioIfNeeded(callback);
//    }

    private void updateFragmentWithPractice(Practice practice) {
        if (practice == null) return;
        bStartAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudioInstructions(areInstructionsPlaying);
//                bStartAudio.setImageResource(areInstructionsPlaying == false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);
//                areInstructionsPlaying = !areInstructionsPlaying;
            }
        });
        tvPracticeNum.setText(getString(R.string.practice_heb) + " " + String.valueOf(practice.getPracticeNum()));
//        Uri uri = Uri.parse(practice.getIntroductionUrl());
//        mp = MediaPlayer.create(getContext(), uri);
//        progressBarAudio.setMax(mp.getDuration());

    }


    private void next() {
        Bundle b = new Bundle();
        b.putSerializable(Practice.PRACTICE_KEY, mPractice);
        ((MainActivity)getActivity()).replaceFragment(WatchFragment.FRAGMENT_TAG, b, false);
    }





    private void playAudioInstructions(boolean areInsrtuctionsPlayingNow) {

        if(mPractice == null){
            Dialogs.getAlertDialog(getActivity(), getString(R.string.no_practice_ready_heb), getString(R.string.can_not_play_audio_heb)).show();
            return;
        }
        try {
            if (areInsrtuctionsPlayingNow) {
                if (mp != null) {
                    mp.pause();
                    areInstructionsPlaying = false;
                    if (drawProgressThread != null && !drawProgressThread.isInterrupted())
                        drawProgressThread.interrupt();
                    mHandler.removeCallbacks(drawProgressThread);
                }
            } else {
                //play
                drawProgressThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mp != null) {
                            try {
                                mCurrentPosition = mp.getCurrentPosition();
                                progressBarAudio.setProgress(mCurrentPosition);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                if (ex instanceof IllegalStateException) {
                                    mp.reset();
                                }
                            }
                        }
                        mHandler.postDelayed(drawProgressThread, 100);
                    }
                });
                playAsynchronusThread = new Thread(new Runnable() {
                    public void run() {
                        if (mp != null && !mp.isPlaying()) mp.start();
                        getActivity().runOnUiThread(drawProgressThread);
                    }
                });

                playAsynchronusThread.start();
                areInstructionsPlaying = true;

            }
            //bStartAudio.setImageResource(areInstructionsPlaying == false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);
            bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);
            //areInstructionsPlaying = !areInstructionsPlaying;
        } catch (Exception e){
            e.printStackTrace();
            Dialogs.getAlertDialog(getActivity(), getString(R.string.no_practice_ready_heb), getString(R.string.can_not_play_audio_heb)).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMp();
    }


    private void stopMp(){
        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
            areInstructionsPlaying = false;
        }
    }

    @Override
    public void storagePermissionGranted() {
//        if(ConnectedUser.getInstance().getImage() != null ) {
//            circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
//        } else{
//
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(getClass().getName(), "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD   onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        if (outState == null) return;
        outState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
        outState.putBoolean(IS_PLAYING, areInstructionsPlaying);
        mHandler.removeCallbacks(drawProgressThread);
//        if (mp != null && mp.isPlaying()) {
//            mp.pause();
//        }
//        if(areInstructionsPlaying && mp != null) {
//            mp.pause();
//        }
    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//
//        if (savedInstanceState == null) return;
//        Log.d(getClass().getName(), "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD   onViewStateRestored()");
//        this.mPractice = (Practice)savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
//        this.mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION);
////        mp.seekTo(this.mCurrentPosition);
//        this.areInstructionsPlaying = savedInstanceState.getBoolean(IS_PLAYING);
//        bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);
////        if(areInstructionsPlaying && mp != null) {
////            mp.start();
////        }
//    }
}
