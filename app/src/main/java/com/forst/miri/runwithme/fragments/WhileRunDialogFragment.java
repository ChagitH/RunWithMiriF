package com.forst.miri.runwithme.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.services.LocationService;

/**
 * Created by chagithazani on 11/12/17.
 */

public class WhileRunDialogFragment extends DialogFragment implements LocationService.UpdateCallback{//SQLiteDatabaseHandler.LocationAddedListener {
    private static final String LISTENER = "listener_";
    private static final String TOTAL_DURATION = "total_duration";
    private static final String TOTAL_DURATION_TICKER = "total_duration_from_ticker";
    private static final String TOTAL_DISTANCE = "totalDistanceInMeters";
    private static final String LOCATION = "loclocationn";
    private static final String RATE = "raratetete";
    private static final String IS_TIMER_RUNNING = "istimerrunning_";
    private static final String FIRST_TIME_KEY = "first_time";
    private static final String BOUND_KEY = "bound_to_service_key";

    private TextView tvDistance, tvDuration, tvRate, tvLookingForGps, lookingForGPSCountdown;
    private ProgressBar pb = null;
    private long totalDuration = 0;
//    private long totalDurationFromTicker = 0;
    private float totalDistanceInMeters = 0;
    private long rate = 0;
    private boolean isTimerRunning = false;
    private LocationService locationService = null;
    private Intent serviceIntent;
    private boolean firstTime = true;
    private boolean bound = false;
    private ImageView pauseButton, stopButton, resumeButton;
    private int dialogWidth , dialogHeight;
    private Animation tvGpsAnimation = null;

    //TickerAsyncTask secondsTicker;


    private boolean animationOn = true;

//    private class TickerAsyncTask extends AsyncTask<Void, Long, Void>{
//
//        private long previousTimeStamp = 0;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            previousTimeStamp = System.currentTimeMillis();
//            totalDurationFromTicker = totalDuration;
//
//            while(!isCancelled()) {
//                try {
////                    synchronized (this) {
////                        this.wait(2000);
////                    }
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                long currentTimeStamp = System.currentTimeMillis();
//                publishProgress(new Long(currentTimeStamp), new Long(previousTimeStamp));
//                previousTimeStamp = currentTimeStamp;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Long... values) {
//            //super.onProgressUpdate(values);
//
//            //final Context fContext = context;
//            totalDurationFromTicker += values[0] - values[1];
//            Log.d(getClass().getName() , " onProgressUpdate() $ * $ * $ * $  $ * $ * $ * $ $ * $ * $ * $ $ * $ * $ * $ $ * $ * $ * $ $ * $ * $ * $ $ * $ * $ * $ $ * $ * $ * $ totalDurationFromTicker = " + totalDurationFromTicker);
//            presentDuration(UIHelper.formatTime(totalDurationFromTicker));
//        }
//    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) iBinder;
            locationService = binder.getServerInstance();//Get instance of service
            locationService.addUpdateCallback(WhileRunDialogFragment.this); //register in the service for callbacks
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    private void presentOnUI(final String rate, final String distance, final String duration) {

        tvDistance.setText(distance);
        tvRate.setText(rate);
        //presentDuration(duration);

    }

    private void presentDuration(String duration){
        tvDuration.setText(duration);
    }


    private void vibrate(){
        if(getActivity() != null) {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
    }

    private void showAnimation(){
        if(pb != null) pb.setVisibility(View.VISIBLE);
        if(pauseButton != null) pauseButton.setEnabled(false);
        turnOnSearchingForGpsAnimation();
        animationOn = true;
    }

    private void hideAnimation(){
        if(pb != null) pb.setVisibility(View.GONE);
        if(pauseButton != null) pauseButton.setEnabled(true);
        turnOffSearchingForGpsAnimation();
        animationOn = false;
    }

    private void setAnimation(){
        if(animationOn){
            showAnimation();
        } else {
            hideAnimation();
        }
    }

    @Override
    public void update(long duration, long rate, float distance) {
        if(firstTime){
            vibrate();
            hideAnimation();
//            startTicker();
            firstTime = false;
        }
//        this.totalDuration = duration;
        this.rate = rate;
        this.totalDistanceInMeters = distance;
        double distanceInKm = (double)( distance / (double) 1000);
        presentOnUI(UIHelper.formatTime(rate), String.format("%1$.2f", distanceInKm), UIHelper.formatTime(duration));
    }

    @Override
    public void reportError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        //Dialogs.showErrorDialog(getContext(), error, null);
    }

