package com.forst.miri.runwithme.miscellaneous;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forst.miri.runwithme.R;

/**
 * Created by chagithazani on 8/17/17.
 */
public class NumberPickerDialog  extends Dialog implements NumberPicker.OnValueChangeListener {

    private NumberPickerDialogCallback mCallback;
    private int mSelectedValue;
    private String mTextToShow;
    private NumberPicker numberPicker;
    private String[] displayedValues = null;

    public NumberPickerDialog(Activity activity, String message, @Nullable  String[] displayedValues, int selectedValIndex, int mainColor, int buttonBackgroundResource, final NumberPickerDialogCallback callback ) {
        this(activity, message, 0,displayedValues.length-1,selectedValIndex, mainColor, buttonBackgroundResource, callback);
        this.displayedValues = displayedValues;
        numberPicker.setDisplayedValues(this.displayedValues);
        if(this.displayedValues != null){
            try {
                String strVal = this.displayedValues[selectedValIndex];
                this.mSelectedValue = Integer.valueOf(strVal);
            } catch (Exception e){
                e.printStackTrace();
                mSelectedValue = 0;
            }
        } else {
            mSelectedValue = 0;
        }
    }

    public NumberPickerDialog(Activity activity, String message, int minVal, int maxVal, int selectedVal, int mainColor, int buttonBackgroundResource, final NumberPickerDialogCallback callback ){
        super(activity);
        mCallback = callback;
        mTextToShow = message;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        setContentView(R.layout.dialog_number_picker);
        RelativeLayout rll = (RelativeLayout) findViewById(R.id.dialog_number_picker_main_layout);


        numberPicker = (NumberPicker)findViewById(R.id.dialog_number_picker_numberPicker);
        TextView tvTextToShow = (TextView) findViewById(R.id.dialog_number_picker_tv_text);

        tvTextToShow.setText(mTextToShow);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 23) {
            tvTextToShow.setBackgroundColor(activity.getResources().getColor(mainColor));
        } else if (Build.VERSION.SDK_INT >= 23) {
            tvTextToShow.setBackgroundColor(activity.getColor(mainColor));
        }

        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(minVal);
        numberPicker.setMaxValue(maxVal);

        numberPicker.setValue(selectedVal);



        mSelectedValue = selectedVal;
        numberPicker.setWrapSelectorWheel(false);

        Button bOk = (Button) findViewById(R.id.dialog_number_picker_button_ok);
        Button bCancel = (Button) findViewById(R.id.dialog_number_picker_button_cancel);

        bOk.setBackgroundResource(buttonBackgroundResource);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                if(mCallback != null) {
                    mCallback.sendValue(mSelectedValue);
                }
                dismiss();
            }
        });

        bCancel.setBackgroundResource(buttonBackgroundResource);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels*0.7f);
        int heightLcl = (int) (displayMetrics.heightPixels*0.4f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams)rll.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        show();
        Window window = getWindow();
        rll .setLayoutParams(paramsLcl);

    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(this.displayedValues != null){
            try {
                String strVal = this.displayedValues[newVal];
                this.mSelectedValue = Integer.valueOf(strVal);
            } catch (Exception e){
                e.printStackTrace();
                mSelectedValue = newVal;
            }
        } else {
            mSelectedValue = newVal;
        }
    }

    public interface NumberPickerDialogCallback{
        public void sendValue(int selectedValue);
    }




}
