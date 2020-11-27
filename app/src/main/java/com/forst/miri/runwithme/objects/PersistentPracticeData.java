package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.forst.miri.runwithme.miscellaneous.SQLiteDatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chagithazani on 11/29/17.
 */
/*
class that will hold PracticeData information in perssitant way,
so if process cut in the middle, data will be saved, up to the point it was saved
 */
public class PersistentPracticeData {

    //private ArrayList<LocationAndMore> mLocations;
//    private HashMap<Integer, ArrayList<Rate>> mRatesMap;
//    private ArrayList<Rate> mRates;

//    private int mPracticeNum = 0;
//    private float mDistance = 0; //total distance in meters
//    private long mDuration = 0; //total duration in millis from beginning to end
    //private long mDurationTemp = 0; //duration in millis from start -> pause

    private SQLiteDatabaseHandler mSQLdbHandler;
    private SharedPreferences mPrefs;

    private static final String DISTACE_KEY = "dis_key_ppd";
    private static final String PRACTICE_NUM_KEY = "practice_num_key_ppd";
    private static final String PRACTICE_PLAN_KEY = "practice_plan_num_key_ppd";
    private static final String DURATION_KEY = "dur_key_ppd";
    private static final String TEMPERATURE_KEY = "temp_key_ppd";
    private static final String PRACTICE_ENDED_YES_OR_NO_KEY = "practice_ended_yes_or_no_key_ppd";
    private static final String PPD_SAVED_TO_PD_KEY = "ppd_saved_to_pd_and_presented_key";

    static public PersistentPracticeData createPersistentPracticeData(Context context){
        PersistentPracticeData ppd = new PersistentPracticeData(context);
        if(ppd.mPrefs == null || ! ppd.mPrefs.contains(DISTACE_KEY)) {
            return null;
        } else {
            return ppd;
        }
    }

    static public PersistentPracticeData createPersistentPracticeDataIfNotSavedYet(Context context){
        boolean wasPpdSaved = true;
        SharedPreferences sp = context.getSharedPreferences(PersistentPracticeData.class.getName(), Context.MODE_PRIVATE);
        if (sp != null ) {
            wasPpdSaved =  sp.getBoolean(PPD_SAVED_TO_PD_KEY, true);
        }
        if(wasPpdSaved == true){
            return null;
        }
        PersistentPracticeData ppd = new PersistentPracticeData(context);
        if(ppd.mPrefs == null || ! ppd.mPrefs.contains(DISTACE_KEY)) {
            return null;
        } else {
            return ppd;
        }
    }

    /* choose this ctor if need to get the data saved in PersistentPracticeData */
    private PersistentPracticeData(Context context){
        mSQLdbHandler = new SQLiteDatabaseHandler(context);
        mPrefs = context.getSharedPreferences(PersistentPracticeData.class.getName(), Context.MODE_PRIVATE);
    }

    /*
    choose this ctor if starting new training and need to start and save data
     */
    public PersistentPracticeData (int practiceNum, int practicePlan, float temp,  Context context){
        this(context);
        eraseData(); //clear and start from new
        setPracticeNum(practiceNum);
        setTemperature(temp);
        setPracticePlan(practicePlan);
        setPpdSavedToPd(false);
    }

    public void addLocation(Location lam){
        try {
            mSQLdbHandler.addLocation(lam);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addRate(Rate newRate){
        //save all rates
        //mRates.add(newRate);
        mSQLdbHandler.addRate(newRate);
//        //save to rate per km map
//        int currentKm = ((int)(mDistance/1000))+1;
//        Integer currentKMInteger = Integer.valueOf(currentKm);
//        if( ! mRatesMap.containsKey(currentKMInteger)){
//            mRatesMap.put(currentKMInteger, new ArrayList<Rate>());
//        }
//        mRatesMap.get(currentKMInteger).add(newRate);
    }

    public void setPpdSavedToPd(boolean saved){
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(PPD_SAVED_TO_PD_KEY, saved);
            editor.commit();

        }
    }

    public void addToDistance(float newSegmentDistance){
        if(mPrefs != null) {
            float totalDistance = mPrefs.getFloat(DISTACE_KEY, 0);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putFloat(DISTACE_KEY, (totalDistance + newSegmentDistance));
            editor.commit();

        }
    }

    public void addToDuration(long duration){
        if(mPrefs != null) {
            long totalDuration = mPrefs.getLong(DURATION_KEY, 0);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putLong(DURATION_KEY, totalDuration + duration);
            editor.commit();
        }
    }

    public void setDuration(long duration) {
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putLong(DURATION_KEY,duration);
            editor.commit();
        }
    }

