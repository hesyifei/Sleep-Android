package com.arefly.sleep;

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eflyjason on 4/8/2016.
 */
public class GlobalFunction {

    public static void startOrStopScreenServiceIntent(Context context) {
        Intent screenServiceIntent = new Intent(context, ScreenService.class);

        Calendar now = Calendar.getInstance();
        Date currentTime = GlobalFunction.parseTime(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
        Logger.v("isPossibleSleepTime: " + isPossibleSleepTime(currentTime));

        if (isPossibleSleepTime(currentTime)) {
            context.startService(screenServiceIntent);
        } else {
            context.stopService(screenServiceIntent);
        }
    }


    public static boolean isPossibleSleepTime(Date currentTime) {
        // Tell user the earlier the better if he/she is not sure about the max. sleep time range
        // Have to check if it's morning and don't start service
        Date nightTime = parseTime("12:30");
        Date morningTime = parseTime("12:00");
        Logger.v("currentTime: " + currentTime + "\nafter: " + currentTime.after(nightTime) + "\nbefore: " + currentTime.before(morningTime));
        return currentTime.after(nightTime) || currentTime.before(morningTime);
    }

    public static Date parseTime(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
            return dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

}