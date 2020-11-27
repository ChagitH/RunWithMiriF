package com.forst.miri.runwithme.objects;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;

import java.io.File;
import java.io.FileOutputStream;

public class Logger {

    private static final String fileName = "locations_log.txt";
    private static Context msContext = null;


    public static void loadContext(Context context){
        msContext = context;
    }


    public static void log(String sMessage) {
        new BackgroundLogger(msContext, sMessage).start();
    }

    private static class BackgroundLogger extends Thread{

        private Context mContext = null;
        private String mMessage = null;

        BackgroundLogger (Context applicationContext, String message){
            this.mContext = applicationContext;
            this.mMessage = message;
        }

        @Override
        public void run() {
            if(mContext == null) return;

            try {

                FileOutputStream fops = mContext.openFileOutput(fileName, Context.MODE_APPEND);
                fops.write(mMessage.getBytes());
                fops.write('\n');
                fops.flush();
                fops.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Logger", e.toString());
            }
        }
    }

    public static Uri getLogUri(Context mContext){

        if(mContext == null) return null;


        File logFile = new File(mContext.getFilesDir(),fileName);
//        File logFile = new File("/data/data/com.forst.miri.runwithme/files/locations_log.txt");
//        if(!logFile.exists()){
//            return null;
//        } else {
            try{


                Uri logFileUri;

//                logFileUri = Uri.fromFile(logFile);

//                if(Build.VERSION.SDK_INT > M) {
                    logFileUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + mContext.getString(R.string.file_provider), logFile);
//                } else {
//                    logFileUri = Uri.fromFile(logFile);
//                }

                return logFileUri;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
//        }


    }

    public static void deleteLog(Context mContext) {
        new BackgroundDeleteLog(mContext).start();
    }

    private static class BackgroundDeleteLog extends Thread{

        private Context mContext = null;

        BackgroundDeleteLog (Context applicationContext){
            this.mContext = applicationContext;
        }

        @Override
        public void run() {
            if(mContext == null) return;


                try {

                    mContext.deleteFile(fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Logger", e.toString());
                }
//            }


        }
    }
}
