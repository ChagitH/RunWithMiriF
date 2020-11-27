package com.forst.miri.runwithme.miscellaneous;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.forst.miri.runwithme.interfaces.DatabaseUpdatedCallback;
import com.forst.miri.runwithme.objects.MinimalLocation;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.Rate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chagithazani on 9/17/17.
 */

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {


//    private Context context;
    //private static LocationAddedListener locationAddedListener = null;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "RunWithMiri";

    // Contacts table name
    private static final String TABLE_TRAININGS = "RunTrainings";
    private static final String TABLE_TRAININGS_NO_ELEVATION = "RunTrainings_noElevation";
    private static final String TABLE_TRAININGS_NOT_SAVED_TO_BACKEND = "RunTrainings_notSavedToBackend";
    private static final String TABLE_LOCATIONS = "Locations_Table";
    private static final String TABLE_RATES = "Rates_Table";


    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        this.context = context;
    }

//    public static void setListener(LocationAddedListener listener){
//        locationAddedListener = listener;
//    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TRAININGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TRAININGS + " ("
                + PracticeData.TIMESTAMP_KEY + " INTEGER PRIMARY KEY UNIQUE, "
                + PracticeData.SERVER_ID_KEY + " TEXT, "
                + PracticeData.ALL_LOCATIONS_KEY + " TEXT, "
                + PracticeData.ALL_RATES_KEY + " TEXT, "
                + PracticeData.RATES_PER_KM_KEY + " TEXT, "
                + PracticeData.DISTANCE_KEY + " INTEGER, "
                + PracticeData.DURATION_KEY + " INTEGER, "
                + PracticeData.CALORIES_KEY + " INTEGER, "
                + PracticeData.AVE_RATE_KEY + " INTEGER, "
                + PracticeData.MAX_RATE_KEY + " INTEGER, "
                + PracticeData.PRACTICE_NUM_KEY + " INTEGER, "
                + PracticeData.PRACTICE_PROGRAM_KEY + " INTEGER, "
                + PracticeData.ELEVATION_KEY + " REAL, "
                + PracticeData.TEMPERATURE_KEY + " REAL, "
                + PracticeData.SPEED_KEY + " REAL )";


        Log.d(this.getClass().getName(),"create table str ->->->->->->>->->>->->  " + CREATE_TRAININGS_TABLE);
        db.execSQL(CREATE_TRAININGS_TABLE);


        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + MinimalLocation.TIMESTAMP_KEY + " INTEGER PRIMARY KEY, "
                + MinimalLocation.LATITUDE_KEY + " REAL ,"
                + MinimalLocation.LONGITUDE_KEY + " REAL " + ")";

        Log.d(this.getClass().getName(),"create table str = " + CREATE_LOCATIONS_TABLE);
        db.execSQL(CREATE_LOCATIONS_TABLE);

        String CREATE_RATES_TABLE = "CREATE TABLE " + TABLE_RATES + "("
                + Rate.TIMESTAMP_KEY + " INTEGER PRIMARY KEY, "
                //+ Rate.KM_KEY + " INTEGER "
                + Rate.METER_TOTAL_KEY + " REAL ,"
                + Rate.RATE_MILLIS_KEY + " INTEGER "+ ")";

        Log.d(this.getClass().getName(),"create table str = " + CREATE_RATES_TABLE);
        db.execSQL(CREATE_RATES_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS_NOT_SAVED_TO_BACKEND);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS_NO_ELEVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
        // Create tables again
        onCreate(db);
    }

    private ContentValues getContentValuesFullPD(PracticeData practiceData, boolean withTimestamp) {
        ContentValues values = new ContentValues();
        values.put(PracticeData.SERVER_ID_KEY, practiceData.getServerId());
        values.put(PracticeData.ALL_LOCATIONS_KEY, practiceData.getLocationsAsJson());
        if(withTimestamp) values.put(PracticeData.TIMESTAMP_KEY, practiceData.getTimeStamp());
        values.put(PracticeData.ALL_RATES_KEY, practiceData.getAllRatesAsJson());
        values.put(PracticeData.RATES_PER_KM_KEY, practiceData.getRatesPerKMAsJson());
        values.put(PracticeData.DISTANCE_KEY, practiceData.getDistanceInMeters());
        values.put(PracticeData.DURATION_KEY, practiceData.getDuration());
        values.put(PracticeData.CALORIES_KEY, practiceData.getKcl());
        values.put(PracticeData.AVE_RATE_KEY, practiceData.getAvgRate());
        values.put(PracticeData.MAX_RATE_KEY, practiceData.getBestRateForKmMiliies());
        values.put(PracticeData.PRACTICE_NUM_KEY, practiceData.getNumOfLessonInPlan());
        values.put(PracticeData.PRACTICE_PROGRAM_KEY, practiceData.getNumPlan());
        values.put(PracticeData.ELEVATION_KEY, practiceData.getPositiveSlope());
        values.put(PracticeData.TEMPERATURE_KEY, practiceData.getTemperature());
        values.put(PracticeData.SPEED_KEY, practiceData.getSpeed());

        return values;
    }


    public void addPractice(PracticeData pData, DatabaseUpdatedCallback callback){
        SQLiteDatabase db = null;
        try {
//            //todo *********************************************************************************************************************************************************************
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
//            Log.d(getClass().toString(), "  addFullPractice() :  BEFORE add Practice ");
//            List<PracticeData>  practices1 = getAllFullPractices();
//            for(PracticeData pd : practices1) {
//                Log.d(getClass().toString(), " TimeStamp: " + pd.getTimeStamp() + " server id: " + pd.getServerId() + " locations size: " + pd.getLocations().size() + " lesson num: " + pd.getNumOfLessonInPlan() + " saved to server: ");
//                Log.d(getClass().toString(), " date = " + new Date(pd.getTimeStamp()).toString() + " Length = " + pd.getDistanceInMeters() + " plan num = " + pd.getNumPlan() + " num of lesson " + pd.getNumOfLessonInPlan());
//            }
//            Log.d(getClass().toString(), " PracticeData to add has timestamp: " + pData.getTimeStamp());
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
//            //todo *********************************************************************************************************************************************************************

            ContentValues values = getContentValuesFullPD(pData, true);

            // Inserting Row
            db = this.getWritableDatabase();
            db.insert(TABLE_TRAININGS, null, values);
            db.close();
            if (callback != null) callback.updateEndedWithNoException();
//            //todo *********************************************************************************************************************************************************************
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
//            Log.d(getClass().toString(), "  addFullPractice() :  AFTER add Practice ");
//            practices1 = getAllFullPractices();
//            for(PracticeData pd : practices1) {
//                Log.d(getClass().toString(), " TimeStamp: " + pd.getTimeStamp() + " server id: " + pd.getServerId() + " locations size: " + pd.getLocations().size() + " lesson num: " + pd.getNumOfLessonInPlan() + " saved to server: ");
//                Log.d(getClass().toString(), " date = " + new Date(pd.getTimeStamp()).toString() + " Length = " + pd.getDistanceInMeters() + " plan num = " + pd.getNumPlan() + " num of lesson " + pd.getNumOfLessonInPlan());
//            }
//            Log.d(getClass().toString(), " PracticeData to add has timestamp: " + pData.getTimeStamp());
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
//            //todo *********************************************************************************************************************************************************************


        } catch(Exception e){
            e.printStackTrace();
            if(db != null && db.isOpen()) db.close();
            if (callback != null) callback.updateFailedWithException(e);
        }
    }

    public void updatePractice(PracticeData pData, DatabaseUpdatedCallback databaseUpdatedCallback) {
        SQLiteDatabase db = null;
        try {

//            //            //todo *********************************************************************************************************************************************************************
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
//            Log.d(getClass().toString(), "  updatePractice() :  BEFORE update Practice ");
//            List<PracticeData>  practices1 = getAllPractices();
//            for(PracticeData pd : practices1) {
//                Log.d(getClass().toString(), " TimeStamp: " + pd.getTimeStamp() + " server id: " + pd.getServerId() + " locations size: " + pd.getLocations().size() + " lesson num: " + pd.getNumOfLessonInPlan() + " saved to server: ");
//                Log.d(getClass().toString(), " date = " + new Date(pd.getTimeStamp()).toString() + " Length = " + pd.getDistanceInMeters() + " plan num = " + pd.getNumPlan() + " num of lesson " + pd.getNumOfLessonInPlan());
//            }
//            Log.d(getClass().toString(), " PracticeData to update has timestamp: " + pData.getTimeStamp());
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
////            //todo *********************************************************************************************************************************************************************


            ContentValues values = getContentValuesFullPD(pData, false);

            // Inserting Row
            db = this.getWritableDatabase();
            db.update(TABLE_TRAININGS, values, PracticeData.TIMESTAMP_KEY+"=? ", new String[] {String.valueOf(pData.getTimeStamp())});
            db.close();

            if (databaseUpdatedCallback != null) databaseUpdatedCallback.updateEndedWithNoException();

//            //            //todo *********************************************************************************************************************************************************************
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
//            Log.d(getClass().toString(), "  addFullPractice() :  AFTER update Practice ");
//            practices1 = getAllPractices();
//            for(PracticeData pd : practices1) {
//                Log.d(getClass().toString(), " TimeStamp: " + pd.getTimeStamp() + " server id: " + pd.getServerId() + " locations size: " + pd.getLocations().size() + " lesson num: " + pd.getNumOfLessonInPlan() + " saved to server: ");
//                Log.d(getClass().toString(), " date = " + new Date(pd.getTimeStamp()).toString() + " Length = " + pd.getDistanceInMeters() + " plan num = " + pd.getNumPlan() + " num of lesson " + pd.getNumOfLessonInPlan());
//            }
//            Log.d(getClass().toString(), " PracticeData to update has timestamp: " + pData.getTimeStamp());
//            Log.d(getClass().toString(), " ********************************************************************************************************************************************************************* ");
////            //todo *********************************************************************************************************************************************************************

        } catch(Exception e){
            e.printStackTrace();
            if(db != null && db.isOpen()) db.close();
            if (databaseUpdatedCallback != null) databaseUpdatedCallback.updateFailedWithException(e);
        }
    }

    public PracticeData getPracticeFromFullTable(Cursor cursor){
        PracticeData practiceData = null;
        if (cursor != null && cursor.getCount() > 0){ //will this fix the java.lang.IllegalStateException ?
            practiceData = new PracticeData();

            //practiceData.setTimeStamp(cursor.getLong(cursor.getColumnIndex(PracticeData.TIMESTAMP_KEY))); //todo throws exception Caused by: java.lang.IllegalStateException: Couldn't read row 0, col 0 from CursorWindow.  Make sure the Cursor is initialized correctly before accessing data from it.
            //practiceData.setTimeStamp(Long.parseLong(cursor.getString(0)));
            practiceData.setServerId(cursor.getString(1));
            practiceData.setLocations(cursor.getString(2));
            practiceData.setAllRates(cursor.getString(3));
            practiceData.setRatesPerKM(cursor.getString(4));
            practiceData.setDistanceInMeters(Integer.parseInt(cursor.getString(5)));
            practiceData.setDuration(Integer.parseInt(cursor.getString(6)));
            practiceData.setKcl(Integer.parseInt(cursor.getString(7)));
            try {
                practiceData.setAvgRate(Long.parseLong(cursor.getString(8)));
            }catch (Exception e){
                e.printStackTrace();
                practiceData.setAvgRate(0);
            }
            try {
                practiceData.setBestRateForKmMiliies(Long.parseLong(cursor.getString(9)));
            }catch (Exception e){
                e.printStackTrace();
                practiceData.setBestRateForKmMiliies(0);
            }
            practiceData.setNumOfLessonInPlan(cursor.getInt(10));
            practiceData.setNumOfPlan(cursor.getInt(11));
            practiceData.setPositiveSlope(Double.parseDouble(cursor.getString(12)));
            practiceData.setTemperature(Double.parseDouble(cursor.getString(13)));
            try {
                practiceData.setSpeed(Double.parseDouble(cursor.getString(14)));
            }catch (Exception e){
                e.printStackTrace();
                practiceData.setSpeed(0);
            }
        }
        return practiceData;
    }




    private String createSqlableEmail(String userEmail){
        if (userEmail == null) return null;
        String newEmail = userEmail.replaceAll("[^A-Za-z0-9]","_");
        //Log.d(getClass().getName(), " -------> Replaced " + userEmail + " To " + newEmail);
        return newEmail;
    }



    public List<PracticeData> getPracticesFromFullTable(String query, String[] whereArgs) {

        List<PracticeData> practicesList = new ArrayList<PracticeData>();
        // Select All Query
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, whereArgs);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PracticeData p1 = getPracticeFromFullTable(cursor);
                practicesList.add(p1);
                Log.d(getClass().toString(), " TimeStamp: " + p1.getTimeStamp() + " server id: " + p1.getServerId() + " locations size: " + p1.getLocations().size() + " lesson num: " + p1.getNumOfLessonInPlan() + " saved to server: ");
                Log.d(getClass().toString(), " date = " + new Date(p1.getTimeStamp()).toString() + " Length = " + p1.getDistanceInMeters() + " plan num = " + p1.getNumPlan() + " num of lesson " + p1.getNumOfLessonInPlan());
            } while (cursor.moveToNext());
        }
        if(!cursor.isClosed()) cursor.close();
        if(db.isOpen())db.close();
        return practicesList;
    }

    public List<PracticeData> getAllPractices() {
        String selectQuery = "SELECT * FROM " + TABLE_TRAININGS + " ORDER BY " + PracticeData.TIMESTAMP_KEY + " DESC";
        return  getPracticesFromFullTable(selectQuery, null);
    }

    public List<PracticeData> getPracticesFromTo(int from, int numOfRows) {
        String selectQuery = "SELECT * FROM " + TABLE_TRAININGS  + " ORDER BY " + PracticeData.TIMESTAMP_KEY + " DESC " + "LIMIT " + from + "," + numOfRows;
        return  getPracticesFromFullTable(selectQuery , null);
    }

    public List<PracticeData> getNoElevationPractices(){
        String selectQuery = "SELECT * FROM " + TABLE_TRAININGS + " WHERE " + PracticeData.ELEVATION_KEY + " =? " + " ORDER BY " + PracticeData.TIMESTAMP_KEY + " DESC";
        return  getPracticesFromFullTable(selectQuery, new String[]{String.valueOf(MinimalLocation.NO_ELEVATION)});
    }

    public List<PracticeData> getNotSavedToDBPractices(){
        String selectQuery = "SELECT * FROM " + TABLE_TRAININGS + " WHERE " + PracticeData.SERVER_ID_KEY + " =? "  + " ORDER BY " + PracticeData.TIMESTAMP_KEY + " DESC";
        return  getPracticesFromFullTable(selectQuery, new String[]{PracticeData.SERVER_ID_DEFAULT});
    }

    private List<PracticeData> getAllPracticesInProgram(int planNum) {
        String selectQuery = "SELECT * FROM " + TABLE_TRAININGS + " WHERE " + PracticeData.PRACTICE_PROGRAM_KEY + " =? "  + " ORDER BY " + PracticeData.TIMESTAMP_KEY + " DESC";
        return  getPracticesFromFullTable(selectQuery, new String[]{String.valueOf(planNum)});
    }

    public int getPracticesCount() {
        String countQuery = "SELECT * FROM " + TABLE_TRAININGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void deletePracticeFromFullTable(PracticeData practiceData, DatabaseUpdatedCallback callback) {

        //todo *********************************************************************************************************************************************************************
        Log.d(getClass().toString(), " ()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()() ");
        Log.d(getClass().toString(), "  deletePracticeFromFullTable() :  BEFORE delete Practice ");
        List<PracticeData > practices1 = getAllPractices();
        for(PracticeData pd : practices1) {
            Log.d(getClass().toString(), " TimeStamp: " + pd.getTimeStamp() + " server id: " + pd.getServerId() + " locations size: " + pd.getLocations().size() + " lesson num: " + pd.getNumOfLessonInPlan() + " saved to server: ");
            Log.d(getClass().toString(), " date = " + new Date(pd.getTimeStamp()).toString() + " Length = " + pd.getDistanceInMeters() + " plan num = " + pd.getNumPlan() + " num of lesson " + pd.getNumOfLessonInPlan());
        }
        Log.d(getClass().toString(), " PracticeData to add has timestamp: " + practiceData.getTimeStamp());
        Log.d(getClass().toString(), " ()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()() ");
        //todo *********************************************************************************************************************************************************************

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_TRAININGS, PracticeData.TIMESTAMP_KEY + " = ?", new String[]{String.valueOf(practiceData.getTimeStamp())});
            db.close();
            if(callback!= null) callback.updateEndedWithNoException();
        } catch (Exception e){
            if (db != null && db.isOpen()) db.close();
            if(callback!= null) callback.updateFailedWithException(e);
        }

        //todo *********************************************************************************************************************************************************************
        Log.d(getClass().toString(), " ()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()() ");
        Log.d(getClass().toString(), "  addNoElevationPractice() :  AFTER add Practice ");
        List<PracticeData > practices2 = getAllPractices();
        for(PracticeData pd : practices2) {
            Log.d(getClass().toString(), " TimeStamp: " + pd.getTimeStamp() + " server id: " + pd.getServerId() + " locations size: " + pd.getLocations().size() + " lesson num: " + pd.getNumOfLessonInPlan() + " saved to server: " );
            Log.d(getClass().toString(), " date = " + new Date(pd.getTimeStamp()).toString() + " Length = " + pd.getDistanceInMeters() + " plan num = " + pd.getNumPlan() + " num of lesson " + pd.getNumOfLessonInPlan());
        }
        Log.d(getClass().toString(), " PracticeData to add has timestamp: " + practiceData.getTimeStamp());
        Log.d(getClass().toString(), " ()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()() ");
        //todo *********************************************************************************************************************************************************************


    }

    public void clearAllFullPractices(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAININGS, null, null);
        db.close();
    }

