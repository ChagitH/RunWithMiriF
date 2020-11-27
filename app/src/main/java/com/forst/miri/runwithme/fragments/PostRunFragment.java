package com.forst.miri.runwithme.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.widges.OutputDataBox;
import com.google.android.gms.maps.GoogleMap;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostRunFragment extends ParentFragment {


    public static final String FRAGMENT_TAG = PostRunFragment.class.getSimpleName();


    static final String CONNECTED_USER = "connected_user";
    //static final String PRACTICE_DATA = "practice_data_";
    public static int PICTURE_REQUEST_AFTER_RUN = 568;
    //public static int CROP_REQUEST = 569;
    private Uri outputFileUri_crop;
    private File sdImageFileInPicturesDirectory_crop;
    private Uri outputFileUri;
    private File sdImageFileInPicturesDirectory;


//    private boolean locationsStartedShrink = false;
    private int mPracticeNum, mTemperature;
    private OutputDataBox durationBox, lengthBox, rateBox, calorieBox;
    private PathFragment pathFragment;
    private PracticeData mPracticeData = null;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView tempTextView, dateTextView, timeTextView, bestRateTextView;
    private Practice mPractice = null;
    private CircleImageView userImage;
    private Button buttonClose;
    private View rootView = null;
    private SharedPreferences.OnSharedPreferenceChangeListener locationsSmoothedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(key != null && key.equals(PracticeData.LOCATIONS_READY_FOR_PRESENTATION_KEY)){
                boolean readyOrNot = prefs.getBoolean(key, false);
                if(readyOrNot) {
                    if (pathFragment != null && mPracticeData != null) {
                        pathFragment.setPracticeData(mPracticeData.getLocations());
                    }
                    prefs.unregisterOnSharedPreferenceChangeListener(locationsSmoothedListener);
                }
            }

        }
    };
