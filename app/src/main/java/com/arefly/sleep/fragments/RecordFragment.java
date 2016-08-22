package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arefly.sleep.R;
import com.arefly.sleep.activities.EachRecordActivity;
import com.arefly.sleep.activities.MainActivity;
import com.arefly.sleep.data.helpers.SleepDurationRecordHelper;
import com.orhanobut.logger.Logger;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by eflyjason on 15/8/2016.
 */
public class RecordFragment extends Fragment implements OnDateSelectedListener {

    private AppCompatActivity mainActivity;

    private MaterialCalendarView widget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("RecordFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("RecordFragment onCreateView()");
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.i("RecordFragment onViewCreated()");

        // TODO: add sleep time below the date button in calendar

        mainActivity = (AppCompatActivity) getActivity();
        MainActivity.setupDrawer(mainActivity, view);
        mainActivity.setTitle(getString(R.string.record_fragment_name));


        widget = (MaterialCalendarView) view.findViewById(R.id.calendar_view);
        widget.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        Logger.v("RecordFragment onDateSelected(" + date + ")");

        /*final FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        Fragment fragment = new DayInfoFragment();

        // put the fragment in place
        transaction.replace(R.id.record_main_frame, fragment);

        // this is the part that will cause a fragment to be added to backstack,
        // this way we can return to it at any time using this tag
        transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();*/

        final Date dateToBeCheckedDate = date.getDate();

        // Delay here to make smoother animation
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(mainActivity, EachRecordActivity.class);

                DateFormat dateFormat = SleepDurationRecordHelper.SIMPLE_DATE_FORMAT;
                String dateToBeChecked = dateFormat.format(dateToBeCheckedDate);
                intent.putExtra(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID, dateToBeChecked);
                startActivity(intent);
            }
        }, 100);


    }

}