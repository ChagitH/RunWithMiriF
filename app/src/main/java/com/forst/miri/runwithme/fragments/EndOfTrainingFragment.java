package com.forst.miri.runwithme.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class EndOfTrainingFragment extends ParentFragment{

    public static final String FRAGMENT_TAG = EndOfTrainingFragment.class.getSimpleName();
    private static final java.lang.String PLAYING_MOVIE = "playing_movie_";
    public static final int YOUTUBE_MOVIE_INTENT = 857;
    private static final String NEXT_PRACTICE_DATE_TAG = "next_prectice_date";
    //private YouTubePlayerSupportFragment youTubePlayerFragment;
    //private boolean youtubeInitialized = false;
    //private YouTubePlayer mYouTubePlayer = null;
//    private boolean nextOption = true;
    private LinearLayout bBackNext;
    private String firstPostRunMovie, secondPostRunMovie;
    private TextView tvMovieTitle;
    private ImageView arrowForward;
    private ImageView arrowBack;
    private TextView buttonText;
    private Practice mPractice;
    private int playingMovie = 1;
    private YouTubeThumbnailView mThumbnailView;
    private YouTubeThumbnailLoader mYouTubeThumbnailLoader;
    private Button bSendFeedback, bIFinished;
    private Date mNextPracticeDate = null;

    @Override
    public int getResourceID() {
        return R.layout.fragment_end_of_training;
    }

    public EndOfTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.mPractice = (Practice)savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
            playingMovie = savedInstanceState.getInt(PLAYING_MOVIE);
            Serializable sDate = savedInstanceState.getSerializable(NEXT_PRACTICE_DATE_TAG);
            if(sDate != null) mNextPracticeDate = (Date) sDate;
        } else {
            Bundle bundle = getArguments();
            this.mPractice = (Practice)bundle.getSerializable(Practice.PRACTICE_KEY);
        }

        // Inflate the layout for this fragment
        View parent =  inflater.inflate(R.layout.fragment_end_of_training, container, false);

        mThumbnailView = (YouTubeThumbnailView)parent.findViewById(R.id.post_run_fragment_imageView_thumbnail);


        firstPostRunMovie = mPractice.getPostRunMovieNum1();
        secondPostRunMovie = mPractice.getPostRunMovieNum2();


        bBackNext = (LinearLayout) parent.findViewById(R.id.end_of_practice_button_next_back);
        arrowForward = (ImageView)parent.findViewById(R.id.end_of_practice_button_next_back_forward_arrow);
        arrowBack = (ImageView)parent.findViewById(R.id.end_of_practice_button_next_back_back_arrow);
        buttonText = (TextView)parent.findViewById(R.id.end_of_practice_button_next_back_text);



        bSendFeedback = (Button) parent.findViewById(R.id.end_of_practice_button_send_feedback);
        bSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int practiceNum = 0;
                if(mPractice != null) practiceNum = mPractice.getPracticeNum();
                Dialogs.openFeedbackDialog(getContext(), practiceNum);

//                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
//                int practiceNum = 0;
//                if(mPractice != null) practiceNum = mPractice.getPracticeNum();
//                intent.putExtra(PracticeData.PRACTICE_NUM_KEY, practiceNum);
//                startActivity(intent);
            }
        });

        bIFinished = (Button) parent.findViewById(R.id.end_of_practice_button_i_finished);
        bIFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int numOfPractice = mPractice.getPracticeNum();
