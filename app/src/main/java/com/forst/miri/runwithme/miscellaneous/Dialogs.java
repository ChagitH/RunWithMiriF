package com.forst.miri.runwithme.miscellaneous;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.fragments.AlertDialogFragment;

import static com.forst.miri.runwithme.miscellaneous.UIHelper.CALL_SERVICE_MESSAGE_FLAG;
import static com.forst.miri.runwithme.miscellaneous.UIHelper.TRY_LATER_MESSAGE_FLAG;

/**
 * Created by chagithazani on 9/7/17.
 */

public class Dialogs{

    private static Dialog spinnerDialog;

    public static Dialog showSpinningWheelDialog(Context context){
        spinnerDialog = new Dialog(context,R.style.DialogTheme);
        spinnerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinnerDialog.setCanceledOnTouchOutside(false);
        spinnerDialog.setCancelable(false);
        spinnerDialog.setContentView(R.layout.spinning_progress_bar);
        spinnerDialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);
        return spinnerDialog;
    }


    private static Dialog alertDialog;

    public static Dialog getAlertDialog(Context activity, String message, String title , int flag, final View.OnClickListener listener) {
        StringBuilder updatedMessage = new StringBuilder(message);
        if (flag != 0) {
            if (flag == TRY_LATER_MESSAGE_FLAG) {
                updatedMessage.append("\n" + activity.getString(R.string.try_later_heb));
            } else if (flag == CALL_SERVICE_MESSAGE_FLAG) {
                updatedMessage.append("\n" + activity.getString(R.string.call_customer_service_heb));
            }
        }
        alertDialog = new Dialog(activity);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.alert_dialog);
        View v = alertDialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        TextView tvTitle = (TextView) alertDialog.findViewById(R.id.alert_dialog_tv_title);
        if (title == null) {
            tvTitle.setVisibility(View.GONE);
        } else{
            tvTitle.setText(title);
        }

        TextView tvMessage = (TextView) alertDialog.findViewById(R.id.alert_dialog_tv_message);
        tvMessage.setText(updatedMessage);
        Button bOk = (Button) alertDialog.findViewById(R.id.alert_dialog_button_ok);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(listener != null) listener.onClick(v);
            }
        });



        return alertDialog;
    }

    public static Dialog getHelthAgreementAlertDialog(Activity activity , final AlertDialogFragment.YesNoListener yesNoListener) {
        alertDialog = new Dialog(activity);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(R.layout.health_agreement_dialog_layout);
        View v = alertDialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);


//        TextView tvTitle = (TextView) alertDialog.findViewById(R.id.health_alert_dialog_tv_title);
//        tvTitle.setText(activity.getString(R.string.health_agreement));
//
//        TextView tvMessage = (TextView) alertDialog.findViewById(R.id.health_alert_dialog_tv_message);
//        tvMessage.setText(activity.getString(R.string.helth_state_statment_heb));

        Button bOk = (Button) alertDialog.findViewById(R.id.health_alert_dialog_button_ok);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesNoListener != null) yesNoListener.onYes();
                alertDialog.dismiss();
            }
        });

        Button bNo = (Button) alertDialog.findViewById(R.id.health_alert_dialog_button_no);
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesNoListener != null) yesNoListener.onNo();
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }


    public static Dialog getAlertDialog(Context activity, String message, String title, View.OnClickListener listener){
        return getAlertDialog(activity,  message, title, 0 , listener);
    }
    public static Dialog getAlertDialog(Context activity, String message, String title){
        return getAlertDialog(activity,  message, title, 0, null);
    }

    public static void showErrorDialog(Context context, String message, DialogInterface.OnClickListener listener){
        showErrorDialog(context,message, listener, 0);
    }

    public static void showErrorDialog(Context context, String message, final DialogInterface.OnClickListener listener, int flag){
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.error_dialog_layout);
        View v = alertDialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        StringBuilder updatedMessage = new StringBuilder(message);

        if (flag != 0){
            if (flag == TRY_LATER_MESSAGE_FLAG){
                updatedMessage.append("\n" + context.getString(R.string.try_later_heb));
            } else if(flag == CALL_SERVICE_MESSAGE_FLAG){
                updatedMessage.append("\n" + context.getString(R.string.call_customer_service_heb));
            }
        }

