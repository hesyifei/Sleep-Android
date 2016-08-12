package com.arefly.sleep.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.arefly.sleep.R;
import com.arefly.sleep.adapters.TabPagerAdapter;
import com.arefly.sleep.fragments.DayInfoFragment;
import com.arefly.sleep.fragments.StatisticsFragment;
import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.arefly.sleep.receivers.CheckServiceAlarmReceiver;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("OverviewActivity onCreate()");
        setContentView(R.layout.activity_overview);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        //fragments.add(StatisticsFragment.newInstance(""));

        fragments.add(new DayInfoFragment());
        titles.add("Yesterday");

        fragments.add(new StatisticsFragment());
        titles.add("Statistics");

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (viewPager != null) {
            viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), titles, fragments, this));
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }


        initServiceAndAlarm(getApplicationContext());

        PreferencesHelper.setSleepTimeString("22:00", this.getApplicationContext());
        PreferencesHelper.setWakeTimeString("10:00", this.getApplicationContext());
        PreferencesHelper.setLongestIgnoreSeconds(60, this.getApplicationContext());
    }


    public static void initServiceAndAlarm(Context context) {
        GlobalFunction.startOrStopScreenServiceIntent(context);
        setCheckServiceAlarm(context);
    }

    private static void setCheckServiceAlarm(Context context) {
        AlarmManager checkServiceAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckServiceAlarmReceiver.class);
        PendingIntent checkServiceAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);


        try {
            Logger.d("checkServiceAlarmMgr cancelled");
            checkServiceAlarmMgr.cancel(checkServiceAlarmIntent);
        } catch (Exception e) {
            Logger.w("checkServiceAlarmMgr cannot be cancelled: " + e.toString());
        }



        // REAL USE:
        checkServiceAlarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, checkServiceAlarmIntent);


        // FOR TESTING ONLY:
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 45);
        //checkServiceAlarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, checkServiceAlarmIntent);
        //checkServiceAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 6000, checkServiceAlarmIntent);*/
        //checkServiceAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 6000, checkServiceAlarmIntent);
        //checkServiceAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 15000, checkServiceAlarmIntent);
    }
}