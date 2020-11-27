package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.interfaces.GetElevationResponseCallback;
import com.forst.miri.runwithme.interfaces.PracticeDataCreatedCallback;
import com.forst.miri.runwithme.miscellaneous.NetworkCenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by chagithazani on 7/31/17.
 */

public class PracticeData { //} implements Parcelable {
    public static final String LOCATIONS_READY_FOR_PRESENTATION_KEY = "locations_ready_for_presentation_key";

    public static String PRACTICE_DATA_SAVE_KEY = PracticeData.class.getName();
//    public static String DOES_HAVE_ELEVATION_DATA = "elevation_data_bool";
//    public static String IS_SAVED_TO_BACKEND = "saved_to_backend_bool";
//    //public static String IS_PART_OF_PLAN = "is_part_of_plan" ;
//    //public static String DATE_KEY = "date";
    public static String PRACTICE_DATA_KEY = "practiceData";
    public static String PRACTICE_PROGRAM_KEY = User.PRACTICE_PROGRAM_ID_KEY;
    public static String PRACTICE_NUM_KEY = "lesson";
    public static String DISTANCE_KEY = "distance";
    public static String DURATION_KEY = "duration";
    public static String SERVER_ID_KEY = "id";
    public static String CALORIES_KEY = "calories";
    public static String AVE_RATE_KEY = "rate";
    public static String MAX_RATE_KEY = "max_rate";
    public static String ELEVATION_KEY = "elevation_gain";
    public static String SPEED_KEY = "mph";
    public static String ALL_LOCATIONS_KEY = "locations";
    public static String ALL_RATES_KEY = "all_rates";
    public static String RATES_PER_KM_KEY = "rates_per_km";
    public static String TIMESTAMP_KEY = "timestamp";
    public static String TEMPERATURE_KEY = "temp";
    public static String WHEN_PRESENTED_KEY = "whenPresented";

    public static int DATA_PRESENTED_AFTER_RUN = 13622;
    public static int DATA_PRESENTED_FROM_LIST = 89547;
    public static int MAX_LOCATIONS_SIZE = 512;
    public static String SERVER_ID_DEFAULT = "server_id";


    private String serverId = SERVER_ID_DEFAULT;
    private long mTimeStamp;
    private ArrayList<MinimalLocation> mLocations;
    private ArrayList<Long> mRatesPerKM;
    private ArrayList<Rate> mAllRates;
    private long mAvgRateInMillies = 0;
    private long mBestRateInMilliesForKm = 0;
    private double mSpeed;
    private int mDistanceInMeters;
    private long mDuration;//total time the practice took in millis;
    private int mKcl;//total kkl burned calculated by the weight at given time.
    private double mPositiveSlope = MinimalLocation.NO_ELEVATION;//total positive slopes in practice (without taking negative slopes in account)
    private double mTemperature = -100;
    //private int hasElevationInfo = 0;
    private int numOfLessonInPlan = 0 , numOfPlan = 0; //partOfPlan = 0;
    //private int savedToBackend = 0;


    public PracticeData(){
        initiatePracticeData();
    }

    public PracticeData(PersistentPracticeData persistentPracticeData) {
        this();

        if(persistentPracticeData == null ) return;

        float distance = persistentPracticeData.getDistance();
        long duration = persistentPracticeData.getDuration();
        ArrayList<MinimalLocation> locations = persistentPracticeData.getLocations();

        if (distance > 0 && duration > 1000 && locations != null && locations.size() > 0) {

            if(persistentPracticeData.getPracticeEnded()) {
                this.setNumOfLessonInPlan(persistentPracticeData.getPracticeNum());
                this.setNumOfPlan(persistentPracticeData.getPracticePlan());
            }
            this.setLocations(locations);
            this.setAllRates(persistentPracticeData.getRates());
            this.setRatesPerKm(persistentPracticeData.getRatesByKM());
            this.setDuration(duration);
            this.setDistanceInMeters( (int) distance);
            this.setTemperature(persistentPracticeData.getTemperature());
            Rate avgRate = new Rate(getDistanceInMeters(), 0, getDuration(), getDistanceInMeters());
            setAvgRate(avgRate.getRateInMillies());

            calculateExtraData();

        }
    }

    public void setTemperature(double temp) {
        this.mTemperature = temp;
    }