//    private boolean locationReadyForPresentation = false;

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    public PostRunFragment() {
        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.post_run_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(savedInstanceState != null){
            //mPracticeData = savedInstanceState.getParcelable(PRACTICE_DATA);
            Object objP = savedInstanceState.getSerializable(Practice.PRACTICE_KEY);
            if (objP != null) {
                this.mPractice = (Practice) objP;
            }
            sdImageFileInPicturesDirectory =  (File) savedInstanceState.getSerializable("file");
            outputFileUri =  savedInstanceState.getParcelable("uri");
            outputFileUri_crop =  savedInstanceState.getParcelable("uri_crop");
//            locationsStartedShrink = savedInstanceState.getBoolean("locationsStartedShrink");
//            locationReadyForPresentation = savedInstanceState.getBoolean("locationReadyForPresentation");

        } else {
            Bundle bundle = getArguments();
            if(bundle != null) {
                //this.mPracticeData = bundle.getParcelable(PracticeData.PRACTICE_DATA_KEY);
                Object obj = bundle.getSerializable(Practice.PRACTICE_KEY);
                if (obj != null) {
                    this.mPractice = (Practice) obj;
                }
            } else {
                if(getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                }
            }



        }

        rootView = inflater.inflate(R.layout.post_run_fragment, container, false);

//        ImageView logo_send_log_iv = (ImageView) rootView.findViewById(R.id.post_run_iv_send_log);
//        logo_send_log_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Uri logUri = Logger.getLogUri(getContext());
//                if (logUri != null){
//                    try {
//
//                        Intent intent = new Intent();
//                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.run_with_email)});
//                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Log");
//                        intent.setAction(Intent.ACTION_SEND);
////                        intent.putExtra(Intent.EXTRA_STREAM, logUri);
//                        intent.setData(logUri);
//                        intent.setType("text/*");
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(Intent.createChooser(intent, getString(R.string.send_log_heb)));
//                    } catch (Exception ex){
//                        ex.printStackTrace();
//                    }
//                } else {
//                    Log.d(PostRunFragment.class.getName()," =================== logUri == null ===============================");
//                }
//            }
//        });


        TextView nameTextView = (TextView) rootView.findViewById(R.id.medal_tv_name);
        nameTextView.setText(ConnectedUser.getInstance().getFirstName());//4.3.2018 Miri requested to eliminate the !
        userImage = (CircleImageView) rootView.findViewById(R.id.medal_layout_circle_image);

        tempTextView = (TextView) rootView.findViewById(R.id.run_data_tv_temperature);
        dateTextView = (TextView) rootView.findViewById(R.id.run_data_tv_date);
        timeTextView = (TextView) rootView.findViewById(R.id.run_data_tv_time);
        bestRateTextView = (TextView) rootView.findViewById(R.id.run_data_tv_best_km);


        ImageView buttonTakePhoto = (ImageView) rootView.findViewById(R.id.hooray_dialog_iv_take_pic);
        ImageView buttonShare = (ImageView) rootView.findViewById(R.id.hooray_dialog_iv_share);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        buttonClose = (Button) rootView.findViewById(R.id.hooray_dialog_button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPractice != null){ //e.g. if has practice == she run with miri
                    Bundle b = new Bundle();
                    b.putSerializable(Practice.PRACTICE_KEY, mPractice);
                    ((MainActivity) getActivity()).replaceFragment(EndOfTrainingFragment.FRAGMENT_TAG , b, false);//R.layout.fragment_end_of_training, b);
                } else {
                    ((MainActivity) getActivity()).replaceFragment(MainFragment.FRAGMENT_TAG, null, false);//R.layout.fragment_main, null);
                }
            }
        });

        pathFragment = new PathFragment();

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.path_fragment_place_holder_2,pathFragment).commit();

        durationBox = (OutputDataBox) rootView.findViewById(R.id.hooray_dialog_output_box_time);
        lengthBox = (OutputDataBox) rootView.findViewById(R.id.hooray_dialog_output_box_length);
        rateBox = (OutputDataBox) rootView.findViewById(R.id.hooray_dialog_output_box_rate);
        calorieBox = (OutputDataBox) rootView.findViewById(R.id.hooray_dialog_output_box_kcl);


        return rootView;
    }

    private void share() {
        final View rootView  = getActivity().getWindow().getDecorView().getRootView();
        final int height = rootView.getHeight();
        final int[] mapPosition = new int[2];
        //final View logo = rootView.findViewById(R.id.run_data_layout_pink_logo);
        //final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)logo.getLayoutParams();
        //logo.setVisibility(View.INVISIBLE);
        final int[] mapWidthAndHeight = pathFragment.getMapFragmentSize();
        for(int i = 0 ; i < mapWidthAndHeight.length ; i++) {
            Log.d(getClass().getName(), " mapWidthAndHeight ============= " + i + " -> " +  mapWidthAndHeight[i]);
        }
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                Log.d(getClass().getName(), " onSnapshotReady !!! ");
                try {
                    final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
                    root.mkdirs();
                    final String fname = System.currentTimeMillis() + ".png";
                    final File sdImageFileInPicturesDirectory = new File(root, fname);

                    rootView.setDrawingCacheEnabled(true);
                    Bitmap backBitmap = rootView.getDrawingCache();
                    Bitmap bmOverlay = Bitmap.createBitmap(backBitmap.getWidth(), backBitmap.getHeight(),backBitmap.getConfig());

                    //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo_for_pic);
                    //Bitmap logoBitmap = Bitmap.createScaledBitmap(b, logo.getWidth(), logo.getHeight(), false);

                    Bitmap mapBitmap = Bitmap.createScaledBitmap(snapshot, mapWidthAndHeight[0], mapWidthAndHeight[1], false);
                    for(int i = 0 ; i < mapPosition.length ; i++) {
                        Log.d(getClass().getName(), " mapPosition ============= " + i + " -> " +  mapPosition[i]);
                    }
                    Canvas canvas = new Canvas(bmOverlay);
                    canvas.drawBitmap(backBitmap, new Matrix(), null);
                    //canvas.drawBitmap(mapBitmap, mapPosition[0]+5, mapPosition[1]+5, null);
                    canvas.drawBitmap(mapBitmap, mapPosition[0], mapPosition[1], null);
                    //canvas.drawBitmap(logoBitmap , logo.getLeft() , logo.getTop() , null);

                    FileOutputStream out = new FileOutputStream( sdImageFileInPicturesDirectory );
                    bmOverlay.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();

                    shareImage(sdImageFileInPicturesDirectory);
                } catch (Exception e) {
                    e.printStackTrace();
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.can_not_share_image) + '\n' + e.getMessage(), getString(R.string.sorry_heb)).show();
                }
            }
        };

        GoogleMap map = pathFragment.getMap();
        Log.d(getClass().getName(), " map ==  " + map.toString());
        if(map != null) {
            pathFragment.getMapPositionOnView(mapPosition);
            for(int i = 0 ; i < mapPosition.length ; i++) {
                Log.d(getClass().getName(), " mapPosition ============= " + i + " -> " +  mapPosition[i]);
            }
            map.snapshot(callback);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) throws OutOfMemoryError{
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void takePhoto() {
        long start = System.currentTimeMillis();
        final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "IMG.jpg";
        this.sdImageFileInPicturesDirectory = new File(root, fname);
        if(Build.VERSION.SDK_INT > M) {
            this.outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + getString(R.string.file_provider), sdImageFileInPicturesDirectory);
        } else {
            this.outputFileUri = Uri.fromFile(sdImageFileInPicturesDirectory);
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            // Continue only if the File was successfully created
            if (this.sdImageFileInPicturesDirectory != null) {

                //Uri photoURI = FileProvider.getUriForFile(this, "com.forst.miri.runwithme.fileprovider", photoFile);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.outputFileUri);
                Log.i("%%%%%%%%%%%%%%%%%%%", " PICTURE - TAKE PHOTO - BEFORE TAKING PHOTO  milis : " + String.valueOf(System.currentTimeMillis() - start) + " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
                this.startActivityForResult(takePictureIntent, PICTURE_REQUEST_AFTER_RUN);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        long start = System.currentTimeMillis();
        try {
            Log.i("%%%%%%%%%%%%%%%%%%%", "onActivityResult() !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
            ContentResolver cr = getActivity().getContentResolver();
            if(cr == null) return;

            if (requestCode == PICTURE_REQUEST_AFTER_RUN) {
                //int size = 0;

                Bitmap photo = null;
                if(resultCode == Activity.RESULT_OK) {

                    photo = android.provider.MediaStore.Images.Media.getBitmap(cr, this.outputFileUri);


                    ExifInterface ei = new ExifInterface(this.sdImageFileInPicturesDirectory.getAbsolutePath());
                    if (ei != null) {
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                            Bitmap mutablePhoto = null;
                            //Bitmap rotatedBitmap = null;
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    mutablePhoto = rotateImage(photo, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    mutablePhoto = rotateImage(photo, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    mutablePhoto = rotateImage(photo, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    mutablePhoto = photo;
                            }


                            Log.i("%%%%%%%%%%%%%%%%%%%", " PICTURE - AFTER ROTATING  milis : " + String.valueOf(System.currentTimeMillis() - start) + " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");

                            if (mutablePhoto == null) {
                                Dialogs.getAlertDialog(getActivity(), getString(R.string.camera_operation_faild_heb), getString(R.string.sorry_heb)).show();
                                return;
                            }

                            //size = mutablePhoto.getHeight() > mutablePhoto.getWidth() ? mutablePhoto.getWidth() : mutablePhoto.getHeight();
                            try {
                                FileOutputStream outputStream = new FileOutputStream(this.sdImageFileInPicturesDirectory);
                                mutablePhoto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.flush();
                                outputStream.close();

                                photo.recycle();
                                photo = null;

                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                Dialogs.getAlertDialog(getActivity(), getString(R.string.taking_picture_faild_heb), null, UIHelper.TRY_LATER_MESSAGE_FLAG, null).show();
                            }
                        }
                    }
                    Log.i("%%%%%%%%%%%%%%%%%%%", " PICTURE - AFTER COMPRESSING  milis : " + String.valueOf(System.currentTimeMillis() - start) + " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");

                }
                if (outputFileUri == null/* || photo == null */|| resultCode != Activity.RESULT_OK) { //result bad or bad outcome
                    Log.d(getFragmentName(), "RESULT_NOT_OK !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.taking_picture_faild_heb), null, UIHelper.TRY_LATER_MESSAGE_FLAG, null).show();
                } else {
                    cropImage();
                }





            } else if (requestCode == Crop.REQUEST_CROP){
                Bitmap cropedPhotoOrNot = null;
                if(resultCode == Activity.RESULT_OK) {

                    //final Uri resultUri = Crop.getOutput(data);

                    try {
                        cropedPhotoOrNot = MediaStore.Images.Media.getBitmap(cr, outputFileUri_crop);
                    } catch (IOException e) {
                        e.printStackTrace();
//                        Dialogs.getAlertDialog(getActivity(), getString(R.string.crop_operation_faild_heb) + "\n" + e.getMessage(), getString(R.string.sorry_heb)).show();
//                        return;
                    }catch (OutOfMemoryError oome) {
                        oome.printStackTrace();
//                        Dialogs.getAlertDialog(getActivity(), getString(R.string.crop_operation_faild_heb) + "\n" + oome.getMessage(), getString(R.string.sorry_heb)).show();
//                        return;
                    }

                }

                if(cropedPhotoOrNot == null) { //if cropping did not work, take an un-cropped pic
                    try {
                        cropedPhotoOrNot = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), this.outputFileUri);
                        outputFileUri_crop = outputFileUri;

                    }catch (Exception e){
                        e.printStackTrace();
                        Dialogs.getAlertDialog(getActivity(), getString(R.string.crop_operation_faild_heb) + "\n" + e.getMessage(), getString(R.string.sorry_heb)).show();
                        return;
                    }

//                catch (OutOfMemoryError oome) {
//                    oome.printStackTrace();
//                    Dialogs.getAlertDialog(getActivity(), getString(R.string.crop_operation_faild_heb), getString(R.string.sorry_heb)).show();
//                    return;
//                }
                }
                if(cropedPhotoOrNot != null) {
                    try {
                        Bitmap cropedPhoto = cropedPhotoOrNot.copy(Bitmap.Config.ARGB_8888, true);

                        //clear memory
                        cropedPhotoOrNot.recycle();
                        cropedPhotoOrNot = null;

                        addDataToPhoto(cropedPhoto);
                        shareImage(outputFileUri_crop);
                    } catch (Exception e){
                        e.printStackTrace();
                        Dialogs.getAlertDialog(getActivity(), getString(R.string.crop_operation_faild_heb) + "\n" + e.getMessage(), getString(R.string.sorry_heb)).show();
                        return;
                    }
                } else {
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.can_not_upload_pic_now_heb), getString(R.string.sorry_heb)).show();
                    return;
                }

                Log.i("%%%%%%%%%%%%%%%%%%%", " PICTURE - AFTER CROPPING  milis : " + String.valueOf(System.currentTimeMillis() - start) + " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
            }
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            Dialogs.getAlertDialog(getActivity(), getString(R.string.camera_operation_faild_heb) + '\n' + oome.getMessage(), getString(R.string.sorry_heb)).show();
        }catch (Exception e) {
            e.printStackTrace();
            Dialogs.getAlertDialog(getActivity(), getString(R.string.camera_operation_faild_heb) + "\n" + e.getMessage(), getString(R.string.sorry_heb)).show();
        }
    }

    private void cropImage(){
        Log.d("%%%%%%%%%%%%%%%%%%%", "Got to cropImage()!! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
        root.mkdirs();
        final String fname = getString(R.string.miri_brand_name_heb) + System.currentTimeMillis() + ".png";
        this.sdImageFileInPicturesDirectory_crop = new File(root, fname);
        if(Build.VERSION.SDK_INT > M) {
            this.outputFileUri_crop = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + getString(R.string.file_provider), sdImageFileInPicturesDirectory_crop);
        } else {
            this.outputFileUri_crop = Uri.fromFile(sdImageFileInPicturesDirectory_crop);
        }

        Crop.of(outputFileUri, outputFileUri_crop).asSquare().start(getActivity());

    }



    private void addDataToPhoto(Bitmap mutablePhoto) throws Exception{

        if (mutablePhoto == null ) return;
        int widthOfPhoto = mutablePhoto.getWidth();
        int heightOfPhoto = mutablePhoto.getHeight();

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;

        view = inflater.inflate(R.layout.data_for_picture, null);

        TextView tvDuration = (TextView) view.findViewById(R.id.data_for_picture_duration_textview);
        TextView tvLength = (TextView) view.findViewById(R.id.data_for_picture_length_textview);
        TextView tvRate = (TextView) view.findViewById(R.id.data_for_picture_rate_textview);

        ImageView ivLogo = (ImageView) view.findViewById(R.id.data_for_picture_logo_imageview);
        ImageView ivDuration = (ImageView) view.findViewById(R.id.data_for_picture_duration_imageview);
        TextView ivLength = (TextView) view.findViewById(R.id.data_for_picture_length_imageview);
        ImageView ivRate = (ImageView) view.findViewById(R.id.data_for_picture_rate_imageview);

        //int maxHeightOfView = (int)((float)heightOfPhoto/4.0);
        //int maxHeightOfView = heightOfPhoto;


        //set sizes of views inside parentView
        float textSize;// = maxHeightOfView / 10;
        int logoWidth;
        int imageSize;
        Log.d(getClass().getName(), "-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-");
        Log.d(getClass().getName(), "photo width = " + widthOfPhoto + " photo height = " + heightOfPhoto);
        Log.d(getClass().getName(), "-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-<>-");
        if (heightOfPhoto >= widthOfPhoto){
            textSize = widthOfPhoto / (float)55;
            logoWidth = widthOfPhoto/2;
            imageSize = widthOfPhoto / 15;
        } else {
            textSize = heightOfPhoto / (float)55;
            logoWidth = widthOfPhoto/3;
            imageSize = heightOfPhoto / 15;
        }

//        textSize *= densityFactor;

        ivLogo.getLayoutParams().width = logoWidth ;
        ivLogo.getLayoutParams().height = (int)(logoWidth/3.0);
//        int y = heightOfPhoto - 200;
//        ivLogo.setY(y);
        ivLogo.requestLayout();

        tvDuration.setTextSize(textSize);
        tvDuration.requestLayout();
        tvLength.setTextSize(textSize);
        tvLength.requestLayout();
        tvRate.setTextSize(textSize);
        tvRate.requestLayout();

        tvDuration.setText(String.valueOf(UIHelper.formatTime(mPracticeData.getDuration())));
        tvLength.setText(String.format("%1$.2f",mPracticeData.getDistanceInKm()));
        tvRate.setText(String.valueOf(UIHelper.formatTime(mPracticeData.getAvgRate())));

//        tvDuration.setText(String.valueOf(widthOfPhoto));
//        tvLength.setText(String.valueOf(textSize));
//        tvRate.setText(String.valueOf(heightOfPhoto));



        ivDuration.getLayoutParams().height = imageSize;
        ivDuration.getLayoutParams().width = imageSize;
        ivDuration.requestLayout();

        ivLength.setTextSize(textSize);
        ivLength.requestLayout();
//        ivLength.getLayoutParams().height = imageSize;
//        ivLength.getLayoutParams().width = imageSize;
//        ivLength.requestLayout();

        ivRate.getLayoutParams().height = imageSize;
        ivRate.getLayoutParams().width = imageSize;
        ivRate.requestLayout();

        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(widthOfPhoto, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(heightOfPhoto, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

//        FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
//        frameLayoutParams.setMargins(0,0, widthOfPhoto, heightOfPhoto);
//        view.setLayoutParams(frameLayoutParams);


        int heightOfView = view.getMeasuredHeight();
        int widthOfView = view.getMeasuredWidth();

//        int x = (widthOfPhoto/2) - (widthOfView/2);
//        int y = heightOfPhoto - (heightOfView + 30);
        //view.layout(x, y, view.getMeasuredWidth(), view.getMeasuredHeight());


        //Rect rect = new Rect(view.getLeft(), view .getTop(), view.getRight(), view.getBottom());
        //Rect rect = new Rect(x, y, widthOfView, heightOfView);
        Rect rect = new Rect(0, 0, widthOfView, heightOfView);
        view.buildDrawingCache(true);
        Canvas canvas = new Canvas(mutablePhoto);
        int s = canvas.save();
        canvas.translate(rect.left, rect.top);
        view.draw(canvas);
        canvas.restore();

//        try {
//            FileOutputStream outputStream = new FileOutputStream(this.sdImageFileInPicturesDirectory);
//            mutablePhoto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            outputStream.flush();
//            outputStream.close();
//            if(sdImageFileInPicturesDirectory_crop != null && sdImageFileInPicturesDirectory_crop.exists()){
//                sdImageFileInPicturesDirectory_crop.delete();
//            }
//        }catch (IOException ioe){
//            ioe.printStackTrace();
//        }

        try {
            FileOutputStream outputStream = new FileOutputStream(this.sdImageFileInPicturesDirectory_crop);
            mutablePhoto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            if(sdImageFileInPicturesDirectory != null && sdImageFileInPicturesDirectory.exists()){
                sdImageFileInPicturesDirectory.delete();
            }
            //clear memory
            mutablePhoto.recycle();
            mutablePhoto = null;

        }catch (IOException ioe){
            ioe.printStackTrace();
            throw ioe;
        }

    }



    private void shareImage(File imageFile) {
        Uri outputFileUri;
        if(Build.VERSION.SDK_INT > M) {
            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + getString(R.string.file_provider), imageFile);
        } else {
            outputFileUri = Uri.fromFile(imageFile);
        }
        shareImage(outputFileUri);

    }


    private void shareImage(Uri imageFileUri) {
        Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_SEND);
//        Uri outputFileUri;
//        if(Build.VERSION.SDK_INT > M) {
//            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + getString(R.string.file_provider), imageFile);
//        } else {
//            outputFileUri = Uri.fromFile(imageFile);
//        }
        intent.putExtra(Intent.EXTRA_STREAM, imageFileUri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, getString(R.string.share_heb)));
    }


    @Override
    public void onStart() {
        super.onStart();
        this.mPracticeData = PracticeData.readFromSharedPreferences(getContext(), null);
        if (this.mPracticeData != null) {
//            final Dialog waitD = Dialogs.showSpinningWheelDialog(getActivity());
//            waitD.show();

            if (this.mPracticeData != null) {
                setFragmentWithData();

            }

        }

        try {
            Bitmap image = ConnectedUser.getInstance().getImage();
            if (image != null) {
                userImage.setImageBitmap(image);
            } else {
                userImage.setVisibility(View.GONE);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }



    public void setFragmentWithData() {

        if(mPractice != null) { // part of practice
            buttonClose.setText(R.string.to_excercise_heb);
        } else {
            buttonClose.setText(R.string.close_heb);
        }
        if(mPracticeData == null) return;

        durationBox.setValueText(String.valueOf(UIHelper.formatTime(mPracticeData.getDuration())));
        lengthBox.setValueText(String.format("%1$.2f",mPracticeData.getDistanceInKm()));
        rateBox.setValueText(String.valueOf(UIHelper.formatTime(mPracticeData.getAvgRate())));
        calorieBox.setValueText(String.valueOf(mPracticeData.getKcl()));

        tempTextView.setText(mPracticeData.getTemperatureAsString());

        dateTextView.setText(UIHelper.formatDate(mPracticeData.getDate()));
        timeTextView.setText(UIHelper.formatDateToTimeOnly(mPracticeData.getDate()));
        bestRateTextView.setText(UIHelper.formatTime(mPracticeData.getBestRateForKmMiliies()));

        //8.4.2018 updating map only after shrinking. shrinking moved to thread here in order to avoid UI Freeze
        //if(mPracticeData.getLocations() != null && mPracticeData.getLocations().size() <= PracticeData.MAX_LOCATIONS_SIZE)
        if(isLocationReadyForPresentation()) {
            pathFragment.setPracticeData(mPracticeData.getLocations());
        }
    }

    private boolean isLocationReadyForPresentation() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PracticeData.LOCATIONS_READY_FOR_PRESENTATION_KEY, Context.MODE_PRIVATE);
        boolean isReady = prefs.getBoolean(PracticeData.LOCATIONS_READY_FOR_PRESENTATION_KEY, false);
        if( ! isReady){
            prefs.registerOnSharedPreferenceChangeListener(locationsSmoothedListener);
        }
        return isReady;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(CONNECTED_USER, ConnectedUser.getInstance());
        if(mPractice != null) savedInstanceState.putSerializable(Practice.PRACTICE_KEY, mPractice);
        //savedInstanceState.putParcelable(PRACTICE_DATA, mPracticeData);
        savedInstanceState.putSerializable("file", sdImageFileInPicturesDirectory);
        savedInstanceState.putParcelable("uri", outputFileUri);
        savedInstanceState.putParcelable("uri_crop", outputFileUri_crop);
//        savedInstanceState.putBoolean("locationsStartedShrink", locationsStartedShrink);
        super.onSaveInstanceState(savedInstanceState);
    }




    @Override
    public void storagePermissionGranted() {

    }

}
