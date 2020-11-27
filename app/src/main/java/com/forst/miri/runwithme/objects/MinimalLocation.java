package com.forst.miri.runwithme.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chagithazani on 1/7/18.
 */

public class MinimalLocation implements Parcelable {

    public static final String TIMESTAMP_KEY = "time_stamp_ML";
    public static final String LONGITUDE_KEY = "longitude_ML";
    public static final String LATITUDE_KEY = "latitude_ML";
    //public static final String MINIMAL_LOCATION_KEY = "MinimalLocation";
    public static double NO_ELEVATION = -500.0;

    private double mLat;
    private double mLon;
    private double mElevation = NO_ELEVATION;
    private long mTimeStamp;

    public MinimalLocation(){}


    public MinimalLocation(long timeStamp, double lat, double lon){
        mTimeStamp = timeStamp;
        mLat = lat;
        mLon = lon;
        //Log.d(getClass().getName().toString(), " Ctor : timeStamp " + timeStamp + "  lat " + lat + " lon " + lon );
    }

    public MinimalLocation(MinimalLocation loc) {
        this.mTimeStamp = loc.getTimeStamp();
        this.mLat = loc.getLat();
        this.mLon = loc.getLon();
        this.mElevation = loc.getElevation();
    }


    public double getLat() {
        return mLat;
    }

    public void setLat(double mLat) {
        this.mLat = mLat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double mLon) {
        this.mLon = mLon;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public void setElevation(double elevation) {
        this.mElevation = elevation;
    }

    public double getElevation(){
        return mElevation;
    }

    protected MinimalLocation(Parcel in) {
        if(in != null){
            this.mTimeStamp = in.readLong();
            this.mLat = in.readDouble();
            this.mLon = in.readDouble();
            this.mElevation = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(dest != null){
            dest.writeLong(mTimeStamp);
            dest.writeDouble(mLat);
            dest.writeDouble(mLon);
            dest.writeDouble(mElevation);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MinimalLocation> CREATOR = new Creator<MinimalLocation>() {
        @Override
        public MinimalLocation createFromParcel(Parcel in) {
            return new MinimalLocation(in);
        }

        @Override
        public MinimalLocation[] newArray(int size) {
            return new MinimalLocation[size];
        }
    };

}
