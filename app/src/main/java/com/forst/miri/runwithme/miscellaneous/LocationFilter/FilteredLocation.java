package com.forst.miri.runwithme.miscellaneous.LocationFilter;

import android.location.Location;

import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.Logger;

/**
 * Created by chagithazani on 5/31/18.
 */

public class FilteredLocation {

    //private final float MinAccuracy = 1;

    private float Q_metres_per_second;
    private long TimeStamp_milliseconds;
    private double lat = 0;
    private double lng = 0;
    private float variance; // P matrix.  Negative means object uninitialised.  NB: units irrelevant, as long as same units used throughout
    private int minimumAccuracyAccepted = 100;

//    private Location initialLocation = null;
//    private double distance = 0;

    public FilteredLocation(float Q_metres_per_second, int minAccuracy) {
        this.Q_metres_per_second = Q_metres_per_second;
        variance = -1;
        minimumAccuracyAccepted = minAccuracy;
    }

//    public long get_TimeStamp() { return TimeStamp_milliseconds; }
//    public double get_lat() { return lat; }
//    public double get_lng() { return lng; }
//    public float get_accuracy() { return (float)Math.sqrt(variance); }

    public Location setState(Location location) {
        if(location == null) return null;
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        float accuracy = location.getAccuracy();
        variance = accuracy * accuracy;
        this.TimeStamp_milliseconds = location.getTime();

        return location;
    }


    public Location process(Location location){//}, int actualSatellitesUsed, int minSatellitesAccepted) {
//
//        Crashlytics.log(Log.INFO,"LocationService","process() " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())+ " location is null? " + (location == null));
//        Crashlytics.logException(new Throwable("LocationService process() " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis())));
//        Logger.log(context, "LocationService process() " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()) + " location is null? " + (location == null));

        if(location == null) return null;
        Logger.log("LocationService process() " + UIHelper.formatTimeAndDateWithMillies(System.currentTimeMillis()) + " Accuracy = " + location.getAccuracy());

        if(location.getAccuracy() > minimumAccuracyAccepted){
            Logger.log(" FilteredLocation.process()  Accuracy Higher than maximum ! " );
            return null;
        }

        //if first time
        if(lat == 0 && lng == 0) {
            Logger.log(" FilteredLocation.process()  lat == 0 && lng == 0 ");
            return setState(location);
        }

        float accuracy = location.getAccuracy();

        if (variance < 0) {
            // if variance < 0, object is uninitialised, so initialise with current values
            this.TimeStamp_milliseconds = location.getTime();
            lat = location.getLatitude();
            lng = location.getLongitude();
            variance = accuracy * accuracy;
            return location;
        } else {
            // else apply Kalman filter methodology
            long TimeInc_milliseconds = location.getTime() - this.TimeStamp_milliseconds;
            if (TimeInc_milliseconds > 0) {
                // time has moved on, so the uncertainty in the current position increases
                variance += TimeInc_milliseconds * Q_metres_per_second * Q_metres_per_second / 1000;
                this.TimeStamp_milliseconds = location.getTime();

            }

            // Kalman gain matrix K = Covarariance * Inverse(Covariance + MeasurementVariance)
            // because K is dimensionless, it doesn't matter that variance has different units to lat and lng
            float K = variance / (variance + accuracy * accuracy);
            // apply K
            lat += K * (location.getLatitude() - lat);
            lng += K * (location.getLongitude() - lng);

            variance = (1 - K) * variance;

            Location location1 = new Location(location);
            location1.setLongitude(lng);
            location1.setLatitude(lat);
            location1.setProvider(FilteredLocationManager.FILTERED_PROVIDER);

            return location1;

        }
    }

}
