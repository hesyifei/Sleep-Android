package com.arefly.sleep.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arefly.sleep.R;
import com.arefly.sleep.activities.MainActivity;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 15/8/2016.
 */
public class RecordFragment extends Fragment {

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

        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        MainActivity.setupDrawer(mainActivity, view);
        mainActivity.setTitle(getString(R.string.record_fragment_name));

    }

}