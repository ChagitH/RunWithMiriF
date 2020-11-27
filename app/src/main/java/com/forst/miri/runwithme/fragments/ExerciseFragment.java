package com.forst.miri.runwithme.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExerciseFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = ExerciseFragment.class.getSimpleName();
    private static final java.lang.String SELECTED_POSITION_KEY = "selected_position_key";
    //private YouTubePlayerSupportFragment youTubePlayerFragment;
    //private boolean youtubeInitialized = false;
    //private YouTubePlayer mYouTubePlayer = null;
    private CircleImageView circleImageView;
    private TextView tvHello;
    private Spinner spinner;
    private YouTubeThumbnailView thumbnailView;
    private YouTubeThumbnailLoader mYouTubeThumbnailLoader = null;
    private int selectedPosition = 0;
    private boolean isAttached = false;

    @Override
    public int getResourceID() {
        return R.layout.fragment_exercise;
    }

    public ExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            selectedPosition = savedInstanceState.getInt(SELECTED_POSITION_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_exercise, container, false);
        circleImageView = (CircleImageView) rootView.findViewById(R.id.exercise_fragment_small_image_circle);
        thumbnailView = (YouTubeThumbnailView)rootView.findViewById(R.id.exercise_fragment_imageView_thumbnail);
        tvHello= (TextView) rootView.findViewById(R.id.exercise_fragment_tv_hello);
        spinner = (Spinner) rootView.findViewById(R.id.exercise_fragment_spinner_choose_an_exercise);
        return rootView;
    }

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

    @Override
    public void onStart() {
        super.onStart();
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(getActivity(), 0).show();
        } else {
            thumbnailView.initialize(getString(R.string.google_api_key), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    //mLoaders.put(view, loader);
                    //loader.setVideo((String) view.getTag());
                    mYouTubeThumbnailLoader = youTubeThumbnailLoader;
                    thumbnailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playMovie();
                        }
                    });
                    getThumbReady(selectedPosition);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            getThumbReady(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    final String errorMessage = youTubeInitializationResult.toString();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

        if(ConnectedUser.getInstance().getImage() != null ) {
            circleImageView.setImageBitmap(ConnectedUser.getInstance().getImage());
        } else {
            circleImageView.setVisibility(View.INVISIBLE);
        }
        tvHello.setText(UIHelper.getGreeting(getContext()) + " " + ConnectedUser.getInstance().getFirstName());

        final Typeface font = UIHelper.getDefaultFontRegular(getContext());
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.exercise_names_heb)){
            // Affects default (closed) state of the spinner
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTypeface(font);
                return view;
            }

            // Affects opened state of the spinner
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTypeface(font);
                return view;
            }
        };

        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(selectedPosition, true);
    }

    private void playMovie() {
        if(getActivity() != null && isAttached && getResources() != null) {
            final String[] exercise_movies = getResources().getStringArray(R.array.exercise_movie_adress);
            if (selectedPosition >= exercise_movies.length) {
                return;
            }
            if (mYouTubeThumbnailLoader != null) {

                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), getString(R.string.google_api_key), exercise_movies[selectedPosition], 0, true, false);
                startActivity(intent);
            }
        }
    }

    private void getThumbReady(int position) {
        if(getActivity() != null && isAttached && getResources() != null) {
            final String[] exercise_movies = getResources().getStringArray(R.array.exercise_movie_adress);
            if (position >= exercise_movies.length) {
                return;
            }
            selectedPosition = position;
            if (mYouTubeThumbnailLoader != null )
                try {
                    mYouTubeThumbnailLoader.setVideo(exercise_movies[position]);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
        }
    }



    private void releaseYoutubePlayer(){
        if(mYouTubeThumbnailLoader!= null){
            mYouTubeThumbnailLoader.release();
            mYouTubeThumbnailLoader = null;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseYoutubePlayer();
    }

    @Override
    public void storagePermissionGranted() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_POSITION_KEY, selectedPosition);
        super.onSaveInstanceState(outState);
    }
}
