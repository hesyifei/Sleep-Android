package com.arefly.sleep.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.arefly.sleep.R;
import com.arefly.sleep.fragments.OverviewFragment;
import com.arefly.sleep.fragments.RecordFragment;
import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.arefly.sleep.receivers.CheckServiceAlarmReceiver;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 15/8/2016.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("MainActivity onCreate()");
        setContentView(R.layout.activity_main);


        replaceFragment(this, "Overview");


        initServiceAndAlarm(getApplicationContext());

        PreferencesHelper.setSleepTimeString("22:00", this.getApplicationContext());
        PreferencesHelper.setWakeTimeString("10:00", this.getApplicationContext());
        PreferencesHelper.setLongestIgnoreSeconds(60, this.getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    Logger.v("KEYCODE_MENU onKeyUp");
                    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public static void setupDrawer(final AppCompatActivity mainActivity, final View fragmentView) {
        Logger.d("MainActivity setupDrawer(" + mainActivity + ", " + fragmentView + ")");

        final Toolbar toolbar = (Toolbar) fragmentView.findViewById(R.id.toolbar);
        Logger.v("toolbar: " + toolbar);
        mainActivity.setSupportActionBar(toolbar);

        final DrawerLayout drawerLayout = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);
        Logger.v("drawerLayout: " + drawerLayout);

        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(mainActivity, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Logger.d("actionBarDrawerToggle onDrawerClosed()");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Logger.d("actionBarDrawerToggle onDrawerOpened()");
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        final NavigationView navigationView = (NavigationView) mainActivity.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Logger.d("drawerLayout onNavigationItemSelected(" + menuItem + ")");
                replaceFragment(mainActivity, menuItem.getTitle().toString());
                menuItem.setChecked(true);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        drawerLayout.closeDrawers();
                    }
                }, 300);
                //drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private static void replaceFragment(AppCompatActivity mainActivity, String title) {
        Fragment fragment;
        switch (title) {
            case "Overview":
                fragment = new OverviewFragment();
                break;
            case "Calendar":
                fragment = new RecordFragment();
                break;
            default:
                fragment = new OverviewFragment();
                break;
        }
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)           // Maybe shouldn't use it
                .replace(R.id.content_frame, fragment)
                .commit();
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