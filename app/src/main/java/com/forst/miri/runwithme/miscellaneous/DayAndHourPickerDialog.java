package com.forst.miri.runwithme.miscellaneous;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.forst.miri.runwithme.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by chagithazani on 3/12/18.
 */

public class DayAndHourPickerDialog  extends Dialog implements View.OnClickListener {

        Button saveButton, cancelButton;
        Button rbSunday, rbMonday, rbTuesday, rbWednesday, rbThursday, rbFriday, rbSaturday;
        ConstraintLayout daysLayout;
        private OnDayAndHourSelectedListener listener;
        TimePicker timePicker;
        TextView tvTitle;
        int counter = 0;
        private int mSelectedDay = 1, mSelectedHour = 1, mSelectedMinute = 0;
        private static String SELECTED_DAY_KEY = "selected_day";
        private static String SELECTED_HOUR_KEY = "selected_hour";
        private static String SELECTED_MINUTE_KEY = "selected_minute";

        public DayAndHourPickerDialog(@NonNull Context context, Date date, OnDayAndHourSelectedListener listener) {
            //public DayAndHourPickerDialog(@NonNull Context context, int day, int hour, int minute, OnDayAndHourSelectedListener listener) {
            super(context);
            this.listener = listener;
            if(date != null){
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                mSelectedDay = cal.get(Calendar.DAY_OF_WEEK);
                mSelectedHour = cal.get(Calendar.HOUR_OF_DAY);
                mSelectedMinute = cal.get(Calendar.MINUTE);
            }
//            mSelectedDay = day;
//            mSelectedHour = hour;
//            mSelectedMinute = minute;
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mSelectedDay = savedInstanceState.getInt(SELECTED_DAY_KEY);
            mSelectedHour = savedInstanceState.getInt(SELECTED_HOUR_KEY);
            mSelectedMinute = savedInstanceState.getInt(SELECTED_MINUTE_KEY);
        }
        setContentView(R.layout.day_hour_picker_dialog_layout);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //rgDays = (RadioGroup) findViewById(R.id.day_hour_picker_dialog_days_radio_group);
        rbSunday = (Button) findViewById(R.id.dhp_sunday);
        rbMonday = (Button) findViewById(R.id.dhp_monday);
        rbTuesday = (Button) findViewById(R.id.dhp_tuesday);
        rbWednesday = (Button) findViewById(R.id.dhp_wednesday);
        rbThursday = (Button) findViewById(R.id.dhp_thursday);
        rbFriday = (Button) findViewById(R.id.dhp_friday);
        rbSaturday = (Button) findViewById(R.id.dhp_saturday);

        //rbSunday.setOnTouchListener(); is this better?
        rbSunday.setOnClickListener(this);
        rbSunday.setOnClickListener(this);
        rbMonday.setOnClickListener(this);
        rbTuesday.setOnClickListener(this);
        rbWednesday.setOnClickListener(this);
        rbThursday.setOnClickListener(this);
        rbFriday.setOnClickListener(this);
        rbSaturday.setOnClickListener(this);


        setSelectedDay(mSelectedDay);
        daysLayout = (ConstraintLayout) findViewById(R.id.dhp_days_constraint_layout);

