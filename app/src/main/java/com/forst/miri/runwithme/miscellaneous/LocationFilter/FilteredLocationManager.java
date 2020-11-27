package com.forst.miri.runwithme.miscellaneous.LocationFilter;

/**
 * Created by chagithazani on 5/31/18.
 */


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.fragments.PostRunFragment;
import com.forst.miri.runwithme.objects.Logger;

public class FilteredLocationManager implements LocationListener,android.location.GpsStatus.Listener {
    public static final String FILTERED_PROVIDER = "my_filtered_location";

    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;
    private Context mContext = null;

    public interface internalLocationMessageCallback {
        void sendMessage(String message);

        void sendCountDown(String count);
    }

    private internalLocationMessageCallback callback = null;


    public static final int MIN_LOCATIONS_FOR_STARTING = 9;
    private static final float Q = 6;

    public static final float MIN_ACCEPTED_ACCURACY = 80;//21.8.18 changed from 30 //28.8.18 changed from 60
    /**
     * If we get fixed satellites >= mFixSatellites mFixed => true
     */
    final int mFixSatellites = 2;
    int mKnownSatellites = 0;
    int mUsedInLastFixSatellites = 0;


    private int locationsCounter = 0;


    private FilteredLocation mFilteredLatLong = null;

    private Boolean mIsPaused = false;


    public FilteredLocationManager(@Nullable Context context, internalLocationMessageCallback callback) {

        mContext = context;

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        this.callback = callback;
    }

//    public void requestLocationUpdates(LocationListener locationListener) {
//        mLocationListener = locationListener;
//
//        if (mIsPaused) {
//            synchronized (mIsPaused) {
//                mIsPaused = false;
//            }
//        } else {
//
//            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                if (mFilteredLatLong == null) {
//                    mFilteredLatLong = new FilteredLocation(Q);
//                }
//                if (mLocationManager == null) {
//                    mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
//                }
//
//                if (mLocationManager != null) {
//                    mLocationManager.addGpsStatusListener(this);
////                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
//
//                    // until gps warms up, ask for any change every 1000 millis. later, change to 2000, 3
//                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
//                } else {
//                    if (callback != null) callback.sendMessage("Location Manager is null flm90");
//                }
//            }
//        }
//
//    }


    @Override
    public void onLocationChanged(Location location) {


        //Log.d(getClass().getName(), " ########### ##### #### # onLocationChanged()1 mIsPaused=" + mIsPaused);

        if (location == null) return;

        Logger.log(" Location Received flm 108" + '\n');

//            StringBuilder message = new StringBuilder();
//
//            message.append(UIHelper.formatTime(System.currentTimeMillis()));
//            message.append(' ');
//            message.append("location time = " + location.getTime());
//            message.append(' ');
//            message.append("accuracy = " + location.getAccuracy());
//            message.append(' ');
//            message.append("speed = " + location.getSpeed());
//            message.append(' ');
//            message.append("lat = " + location.getLatitude());
//            message.append(' ');
//            message.append("lon = " + location.getLongitude());
//            if(Build.VERSION.SDK_INT >= 26) {
//                message.append(' ');
//                message.append("speed accu = " + location.getSpeedAccuracyMetersPerSecond());
//            }
//            Logger.log(mContext, message.toString());


        if (locationsCounter < MIN_LOCATIONS_FOR_STARTING) {
            Log.d(getClass().getName(), " ########### ##### #### # onLocationChanged()2 locationsCounter=" + locationsCounter);
            if (locationsCounter >= MIN_LOCATIONS_FOR_STARTING - 1) {
                if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this);

                } else {
                    if (callback != null)
                        callback.sendMessage(mContext.getString(R.string.no_location_permit_heb) + " flm115");
                }
//                if(mFilteredLatLong != null ) mFilteredLatLong.setState(location);
            }
            if (callback != null)
                callback.sendCountDown(String.valueOf(MIN_LOCATIONS_FOR_STARTING - locationsCounter));
            locationsCounter++;
            return;
        }

        if (mFilteredLatLong == null) {
            if (callback != null) callback.sendMessage("mFilteredLatLong == null flm125");
            return;
        }

        if (!mIsPaused) {

            Log.d(getClass().getName(), " ########### ##### #### # onLocationChanged()3 mIsPaused=" + mIsPaused);
            Location filteredLocation = mFilteredLatLong.process(location);

            if (mLocationListener != null && filteredLocation != null) {
                mLocationListener.onLocationChanged(filteredLocation);
            } else {
                if (mLocationListener == null) {
                    if (callback != null) callback.sendMessage("mLocationListener == null flm138");

                } else {
                    Log.d(PostRunFragment.class.getName(), " =================== logUri == null ===============================");
                }
            }

            if (filteredLocation == null) {
                Logger.log(" filteredLocation == null flm179" + '\n');
                if (callback != null) callback.sendMessage("filteredLocation == null flm179");
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        if (provider.equalsIgnoreCase("gps")) {
            if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                clear(true);
            }
            if(mLocationListener != null) mLocationListener.onStatusChanged(provider,status,bundle);

        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equalsIgnoreCase("gps")) {
            clear(false);
            if(mLocationListener != null) mLocationListener.onProviderEnabled(provider);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equalsIgnoreCase("gps")) {
            clear(true);
            if(mLocationListener != null) mLocationListener.onProviderDisabled(provider);

        }
    }

    @Override
    public void onGpsStatusChanged(int i) {
        if (mLocationManager == null)
            return;
        if (ContextCompat.checkSelfPermission(this.mContext,  android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            android.location.GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);

            if (gpsStatus == null)
                return;

            int cnt0 = 0, cnt1 = 0;
            Iterable<GpsSatellite> list = gpsStatus.getSatellites();
            for (GpsSatellite satellite : list) {
                cnt0++;
                if (satellite.usedInFix()) {
                    cnt1++;
                }
            }
            mKnownSatellites = cnt0;
            mUsedInLastFixSatellites = cnt1;

        }
    }

    private void clear(boolean resetIsFixed) {

        mKnownSatellites = 0;
        mUsedInLastFixSatellites = 0;
    }

    public void pauseLocationUpdates() {
        Log.d(getClass().getName(), " ########### ##### #### # pauseLocationUpdates()1 mIsPaused=" + mIsPaused);
        synchronized (mIsPaused) {
            mIsPaused = true;
        }
        Log.d(getClass().getName(), " ########### ##### #### # pauseLocationUpdates()2 mIsPaused=" + mIsPaused);


    }

    public void stop( ) {

        if (mLocationManager != null) {
            try {
                mLocationManager.removeGpsStatusListener(this);
                mLocationManager.removeUpdates(this);
            } catch (SecurityException ex) {
                //Ignore if user turned off GPS
            }
        }
        mLocationManager = null;
        mLocationListener = null;
        mFilteredLatLong = null;
        locationsCounter = 0;
        callback = null;
    }

}
