package com.forst.miri.runwithme.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.miscellaneous.NumberPickerDialog;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.UserProperties;
import com.forst.miri.runwithme.widges.CustomCheckBox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetDayAndTimePracticeFragment extends ParentFragment{


    public static final String FRAGMENT_TAG = SetDayAndTimePracticeFragment.class.getSimpleName();


    public static final int TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE = 9078;
    public static final int EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE = 9079;
    public static final int WATER_NOTIFICATION_RINGTON_REQUEST_CODE = 9077;

    public static final String STR_FALSE = "false";
    public static final String STR_TRUE = "true";

    private LinearLayout llTrainings, llDrinks;
    private LinearLayout llExercises;

    private LinearLayout llReminderTraining1, llReminderTraining2, llAddReminderTraining;
    private Spinner spReminderTraining1, spReminderTraining2;
    private ArrayAdapter<String> dataAdapterReminderTraining1, dataAdapterReminderTraining2;
    private ImageButton biDeleteReminderTraining1, biDeleteReminderTraining2, biAddReminderTraining, biChooseRingtoneTraining;


    private LinearLayout llReminderExercise1, llReminderExercise2, llAddReminderExercise;
    private Spinner spReminderExercise1, spReminderExercise2;
    private ArrayAdapter<String> dataAdapterReminderExercise1, dataAdapterReminderExercise2;
    private ImageButton biDeleteReminderExercise1, biDeleteReminderExercise2, biAddReminderExercise, biChooseRingtoneExercise;

    private ImageButton biChooseRingtoneWater;

    private int waterReminderFrequency = 0;
    private ArrayList<String> trainingSchedule = new ArrayList<>();
    private ArrayList<String> exerciseSchedule = new ArrayList<>();
    //private ArrayList<String> originalTrainingReminders;
    private TextView fromHourTv, untilHourTv, frequencyHourTv, soundTrainingReminderTv, soundExerciseReminderTv, soundWaterReminderTv;
    private TextView fromTextTv, untilTextTv, frequencyTextTv, minutesTexttv, ringtonTexttv;
    private String trainingChosenRingtonePath = null, exerciseChosenRingtonePath = null, waterChosenRingtonePath = null;
    private String trainingChosenRingtoneName = null, exerciseChosenRingtoneName = null, waterChosenRingtoneName = null;
    private UserProperties userProperties = null;
    private int firstReminderTraining, secondReminderTraining, firstReminderExercise, secondReminderExercise, reminderWaterInterval;
    private ImageButton biOpenTraining , biOpenExercise, biOpenWater;
    private SwitchCompat swOnOffWater;



    public SetDayAndTimePracticeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_set_day_and_time_practice;
    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(UserProperties.USER_PROPERTIES_KEY, userProperties);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_set_day_and_time_practice, container, false);

        if(savedInstanceState!= null){
            Serializable ser = savedInstanceState.getSerializable(UserProperties.USER_PROPERTIES_KEY);
            if(ser != null) userProperties = ((UserProperties)ser);
        }

        llTrainings = (LinearLayout) rootView.findViewById(R.id.set_date_training_days_layout);
        llExercises = (LinearLayout) rootView.findViewById(R.id.set_date_exercise_days_layout);
        llDrinks = (LinearLayout) rootView.findViewById(R.id.set_drink_reminder_layout);

        //Training UI
        biOpenTraining = (ImageButton) rootView.findViewById(R.id.set_day_trainings_switch);
        biOpenTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tag = view.getTag();
                if(tag != null && tag.toString().equalsIgnoreCase(STR_FALSE)){
                    view.setTag(STR_TRUE);
                    expand(llTrainings);
                    ((ImageButton)view).setBackgroundResource(R.drawable.arrow_left_purple);
                } else {
                    view.setTag(STR_FALSE);
                    collapse(llTrainings);
                    ((ImageButton)view).setBackgroundResource(R.drawable.arrow_down_purple);
                }

                //int tag = view.getTag();
            }
        });

        llReminderTraining1 = (LinearLayout) rootView.findViewById(R.id.set_reminders_training_ll_reminder1);
        llReminderTraining1.setVisibility(View.VISIBLE);
        llReminderTraining2 = (LinearLayout) rootView.findViewById(R.id.set_reminders_training_ll_reminder2);
        llReminderTraining2.setVisibility(View.GONE);
        llAddReminderTraining = (LinearLayout) rootView.findViewById(R.id.set_reminders_training_ll_add_reminder);
        llAddReminderTraining.setVisibility(View.VISIBLE);


        spReminderTraining1 = (Spinner) rootView.findViewById(R.id.set_reminders_training_reminder_spinner1);
        spReminderTraining2 = (Spinner) rootView.findViewById(R.id.set_reminders_training_reminder_spinner2);

        // Spinner Drop down elements
        List<String> options = new ArrayList<String>();
        options.add(getResources().getString(R.string.at_time_heb));
        options.add(getResources().getString(R.string.ten_mins_before_heb));
        options.add(getResources().getString(R.string.thirtey_mins_before_heb));
        options.add(getResources().getString(R.string.one_hour_before_heb));
        options.add(getResources().getString(R.string.two_hours_before_heb));

        dataAdapterReminderTraining1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        dataAdapterReminderTraining2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        dataAdapterReminderTraining1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterReminderTraining2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spReminderTraining1.setAdapter(dataAdapterReminderTraining1);
        spReminderTraining2.setAdapter(dataAdapterReminderTraining2);
        spReminderTraining1.setSelection(0);
        spReminderTraining2.setSelection(0);

        biDeleteReminderTraining1 = (ImageButton) rootView.findViewById(R.id.set_reminders_training_reminder_spinner1_button);
        biDeleteReminderTraining2  = (ImageButton) rootView.findViewById(R.id.set_reminders_training_reminder_spinner2_button);
        biAddReminderTraining  = (ImageButton) rootView.findViewById(R.id.set_reminders_training_add_reminder_button);

        biAddReminderTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(llReminderTraining1.getVisibility() == View.VISIBLE){
                    llAddReminderTraining.setVisibility(View.GONE);
                    llReminderTraining2.setVisibility(View.VISIBLE);
                } else {
                    llReminderTraining1.setVisibility(View.VISIBLE);
                }

            }
        });

        biDeleteReminderTraining2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llReminderTraining2.setVisibility(View.GONE);
                llAddReminderTraining.setVisibility(View.VISIBLE);
            }
        });

        biDeleteReminderTraining1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llReminderTraining1.setVisibility(View.GONE);
                llAddReminderTraining.setVisibility(View.VISIBLE);
            }
        });

        //Ringtone
        biChooseRingtoneTraining = (ImageButton) rootView.findViewById(R.id.set_reminders_training_choose_ringtone_button);
        soundTrainingReminderTv = (TextView) rootView.findViewById(R.id.set_training_notification_sound_textview);

        if(this.trainingChosenRingtoneName != null){
            soundTrainingReminderTv.setText(this.trainingChosenRingtoneName);
        }
        soundTrainingReminderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRingtoneChooser(TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE);
            }
        });
        biChooseRingtoneTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRingtoneChooser(TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE);
            }
        });



        //Exercise UI
        biOpenExercise = (ImageButton) rootView.findViewById(R.id.set_day_exersices_switch);
        biOpenExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tag = view.getTag();
                if(tag != null && tag.toString().equalsIgnoreCase(STR_FALSE)){
                    view.setTag(STR_TRUE);
                    expand(llExercises);
                    ((ImageButton)view).setBackgroundResource(R.drawable.arrow_left_blue);
                } else {
                    view.setTag(STR_FALSE);
                    collapse(llExercises);
                    ((ImageButton)view).setBackgroundResource(R.drawable.arrow_down_blue);
                }

                //int tag = view.getTag();
            }
        });

        llReminderExercise1 = (LinearLayout) rootView.findViewById(R.id.set_reminders_exersize_ll_reminder1);
        llReminderExercise1.setVisibility(View.VISIBLE);
        llReminderExercise2 = (LinearLayout) rootView.findViewById(R.id.set_reminders_exersize_ll_reminder2);
        llReminderExercise2.setVisibility(View.GONE);
        llAddReminderExercise = (LinearLayout) rootView.findViewById(R.id.set_reminders_exersize_ll_add_reminder);
        llAddReminderExercise.setVisibility(View.VISIBLE);


        spReminderExercise1 = (Spinner) rootView.findViewById(R.id.set_reminders_exersize_reminder_spinner1);
        spReminderExercise2 = (Spinner) rootView.findViewById(R.id.set_reminders_exersize_reminder_spinner2);


        dataAdapterReminderExercise1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        dataAdapterReminderExercise2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);


        dataAdapterReminderExercise1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterReminderExercise2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReminderExercise1.setAdapter(dataAdapterReminderExercise1);
        spReminderExercise2.setAdapter(dataAdapterReminderExercise2);
        spReminderExercise1.setSelection(0);
        spReminderExercise2.setSelection(0);

        biDeleteReminderExercise1 = (ImageButton) rootView.findViewById(R.id.set_reminders_exersize_reminder_spinner1_button);
        biDeleteReminderExercise2  = (ImageButton) rootView.findViewById(R.id.set_reminders_exersize_reminder_spinner2_button);
        biAddReminderExercise  = (ImageButton) rootView.findViewById(R.id.set_reminders_exersize_add_reminder_button);

        biAddReminderExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(llReminderExercise1.getVisibility() == View.VISIBLE){
                    llAddReminderExercise.setVisibility(View.GONE);
                    llReminderExercise2.setVisibility(View.VISIBLE);
                } else {
                    llReminderExercise1.setVisibility(View.VISIBLE);
                }

            }
        });

        biDeleteReminderExercise2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llReminderExercise2.setVisibility(View.GONE);
                llAddReminderExercise.setVisibility(View.VISIBLE);
            }
        });

        biDeleteReminderExercise1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llReminderExercise1.setVisibility(View.GONE);
                llAddReminderExercise.setVisibility(View.VISIBLE);
            }
        });

        //Ringtone
        biChooseRingtoneExercise = (ImageButton) rootView.findViewById(R.id.set_reminders_exersize_choose_ringtone_button);
        soundExerciseReminderTv = (TextView) rootView.findViewById(R.id.set_exersize_notification_sound_textview);

        if(this.exerciseChosenRingtoneName != null){
            soundExerciseReminderTv.setText(this.exerciseChosenRingtoneName);
        }
        soundExerciseReminderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRingtoneChooser(EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE);
            }
        });
        biChooseRingtoneExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRingtoneChooser(EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE);
            }
        });




        //Water UI
        biOpenWater = (ImageButton) rootView.findViewById(R.id.set_water_reminders_switch);
        biOpenWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tag = view.getTag();
                if(tag != null && tag.toString().equalsIgnoreCase(STR_FALSE)){
                    view.setTag(STR_TRUE);
                    expand(llDrinks);
                    ((ImageButton)view).setBackgroundResource(R.drawable.arrow_left_pink);
                } else {
                    view.setTag(STR_FALSE);
                    collapse(llDrinks);
                    ((ImageButton)view).setBackgroundResource(R.drawable.arrow_down_pink);
                }
            }
        });


        //water
        biChooseRingtoneWater = (ImageButton) rootView.findViewById(R.id.set_water_choose_ringtone_button);
        soundWaterReminderTv = (TextView) rootView.findViewById(R.id.set_water_notification_sound_textview);
        if(this.waterChosenRingtoneName != null){
            soundWaterReminderTv.setText(waterChosenRingtoneName);
        }
        soundWaterReminderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRingtoneChooser(WATER_NOTIFICATION_RINGTON_REQUEST_CODE);
            }
        });
        biChooseRingtoneWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRingtoneChooser(WATER_NOTIFICATION_RINGTON_REQUEST_CODE);
            }
        });


        frequencyHourTv = (TextView) rootView.findViewById(R.id.add_water_reminder_frequency_tv);
        fromHourTv = (TextView) rootView.findViewById(R.id.add_water_reminder_from_tv);