        timePicker = (TimePicker) findViewById(R.id.dtpd_time_picker);
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT < 23) {
            timePicker.setCurrentHour(mSelectedHour);
            timePicker.setCurrentMinute(mSelectedMinute);
        } else {
            timePicker.setHour(mSelectedHour);
            timePicker.setMinute(mSelectedMinute);
        }


        tvTitle = (TextView) findViewById(R.id.day_hour_picker_dialog_days_title_tv);
        saveButton = (Button) findViewById(R.id.dtpd_ok_next_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                if(counter == 2) { //finished both pickers
                    int day = mSelectedDay;
                    int hour, minute;
                    if (Build.VERSION.SDK_INT < 23) {
                        hour = timePicker.getCurrentHour();
                        minute = timePicker.getCurrentMinute();
                    } else {
                        minute = timePicker.getMinute();
                        hour = timePicker.getHour();
                    }
                    if (listener != null) listener.OnDayHourSelected(day, hour, minute);
                    DayAndHourPickerDialog.this.dismiss();
                }
                saveButton.setText(R.string.save_woman_heb);
                cancelButton.setText(R.string.go_back_heb);

                timePicker.setVisibility(View.VISIBLE);
                daysLayout.setVisibility(View.INVISIBLE);
                tvTitle.setText(getContext().getString(R.string.choose_hour_to_go_out_heb));

            }
        });
        cancelButton = (Button) findViewById(R.id.dtpd_cancel_back_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counter == 0) DayAndHourPickerDialog.this.dismiss();
                counter = 0;
                saveButton.setText(R.string.continue_heb);
                cancelButton.setText(R.string.cancel_heb);
                daysLayout.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.INVISIBLE);
                tvTitle.setText(getContext().getString(R.string.choose_new_day_and_time_heb));

            }
        });

        timePicker = (TimePicker) findViewById(R.id.dtpd_time_picker);
        timePicker.setIs24HourView(true);
    }

    private void setSelectedDay(int selectedDay) {
            switch (selectedDay) {
                case 1:
                    mSelectedDay = 1;
                    setButtonSelected(rbSunday);
                    setButtonUnselected(rbMonday);
                    setButtonUnselected(rbTuesday);
                    setButtonUnselected(rbWednesday);
                    setButtonUnselected(rbThursday);
                    setButtonUnselected(rbFriday);
                    setButtonUnselected(rbSaturday);
                    break;
                case 2:
                    mSelectedDay = 2;
                    setButtonUnselected(rbSunday);
                    setButtonSelected(rbMonday);
                    setButtonUnselected(rbTuesday);
                    setButtonUnselected(rbWednesday);
                    setButtonUnselected(rbThursday);
                    setButtonUnselected(rbFriday);
                    setButtonUnselected(rbSaturday);
                    break;
                case 3:
                    mSelectedDay = 3;
                    setButtonUnselected(rbSunday);
                    setButtonUnselected(rbMonday);
                    setButtonSelected(rbTuesday);
                    setButtonUnselected(rbWednesday);
                    setButtonUnselected(rbThursday);
                    setButtonUnselected(rbFriday);
                    setButtonUnselected(rbSaturday);
                    break;
                case 4:
                    mSelectedDay = 4;
                    setButtonUnselected(rbSunday);
                    setButtonUnselected(rbMonday);
                    setButtonUnselected(rbTuesday);
                    setButtonSelected(rbWednesday);
                    setButtonUnselected(rbThursday);
                    setButtonUnselected(rbFriday);
                    setButtonUnselected(rbSaturday);
                    break;
                case 5:
                    mSelectedDay = 5;
                    setButtonUnselected(rbSunday);
                    setButtonUnselected(rbMonday);
                    setButtonUnselected(rbTuesday);
                    setButtonUnselected(rbWednesday);
                    setButtonSelected(rbThursday);
                    setButtonUnselected(rbFriday);
                    setButtonUnselected(rbSaturday);
                    break;
                case 6:
                    mSelectedDay = 6;
                    setButtonUnselected(rbSunday);
                    setButtonUnselected(rbMonday);
                    setButtonUnselected(rbTuesday);
                    setButtonUnselected(rbWednesday);
                    setButtonUnselected(rbThursday);
                    setButtonSelected(rbFriday);
                    setButtonUnselected(rbSaturday);
                    break;
                case 7:
                    mSelectedDay = 7;
                    setButtonUnselected(rbSunday);
                    setButtonUnselected(rbMonday);
                    setButtonUnselected(rbTuesday);
                    setButtonUnselected(rbWednesday);
                    setButtonUnselected(rbThursday);
                    setButtonUnselected(rbFriday);
                    setButtonSelected(rbSaturday);
            }
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle b = super.onSaveInstanceState();
        b.putInt(SELECTED_DAY_KEY, mSelectedDay);
        b.putInt(SELECTED_HOUR_KEY, mSelectedHour);
        b.putInt(SELECTED_MINUTE_KEY, mSelectedMinute);
        return b;
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.dhp_sunday){
            setSelectedDay(1);
        } else if (view.getId() == R.id.dhp_monday){
            setSelectedDay(2);
        }else if (view.getId() == R.id.dhp_tuesday){
            setSelectedDay(3);
        }else if (view.getId() == R.id.dhp_wednesday){
            setSelectedDay(4);
        }else if (view.getId() == R.id.dhp_thursday){
            setSelectedDay(5);
        }else if (view.getId() == R.id.dhp_friday){
            setSelectedDay(6);
        }else if (view.getId() == R.id.dhp_saturday){
            setSelectedDay(7);
        }
    }

    private void setButtonSelected(Button b){
        if (android.os.Build.VERSION.SDK_INT >= 23){
            b.setTextColor(this.getContext().getColor(R.color.colorDarkPink));
        } else{
            b.setTextColor(this.getContext().getResources().getColor(R.color.colorDarkPink));
        }
        b.setBackgroundResource(R.drawable.dark_pink_radio_button_stroke);
    }

    private void setButtonUnselected(Button b){
        if (android.os.Build.VERSION.SDK_INT >= 23){
            b.setTextColor(this.getContext().getColor(R.color.colorWhite));
        } else{
            b.setTextColor(this.getContext().getResources().getColor(R.color.colorWhite));
        }
        b.setBackgroundResource(R.drawable.dark_pink_radio_button);
    }

    public interface OnDayAndHourSelectedListener {
         void OnDayHourSelected(int day, int hour, int minute);
    }


}
