package com.arefly.sleep.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arefly.sleep.R;
import com.orhanobut.logger.Logger;

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

}