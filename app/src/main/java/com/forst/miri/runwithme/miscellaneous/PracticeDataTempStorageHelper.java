package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.util.Log;

import com.forst.miri.runwithme.objects.PracticeData;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by chagithazani on 3/21/18.
 */

public class PracticeDataTempStorageHelper {
    private static String PRACTICE_DATA_KEY_SPECIAL = "p_data_key_";
    private static String NOT_SAVED_PRACTICE_DATA_KEY = PRACTICE_DATA_KEY_SPECIAL+"not_saved";
    private static String NO_ELEVATION_PRACTICE_DATA_KEY = PRACTICE_DATA_KEY_SPECIAL+"no_elevation";

    private static String NEW_PRACTICE_SAVED = "new_practice_saved";
    private static String VALUE = "val";

    private static PracticeData getPracticeData(Context context, String key){
        if(context == null || key == null) return null;
        return PracticeData.readFromSharedPreferences(context, key);
    }





//    public static void saveNoElevationPracticeData(final Context context, final PracticeData practiceData){
//        Log.d(PracticeDataTempStorageHelper.class.getName(), " --- *** --- ***  --- *** --- *** ---  saveNoElevationPracticeData()");
//
//        if(context == null || practiceData == null) return;
//
////        String keyToSave = NO_ELEVATION_PRACTICE_DATA_KEY + (String.valueOf(practiceData.getTimeStamp()));
////
////        practiceData.writeToSharedPreferences(context, keyToSave, false);
//
//        final SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
//
//        sqLiteDatabaseHandler.addPractice(practiceData, new DatabaseUpdatedCallback() {
//            @Override
//            public void updateFailedWithException(Exception e) {
//                Log.d(PracticeData.class.getName(), " PracticeData NOt saved to full practices table ");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void updateEndedWithNoException() {
//                Log.d(PracticeData.class.getName(), " PracticeData YES saved to full practices table ");
//
//                //PracticeDataTempStorageHelper.deleteNoElevationPracticeData(context, practiceData);
//            }
//        });
//
//    }

    public static void saveNotSavedToBackendPracticeData(Context context, PracticeData practiceData){//, String email){//}, boolean update){

        final SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);

        sqLiteDatabaseHandler.addPractice(practiceData, null);


//        Log.d(PracticeDataTempStorageHelper.class.getName(), " --- *** --- ***  --- *** --- *** ---  saveNotSavedToBackendPracticeData()");
//        if(context == null || practiceData == null || email == null) return;
//
//        //String keyToSave = NOT_SAVED_PRACTICE_DATA_KEY + (String.valueOf(practiceData.getTimeStamp())) + "_" + email;
//        String keyToSave = getNotSavedPracticeDataKey(practiceData, email);
//
//        //if(!update) {
//            practiceData.writeToSharedPreferences(context, keyToSave, false);
////        } else{
////            practiceData.updateLocationsAndRatesToSharedPreferences(context, keyToSave);
////        }
//
////        writeNewPracticeSaved(context);
    }

//    private static void writeNewPracticeSaved(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(NEW_PRACTICE_SAVED, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean(VALUE, true);
//
//        editor.apply();
//
//    }
//
//    private static boolean isNewPracticeSaved(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(NEW_PRACTICE_SAVED, Context.MODE_PRIVATE);
//        return prefs.getBoolean(VALUE, true);
//    }
//
//    private static void eraseNewPracticeSaved(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(NEW_PRACTICE_SAVED, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean(VALUE, false);
//
//        editor.apply();
//
//    }

    public static void deleteNotSavedPracticeData(Context context, PracticeData practiceData, String email){
        Log.d(PracticeDataTempStorageHelper.class.getName(), " --- *** --- ***  --- *** --- *** ---  deleteNotSavedPracticeData()");
        //try { Thread.sleep(1000); } catch (InterruptedException e) {}
        if(context == null || practiceData == null) return;
        //String keyToDelete = NOT_SAVED_PRACTICE_DATA_KEY + (String.valueOf(practiceData.getTimeStamp())) + "_" + email;
        String keyToDelete = getNotSavedPracticeDataKey(practiceData, email).concat(".xml");

        deleteFile(context, keyToDelete);

    }

    public static String getNotSavedPracticeDataKey(PracticeData practiceData, String email){
        return NOT_SAVED_PRACTICE_DATA_KEY + (String.valueOf(practiceData.getTimeStamp())) + "_" + email;
    }

    private static void deleteFile(Context context, String keyToDelete){
        Log.e(PracticeDataTempStorageHelper.class.getName(), " ....................................... deleteFile() ..... "+ keyToDelete);

        //Log.e(PracticeDataTempStorageHelper.class.getName(), " ....................................... deleteFile() ..... Under Build.VERSION_CODES.N");
        try {


            File file = new File(context.getFilesDir().getParent() + "/shared_prefs/"+keyToDelete);//+".xml");
            Log.e(PracticeDataTempStorageHelper.class.getName(), " .....................................................................file to be deleted " + file.getAbsolutePath());

            if(file.exists()){
                Log.e(PracticeDataTempStorageHelper.class.getName(), " ------------- file exists..... will be deleted "+ file.getAbsolutePath());
                file.delete();
                Log.e(PracticeDataTempStorageHelper.class.getName(), " ------------- file deleted..... does it still exist? "+ file.exists());
            } else {
                Log.e(PracticeDataTempStorageHelper.class.getName(), "------------- file did not exist :) :) :) ");
            }

        } catch (Exception e) {
            Log.e(PracticeDataTempStorageHelper.class.getName(), "Cannot delete files in shared pref directory", e);
            e.printStackTrace();
        }
    }



