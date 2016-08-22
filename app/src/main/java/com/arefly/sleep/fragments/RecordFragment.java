package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
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
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by eflyjason on 15/8/2016.
 */
public class RecordFragment extends Fragment implements OnDateSelectedListener {

    private AppCompatActivity mainActivity;

    private MaterialCalendarView calendarView;

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

        Realm realm = Realm.getDefaultInstance();


        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangedListener(this);
        calendarView.addDecorator(new HighlightDatesDecorator(Color.LTGRAY, SleepDurationRecordHelper.getAllAvailableDate(realm)));
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        Logger.v("RecordFragment onDateSelected(" + date + ")");

        final MaterialCalendarView thisCalendarView = widget;

        final Date dateToBeCheckedDate = date.getDate();

        // Delay here to make smoother animation
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(mainActivity, EachRecordActivity.class);

                DateFormat dateFormat = SleepDurationRecordHelper.SIMPLE_DATE_FORMAT;
                String dateToBeChecked = dateFormat.format(dateToBeCheckedDate);
                intent.putExtra(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID, dateToBeChecked);
                startActivity(intent);

                thisCalendarView.clearSelection();
            }
        }, 100);

    }

    private class HighlightDatesDecorator implements DayViewDecorator {
        private final int color;
        private final List<String> dates;

        public HighlightDatesDecorator(int color, List<String> dates) {
            this.color = color;
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(SleepDurationRecordHelper.SIMPLE_DATE_FORMAT.format(day.getDate()));
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(4, color));
        }
    }
}