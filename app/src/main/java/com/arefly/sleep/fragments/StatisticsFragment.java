package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.arefly.sleep.R;
import com.arefly.sleep.data.helpers.SleepDurationRecordHelper;
import com.arefly.sleep.data.objects.SleepDurationRecord;
import com.arefly.sleep.formatters.DayAxisValueFormatter;
import com.arefly.sleep.helpers.GlobalFunction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.orhanobut.logger.Logger;

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


        Spinner dropdown = (Spinner) view.findViewById(R.id.statistics_spinner);
        String[] items = new String[]{"Last Week", "Last Month", "Last Year", "All Time"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_statistics, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(0, true);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Logger.v("onItemSelected " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


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
            averageSleepDurationString = GlobalFunction.getHoursAndMinutesString(averageSleepDuration, false, true, getActivity().getApplicationContext());
            averageStartTimeString = statisticsData.getAverageStartTime();
            averageEndTimeString = statisticsData.getAverageEndTime();
        }


        upperLabel.setText(averageSleepDurationString);
        upperLabelSmall.setText("Average Sleep Duration");

        lowerLeftLabel.setText(averageStartTimeString);
        lowerLeftLabelSmall.setText("Average Start Sleep Time");

        lowerRightLabel.setText(averageEndTimeString);
        lowerRightLabelSmall.setText("Average End Sleep Time");


        BarChart chart = (BarChart) view.findViewById(R.id.statistics_chart);
        List<BarEntry> entries = SleepDurationRecordHelper.getSleepDurationList(allSleepDurationRecordInDays);

        AxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
        BarData lineData = new BarData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

}