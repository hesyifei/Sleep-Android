package com.arefly.sleep.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class CheckServiceAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("CheckServiceAlarmReceiver onReceive()");

        GlobalFunction.startOrStopScreenServiceIntent(context);



        AlarmManager startScreenAtEndAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent startScreenAtEndAlarmReceiver = new Intent(context, StartScreenAtEndAlarmReceiver.class);
        PendingIntent startScreenAtEndAlarmReceiverIntent = PendingIntent.getBroadcast(context, 0, startScreenAtEndAlarmReceiver, 0);


        try {
            Logger.d("startScreenAtEndAlarmMgr cancelled");
            startScreenAtEndAlarmMgr.cancel(startScreenAtEndAlarmReceiverIntent);
        } catch (Exception e) {
            Logger.w("startScreenAtEndAlarmMgr cannot be cancelled: " + e.toString());
        }



        Date currentTime = GlobalFunction.parseTime(GlobalFunction.getCurrentTimeString());
        Date sleepTime = GlobalFunction.parseTime(PreferencesHelper.getSleepTimeString(context));

        Calendar sleepTimeCalendar = GregorianCalendar.getInstance();   // Date is 1/1/1970 here
        sleepTimeCalendar.setTime(sleepTime);   // assigns calendar to given date

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, sleepTimeCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, sleepTimeCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, -1);         // Fire alarm 1 hour earlier to make sure that the alarm is fired

        if (currentTime.before(sleepTime)) {

        } else {
            calendar.add(Calendar.DATE, 1);
        }
        Logger.v("startScreenAtEndAlarmMgr calendar: " + GlobalFunction.getCalendarDateString(calendar));


        // http://stackoverflow.com/a/38302891/2603230
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExactAndAllowWhileIdle(ALARM_TYPE, calendar.getTimeInMillis(), startScreenAtEndAlarmReceiverIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            am.setExact(ALARM_TYPE, calendar.getTimeInMillis(), startScreenAtEndAlarmReceiverIntent);
        else
            am.set(ALARM_TYPE, calendar.getTimeInMillis(), startScreenAtEndAlarmReceiverIntent);

        Logger.d("startScreenAtEndAlarmMgr created");
    }

}