    @Override
    public void countDown(String count) {
        if(lookingForGPSCountdown != null) lookingForGPSCountdown.setText(count);
//        if(tvLookingForGps != null) tvLookingForGps.setText(count);

    }

    @Override
    public void stopAnimation() {
        hideAnimation();

        //todo should i do all 4?
//        vibrate();
//        hideAnimation();
//        startTicker();
//        firstTime = false;
    }

    @Override
    public void updateDuration(final long durationFromTimer) {
        this.totalDuration = durationFromTimer;
        Activity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(new Runnable() //run on ui thread
            {
                public void run() {
                    presentDuration(UIHelper.formatTime(durationFromTimer));

                }
            });
        }


    }


//    private void startTicker(){
//        secondsTicker = new TickerAsyncTask();
//        secondsTicker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//
//
//    private void stopTicker() {
//        if (secondsTicker != null) {
//            if (!secondsTicker.isCancelled()) secondsTicker.cancel(true);
//            secondsTicker = null;
//        }
//    }

    public interface YesListener extends Parcelable {
        void onYes();

    }

    private YesListener listener = null;


    public WhileRunDialogFragment() {
    }

    public static WhileRunDialogFragment getAndSetArguments(YesListener listener) {
        Bundle b = new Bundle();
        b.putParcelable(LISTENER, listener);
        WhileRunDialogFragment f = new WhileRunDialogFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog d = getDialog();
        if(d != null){
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        }

        final View view = inflater.inflate(R.layout.while_run_fragment, container, false);
        Bundle b = getArguments();
        if( b != null){
            listener = (YesListener) b.getParcelable(LISTENER);
        }

        pauseButton = (ImageView)view.findViewById(R.id.animation_circle);
        stopButton = (ImageView)view.findViewById(R.id.iv_stop_run_button);
        resumeButton = (ImageView)view.findViewById(R.id.iv_resume_run_button);


        tvDistance = (TextView)view.findViewById(R.id.while_run_distance_tv);
        tvDuration = (TextView)view.findViewById(R.id.while_run_duration_tv);
        tvRate = (TextView)view.findViewById(R.id.while_run_rate_tv);
        tvLookingForGps = (TextView)view.findViewById(R.id.while_run_looking_for_gps_tv);
        lookingForGPSCountdown = (TextView)view.findViewById(R.id.animation_pb_text_countdown);

        pb = (ProgressBar) view.findViewById(R.id.animation_pb);

        serviceIntent = new Intent(getActivity().getApplicationContext(), LocationService.class);

        if( getActivity() != null ) getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);//will this solve vol being very low? now it is controlled by user

        final ConstraintLayout llTotal = (ConstraintLayout) view.findViewById(R.id.while_run_main_frame);
        final ViewTreeObserver vto = llTotal.getViewTreeObserver();
        final ViewTreeObserver.OnGlobalLayoutListener llListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llTotal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                dialogWidth  = llTotal.getMeasuredWidth();
                dialogHeight = llTotal.getMeasuredHeight();
//                Log.d(getClass().getName(), "-> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -onGlobalLayout-> -> -> -> dialogWidth = " + dialogWidth);
//                Log.d(getClass().getName(), "-> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> onGlobalLayout-> -> -> ->  dialogHeight = " + dialogHeight);
//                final int translationX = dialogWidth / 3;
//                Log.d(getClass().getName(), "-> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> translationX = " + translationX);

                final int buttonWidth = pauseButton.getMeasuredWidth();
//                Log.d(getClass().getName(), "-> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> pauseButton.getMeasuredWidth() = " + buttonWidth);


                final double trans = ((dialogWidth - (2* buttonWidth)))/2;
