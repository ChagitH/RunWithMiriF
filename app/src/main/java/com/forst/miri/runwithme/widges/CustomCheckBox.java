package com.forst.miri.runwithme.widges;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.forst.miri.runwithme.R;

/**
 * Created by chagithazani on 12/7/17.
 */

public class CustomCheckBox  extends View implements View.OnClickListener{
    private boolean isChecked = false;
    private Drawable drawableChecked, drawableNotChecked;
    private OnCheckedListener listener = null;

    public CustomCheckBox(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomCheckBox, 0, 0);
        drawableChecked = a.getDrawable(R.styleable.CustomCheckBox_checkedBackground);
        drawableNotChecked = a.getDrawable(R.styleable.CustomCheckBox_uncheckedBackground);
        isChecked = a.getBoolean(R.styleable.CustomCheckBox_isChecked, false);
        a.recycle();

        setChecked(isChecked);
        setOnClickListener(this);

    }

    public boolean isChecked(){
        return isChecked;
    }

    public void setChecked(boolean check){
        this.isChecked = check;
        this.setBackground(isChecked ? drawableChecked : drawableNotChecked);
    }

    public void setOnCheckedListener(OnCheckedListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        setChecked(!isChecked);
        if(listener != null) listener.onChecked(this, isChecked());
    }

    public interface OnCheckedListener{
        public void onChecked(CustomCheckBox cb, boolean checked);

    }
}