    private void setRatesPerKm(HashMap< Integer, ArrayList<Rate> > ratesByKM) {
        if(ratesByKM == null) return;
        Set<Integer> keys = ratesByKM.keySet();
        //long bestKMRate = 0;
        for(Integer key : keys){
            ArrayList<Rate> kmRatesArray = ratesByKM.get(key);
//            //long counter = 0;
//            long rateLong = 0;
//            int
//            for(Rate rate : kmRatesArray){
//                //counter++;
//                rate.
//                rateLong += rate.getRateInMillies();
//            }
//            long avgRateForKm = 0;
////            if(counter > 0){
////                avgRateForKm = rateLong/counter;
////            }
            long avgRateForKm = 0;
            if(kmRatesArray != null ){
               if( kmRatesArray.size() > 1) {
                   Rate firstRate = kmRatesArray.get(0);
                   Rate lastRate = kmRatesArray.get(kmRatesArray.size()-1);

                   long timeInMillies = lastRate.getTimestamp() - firstRate.getTimestamp();

                   float distanceInMeters = lastRate.getMeter() - firstRate.getMeter();
                   //double avgRate = millisToMinutes(timeInMillies)/(double)distanceInMeters;
                   double avgRateInMillies = timeInMillies/(double)distanceInMeters;


                   double ratio = (double)((double)1000/(double)distanceInMeters);
//                   this.rateInMillies = (long)(ratio * timeInMillis);


                   //avgRateForKm = (long)avgRateInMillies;
                   avgRateForKm = (long)(ratio * timeInMillies);
               } else if(kmRatesArray.size() > 0){
                   avgRateForKm = kmRatesArray.get(0).getRateInMillies();
               }

//               else {
//                   this.mRatesPerKM.add(0L);
//               }
            }
            this.mRatesPerKM.add(avgRateForKm);
            if(mBestRateInMilliesForKm == 0){
                mBestRateInMilliesForKm = avgRateForKm;
            } else {
                if(avgRateForKm < mBestRateInMilliesForKm){
                    mBestRateInMilliesForKm = avgRateForKm;
                }
            }
        }
    }

