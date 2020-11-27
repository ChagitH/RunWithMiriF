package com.forst.miri.runwithme.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.PracticeData;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etExperienceRemark, etExplanatinsRemark, etLevelRemark, etGeneralRemark;
    ImageView experienceThumb1, explanationsThumb1, levelThumb1;
    ImageView experienceThumb2, explanationsThumb2, levelThumb2;
    ImageView experienceThumb3, explanationsThumb3, levelThumb3;
    float trackLeft;
    float trackRight;
    //int windowWidth = 0;
    private int mPracticeNum = 0;
    private TextView practiceNumTextView;

    int experienceRate = 1, explanationRate = 1, levelRate = 1;
    private float xCoOrdinate,x , allLength;
    int thirdOfImageWidth;
    private static final int RATE_IN_FEEDBACK = 372165464;
    private int xMin, xMax, fixedMinX, fixedMaxX;
    RelativeLayout parent;
    int knobWidth;

    float initialX, currentX, directionX;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            this.mPracticeNum = savedInstanceState.getInt(PracticeData.PRACTICE_NUM_KEY);
        } else {
            Intent i = getIntent();
            if (i != null) {
                this.mPracticeNum = i.getIntExtra(PracticeData.PRACTICE_NUM_KEY, 0);
            }
        }

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
                finish();
            }
        });

        Button bClose = (Button) findViewById(R.id.feedback_close_button);
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        practiceNumTextView.setText(getString(R.string.practice_heb) + " " + String.valueOf(mPracticeNum));

//        parent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                int[] absoluteLocation = new int[]{0,0};
//                parent.getLocationOnScreen(absoluteLocation);
//                xMin = absoluteLocation[0];
//                xMax = xMin + parent.getWidth();
//                knobWidth = explanationsThumb.getWidth();
//                fixedMaxX = xMax - knobWidth;
//                fixedMinX = xMin + knobWidth;
//            }
//        });

        //windowWidth = getWindowManager().getDefaultDisplay().getWidth();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int getPracticeNum(){
        return this.mPracticeNum;
        //return ConnectedUser.getLessonNum();

    }

    private String getSubject(){
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.feedback_email_subject_heb));
        sb.append(" : " + ConnectedUser.getInstance().getFirstName() + " " + ConnectedUser.getInstance().getLastName());
        sb.append(" " + getString(R.string.feedback_email_subject2_heb));
        sb.append(" " + getPracticeNum());
        return sb.toString();
    }

    private String getBody(){
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.name_heb) + " ");
        sb.append(ConnectedUser.getInstance().getFirstName() + " " + ConnectedUser.getInstance().getLastName());
        sb.append(" \n");
        sb.append(" " + getString(R.string.feedback_email_subject2_heb));
        sb.append(" " + getPracticeNum());
        sb.append(" \n");

        sb.append(" " + getString(R.string.how_was_the_practice_experience_heb) + " " + experienceRate);
        sb.append(" \n");
        if(getExperienceRemark() != null) {
            sb.append(" " + getString(R.string.remark_heb) + " " + getExperienceRemark());
        }
        sb.append(" \n");

        sb.append(" " + getString(R.string.how_were_the_explanations_heb) + " " + explanationRate);
        sb.append(" \n");
        if(getExplanationsRemark() != null) {
            sb.append(" " + getString(R.string.remark_heb) + " " + getExplanationsRemark());
        }
        sb.append(" \n");

        sb.append(" " + getString(R.string.how_was_the_level_heb) + " " + levelRate);
        sb.append(" \n");
        if(getLevelRemark() != null) {
            sb.append(" " + getString(R.string.remark_heb) + " " + getLevelRemark());
        }
        sb.append(" \n");

        if(getGeneralRemark() != null) {
            sb.append(" " + getString(R.string.general_remark_heb) + " " + getGeneralRemark());
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

//    private String getExperienceRate() {
//        Float grade = (Float) experienceThumb.getTag(RATE_IN_FEEDBACK);
//        String ret = "10";
//        if (grade != null){
//            int gradeint = (int) (grade / 10);
//            ret = String.valueOf(gradeint);
//        }
//        Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
//        return ret;
//
////        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) experienceThumb.getLayoutParams();
////        String rate = null;
////        if (params.gravity == Gravity.LEFT){
////            rate = getString(R.string.not_convinient_heb);
////        } else if (params.gravity == Gravity.CENTER_HORIZONTAL){
////            rate = getString(R.string.ok_convinient_heb);
////        } else if (params.gravity == Gravity.RIGHT){
////            rate = getString(R.string.very_convinient_heb);
////        }
////        return rate;
//    }
//    private String getExplanationsRate() {
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) explanationsThumb.getLayoutParams();
//        String rate = null;
//        if (params.gravity == Gravity.LEFT){
//            rate = getString(R.string.not_clear_heb);
//        } else if (params.gravity == Gravity.CENTER_HORIZONTAL){
//            rate = getString(R.string.clear_heb);
//        } else if (params.gravity == Gravity.RIGHT){
//            rate = getString(R.string.very_clear_heb);
//        }
//        return rate;
//    }
//    private String getLevelRate() {
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) levelThumb.getLayoutParams();
//        String rate = null;
//        if (params.gravity == Gravity.LEFT){
//            rate = getString(R.string.hard_heb);
//        } else if (params.gravity == Gravity.CENTER_HORIZONTAL){
//            rate = getString(R.string.medium_heb);
//        } else if (params.gravity == Gravity.RIGHT){
//            rate = getString(R.string.easy_heb);
//        }
//        return rate;
//    }

    private void sendFeedback() {

        final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.run_with_email) });

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getSubject());

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getBody());

        startActivity(Intent.createChooser( emailIntent, getString(R.string.send_feedback_heb)));

        FeedbackActivity.this.finish();


