package com.arefly.sleep.activities;

/**
 * Created by eflyjason on 17/8/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.arefly.sleep.data.helpers.SleepDurationRecordHelper;
import com.arefly.sleep.fragments.DayInfoFragment;
import com.orhanobut.logger.Logger;

public class EachRecordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("EachRecordActivity onCreate()");

        setTitle("Record");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID)) {
                String dateString = extras.getString(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID);

                setTitle(dateString);


                Fragment fragment = new DayInfoFragment();

                Bundle bundle = new Bundle();
                bundle.putString(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID, dateString);
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}