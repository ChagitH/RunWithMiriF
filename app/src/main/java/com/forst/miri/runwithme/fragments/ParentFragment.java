package com.forst.miri.runwithme.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by chagithazani on 10/17/17.
 */

public abstract class ParentFragment extends Fragment {

    public abstract void storagePermissionGranted();
    public abstract String getFragmentName();
    public abstract int getResourceID();
}
