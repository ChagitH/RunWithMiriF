package com.forst.miri.runwithme.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.application.RunWithMiriApplication;
import com.forst.miri.runwithme.exceptions.UserException;
import com.forst.miri.runwithme.fragments.AlertDialogFragment;
import com.forst.miri.runwithme.fragments.MainFragment;
import com.forst.miri.runwithme.fragments.PostRunFragment;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.NetworkCenter;
import com.forst.miri.runwithme.miscellaneous.NumberPickerDialog;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
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


public class RegistrationActivity extends AppCompatActivity implements NumberPickerDialog.NumberPickerDialogCallback, PostResponseCallback {

    private TextView tvWeight, tvYear, tvHeight, mTvUploadImage;
    private EditText mEditPrivateName, mEditLastName, mEditPhone, mEditEmail, mEditPassword1,  mEditPassword2;//, mEditId;
    private String mFirstName, mLastName, mPhone, mEmail, mPassword;//, mId;
    private CircleImageView imageCircle;
    private User connectedUser;
    public static final int PICTURE_REQUEST = 11;
    //public static final int CROP_REQUEST = 22;
    private Uri outputFileUri;
    private Uri outputFileUri_crop;
    private File sdImageFileInPicturesDirectory_crop;
    private Bitmap mImage = null;
    private int mWeight, mHeight, mYearOfBirth;
    Dialog myDialog = null;
    private CheckBox chHealthDeclaration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        imageCircle = (CircleImageView) findViewById(R.id.main_activity_circle_image);
        imageCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

        mTvUploadImage = (TextView) findViewById(R.id.registration_activity_tv_upload_image);
        final View vFirstName = findViewById(R.id.registration_activity_v_first_name);
        final View vLastName = findViewById(R.id.registration_activity_v_family_name);
        //final View vId = findViewById(R.id.registration_activity_v_id);
        final View vPhone = findViewById(R.id.registration_activity_v_phone);
        final View vEmail = findViewById(R.id.registration_activity_v_email);
        final View vPassword1 = findViewById(R.id.registration_activity_v_password_1);
        final View vPassword2 = findViewById(R.id.registration_activity_v_password_2);

        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {

            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }



        tvWeight = (TextView) findViewById(R.id.registration_activity_tv_kg);
        tvWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog npDialog = new NumberPickerDialog(RegistrationActivity.this, getString(R.string.weight_in_kg_hebrew), 35, 160, 70, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        mWeight = selectedValue;
                        tvWeight.setText(String.valueOf(selectedValue) + " " + getString(R.string.kg_heb));
                    }
                });
            }
        });

        tvYear = (TextView) findViewById(R.id.registration_activity_tv_year_of_birth);
        tvYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog npDialog = new NumberPickerDialog(RegistrationActivity.this, getString(R.string.year_of_birth_heb), 1930, 2010, 1980, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        mYearOfBirth = selectedValue;
                        tvYear.setText(String.valueOf(selectedValue));
                    }
                });
            }
        });

        tvHeight = (TextView) findViewById(R.id.registration_activity_tv_hight);
        tvHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog npDialog = new NumberPickerDialog(RegistrationActivity.this, getString(R.string.height_in_cm_heb), 150, 215, 165, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        mHeight = selectedValue;
                        tvHeight.setText(String.valueOf(selectedValue) + " " + getString(R.string.cm_heb));
                    }
                });
            }
        });


        mEditPrivateName = (EditText) findViewById(R.id.registration_activity_et_first_name);
        mEditPrivateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateName(s.toString()) != null) {
                    vFirstName.setVisibility(View.VISIBLE);
                } else {
                    vFirstName.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
                validateName(s.toString());
            }
        });
        mEditLastName = (EditText) findViewById(R.id.registration_activity_et_last_name);
        mEditLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in beforeTextChanged() - " +  s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateName(s.toString()) != null) {
                    vLastName.setVisibility(View.VISIBLE);
                } else {
                    vLastName.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
                validateName(s.toString());
            }
        });
        mEditPhone = (EditText) findViewById(R.id.registration_activity_et_phone_num);
        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in beforeTextChanged() - " +  s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validatePhone(s.toString()) != null) {
                    vPhone.setVisibility(View.VISIBLE);
                } else {
                    vPhone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
                validatePhone(s.toString());
            }
        });
        mEditEmail = (EditText) findViewById(R.id.registration_activity_et_email);
        mEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in beforeTextChanged() - " +  s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateEmail(s.toString()) != null) {
                    vEmail.setVisibility(View.VISIBLE);
                } else {
                    vEmail.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
                validateEmail(s.toString());
            }
        });
        mEditPassword1 = (EditText) findViewById(R.id.registration_activity_et_password_1);
        mEditPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.i("§§§§§§§§§§§§§§§§§§§§","in beforeTextChanged() - " +  s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validatePassword_num1(s.toString()) != null) {
                    vPassword1.setVisibility(View.VISIBLE);
                } else {
                    vPassword1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
//                validatePassword_num1(s.toString());
            }
        });
        mEditPassword2 = (EditText) findViewById(R.id.registration_activity_et_password_2);
        mEditPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.i("§§§§§§§§§§§§§§§§§§§§","in beforeTextChanged() - " +  s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validatePassword_num2(s.toString()) != null) {
                    vPassword2.setVisibility(View.VISIBLE);
                } else {
                    vPassword2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
//                validatePassword_num2(s.toString());
            }
        });

