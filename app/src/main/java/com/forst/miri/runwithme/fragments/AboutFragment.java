package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = AboutFragment.class.getSimpleName();
//    public static final int YOUTUBE_MOVIE_INTENT = 9456;
////    private int playingMovie = 1;
//    private YouTubeThumbnailView mThumbnailView;
//    private YouTubeThumbnailLoader mYouTubeThumbnailLoader;
//    private String youtubeMovieAdress = null;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
//        youtubeMovieAdress = getString(R.string.pre_run_exercise_video_address);
//        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());
//        if (result != YouTubeInitializationResult.SUCCESS) {
//            //If there are any issues we can show an error dialog.
//            result.getErrorDialog(getActivity(), 0).show();
//        } else {
//            mThumbnailView.initialize(getString(R.string.google_api_key), new YouTubeThumbnailView.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
//                    //mLoaders.put(view, loader);
//                    //loader.setVideo((String) view.getTag());
//                    mYouTubeThumbnailLoader = youTubeThumbnailLoader;
//                    mThumbnailView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            playMovie(youtubeMovieAdress);
//                        }
//                    });
//                    getThumbReady(youtubeMovieAdress);
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//                    final String errorMessage = youTubeInitializationResult.toString();
//                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
//                }
//            });
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView versionNameTv = (TextView) parentView.findViewById(R.id.about_version_name_textview);
        //mThumbnailView = (YouTubeThumbnailView)parentView.findViewById(R.id.about_fragment_imageView_thumbnail);

        //int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versionNameTv.setText(versionName);


        return parentView;
    }

//    private void getThumbReady(String postRunMovieAddress) {
//        if(mYouTubeThumbnailLoader != null) mYouTubeThumbnailLoader.setVideo(postRunMovieAddress);
//    }
//
//
//    private void playMovie(final String address) {
//        if(mYouTubeThumbnailLoader != null) {
//            Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), getString(R.string.google_api_key), address, 0, true, true);
////            startActivity(intent);
//            startActivityForResult(intent, YOUTUBE_MOVIE_INTENT);
//        }
//
//
//
//    }

    @Override
    public void storagePermissionGranted() {

    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_about;
    }
}