//    public void clearAllNoElevationPractices(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_TRAININGS_NO_ELEVATION, null, null);
//        db.close();
//    }
//
//    public void clearAllNotSavedToBackendPractices(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_TRAININGS_NOT_SAVED_TO_BACKEND, null, null);
//        db.close();
//    }




//
    public void addLocation(Location loc) {
        ContentValues values = new ContentValues();

        values.put(MinimalLocation.TIMESTAMP_KEY, loc.getTime());
        values.put(MinimalLocation.LATITUDE_KEY, loc.getLatitude());
        values.put(MinimalLocation.LONGITUDE_KEY, loc.getLongitude());
        //values.put(LocationAndMore.ELE_KEY, lam.getElevation());

        try {
            // Inserting Row
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_LOCATIONS, null, values);
            db.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public MinimalLocation getLocation(Cursor cursor){
        MinimalLocation miniLoc = null;
        if (cursor != null){
            Long timestamp = cursor.getLong(0);
            Double lat = cursor.getDouble(1);
            Double lon = cursor.getDouble(2);
            miniLoc = new MinimalLocation(timestamp, lat, lon);
        }
        return miniLoc;
    }

    public ArrayList<MinimalLocation> getAllLocations() {
        ArrayList<MinimalLocation> locationsList = new ArrayList<MinimalLocation>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS + " ORDER BY " + MinimalLocation.TIMESTAMP_KEY + " ASC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MinimalLocation minLoc = getLocation(cursor);
                locationsList.add(minLoc);
            } while (cursor.moveToNext());
        }
        db.close();
        return locationsList;
    }

    public void clearAllLocations(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LOCATIONS, null, null);

        db.close();
    }

    public void addRate(Rate rate) {

        ContentValues values = new ContentValues();
        values.put(Rate.TIMESTAMP_KEY, rate.getTimestamp());//long
        //values.put(Rate.KM_KEY, km);//int
        values.put(Rate.METER_TOTAL_KEY, rate.getMeter());//float
        values.put(Rate.RATE_MILLIS_KEY, rate.getRateInMillies());//long

        try {
            // Inserting Row
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_RATES, null, values);
            db.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void clearAllRates(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RATES, null, null);

        db.close();
    }

    public Rate getRate(Cursor cursor){
        Rate rate = null;
        if (cursor != null){
            Long timestamp = cursor.getLong(0);
            //int km = cursor.getInt(1);
            float meter = cursor.getFloat(1);
            long rateInMillis = cursor.getLong(2);
            rate = new Rate(timestamp, meter, rateInMillis);
        }
        return rate;
    }

    public ArrayList<Rate> getAllRates() {
        ArrayList<Rate> rates = new ArrayList<Rate>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RATES + " ORDER BY " + Rate.TIMESTAMP_KEY + " ASC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Rate rate = getRate(cursor);
                rates.add(rate);
            } while (cursor.moveToNext());
        }
        db.close();
        return rates;
    }

    public PracticeData getLastPracticeOfProgram(Context context, int planNum, String userEmail) {
        int from = 0, to = 9;
        //while (true) {
//            List<PracticeData> practices = getAllPractices(context, userEmail, from, to);
            List<PracticeData> practices = getAllPracticesInProgram(planNum);//, userEmail);
            for (PracticeData pd : practices) {
                if (pd.getNumPlan() == planNum) {
                    return pd;
                }
            }
            from += 10;
            to += 10;

//            if(from >= 200) return null;
        return null;
        //}
    }