//        mEditId= (EditText) findViewById(R.id.registration_activity_et_id_num);
//        mEditId.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.i("§§§§§§§§§§§§§§§§§§§§","in beforeTextChanged() - " +  s.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(validateId(s.toString()) != null) {
//                    vId.setVisibility(View.VISIBLE);
//                } else {
//                    vId.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.i("§§§§§§§§§§§§§§§§§§§§","in afterTextChanged() - " +  s.toString());
//                validateId(s.toString());
//            }
//        });

        Button bRegister = (Button) findViewById(R.id.registration_activity_button_register);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // register
                gatherData();
            }
        });
        Button bLogin = (Button) findViewById(R.id.registration_activity_button_login);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        chHealthDeclaration = (CheckBox) findViewById(R.id.registration_activity_checkBox_health_agreement);

        Button bReadMoreHealthDeclaration = (Button) findViewById(R.id.registration_activity_button_health_agreement);
        bReadMoreHealthDeclaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open dialog with health agreement
                Dialogs.getHelthAgreementAlertDialog(RegistrationActivity.this, new AlertDialogFragment.YesNoListener() {
                    @Override
                    public void onYes() {
                        chHealthDeclaration.setChecked(true);
                    }

                    @Override
                    public void onNo() {
                        RegistrationActivity.this.finishAffinity();
                    }
                }).show();
            }
        });
    }


    private void gatherData(){

        //if does not have internet access - nothing to do here...
        if(! MainActivity.isNetworkAvailable(this)) {
            Dialogs.getAlertDialog(this, getString(R.string.cannot_connect_to_server), getString(R.string.no_internet_connection)).show();
            return;
        }


        if(! chHealthDeclaration.isChecked()){
            Dialogs.getAlertDialog(this, getString(R.string.must_agree_to_agreement_heb), null).show();
            return;
        }

        String messageHeader = getString(R.string.register_message_header_heb);
        int weight = validateWeight(mWeight);
        if(weight <= 0){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.weight_in_kg_hebrew) + ",\n");

            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.weight_in_kg_hebrew), null).show();
            return;
        }
        int year_of_birth = validateYear(mYearOfBirth);
        if(year_of_birth < 0){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.year_of_birth_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.year_of_birth_heb), null).show();
            return;
        }
        int height = validateHeight(mHeight);
        if(height < 0){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.hight_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.hight_heb), null).show();
            return;
        }

        mFirstName = validateName(mEditPrivateName.getText());
        if(mFirstName == null){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.first_name_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.first_name_heb), null).show();
            return;
        }
        mLastName = validateName(mEditLastName.getText());
        if(mLastName == null){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.last_name_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.last_name_heb), null).show();
            return;
        }
