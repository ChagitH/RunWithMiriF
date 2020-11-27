package com.forst.miri.runwithme.miscellaneous;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by chagithazani on 11/20/17.
 */

public class ChartValueFormatter implements IAxisValueFormatter {


    public ChartValueFormatter() {

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return UIHelper.formatTimeFromMinutes(value);
    }
}
