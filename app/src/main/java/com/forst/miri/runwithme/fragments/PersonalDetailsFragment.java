package com.forst.miri.runwithme.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.LoginActivity;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.activities.RegistrationActivity;
import com.forst.miri.runwithme.application.RunWithMiriApplication;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.miscellaneous.NumberPickerDialog;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.User;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalDetailsFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = PersonalDetailsFragment.class.getSimpleName();
    public static String PICTURE_UPDATED_KEY = "pic_updated_key";
    private static int NAME_FLAG = 901;
    private static int PHONE_FLAG = 902;
    private TextView tvWeight, tvYear, tvHeight, mTvUploadImage;
    private ImageView mIvCameraIcon;
    private EditText mEditPrivateName, mEditLastName, mEditPhone, mEditEmail;//, mEditID;
    private Button bResetPassword, bSave;
    private String mFirstName, mLastName, mPhone;
    public static final int UPDATE_DETAILS_PICTURE_REQUEST = 938;
    private CircleImageView mImageCircle;
    private Uri outputFileUri;
    private Bitmap mImage = null;
    private int mWeight, mHeight, mYearOfBirth;
    Dialog myDialog = null;
    private View vFirstName , vLastName, vPhone;
    private User mUser = null;
    private boolean mWasPictureUpdated = false;

    public PersonalDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,   Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        if ( savedInstanceState != null){
            mWeight = savedInstanceState.getInt(User.WEIGHT_KEY);
            mHeight = savedInstanceState.getInt(User.HEIGHT_KEY);
            mYearOfBirth = savedInstanceState.getInt(User.YEAR_OF_BIRTH_KEY);
            mFirstName = savedInstanceState.getString(User.FIRST_NAME_KEY);
            mLastName = savedInstanceState.getString(User.LAST_NAME_KEY);
            mPhone = savedInstanceState.getString(User.CELLULAR_KEY);
            //outputFileUri = savedInstanceState.getParcelable(User.PHOTO_LOCAL_KEY);
            mImage = savedInstanceState.getParcelable(User.PHOTO_LOCAL_KEY);
            mWasPictureUpdated = savedInstanceState.getBoolean(PICTURE_UPDATED_KEY);
        }

        View parentView = inflater.inflate(R.layout.fragment_personal_details, container, false);
        mImageCircle = (CircleImageView) parentView.findViewById(R.id.update_details_circle_image);
        mImageCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

//        mTvUploadImage = (TextView) parentView.findViewById(R.id.update_details_tv_upload_image);
//        vFirstName = parentView.findViewById(R.id.update_details_v_first_name);
//        vLastName = parentView.findViewById(R.id.update_details_v_family_name);
//        vPhone = parentView.findViewById(R.id.update_details_v_phone);

        mTvUploadImage = (TextView) parentView.findViewById(R.id.update_details_tv_upload_image);
        mIvCameraIcon = (ImageView) parentView.findViewById(R.id.update_details_im_camera_icon_upload_image);

        tvWeight = (TextView) parentView.findViewById(R.id.update_details_tv_kg);
        tvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog npDialog = new NumberPickerDialog(getActivity(), getString(R.string.weight_in_kg_hebrew), 35, 160, 70, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        mWeight = selectedValue;
//                        tvWeight.setText(String.valueOf(selectedValue) + " " + getString(R.string.kg_heb));
                        setWeightText(selectedValue);
                    }
                });
            }
        });

        tvYear = (TextView) parentView.findViewById(R.id.update_details_tv_year_of_birth);
        tvYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog npDialog = new NumberPickerDialog(getActivity(), getString(R.string.year_of_birth_heb), 1930, 2010, 1980, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        mYearOfBirth = selectedValue;
//                        tvYear.setText(String.valueOf(selectedValue));
                        setYearText(selectedValue);
                    }
                });
            }
        });

        tvHeight = (TextView) parentView.findViewById(R.id.update_details_tv_hight);
        tvHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog npDialog = new NumberPickerDialog(getActivity(), getString(R.string.height_in_cm_heb), 150, 215, 165, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        mHeight = selectedValue;
//                        tvHeight.setText(String.valueOf(selectedValue) + " " + getString(R.string.cm_heb));
                        setHeightText(selectedValue);
                    }
                });
            }
        });

        mEditEmail = (EditText) parentView.findViewById(R.id.update_details_et_email);
