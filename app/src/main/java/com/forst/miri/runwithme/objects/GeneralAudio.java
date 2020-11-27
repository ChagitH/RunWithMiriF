package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class GeneralAudio {

    public static final String DELAYS_AUDIOS_KEY = "GENERALDelaysAndAudioUrls";
    private static Map<Float , String> mDelaysAndAudioUrls = null;


    public static Map<Float , String> getDelaysAndAudioUrls(Context context){
        if(mDelaysAndAudioUrls == null){
            setDelaysAudioUrlsFromSharedPreferences(context);
        }
        return mDelaysAndAudioUrls;
    }

    public static void setDelaysAudioKeys(Map<Float , String> delaysAndAudioUrls){
        mDelaysAndAudioUrls = delaysAndAudioUrls;
    }

    public static void saveLocally(Context context) {
        //first erase last saved
        SharedPreferences prefs = context.getSharedPreferences(GeneralAudio.class.getName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();


        if(mDelaysAndAudioUrls == null || mDelaysAndAudioUrls.isEmpty()) return;

        //write to sharedPreferences
        String jsonString = new Gson().toJson(mDelaysAndAudioUrls);
        editor.putString(DELAYS_AUDIOS_KEY, jsonString);
        editor.commit();
    }

//    public static boolean eraseLocally(Context context/*, int practiceNum*/){
//        SharedPreferences prefs = context.getSharedPreferences(GeneralAudio.class.getName(), MODE_PRIVATE);
//        String defValue = new Gson().toJson(new HashMap<Float, String>());
//        String json = prefs.getString(DELAYS_AUDIOS_KEY , defValue);
//        Type type = new TypeToken<HashMap<Float, String>>(){}.getType();
//        HashMap<Float, String> hash = new Gson().fromJson(json, type);
//        //boolean first = true;
//        if( hash != null && !hash.isEmpty()) {
//            Set<Float> keys = hash.keySet();
//            if(keys != null && keys.size() > 0){
//                try {
//                    File parentDir = null;
//                    Object[] keysArray = keys.toArray();
//                    String path = hash.get(keysArray[0]);
//                    File file = new File(path);
//                    parentDir = file.getParentFile();
//
//                    if(parentDir != null && parentDir.exists()){
//                        File[] children = parentDir.listFiles();
//                        for(File child : children){
//                            Log.e(Practice.class.getName(), " ------------- leftOver exists..... will be deleted " + child.getAbsolutePath());
//                            child.delete();
//                            Log.e(Practice.class.getName(), " ------------- leftOver was deleted..... does leftOver exist? " + child.exists());
//                        }
//                        Log.e(Practice.class.getName(), " ------------- parentDir exists..... will be deleted " + parentDir.getAbsolutePath());
//                        parentDir.delete();
//                        Log.e(Practice.class.getName(), " ------------- parentDir was deleted..... does parentDir exist? " + parentDir.exists());
//                    }
//                } catch (Exception e) {
//                    Log.e(Practice.class.getName(), "Cannot delete Audio file", e);
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if(prefs != null) {
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.clear();
//            editor.commit();
//        }
//        return true;
//    }


    private static void setDelaysAudioUrlsFromSharedPreferences(Context context){
        SharedPreferences prefs = context.getSharedPreferences(GeneralAudio.class.getName(), MODE_PRIVATE);
        if (prefs.contains(DELAYS_AUDIOS_KEY)) {
            String defValue = new Gson().toJson(new HashMap<Float, String>());
            String json = prefs.getString(DELAYS_AUDIOS_KEY , defValue);
            Type type = new TypeToken<HashMap<Float, String>>(){}.getType();
            HashMap<Float, String> hash = new Gson().fromJson(json, type);
            if( hash != null || !hash.isEmpty()) setDelaysAudioKeys(hash);
        }
    }


    public static boolean doesExist(Context context) {

        if(getDelaysAndAudioUrls(context) == null || (mDelaysAndAudioUrls != null && mDelaysAndAudioUrls.isEmpty())) {
            Log.d(GeneralAudio.class.getName(), " NOTHING IN DELAYS AND AUDIOS URLS");
            return false;
        } else {

            return true;
        }


//        SharedPreferences prefs = context.getSharedPreferences(GeneralAudio.class.getName(), MODE_PRIVATE);
//        if(prefs == null) {
//            Log.d(GeneralAudio.class.getName(), "prefs == null");
//            return false;
//        }
//        if(! prefs.contains(DELAYS_AUDIOS_KEY)){
//            Log.d(GeneralAudio.class.getName(), "! prefs.contains(DELAYS_AUDIOS_KEY)");
//            return false;
//        }
//        return true;
    }
}