//        mId = validateId(mEditId.getText());
//        if(mId == null){
////            if (message == null) message = new StringBuilder();
////            message.append(getString(R.string.id_num_heb) + ",\n");
//            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.id_num_heb), null).show();
//            return;
//        }
        mPhone = validatePhone(mEditPhone.getText());
        if(mPhone == null){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.phone_num_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.phone_num_heb), null).show();
            return;
        }
        mEmail = validateEmail(mEditEmail.getText());
        if(mEmail == null){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.email_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.email_heb), null).show();
            return;
        }
        mPassword = mEditPassword2.getText() != null ? validatePassword_num2( mEditPassword2.getText().toString()) : null;
        if(mPassword == null){
//            if (message == null) message = new StringBuilder();
//            message.append(getString(R.string.password_heb) + ",\n");
            Dialogs.getAlertDialog(this, messageHeader + " " + getString(R.string.password_heb), null).show();
            return;
        }
        if(mImage == null){
            //todo ask if want to add with ok/cancel dialog
        }

//        User user = new User();
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setId(id);
//        user.setPhoneNum(phone);
//        user.setEmail(email);
//        user.setPassword(password);
//        user.setWeight(weight);
//        user.setYearOfBirth(year_of_birth);
//        user.setHeight(height);
//        if(mImage != null)  user.setImage(mImage); // not validated and not demanded.

        register();



    }




    private void register() {
        String url = new String(getString(R.string.server_base) + "/api/users");

        Map<String,String> params = new HashMap<String, String>();

        params.put(User.FIRST_NAME_KEY,mFirstName);
        params.put(User.LAST_NAME_KEY,mLastName);
        params.put(User.EMAIL_KEY,mEmail);
        params.put(User.PASSWORD_KEY, mPassword);
        params.put(User.PERSONAL_ID_KEY,"012346789"); //3.7.2018 miri asked to eliminate id
        params.put(User.CELLULAR_KEY,mPhone);
        params.put(User.HEIGHT_KEY,String.valueOf(mHeight));
        params.put(User.WEIGHT_KEY,String.valueOf(mWeight));
        params.put(User.YEAR_OF_BIRTH_KEY,String.valueOf(mYearOfBirth));
        params.put(User.ONE_SIGNAL_REGISTRATION_ID, User.getNotificationUserId());


        //save image if uploaded
        if(mImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mImage.compress(Bitmap.CompressFormat.PNG, 100 , stream );
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr , Base64.DEFAULT);
            params.put(User.PHOTO_BASE_KEY, image_str);
        }

        NetworkCenter.postInstance().requestPost(this, url, this, params, null);

    }



    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /*
     Adding option to swipe down to close keyboard
      */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void sendValue(int selectedValue) {
        tvWeight.setText(String.valueOf(selectedValue));
    }


    private File sdImageFileInPicturesDirectory = null;
    private void uploadPicture() {

        // Determine Uri of camera image to save.
        final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "IMG";
        sdImageFileInPicturesDirectory = new File(root, fname);
        if(Build.VERSION.SDK_INT > M) {
            outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + getString(R.string.file_provider), sdImageFileInPicturesDirectory);
        } else {
            outputFileUri = Uri.fromFile(sdImageFileInPicturesDirectory);
        }

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
//        final Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryIntent.setType("image/*");

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.choose_heb));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, PICTURE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            //Uri selectedImageUri = null;
            Log.i("%%%%%%%%%%%%%%%%%%%", "onActivityResult() !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            //if (resultCode == RESULT_OK) {
                if (requestCode == PICTURE_REQUEST) {
                    final boolean isCamera;
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
                        // selectedImageUri = outputFileUri;

                        //////
                        Bitmap mutablePhoto = null;
                        Bitmap photo;
                        try {
                            photo = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), this.outputFileUri);

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
                                Dialogs.getAlertDialog(this, getString(R.string.camera_operation_faild_heb), getString(R.string.sorry_heb)).show();
                                return;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Dialogs.getAlertDialog(this, getString(R.string.camera_operation_faild_heb), getString(R.string.sorry_heb)).show();
                            return;
                        }

                        try {
                            FileOutputStream outputStream = new FileOutputStream(this.sdImageFileInPicturesDirectory);
                            mutablePhoto.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }


                    } else {
                        outputFileUri = data == null ? null : data.getData();
                        //outputFileUri = selectedImageUri;
                    }


                    ///////
                    if (outputFileUri == null) { //result bad or bad outcome
                        Log.d(getClass().getName(), "RESULT_NOT_OK !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                        Dialogs.getAlertDialog(this, getString(R.string.creating_picture_faild_heb), null, UIHelper.TRY_LATER_MESSAGE_FLAG, null).show();
                    } else {

                        // intent to crop image
                        cropImage(outputFileUri);
                    }

                    //cropImage(outputFileUri);
                }