//        mEditID = (EditText) parentView.findViewById(R.id.update_details_et_id_num);
        mEditPrivateName = (EditText) parentView.findViewById(R.id.update_details_et_first_name);

        mEditLastName = (EditText) parentView.findViewById(R.id.update_details_et_last_name);

        mEditPhone = (EditText) parentView.findViewById(R.id.update_details_et_phone_num);



        bResetPassword = (Button) parentView.findViewById(R.id.update_details_button_reset_password);
        bResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        bSave = (Button) parentView.findViewById(R.id.update_details_button_save);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveDetails();
            }
        });

        setUpFragment(savedInstanceState == null);


        return parentView;
    }

    private void setHeightText(int selectedValue) {
        tvHeight.setText(String.valueOf(selectedValue) + " " + getString(R.string.cm_heb));
    }
    private void setWeightText(int selectedValue) {
        tvWeight.setText(String.valueOf(selectedValue) + " " + getString(R.string.kg_heb));
    }
    private void setYearText(int selectedValue) {
        tvYear.setText(String.valueOf(selectedValue));
    }



    private void setUpFragment(boolean fromUser){

        mUser = User.createUserFromSharedPreferences(getContext(), false);
        if(mUser == null) mUser = ConnectedUser.getInstance();

        bResetPassword.setEnabled(mUser != null);
        bSave.setEnabled(mUser != null);

        if( mUser == null ) return;

        if(fromUser) {
            mYearOfBirth = mUser.getYearOfBirth();
            mWeight = mUser.getWeight();
            mHeight = mUser.getHeight();
            mFirstName = mUser.getFirstName();
            mLastName = mUser.getLastName();
            mPhone = mUser.getPhoneNum();
            mImage = mUser.getImage();

        } else {

            if (outputFileUri != null){
                try {
                    mImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outputFileUri);
                } catch (Exception e){
                    e.printStackTrace();
                    mImage = mUser.getImage();
                }
            } else {
                mImage = mUser.getImage();
            }
        }
        setHeightText(mHeight);
        setWeightText(mWeight);
        setYearText(mYearOfBirth);
        mEditPrivateName.setText(mFirstName);
        mEditLastName.setText(mLastName);
        mEditPhone.setText(mPhone);
        mEditEmail.setText(mUser.getEmail());
