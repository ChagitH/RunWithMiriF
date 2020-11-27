package com.forst.miri.runwithme.miscellaneous;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.PracticeData;

public class FeedbackDialog extends Dialog {

    private EditText etExperienceRemark, etExplanatinsRemark, etLevelRemark, etGeneralRemark;
    ImageView experienceThumb1, explanationsThumb1, levelThumb1;
    ImageView experienceThumb2, explanationsThumb2, levelThumb2;
    ImageView experienceThumb3, explanationsThumb3, levelThumb3;

    private int mPracticeNum = 0;
    private TextView practiceNumTextView;

    int experienceRate = 1, explanationRate = 1, levelRate = 1;


    public FeedbackDialog(@NonNull Context context, int practiceNum) {
        this(context);
        this.mPracticeNum = practiceNum;
    }

    public FeedbackDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback);

        practiceNumTextView = (TextView) findViewById(R.id.feedback_tv_practice_name);

        experienceThumb1 = (ImageView) findViewById(R.id.experience_seek_bar_thumb1);
        experienceThumb2 = (ImageView) findViewById(R.id.experience_seek_bar_thumb2);
        experienceThumb3 = (ImageView) findViewById(R.id.experience_seek_bar_thumb3);

        experienceThumb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experienceSelected(view);
            }
        });

        experienceThumb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experienceSelected(view);
            }
        });

        experienceThumb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experienceSelected(view);
            }
        });

        explanationsThumb1 = (ImageView) findViewById(R.id.explanations_seek_bar_thumb1);
        explanationsThumb2 = (ImageView) findViewById(R.id.explanations_seek_bar_thumb2);
        explanationsThumb3 = (ImageView) findViewById(R.id.explanations_seek_bar_thumb3);

        explanationsThumb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explanationsSelected(view);
            }
        });

        explanationsThumb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explanationsSelected(view);
            }
        });

        explanationsThumb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explanationsSelected(view);
            }
        });
        levelThumb1 = (ImageView) findViewById(R.id.level_seek_bar_thumb_1);
        levelThumb2 = (ImageView) findViewById(R.id.level_seek_bar_thumb_2);
        levelThumb3 = (ImageView) findViewById(R.id.level_seek_bar_thumb_3);
        levelThumb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelSelected(view);
            }
        });
        levelThumb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelSelected(view);
            }
        });
        levelThumb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelSelected(view);
            }
        });

        etGeneralRemark = (EditText) findViewById(R.id.feedback_general_remark_edit_text);
        etExperienceRemark = (EditText) findViewById(R.id.experience_edit_text);
        etExplanatinsRemark = (EditText) findViewById(R.id.explanations_edit_text);
        etLevelRemark = (EditText) findViewById(R.id.level_edit_text);

        Button bSendFeedback = (Button) findViewById(R.id.feedback_send_button);
        bSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();

            }
        });

        Button bClose = (Button) findViewById(R.id.feedback_close_button);
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void experienceSelected(View view){
        if(view == experienceThumb1){
            experienceRate = 1;
            experienceThumb1.setImageResource(R.drawable.small_rectangle_pink_full);
            experienceThumb2.setImageResource(R.drawable.small_rectangle_pink);
            experienceThumb3.setImageResource(R.drawable.small_rectangle_pink);

        } else  if(view == experienceThumb2){
            experienceRate = 2;
            experienceThumb1.setImageResource(R.drawable.small_rectangle_pink);
            experienceThumb2.setImageResource(R.drawable.small_rectangle_pink_full);
            experienceThumb3.setImageResource(R.drawable.small_rectangle_pink);

        } else  if(view == experienceThumb3){
            experienceRate = 3;
            experienceThumb1.setImageResource(R.drawable.small_rectangle_pink);
            experienceThumb2.setImageResource(R.drawable.small_rectangle_pink);
            experienceThumb3.setImageResource(R.drawable.small_rectangle_pink_full);

        }
    }

    private void explanationsSelected(View view){
        if(view == explanationsThumb1){
            explanationRate = 1;
            explanationsThumb1.setImageResource(R.drawable.small_rectangle_purple_full);
            explanationsThumb2.setImageResource(R.drawable.small_rectangle_purple);
            explanationsThumb3.setImageResource(R.drawable.small_rectangle_purple);

        } else  if(view == explanationsThumb2){
            explanationRate = 2;
            explanationsThumb1.setImageResource(R.drawable.small_rectangle_purple);
            explanationsThumb2.setImageResource(R.drawable.small_rectangle_purple_full);
            explanationsThumb3.setImageResource(R.drawable.small_rectangle_purple);

        } else  if(view == explanationsThumb3){
            explanationRate = 3;
            explanationsThumb1.setImageResource(R.drawable.small_rectangle_purple);
            explanationsThumb2.setImageResource(R.drawable.small_rectangle_purple);
            explanationsThumb3.setImageResource(R.drawable.small_rectangle_purple_full);

        }
    }

    private void levelSelected(View view){
        if(view == levelThumb1){
            levelRate = 1;
            levelThumb1.setImageResource(R.drawable.small_rectangle_blue_full);
            levelThumb2.setImageResource(R.drawable.small_rectangle_blue);
            levelThumb3.setImageResource(R.drawable.small_rectangle_blue);

        } else  if(view == levelThumb2){
            levelRate = 2;
            levelThumb1.setImageResource(R.drawable.small_rectangle_blue);
            levelThumb2.setImageResource(R.drawable.small_rectangle_blue_full);
            levelThumb3.setImageResource(R.drawable.small_rectangle_blue);

        } else  if(view == levelThumb3){
            levelRate = 3;
            levelThumb1.setImageResource(R.drawable.small_rectangle_blue);
            levelThumb2.setImageResource(R.drawable.small_rectangle_blue);
            levelThumb3.setImageResource(R.drawable.small_rectangle_blue_full);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        practiceNumTextView.setText(getContext().getString(R.string.practice_heb) + " " + String.valueOf(mPracticeNum));

    }


    private int getPracticeNum(){
        return this.mPracticeNum;

    }

    private String getSubject(){
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(R.string.feedback_email_subject_heb));
        sb.append(" : " + ConnectedUser.getInstance().getFirstName() + " " + ConnectedUser.getInstance().getLastName());
        sb.append(" " + getContext().getString(R.string.feedback_email_subject2_heb));
        sb.append(" " + getPracticeNum());
        return sb.toString();
    }

    private String getBody(){
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(R.string.name_heb) + " ");
        sb.append(ConnectedUser.getInstance().getFirstName() + " " + ConnectedUser.getInstance().getLastName());
        sb.append(" \n");
        sb.append(" " + getContext().getString(R.string.feedback_email_subject2_heb));
        sb.append(" " + getPracticeNum());
        sb.append(" \n");

        sb.append(" " + getContext().getString(R.string.how_was_the_practice_experience_heb) + " " + experienceRate);
        sb.append(" \n");
        if(getExperienceRemark() != null) {
            sb.append(" " + getContext().getString(R.string.remark_heb) + " " + getExperienceRemark());
        }
        sb.append(" \n");

        sb.append(" " + getContext().getString(R.string.how_were_the_explanations_heb) + " " + explanationRate);
        sb.append(" \n");
        if(getExplanationsRemark() != null) {
            sb.append(" " + getContext().getString(R.string.remark_heb) + " " + getExplanationsRemark());
        }
        sb.append(" \n");

        sb.append(" " + getContext().getString(R.string.how_was_the_level_heb) + " " + levelRate);
        sb.append(" \n");
        if(getLevelRemark() != null) {
            sb.append(" " + getContext().getString(R.string.remark_heb) + " " + getLevelRemark());
        }
        sb.append(" \n");

        if(getGeneralRemark() != null) {
            sb.append(" " + getContext().getString(R.string.general_remark_heb) + " " + getGeneralRemark());
        }

        sb.append("\nPhone model: ").append(Build.MANUFACTURER).append(" ").append(android.os.Build.MODEL);
        sb.append("\nAndroid version: ").append(Build.VERSION.SDK_INT);

        //todo: add more data? temperature? length of practice, duration, etc. etc. maybe after Miri askes? now?
        String versionName = BuildConfig.VERSION_NAME;
        sb.append("\nApp version: ").append(versionName);

        sb.append(" \n");
        return sb.toString();
    }

    private String getExperienceRemark() {
        return this.etExperienceRemark.getText() == null || this.etExperienceRemark.getText().length() == 0 ? null : this.etExperienceRemark.getText().toString();
    }

    private String getExplanationsRemark() {
        return this.etExplanatinsRemark.getText() == null || this.etExplanatinsRemark.getText().length() == 0 ? null : this.etExplanatinsRemark.getText().toString();
    }

    private String getLevelRemark() {
        return this.etLevelRemark.getText() == null || this.etLevelRemark.getText().length() == 0 ? null : this.etLevelRemark.getText().toString();
    }
    private String getGeneralRemark() {
        return this.etGeneralRemark.getText() == null || this.etGeneralRemark.getText().length() == 0 ? null : this.etGeneralRemark.getText().toString();
    }

    private void sendFeedback() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getContext().getString(R.string.run_with_email) });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getSubject());
        emailIntent.putExtra(Intent.EXTRA_TEXT, getBody());
        if (emailIntent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(emailIntent);
        }

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
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle outState = super.onSaveInstanceState();
        outState.putInt(PracticeData.PRACTICE_NUM_KEY, this.mPracticeNum);
        return outState;
    }
}
