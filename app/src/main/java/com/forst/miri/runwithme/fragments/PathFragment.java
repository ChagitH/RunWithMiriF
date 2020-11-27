package com.forst.miri.runwithme.fragments;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.crashlytics.android.Crashlytics;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.objects.MinimalLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PathFragment extends Fragment implements OnMapReadyCallback {

    private static View rootView;
    //private PracticeData pData = null;
    private ArrayList<MinimalLocation> locations = null;
//    private boolean practiceDataExsiting = false, mapIsReady = false;
    private GoogleMap googleMap = null;
    private int[] widthAndHeight = new int[2];


    public PathFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String languageToLoad = "iw";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if(true) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());


        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_path, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.path_fragment_map);

        mapFragment.getMapAsync(this);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(getView() != null) {
                    widthAndHeight[0] = getView().getWidth();
                    widthAndHeight[1] = getView().getHeight();
                }
            }
        });

        updateFragment();
        //return rootView;
        return rootView;
    }

    private boolean hasFragmentStarted = false;
    @Override
    public void onStart() {
        super.onStart();
        hasFragmentStarted = true;
    }

    public GoogleMap getMap(){
        return this.googleMap;
    }

    public void getMapPositionOnView(int[] locationOnView) {
        rootView.getLocationOnScreen(locationOnView);
        //return locationOnView;
    }

    public int[] getMapFragmentSize(){
        return this.widthAndHeight;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(this.getClass().getName(), "->->->->->-> onMapReady <-<-<-<-<-<-<-<- ");
        if(googleMap != null ) {
            this.googleMap = googleMap;

            updateFragment();
        }
    }

    public void setPracticeData(List<MinimalLocation> locations){//Context context){//PracticeData practiceData){

        Log.d(getClass().getName(), "in setPracticeData() *-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*");
        this.locations = new ArrayList<>();
        if (locations != null) this.locations.addAll(locations);

        if(this.locations != null) {
            updateFragment();
        }
//        if(practiceData != null) {
//            this.pData =  practiceData;
//            updateFragment();
//        }

    }

    private void updateFragment() {
        if(Looper.myLooper() != Looper.getMainLooper()){
            Log.d(this.getClass().getName(), "->->->->->-> Looper.myLooper() != Looper.getMainLooper() <-<-<-<-<-<-<-<- ");
            return;
        }
        if (googleMap != null && this.locations != null){// && pData.getLocations() != null){

            Log.d(getClass().getName(), "in updateFragment() *-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*");
            googleMap.clear();
            LatLng latLng = null;
            final PolylineOptions rectOptions = new PolylineOptions();
            final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for(MinimalLocation loc : this.locations) {
                latLng = new LatLng(loc.getLat(), loc.getLon());
                rectOptions.add(latLng);
                boundsBuilder.include(latLng);
            }

            Polyline polyline = googleMap.addPolyline(rectOptions);
            polyline.setJointType(JointType.ROUND);
            polyline.setColor(Color.RED);
//            polyline.setColor(getResources().getColor(R.color.colorBlue));
            polyline.setWidth(12);

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    try {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 60));
                        UiSettings uiSettings = googleMap.getUiSettings();
                        uiSettings.setAllGesturesEnabled(false);
                        uiSettings.setCompassEnabled(false);
                        uiSettings.setZoomControlsEnabled(false);
                        uiSettings.setMyLocationButtonEnabled(false);
                        uiSettings.setScrollGesturesEnabled(false);
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    } catch ( Exception ex){
                        ex.printStackTrace();
                        Crashlytics.log(ex.toString());
                    }
                        // Remove listener to prevent position reset on camera move.
                        googleMap.setOnMapLoadedCallback(null);
                }
            });
        }
    }
}
