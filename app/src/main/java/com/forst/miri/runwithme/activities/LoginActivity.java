package com.forst.miri.runwithme.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.application.RunWithMiriApplication;
import com.forst.miri.runwithme.exceptions.UserException;
import com.forst.miri.runwithme.fragments.AlertDialogFragment;
import com.forst.miri.runwithme.fragments.MainFragment;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.DownloadPracticeTask;
import com.forst.miri.runwithme.miscellaneous.FetchPracticesFromCloud;
import com.forst.miri.runwithme.miscellaneous.NetworkCenter;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_PICTURES;

public class LoginActivity extends AppCompatActivity implements PostResponseCallback {

    public static final String SUCCESS_KEY = "success";
    public static final String STATUS_KEY = "status";
    private EditText mEditEmail, mEditPassword;
    Dialog myDialog = null;
    private Dialog mRetrivePasswordDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_up_from_bottom, R.anim.nothing);
        //getRuntimePermissions();

        setContentView(R.layout.activity_login);


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

        mEditEmail = (EditText) findViewById(R.id.login_activity_et_email);
        mEditPassword = (EditText) findViewById(R.id.login_activity_et_password);

        Button bLogin = (Button) findViewById(R.id.login_activity_button_login);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        Button bToRegistration = (Button) findViewById(R.id.login_activity_button_go_to_register);
        bToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });
        Button bForgotPassword = (Button) findViewById(R.id.login_activity_button_forgot_password);
        bForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = null;
                if(mEditEmail != null || mEditEmail.getText() != null){
                    email = mEditEmail.getText().toString();
                }

                mRetrivePasswordDialog = Dialogs.getRetrivePasswordDialog(LoginActivity.this, email, new Dialogs.RetrievePasswordDialogListener() {
                    @Override
                    public void onClick(String email) {
                        retrievePassword(email);
                    }
                });
                mRetrivePasswordDialog.show();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(! writeExternalPermissionGranted) {
            getRuntimePermissions();
        }
    }

    private boolean writeExternalPermissionGranted = false;
    private boolean getRuntimePermissions() {
        if(Build.VERSION.SDK_INT >= 23 ){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MainActivity.WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                writeExternalPermissionGranted = false;
            } else {
                writeExternalPermissionGranted = true;
            }
        } else{
            writeExternalPermissionGranted = true;
        }
        return writeExternalPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MainActivity.WRITE_EXTERNAL_STORAGE_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeExternalPermissionGranted = true;
            } else {
                writeExternalPermissionGranted = false;
                //presentPermmissionDialog(); todo maybe will fix Miris exception
            }
        }
    }

    private void presentPermissionDialog(final AlertDialogFragment.YesNoListener listener){
        Dialogs.showYesNoErrorDialog(this, getString(R.string.permission_is_necessary_heb), getString(R.string.no_permission_heb), getString(R.string.yes_heb), getString(R.string.no_heb), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag() == Dialogs.YES){
                    if(getRuntimePermissions()){
                        if(listener != null) listener.onYes();
                    }
                } else {
                    closeApp();
                }
            }
        }).show();
    }

    private void closeApp() {
        this.finishAffinity();
    }

    private void login() {
        //if does not have internet access - nothing to do here...
        if(! MainActivity.isNetworkAvailable(this)) {
            Dialogs.getAlertDialog(this, getString(R.string.cannot_connect_to_server), getString(R.string.no_internet_connection)).show();
            return;
        }

        if(! writeExternalPermissionGranted){
            Dialogs.getAlertDialog(this, getString(R.string.security_error), getString(R.string.please_allow_saving_data)).show();
            return;//10.4.2018
        }
//        if(! writeExternalPermissionGranted) {
//            presentPermissionDialog(new AlertDialogFragment.YesNoListener() {
//                @Override
//                public void onYes() {
//                    login();
//                }
//
//                @Override
//                public void onNo() {
//
//                }
//            });
//            return;
//        }
        if(mEditEmail.getText() == null || mEditEmail.getText().toString().length() == 0 ||
                mEditPassword.getText() == null || mEditPassword.getText().toString().length() == 0){
            displayOkDialog(getString(R.string.please_enter_email_and_password_heb));
            return;
        }

        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        String url = new String(getString(R.string.server_base) + "/api/login");

        Map<String,String> params = new HashMap<String, String>();
        params.put(User.EMAIL_KEY,email);
        params.put(User.PASSWORD_KEY, password);
        String userNotificationId = User.getNotificationUserId();
        if(userNotificationId != null) {
            params.put(User.ONE_SIGNAL_REGISTRATION_ID, userNotificationId);
        }

        NetworkCenter.postInstance().requestPost(this, url, this, params, null);
    }

    private void displayOkDialog(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton(getString(R.string.i_understand_heb),null);
        alertDialogBuilder.setMessage(message);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void retrievePassword(final String email) {
//        if(mEditEmail == null || mEditEmail.getText() == null){
//            Dialogs.showErrorDialog(this, getString(R.string.email_is_empty_heb), null);
//            return;
//        }


        final Dialog spinningDialog = Dialogs.showSpinningWheelDialog(LoginActivity.this);
        spinningDialog.show();

        NetworkCenterHelper.resetPassword(LoginActivity.this, email, new PostResponseCallback() {
            @Override
            public void requestStarted() {

            }

            @Override
            public void requestCompleted(String response) {
                if(response != null) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String sucess = jObj.getString(SUCCESS_KEY);
                        String status = jObj.getString(STATUS_KEY);
                        if(sucess != null && sucess.matches("true")){
                            spinningDialog.dismiss();
                            if (mRetrivePasswordDialog != null) {
                                mRetrivePasswordDialog.dismiss();
                            }
                            Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.reset_link_was_sent_to_email_heb)+ "\n" + email, null);

                        } else {
                            spinningDialog.dismiss();
                            Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.password_was_not_reset_heb) + "\n" + status != null ? status : "" , null);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                        spinningDialog.dismiss();
                        Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.password_was_not_reset_heb)+ "\n" + getString(R.string.check_email_address_heb), null);
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
                Dialogs.showErrorDialog(LoginActivity.this, message, null);
            }
        });


    }

    // for setting status bar fully transparent.
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


    @Override
    public void requestStarted() {
        myDialog = new Dialogs().showSpinningWheelDialog(this);//this.getApplicationContext());
        myDialog.show();
    }

    private User user = null;
    @Override
    public void requestCompleted(String response) {
        Log.d("LoginActivity", "requestCompleted !!! response = " + response);

        try {
            JSONObject jsonUser = new JSONObject(response);
            //create an User object
            user = new User(jsonUser, this);


            new AsyncTask<Void, Void, Void>() {


                @Override
                protected Void doInBackground(Void[] params) {
                    NetworkCenterHelper.getPhotoFromServer(getApplicationContext(), user, null);
                    if (user != null && user.IsRegisteredToRunWithMiri() && user.getPracticeNum() > 0) {
                        new DownloadPracticeTask(getApplicationContext(), null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Integer.valueOf(user.getPracticeNum()), Integer.valueOf(user.getProgramId()));
                        //download user properties
                    } else {
                        new DownloadPracticeTask(getApplicationContext(), null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Integer.valueOf(Practice.TRIAL_PRACTICE_NUM), Integer.valueOf(Practice.TRIAL_PROGRAM_ID));
                    }
                    if (user != null) {
                        new FetchPracticesFromCloud(getApplicationContext(), user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    return null;
                }


                @Override
                protected void onPostExecute(Void params) {
                    super.onPostExecute(params);

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            savePhotoToLocalDevice(user, getApplicationContext());
                            //set user to Application
                            ((RunWithMiriApplication)getApplication()).setConnectedUser(user);
                            user.saveLocally(LoginActivity.this);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            if(myDialog != null && myDialog.isShowing()) myDialog.dismiss();
                            // open MainActivity
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.putExtra(User.USER_KEY, user);
                            i.putExtra(MainActivity.FRAGMET_TAG_KEY, MainFragment.FRAGMENT_TAG);
                            finishAffinity();
                            startActivity(i);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                    savePhotoToLocalDevice(user, getApplicationContext());
//                    //set user to Application
//                    ((RunWithMiriApplication)getApplication()).setConnectedUser(user);
//
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            // save the user to shared preferences
//                            user.saveLocally(LoginActivity.this);
//                        }
//
//                    }.run();

//                    if(myDialog != null && myDialog.isShowing()) myDialog.dismiss();


//                    // open MainActivity
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    i.putExtra(User.USER_KEY, user);
//                    i.putExtra(MainActivity.FRAGMET_TAG_KEY, MainFragment.FRAGMENT_TAG);
//                    finishAffinity();
//                    startActivity(i);
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (JSONException e){
            e.printStackTrace();
            Dialogs.getAlertDialog(this, getString(R.string.problem_retreiving_user_data), null).show();
            //stop animation
            if(myDialog != null && myDialog.isShowing()) myDialog.dismiss();
        } catch (UserException e){
            e.printStackTrace();
            Dialogs.getAlertDialog(this, getString(R.string.problem_creating_user_data) + '\n' + e.getMessage(), null).show();
            //stop animation
            if(myDialog != null && myDialog.isShowing()) myDialog.dismiss();
        }

    }

    public static void savePhotoToLocalDevice(User user, Context context) {
        if(user == null || user.getImage() == null) {
            return;
        }
        // Determine Uri of camera image to save.
        final File root = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        root.mkdirs();
        //final String fname = user.getFirstName() + "-" + user.getLastName()+ "-" + "IMG";
        final String fname = user.getServerId() + "-" + "IMG";
        final File sdImageMainDirectory = new File(root, fname);

        FileOutputStream out = null;
        Bitmap img = user.getImage();

        if (sdImageMainDirectory.exists ())
            sdImageMainDirectory.delete ();

        try {
            sdImageMainDirectory.createNewFile();

            out = new FileOutputStream(sdImageMainDirectory);

            img.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        user.setLocalPhotoUri(Uri.fromFile(sdImageMainDirectory), context,false);
    }

    @Override
    public void requestEndedWithError(VolleyError error) {
        //stop animation
        if(myDialog != null) myDialog.dismiss();
        String errorMsg = null;
        if(error != null){
            Log.i("ZZZZZZZZZZZZZZZZZZZ", "requestEndedWithError :(  = " + error.getMessage());
            error.printStackTrace();
            errorMsg = getString(R.string.problem_retreiving_user_data).concat ("\n").concat(getString(R.string.check_parameters_heb)).concat (error.getMessage() != null ? "\n" + error.getMessage() : "");
            if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                errorMsg = getString(R.string.wrong_user_password_heb);
            }
        } else {
            errorMsg = getString(R.string.problem_retreiving_user_data).concat ("\n").concat(getString(R.string.check_parameters_heb));
        }
        Dialogs.getAlertDialog(this, errorMsg, null).show();
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
}
