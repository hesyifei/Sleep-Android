package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arefly.sleep.R;
import com.arefly.sleep.data.helpers.SleepDurationRecordHelper;
import com.arefly.sleep.data.objects.SleepDurationRecord;
import com.arefly.sleep.helpers.GlobalFunction;
import com.orhanobut.logger.Logger;

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


        String dateToBeChecked;

        Bundle arguments = getArguments();
        if (arguments != null) {
            dateToBeChecked = arguments.getString(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID);
        } else {
            dateToBeChecked = "ERROR";
        }


        Realm realm = Realm.getDefaultInstance();
        RealmResults<SleepDurationRecord> queryResult = realm.where(SleepDurationRecord.class)
                .equalTo("date", dateToBeChecked)
                .findAll();

        String sleepDurationText;
        if (queryResult.isEmpty()) {
            sleepDurationText = "empty!!";
        } else {
            long sleepMilliseconds = queryResult.get(0).getDuration();
            sleepDurationText = GlobalFunction.getHoursAndMinutesString(sleepMilliseconds, true, false, getContext());
        }

        TextView textView = (TextView) view.findViewById(R.id.day_info_upper_label);
        textView.setText(sleepDurationText);


        ImageView sleepImage = (ImageView) view.findViewById(R.id.day_info_lower_image_view);
        sleepImage.setBackgroundResource(R.drawable.sleep_animation);
        AnimationDrawable sleepAnimation = (AnimationDrawable) sleepImage.getBackground();
        sleepAnimation.start();
    }

}