//        fromHourTv.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                setWaterIntervalTextViewEnability(fromHourTv.getText() != null ? fromHourTv.getText().toString() : "00:00", untilHourTv.getText() != null ? untilHourTv.getText().toString() : "00:00");
//            }
//        });
        untilHourTv = (TextView) rootView.findViewById(R.id.add_water_reminder_untill_tv);
//        untilHourTv.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                setWaterIntervalTextViewEnability(fromHourTv.getText() != null ? fromHourTv.getText().toString() : "00:00", untilHourTv.getText() != null ? untilHourTv.getText().toString() : "00:00");
//            }
//        });
        Button bSave = (Button) rootView.findViewById(R.id.set_days_save_button);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        untilTextTv = (TextView) rootView.findViewById(R.id.add_water_reminder_untill_time_tv);
        fromTextTv = (TextView) rootView.findViewById(R.id.add_water_reminder_from_time_tv);
        frequencyTextTv = (TextView) rootView.findViewById(R.id.add_water_reminder_every_time_tv);
        minutesTexttv = (TextView) rootView.findViewById(R.id.add_water_reminder_minutes_time_tv);
        ringtonTexttv = (TextView) rootView.findViewById(R.id.set_water_notification_no_sound_textview1);

        swOnOffWater = (SwitchCompat) rootView.findViewById(R.id.set_drink_reminder_on_off_switch);
        swOnOffWater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked) {
                    enableWaterSetting(true);
                } else {

                    enableWaterSetting(false);
                }
            }
        });

        SetUpFragment();
        setupAllDayCheckboxs(rootView);

        return rootView;
    }

    private void enableWaterSetting(boolean enable){
        untilTextTv.setEnabled(enable);
        fromTextTv.setEnabled(enable);
        frequencyTextTv.setEnabled(enable);
        minutesTexttv.setEnabled(enable);
        untilHourTv.setEnabled(enable);
        fromHourTv.setEnabled(enable);
        frequencyHourTv.setEnabled(enable);
        soundWaterReminderTv.setEnabled(enable);
        ringtonTexttv.setEnabled(enable);
        if(enable){
            biChooseRingtoneWater.setBackgroundResource(R.drawable.arrow_left_pink);
        } else {
            biChooseRingtoneWater.setBackgroundResource(R.drawable.arrow_left_gray);
        }
    }

    private void setupAllDayCheckboxs(View rootView) {
        View.OnClickListener trainingListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView tv = (TextView)view;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("%02d:",selectedHour));
                        sb.append(String.format("%02d",selectedMinute));
                        tv.setText(sb.toString());
                        int arrayIndex = (Integer)tv.getTag();
                        replaceDayOfTraining(arrayIndex, sb.toString(), trainingSchedule);
                    }
                }, 21, 00, true);
                mTimePicker.setTitle(getString(R.string.choose_time_heb));
                mTimePicker.show();
            }
        };

        View.OnClickListener exerciseListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView tv = (TextView)view;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("%02d:",selectedHour));
                        sb.append(String.format("%02d",selectedMinute));
                        tv.setText(sb.toString());
                        int arrayIndex = (Integer)tv.getTag();
                        replaceDayOfTraining(arrayIndex, sb.toString(), exerciseSchedule);
                    }
                }, 21, 00, true);
                mTimePicker.setTitle(getString(R.string.choose_time_heb));
                mTimePicker.show();
            }
        };

        CustomCheckBox sundayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_sunday_view);
        final TextView sundayTtv = (TextView)rootView.findViewById(R.id.time_of_training_sunday_view);
        sundayTtv.setTag(0);
        sundayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        sundayTtv.setVisibility(View.VISIBLE);
                    } else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    sundayTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        sundayTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(0).matches("-")){
            sundayT.setChecked(true);
            sundayTtv.setText(trainingSchedule.get(0));
            sundayTtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox mondayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_monday_view);
        final TextView mondayTtv = (TextView)rootView.findViewById(R.id.time_of_training_monday_view);
        mondayTtv.setTag(1);
        mondayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        mondayTtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    mondayTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        mondayTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(1).matches("-")){
            mondayT.setChecked(true);
            mondayTtv.setText(trainingSchedule.get(1));
            mondayTtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox tuesdayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_tuesday_view);
        final TextView teusdayTtv = (TextView)rootView.findViewById(R.id.time_of_training_tuesday_view);
        teusdayTtv.setTag(2);
        tuesdayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        teusdayTtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    teusdayTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        teusdayTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(2).matches("-")){
            tuesdayT.setChecked(true);
            teusdayTtv.setText(trainingSchedule.get(2));
            teusdayTtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox wednesdayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_wednesday_view);
        final TextView wednesdayTtv = (TextView)rootView.findViewById(R.id.time_of_training_wednesday_view);
        wednesdayTtv.setTag(3);
        wednesdayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        wednesdayTtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    wednesdayTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        wednesdayTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(3).matches("-")){
            wednesdayT.setChecked(true);
            wednesdayTtv.setText(trainingSchedule.get(3));
            wednesdayTtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox thursdayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_thursday_view);
        final TextView thursadyTtv = (TextView)rootView.findViewById(R.id.time_of_training_thursday_view);
        thursadyTtv.setTag(4);
        thursdayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        thursadyTtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    thursadyTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        thursadyTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(4).matches("-")){
            thursdayT.setChecked(true);
            thursadyTtv.setText(trainingSchedule.get(4));
            thursadyTtv.setVisibility(View.VISIBLE);
        }
        CustomCheckBox fridayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_friday_view);
        final TextView fridayTtv = (TextView)rootView.findViewById(R.id.time_of_training_friday_view);
        fridayTtv.setTag(5);
        fridayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        fridayTtv.setVisibility(View.VISIBLE);
                    } else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    fridayTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        fridayTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(5).matches("-")){
            fridayT.setChecked(true);
            fridayTtv.setText(trainingSchedule.get(5));
            fridayTtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox saturdayT = (CustomCheckBox)rootView.findViewById(R.id.day_of_training_saturday_view);
        final TextView saturdayTtv = (TextView)rootView.findViewById(R.id.time_of_training_saturday_view);
        saturdayTtv.setTag(6);
        saturdayT.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());

                if(checked){
                    if(addDayOfTraining(day, trainingSchedule)) {
                        saturdayTtv.setVisibility(View.VISIBLE);
                    } else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, trainingSchedule);
                    saturdayTtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        saturdayTtv.setOnClickListener(trainingListener);
        if(!this.trainingSchedule.get(6).matches("-")){
            saturdayT.setChecked(true);
            saturdayTtv.setText(trainingSchedule.get(6));
            saturdayTtv.setVisibility(View.VISIBLE);
        }

        ////// Exercise
        CustomCheckBox sundayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_sunday_view);
        final TextView sundayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_sunday_view);
        sundayEtv.setTag(0);
        sundayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        sundayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    sundayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        sundayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(0).matches("-")){
            sundayE.setChecked(true);
            sundayEtv.setText(exerciseSchedule.get(0));
            sundayEtv.setVisibility(View.VISIBLE);
        }



        CustomCheckBox mondayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_monday_view);
        final TextView mondayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_monday_view);
        mondayEtv.setTag(1);
        mondayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        mondayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    mondayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        mondayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(1).matches("-")){
            mondayE.setChecked(true);
            mondayEtv.setText(exerciseSchedule.get(1));
            mondayEtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox tuesdayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_tuesday_view);
        final TextView teusdayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_tuesday_view);
        teusdayEtv.setTag(2);
        tuesdayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        teusdayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    teusdayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        teusdayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(2).matches("-")){
            tuesdayE.setChecked(true);
            teusdayEtv.setText(exerciseSchedule.get(2));
            teusdayEtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox wednesdayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_wednesday_view);
        final TextView wednesdayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_wednesday_view);
        wednesdayEtv.setTag(3);
        wednesdayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        wednesdayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    wednesdayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        wednesdayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(3).matches("-")){
            wednesdayE.setChecked(true);
            wednesdayEtv.setText(exerciseSchedule.get(3));
            wednesdayEtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox thursdayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_thursday_view);
        final TextView thursdayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_thursday_view);
        thursdayEtv.setTag(4);
        thursdayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        thursdayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    thursdayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        thursdayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(4).matches("-")){
            thursdayE.setChecked(true);
            thursdayEtv.setText(exerciseSchedule.get(4));
            thursdayEtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox fridayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_friday_view);
        final TextView fridayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_friday_view);
        fridayEtv.setTag(5);
        fridayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        fridayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    fridayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        fridayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(5).matches("-")){
            fridayE.setChecked(true);
            fridayEtv.setText(exerciseSchedule.get(5));
            fridayEtv.setVisibility(View.VISIBLE);
        }

        CustomCheckBox saturdayE = (CustomCheckBox)rootView.findViewById(R.id.day_of_exercise_saturday_view);
        final TextView saturdayEtv = (TextView)rootView.findViewById(R.id.time_of_exercise_saturday_view);
        saturdayEtv.setTag(6);
        saturdayE.setOnCheckedListener(new CustomCheckBox.OnCheckedListener() {
            @Override
            public void onChecked(CustomCheckBox cb, boolean checked) {
                int day = Integer.parseInt((String)cb.getTag());
                if(checked){
                    if(addDayOfExercise(day, exerciseSchedule)) {
                        saturdayEtv.setVisibility(View.VISIBLE);
                    }else {
                        cb.setChecked(false);
                    }
                } else {
                    removeDayOfTraining(day, exerciseSchedule);
                    saturdayEtv.setVisibility(View.INVISIBLE);
                }
            }
        });
        saturdayEtv.setOnClickListener(exerciseListener);
        if(!this.exerciseSchedule.get(6).matches("-")){
            saturdayE.setChecked(true);
            saturdayEtv.setText(exerciseSchedule.get(6));
            saturdayEtv.setVisibility(View.VISIBLE);
        }

        //Water Reminders
        swOnOffWater.setChecked(userProperties.isWaterNotificationOn());
        fromHourTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView tv = (TextView)view;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("%02d:",selectedHour));
                        sb.append(String.format("%02d",selectedMinute));
                        tv.setText(sb.toString());
                        if(userProperties != null) userProperties.setWaterNotificationFrom(sb.toString());
