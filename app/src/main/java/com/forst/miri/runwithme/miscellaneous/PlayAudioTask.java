package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by chagithazani on 11/21/17.
 */

public class PlayAudioTask extends AsyncTask<String, Void, Void> {

    private Context mContext = null;
    private MediaPlayer mp = null;
    private String url = null;
    private Uri uri = null;
    private MediaPlayer.OnCompletionListener mOnCompletionListener = null;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = null;

    public PlayAudioTask(Context context, MediaPlayer.OnCompletionListener listener){
        this.mContext = context;
        this.mOnCompletionListener = listener;
    }


    @Override
    protected Void doInBackground(String... args) {
        if(args != null && args.length > 0) {
            this.url = args[0];
            playRecording(true);
        }
        return null;
    }

    private void playRecording(boolean newAudio){//final Practice practice, final Float key) {
        final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        Log.d("PlayAudioTask" , " playRecordingAsynchronous url = ()!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + url);

        //String value = mPractice.getDelaysAndAudioUrls().get(key);
        try {
            if (newAudio || this.uri == null || this.mp == null) {
                this.uri = Uri.parse(url);
                mp = MediaPlayer.create(mContext, uri);
            }
            mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusState) {
                    switch (focusState) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            Log.i("PlayAudioTask*********", " focusState --------------------- AudioManager.AUDIOFOCUS_GAIN");
                            // resume playback
                            if (mp == null) {
                                mp = MediaPlayer.create(mContext, uri);
                            } else if (!mp.isPlaying()) {
                                mp.start();
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            Log.i("PlayAudioTask*********", " focusState --------------------- AudioManager.AUDIOFOCUS_LOSS");
                            // Lost focus for an unbounded amount of time: stop playback and release media player
                            if (mp != null) {
                                if (mp.isPlaying()) {
                                    mp.stop();
                                }
                                mp.release();
                                mp = null;
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            Log.i("PlayAudioTask*********", " focusState --------------------- AudioManager.AUDIOFOCUS_LOSS_TRANSIENT");
                            // Lost focus for a short time, but we have to stop
                            // playback. We don't release the media player because playback
                            // is likely to resume
                            if (mp != null && mp.isPlaying()) mp.pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            Log.i("PlayAudioTask*********", " focusState --------------------- AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                            // Lost focus for a short time, but it's ok to keep playing
                            // at an attenuated level

                            //
                            // if (mp.isPlaying()) mp.setVolume(0.1f, 0.1f); i do not want to lower. the user will miss Miris explanation!!
                            if (mp != null && mp.isPlaying()) mp.pause();
                            break;
                    }
                }
            };

            int result = audioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
//            audioManager.abandonAudioFocusRequest(mAudioFocusListener);

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Log.d("PlayAudioTask", "Media player completed playing (for now :) :) :) :) :) :) :) :) :) url : " + url);
                    finishUp();
                    // audioManager.abandonAudioFocus(listener);
                    if (mOnCompletionListener != null)
                        mOnCompletionListener.onCompletion(mediaPlayer);
                }
            });


            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mp.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                if (mp == null) {
                    mp = MediaPlayer.create(mContext, uri);
                } else if (!mp.isPlaying()) {
                    Log.d("PlayAudioTask", ">>>>>>>>>>>>>> length =  " + mp.getDuration() + " <<<<<<<<<<<<<<<");
                    mp.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void finishUp() {
        Log.d("PlayAudioTask", ">>>>>>>>>>>>>> finishUp( ) <<<<<<<<<<<<<<< url : " + url);
        if(mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        if(mContext != null && mAudioFocusListener != null) {
            AudioManager audioManaget = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManaget.abandonAudioFocus(mAudioFocusListener);
        }
    }

    @Override
    protected void onCancelled() {
        Log.d("PlayAudioTask", ">>>>>>>>>>>>>> onCancelled( ) <<<<<<<<<<<<<<<");
        finishUp();
        super.onCancelled();

    }

    public void stopAudio(){
        finishUp();
    }

    public void pauseAudio(){
        if (mp!= null && mp.isPlaying()) mp.pause();
    }

    public void resumeAudio(){
        if (mp!= null && !mp.isPlaying()) mp.start();
        // playRecording(false); this caused a bug that recordings (mp) was recreated, and waited to get awake after different sound gave back the audio focus.
    }


}
