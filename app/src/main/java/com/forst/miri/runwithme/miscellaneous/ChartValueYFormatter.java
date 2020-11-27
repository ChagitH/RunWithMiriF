package com.forst.miri.runwithme.miscellaneous;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by chagithazani on 11/20/17.
 */

public class ChartValueYFormatter implements IAxisValueFormatter {

    private long mBestValue= 0;
    public ChartValueYFormatter(long bestValue) {
        this.mBestValue = bestValue;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long longValue = (long)(((double)value) * (double)60 * (double)1000);
        long newValue = mBestValue-longValue;
        if(newValue < 0) newValue = 0;
//        return UIHelper.formatTimeFromMinutes(value);
        return UIHelper.formatTime(newValue);
    }
}
