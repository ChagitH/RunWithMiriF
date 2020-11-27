package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.forst.miri.runwithme.interfaces.PracticeDownloadEndedCallback;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by chagithazani on 10/26/17.
 */

public class DownloadPracticeTask extends AsyncTask<Integer, String, String> {

    private static final int PRACTICE_DOWNLOADED_SUCCESSFULLY = 9855;
    public static final String PRACTICE_DOWNLOADED_SUCCESSFULLY_STR = "practice downloaded successfully";
    private StorageReference mStorageRef;

    private PracticeDownloadEndedCallback mCallback;
    private Context context;
    private boolean failureReported = false;

    private Integer lessonNum = null;
    private Integer planId = null;

    public DownloadPracticeTask(Context context, PracticeDownloadEndedCallback callback) {
        this.mCallback = callback;
        this.context = context;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(getClass().getName(), "§§§§§§§§§§§§§§§§§§§§§§§§§§§§§ §§§§§§§§§§§§§§§§§§§§§§§§§§ §§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§ onProgressUpdate" );
        if(values != null && values.length >0 && values[0] != null && values[0].matches(PRACTICE_DOWNLOADED_SUCCESSFULLY_STR)){
            onPostExecute(PRACTICE_DOWNLOADED_SUCCESSFULLY_STR);
        }
        if( ! failureReported) {
            if (mCallback != null) mCallback.practiceDownloadFailed();
            failureReported = true;
        }
    }

//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//    }

    @Override
    protected String doInBackground(Integer... params) {

        try {
            lessonNum = params[0];
            planId = params[1];
        } catch (ArrayIndexOutOfBoundsException aobe){
            aobe.printStackTrace();
            return "";
        }

        if(lessonNum == null || planId == null){
            return "";
        }

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage fs = FirebaseStorage.getInstance();
        mStorageRef = fs.getReference();

        if(mAuth.getCurrentUser() == null) {
            Log.d(getClass().getName(), "mAuth.getCurrentUser() == null");
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(getClass().getName(), "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.d(getClass().getName(), "R.string.auth_failed");
                                publishProgress(task.toString()); // e.g. report Failure
                            } else {
                                Log.d(getClass().getName(),  "R.string.auth_ SUCCESS!!!");
                                download(String.valueOf(lessonNum.intValue()), String.valueOf(planId.intValue()));
                            }
                        }
                    });