//                        setWaterInterval(fromHourTv.getText() != null ? fromHourTv.getText().toString() : "00:00", untilHourTv.getText() != null ? untilHourTv.getText().toString() : "00:00");
                    }
                }, 8, 00, true);
                mTimePicker.setTitle(getString(R.string.choose_time_heb));
                mTimePicker.show();
            }
        });

        untilHourTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView tv = (TextView)view;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("%02d:",selectedHour));
                        sb.append(String.format("%02d",selectedMinute));
                        tv.setText(sb.toString());
                        if(userProperties != null) userProperties.setWaterNotificationUntil(sb.toString());
//                        setWaterInterval(fromHourTv.getText() != null ? fromHourTv.getText().toString() : "00:00", untilHourTv.getText() != null ? untilHourTv.getText().toString() : "00:00");
                    }
                }, 20, 00, true);
                mTimePicker.setTitle(getString(R.string.choose_time_heb));
                mTimePicker.show();
            }
        });

        final String[] waterReminderValues = {"10","15","20","30","60","90","120"};
        frequencyHourTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView tv = (TextView)view;
                new NumberPickerDialog(getActivity(), getString(R.string.frequency_of_water_reminder_heb), waterReminderValues, 4, R.color.colorDarkPink, R.drawable.dark_pink_button, new NumberPickerDialog.NumberPickerDialogCallback() {
                    @Override
                    public void sendValue(int selectedValue) {
                        tv.setText(String.valueOf(selectedValue));
                    }
                });
            }
        });


    }

    private boolean addDayOfTraining(int day, ArrayList<String> schedule) {
        int dayBefore, dayAfter;
        if(day == 0){ //sunday
            dayBefore = 6;
            dayAfter = 1;

        } else if(day == 6){ //saturday
            dayBefore = 5;
            dayAfter = 0;

        } else { //mon-friday
            dayBefore = day+1;
            dayAfter = day-1;
        }
        if(schedule.get(dayBefore).matches("-")  && schedule.get(dayAfter).matches("-")){
            replaceDayOfTraining(day,"-",schedule);
            return true;
        } else {
            Dialogs.getAlertDialog(getActivity(), getString(R.string.can_not_set_reminder_day_after_day_heb), getString(R.string.can_not_set_reminder)).show();
            return false;
        }
    }

    private boolean addDayOfExercise(int day, ArrayList<String> schedule) {
        replaceDayOfTraining(day,"-",schedule);
        return true;
    }

    private void removeDayOfTraining(int day, ArrayList<String> schedule){
        replaceDayOfTraining(day,"-", schedule);
    }

    private void replaceDayOfTraining(int day, String value, ArrayList<String> schedule){
        schedule.remove(day);
        schedule.add(day, value);
    }


    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void collapseAll() {

        biOpenTraining.setTag(STR_FALSE);
        collapse(llTrainings);
        biOpenTraining.setBackgroundResource(R.drawable.arrow_down_purple);

        biOpenExercise.setTag(STR_FALSE);
        collapse(llExercises);
        biOpenExercise.setBackgroundResource(R.drawable.arrow_down_blue);

        biOpenWater.setTag(STR_FALSE);
        collapse(llDrinks);
        biOpenWater.setBackgroundResource(R.drawable.arrow_down_pink);

    }


    @Override
    public void storagePermissionGranted() {

    }


    private void save(){
        if(! NetworkCenterHelper.isNetworkAvailable(getContext())){
            Dialogs.getAlertDialog(getContext(), getString(R.string.cannot_connect_to_server), getString(R.string.no_internet_connection)).show();
        } else {
            UserProperties properties = new UserProperties(getContext());
            //trainings
            properties.setTrainingReminderSoundPath(this.trainingChosenRingtonePath);
            properties.setTrainingReminderSoundName(this.trainingChosenRingtoneName);
            properties.setTrainingSchedule(this.trainingSchedule);
            properties.clearTrainingReminder();

            if (llReminderTraining1.getVisibility() == View.VISIBLE) {
                firstReminderTraining = getIntValueFromSelection(spReminderTraining1.getSelectedItemPosition());
                properties.addTrainingReminder(firstReminderTraining);
            }
            if (llReminderTraining2.getVisibility() == View.VISIBLE) {
                secondReminderTraining = getIntValueFromSelection(spReminderTraining2.getSelectedItemPosition());
                properties.addTrainingReminder(secondReminderTraining);
            }

            //exercise
            properties.setExerciseReminderSoundPath(this.exerciseChosenRingtonePath);
            properties.setExerciseReminderSoundName(this.exerciseChosenRingtoneName);
            properties.setExerciseSchedule(this.exerciseSchedule);
            properties.clearExerciseReminders();
            if (llReminderExercise1.getVisibility() == View.VISIBLE) {
                firstReminderExercise = getIntValueFromSelection(spReminderExercise1.getSelectedItemPosition());
                properties.addExerciseReminder(firstReminderExercise);
            }
            if (llReminderExercise2.getVisibility() == View.VISIBLE) {
                secondReminderExercise = getIntValueFromSelection(spReminderExercise2.getSelectedItemPosition());
                properties.addExerciseReminder(secondReminderExercise);
            }

            //water

            if(swOnOffWater.isChecked() && frequencyHourTv.getText() != null) {
                String intervalStr = frequencyHourTv.getText().toString();
                if(!intervalStr.matches("00")) {
                    String from = fromHourTv.getText().toString();
                    String until = untilHourTv.getText().toString();
                    int interval = 0;
                        try {
                            interval = Integer.parseInt(intervalStr);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Crashlytics.log(ex.toString());
                        }
                    properties.setWaterNotificationFrom(from);
                    properties.setWaterNotificationUntil(until);
                    properties.setWaterNotificationInterval(interval);
                    properties.setWaterReminderSoundPath(this.waterChosenRingtonePath);
                    properties.setWaterReminderSoundName(this.waterChosenRingtoneName);

                }

            } else {
                properties.setWaterReminderSoundPath(null);
                properties.setWaterReminderSoundName(null);
                properties.setWaterNotificationFrom(null);
                properties.setWaterNotificationUntil(null);
                properties.setWaterNotificationInterval(0);
            }

            ConnectedUser.getInstance().setProperties(properties);
            properties.saveToSharedPreferences(getContext());

            //save to backend
            saveToBackend(properties);


//            Activity activity = getActivity();
//            if (activity != null && activity instanceof MainActivity) {
//                ((MainActivity) activity).onBackPressed();
//            }

        }

    }

    private boolean back_from_server_1 = false, back_from_server_2 = false, back_from_server_3 = false;
    private boolean failure_notified = false;
    private Dialog progressDialog= null;
    private void saveToBackend(UserProperties properties) {
        progressDialog = Dialogs.showSpinningWheelDialog(getActivity());
        progressDialog.show();

        NetworkCenterHelper.saveTrainingNotificationDays(getContext(), ConnectedUser.getInstance(), new PostResponseCallback() {
            @Override
            public void requestStarted() {
                failure_notified = false;
                back_from_server_1 = false;
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestStarted TrainingNotificationDays");
            }

            @Override
            public void requestCompleted(String response) {
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestCompleted TrainingNotificationDays");
                Log.d(SetDayAndTimePracticeFragment.class.getName(), response);

                back_from_server_1 = true;
                if(back_from_server_1 && back_from_server_2 && back_from_server_3) {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    Dialogs.getAlertDialog(getActivity(), getString(R.string.notifications_updated_heb), null, new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
//                            Activity activity = getActivity();
//                            if (activity != null && activity instanceof MainActivity) {
//                                ((MainActivity) activity).onBackPressed();
//                            }
                            collapseAll();
                        }
                    }).show();
                }


            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                int networkcode = -1;
                if(error != null) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null){
                        networkcode = networkResponse.statusCode;
                    }
//                    if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
//                        // HTTP Status Code: 401 Unauthorized
//                    }
                    error.printStackTrace();
                }

                if(!failure_notified ) {
                    failure_notified = true;
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Dialogs.getAlertDialog(getActivity().getApplicationContext(), getString(R.string.notifications_not_updated_heb) + "\n" + " Status Code: " + networkcode, null).show();
                }
            }

        });

        NetworkCenterHelper.saveNotificationRemindersSettings(getContext(), ConnectedUser.getInstance(), new PostResponseCallback() {
            @Override
            public void requestStarted() {
                failure_notified = false;
                back_from_server_2 = false;
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestStarted TrainingNotificationReminders");
            }

            @Override
            public void requestCompleted(String response) {
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestCompleted TrainingNotificationReminders");
                Log.d(SetDayAndTimePracticeFragment.class.getName(), response);
                back_from_server_2 = true;
                if(back_from_server_1 && back_from_server_2 && back_from_server_3) {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.notifications_updated_heb), null, new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            //                            Activity activity = getActivity();
//                            if (activity != null && activity instanceof MainActivity) {
//                                ((MainActivity) activity).onBackPressed();
//                            }
                            collapseAll();
                        }
                    }).show();
                }
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestEndedWithError TrainingNotificationReminders");
                int networkcode = -1;
                if(error != null) {
                    NetworkResponse networkResponse = error.networkResponse;
                    networkcode = networkResponse.statusCode;
//                    if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
//                        // HTTP Status Code: 401 Unauthorized
//                    }
                    error.printStackTrace();
                }

                if(!failure_notified ) {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    failure_notified = true;
                    Dialogs.getAlertDialog(getActivity().getApplicationContext(), getString(R.string.notifications_not_updated_heb) + "\n" + " Status Code: " + networkcode, null).show();
                }
            }

        });

        NetworkCenterHelper.saveExerciseNotificationDays(getContext(), ConnectedUser.getInstance(), new PostResponseCallback() {
            @Override
            public void requestStarted() {
                failure_notified = false;
                back_from_server_3 = false;
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestStarted ExerciseNotificationDays");
            }

            @Override
            public void requestCompleted(String response) {
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestCompleted ExerciseNotificationDays");
                Log.d(SetDayAndTimePracticeFragment.class.getName(), response);
                back_from_server_3 = true;
                if(back_from_server_1 && back_from_server_2 && back_from_server_3) {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.notifications_updated_heb), null, new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            //                            Activity activity = getActivity();
//                            if (activity != null && activity instanceof MainActivity) {
//                                ((MainActivity) activity).onBackPressed();
//                            }
                            collapseAll();
                        }
                    }).show();
                }
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.d(SetDayAndTimePracticeFragment.class.getName(), "@@@@@@@@@@@@@ @@@@@@@@@@@@@ @@@@@@@@@@@@@ requestEndedWithError ExerciseNotificationDays");
                int networkcode = -1;
                if(error != null) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null ) networkcode = networkResponse.statusCode;
                    error.printStackTrace();
                }
                if(!failure_notified ) {
                    failure_notified = true;
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Dialogs.getAlertDialog(getActivity().getApplicationContext(), getString(R.string.notifications_not_updated_heb) + "\n" + " Status Code: " + networkcode, null).show();
                }
            }

        });
    }



    private void setWaterNotificationEnabled(String from, String to){
        if(from == null || to == null || (from.matches("00:00") && to.matches("00:00"))){
            enableWaterSetting(false);
            //disable
//            if (android.os.Build.VERSION.SDK_INT >= 23){
//                frequencyHourTv.setTextColor(this.getContext().getColor(R.color.colorLightGray));
//            } else{
//                frequencyHourTv.setTextColor(this.getContext().getResources().getColor(R.color.colorLightGray));
//            }
//            frequencyHourTv.setEnabled(false);
        } else {
            // enable
            enableWaterSetting(true);
//            frequencyHourTv.setEnabled(true);
//            if (android.os.Build.VERSION.SDK_INT >= 23){
//                frequencyHourTv.setTextColor(this.getContext().getColor(R.color.colorDarkPink));
//            } else{
//                frequencyHourTv.setTextColor(this.getContext().getResources().getColor(R.color.colorDarkPink));
//            }
        }
    }

    private int getIntValueFromSelection(int selectedPosition) {
        if(selectedPosition == 0){
            return 0;
        } else if(selectedPosition == 1){
            return 10;
        } else if(selectedPosition == 2){
            return 30;
        } else if(selectedPosition == 3){
            return 60;
        } else if(selectedPosition == 4){
            return 120;
        } else {
            return -1;
        }
    }

    private int getPositionPerValue(int value){
        switch (value){
            case 0: return 0;
            case 10: return 1;
            case 30: return 2;
            case 60: return 3;
            case 120: return 4;
            default: return 0;
        }
    }

    private String getStringValueFromInt(Integer integer) {
        if(integer == null || integer == 0) {
            return getString(R.string.at_time_heb);
        } else if(integer == 10){
            return getString(R.string.ten_mins_before_heb);
        } else if(integer == 30){
            return getString(R.string.thirtey_mins_before_heb);
        } else if(integer == 60){
            return getString(R.string.one_hour_before_heb);
        } else if(integer == 120){
            return getString(R.string.two_hours_before_heb);
        }  else {
            return getString(R.string.at_time_heb);
        }
    }

    private void SetUpFragment(){
        if(userProperties == null){
            if(ConnectedUser.getInstance() != null && ConnectedUser.getInstance().getProperties() != null) {
                this.userProperties = ConnectedUser.getInstance().getProperties();
            } else {
                this.userProperties = new UserProperties(getContext());
            }
        }

        //training
        trainingChosenRingtoneName = this.userProperties.getTrainingReminderSoundName();
        trainingChosenRingtonePath = this.userProperties.getTrainingReminderSoundPath();

        if(trainingChosenRingtoneName == null || trainingChosenRingtoneName.isEmpty() ){
            soundTrainingReminderTv.setText(getString(R.string.no_ringtone_heb));
        } else {
            soundTrainingReminderTv.setText(trainingChosenRingtoneName);
        }
        this.trainingSchedule = this.userProperties.getTrainingSchedule();
        if( trainingSchedule == null) {
            trainingSchedule = new ArrayList<String>();
            while (trainingSchedule.size() < 7) trainingSchedule.add("-");
        }


        ArrayList<Integer> tSchedule = this.userProperties.getTrainingRemindersBefore();
        if(tSchedule != null) {

            if(tSchedule.size() > 0 && tSchedule.get(0) != null){
                firstReminderTraining = tSchedule.get(0);
                if(firstReminderTraining > -1) {
                    llReminderTraining1.setVisibility(View.VISIBLE);
                    llAddReminderTraining.setVisibility(View.VISIBLE);
                    spReminderTraining1.setSelection(getPositionPerValue(firstReminderTraining));
                }
            }
            if(tSchedule.size() > 1 && tSchedule.get(1) != null){
                secondReminderTraining = tSchedule.get(1);
                if(secondReminderTraining != firstReminderTraining && secondReminderTraining > -1) {
                    llReminderTraining2.setVisibility(View.VISIBLE);
                    llAddReminderTraining.setVisibility(View.GONE);
                    spReminderTraining2.setSelection(getPositionPerValue(secondReminderTraining));
                }
            }
        }

        //exercise
        exerciseChosenRingtoneName = this.userProperties.getExerciseReminderSoundName();
        exerciseChosenRingtonePath = this.userProperties.getExerciseReminderSoundPath();
        if(exerciseChosenRingtoneName == null || exerciseChosenRingtoneName.isEmpty() ){
            soundExerciseReminderTv.setText(getString(R.string.no_ringtone_heb));
        } else {
            soundExerciseReminderTv.setText(exerciseChosenRingtoneName);
        }
        this.exerciseSchedule = this.userProperties.getExerciseSchedule();
        if( exerciseSchedule == null) {
            exerciseSchedule = new ArrayList<String>();
            while (exerciseSchedule.size() < 7) exerciseSchedule.add("-");
        }
        ArrayList<Integer> eSchedule = this.userProperties.getExerciseRemindersBefore();
        if(eSchedule != null) {

            if(eSchedule.size() > 0 && eSchedule.get(0) != null){
                firstReminderExercise = eSchedule.get(0);
                if(firstReminderExercise > -1) {
                    llReminderExercise1.setVisibility(View.VISIBLE);
                    llAddReminderExercise.setVisibility(View.VISIBLE);
                    spReminderExercise1.setSelection(getPositionPerValue(firstReminderExercise));
                }

            }
            if(eSchedule.size() > 1 && eSchedule.get(1) != null){
                secondReminderExercise = eSchedule.get(1);
                if(secondReminderExercise != firstReminderExercise && secondReminderExercise > -1) {
                    llReminderExercise2.setVisibility(View.VISIBLE);
                    llAddReminderExercise.setVisibility(View.GONE);
                    spReminderExercise2.setSelection(getPositionPerValue(secondReminderExercise));
                }

            }
        }

        //water
        if(userProperties.isWaterNotificationOn()){
            waterChosenRingtoneName = this.userProperties.getWaterReminderSoundName();
            waterChosenRingtonePath = this.userProperties.getWaterReminderSoundPath();
            if(waterChosenRingtoneName == null || waterChosenRingtoneName.isEmpty() ){
                soundWaterReminderTv.setText(getString(R.string.no_ringtone_heb));
            } else {
                soundWaterReminderTv.setText(waterChosenRingtoneName);
            }
            fromHourTv.setText(this.userProperties.getWaterNotificationFrom());
            untilHourTv.setText(this.userProperties.getWaterNotificationUntil());
            frequencyHourTv.setText(String.valueOf(this.userProperties.getWaterNotificationInterval()));
        }
        setWaterNotificationEnabled(this.userProperties.getWaterNotificationFrom(), this.userProperties.getWaterNotificationUntil());
    }

