package com.forst.miri.runwithme.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.forst.miri.runwithme.miscellaneous.UIHelper;

/**
 * Created by chagithazani on 8/9/17.
 */

public class Rate implements Parcelable{
    public static final String TIMESTAMP_KEY = "timestamp_rate";
    public static final String METER_TOTAL_KEY = "meter_total_rate";
    public static final String RATE_MILLIS_KEY = "rate_in_millis_rate";
    public static final String KM_KEY = "km_of_rate";
//    private double rate; //rate per segment

    private long rateInMillies; //rate per segment
    private float meter; // meter into the track
//    private long time; //time it took to do the segment
//    private float length; //the length of the segment
    private long timeStamp; //timestamp


    public Rate (float meter, long timeStamp,  long timeInMillis, float lengthInMeters){
        //this(meter, (timeInMillis > 0 && lengthInMeters > 0 ) ?  (float)((PracticeData.millisToMinutes(timeInMillis) / (float)(lengthInMeters/1000) )) : 0); //rate = minutes/km
        if(timeInMillis > 0 && lengthInMeters > 0) {

//            double ratio = (1000/(double)lengthInMeters);
//            this.rateInMillies = (long)(ratio * timeInMillis);

            double lengthInKm = ((double)lengthInMeters/1000);
            this.rateInMillies = (long)(timeInMillis / lengthInKm);


            //int rate = PracticeData.millisToMinutes(rateInMillies);
//            this.meter = meter;

//            double minutes = PracticeData.millisToMinutes(timeInMillis);
//            double lengthInKm = (lengthInMeters / 1000);
//            double rate = minutes  / lengthInKm;
//            if (Double.isNaN(rate)){
//                rate = 0;
//            }
//            this.rate = rate ;
        } else {
            this.rateInMillies = 0;
        }
        this.meter = meter;
//        Log.i(getClass().getSimpleName()," meter into the track = " + meter + " timeInMillis = " + timeInMillis + " lengthInMeters = " + lengthInMeters);
//        Log.i(getClass().getSimpleName()," time in minutes = " + PracticeData.millisToMinutes(timeInMillis) + " lengthInMeters = " + lengthInMeters + " time in minutes / lengthInMeters= " + PracticeData.millisToMinutes(timeInMillis)/lengthInMeters);
//        this.time = timeInMillis;
//        this.length = lengthInMeters;
        this.timeStamp = timeStamp;
        Log.i(getClass().getSimpleName()," Meter in MASLUL = " + this.getMeter() + " rate = " + UIHelper.formatTime(getRateInMillies()));
    }

    public Rate(Long timestamp, float meter, long rateInMillis) {
        this.rateInMillies = rateInMillis;
        this.meter = meter;
        this.timeStamp = timestamp;
    }

//    public Rate (int meter , long rateMillies){
//        this.meter = meter;
//        this.rateInMillies = rateMillies;
//    }

    public long getRateInMillies(){
        return this.rateInMillies;
    }

    public long getRateInSeconds(){
        return this.rateInMillies/1000;
    }

//    public String getRateForPresentation(){
//
//        return this.rateInMillies;
//    }

    public float getMeter(){
        return this.meter;
    }
    public long getTimestamp(){
        return this.timeStamp;
    }

    public Rate(Parcel in){
        rateInMillies = in.readLong();
        meter = in.readFloat();
//        time = in.readLong();
//        length = in.readFloat();
        timeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(rateInMillies);
        dest.writeFloat(meter);
//        dest.writeLong(time);
//        dest.writeFloat(length);
        dest.writeLong(timeStamp);
    }

    public static final Creator<Rate> CREATOR = new Creator<Rate>() {
        @Override
        public Rate createFromParcel(Parcel in) {
            return new Rate(in);
        }

        @Override
        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };
}