//        mEditID.setText(mUser.getPersonal_id());
        try {
            setImage(mImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(User.WEIGHT_KEY, mWeight);
        outState.putInt(User.HEIGHT_KEY, mHeight);
        outState.putInt(User.YEAR_OF_BIRTH_KEY, mYearOfBirth);
        outState.putString(User.FIRST_NAME_KEY, mFirstName);
        outState.putString(User.LAST_NAME_KEY, mLastName);
        outState.putString(User.CELLULAR_KEY, mPhone);
        //outState.putParcelable(User.PHOTO_LOCAL_KEY, outputFileUri);
        outState.putParcelable(User.PHOTO_LOCAL_KEY, mImage);
        outState.putBoolean(PICTURE_UPDATED_KEY, mWasPictureUpdated);
    }

    private void setImage(Bitmap bitmap){
        if ( bitmap == null) {

            //mTvUploadImage.setVisibility(View.VISIBLE);
            //mIvCameraIcon.setVisibility(View.INVISIBLE);
        } else {
           // mImageCircle.setVisibility(View.GONE);
            Log.d(getFragmentName(), " <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> <-> setImage() ");
            mImageCircle.setImageBitmap(bitmap);
            mImageCircle.refreshDrawableState();
//            mTvUploadImage.setText(R.string.update_photo_heb);
//            mTvUploadImage.setTextColor(ContextCompat.getColor(getContext(), R.color.colorLightPink));
            mTvUploadImage.setVisibility(View.INVISIBLE);
            mIvCameraIcon.setVisibility(View.VISIBLE);
        }
    }
    private void saveDetails() {
        //if does not have internet access - nothing to do here...
        if(! MainActivity.isNetworkAvailable(getContext())) {
            Dialogs.getAlertDialog(getContext(), getString(R.string.cannot_connect_to_server), getString(R.string.no_internet_connection)).show();
            return;
        }
        if(! MainActivity.doesHaveStoragePermission(getContext())) {
            Dialogs.getAlertDialog(getContext(), getString(R.string.no_storage_permission_heb), getString(R.string.no_permission_heb)).show();
            return;
        }

        String firstName, lastName, phone ;
        if( mEditPrivateName.getText() != null ) {
            firstName = mEditPrivateName.getText().toString();
            if (RegistrationActivity.validateName(firstName) == null){
                openBadInputDialog(firstName, NAME_FLAG);
                return;
            }
        } else {
            openBadInputDialog(null, NAME_FLAG);
            return;
        }

        if( mEditLastName.getText() != null ) {
            lastName = mEditLastName.getText().toString();
            if (RegistrationActivity.validateName(lastName) == null){
                openBadInputDialog(lastName, NAME_FLAG);
                return;
            }
        } else {
            openBadInputDialog(null, NAME_FLAG);
            return;
        }

        if( mEditPhone.getText() != null ) {
            phone = mEditPhone.getText().toString();
            if (RegistrationActivity.validatePhone(phone) == null){
                openBadInputDialog(phone, PHONE_FLAG);
                return;
            }
        } else {
            openBadInputDialog(null, PHONE_FLAG);
            return;
        }


        Map<String,String> params = new HashMap<String, String>();

        params.put(User.FIRST_NAME_KEY,firstName);
        params.put(User.LAST_NAME_KEY,lastName);
        params.put(User.EMAIL_KEY,mUser.getEmail());
        params.put(User.PERSONAL_ID_KEY,mUser.getPersonal_id());
        params.put(User.ID_ID_KEY,mUser.getServerId());
        params.put(User.CELLULAR_KEY,phone);
        params.put(User.HEIGHT_KEY,String.valueOf(mHeight));
        params.put(User.WEIGHT_KEY,String.valueOf(mWeight));
        params.put(User.YEAR_OF_BIRTH_KEY,String.valueOf(mYearOfBirth));
        params.put(User.ONE_SIGNAL_REGISTRATION_ID, User.getNotificationUserId() != null ? User.getNotificationUserId() : "");

        if(mWasPictureUpdated && mImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mImage.compress(Bitmap.CompressFormat.PNG, 100 , stream );
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr , Base64.DEFAULT);
            params.put(User.PHOTO_BASE_KEY, image_str);
        }

//        String userNotificationId = User.getNotificationUserId();
//        if(userNotificationId != null) {
//            params.put(User.ONE_SIGNAL_REGISTRATION_ID, User.getNotificationUserId() != null ? User.getNotificationUserId() : "");
//        }

        final Dialog spinningDialog = Dialogs.showSpinningWheelDialog(getActivity());
        spinningDialog.show();
        //send request
        NetworkCenterHelper.updateUserDetails(getContext(), mUser, params, new PostResponseCallback() {
            @Override
            public void requestStarted() {
                Log.d(getFragmentName(), "<> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> in requestStarted()");

            }

            @Override
            public void requestCompleted(String response) {
                Log.d(getFragmentName(), "<> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> in requestCompleted() response = " + response);
                JSONObject jsonUser = null;
                User user = null;
                try {
                    jsonUser = new JSONObject(response);
                    //create an User object
                    ConnectedUser.getInstance().updateUserDetails(jsonUser, getActivity().getApplicationContext());

//                    //set user to Application
//                    ((RunWithMiriApplication)getActivity().getApplication()).setConnectedUser(user);
//                    user.saveLocally(getContext());

                    if(mWasPictureUpdated) {
                        ConnectedUser.getInstance().setLocalPhotoUri(outputFileUri_crop, getContext(), true);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Dialogs.getAlertDialog(getContext(), getString(R.string.details_not_saved_heb) + '\n' + e.getMessage() , getString(R.string.sorry_heb)).show();
                    if(spinningDialog != null && spinningDialog.isShowing() ) spinningDialog.dismiss();
                    return;
                }
//                catch (UserException e){
//                    e.printStackTrace();
//                    Dialogs.getAlertDialog(getContext(), getString(R.string.details_not_saved_heb) + '\n' + e.getMessage(), getString(R.string.sorry_heb)).show();
//                    if(spinningDialog != null && spinningDialog.isShowing() ) spinningDialog.dismiss();
//                    return;
//                }
                catch (Exception e){
                    e.printStackTrace();
                    Dialogs.getAlertDialog(getContext(), getString(R.string.details_not_saved_heb) + '\n' + e.getMessage(), getString(R.string.sorry_heb)).show();
                    if(spinningDialog != null && spinningDialog.isShowing() ) spinningDialog.dismiss();
                    return;
                }


                ConnectedUser.getInstance().saveLocally(getContext());
                ((RunWithMiriApplication)getActivity().getApplication()).setConnectedUser(ConnectedUser.getInstance());
                if(spinningDialog != null && spinningDialog.isShowing() ) spinningDialog.dismiss();
                Dialogs.getAlertDialog(getContext(), getString(R.string.details_saved_heb), null).show();
                ((MainActivity)getActivity()).replaceFragment(MainFragment.FRAGMENT_TAG, null, false);
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.d(getFragmentName(), "<> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> <> in requestEndedWithError() error will be printed ");
                String message = "";
                if(error != null) {
                    error.printStackTrace();
                    message = error.getMessage();
                }

                Dialogs.getAlertDialog(getContext(), getString(R.string.details_not_saved_heb) + '\n' + message, getString(R.string.sorry_heb)).show();
                if(spinningDialog != null && spinningDialog.isShowing() ) spinningDialog.dismiss();
            }
        });

    }

    private void openBadInputDialog(String input, int flag) {
        String message = null;
        if(flag == NAME_FLAG){
            if(input == null){
                message = getString(R.string.please_enter_name_heb);
            } else {
                message = input + ' ' + getString(R.string.is_not_a_valid_name_heb) + '\n' + getString(R.string.please_enter_a_valid_name_heb);
            }
        } else {
            if(input == null){
                message = getString(R.string.please_enter_phone_heb);
            } else {
                message = input + ' ' + getString(R.string.is_not_a_valid_phone_heb) + '\n' + getString(R.string.please_enter_a_valid_phone_heb);
            }
        }
        Dialogs.getAlertDialog(getContext(), message , getString(R.string.wrong_input_heb)).show();
    }

    private void resetPassword() {
        //if does not have internet access - nothing to do here...
        if(! MainActivity.isNetworkAvailable(getContext())) {
            Dialogs.getAlertDialog(getContext(), getString(R.string.cannot_connect_to_server), getString(R.string.no_internet_connection)).show();
            return;
        }

        final Dialog spinningDialog = Dialogs.showSpinningWheelDialog(getActivity());
        spinningDialog.show();

        NetworkCenterHelper.resetPassword(getContext(), mUser.getEmail() , new PostResponseCallback() {
            @Override
            public void requestStarted() {

            }

            @Override
            public void requestCompleted(String response) {
                if(response != null) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String sucess = jObj.getString(LoginActivity.SUCCESS_KEY);
                        String status = jObj.getString(LoginActivity.STATUS_KEY);
                        if(sucess != null && sucess.matches("true")){
                            spinningDialog.dismiss();
                            Dialogs.showErrorDialog(getContext(), getString(R.string.reset_link_was_sent_to_email_heb)+ "\n" + mUser.getEmail(), null);
                        } else {
                            spinningDialog.dismiss();
                            Dialogs.showErrorDialog(getContext(), getString(R.string.password_was_not_reset_heb) + "\n" + status != null ? status : "" , null);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                        spinningDialog.dismiss();
                        Dialogs.showErrorDialog(getContext(), getString(R.string.password_was_not_reset_heb)+ "\n" + getString(R.string.check_email_address_heb), null);
                    }
                }


            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                String message = getString(R.string.password_was_not_reset_heb);
                if (error != null){
                    message += "\n" + error.getMessage();
                    error.printStackTrace();
                }
                spinningDialog.dismiss();
                Dialogs.showErrorDialog(getContext(), message, null);

            }
        });

    }

    @Override
    public void storagePermissionGranted() {

    }


    @Override
    public int getResourceID() {
        return R.layout.fragment_personal_details;
    }


    private File sdImageFileInPicturesDirectory;

    private void uploadPicture() {

        try {
            // Determine Uri of camera image to save.
            final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
            root.mkdirs();
            final String fname = System.currentTimeMillis() + "IMG";
            sdImageFileInPicturesDirectory = new File(root, fname);
            if (Build.VERSION.SDK_INT > M) {//6
                outputFileUri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName() + getString(R.string.file_provider), sdImageFileInPicturesDirectory);
            } else {
                outputFileUri = Uri.fromFile(sdImageFileInPicturesDirectory);
            }

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getActivity().getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                cameraIntents.add(intent);
            }

            // Filesystem.
