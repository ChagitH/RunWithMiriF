package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.forst.miri.runwithme.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by chagithazani on 8/9/17.
 */

public class Practice implements Serializable{

    //public static final String
//    public static final String PRACTICE_TYPE_KEY = "practicename";
    public static final String NUM_OF_PRACTICE_KEY = "numofpractice";
    public static final String DELAYS_AUDIOS_KEY = "DelaysAndAudioUrls";
    public static final String INTRODUCTION_KEY = "introduction";
    public static final String FIRST_POST_RUN_MOVIE_KEY = "firstPostRunMovie";
    public static final String SECOND_POST_RUN_MOVIE_KEY = "secondPostRunMovie";
    public static final String PRACTICE_KEY = "practice";
    public static final int TRIAL_PROGRAM_ID = 4;
    public static final int TRIAL_PRACTICE_NUM = 1;

    private String introductionUrl = "5HgEKyRHhno";
    private String postRunMovieNum1, postRunMovieNum2;
    private PracticeType mPracticeType;
    private Map<Float , String> mDelaysAndAudioUrls;
    private int mPracticeNum = -1;

    public int getPracticeNum() {
        return mPracticeNum;
    }




    public enum PracticeType{
        TIME, DISTANCE;
    }

    public static PracticeType getPracticeType(int practiceNum ){
        //if(practiceNum == 22 || practiceNum == 40 || practiceNum == 58 || practiceNum <= 18){
        if(practiceNum > 0 && practiceNum <= 18){
            return PracticeType.TIME;
        } else {
            return PracticeType.DISTANCE;
        }
    }

    public static String getProgressString(Context context, int practiceNum) {
    //for 10k only :(
        String[] sentences = context.getResources().getStringArray(R.array.place_in_plan_10);
        String sentence = context.getResources().getString(R.string.not_available_heb);
        if(sentences != null && sentences.length >= practiceNum){
            sentence = sentences[practiceNum];
        }
       return sentence;
    }


    public static String getPepString(Context context, int practiceNum) {
        //for 10k only :(
        String[] sentences = context.getResources().getStringArray(R.array.pep_in_plan_10);
        String sentence = "";
        if(sentences != null && sentences.length >= practiceNum){
            sentence = sentences[practiceNum-1];
        }
        return sentence;
    }

    public String getIntroductionUrl() {
        return introductionUrl;
    }

    public void setIntroductionUrl(String introductionUrl) {
        this.introductionUrl = introductionUrl;
    }

    public String getPostRunMovieNum1() {
        return postRunMovieNum1;
    }

    public void setPostRunMovieNum1(String after1MovieName) {
        this.postRunMovieNum1 = after1MovieName;
    }

    public String getPostRunMovieNum2() {
        return postRunMovieNum2;
    }

    public void setPostRunMovieNum2(String after2MovieName) {
        this.postRunMovieNum2 = after2MovieName;
    }
    public Practice(){    }

    public Practice(int practiceNum, Map<Float , String> delaysAndAudioUrls){
        setPracticeNum(practiceNum);
        this.mDelaysAndAudioUrls = delaysAndAudioUrls;
    }

    public PracticeType getPracticeType(){
        return this.mPracticeType;
    }

    public Map<Float , String> getDelaysAndAudioUrls(){
        return this.mDelaysAndAudioUrls;
    }

    public void setDelaysAndAudioUrls(Map<Float, String> mDelaysAndAudioUrls) {
        this.mDelaysAndAudioUrls = mDelaysAndAudioUrls;
    }
//
//    public void addAudioUrl(Float delay, String audioUrl){
//        if ( this.mDelaysAndAudioUrls == null ) this.mDelaysAndAudioUrls = new HashMap<Float, String>();
//        this.mDelaysAndAudioUrls.put(delay, audioUrl);
//    }

    public void setPracticeNum(int practiceNum) {
        this.mPracticeNum = practiceNum;
         mPracticeType =  getPracticeType(practiceNum);
        //set post movies
        setPostRunMovies(practiceNum);
    }

    private void setPostRunMovies(int practiceNum) {

        if (practiceNum >= 1 && practiceNum <= 22){
            setPostRunMovieNum1("fpAXmsNEBMg");

        } else if (practiceNum >= 23 && practiceNum <= 47){
            setPostRunMovieNum1("PNwdUjVr3cI");

        } else if (practiceNum >= 48 && practiceNum <= 70){
            setPostRunMovieNum1("Qq9T4A458V4");
        }

        int evenOrNot = practiceNum % 2;
        if (evenOrNot == 0 ){
            setPostRunMovieNum2("gaNh-CSnPfo");
        } else {
            setPostRunMovieNum2("LHe5BBRlrWQ");
        }
    }

