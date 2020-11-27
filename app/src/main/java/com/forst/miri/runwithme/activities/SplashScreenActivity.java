package com.forst.miri.runwithme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.forst.miri.runwithme.R;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1800;
    private static boolean activityStarted;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (   activityStarted
                && getIntent() != null
                && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
            finish();
            return;

//            OneSignal calls startActivity with the following intent flags.
//
//            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK
        }

        activityStarted = true;

        setContentView(R.layout.activity_splash_screen);

        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        final ImageView logo = (ImageView) findViewById(R.id.splash_v2);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate);
        logo.setAnimation(translate);
        logo.animate();

        setVisible(true);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

            @Override
            public void run() {

                // This method will be executed once the timer is over
                // Start app main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