//    public static void deleteNoElevationPracticeData(Context context, PracticeData practiceData){
//        Log.d(PracticeDataTempStorageHelper.class.getName(), " --- *** --- ***  --- *** --- *** ---  deleteNoElevationPracticeData()");
//        if(context == null || practiceData == null) return;
//        String keyToDelete = NO_ELEVATION_PRACTICE_DATA_KEY + (String.valueOf(practiceData.getTimeStamp())) + ".xml";
//
//        deleteFile(context, keyToDelete);
//    }

    private static File[] getAllSharedPrefsFiles(Context context){
        try {
            File dir1 = new File(context.getFilesDir().getParent() + "/shared_prefs/");

            if (dir1.isDirectory()) {


                Log.d(PracticeDataTempStorageHelper.class.getName(), "it is a DIR ^^^^^^^^^^^^^^^^^^^ getAllSharedPrefsFiles () ");
                //String[] fileNames = dir1.list();

                File[] files = dir1.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith(PRACTICE_DATA_KEY_SPECIAL);
                    }
                });

                //todo print debug ---- print debug ---- print debug ---- print debug ---- print debug ---- print debug ---- print debug ----
                Log.d(PracticeDataTempStorageHelper.class.getName(), "--------- fileNames Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    Log.d(PracticeDataTempStorageHelper.class.getName(), "FileName:" + files[i].getName());
                }

                Log.d(PracticeDataTempStorageHelper.class.getName(), "FILE PRINT OVER  ^^^^^^^^^^^^^^^^^^^ getAllSharedPrefsFiles () ");

                //todo print debug ---- print debug ---- print debug ---- print debug ---- print debug ---- print debug ---- print debug ----


                return files;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

//    public static ArrayList<PracticeData> getNotSavedPracticeDataList(Context context, String email){
//
//       Log.d(PracticeDataTempStorageHelper.class.getName(), " --- *** --- ***  --- *** --- *** ---  getNotSavedPracticeDataList()");
//
//        if(context == null) return null;
//
////        String[] fileNames = getAllSharedPrefsFiles(context);
//        File[] files = getAllSharedPrefsFiles(context);
//        ArrayList<String> spNames = new ArrayList<>();
//        ArrayList<PracticeData> practiceDataArray = new ArrayList<>();
//
//        if(files == null) return practiceDataArray;
//
//        for(int i = 0 ; i < files.length ; i++){
//            String fName = files[i].getName();
//            if(fName.contains(NOT_SAVED_PRACTICE_DATA_KEY) && fName.contains(email)){
//                spNames.add(fName.replace(".xml", ""));
//            }
//        }
//
//        //??? Collections.sort(spNames);
//
//        for(String key : spNames){
//            PracticeData pd = getPracticeData(context, key);
//            if(pd != null) practiceDataArray.add(pd);
//        }
//        return practiceDataArray;
//    }

//    public static ArrayList<PracticeData> getNoElevationPracticeDataList(Context context){
//        Log.d(PracticeDataTempStorageHelper.class.getName(), " --- *** --- ***  --- *** --- *** ---  getNoElevationPracticeDataList()");
//        if(context == null ) return null;
//
//        //String[] fileNames = getAllSharedPrefsFiles(context);
//        File[] files = getAllSharedPrefsFiles(context);
//        ArrayList<String> spNames = new ArrayList<>();
//        ArrayList<PracticeData> practiceDataArray = new ArrayList<>();
//
//        if(files == null) return practiceDataArray;
//
//        for(int i = 0 ; i < files.length ; i++){
//            String fName = files[i].getName();
//            if(fName.contains(NO_ELEVATION_PRACTICE_DATA_KEY)){
//                spNames.add(fName.replace(".xml", ""));
//            }
//        }
//
//        Collections.sort(spNames);
//        Collections.reverse(spNames);
//
//        for(String key : spNames){
//            PracticeData pd = getPracticeData(context, key);
//            if(pd != null) practiceDataArray.add(practiceDataArray.size(), pd);
//        }
//        return practiceDataArray;
//    }

//    public static void eraseAllNoElevationPracticeData(Context context) {
//        if(context == null) return;
//        //String[] noEleFilePathes = getAllSharedPrefsFiles(context);
//        File[] noEleFileFiles = getAllSharedPrefsFiles(context);
//        for(File file : noEleFileFiles){
//            String name = file.getName();
//            if(name.contains(NO_ELEVATION_PRACTICE_DATA_KEY)){
//                deleteFile(context, name);
//            }
//        }
//
////        SharedPreferences prefs = context.getSharedPreferences(NO_ELEVATION_KEYs, Context.MODE_PRIVATE);
////        Set<String> stringSet = prefs.getStringSet(STRING_SET, null);
////        ArraySet<String> keysArray = new ArraySet<>();
////        if(stringSet != null) keysArray.addAll(stringSet);
////
////        for ( String key : keysArray){
////            SharedPreferences prefsPD = context.getSharedPreferences(key, Context.MODE_PRIVATE);
////            SharedPreferences.Editor editor = prefsPD.edit();
////            editor.clear();
////            editor.apply();
////            //context.deleteSharedPreferences(key); API 24
////        }
////        SharedPreferences.Editor editor = prefs.edit();
////        editor.clear();
////        editor.apply();
//    }



//    private static String modifyEmail(String userEmail){
////        if (userEmail == null) return null;
////        String newEmail = userEmail.replaceAll("[^A-Za-z0-9.]","_");
////        //Log.d(getClass().getName(), " -------> Replaced " + userEmail + " To " + newEmail);
////        return newEmail;
//        return userEmail;
//    }
}