//    public List<PracticeData> getAllPractices(Context context, String userEmail, int fromRecord, int numOfRows){
//
//        Log.d(SQLiteDatabaseHandler.class.getName(), " getAllPractices.............. Users email:  " + userEmail);
//
//        ArrayList<PracticeData> targetArray = new ArrayList<PracticeData>();
//
//        List<PracticeData> notSavedPractices = PracticeDataTempStorageHelper.getNotSavedPracticeDataList(context, userEmail);
//        // if unsaved practices will fill up the needed amount
//        if (notSavedPractices != null && notSavedPractices.size() > fromRecord) {
//            targetArray.addAll(notSavedPractices.subList(fromRecord ,  numOfRows <= notSavedPractices.size() ? numOfRows :  notSavedPractices.size() - 1));
//        }
//
//
//        int numOfRows = (targetArray.size() > 0 ? targetArray.size() : fromRecord );
//        if(numOfRows > fromRecordCalculated) {
//
//            targetArray.addAll(getPracticesFromTo(fromRecordCalculated, numOfRows));
//        }
//
//        return targetArray;
//
//
//    }



    //    public List<PracticeData> getAllPractices(Context context, String userEmail, int fromRecord, int toRecord){
//    public List<PracticeData> getAllPractices(Context context, String userEmail){