//
//        TextView tvTitle = (TextView) alertDialog.findViewById(R.id.error_alert_dialog_tv_title);
//        if(title != null )tvTitle.setText(title);

        TextView tvMessage = (TextView) alertDialog.findViewById(R.id.error_alert_dialog_tv_message);
        tvMessage.setText(message);

        Button bYes = (Button) alertDialog.findViewById(R.id.error_alert_dialog_button_close);
        bYes.setText(context.getString(R.string.i_understand_heb));

        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) listener.onClick(null,0);
                alertDialog.dismiss();
            }


        });


        alertDialog.show();


    }

    public static final Integer YES = 765;
    public static final Integer NO = 764;

    public static Dialog showYesNoErrorDialog(Context context, String message, String title, String okButtonTitle, String cancelButtonTitle, final View.OnClickListener onClickListener) {
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.yes_no_dialog_layout);
        View v = alertDialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        TextView tvTitle = (TextView) alertDialog.findViewById(R.id.yes_no_alert_dialog_tv_title);
        if(title != null )tvTitle.setText(title);

        TextView tvMessage = (TextView) alertDialog.findViewById(R.id.yes_no_alert_dialog_tv_message);
        tvMessage.setText(message);

        Button bYes = (Button) alertDialog.findViewById(R.id.yes_no_alert_dialog_button_yes);
        bYes.setTag(YES);
        if(okButtonTitle != null )bYes.setText(okButtonTitle);
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( onClickListener != null) onClickListener.onClick(view);
                alertDialog.dismiss();
            }


        });
        Button bNo = (Button) alertDialog.findViewById(R.id.yes_no_alert_dialog_button_no);
        bNo.setTag(NO);
        if(cancelButtonTitle != null )bNo.setText(cancelButtonTitle);
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( onClickListener != null) onClickListener.onClick(view);
                alertDialog.dismiss();
            }


        });

        return alertDialog;
    }

    public static Dialog showYesNoErrorDialog(Context context, String message, final View.OnClickListener onClickListener) {
        return showYesNoErrorDialog(context, message,null, null, null, onClickListener);
    }

    public static Dialog getEndOfTrainingDialog(Context context, StringBuilder updatedMessage, final View.OnClickListener onClickListener) {
        final Dialog eotAlertDialog = new Dialog(context);
        eotAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        eotAlertDialog.setCancelable(false);
        eotAlertDialog.setContentView(R.layout.end_of_training_dialog);

        TextView tvMessage = (TextView) eotAlertDialog.findViewById(R.id.end_of_training_dialog_message_tv);
        tvMessage.setText(updatedMessage);

        Button bClose = (Button) eotAlertDialog.findViewById(R.id.end_of_training_dialog_close_image_button);
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((MainActivity)getActivity()).replaceFragment(MainFragment.FRAGMENT_TAG, null);
                if(onClickListener != null) onClickListener.onClick(v);
                eotAlertDialog.dismiss();
            }
        });

//        Button bRescheduleTraining = (Button) eotAlertDialog.findViewById(R.id.end_of_training_dialog_rechedule_training_button);
//        bRescheduleTraining.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(onClickListener != null) onClickListener.onClick(view);
//                eotAlertDialog.dismiss();
//            }
//        });

        return eotAlertDialog;
    }

    public static Dialog getRetrivePasswordDialog(Context context, String email, final RetrievePasswordDialogListener onClickListener) {
        final Dialog passwordDialog = new Dialog(context);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.setCanceledOnTouchOutside(false);
        passwordDialog.setCancelable(false);
        passwordDialog.setContentView(R.layout.retrive_password_dialog_layout);
        View v = passwordDialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);


        final EditText etEmail = (EditText) passwordDialog.findViewById(R.id.et_retrive_password_email);
        if(etEmail != null && email != null && !email.isEmpty()) {
            etEmail.setText(email);
        }

        Button bSend = (Button) passwordDialog.findViewById(R.id.retrive_password_button_send);
        bSend.setTag("send");
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String outEmail = null;
                if(etEmail != null && etEmail.getText() != null) {
                    outEmail = etEmail.getText().toString();
                }
                if(onClickListener != null) onClickListener.onClick(outEmail);
            }
        });

        Button bCancel = (Button) passwordDialog.findViewById(R.id.retrive_password_button_cancel);
        bCancel.setTag("cancel");
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordDialog.dismiss();
//                if(onClickListener != null) onClickListener.onClick(view);
            }
        });



        return passwordDialog;
    }

    public static void openFeedbackDialog(Context context, int practiceNum) {

        FeedbackDialog feedbackDialog = new FeedbackDialog(context, practiceNum);
        feedbackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = feedbackDialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        feedbackDialog.setCanceledOnTouchOutside(false);
        feedbackDialog.setCancelable(false);

        int width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels * 0.90);

        feedbackDialog.getWindow().setLayout(width, height);



//        StringBuilder updatedMessage = new StringBuilder(message);
//
//        if (flag != 0){
//            if (flag == TRY_LATER_MESSAGE_FLAG){
//                updatedMessage.append("\n" + context.getString(R.string.try_later_heb));
//            } else if(flag == CALL_SERVICE_MESSAGE_FLAG){
//                updatedMessage.append("\n" + context.getString(R.string.call_customer_service_heb));
//            }
//        }
//
////
////        TextView tvTitle = (TextView) alertDialog.findViewById(R.id.error_alert_dialog_tv_title);
////        if(title != null )tvTitle.setText(title);
//
//        TextView tvMessage = (TextView) feedbackDialog.findViewById(R.id.error_alert_dialog_tv_message);
//        tvMessage.setText(message);
//
//        Button bYes = (Button) feedbackDialog.findViewById(R.id.error_alert_dialog_button_close);
//        bYes.setText(context.getString(R.string.i_understand_heb));
//
//        bYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(listener != null) listener.onClick(null,0);
//                feedbackDialog.dismiss();
//            }
//
//
//        });


        feedbackDialog.show();

    }

    public interface RetrievePasswordDialogListener{

        public void onClick(String email) ;

    }

}
