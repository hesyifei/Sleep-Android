package com.arefly.sleep.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.arefly.sleep.R;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 21/8/2016.
 */
public class InnerSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("InnerSettingsFragment onCreate()");

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}