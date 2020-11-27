package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

public class PhotoHelper {

    File mFileInPicturesDirectory = null;
    Uri mUriOutputFile = null;
    Context mContext = null;

    public PhotoHelper(@NonNull Context context){
        mContext = context;
    }


//    public void takePhoto(String fileName) {
//        final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
//        root.mkdirs();
//        ///final String fname = System.currentTimeMillis() + "IMG.jpg";
//        this.mFileInPicturesDirectory = new File(root, fileName);
//        if(Build.VERSION.SDK_INT > M) {
//            this.mUriOutputFile = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + mContext.getString(R.string.file_provider), mFileInPicturesDirectory);
//        } else {
//            this.mUriOutputFile = Uri.fromFile(mFileInPicturesDirectory);
//        }
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
//            // Create the File where the photo should go
//            // Continue only if the File was successfully created
//            if (this.mFileInPicturesDirectory != null) {
//
//                //Uri photoURI = FileProvider.getUriForFile(this, "com.forst.miri.runwithme.fileprovider", photoFile);
//                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.mUriOutputFile);
//
//                mContext.startActivityForResult(takePictureIntent, PICTURE_REQUEST_AFTER_RUN);
//            }
//        }
//    }

}