    public void saveLocally(Context context) {
        //first erase last saved
        SharedPreferences prefs = context.getSharedPreferences(Practice.class.getName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        if(getDelaysAndAudioUrls() == null || getDelaysAndAudioUrls().isEmpty()
                || getIntroductionUrl() == null || getIntroductionUrl().isEmpty()
                || getPracticeNum() < 1
                || getPostRunMovieNum1() == null || getPostRunMovieNum1().isEmpty()
                || getPostRunMovieNum2() == null || getPostRunMovieNum2().isEmpty()) return;

        //write to sharedPreferences
        String jsonString = new Gson().toJson(getDelaysAndAudioUrls());
        editor.putString(DELAYS_AUDIOS_KEY, jsonString);
        editor.putString(INTRODUCTION_KEY, getIntroductionUrl());
        editor.putInt(NUM_OF_PRACTICE_KEY, getPracticeNum());
        editor.putString(FIRST_POST_RUN_MOVIE_KEY, getPostRunMovieNum1());
        editor.putString(SECOND_POST_RUN_MOVIE_KEY, getPostRunMovieNum2());
        editor.commit();
    }

    public static boolean eraseLocally(Context context/*, int practiceNum*/){
        Log.d(Practice.class.getName(), "... erasing Practice num - ");// + practiceNum);
        SharedPreferences prefs = context.getSharedPreferences(Practice.class.getName(), MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<Float, String>());
        String json = prefs.getString(DELAYS_AUDIOS_KEY , defValue);
        Type type = new TypeToken<HashMap<Float, String>>(){}.getType();
        HashMap<Float, String> hash = new Gson().fromJson(json, type);
        //boolean first = true;
        if( hash != null && !hash.isEmpty()) {
            Set<Float> keys = hash.keySet();
            if(keys != null && keys.size() > 0){
                    try {
                        File parentDir = null;
                        Object[] keysArray = keys.toArray();
                        String path = hash.get(keysArray[0]);
                        File file = new File(path);
                        parentDir = file.getParentFile();
//                        for(Float key : keys) { //not really needed
//                            String path = hash.get(key);
//                            Log.d(Practice.class.getName(), "erasing Practice Audio Files ..... B\"H!!! path = " + path);
//
//                            File file = new File(path);
//
//
//                            if (first) {
//                                first = false;
//                                parentDir = file.getParentFile();
//                            }
//                            Log.d(Practice.class.getName(), "erasing Practice Audio Files ..... B\"H!!! file = " + file.getAbsolutePath());
//
//                            if (file.exists()) {
//                                Log.e(Practice.class.getName(), " ------------- file exists..... will be deleted " + file.getAbsolutePath());
//                                file.delete();
//                                Log.e(Practice.class.getName(), " ------------- file was deleted..... does file exist? " + file.exists());
//                            } else {
//                                Log.e(Practice.class.getName(), "------------- file does not exit :) :) :) ");
//                            }
//                        }
                        if(parentDir != null && parentDir.exists()){
                            File[] children = parentDir.listFiles();
                            for(File child : children){
                                Log.e(Practice.class.getName(), " ------------- leftOver exists..... will be deleted " + child.getAbsolutePath());
                                child.delete();
                                Log.e(Practice.class.getName(), " ------------- leftOver was deleted..... does leftOver exist? " + child.exists());
                            }
                            Log.e(Practice.class.getName(), " ------------- parentDir exists..... will be deleted " + parentDir.getAbsolutePath());
                            parentDir.delete();
                            Log.e(Practice.class.getName(), " ------------- parentDir was deleted..... does parentDir exist? " + parentDir.exists());
                        }
                    } catch (Exception e) {
                        Log.e(Practice.class.getName(), "Cannot delete Audio file", e);
                        e.printStackTrace();
                    }
            }
        }

        if(prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();

            // erased 27.3.18. maybe will fix bug by Sarit that run is not saved as part of plan, and next practice is not downloaded
//        if (! prefs.contains(NUM_OF_PRACTICE_KEY)) {
//            return false;
//        }
//        int pn = prefs.getInt(NUM_OF_PRACTICE_KEY , 0);
//        if(pn != practiceNum){
//            return false;
//        }
            editor.clear();
            editor.commit();
            Log.d(Practice.class.getName(), "... .... ..... .. ... Practice erased! ");
        }
        return true;
    }


    public static Practice createFromSharedPreferences(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Practice.class.getName(), MODE_PRIVATE);
        if (! prefs.contains(NUM_OF_PRACTICE_KEY)) {
            return null;
        }
        Practice practice = new Practice();
        int pn = prefs.getInt(NUM_OF_PRACTICE_KEY , 0);
        if( pn < 1) return null;
        practice.setPracticeNum(pn);

        String defValue = new Gson().toJson(new HashMap<Float, String>());
        String json = prefs.getString(DELAYS_AUDIOS_KEY , defValue);
        Type type = new TypeToken<HashMap<Float, String>>(){}.getType();
        HashMap<Float, String> hash = new Gson().fromJson(json, type);
        if( hash == null || hash.isEmpty()) return null;
        practice.setDelaysAndAudioUrls(hash);

        String introUrl = prefs.getString(INTRODUCTION_KEY , "");
        if( introUrl == null || introUrl.isEmpty()) return null;
        practice.setIntroductionUrl(introUrl);

        String firstPostMovie = prefs.getString(FIRST_POST_RUN_MOVIE_KEY , "");
        if( firstPostMovie == null || firstPostMovie.isEmpty()) return null;
        practice.setPostRunMovieNum1(firstPostMovie);

        String secondPostMovie = prefs.getString(SECOND_POST_RUN_MOVIE_KEY , "");
        if( secondPostMovie == null || secondPostMovie.isEmpty()) return null;
        practice.setPostRunMovieNum2(secondPostMovie);

        return practice;
    }

