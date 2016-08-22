package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.graphics.Color;
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
import com.arefly.sleep.formatters.DurationHourAxisValueFormatter;
import com.arefly.sleep.formatters.HourAxisValueFormatter;
import com.arefly.sleep.helpers.GlobalFunction;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

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


        CombinedChart mChart = (CombinedChart) view.findViewById(R.id.statistics_chart);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("Nothing yet :)");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(false);
        mChart.setBackgroundColor(Color.TRANSPARENT);


        AxisValueFormatter yLineAxisFormatter = new HourAxisValueFormatter(mChart);
        AxisValueFormatter yBarAxisFormatter = new DurationHourAxisValueFormatter(mChart);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setValueFormatter(yBarAxisFormatter);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setGranularity(0.5f);          // 0.5h=30m


        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setValueFormatter(yLineAxisFormatter);
        rightAxis.setGranularity(120f);         // 120s=2m





        AxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        CombinedData chartData = SleepDurationRecordHelper.setCombinedData(new CombinedData(), allSleepDurationRecordInDays);
        if (chartData != null) {
            mChart.setData(chartData);
            mChart.getData().setHighlightEnabled(false);
        }
        mChart.animateY(3000);
        mChart.invalidate();
    }

}