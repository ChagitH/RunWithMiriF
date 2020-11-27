package com.forst.miri.runwithme.widges;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

/**
 * Created by chagithazani on 12/7/17.
 */

public class SwitchPlus extends SwitchCompat {

    public SwitchPlus(Context context) {
        super(context);
    }

    public SwitchPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        changeColor(checked);
    }

    private void changeColor(boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int thumbColor;
            int trackColor;

            if(isChecked) {
//                thumbColor = Color.argb(255, 239, 144, 181);
                thumbColor = Color.argb(255, 255, 255, 255);
                trackColor = Color.argb(255, 226, 25, 111);
            } else {
                thumbColor = Color.argb(255, 255, 255, 255);
                trackColor = Color.argb(255, 255, 255, 255);
            }

            try {
                getThumbDrawable().setColorFilter(thumbColor, PorterDuff.Mode.MULTIPLY);
                getTrackDrawable().setColorFilter(trackColor, PorterDuff.Mode.MULTIPLY);
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
