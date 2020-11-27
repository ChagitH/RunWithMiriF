package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.forst.miri.runwithme.interfaces.PracticeDownloadEndedCallback;
import com.forst.miri.runwithme.objects.GeneralAudio;
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

public class DownloadAudioTask extends AsyncTask<Void, Void, String> {

    private StorageReference mStorageRef;
    private String generalAudioName = "audio";
    private PracticeDownloadEndedCallback mCallback;
    private Context context;

    private boolean failureReported = false;
    public static final String AUDIO_DOWNLOADED_SUCCESSFULLY_STR = "audio downloaded successfully";
    public static final String AUDIO_DOWNLOAD_FAILED_STR = "audio download failed";




    public DownloadAudioTask(Context context, PracticeDownloadEndedCallback callback) {
        this.mCallback = callback;
        this.context = context;
    }



//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//    }

    @Override
    protected String doInBackground(Void... voids) {

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
                            if (task.isSuccessful()) {

                                Log.d(getClass().getName(),  "R.string.auth_ SUCCESS!!!");

                                download();
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
            download();
        }
        return null;
    }

    private void download() {
        Log.d(getClass().getName(), "download() 1 ");
        //////////
//                StorageReference mStorageRef;
//                mStorageRef = FirebaseStorage.getInstance().getReference();

        //String lessonLocation = String.valueOf(1) + "/";// + String.valueOf(0) + ".mp3";

        final String fileExtention = "zip";
        StorageReference lessonRef = mStorageRef.child(generalAudioName + "." + fileExtention);
//        file = File.createTempFile(fileName, null, context.getCacheDir());
//        try {

        Log.d(getClass().getName(), "download() 2 ");
        final File dir = new File(context.getFilesDir() + "/" + generalAudioName);
        dir.mkdirs(); //create folders where write files
        //final File file = new File(dir, String.valueOf(0) + ".mp3");

        final File localFile = new File(dir, generalAudioName + "." + fileExtention);

        Log.d(getClass().getName(), "download() 3 localfile = " + localFile.getPath());
        Log.d(getClass().getName(), "download() 3 lessonRef = " + lessonRef.getPath());
        lessonRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(getClass().getName(), "download() 4 ");
                // Local temp file has been created
                Log.d(getClass().getName(), "StorageReference.onSuccess() taskSnapshot.toString() = " + taskSnapshot.toString());
                unzipAndSave(localFile.getParent() + "/", localFile.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(getClass().getName(), "download() 5 ");
                // Handle any errors
                // I dont think i need to notify user- it will download some other time
                Log.d(getClass().getName(), "StorageReference.onFailure() exception = " + exception.getMessage());
                exception.printStackTrace();
                if(!failureReported) {
                    onPostExecute(AUDIO_DOWNLOAD_FAILED_STR);
                    failureReported = true;
                }
            }
        });

        Log.d(getClass().getName(), "download() 6 ");

    }


    private void unzipAndSave(String path, String zipname) {
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
                    try {
                        Float floatName = Float.parseFloat(nameNumOnly);
                        practiceAudio.put(new Float(floatName), path + filename);
                    } catch(NumberFormatException nfe){
                        nfe.printStackTrace();
//                        publishProgress(); // e.g. report Failure
                    }
                }

                if (ze.isDirectory()) {
                    Log.d(getClass().getName(), "WHAT DA YA KNOW? It's a Directory!");

                    continue; //just skip
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
            GeneralAudio.setDelaysAudioKeys(practiceAudio);
            GeneralAudio.saveLocally(context);

            onPostExecute(AUDIO_DOWNLOADED_SUCCESSFULLY_STR);

        } catch (IOException e) {
            e.printStackTrace();
            if(!failureReported) {
                onPostExecute(AUDIO_DOWNLOAD_FAILED_STR);
                failureReported = true;
            }
        }
    }

}