//                Log.d(getClass().getName(), "-> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> -> trans = " + trans);

                pauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pauseRun();
                        resumeButton.animate().translationX((int)-(trans + (buttonWidth/3))).setDuration(350).start();
                        resumeButton.setEnabled(true);
                        stopButton.animate().translationX((buttonWidth/3)+(int)trans).setDuration(350).start();
                        stopButton.setEnabled(true);
                        pauseButton.animate().alpha(0).start();
                        pauseButton.setEnabled(false);
                    }
                });


                resumeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resumeRun();
                        resumeButton.animate().x(pauseButton.getX()).setDuration(350).start();
                        resumeButton.setEnabled(false);
                        stopButton.animate().x(pauseButton.getX()).setDuration(350).start();
                        stopButton.setEnabled(false);
                        pauseButton.animate().alpha(1).start();
                        pauseButton.setEnabled(true);

                    }
                });

                stopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialogs.showYesNoErrorDialog(getContext(), getString(R.string.are_you_sure_you_want_to_stop_heb), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (view.getTag() == Dialogs.YES){
//                                    disconnectFromService();
//                                    if(listener != null) listener.onYes();
                                    stopRunning();
                                }
                            }
                        }).show();
                    }
                });


            }
        };

        if(vto.isAlive()){
            vto.addOnGlobalLayoutListener(llListener);
        }

        return view;
    }




    private void turnOnSearchingForGpsAnimation() {
        setCancelableHolistic(true);
        tvGpsAnimation = new AlphaAnimation(0.0f, 1.0f);
        tvGpsAnimation.setDuration(300);
        tvGpsAnimation.setStartOffset(20);
        tvGpsAnimation.setRepeatMode(Animation.REVERSE);
        tvGpsAnimation.setRepeatCount(Animation.INFINITE);
        tvLookingForGps.startAnimation(tvGpsAnimation);

        tvLookingForGps.setVisibility(View.VISIBLE);
    }

    private void turnOffSearchingForGpsAnimation() {
        setCancelableHolistic(false);
        if(tvGpsAnimation != null){
            tvGpsAnimation.cancel();
            tvLookingForGps.clearAnimation();
        }
        tvLookingForGps.setVisibility(View.INVISIBLE);
        lookingForGPSCountdown.setVisibility(View.INVISIBLE);
    }

    private void pauseRun() {
        if(bound && locationService != null){
            locationService.pause();
        }
        vibrate();
        //firstTime = true;
        //stopTicker();
    }

    private void resumeRun() {
        //showAnimation();
        if(bound && locationService != null){
            locationService.resume();
        }
        vibrate();
//        startTicker();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//    }

    @Override
    public void onStart()    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null){

            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            setCancelableHolistic(false);

            setAnimation();
        }
    }

    private void setCancelableHolistic(boolean cancelable){
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(cancelable);
            dialog.setCancelable(cancelable);
            if(cancelable) {
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if(bound && locationService != null){
                            locationService.stop();
                        }
                        stopRunning();
                    }
                });
            } else {
                dialog.setOnCancelListener(null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        connectToService();

        //if(isTimerRunning) secondsTimer.execute();secondsTicker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onPause() {
        super.onPause();
        disconnectFromService();
    }


    private void connectToService(){
        if(!bound) {
            getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            bound = true;

        }
    }

    private void disconnectFromService(){
        if(bound) {

            getActivity().unbindService(serviceConnection);
            bound = false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //destroy ticker
//        stopTicker();
        animationOn = true;
        //stopRunning();
    }

    private void stopRunning() {
        disconnectFromService();
        if(listener != null) listener.onYes();
//        stopTicker();
        firstTime = true;
        animationOn = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FIRST_TIME_KEY, firstTime);
        outState.putBoolean(BOUND_KEY, bound);
        outState.putLong(TOTAL_DURATION, totalDuration);
        outState.putFloat(TOTAL_DISTANCE, totalDistanceInMeters);
        outState.putLong(RATE, rate);
        outState.putParcelable(LISTENER, listener);
//        outState.putLong(TOTAL_DURATION_TICKER, totalDurationFromTicker);
        outState.putBoolean("animation", animationOn);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            firstTime = savedInstanceState.getBoolean(FIRST_TIME_KEY);
            bound = savedInstanceState.getBoolean(BOUND_KEY);
            totalDuration = savedInstanceState.getLong(TOTAL_DURATION);
            totalDistanceInMeters = savedInstanceState.getFloat(TOTAL_DISTANCE);
            rate = savedInstanceState.getLong(RATE);
            listener = savedInstanceState.getParcelable(LISTENER);
            update(totalDuration, rate, totalDistanceInMeters);
//            totalDurationFromTicker = savedInstanceState.getLong(TOTAL_DURATION_TICKER);
            animationOn = savedInstanceState.getBoolean("animation");
        }
        super.onViewStateRestored(savedInstanceState);
    }


}

