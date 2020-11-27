package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.forst.miri.runwithme.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Created by chagithazani on 8/27/17.
 */

public class UIHelper {

    public static int TRY_LATER_MESSAGE_FLAG = 1;
    public static int CALL_SERVICE_MESSAGE_FLAG = 2;

    // todo maybe to make a whole class of video presenter, that will have parameters, and fit them into the view accordingly
    public static View getVideoPresenterBySystemAbilities() {
        return null;
    }

    public static String getGreeting(Context context) {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 5 && timeOfDay < 11) {
            return context.getString(R.string.good_moring_heb);
        } else if (timeOfDay >= 11 && timeOfDay < 17) {
            return context.getString(R.string.good_noon_heb);
        } else if (timeOfDay >= 17 && timeOfDay < 21) {
            return context.getString(R.string.good_evening_heb);
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            return context.getString(R.string.good_night_heb);
        } else if (timeOfDay >= 24 && timeOfDay < 5) {
            return context.getString(R.string.good_night_heb);
        } else {
            return context.getString(R.string.hello_heb);
        }

    }

    public static String getErrorStringToPresentToUserFromJson(Context context, String jsonResponse) {
        if (jsonResponse == null) return "";
        StringBuilder errorString = new StringBuilder("");
        try {
            JSONObject jsonObj = new JSONObject(jsonResponse);
            Iterator<String> keys = jsonObj.keys();
            while (keys.hasNext()) {
                String message = jsonObj.getString(keys.next());
//                switch (message){
//                    case "[\"\"שדה דואר אלקטרוני כבר תפוס.\".\"]":
//                    //{"email":["שדה דואר אלקטרוני כבר תפוס."]}
//                        errorString.append(context.getString(R.string.email_has_been_taken_heb)).append(" 1 ");
//                        break;
//                    case "שדה דואר אלקטרוני כבר תפוס." :
//                    errorString.append(context.getString(R.string.email_has_been_taken_heb)).append(" 2 ");
//                    break;
//                }
                errorString.append(message).append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return jsonResponse;
        }
        return errorString.toString();
    }

    public static Typeface getDefaultFontRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Rubik-Regular.ttf");
    }


    public static String formatTimeFromMinutes(double timeInMinutes) {
        long millies = minutesToMillies(timeInMinutes);
        return formatTime(millies);

    }

    private static long minutesToMillies(double timeInMinutes) {
        double secs = timeInMinutes * 60;
        long millies = (long) (secs * 1000);
        return millies;
    }

    public static String formatTimeToMinutes(long timeInMillies){ // or in millies|??????

        int totalNumOfSecs = (int) timeInMillies / 1000;


        int secondsLeftAfterMinutes = totalNumOfSecs % 60 ;

        int minutes = (int) (totalNumOfSecs - secondsLeftAfterMinutes) / 60;



        long min =         TimeUnit.MILLISECONDS.toMinutes(timeInMillies);
        long sec =         TimeUnit.MILLISECONDS.toSeconds(timeInMillies) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillies));
//
//        Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ seconds: " + seconds + "§§§§§§§§§§§§§§§§§§");
//        Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ minutes: " + minutes + "§§§§§§§§§§§§§§§§§§");
//        Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ hours: " + hours + "§§§§§§§§§§§§§§§§§§");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02d:",min));
        sb.append(String.format("%02d",sec));

        return sb.toString();

//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%02d:", TimeUnit.MILLISECONDS.toMinutes(timeInMillies)));
//        sb.append(String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(timeInMillies) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillies))));
//        return sb.toString();

//        return  String.format("%02d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes(timeInMillies),
//                TimeUnit.MILLISECONDS.toSeconds(timeInMillies) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillies))
//        );

    }

    public static String formatTime(long timeInMillis){
//        return String.format("%02d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes(timeInSeconds),
//                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
//        );
        //Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ time in seconds: " + timeInMillies + "§§§§§§§§§§§§§§§§§§");
        int seconds = (int) (timeInMillis / 1000) % 60 ;
        int minutes = (int) ((timeInMillis / (1000*60)) % 60);
        int hours   = (int) ((timeInMillis / (1000*60*60)) % 24);

//        Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ seconds: " + seconds + "§§§§§§§§§§§§§§§§§§");
//        Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ minutes: " + minutes + "§§§§§§§§§§§§§§§§§§");
//        Log.i("§§§§§§§§§§§§§§§§§§", "§§§§§§§§§ hours: " + hours + "§§§§§§§§§§§§§§§§§§");
        StringBuilder sb = new StringBuilder();
        if (hours > 0)  sb.append(String.format("%02d:",hours));
        sb.append(String.format("%02d:",minutes));
        sb.append(String.format("%02d",seconds));

        return sb.toString();
    }

    public static String formatTimeAndDate(long timeInMillis){
        Date now = new Date(timeInMillis);
        return formatDateAndTime(now);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return  dateFormat.format(date);

    }

    public static String formatTimeAndDateWithMillies(long timeInMillis){
        Date now = new Date(timeInMillis);
        return formatDateAndTimeWithMillies(now);
    }

    public static String formatDateAndTimeWithMillies(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            return  dateFormat.format(date);
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }


    }

    public static String formatDateAndTime(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            return  dateFormat.format(date);
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }


    }
    /*
    returns Date from String in format "yyyy-MM-dd-HH-mm"
     */
    public static Date formatStringToDateAndTime(String dateStr) {
        Calendar cal = Calendar.getInstance();
        if(dateStr == null) return cal.getTime();
        try {
            StringTokenizer st = new StringTokenizer(dateStr, "-", false);
            if (st != null) {
                String yearS = null, monthS = null, dayS = null, hourS = null, minuteS = null;
                if (st.hasMoreTokens()) yearS = st.nextToken();
                if (st.hasMoreTokens()) monthS = st.nextToken();
                if (st.hasMoreTokens()) dayS = st.nextToken();
                if (st.hasMoreTokens()) hourS = st.nextToken();
                if (st.hasMoreTokens()) minuteS = st.nextToken();

                int year = 0, month = 0, day = 0, hour = 0, minute = 0;
                if (yearS != null) year = Integer.parseInt(yearS);
                if (monthS != null) month = Integer.parseInt(monthS);
                if (dayS != null) day = Integer.parseInt(dayS);
                if (hourS != null) hour = Integer.parseInt(hourS);
                if (minuteS != null) minute = Integer.parseInt(minuteS);


                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return cal.getTime();
        }
        return cal.getTime();

    }

    public static String formatDateForServerRequest(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return  dateFormat.format(date);

    }
    public static String formatDateToTimeOnly(Date date) {
        DateFormat dateformat = new SimpleDateFormat("HH:mm");
        return  dateformat.format(date);
    }

    public static int getDay(Date selectedDateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDateTime);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getDate(int day, int hour, int minute){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        // in case Date comes out in the past. Happens when the DAY_OF_WEEK that's selected is in the past (reletivl to the current week.
        if(cal.getTime().before(Calendar.getInstance().getTime())){
            cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR)+1);
        }
        return cal.getTime();
    }

    public static String getHebDay(Context context, int dayOfWeek) {
        switch (dayOfWeek){
            case 1:
                return context.getString(R.string.sunday_heb);
            case 2:
                return context.getString(R.string.monday_heb);
            case 3:
                return context.getString(R.string.tuesday_heb);
            case 4:
                return context.getString(R.string.wednesday_heb);
            case 5:
                return context.getString(R.string.thursday_heb);
            case 6:
                return context.getString(R.string.friday_heb);
            case 7:
                return context.getString(R.string.saturday_heb);
            default:
                return null;
        }
    }

}
