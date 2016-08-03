package com.arefly.sleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orhanobut.logger.Logger;

import java.util.Calendar;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class StatisticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("StatisticActivity onCreate()");
        setContentView(R.layout.activity_statistic);

        CheckServiceAlarmReceiver.startOrStopScreenServiceIntent(this);


        AlarmManager checkServiceAlarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), CheckServiceAlarmReceiver.class);
        PendingIntent checkServiceAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);


        try {
            Logger.d("checkServiceAlarmMgr cancelled");
            checkServiceAlarmMgr.cancel(checkServiceAlarmIntent);
        } catch (Exception e) {
            Logger.w("checkServiceAlarmMgr cannot be cancelled: " + e.toString());
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 45);
        checkServiceAlarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, checkServiceAlarmIntent);
        //checkServiceAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 2000, checkServiceAlarmIntent);

    }
}