//    private boolean didTrainingNotificationChange(){
//
//      return true;
//    }
//
//    private boolean didExerciseNotificationChange(){
//
//        return true;
//    }
//
//    private boolean didWaterNotificationChange(){
//
//        return true;
//    }

    private void openRingtoneChooser(int requestCode){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        String chosenRingtone = null;
        switch (requestCode){
            case TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE:
                chosenRingtone = this.trainingChosenRingtonePath;
                break;
            case EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE:
                chosenRingtone = this.exerciseChosenRingtonePath;
                break;
            case WATER_NOTIFICATION_RINGTON_REQUEST_CODE:
                chosenRingtone = this.waterChosenRingtonePath;
                break;
        }
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (chosenRingtone == null) ? (Uri)null :Uri.parse(chosenRingtone));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent){
        Log.d("SetDayAndTimePracticeF", "SetDayAndTimePracticeFragment got it!!!!!!!!!!!");
        if (resultCode == Activity.RESULT_OK ){
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            setRingtone(uri, requestCode);
        }
    }

    public void setRingtone(Uri uri, int requestCode){
        if (uri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
            switch (requestCode) {

                case TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE:
                    this.trainingChosenRingtonePath = uri.toString();
                    this.trainingChosenRingtoneName = ringtone.getTitle(getContext());
                    soundTrainingReminderTv.setText(this.trainingChosenRingtoneName);
                    break;

                case EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE:
                    this.exerciseChosenRingtonePath = uri.toString();
                    this.exerciseChosenRingtoneName = ringtone.getTitle(getContext());
                    soundExerciseReminderTv.setText(this.exerciseChosenRingtoneName);
                    break;

                case WATER_NOTIFICATION_RINGTON_REQUEST_CODE:
                    this.waterChosenRingtonePath = uri.toString();
                    this.waterChosenRingtoneName = ringtone.getTitle(getContext());
                    soundWaterReminderTv.setText(this.waterChosenRingtoneName);
                    break;

            }
        } else {
            switch (requestCode) {

                case TRAINING_NOTIFICATION_RINGTON_REQUEST_CODE:
                    this.trainingChosenRingtonePath = null;
                    this.trainingChosenRingtoneName = null;
                    soundTrainingReminderTv.setText(getString(R.string.no_ringtone_heb));
                    break;

                case EXERCISE_NOTIFICATION_RINGTON_REQUEST_CODE:
                    this.exerciseChosenRingtonePath = null;
                    this.exerciseChosenRingtoneName = null;
                    soundExerciseReminderTv.setText(getString(R.string.no_ringtone_heb));
                    break;

                case WATER_NOTIFICATION_RINGTON_REQUEST_CODE:
                    this.waterChosenRingtonePath = null;
                    this.waterChosenRingtoneName = null;
                    soundWaterReminderTv.setText(getString(R.string.no_ringtone_heb));
                    break;

            }
        }
    }

}