//            final Intent galleryIntent = new Intent();
//            galleryIntent.setType("image/*");
//            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            final Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            galleryIntent.setType("image/*");


            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.choose_heb));

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, UPDATE_DETAILS_PICTURE_REQUEST);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            Log.i(getFragmentName(), "onActivityResult() - PersonalDetailsFragment !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            if (requestCode == UPDATE_DETAILS_PICTURE_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    boolean isCamera;
                    //Uri selectedImageUri = null;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }


                    if (isCamera) {
                        Bitmap mutablePhoto = null;
                        Bitmap photo;
                        try {
                            photo = android.provider.MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), this.outputFileUri);

                            ExifInterface ei = new ExifInterface(this.sdImageFileInPicturesDirectory.getAbsolutePath());
                            if (ei != null) {
                                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                                //Bitmap rotatedBitmap = null;
                                switch (orientation) {

                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        mutablePhoto = PostRunFragment.rotateImage(photo, 90);
                                        break;

                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        mutablePhoto = PostRunFragment.rotateImage(photo, 180);
                                        break;

                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        mutablePhoto = PostRunFragment.rotateImage(photo, 270);
                                        break;

                                    case ExifInterface.ORIENTATION_NORMAL:
                                    default:
                                        mutablePhoto = photo;
                                }
                            } else {
                                mutablePhoto = photo;
                                //mutablePhoto = photo.copy(Bitmap.Config.ARGB_8888, true);
                            }

                            if (mutablePhoto == null) {
                                Dialogs.getAlertDialog(getActivity(), getString(R.string.camera_operation_faild_heb), getString(R.string.sorry_heb)).show();
                                return;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            FileOutputStream outputStream = new FileOutputStream(this.sdImageFileInPicturesDirectory);
                            mutablePhoto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        } catch (RuntimeException rte) {
                            rte.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        outputFileUri = data == null ? null : data.getData();
                    }
                }

                if (outputFileUri == null) { //result bad or bad outcome
                    Log.d(getFragmentName(), "RESULT_NOT_OK !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.creating_picture_faild_heb), null, UIHelper.TRY_LATER_MESSAGE_FLAG, null).show();
                } else {
                    cropImage(outputFileUri);
                    mWasPictureUpdated = true;
                }

            } else if (requestCode == Crop.REQUEST_CROP) {//UPDATE_DETAILS_CROP_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    //get the returned data
                    //Bitmap cropedImage = null;
                    try {
                        mImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outputFileUri_crop);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Dialogs.getAlertDialog(getActivity(), getString(R.string.picture_not_croped_heb), null).show();
                    }
                    if (mImage != null) {

                        setImage(mImage);
                    }

                }

            } else {
                try {


                    // in case crop failed, just copy original image
                    if (mImage == null) {

                        // copy image because- in case of choosing from Google photos, uri will not be available after leaving app or period of time. I get SecurityException when try to reach it.

                        ContentResolver contentResolver = getActivity().getContentResolver();

                        if(contentResolver != null) {
                            InputStream inputStream = contentResolver.openInputStream(this.outputFileUri);
                            FileOutputStream fileOutputStream = null;

                            if (sdImageFileInPicturesDirectory_crop != null && sdImageFileInPicturesDirectory_crop.exists()) {
                                fileOutputStream = new FileOutputStream(sdImageFileInPicturesDirectory_crop);
                            } else {

                            }
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                            fileOutputStream.close();
                            inputStream.close();


                            outputFileUri_crop = Uri.fromFile(sdImageFileInPicturesDirectory_crop);

                            mImage = MediaStore.Images.Media.getBitmap(contentResolver, this.outputFileUri_crop);
                            setImage(mImage);

                        }
                        else {
                            Dialogs.getAlertDialog(getActivity(), getString(R.string.picture_not_croped_heb), null).show();
                            return;
                        }

                    }



//                    if (mImage == null) {
//                        mImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outputFileUri);
//                    }

//                    setImage(mImage);
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.picture_not_croped_heb), null).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (OutOfMemoryError er){
            er.printStackTrace();
            Dialogs.getAlertDialog(getContext(), getString(R.string.picture_not_loaded_heb), getString(R.string.sorry_heb)).show();
        } catch (Exception ex){
            ex.printStackTrace();
            Dialogs.getAlertDialog(getContext(), getString(R.string.picture_not_loaded_heb), getString(R.string.sorry_heb)).show();
        }

    }

    File sdImageFileInPicturesDirectory_crop = null;
    Uri outputFileUri_crop = null;

    private void cropImage(Uri picUri){
        if (picUri == null) return;
        Log.i(getFragmentName(), "Got to cropImage()!! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "_CROP_IMG.jpg";
        this.sdImageFileInPicturesDirectory_crop = new File(root, fname);
        if(Build.VERSION.SDK_INT > M) {
            this.outputFileUri_crop = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + getString(R.string.file_provider), sdImageFileInPicturesDirectory_crop);
        } else {
            this.outputFileUri_crop = Uri.fromFile(sdImageFileInPicturesDirectory_crop);
        }
        Crop.of(picUri, outputFileUri_crop).asSquare().withMaxSize(256, 256).start(getActivity());
    }
}