//            }
//            else {
//                //open message
//                Log.i("%%%%%%%%%%%%%%%%%%%", "RESULT_NOT_OK !! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                Dialogs.getAlertDialog(this, getString(R.string.creating_picture_faild_heb), null, UIHelper.TRY_LATER_MESSAGE_FLAG, null).show();
//            }

            if (requestCode == Crop.REQUEST_CROP) {


                //Bitmap cropedPhotoOrNot = null;
                if (resultCode == Activity.RESULT_OK) {

                    final Uri resultUri = Crop.getOutput(data);

                    try {
                        mImage = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError oome) {
                        oome.printStackTrace();
                        Dialogs.getAlertDialog(this, getString(R.string.crop_operation_faild_heb), getString(R.string.sorry_heb)).show();
                        return;
                    }

                }

                // in case crop failed (i.e. do not accept pic in Cropping), just copy original image
                if (mImage == null) {

                    try {
                       // copy image because- in case of choosing from Google photos, uri will not be available after leaving app or period of time. I get SecurityException when try to reach it.

                        InputStream inputStream = getContentResolver().openInputStream(this.outputFileUri);
                        FileOutputStream fileOutputStream = null;

                        if(sdImageFileInPicturesDirectory_crop != null){// && sdImageFileInPicturesDirectory_crop.exists()) {
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

                        mImage = MediaStore.Images.Media.getBitmap(getContentResolver(), this.outputFileUri_crop);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Dialogs.getAlertDialog(this, getString(R.string.crop_operation_faild_heb), getString(R.string.sorry_heb)).show();
                        return;
                    }
                }

                if (mImage != null) {
                    //display the returned cropped image

                    imageCircle.setImageBitmap(mImage);

                    mTvUploadImage.setVisibility(View.GONE);
                }

            }
        }catch (OutOfMemoryError er){
            er.printStackTrace();
            Dialogs.getAlertDialog(this, getString(R.string.picture_not_loaded_heb), getString(R.string.sorry_heb)).show();
        } catch (Exception ex){
            ex.printStackTrace();
            Dialogs.getAlertDialog(this, getString(R.string.picture_not_loaded_heb), getString(R.string.sorry_heb)).show();
        }

    }


    private void cropImage(Uri picUri){

        if (picUri == null) return;

        Log.d(getClass().getName(), "Got to cropImage()!! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "_CROP_IMG.jpg";
        this.sdImageFileInPicturesDirectory_crop = new File(root, fname);
        if(Build.VERSION.SDK_INT > M) {
            this.outputFileUri_crop = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + getString(R.string.file_provider), sdImageFileInPicturesDirectory_crop);
        } else {
            this.outputFileUri_crop = Uri.fromFile(sdImageFileInPicturesDirectory_crop);
        }


        Crop.of(picUri, outputFileUri_crop).asSquare().withMaxSize(256, 256).start(this);
    }



