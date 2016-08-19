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
    }

}