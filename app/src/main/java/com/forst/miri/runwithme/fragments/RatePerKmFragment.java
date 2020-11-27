package com.forst.miri.runwithme.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.miscellaneous.RateListAdapter;

import java.util.ArrayList;

public class RatePerKmFragment extends Fragment {

    private ArrayList<Long> rates = new ArrayList<Long>();
    private ListView ratesListView;
    //private int color;
    private Context mContext;
    private RateListAdapter adapter = null;

    public RatePerKmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Log.d(getClass().getName(), "in onCreateView() *-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*");
        View rootView = inflater.inflate(R.layout.fragment_rate_per_km, container, false);
        ratesListView = (ListView) rootView.findViewById(R.id.rates_per_km_list_view);
        adapter = new RateListAdapter(mContext, rates);//, color);
        ratesListView.setAdapter(adapter);

        updateFragment();
        return rootView;
    }

    public void setRatesArray(ArrayList<Long> ratesSent){

        Log.d(getClass().getName(), "in setRatesArray() *-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*");
        this.rates = ratesSent;
        if (adapter != null && rates != null) {
            adapter.setData(rates);
        }

        updateFragment();
    }

//    public void setColor(int color){
//        this.color = color;
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.setData(rates);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    private void updateFragment(){
        if(rates != null && adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
