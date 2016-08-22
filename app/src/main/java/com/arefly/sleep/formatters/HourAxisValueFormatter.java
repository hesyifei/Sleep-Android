package com.arefly.sleep.formatters;

import com.arefly.sleep.helpers.GlobalFunction;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 * Created by eflyjason on 22/8/2016.
 */
public class HourAxisValueFormatter implements AxisValueFormatter {
    private BarLineChartBase<?> chart;

    public HourAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return GlobalFunction.getTimeStringFromSecondsSinceMidNight((long) value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}