package com.forst.miri.runwithme.fragments;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.DayAndHourPickerDialog;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.User;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingPlanFragment extends ParentFragment {


    public static final String FRAGMENT_TAG = TrainingPlanFragment.class.getSimpleName();


    private Practice mPractice = null;
    private static String IS_PLAYING = "instructions_are_playing";
    private static String CURRENT_POSITION = "current_P" ;
    private static String NEXT_PRACTICE_DATE = "next_practice_date" ;
    private ProgressBar progressBarAudio;
    boolean areInstructionsPlaying = false;
    private MediaPlayer mp;
    private CircleImageView circleImageView;
    private TextView tvHooray, tvProgress, tvLastTraining, tvNextTrainingNum, tvNextTraining, tvReSchedule, tvExpirationDate , tvPlanName;
    private ImageButton bStartAudio;
    private int mCurrentPosition = 0;
    private Handler mHandler = new Handler();
    private Thread playAsynchronusThread, drawProgressThread;
    private Button bGoToScheduling;
    private Date mNextPracticeDate = null;

    public TrainingPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_training_plan;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null){
            this.mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            this.areInstructionsPlaying = savedInstanceState.getBoolean(IS_PLAYING);
            Serializable serDate = savedInstanceState.getSerializable(NEXT_PRACTICE_DATE);
            if(serDate != null) this.mNextPracticeDate = (Date)serDate;
        }
        //if (mPractice == null){
            //User user = User.createUserFromSharedPreferences(getContext());
            mPractice = Practice.createFromSharedPreferences(getContext(), ConnectedUser.getLessonNum());
            //mPractice = Practice.createFromSharedPreferences(getContext(), user.getPracticeNum());
        //}

        View rootView = inflater.inflate(R.layout.fragment_training_plan, container, false);

        circleImageView = (CircleImageView) rootView.findViewById(R.id.program_fragment_small_image_circle);
        tvHooray = (TextView) rootView.findViewById(R.id.program_fragment_hooray_textview);
        tvProgress = (TextView) rootView.findViewById(R.id.program_fragment_progress_textview);
        tvPlanName = (TextView) rootView.findViewById(R.id.program_fragment_training_plan_name_textview);

        tvLastTraining = (TextView) rootView.findViewById(R.id.program_fragment_last_training_day_textview);
        //tvLastTraining.setText();
        tvNextTrainingNum = (TextView) rootView.findViewById(R.id.program_fragment_training_num_textview);
        //tvNextTrainingNum.setText();
        tvNextTraining = (TextView) rootView.findViewById(R.id.program_fragment_next_training_day_textview);
        //tvNextTraining.setText();
        tvReSchedule = (TextView) rootView.findViewById(R.id.program_fragment_to_change_next_training_day_textview);
        //tvReSchedule.setTypeface(R.);


        tvExpirationDate = (TextView) rootView.findViewById(R.id.program_fragment_program_expiration_textview);
        progressBarAudio = (ProgressBar) rootView.findViewById(R.id.program_fragment_audio_progress_bar);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_line);
        progressBarAudio.setProgressDrawable(drawable);
        progressBarAudio.setRotation(180);
        bStartAudio = (ImageButton) rootView.findViewById(R.id.program_fragment_play_imageview);
        bStartAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startAudio();
                playAudioInstructions(areInstructionsPlaying);
            }
        });
        bGoToScheduling = (Button) rootView.findViewById(R.id.program_fragment_go_to_set_days_button);
        bGoToScheduling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).replaceFragment(SetDayAndTimePracticeFragment.FRAGMENT_TAG, null, false);
            }
        });

        //setUpFragment();

        return rootView;
    }

    private void setUpFragment(){
        User connectedUser = ConnectedUser.getInstance();
        if(connectedUser == null) return;

        Bitmap userImage = connectedUser.getImage();
        if(userImage != null){
            try{
                circleImageView.setImageBitmap(userImage);
                circleImageView.setVisibility(View.VISIBLE);
            } catch (Exception e){
                e.printStackTrace();
                circleImageView.setVisibility(View.INVISIBLE);
            }
        }

        PracticeData lastPracticeData = connectedUser.getLastPractice(getContext(), connectedUser.getEmail());
        int lastPracticeNum = 0;
        if(lastPracticeData != null ) lastPracticeNum = lastPracticeData.getNumOfLessonInPlan();
        int nextPracticeNum = getNextPracticeNum(lastPracticeNum, connectedUser);
        if(nextPracticeNum == -1) redirectToRegisterToPlan();

        tvHooray.setText(getString(R.string.hooray_hev) + " " + connectedUser.getFirstName());
        tvProgress.setText(Practice.getProgressString(getContext(), lastPracticeNum));
        tvPlanName.setText(connectedUser.getPlanName());


        if(lastPracticeData != null ) {
            String formattedDateLastPractice = UIHelper.formatDate(lastPracticeData.getDate());
            tvLastTraining.setText(formattedDateLastPractice);
        }
        tvNextTrainingNum.setText(getString(R.string.num_heb) + " " + nextPracticeNum);

        //final Date nextPracticeDate = null;
        if(mNextPracticeDate == null) {
            getNextPracticeDate(new GetResponseCallback() {
                @Override
                public void requestStarted() {
                    Log.d(TrainingPlanFragment.class.getName(), " requestStarted ");
                }

                @Override
                public void requestCompleted(String response) {
                    Log.d(TrainingPlanFragment.class.getName(), " completed response = " + response);
                    //nextPracticeDate =
                    if (mNextPracticeDate != null)
                        setNextTrainingTV(mNextPracticeDate);
                }

                @Override
                public void requestEndedWithError(VolleyError error) {
                    Log.d(TrainingPlanFragment.class.getName(), " requestEndedWithError error = " + error);
                    setNextTrainingTV(null);
                }
            });
        } else {
            setNextTrainingTV(mNextPracticeDate);
        }



        tvReSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTrainingTimeDialog();

            }
        });

        Date expDate = connectedUser.getExpirationOfPlan();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        tvExpirationDate.setText(expDate == null ? getString(R.string.expiration_heb) : format.format(expDate));


        if( getActivity() != null ) getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setUpMp();
    }

    public void updateTrainingTimeDialog(){
        if(NetworkCenterHelper.isNetworkAvailable(getContext())) {
            //final TextView tv = (TextView) view;
            DayAndHourPickerDialog dayHourDialog = new DayAndHourPickerDialog(getContext(), mNextPracticeDate, new DayAndHourPickerDialog.OnDayAndHourSelectedListener() {
                @Override
                public void OnDayHourSelected(final int day, final int hour, final int minute) {
                    String dayStr = UIHelper.getHebDay(getContext(), day);
                    String timeStr = String.format("%02d:", hour) + String.format("%02d", minute);
                    final Date date = UIHelper.getDate(day, hour, minute);
                    String dateStr = UIHelper.formatDate(date);
                    Dialogs.showYesNoErrorDialog(getContext(), getString(R.string.the_training_was_rescheduled_to_heb) + " " + dayStr + '\n' + dateStr + '\n' + getString(R.string.at_hour_heb) + " " + timeStr, null, getString(R.string.save_heb), getString(R.string.cancel_heb), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Integer tag = (Integer) view.getTag();
                            if (tag == Dialogs.YES) {
                                mNextPracticeDate = date;
                                rescheduleTraining(date);
                            } else {
                                //do nothing
                            }
                        }
                    }).show();
                }
            });
            dayHourDialog.show();
        } else {
            Dialogs.getAlertDialog(getContext(), getString(R.string.cannot_connect_to_server), getString(R.string.no_internet_connection)).show();
        }
    }


    private void setNextTrainingTV(Date mNextPracticeDate) {
        if(getContext() == null) return; //added on 30.4.2018 maybe will fix exception Miri got in the middle of night

        if(mNextPracticeDate!= null) {
            tvNextTraining.setText(getString(R.string.will_be_on_heb) + " " + dateToString(mNextPracticeDate));
        } else{
            tvNextTraining.setText(getString(R.string.will_be_on_heb) + " " + getString(R.string.not_available_heb));
        }
    }

    Dialog waitD = null;
    @Override
    public void onStart() {
        super.onStart();

//        new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                waitD = Dialogs.showSpinningWheelDialog(getActivity()) ;
//                if (waitD != null && waitD.isShowing()) waitD.show();
//            }
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//
//                setUpFragment();
//                if (waitD != null && waitD.isShowing()) waitD.dismiss();
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                waitD = Dialogs.showSpinningWheelDialog(getActivity()) ;
                if (waitD != null && waitD.isShowing()) waitD.show();
                setUpFragment();
                if (waitD != null && waitD.isShowing()) waitD.dismiss();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mp != null && drawProgressThread != null) getActivity().runOnUiThread(drawProgressThread);
        if(bStartAudio != null ) bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.ic_pause_white_18dp : R.drawable.ic_play_arrow_white_18dp);

    }

    private void setUpMp(){
        if(this.mPractice == null) return;

        //if(! areInstructionsPlaying ) {
        bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.ic_pause_white_18dp : R.drawable.ic_play_arrow_white_18dp);

        try {
            Uri uri = Uri.parse(mPractice.getIntroductionUrl());
            mp = MediaPlayer.create(getContext(), uri);
            mp.seekTo(this.mCurrentPosition);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Log.d(ListenFragment.class.getName(), "DDDDDDDDDDDDD onCompletion DDDDDDDDDDD onCompletion DDDDDDDDDDDDDD onCompletion DDDDDDDDDD");
                    areInstructionsPlaying = false;
                    bStartAudio.setImageResource(R.drawable.ic_play_arrow_white_18dp);
                    progressBarAudio.setProgress(1);
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

    private void playAudioInstructions(boolean areInsrtuctionsPlayingNow) {

        if(mPractice == null){
            Dialogs.getAlertDialog(getActivity(), getString(R.string.no_practice_ready_heb), getString(R.string.can_not_play_audio_heb)).show();
            return;
        }

        if (areInsrtuctionsPlayingNow) {
            if(mp != null){
                mp.pause();
                areInstructionsPlaying = false;
                if( drawProgressThread != null && !drawProgressThread.isInterrupted() ) drawProgressThread.interrupt();
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
                            if(ex instanceof IllegalStateException){
                                mp.reset();
                            }
                        }
                    }
                    mHandler.postDelayed(drawProgressThread, 100);
                }
            });
            playAsynchronusThread = new Thread(new Runnable() {
                public void run() {
                    if(mp != null && ! mp.isPlaying()) mp.start();
                    getActivity().runOnUiThread(drawProgressThread);
                }
            });

            playAsynchronusThread.start();
            areInstructionsPlaying = true;

        }
        //bStartAudio.setImageResource(areInstructionsPlaying == false ? R.drawable.purple_circle_pause : R.drawable.purple_circle_play);
        bStartAudio.setImageResource(areInstructionsPlaying != false ? R.drawable.ic_pause_white_18dp : R.drawable.ic_play_arrow_white_18dp);
        //areInstructionsPlaying = !areInstructionsPlaying;
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

    private void redirectToRegisterToPlan() {
        Dialogs.getAlertDialog(getContext(), getString(R.string.you_are_not_registered_to_run_with_miri_please_register), getString(R.string.for_your_attention)).show();
        ((MainActivity) getActivity()).replaceFragment(TrainingPlanRegistrationFragment.FRAGMENT_TAG, null, false);
    }

    private int getNextPracticeNum(int lastPracticeNum, User user){
        if(user == null) return -1;
        else {
            if(user.getPlanName() != null && user.getExpirationOfPlan() != null && user.getExpirationOfPlan().after(new Date())) {
                return user.getPracticeNum();
            }
        }
        return -1;

//        if(lastPracticeNum < 0) return -1;
//        if(lastPracticeNum > user.getTotalAmount()) return -1;
//        if(user.getExpirationOfPlan() == null) return -1;
//        if(user.getExpirationOfPlan().after(new Date()) && lastPracticeNum <= user.getTotalAmount()) {
//            return lastPracticeNum+1;
//        } else {
//            return -1;
//        }
    }


    private void getNextPracticeDate(final GetResponseCallback callback){

        if(NetworkCenterHelper.isNetworkAvailable(getContext())) {
            NetworkCenterHelper.getNextScheduledTraining(getContext(), ConnectedUser.getInstance(), new GetResponseCallback() {
                @Override
                public void requestStarted() {
                    Log.d(TrainingPlanFragment.class.getName(), " -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* getNextPractice - Started()");
                    if (callback != null) callback.requestStarted();
                }

                @Override
                public void requestCompleted(String response) {
                    Log.d(TrainingPlanFragment.class.getName(), " -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* getNextPractice - requestCompleted() " + response);
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String date = jsonObject.getString("inDate");
                            String when = jsonObject.getString("when");
                            if (date != null && when != null) {
                                mNextPracticeDate = createDate(date, when);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (callback != null) callback.requestCompleted(response);
                }

                @Override
                public void requestEndedWithError(VolleyError error) {
                    if (error != null) error.printStackTrace();
                    Log.d(TrainingPlanFragment.class.getName(), " -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* getNextPractice - requestEndedWithError (-)");
                    if (callback != null) callback.requestEndedWithError(error);
                }
            });
        } else {
            if (callback != null) callback.requestEndedWithError(null);
        }

    }

    public static Date createDate(String date, String when) {
        if(date == null || when == null) return null;
        try {
            Calendar cal = Calendar.getInstance();
            StringTokenizer stDate = new StringTokenizer(date, "-", false);
            if (stDate == null) return null;
            String yearStr = stDate.nextToken();
            String monthStr = stDate.nextToken();
            String dayStr = stDate.nextToken();

            StringTokenizer stTime = new StringTokenizer(when, ":", false);
            if (stTime == null) return null;
            String hourStr = stTime.nextToken();
            String minuteStr = stTime.nextToken();

            Log.d(TrainingPlanFragment.FRAGMENT_TAG," ------------- createDate(String date, String when) " + hourStr + ":" + minuteStr + " " + dayStr + "-" + monthStr + "-" + yearStr);
            int year = Integer.parseInt(yearStr);
            int month = (Integer.parseInt(monthStr))-1;
            int day = Integer.parseInt(dayStr);
            int hour = Integer.parseInt(hourStr);
            int minute = Integer.parseInt(minuteStr);

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            return cal.getTime();

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String dateToString(Date nextPracticeDate) {
        if (nextPracticeDate == null) return getString(R.string.not_available_heb);
        try {
            StringBuilder sb = new StringBuilder();
            Calendar cal = Calendar.getInstance();
            cal.setTime(nextPracticeDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);
            sb.append(UIHelper.getHebDay(getContext(), dayOfWeek) + " ");
            sb.append(getString(R.string.at_heb) + " ");
            sb.append(String.format("%02d:", hour));
            sb.append(String.format("%02d", minutes));

            return sb.toString();
        } catch (Exception e){
            e.printStackTrace();
            return getString(R.string.not_available_heb);
        }
    }

    private boolean isDateIsInFuture(Date nextPracticeDate) {
        if (nextPracticeDate == null) return false;
        return nextPracticeDate.after(new Date());
    }

    private Date getDate(String reminderTime, int today, int trainingDay) {
        Calendar cal = Calendar.getInstance();

        //get Next Training days date
        int numDays = 7 - ((today - trainingDay) % 7 + 7) % 7;
        cal.add(Calendar.DAY_OF_YEAR, numDays);


        //adding Next Training days time
        StringTokenizer st = new StringTokenizer(reminderTime, ":");
        int hour = Integer.parseInt(st.nextElement().toString());
        int minute = Integer.parseInt(st.nextElement().toString());

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        return cal.getTime();
    }

    private void rescheduleTraining(Date selectedDateTime) {
        if( ConnectedUser.getInstance() == null ) return;

        NetworkCenterHelper.rescheduleTraining(getContext(), ConnectedUser.getInstance(), selectedDateTime, new PostResponseCallback() {
            @Override
            public void requestStarted() {
                Log.d(getFragmentName(), "*-*-*-* *-*-*-* rescheduleTraining - started *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-*");
            }

            @Override
            public void requestCompleted(String response) {
                Log.d(getFragmentName(), "*-*-*-* *-*-*-* rescheduleTraining - requestCompleted response : *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* " + '\n' + response);
                if(response != null){

                    setNextTrainingTV(mNextPracticeDate);

                }
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.d(getFragmentName(), "*-*-*-* *-*-*-* rescheduleTraining - requestEndedWithError  *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-*");
                if (error != null) error.printStackTrace();
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) return;
        outState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
        outState.putBoolean(IS_PLAYING, areInstructionsPlaying);
        outState.putSerializable(NEXT_PRACTICE_DATE, mNextPracticeDate);
        mHandler.removeCallbacks(drawProgressThread);

    }



    @Override
    public void storagePermissionGranted() {

    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }
}
