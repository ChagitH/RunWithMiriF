package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forst.miri.runwithme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionsFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = QuestionsFragment.class.getSimpleName();

    public QuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questions, container, false);
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
        return R.layout.fragment_questions;
    }

}
