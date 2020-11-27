package com.forst.miri.runwithme.objects;

import android.location.Location;
import android.os.Parcel;

/**
 * Created by chagithazani on 8/8/17.
 */

public class LocationAndMore{//} implements Parcelable{
//    public static final String TIMESTAMP_KEY = "location_created_time_stamp";
//    public static final String LOCATION_AND_MORE_KEY = "location_and_more";
//    public static final String LONGITUDE_KEY = "longitude_";
//    public static final String LATITUDE_KEY = "latitude_";
    //    private double lat;
//    private double lng;
    Location location;
    public static final String LOCATION_AND_MORE = "locationandmooore";
    private double elevation;
    //private double resolution;
    private long timeStamp;

    public LocationAndMore(){}

    public LocationAndMore(Location location/*, double lat, double lng*/, double elevation/*, double resolution*/, long timeStamp) {
        //super(location);
//        this.lat = lat;
//        this.lng = lng;
        this.location = location;
        this.elevation = elevation;
        //this.resolution = resolution;
        this.timeStamp = timeStamp;
    }

    protected LocationAndMore(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        elevation = in.readDouble();
        //resolution = in.readDouble();
        timeStamp = in.readLong();
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public void setLatitude(double lat) {
        this.location.setLatitude(lat);
    }

    public double getLongitude() {
        return this.location.getLongitude();
    }

    public void setLongitude(double lng) {
        this.location.setLongitude(lng);
    }



//    public static final Creator<LocationAndMore> CREATOR = new Creator<LocationAndMore>() {
//        @Override
//        public LocationAndMore createFromParcel(Parcel in) {
//            return new LocationAndMore(in);
//        }
//
//        @Override
//        public LocationAndMore[] newArray(int size) {
//            return new LocationAndMore[size];
//        }
//    };

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

//    public double getResolution() {
//        return resolution;
//    }
//
//    public void setResolution(double resolution) {
//        this.resolution = resolution;
//    }

    public long getTimeStamp() {
        //return timeStamp;
        return this.location.getTime();
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(location, flags);
//        dest.writeDouble(elevation);
//        //dest.writeDouble(resolution);
//        dest.writeLong(timeStamp);
//    }

    public float distanceTo(LocationAndMore loc){
        return this.location.distanceTo(loc.location);
    }


}