//                Dialogs.showEndOfPracticeDialog(getContext(), numOfPractice);
                openGoodByDialog();
            }
        });

        tvMovieTitle = (TextView) parent.findViewById(R.id.end_of_practice_fragment_movie_title_textview) ;

        setButtonsAvailabilityAndTitle(playingMovie);
        return parent;
    }



    @Override
    public void onStart() {
        super.onStart();

        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(getActivity(), 0).show();
        } else {
            mThumbnailView.initialize(getString(R.string.google_api_key), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    //mLoaders.put(view, loader);
                    //loader.setVideo((String) view.getTag());
                    mYouTubeThumbnailLoader = youTubeThumbnailLoader;
                    mThumbnailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playMovie(playingMovie);
                        }
                    });
                    getThumbReady(playingMovie);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    final String errorMessage = youTubeInitializationResult.toString();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

        bBackNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playingMovie == 1){
                    //getMovieReady(secondPostRunMovie);
                    getThumbReady(secondPostRunMovie);
                    playingMovie = 2;
                    setButtonsAvailabilityAndTitle(playingMovie);
                } else {
                    //getMovieReady(firstPostRunMovie);
                    getThumbReady(firstPostRunMovie);
                    playingMovie = 1;
                    setButtonsAvailabilityAndTitle(playingMovie);
                }
            }
        });


        fetchNextPractice();

    }

    private void getThumbReady(String postRunMovieAddress) {
        if(mYouTubeThumbnailLoader != null) mYouTubeThumbnailLoader.setVideo(postRunMovieAddress);
    }

    private void getThumbReady(int playingMovie) {
        getThumbReady(playingMovie == 1 ? firstPostRunMovie : secondPostRunMovie );
    }

    private void setButtonsAvailabilityAndTitle(int moviePlaying) {
        if( moviePlaying == 1 ) {
            buttonText.setText(getString(R.string.to_heb) + getString(R.string.second_set_post_run_excersize_heb));
            arrowForward.setVisibility(View.VISIBLE);
            arrowBack.setVisibility(View.INVISIBLE);
            tvMovieTitle.setText( getString( R.string.first_set_post_run_excersize_heb));
            bIFinished.setVisibility(View.INVISIBLE);
            bSendFeedback.setVisibility(View.INVISIBLE);
        } else {
            buttonText.setText(getString(R.string.to_heb) + getString(R.string.first_set_post_run_excersize_heb));
            arrowForward.setVisibility(View.INVISIBLE);
            arrowBack.setVisibility(View.VISIBLE);
            tvMovieTitle.setText(  getString( R.string.second_set_post_run_excersize_heb));
            bIFinished.setVisibility(View.VISIBLE);
            bSendFeedback.setVisibility(View.VISIBLE);
        }
    }

    private void playMovie(int playingMovie) {
        playMovie(playingMovie == 1 ? firstPostRunMovie : secondPostRunMovie);
    }

    private void playMovie(final String address) {
        if(mYouTubeThumbnailLoader != null) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), getString(R.string.google_api_key), address, 0, true, false);
//            startActivity(intent);
            startActivityForResult(intent, YOUTUBE_MOVIE_INTENT);
        }



    }


    private void fetchNextPractice(){
        if(NetworkCenterHelper.isNetworkAvailable(getContext())) {
            NetworkCenterHelper.getNextScheduledTraining(getContext(), ConnectedUser.getInstance(), new GetResponseCallback() {
                @Override
                public void requestStarted() {
                    Log.d(TrainingPlanFragment.class.getName(), " -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* getNextPractice - Started()");

                }

                @Override
                public void requestCompleted(String response) {
                    Log.d(TrainingPlanFragment.class.getName(), " -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* getNextPractice - requestCompleted() " + response);
                    if(response != null){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String date = jsonObject.getString("inDate");
                            String when = jsonObject.getString("when");
                            if(date != null && when != null){
                                mNextPracticeDate = TrainingPlanFragment.createDate(date, when);
                            } else {
                                mNextPracticeDate = null;
                            }
                        } catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void requestEndedWithError(VolleyError error) {
                    if(error != null) error.printStackTrace();
                    Log.d(TrainingPlanFragment.class.getName(), " -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* getNextPractice - requestEndedWithError (-)");
                }
            });
        }


    }

    private void  openGoodByDialog() {
        //final Dialog alertDialog;
        StringBuilder updatedMessage = new StringBuilder(Practice.getPepString(getContext(), mPractice.getPracticeNum())).append("\n").append("\n");
        updatedMessage.append(getString(R.string.you_just_finished_train_num)).append(" ");
        updatedMessage.append(mPractice.getPracticeNum()).append("\n");
        updatedMessage.append(getString(R.string.we_will_meet_heb)).append( " ");
        updatedMessage.append(getString(R.string.in_the_next_training2_heb)).append("\n");
        updatedMessage.append(getString(R.string.at_heb) + " ");
        updatedMessage.append(mNextPracticeDate != null ? UIHelper.formatDate(mNextPracticeDate) + " " + getString(R.string.at_hour_heb) + " " + UIHelper.formatDateToTimeOnly(mNextPracticeDate) : getString(R.string.not_available_heb));

        Dialogs.getEndOfTrainingDialog(getContext(), updatedMessage, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo Miri wanted to close Fragment. I disagree.
            }
        }).show();



    }


    private void releaseYoutubePlayer(){
//        if(mYouTubePlayer != null) mYouTubePlayer.release();
        if(mYouTubeThumbnailLoader!= null) mYouTubeThumbnailLoader.release();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseYoutubePlayer();
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
                //todo: if fragment still open - write in new scheduled lesson.
                //todo---------------------       open a dialog like in Training Plan Fragment, with a conclusion
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.d(getFragmentName(), "*-*-*-* *-*-*-* rescheduleTraining - requestEndedWithError  *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-* *-*-*-*");
                if(error != null) error.printStackTrace();
            }
        });
    }

    @Override
    public void storagePermissionGranted() {

    }

    /**
     * Saves the state of the map when the fragment is ??????.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        outState.putInt(PLAYING_MOVIE, playingMovie);
        outState.putSerializable(NEXT_PRACTICE_DATE_TAG, mNextPracticeDate);
    }
}
