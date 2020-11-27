package com.forst.miri.runwithme.fragments;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.objects.MinimalLocation;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.objects.Rate;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class RateAndHeightsGraphFragment extends Fragment {

    private PracticeData practiceData = null;
    private CombinedChart mChart;
    private boolean practiceDataSet = false, viewReady = false;
    private List<Long> xArray = null;
    private TextView tvRate, tvHeight;

    public RateAndHeightsGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rate_and_heights_graph, container, false);
        mChart = (CombinedChart) rootView.findViewById(R.id.chart);
        tvRate = (TextView) rootView.findViewById(R.id.tv_rate);
        tvHeight = (TextView) rootView.findViewById(R.id.tv_height);

        practiceData = PracticeData.readFromSharedPreferences(getContext(), PracticeDataFragment.P_DATA_TO_PASS);

        if(practiceData != null && practiceData.getLocations() != null && practiceData.getLocations().size() > 0) {
            xArray = new ArrayList<>();
            long baseTimeStamp = practiceData.getLocations().get(0).getTimeStamp();
            for (MinimalLocation loc : practiceData.getLocations()) {
                xArray.add(loc.getTimeStamp() - baseTimeStamp);
            }
        }

        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
//        mChart.setBackgroundResource(R.color.colorLightPink);
//        mChart.setGridBackgroundColor(R.color.colorLightPink);
        mChart.setDrawGridBackground(true);
        mChart.setGridBackgroundColor(Color.parseColor("#ef90b5"));

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);

//        mChart.setDrawBarShadow(false);
//        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{ CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularity(1f);
        rightAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                /*
                Present or not the elevation axis?
                the relation is correct, but I am not sure the exact num is correct
                 */
                //return String.valueOf((int)value);
                return String.format("%.2f", value);
                //return "";
            }
        });



        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                Long timestamp = xArray.get((int)value);
                return UIHelper.formatTime(timestamp);
            }
        });



        CombinedData data = new CombinedData();

        data.setData(generateBarData());
        data.setData(generateLineData());

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Rubik-Regular.ttf");
        data.setValueTypeface(typeface);


        mChart.setData(data);
        mChart.invalidate();

        return rootView;
    }

    private LineData generateLineData() {

        if(practiceData == null) return null;
        ArrayList<MinimalLocation> locations = practiceData.getLocations();
        if(locations == null) return null;
        if(locations.size() < 1) return null;



        //no elevation yet
        if(locations.get(0).getElevation() == MinimalLocation.NO_ELEVATION) {
            mChart.getAxisRight().setEnabled(false);
            return null;
        }

        tvHeight.setVisibility(View.VISIBLE);
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

//        long baseTimeStamp = locations.get(0).getTimeStamp();
//        float baseElevation = (float)locations.get(0).getElevation();

        //for(MinimalLocation loc : locations){
        for (int index = 0; index < locations.size(); index++) {
            MinimalLocation loc = locations.get(index);
            //entries.add(new Entry(index, ((float) loc.getElevation()-baseElevation)));
            entries.add(new Entry(index, ((float) loc.getElevation())));
        }

        LineDataSet set = new LineDataSet(entries, getString(R.string.elevation_heb));
        set.setColor(ContextCompat.getColor(getContext(), R.color.colorPurple));
        set.setLineWidth(1.5f);
        set.setDrawCircles(false);
//        set.setCircleColor(Color.rgb(240, 238, 70));
//        set.setCircleRadius(5f);
//        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawValues(false);
//        set.setValueTextSize(10f);
//        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {
        if(practiceData == null) return null;
        ArrayList<Rate> rates = practiceData.getAllRates();
        if(rates == null) return null;
        if(rates.size() < 1) return null;


        tvRate.setVisibility(View.VISIBLE);
        setMinMaxRate(rates);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(minVal);
        leftAxis.setInverted(true);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return UIHelper.formatTimeToMinutes((long)value);
            }
        });


        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int index = 0; index < rates.size(); index++) {
            Rate rate = rates.get(index);
            if(rate.getRateInMillies() > 0) { //rate 0 has not meaning..
//                entries.add(new BarEntry(index, maxVal - rate.getRateInMillies()));
                entries.add(new BarEntry(index, rate.getRateInMillies()));
            }
        }

        BarDataSet set1 = new BarDataSet(entries, getString(R.string.rate_heb));

//        set1.setColor(ContextCompat.getColor(getContext(), R.color.colorLightPink));
        set1.setColor(ContextCompat.getColor(getContext(), R.color.colorWhite));



        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData d = new BarData(set1);
        d.setBarWidth(1f);


        return d;
    }

    private long minVal, maxVal;
    private void setMinMaxRate(ArrayList<Rate> rates) {
        if(rates == null || rates.size() < 1) return ;
        minVal = maxVal = rates.get(0).getRateInMillies();
        for (Rate rate : rates) {
            if (rate.getRateInMillies() > maxVal) {
                maxVal = rate.getRateInMillies();
            } else if (rate.getRateInMillies() < minVal) {
                minVal = rate.getRateInMillies();
            }
        }
    }

    public void setPracticeData(PracticeData pData){
        //pData = PracticeData.readFromSharedPreferences(getContext(), PracticeDataFragment.P_DATA_TO_PASS);
        if (pData != null) {
            this.practiceData = pData;
            practiceDataSet = true;
            //setUpScreen();
        }
    }



}