//    public List<PracticeData> getAllPractices(Context context){
////        Log.d(SQLiteDatabaseHandler.class.getName(), " getAllPractices.............. Users email:  " + userEmail);
//
//        ArrayList<PracticeData> targetArray = new ArrayList<PracticeData>();
//
//        //List<PracticeData> notSavedPractices = PracticeDataTempStorageHelper.getNotSavedPracticeDataList(context, userEmail);
//        // if unsaved practices will fill up the needed amount
////        if (notSavedPractices != null && notSavedPractices.size() > fromRecord) {
////            targetArray.addAll(notSavedPractices.subList(fromRecord ,  toRecord <= notSavedPractices.size() ? toRecord :  notSavedPractices.size() - 1));
////        }
////        if (notSavedPractices != null && notSavedPractices.size() > 0) {
////            targetArray.addAll(notSavedPractices);
////        }
//
////        int fromRecordCalculated = (targetArray.size() > 0 ? targetArray.size() : fromRecord );
////        if(toRecord > fromRecordCalculated) {
//
////            targetArray.addAll(getPracticesFromTo(fromRecordCalculated, toRecord));
//        targetArray.addAll(getAllPractices());
////        }
//
//        return targetArray;
//
//
//    }

//    public int getAllPracticesCount(Context context, String userEmail){
//        try {
//            return getAllPractices(context, userEmail).size();
//        } catch (Exception e){
//            e.printStackTrace();
//            return 0;
//        }
//    }

