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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by eflyjason on 11/8/2016.
 */
public class DayInfoFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("DayInfoFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("DayInfoFragment onCreateView()");
        return inflater.inflate(R.layout.fragment_day_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.i("DayInfoFragment onViewCreated()");


        String sleepDurationText;

        DateFormat dateFormat = SleepDurationRecordHelper.SIMPLE_DATE_FORMAT;
        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DATE, -1);
        String dateToBeChecked = dateFormat.format(yesterdayCal.getTime());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<SleepDurationRecord> queryResult = realm.where(SleepDurationRecord.class)
                .equalTo("date", dateToBeChecked)
                .findAll();
        if (queryResult.isEmpty()) {
            sleepDurationText = "empty!!";
        } else {
            long sleepMilliseconds = queryResult.get(0).getDuration();
            sleepDurationText = GlobalFunction.getHoursAndMinutesString(sleepMilliseconds, getContext());
        }

        TextView textView = (TextView) view.findViewById(R.id.day_info_upper_label);
        textView.setText(sleepDurationText);
    }

}