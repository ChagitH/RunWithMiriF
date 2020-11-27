package com.forst.miri.runwithme.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.objects.Logger;
import com.forst.miri.runwithme.widges.SwitchPlus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = SettingsFragment.class.getSimpleName();
    public static final String AUDIO_STRIPS_DURING_FREE_RUN_SETTINGS_KEY = "audio_while_free_run";


    private Button sendLogButton;
    private SwitchPlus switchPlus;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_settings, container, false);

        switchPlus = (SwitchPlus) parentView.findViewById(R.id.settings_switch);
        switchPlus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveToSharedPreferences(b);
            }
        });


        sendLogButton = (Button)parentView.findViewById(R.id.side_menu_button_send_log);

        return parentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        sendLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri logUri = Logger.getLogUri(getContext());
                if (logUri != null){
                    try {
                        Intent intent = new Intent();
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.run_with_email)});
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Log");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, logUri);
//                        intent.setData(logUri);
                        intent.setType("text/plain");
//                        intent.setType("text/*");
                        startActivity(Intent.createChooser(intent, getString(R.string.send_log_heb)));
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                } else {
                    Log.d(PostRunFragment.class.getName()," =================== logUri == null ===============================");
                }
            }
        });


        switchPlus.setChecked(getListenToAudioDuringRun(getContext()));
    }

    private void saveToSharedPreferences(boolean audio_during_run) {
        Context context = getContext();
        if(context != null){
            Log.i("********************", "********************************* " + audio_during_run+ " *************");
            SharedPreferences sp = context.getSharedPreferences(SettingsFragment.class.getSimpleName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(AUDIO_STRIPS_DURING_FREE_RUN_SETTINGS_KEY, audio_during_run);
            editor.apply();
        }
    }



    @Override
    public void storagePermissionGranted() {

    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_settings;
    }

    public static boolean getListenToAudioDuringRun(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SettingsFragment.class.getSimpleName(), Context.MODE_PRIVATE);
        try {
            return sp.getBoolean(SettingsFragment.AUDIO_STRIPS_DURING_FREE_RUN_SETTINGS_KEY, true);
        }catch (Exception ex){
            return true;
        }
    }
}