//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //todo:
//        super.onSaveInstanceState(outState);
//    }


    @Override
    public void requestStarted() {
        myDialog = new Dialogs().showSpinningWheelDialog(this);
        myDialog.show();
    }

    @Override
    public void requestCompleted(String response) {
        Log.i("ZZZZZZZZZZZZZZZZZZZ", "requestCompleted !!! response = " + response);
        try {
            JSONObject jsonUser = new JSONObject(response);
            final User user = new User(jsonUser, this);

            user.setLocalPhotoUri(outputFileUri_crop, this, false);
            user.saveLocally(this); //save to shared Preferences
            ((RunWithMiriApplication)getApplication()).setConnectedUser(user);
            //go to main activity. remove from stack Trace
            Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(User.USER_KEY, user);
            i.putExtra(MainActivity.FRAGMET_TAG_KEY, MainFragment.FRAGMENT_TAG);
            finishAffinity();
            startActivity(i);
//            finish();

        } catch (JSONException e){
            e.printStackTrace();
//            showErrorDialog(this, getString(R.string.problem_retreiving_user_data), UIHelper.CALL_SERVICE_MESSAGE_FLAG);
            Dialogs.getAlertDialog(this, getString(R.string.problem_registring_user_data) + ".", getString(R.string.problem_with_registration_heb), UIHelper.CALL_SERVICE_MESSAGE_FLAG, null).show();
        } catch (UserException e){
            e.printStackTrace();
//            showErrorDialog(this, getString(R.string.problem_creating_user_data), UIHelper.CALL_SERVICE_MESSAGE_FLAG);
            Dialogs.getAlertDialog(this, getString(R.string.problem_creating_user_data) + ".", getString(R.string.problem_with_registration_heb), UIHelper.CALL_SERVICE_MESSAGE_FLAG, null).show();
        }
        if(myDialog != null) myDialog.dismiss();
    }

    @Override
    public void requestEndedWithError(VolleyError error) {
        String message = getString(R.string.problem_with_connecting) + ".";
        if (error != null) {
            error.printStackTrace();
            if (error.networkResponse != null && error.networkResponse.data != null) {
                String data = new String(error.networkResponse.data);
                message = UIHelper.getErrorStringToPresentToUserFromJson(this, data);
//                Log.i("ZZZZZZZZZZZZZZZZZZZ", "requestEndedWithError :(  DATA = " + data);
            }
//            Log.i("ZZZZZZZZZZZZZZZZZZZ", "requestEndedWithError :(  = " + error.networkResponse);
        }
        if(myDialog != null) myDialog.dismiss();
        Dialogs.getAlertDialog(this, message, getString(R.string.problem_with_registration_heb)).show();
    }




    private int validateHeight(int val) {
        if(val > 100 && val <250) {
            return val;
        } else {
            return -1;
        }
    }

    private int validateYear(int val) {
        if(val > 1900 && val < 2015) {
            return val;
        } else {
            return -1;
        }
    }

    private int validateWeight(int val) {
        if(val > 40 && val < 200) {
            return val;
        } else {
            return -1;
        }
    }

    private String validateEmail(String email) {
        if (TextUtils.isEmpty(email) || ! android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return null;
        }
        return email;
    }

    public static String validatePhone(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10 || phoneNumber.length() > 16){
            return null;
        }
        if( android.util.Patterns.PHONE.matcher(phoneNumber).matches()){
            return phoneNumber;
        }
        return null;
    }

    public static String validateName(String name) {
        if (name == null || TextUtils.isEmpty(name)) return null;
        return name;
    }

    private String validatePassword(String password){
        if (password == null || TextUtils.isEmpty(password)){
            return null;
        } else if(password.length() < 4){
            return null;
        }
        return password;
    }

    private String validatePassword_num1(String password1) {
        return validatePassword(password1);
    }

    private String validatePassword_num2(String password2) {
        if (validatePassword(password2) == null) return null;
        if(mEditPassword1.getText() != null ){
            String p1 = mEditPassword1.getText().toString();
            String p2 = password2.toString();
            if(p2.equals(p1)){
                return p2;
            }
        }
        return null;
    }

//    private String validateId(String id){
//        if (TextUtils.isEmpty(id) || id.length() < 8 || id.length() > 10){
//            return null;
//        }
//        try {
//            Long.parseLong(id);
//        } catch(NumberFormatException e){
//            return null;
//        }
//        return id;
//    }

    private String validateEmail(Editable email) {
        return validateEmail(email.toString());
    }

    private String validatePhone(Editable phoneNumber) {
        return phoneNumber == null ? null : validatePhone(phoneNumber.toString());
    }

    private String validateName(Editable name) {
        return name == null ? null : validateName(name.toString());
    }

//    private String validatePassword(Editable password){
//        if(password == null) return null;
//        else return validatePassword(password.toString());
//    }

//    private String validateId(Editable id){
//        return id == null ? null : validateId(id.toString());
//    }
}










