    private void calculateExtraData() {

        this.setDuration(mDuration);

//        double ratio = getRatio(mDistanceInMeters);
//        long avgRateInMillies = (long) (mDuration * ratio);
//        this.setAvgRate(avgRateInMillies);

        double speed = 0;
        double hours = ((double)millisToHours(mDuration));
        double distanceInKm = ((double)mDistanceInMeters) / ((double)1000);
        try {
            speed = ((double)distanceInKm/hours); // km per hour
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        this.setSpeed(speed);

        User user = ConnectedUser.getInstance();
        if(user != null) {
            int weight = user.getWeight();
            double kcl = millisToMinutes(mDuration) * 0.16 * (double)weight; //time in minutes * 0.16 * weight
            this.setKcl((int)kcl);
        }
    }

//    public boolean hasElevationInfo(){
//        return hasElevationInfo == 1;
//    }
//
//     public void setHasElevationInfo(boolean has){
//        this.hasElevationInfo = has ? 1 : 0 ;
//     }
//
//    public boolean isSavedToBackend(){
//        return savedToBackend == 1;
//    }
//
//    public void setSavedToBackend(boolean has){
//        this.savedToBackend = has ? 1 : 0 ;
//    }

    public boolean isPartOfPlan(){
        return numOfLessonInPlan  > 0;
    }

    public void setNumOfLessonInPlan(int num){
        this.numOfLessonInPlan = num ;
    }

    public int getNumPlan(){
        return this.numOfPlan;
    }

    public void setNumOfPlan(int num){
        this.numOfPlan = num ;
    }

    public int getNumOfLessonInPlan(){
        return this.numOfLessonInPlan;
    }

    public static PracticeData fromJson(Context context, JSONObject json){
        PracticeData pData = new PracticeData();
        Gson gson = new Gson();
        try {
            pData.setServerId(json.getString(PracticeData.SERVER_ID_KEY));
            pData.setAvgRate(json.getLong(PracticeData.AVE_RATE_KEY));
            pData.setDistanceInMeters(json.getInt(PracticeData.DISTANCE_KEY));
            pData.setDuration(json.getLong(PracticeData.DURATION_KEY));
            pData.setKcl(json.getInt(PracticeData.CALORIES_KEY));
            pData.setBestRateForKmMiliies(json.getLong(PracticeData.MAX_RATE_KEY));
            pData.setSpeed(json.getDouble(PracticeData.SPEED_KEY));

            pData.setTemperature(json.getDouble(PracticeData.TEMPERATURE_KEY));


            pData.setLocations(json.getString(PracticeData.ALL_LOCATIONS_KEY));

            pData.setPositiveSlope(json.getDouble(PracticeData.ELEVATION_KEY));

            Type listTypeRates = new TypeToken<List<Rate>>(){}.getType();
            pData.mAllRates = gson.fromJson(json.getString(PracticeData.ALL_RATES_KEY), listTypeRates);

            if(json.has(PracticeData.RATES_PER_KM_KEY)) {
                Type listTypeRatesPerKM = new TypeToken<List<Long>>(){}.getType();
                pData.mRatesPerKM = gson.fromJson(json.getString(PracticeData.RATES_PER_KM_KEY), listTypeRatesPerKM);
            } else{
                if(pData.mRatesPerKM == null){
                    pData.mRatesPerKM = new ArrayList<>();
                }
            }
            if(json.has(PracticeData.PRACTICE_NUM_KEY) && !json.isNull(PracticeData.PRACTICE_NUM_KEY)) pData.setNumOfLessonInPlan(json.getInt(PRACTICE_NUM_KEY));
            User user = User.createUserFromSharedPreferences(context, true);
            if(user != null && json.has(PracticeData.PRACTICE_PROGRAM_KEY) && !json.isNull(PracticeData.PRACTICE_PROGRAM_KEY) ) {
                pData.setNumOfPlan(user.getProgramId());
            }
            //if(json.has(PracticeData.PRACTICE_PROGRAM_KEY) && !json.isNull(PracticeData.PRACTICE_PROGRAM_KEY)) pData.setNumOfPlan(json.getInt(PRACTICE_PROGRAM_KEY));


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return pData;
    }

    private void shrinkRatesSize(/*Context context*/){
        try {
            int maxSize = MAX_LOCATIONS_SIZE;
            int ratesSize = mAllRates.size();
            Log.d(PracticeData.class.getName(), " ratesSize = = = = = = = = = = = = = = = = = = = = = = " + ratesSize);
            if (ratesSize > maxSize) {
                while (ratesSize > maxSize) {
                    Log.d(PracticeData.class.getName(), " ratesSize bigger than " + maxSize);
                    Double jumpSizeD = null;
                    int i = 0;
                    if (ratesSize > maxSize * 2) {
                        Log.d(PracticeData.class.getName(), " ratesSize bigger than " + maxSize + " * 2 ");
                        jumpSizeD = (double) (ratesSize / maxSize);
                        if (jumpSizeD == null) return;
                        int jumpSize = jumpSizeD.intValue();
                        Iterator<Rate> iterator = mAllRates.iterator();
                        while (iterator.hasNext()) {
                            //MinimalLocation mLoc =
                            iterator.next();
                            if (i < jumpSize) {
                                iterator.remove();
                            } else {
                                i = 0;
                            }
                            i++;
                        }
                    } else {
                        Log.d(PracticeData.class.getName(), " ratesSize smaller than " + maxSize + " * 2 ");
                        int delta = ratesSize - maxSize;//260
                        if(delta == 1) delta = 2 ;
                        jumpSizeD = (double) (ratesSize / delta);//2.969
                        if (jumpSizeD == null) return;
                        Long jumpSizeL = Math.round(jumpSizeD);
                        int jumpSize = Integer.valueOf(jumpSizeL.intValue());
                        Log.d(PracticeData.class.getName(), " jumpSize = = = = = = = = = = = = = = = = = = = = = = " + jumpSize);
                        Iterator<Rate> iterator = mAllRates.iterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            if (i >= jumpSize) {
                                iterator.remove();
                                i = 1;
                            } else {
                                i++;
                            }
                        }
                    }

                    ratesSize = mAllRates.size();
                    Log.d(PracticeData.class.getName(), " Round Over. new ratesSize = = = = = = = = = = = = = = = = = = = = = = " + ratesSize);

                }
            }
            Log.d(PracticeData.class.getName(), " ALL OVER. new ratesSize = = = = = = = = = = = = = = = = = = = = = = " + mAllRates.size());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
    context will enable saving to db.
    if null sent, will not save to db
     */
    private void shrinkLocationsSize(/*Context context*/){
        int maxSize = MAX_LOCATIONS_SIZE;
        int locationsSize = mLocations.size();
        Log.d(PracticeData.class.getName()," locationsSize = = = = = = = = = = = = = = = = = = = = = = " + locationsSize);
        if(locationsSize > maxSize) {
            while (locationsSize > maxSize) {
                Log.d(PracticeData.class.getName(), " locationsSize bigger than " + maxSize );
                Double jumpSizeD = null;
                int i = 0;
                if (locationsSize > maxSize * 2) {
                    //Log.d(PracticeData.class.getName(), " locationsSize bigger than " + maxSize + " * 2 ");
                    jumpSizeD = (double) (locationsSize / maxSize);
                    if (jumpSizeD == null) return;
                    int jumpSize = jumpSizeD.intValue();
                    Iterator<MinimalLocation> iterator = mLocations.iterator();
                    while (iterator.hasNext()) {
                        //MinimalLocation mLoc =
                        iterator.next();
                        if (i < jumpSize) {
                            iterator.remove();
                        } else {
                            i = 0;
                        }
                        i++;
                    }
                } else {
                    Log.d(PracticeData.class.getName(), " locationsSize smaller than " + maxSize + " * 2 ");
                    int delta = locationsSize - maxSize;//260
                    if(delta == 1) delta = 2 ;
                    jumpSizeD = (double) (locationsSize / delta);//2.969
                    if (jumpSizeD == null) return;
                    Long jumpSizeL = Math.round(jumpSizeD);
                    int jumpSize = Integer.valueOf(jumpSizeL.intValue());
                    Log.d(PracticeData.class.getName(), " jumpSize = = = = = = = = = = = = = = = = = = = = = = " + jumpSize);
                    Iterator<MinimalLocation> iterator = mLocations.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        if (i >= jumpSize) {
                            iterator.remove();
                            i = 1;
                        } else {
                            i++;
                        }
                    }
                }

                locationsSize = mLocations.size();
                Log.d(PracticeData.class.getName(), " Round Over. new locationsSize = = = = = = = = = = = = = = = = = = = = = = " + locationsSize);
            }
        }
        Log.d(PracticeData.class.getName()," ALL OVER. new locationsSize = = = = = = = = = = = = = = = = = = = = = = " + mLocations.size());

        setTimeStamp(mLocations.get(0).getTimeStamp()); //fix bug that sometimes pd not deleted from db or sharedPrefs etc. BH !!! 22.3.2018
    }

    public void shrinkWhateverNeeded(){
        try {
            if (getLocations() != null && getLocations().size() > MAX_LOCATIONS_SIZE) {
                shrinkLocationsSize();
                shrinkRatesSize();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void smoothLocations() {
        try {
            calculateBezierLocations();
            setTimeStamp(mLocations.get(0).getTimeStamp()); //fix bug that sometimes pd not deleted from db or sharedPrefs etc.
        } catch (Exception ex){
            ex.printStackTrace();
            // no smoothing done...
        }
    }

    private void calculateBezierLocations(){

        MinimalLocation[] postBezierArray = new MinimalLocation[mLocations.size()];

        //copy locations into postBezierArray
        for(int i = 0 ; i < mLocations.size() ; i++ ) {
            postBezierArray[i] = new MinimalLocation(mLocations.get(i));
        }

        MinimalLocation[] tmp = new MinimalLocation[mLocations.size()];

        for (int i = 0 ; i < postBezierArray.length ; i++) {

            double t = ((double) i)/(postBezierArray.length - 1);

            //copy mLocations into tmp
            for (int j = 0 ; j < mLocations.size(); j++) {
                tmp[j] = new MinimalLocation(mLocations.get(j));
            }


            int Depth = tmp.length;

            while (Depth > 1) {
                for (int j=0 ; j < Depth-1 ; j++) {
                    SubDivide(tmp[j], tmp[j + 1], t);
                }
                Depth--;
            }
            postBezierArray[i] = new MinimalLocation(tmp[0]);
        }

        mLocations.clear();
        mLocations.addAll(Arrays.asList(postBezierArray));

    }

    private void SubDivide(MinimalLocation p1, MinimalLocation p2, double t) {

        if(p1.getLon() >p2.getLon()) {
            p1.setLon(p1.getLon() - Math.abs(p1.getLon() - p2.getLon()) * t);
        } else {
            p1.setLon(p1.getLon() + Math.abs(p1.getLon() - p2.getLon()) * t);
        }

        if (p1.getLat() > p2.getLat()) {
            p1.setLat(p1.getLat() - Math.abs(p1.getLat() - p2.getLat()) * t);
        } else {
            p1.setLat(p1.getLat() + Math.abs(p1.getLat() - p2.getLat())*t);
        }
    }

    public void getGoogleElevationData(final Context context, final PracticeDataCreatedCallback callback) {
            if(mLocations == null || mLocations.size() <= 0){
                if(callback != null) callback.requestEndedWithError(new Exception("no locations"));
                return;
            }
            // reduce size of locations to max of 512 locations to fit google elevation max limit
            //todo: this may cause aesthetic problem when show map for very long runs. if yes, change to 2 batches of requests
            //shrinkLocationsSize(context); //6.3.2018 this was called already in creation of PracticeData from pdd.

            getElevationForLocations(context, 0, mLocations.size() > 300 ? 199 : mLocations.size()-1, callback);
    }

    private void getElevationForLocations(final Context context, int startPoint, final int endPoint, final PracticeDataCreatedCallback callback) {
         //call method to get for these locations

        //Build the request
        StringBuilder httpRequestString = new StringBuilder("https://maps.googleapis.com/maps/api/elevation/json?locations=");
        int counter = 0;
        List<MinimalLocation> locationsBit = mLocations.subList(startPoint, endPoint+1);
        for(MinimalLocation loc : locationsBit){
            httpRequestString.append(loc.getLat() + "," +loc.getLon());
            counter++;
            if(counter < locationsBit.size()){
                httpRequestString.append("|");
            }
        }
//        httpRequestString.append("&key=" + context.getString(R.string.google_maps_elevation_key));
        //httpRequestString.append("&key=" + context.getString(R.string.google_api_key));
        httpRequestString.append("&key=" + context.getString(R.string.google_maps_key));

        Log.d("XXXXXXXXXXXXXXXX","getElevation() locationBit size = " + locationsBit.size() + " httpRequestString = " + httpRequestString.toString());

        NetworkCenter.getInstance().requestGet(context, httpRequestString.toString(),null, null, new GetElevationResponseCallback(startPoint, endPoint) {
            @Override
            public void requestStarted() {
                Log.d("XXXXXXXXXXXXXXXX","getElevation() requestStarted !!!!!!! ");
            }

            @Override
            public void requestCompleted(String response) {
                Log.d(PracticeData.class.getName(),"getElevation() requestCompleted() !!!!!!! response = " );
                parseAndSaveGoogleElevationData(context, response, this.start, this.end, callback);
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                if(error != null) error.printStackTrace();
                Log.i("XXXXXXXXXXXXXXXX","getElevation() requestCompleted !!!!!!! error = " + error);
                if (callback != null) callback.requestEndedWithError(error);
            }
        });

        //sent a nother request if needed
        if(mLocations.size() > endPoint + 1){
            int newEndPoint = endPoint + 300 > mLocations.size()-1 ? mLocations.size()-1 : endPoint + 199;
            getElevationForLocations(context, endPoint+1, newEndPoint, callback);
        }
    }


    private void initiatePracticeData(){
       if (mRatesPerKM == null) mRatesPerKM = new ArrayList<Long>();
       if (mAllRates == null) mAllRates = new ArrayList<Rate>();
    }

    protected PracticeData(Parcel in) {
        serverId = in.readString();
        mTimeStamp = in.readLong();
        mLocations = in.createTypedArrayList(MinimalLocation.CREATOR);
        Object[] obj = in.readArray(Long.class.getClassLoader());
        mRatesPerKM = new ArrayList<Long>();
        if(obj != null){
            for(int i = 0 ; i < obj.length ; i++){
                mRatesPerKM.add(((Long) obj[i]));
            }
        }
        mAllRates = in.createTypedArrayList(Rate.CREATOR);
        mAvgRateInMillies = in.readLong();
        mBestRateInMilliesForKm = in.readLong();
        mSpeed = in.readDouble();
        mDistanceInMeters = in.readInt();
        mDuration = in.readLong();
        mKcl = in.readInt();
        mPositiveSlope = in.readDouble();
        mTemperature = in.readDouble();
        //int hasEle = in.readInt();
        //setHasElevationInfo(hasEle == 1);
        //int isSaved = in.readInt();
        //setSavedToBackend(isSaved == 1);
        this.numOfLessonInPlan = in.readInt();
        this.numOfPlan = in.readInt();

    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(serverId);
//        dest.writeLong(mTimeStamp);
//        dest.writeTypedList(mLocations);
//        dest.writeArray(mRatesPerKM.toArray());
//        dest.writeTypedList(mAllRates);
//        dest.writeLong(mAvgRateInMillies);
//        dest.writeLong(mBestRateInMilliesForKm);
//        dest.writeDouble(mSpeed);
//        dest.writeInt(mDistanceInMeters);
//        dest.writeLong(mDuration);
//        dest.writeInt(mKcl);
//        dest.writeDouble(mPositiveSlope);
//        dest.writeDouble(mTemperature);
////        dest.writeInt(hasElevationInfo);
////        dest.writeInt(savedToBackend);
//        dest.writeInt(getNumOfLessonInPlan());
//        dest.writeInt(getNumPlan());
//
//    }
//
//    public static final Creator<PracticeData> CREATOR = new Creator<PracticeData>() {
//        @Override
//        public PracticeData createFromParcel(Parcel in) {
//            return new PracticeData(in);
//        }
//
//        @Override
//        public PracticeData[] newArray(int size) {
//            return new PracticeData[size];
//        }
//    };

    public String getLocationsAsJson(){
        Gson gson = new Gson();
        return gson.toJson(mLocations);
    }

    public String getAllRatesAsJson(){
//        ((List<Rate>) mAllRates);
//
//        gson.toJsonTree(mAllRates.subList(0, mAllRates.size()), new TypeToken<List<Rate>>() { }.getType());
        Gson gson = new Gson();
        return gson.toJson(mAllRates);
    }

    public String getRatesPerKMAsJson(){
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        //gsonBuilder.serializeSpecialFloatingPointValues();
//        Gson gson = gsonBuilder.create();
        Gson gson = new Gson();
        String r = gson.toJson(mRatesPerKM);
        return r;
    }

    public void setLocations(String json/*, boolean saveTimeStamp*/){
        if(json == null) {
            this.setLocations((ArrayList<MinimalLocation> )null);
        } else {
            Type listType = new TypeToken<ArrayList<MinimalLocation>>() { }.getType();
            ArrayList<MinimalLocation> locationsList = new Gson().fromJson(json, listType);
            this.setLocations(locationsList);//, saveTimeStamp);
        }
    }

    public void setAllRates(String json){
        if(json == null) return;
        Type listType = new TypeToken<ArrayList<Rate>>(){}.getType();
        ArrayList<Rate> allRates = new Gson().fromJson(json, listType);
        this.setAllRates(allRates);
    }

    public void setRatesPerKM(String json){
        if(json == null) return;
        Type listType = new TypeToken<ArrayList<Long>>(){}.getType();
        ArrayList<Long> ratesPerKM = new Gson().fromJson(json, listType);

        this.setRatesPerKM(ratesPerKM);
    }

    public ArrayList<MinimalLocation> getLocations() {
        return mLocations;
    }

    public ArrayList<Rate> getAllRates() {
        if(mAllRates != null ) Log.d(getClass().getName(), " getAllRates size = " + mAllRates.size());
        return mAllRates;
    }

    public void setAllRates(ArrayList<Rate> allRates) {
        if(this.mAllRates == null) {
            this.mAllRates = new ArrayList<Rate>();
        } else {
            this.mAllRates.clear();
        }
        if(allRates != null) this.mAllRates.addAll(allRates);

    }

    public ArrayList<Long> getRatesPerKM() {
        return mRatesPerKM;
    }

    public void setRatesPerKM(ArrayList<Long> ratesPerKM) {
        if(mRatesPerKM == null) {
            this.mRatesPerKM = new ArrayList<Long>();
        } else {
            this.mRatesPerKM.clear();
        }
        this.mRatesPerKM.addAll(ratesPerKM);
//        if(this.mRatesPerKM.size() > 0){
//            this.setBestRateForKmMiliies(this.mRatesPerKM.get(0));
//        }
//        for(Long rate : this.mRatesPerKM){
//            if(rate < this.getBestRateForKmMiliies()){
//                this.setBestRateForKmMiliies(rate);
//            }
//        }
    }

    public void setLocations(ArrayList<MinimalLocation> locations){//}, boolean saveTimeStamp) {
        if(this.mLocations == null) this.mLocations = new ArrayList<MinimalLocation>();
        if(locations == null || locations.size() < 1){
            Log.d(getClass().getName(), " setLocations(locations == null");
            return;
        }
        this.mLocations.clear();
        this.mLocations.addAll(locations);
        //if(saveTimeStamp) {
            MinimalLocation mLoc = this.mLocations.get(0);
            this.setTimeStamp(mLoc.getTimeStamp());
        //}
    }


    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    private void setTimeStamp(long timeStamp) {
        //Log.d(getClass().getName()," #-#-#-#-#-#-# SetTimeStamp old val = " + mTimeStamp + " newVal = " + timeStamp + " oldVal == newVal = " + (timeStamp == mTimeStamp));
        this.mTimeStamp = timeStamp;
    }

    public float getDistanceInMeters() {
        return mDistanceInMeters;
    }

    public void setDistanceInMeters(int distance) {
        this.mDistanceInMeters = distance;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public int getKcl() {
        return this.mKcl;
    }

    public void setKcl(int kkl) {
        this.mKcl = kkl;
    }

    public double getPositiveSlope() {
        return mPositiveSlope;
    }

    public void setPositiveSlope(double positiveSlope) {
        this.mPositiveSlope = positiveSlope;
    }

    public void setAvgRate(long avgRateInMillies) {
        this.mAvgRateInMillies = avgRateInMillies;
    }

    public void setSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public long getAvgRate() {
        return this.mAvgRateInMillies;
    }

    public double getSpeed() {
        return this.mSpeed;
    }

    public Date getDate(){
        return new Date(this.mTimeStamp);
    }

//    public int getCalories() {
//        return mCalories;
//    }

//    public ArrayList<Double> getRates(){
//        return this.mRatesPerKM;
//    }


    public long getBestRateForKmMiliies() {
        return mBestRateInMilliesForKm;
    }

    public void setBestRateForKmMiliies(long maxRateInMillies) {
        this.mBestRateInMilliesForKm = maxRateInMillies;
    }




    private double getRatio(float totalDistanceInMeters) {
        if(totalDistanceInMeters > 0) {
            return ((double)((double)1000)/((double)totalDistanceInMeters));
        } else {
            return 0;
        }
    }

    static public double millisToMinutes(long millis){
        return ((double)((double)millis / (double)(1000 * 60)));
    }

    static public double millisToHours(long millis){
        double sec = ((double)millis)/((double)1000);
        double min = ((double)sec)/((double)60);
        double hour = ((double)min)/((double)60);

        return hour;
    }

    public double calculateElevationData(){
        double totalElevation = 0, previousElevation = 0;
        boolean firstTime = true;
        //int counter = 0;
        for(MinimalLocation loc : mLocations){
            //counter++;
            double elevation = loc.getElevation();
            if(elevation <= MinimalLocation.NO_ELEVATION) {
                return -1; //error. not all elevations are filled
            }
            if (firstTime) {
                firstTime = false;
            } else {
                double deltaElevation = elevation - previousElevation;
                if (deltaElevation > 0) {
                    totalElevation += deltaElevation;
                }
            }
            previousElevation = elevation;
        }
        return totalElevation;
    }

    public void parseAndSaveGoogleElevationData(Context context, String json, int startPoint, int endPoint, PracticeDataCreatedCallback callback) {
        //todo: if has problem with length of path, check again with Pitagoras
        if (json == null) {
            Log.d(getClass().toString(),"parseAndSaveGoogleElevationData from " + startPoint +  " to " + endPoint + " json == null " );
            if(callback != null) callback.requestEndedWithError(null);
            return;
        }

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray resultsJsonArray = jObject.getJSONArray("results");
            //double totalElevation = 0, previousElevation = 0;
            //boolean firstTime = true;
            for(int i = 0; i < resultsJsonArray.length() ; i++){
                JSONObject  jObjData = resultsJsonArray.getJSONObject(i);

                JSONObject jObjLocation =  jObjData.getJSONObject("location");
                double lng = jObjLocation.getDouble("lng");
                double lat = jObjLocation.getDouble("lat");

                double elevation = jObjData.getDouble("elevation");
                double resolution = jObjData.getDouble("resolution");
//                if(firstTime) {
//                    previousElevation = elevation;
//                    firstTime = false;
//                }
//                double deltaElevation = elevation - previousElevation;
//                if(deltaElevation > 0) totalElevation += deltaElevation;
                int iInBit = startPoint + i;

                if ( iInBit < mLocations.size()){
                    MinimalLocation mLoc = mLocations.get(iInBit);
                    //if(mLoc.getLat() == lat && mLoc.getLon() == lng){
                        mLoc.setElevation(elevation);
                       // lam.setResolution(resolution);
//                    } else {
//                        //what if else? maybe just skip the loc? can it be caused by number format?
//                        Log.i(getClass().toString(),"------------------------ VERY IMPORTANT TO KNOW!! in parseJSON() lat and lng are not identicle in result and Original array ------------------------ i = " + i + " iInBit " + iInBit );
//                        Log.i(getClass().toString(),"result lat = " + lat + " array lat = " + mLoc.getLat() + " result lng = " + lng + " array lng = " + mLoc.getLon());
//                        Log.i(getClass().toString(),"------------------------  ------------------------ ------------------------  ------------------------ ------------------------  ------------------------ ");
//
//                    }
                }
                //locationsAndMore.add(new LocationAndMore(lat,lng,elevation,resolution,0));

                //Log.i(getClass().toString(),"parseJSON() lng=" + lng + " lat=" + lat + " elevation= " + elevation + " resolution= " +resolution);

            }


            double totalElevation = calculateElevationData();
            if(totalElevation > -1) {
//                this.setHasElevationInfo(true);
                this.setPositiveSlope(totalElevation);

                if (callback != null) callback.PracticeDataCreated(this);
            }

        } catch (JSONException e){
            e.printStackTrace();
            if(callback != null) callback.requestEndedWithError(e);
        }
    }

    public double getDistanceInKm() {
        if(getDistanceInMeters() > 0) {
            return ((double) getDistanceInMeters()) / ((double) 1000);
        } else {
            return 0;
        }
    }

    public double getTemperature() {
        return mTemperature;
    }

    public String getTemperatureAsString() {
        if(mTemperature == -100 || mTemperature > 60) return "";
        return String.valueOf((int)mTemperature).concat("\u2103");
    }


    public void writeToSharedPreferences(Context context, String key, boolean eraseBeforeSave){
        if(key == null) key = PracticeData.PRACTICE_DATA_SAVE_KEY;
        if(eraseBeforeSave) eraseFromSharedPreferences(context, key);

        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SERVER_ID_KEY, getServerId());
        editor.putLong(TIMESTAMP_KEY, getTimeStamp());
        editor.putString(ALL_LOCATIONS_KEY, getLocationsAsJson());
        editor.putString(ALL_RATES_KEY, getAllRatesAsJson());
        editor.putString(RATES_PER_KM_KEY, getRatesPerKMAsJson());
        editor.putLong(AVE_RATE_KEY, getAvgRate());
        editor.putLong(MAX_RATE_KEY, getBestRateForKmMiliies());
        editor.putFloat(SPEED_KEY, (float)getSpeed());
        editor.putInt(DISTANCE_KEY, (int)getDistanceInMeters());
        editor.putLong(DURATION_KEY, getDuration());
        editor.putInt(CALORIES_KEY, getKcl());
        editor.putFloat(ELEVATION_KEY, (float)getPositiveSlope());
        editor.putFloat(TEMPERATURE_KEY, (float)getTemperature());
        editor.putInt(PRACTICE_NUM_KEY, getNumOfLessonInPlan());
        editor.putInt(PRACTICE_PROGRAM_KEY, getNumPlan());

        editor.commit();//commit will write changes immediately

    }

    public void updateLocationsToSharedPreferences(Context context, String keyToSave) {
        if(keyToSave == null) keyToSave = PracticeData.PRACTICE_DATA_SAVE_KEY;
        SharedPreferences prefs = context.getSharedPreferences(keyToSave, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ALL_LOCATIONS_KEY);
        editor.putString(ALL_LOCATIONS_KEY, getLocationsAsJson());
        editor.commit();
    }

    public static PracticeData readFromSharedPreferences(Context context , String key){
        if(key == null) key = PracticeData.PRACTICE_DATA_SAVE_KEY;
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        if(prefs != null && prefs.contains(TIMESTAMP_KEY)){
            PracticeData pData = new PracticeData();
            pData.setServerId(prefs.getString(SERVER_ID_KEY, null));
            pData.setTimeStamp(prefs.getLong(TIMESTAMP_KEY,0));
            pData.setLocations(prefs.getString(ALL_LOCATIONS_KEY, null));
            pData.setAllRates(prefs.getString(ALL_RATES_KEY, null));
            pData.setRatesPerKM(prefs.getString(RATES_PER_KM_KEY, null));
            pData.setAvgRate(prefs.getLong(AVE_RATE_KEY, 0));
            pData.setBestRateForKmMiliies(prefs.getLong(MAX_RATE_KEY, 0));
            pData.setSpeed((double)prefs.getFloat(SPEED_KEY, 0));
            pData.setDistanceInMeters(prefs.getInt(DISTANCE_KEY,0));
            pData.setDuration(prefs.getLong(DURATION_KEY,0));
            pData.setKcl(prefs.getInt(CALORIES_KEY,0));
            pData.setPositiveSlope((double)prefs.getFloat(ELEVATION_KEY, 0));
            pData.setTemperature((double)prefs.getFloat(TEMPERATURE_KEY, 100));
            pData.setNumOfLessonInPlan(prefs.getInt(PRACTICE_NUM_KEY,0));
            pData.setNumOfPlan(prefs.getInt(PRACTICE_PROGRAM_KEY,0));
            return pData;
        } else {
            return null;
        }
    }



    public static void eraseFromSharedPreferences(Context context, String key){
        if(key == null) key = PracticeData.PRACTICE_DATA_SAVE_KEY;
        //erase user from sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        if(prefs == null){
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();//commit will write changes immediately
    }


    public static boolean isSavedPracticePartOfPlan(Context context, int lessonNum) {
        SharedPreferences prefs = context.getSharedPreferences(PracticeData.PRACTICE_DATA_SAVE_KEY, Context.MODE_PRIVATE);
        if(prefs != null && prefs.contains(TIMESTAMP_KEY)) {

            return prefs.getInt(PRACTICE_NUM_KEY, 0) == lessonNum;
        }
        return false;

    }


}