//    private static ArrayList<PracticeData> merge(int maxNumOfRecords, List<PracticeData> list1, List<PracticeData> list2){//}, List<PracticeData> list3) {
//        ArrayList<PracticeData> targetArray = new ArrayList<PracticeData>();
//        if(list1 == null) list1 = new ArrayList<PracticeData>();
//        if(list2 == null) list2 = new ArrayList<PracticeData>();
//        //if(list3 == null) list3 = new ArrayList<PracticeData>();
//
//        //Log.d(SQLiteDatabaseHandler.class.getName(), " merge.............. fullPractices size = " + list1.size() + " notSavedPractices size = " + list2.size() + " noElevationPractices size = " + list3.size());
//
//
//        ArrayDeque<PracticeData> deque1 = new ArrayDeque<>();
//        deque1.addAll(list1);
//        ArrayDeque<PracticeData> deque2 = new ArrayDeque<>();
//        deque2.addAll(list2);
//        ArrayDeque<PracticeData> deque3 = new ArrayDeque<>();
//        //deque3.addAll(list3);
//
//
//        while( ! (deque1.isEmpty() && deque2.isEmpty() && deque3.isEmpty())){
//            Log.d(SQLiteDatabaseHandler.class.getName(), " Merging............... deque 1 size = " + deque1.size() + " deque 2 size = " + deque2.size() + " deque 3 size = " + deque3.size() + " ..... targetArray = " + targetArray.size());
//            long timeStamp1 = 0;
//            long timeStamp2 = 0;
//            long timeStamp3 = 0;
//
//            if(!deque1.isEmpty()) {
//                PracticeData pd = deque1.peek();
//                timeStamp1 = pd.getTimeStamp();
//            }
//
//            if(!deque2.isEmpty()) {
//                PracticeData pd = deque2.peek();
//                timeStamp2 = pd.getTimeStamp();
//            }
//
//            if(!deque3.isEmpty()) {
//                PracticeData pd = deque3.peek();
//                timeStamp3 = pd.getTimeStamp();
//            }
//
//            if (timeStamp1 >= timeStamp2 && timeStamp1 >= timeStamp3){ // 1 is biggest
//                targetArray.add(deque1.pop());
//            } else  if (timeStamp2 >= timeStamp1 && timeStamp2 >= timeStamp3){ // 1 is biggest
//                targetArray.add(deque2.pop());
//            } else  if (timeStamp3 >= timeStamp1 && timeStamp3 >= timeStamp2){ // 1 is biggest
//                targetArray.add(deque3.pop());
//            }
//        }
//
//        //Collections.reverse(targetArray);
//        Log.d(SQLiteDatabaseHandler.class.getName(), " End of Merging............... targetArray size = " + targetArray.size());
//        return targetArray;
//
//    }



//    public HashMap<Integer, ArrayList<Rate>> getRatesByKm() {
//
//    }
//    public interface LocationAddedListener {
//        public void locationAdded (LocationAndMore loc);
//    }
}
