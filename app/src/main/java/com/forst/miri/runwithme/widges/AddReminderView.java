package com.forst.miri.runwithme.widges;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.miscellaneous.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chagithazani on 12/7/17.
 */

public class AddReminderView extends LinearLayout {
    private TextView tvOriginalTextView, tvBeforeSpinner;
    private LinearLayout addReminderLinearLayout;
    private Spinner spinner;
    private SwitchCompat switcher;
    private String selectedOption;
    private ArrayAdapter<String> dataAdapter;
    private CompoundButton.OnCheckedChangeListener listener = null;

    public AddReminderView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AddReminderView, 0, 0);
        String originalText = a.getString(R.styleable.AddReminderView_originalText);
        String insideText = a.getString(R.styleable.AddReminderView_insideText);
        @SuppressWarnings("ResourceAsColor")
        int textColor = a.getColor(R.styleable.AddReminderView_reminderTextColor, R.color.colorPurple);
        boolean isReminderSet = a.getBoolean(R.styleable.AddReminderView_isReminderSet, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.add_reminder_layout, this, true);

        final Typeface font = UIHelper.getDefaultFontRegular(context);
        tvOriginalTextView = (TextView) findViewById(R.id.add_reminder_original_text_view);
        tvOriginalTextView.setText(originalText);
        tvOriginalTextView.setTextColor(textColor);
        tvOriginalTextView.setTypeface(font);

        tvBeforeSpinner = (TextView) findViewById(R.id.add_reminder_before_spinner_textview);
        tvBeforeSpinner.setText(insideText);
        tvBeforeSpinner.setTextColor(textColor);
        tvBeforeSpinner.setTypeface(font);


        addReminderLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_spinner_layout);
        spinner = (Spinner) findViewById(R.id.add_reminder_spinner);

        switcher = (SwitchCompat) findViewById(R.id.add_reminder_switch);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    tvOriginalTextView.setVisibility(GONE);
                    addReminderLinearLayout.setVisibility(VISIBLE);
                } else {
                    tvOriginalTextView.setVisibility(VISIBLE);
                    addReminderLinearLayout.setVisibility(GONE);
                }
                if(listener != null) listener.onCheckedChanged(compoundButton, checked);
            }
        });
        switcher.setChecked(isReminderSet);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOption = dataAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Spinner Drop down elements
        List<String> options = new ArrayList<String>();
        options.add(getResources().getString(R.string.at_time_heb));
        options.add(getResources().getString(R.string.ten_mins_before_heb));
        options.add(getResources().getString(R.string.thirtey_mins_before_heb));
        options.add(getResources().getString(R.string.one_hour_before_heb));
        options.add(getResources().getString(R.string.two_hours_before_heb));

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options){
            // Affects default (closed) state of the spinner
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTypeface(font);
                return view;
            }

            // Affects opened state of the spinner
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTypeface(font);
                return view;
            }
        };

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }


    public String getSelectedOption(){
        return this.selectedOption;
    }

    public void setSelectedOption(String selected){
        this.selectedOption = selected;
        if(this.selectedOption != null){
            spinner.setSelection(getIndexOfSelection(selectedOption));
        }
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        this.listener = listener;
    }
    private int getIndexOfSelection(String selectedOption) {
        return dataAdapter.getPosition(selectedOption);
    }

    public boolean isChecked(){
        return switcher.isChecked();
    }

    public void setChecked(boolean bool){
        switcher.setChecked(bool);
    }


}
