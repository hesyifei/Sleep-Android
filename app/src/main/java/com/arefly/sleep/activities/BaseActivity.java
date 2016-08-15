package com.arefly.sleep.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arefly.sleep.R;
import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.arefly.sleep.receivers.CheckServiceAlarmReceiver;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 15/8/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        Logger.i("BaseActivity setContentView()");
        onCreateDrawer();

        initServiceAndAlarm(getApplicationContext());

        PreferencesHelper.setSleepTimeString("22:00", this.getApplicationContext());
        PreferencesHelper.setWakeTimeString("10:00", this.getApplicationContext());
        PreferencesHelper.setLongestIgnoreSeconds(60, this.getApplicationContext());
    }

    protected void onCreateDrawer() {
        Logger.i("BaseActivity onCreateDrawer()");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActionBar().setTitle(R.string.app_name);
                Logger.d("drawerToggle onDrawerClosed()");
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(R.string.menu);
                Logger.d("drawerToggle onDrawerOpened()");
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }*/

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