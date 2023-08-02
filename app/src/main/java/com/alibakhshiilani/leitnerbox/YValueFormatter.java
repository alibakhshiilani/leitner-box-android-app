package com.alibakhshiilani.leitnerbox;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class YValueFormatter implements IAxisValueFormatter
{

    private DecimalFormat mFormat;

    public YValueFormatter() {
        mFormat = new DecimalFormat("###########");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value)+" کارت";
    }
}