package com.arefly.sleep;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class StatisticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("StatisticActivity onCreate()");
        setContentView(R.layout.activity_statistic);

        Intent screenServiceIntent = new Intent(this, ScreenService.class);
        startService(screenServiceIntent);
    }
}