//        new AsyncTask<Void, Void, Void>(){
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                Log.e("SendMail", "SENDING?????");
//                try {
//                    GMailSender sender = new GMailSender(getString(R.string.run_with_email), getString(R.string.run_with_pass));
//                    sender.sendMail(getSubject(), getBody(), getString(R.string.run_with_email),getString(R.string.run_with_email));
//                } catch (Exception e) {
//                    Log.e("SendMail", e.getMessage(), e);
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_was_sent_heb), Toast.LENGTH_LONG).show();
//                FeedbackActivity.this.finish();
//            }
//        }.execute();


    }

    private void animateTranslation(FrameLayout seekBar, float x, ImageView thumbView, int moveType){//}, FrameLayout parentOfThumb) {
        FrameLayout parentOfThumb = (FrameLayout)thumbView.getParent();
        final int seekbarLeftPoint = seekBar.getLeft();
        final int seekbarRightPoint = seekbarLeftPoint + seekBar.getWidth(); //experienceSeekBar.getLeft();
        int length = seekbarRightPoint - seekbarLeftPoint;
        final int leftRange = (length/3) + seekbarLeftPoint; // most right point for left range
        final int rightRange = ((length/3) * 2) + seekbarLeftPoint; // most left point for right range

        if(moveType == MotionEvent.ACTION_MOVE){


        } else {
            Log.d(FeedbackActivity.class.getName(), "On Touch event.getX() = " + x);
            Log.d(FeedbackActivity.class.getName(), "On Touch seekbarLeftPoint = " + seekbarLeftPoint + " leftRange= " + leftRange + " rightRange " + rightRange + " seekbarRightPoint " + seekbarRightPoint );
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (x < leftRange){
                params.gravity = Gravity.LEFT;
            } else if (x >= leftRange && x <= rightRange){
                params.gravity = Gravity.CENTER_HORIZONTAL;
            } else if (x > rightRange){
                params.gravity = Gravity.RIGHT;
            }

            //ImageView thumb = (ImageView)parentOfThumb.findViewById(R.id.thumb);
            TransitionManager.beginDelayedTransition(parentOfThumb);
            thumbView.setLayoutParams(params);
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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(PracticeData.PRACTICE_NUM_KEY, this.mPracticeNum);
        super.onSaveInstanceState(outState);
    }
}