    public int getPracticeNum(){
        if (mPrefs != null && mPrefs.contains(PRACTICE_NUM_KEY)) { //do i need to check for exsitance or not? worse comes to worse will return 0
            return mPrefs.getInt(PRACTICE_NUM_KEY, 0);
        } else {
            return 0;
        }
    }

    public float getDistance(){
        if (mPrefs != null ) {
            return mPrefs.getFloat(DISTACE_KEY, 0);
        } else {
            return 0;
        }
    }

    public long getDuration(){
        if (mPrefs != null ) {
            return mPrefs.getLong(DURATION_KEY, 0);
        } else {
            return 0;
        }
    }

    public ArrayList<MinimalLocation> getLocations(){
        ArrayList<MinimalLocation> locations = null;
        try {
            locations = mSQLdbHandler.getAllLocations();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return locations;
    }

    public ArrayList<Rate> getRates(){
        ArrayList<Rate> rates = null;
        try {
            rates = mSQLdbHandler.getAllRates();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return rates;
    }



    public HashMap<Integer, ArrayList<Rate>> getRatesByKM(){
        HashMap<Integer, ArrayList<Rate>> ratesMap = new HashMap<>() ;
        ArrayList<Rate> rates = getRates();
        if(rates != null){
            for(Rate rate : rates){
                int currentKm = ((int)(rate.getMeter()/1000))+1;
                Integer currentKMInteger = Integer.valueOf(currentKm);
                if( ! ratesMap.containsKey(currentKMInteger)){
                    ratesMap.put(currentKMInteger, new ArrayList<Rate>());
                }
                ratesMap.get(currentKMInteger).add(rate);
            }
        }
        return ratesMap;
    }

    public void eraseData() {
        mSQLdbHandler.clearAllLocations();
        mSQLdbHandler.clearAllRates();
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.clear();
            editor.commit();
        }
    }

    public static void erase(Context context) {
        SQLiteDatabaseHandler sqldbHandler = new SQLiteDatabaseHandler(context);
        SharedPreferences prefs = context.getSharedPreferences(PersistentPracticeData.class.getName(), Context.MODE_PRIVATE);
        sqldbHandler.clearAllLocations();
        sqldbHandler.clearAllRates();
        if(prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
        }
    }

    public void setPracticeEnded(boolean practiceEnded) {
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(PRACTICE_ENDED_YES_OR_NO_KEY, practiceEnded);
            editor.apply();
        }
    }

    public boolean getPracticeEnded() {
        if (mPrefs != null ) {
            return mPrefs.getBoolean(PRACTICE_ENDED_YES_OR_NO_KEY, false);
        } else {
            return false;
        }
    }

    public void setPracticeNum(int practiceNum) {
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(PRACTICE_NUM_KEY, practiceNum);
            editor.apply();
        }
    }

    public double getTemperature() {
        if (mPrefs != null ) {
            return (double)mPrefs.getFloat(TEMPERATURE_KEY, -100);
        } else {
            return -100;
        }
    }

    public void setTemperature(float temp) {
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putFloat(TEMPERATURE_KEY, temp);
            editor.apply();
        }
    }

    public void setPracticePlan(int practicePlanNum) {
        if(mPrefs != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(PRACTICE_PLAN_KEY, practicePlanNum);
            editor.apply();
        }
    }

    public int getPracticePlan() {
        if (mPrefs != null && mPrefs.contains(PRACTICE_PLAN_KEY)) {
            return mPrefs.getInt(PRACTICE_PLAN_KEY, 0);
        } else {
            return 0;
        }
    }


}
