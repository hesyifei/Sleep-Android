package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arefly.sleep.R;
import com.arefly.sleep.activities.MainActivity;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 21/8/2016.
 */
public class SettingsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("SettingsFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("SettingsFragment onCreateView()");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.i("SettingsFragment onViewCreated()");

        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        MainActivity.setupDrawer(mainActivity, view);
        mainActivity.setTitle(getString(R.string.settings_fragment_name));

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_frame, new InnerSettingsFragment())
                .commit();
    }
}