package com.arefly.sleep.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arefly.sleep.R;
import com.arefly.sleep.data.helpers.SleepDurationRecordHelper;
import com.arefly.sleep.data.objects.SleepDurationRecord;
import com.arefly.sleep.helpers.GlobalFunction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eflyjason on 10/8/2016.
 */
public class StatisticsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("StatisticsFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("StatisticsFragment onCreateView()");

        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.i("DayInfoFragment onCreateView()");

        TextView upperLabel = (TextView) view.findViewById(R.id.statistics_upper_label);
        TextView upperLabelSmall = (TextView) view.findViewById(R.id.statistics_upper_label_small);
        TextView lowerLeftLabel = (TextView) view.findViewById(R.id.statistics_lower_left_label);
        TextView lowerLeftLabelSmall = (TextView) view.findViewById(R.id.statistics_lower_left_label_small);
        TextView lowerRightLabel = (TextView) view.findViewById(R.id.statistics_lower_right_label);
        TextView lowerRightLabelSmall = (TextView) view.findViewById(R.id.statistics_lower_right_label_small);


        // TODO: startDate after -7 day 00:00 (e.g.)
        Realm realm = Realm.getDefaultInstance();

        SleepDurationRecordHelper.removeAllRepeatingDate(realm);


        Calendar toBeCheckedStartTimeCal = GregorianCalendar.getInstance();
        toBeCheckedStartTimeCal.set(Calendar.MILLISECOND, 0);
        toBeCheckedStartTimeCal.set(Calendar.SECOND, 0);
        toBeCheckedStartTimeCal.set(Calendar.MINUTE, 0);
        toBeCheckedStartTimeCal.set(Calendar.HOUR_OF_DAY, 0);
        toBeCheckedStartTimeCal.add(Calendar.DATE, -7);


        RealmResults<SleepDurationRecord> allSleepDurationRecordInDays = realm.where(SleepDurationRecord.class)
                .greaterThanOrEqualTo("startTime", toBeCheckedStartTimeCal.getTime())
                .findAllSorted("startTime", Sort.ASCENDING);

        Logger.v("allSleepDurationRecordInDays: " + allSleepDurationRecordInDays);


        SleepDurationRecordHelper.StatisticsData statisticsData = SleepDurationRecordHelper.getStatisticsData(allSleepDurationRecordInDays);

        long averageSleepDuration = statisticsData.getAverageSleepDuration();
        Logger.v("averageSleepDuration: " + averageSleepDuration);

        String averageSleepDurationString, averageStartTimeString, averageEndTimeString;
        if (averageSleepDuration == -1) {
            averageSleepDurationString = "N/A";
            averageStartTimeString = "N/A";
            averageEndTimeString = "N/A";
        } else {
            averageSleepDurationString = GlobalFunction.getHoursAndMinutesString(averageSleepDuration, false, true, getContext());
            averageStartTimeString = statisticsData.getAverageStartTime();
            averageEndTimeString = statisticsData.getAverageEndTime();
        }


        upperLabel.setText(averageSleepDurationString);
        upperLabelSmall.setText("Average Sleep Duration");

        lowerLeftLabel.setText(averageStartTimeString);
        lowerLeftLabelSmall.setText("Average Start Sleep Time");

        lowerRightLabel.setText(averageEndTimeString);
        lowerRightLabelSmall.setText("Average End Sleep Time");



        LineChart chart = (LineChart) view.findViewById(R.id.statistics_line_chart);
        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, 5));
        entries.add(new Entry(1, 8));

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[]{"Q1", "Q2", "Q3", "Q4"};

        AxisValueFormatter formatter = new AxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

    }

}