//            mAuth.signInWithEmailAndPassword("run.with.miri@gmail.com", "runwithmiri3309773")
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d("$$$$$$$$$$$$$$$$$$$$$", "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                            // If sign in fails, display a message to the user. If sign in succeeds
//                            // the auth state listener will be notified and logic to handle the
//                            // signed in user can be handled in the listener.
//                            if (!task.isSuccessful()) {
//                                Log.d("$$$$$$$$$$$$$$$$$$$$$", "R.string.auth_failed");
//                                publishProgress(task.toString()); // e.g. report Failure
//                            } else {
//                                Log.d("$$$$$$$$$$$$$$$$$$$$$",  "R.string.auth_ SUCCESS!!!");
//                                download(String.valueOf(lessonNum.intValue()), String.valueOf(planId.intValue()));
//                            }
//                        }
//                    });
        } else {
            Log.d(getClass().getName(), "mAuth.getCurrentUser() != null");
            download(String.valueOf(lessonNum.intValue()) , String.valueOf(planId.intValue()));
        }
        return null;
    }

    private void download(final String lessonNum, final String planId) {
        Log.d(getClass().getName(), "download() 1 ");
        //////////
//                StorageReference mStorageRef;
//                mStorageRef = FirebaseStorage.getInstance().getReference();

        //String lessonLocation = String.valueOf(1) + "/";// + String.valueOf(0) + ".mp3";
        String lessonLocation = String.valueOf(planId) + "/";
        String fileName = lessonNum;
        final String fileExtention = "zip";
        StorageReference lessonRef = mStorageRef.child(lessonLocation + fileName + "." + fileExtention);
//        file = File.createTempFile(fileName, null, context.getCacheDir());
//        try {

        Log.d(getClass().getName(), "download() 2 ");
        final File dir = new File(context.getFilesDir() + "/" + lessonLocation);
        dir.mkdirs(); //create folders where write files
        //final File file = new File(dir, String.valueOf(0) + ".mp3");

        final File localFile = new File(dir, fileName + "." + fileExtention);

        Log.d(getClass().getName(), "download() 3 localfile = " + localFile.getPath());
        Log.d(getClass().getName(), "download() 3 lessonRef = " + lessonRef.getPath());
        lessonRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(getClass().getName(), "download() 4 ");
                // Local temp file has been created
                Log.d(getClass().getName(), "StorageReference.onSuccess() taskSnapshot.toString() = " + taskSnapshot.toString());
                unzipAndSave(localFile.getParent() + "/", localFile.getName(), lessonNum);
                publishProgress(PRACTICE_DOWNLOADED_SUCCESSFULLY_STR); // e.g. report success
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(getClass().getName(), "download() 5 ");
                // Handle any errors
                // I dont think i need to notify user- it will download some other time
                Log.d(getClass().getName(), "StorageReference.onFailure() exception = " + exception.getMessage());
                exception.printStackTrace();
                publishProgress(exception.getMessage()); // e.g. report Failure
            }
        });

        Log.d(getClass().getName(), "download() 6 ");

    }


    private int unzipAndSave(String path, String zipname, String lessonNum) {
        Log.d(getClass().getName(), "unzipAndPlay() 1  path = " + path + " zipname = " + zipname);
        InputStream is;
        ZipInputStream zis;
        Map<Float, String> practiceAudio = new HashMap<Float, String>();
        try {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            Log.d(getClass().getName(), "unzipAndPlay() 2 ");
            ZipEntry ze;
            byte[] buffer = new byte[1024 * 1024]; // 10M is much more than the biggest file
            int count;
            String introductionUrl = null;
            while ((ze = zis.getNextEntry()) != null) {
                Log.d(getClass().getName(), "unzipAndPlay()  3");
                filename = ze.getName();
                Log.d(getClass().getName(), "unzipAndPlay() 3  filename = " + filename);
                if (filename.endsWith(".mp3")) {
                    String nameNumOnly = filename.substring(0, filename.length() - 4);
                    if (nameNumOnly.equals(Practice.INTRODUCTION_KEY)) {
                        introductionUrl = path + filename;
                    } else {
                        try {
                            Float floatName = Float.parseFloat(nameNumOnly);
                            practiceAudio.put(new Float(floatName), path + filename);
                        } catch(NumberFormatException nfe){
                            nfe.printStackTrace();
                            publishProgress(); // e.g. report Failure
                        }
                    }
                }
                //todo: is this neccasery?
                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    Log.d(getClass().getName(), "WHAT DA YA KNOW? It's a Directory!");
//                            File fmd = new File(path + filename);
//                            fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);
                Log.d(getClass().getName(), "unzipAndPlay() 3  fout = " + fout.toString());
                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    //Log.i("±±±±±±±±±±±±±±±±±±±", "unzipAndPlay()  count = " + count);
                    fout.write(buffer, 0, count);
                }

                fout.close();

                zis.closeEntry();
                Log.d(getClass().getName(), "unzipAndPlay()  " + 4);
            }

            zis.close();

            Log.d(getClass().getName(), "unzipAndPlay()  " + 5);


            //save practice to local db
            Practice practice = new Practice(Integer.valueOf(lessonNum), practiceAudio);
            practice.setIntroductionUrl(introductionUrl);
            practice.saveLocally(context);
            if(ConnectedUser.getInstance() != null) ConnectedUser.getInstance().setPracticeNum(context, practice.getPracticeNum());
            if (mCallback != null) mCallback.practiceDownloadEnded(practice);
            Log.d(getClass().getName(), "unzipAndPlay()  6 practice created");

            return PRACTICE_DOWNLOADED_SUCCESSFULLY;

        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(e.getMessage()); // e.g. report Failure
        }
        return -1;
    }

}
