package com.arefly.sleep.formatters;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by eflyjason on 22/8/2016.
 */
public class DurationHourAxisValueFormatter implements AxisValueFormatter {
    private BarLineChartBase<?> chart;

    public DurationHourAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        float hours = (float) Math.floor(value);
        float minutes = (value % 1) * 60;
        if (minutes == 0) {
            return String.format(Locale.US, "%.0fh", hours);
        } else {
            return String.format(Locale.US, "%.0fh%sm", hours, new DecimalFormat("00").format(minutes));
        }
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}