    public static Practice createFromSharedPreferences(Context context, int practiceNum){
        if (! isNextLessonNum(context, practiceNum)) return null;
        return createFromSharedPreferences(context);
    }

    public static boolean isNextLessonNum(Context context, int nextLesson) {
        SharedPreferences prefs = context.getSharedPreferences(Practice.class.getName(), MODE_PRIVATE);
        if(prefs == null) {
            Log.d(Practice.class.getName(), "prefs == null");
            return false;
        }
        int pn = prefs.getInt(NUM_OF_PRACTICE_KEY , 0);
        if( pn != nextLesson){
            Log.d(Practice.class.getName(), "pn != practiceNum pn = " + pn + " nextLesson= " + nextLesson);
            return false;
        }
        return true;
    }

    public static Integer doesPracticeExist(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(Practice.class.getName(), MODE_PRIVATE);
            if (!prefs.contains(Practice.NUM_OF_PRACTICE_KEY) || prefs.getInt(Practice.NUM_OF_PRACTICE_KEY, 0) < 1) {
                return null;
            }
            String defValue = new Gson().toJson(new HashMap<Float, String>());
            String json = prefs.getString(DELAYS_AUDIOS_KEY, defValue);
            Type type = new TypeToken<HashMap<Float, String>>() { }.getType();
            HashMap<Float, String> hash = new Gson().fromJson(json, type);
            if (hash != null && !hash.isEmpty()) {
                Set<Float> keys = hash.keySet();
                if (keys != null && keys.size() > 0) {
                    Object[] keysArray = keys.toArray();
                    String path = hash.get(keysArray[0]);
                    File file = new File(path);
                    File parentDir = file.getParentFile();
                    if(parentDir != null && parentDir.exists()){
                        File[] children = parentDir.listFiles();
                        if(children != null && children.length > 1){
                            int numOfTraining = prefs.getInt(Practice.NUM_OF_PRACTICE_KEY, 0);
                            return new Integer(numOfTraining);
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }



//    public static boolean doesPracticeExist(Context context, int practiceNum) {
//        SharedPreferences prefs = context.getSharedPreferences(Practice.class.getName(), MODE_PRIVATE);
//        if(prefs == null) {
//            Log.d("isNextLessonNum", "prefs == null");
//            return false;
//        }
//        int pn = prefs.getInt(NUM_OF_PRACTICE_KEY , 0);
//        if( pn != practiceNum){
//            Log.d(Practice.class.getName(), "pn != practiceNum pn = " + pn + " nextLesson= " + practiceNum);
//            return false;
//        }
//        return true;
//    }
}
