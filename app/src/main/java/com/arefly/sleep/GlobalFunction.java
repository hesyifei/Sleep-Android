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

    /**
     * Start or stop (based on isCurrentTimePossibleSleepTime()) screen service
     * @param context Context (preferably application context)
     */
    public static void startOrStopScreenServiceIntent(Context context) {
        Intent screenServiceIntent = new Intent(context, ScreenService.class);


        boolean isCurrentTimePossibleSleepTimeBool = isCurrentTimePossibleSleepTime(context);
        Logger.v("isCurrentTimePossibleSleepTime: " + isCurrentTimePossibleSleepTimeBool);

        if (isCurrentTimePossibleSleepTimeBool) {
            context.startService(screenServiceIntent);
        } else {
            context.stopService(screenServiceIntent);
        }


        boolean isRealSleepTimeBool = isRealSleepTime(parseTime(getCurrentTimeString()), context);
        Logger.v("isRealSleepTime: " + isRealSleepTimeBool);

        if (isRealSleepTimeBool) {
            PreferencesHelper.setIsWakenUpBool(false, context);
        }

        Logger.v("isWakenUp: " + PreferencesHelper.getIsWakenUpBool(context));
    }


    /**
     * Call isPossibleSleepTime() with current time
     * @param context Context (preferably application context)
     * @return isPossibleSleepTime()
     */
    public static boolean isCurrentTimePossibleSleepTime(Context context) {
        Date currentTime = parseTime(getCurrentTimeString());
        return isPossibleSleepTime(currentTime, context);
    }

    /**
     * If a time is possible sleep time (NOTE: return true if user haven't waken up (turn on screen) yet)
     * @param inputTime input time (Date)
     * @param context Context (preferably application context)
     * @return boolean
     */
    public static boolean isPossibleSleepTime(Date inputTime, Context context) {
        // Tell user the earlier the better if he/she is not sure about the max. sleep time range

        // Have to check if it's morning and don't start service (seemly done)



        Date sleepTime = parseTime(PreferencesHelper.getSleepTimeString(context));
        Date wakeTime = parseTime(PreferencesHelper.getWakeTimeString(context));
        Logger.v("inputTime: " + inputTime + "\nafter sleepTime: " + inputTime.after(sleepTime) + "\nbefore wakeTime: " + inputTime.before(wakeTime));

        boolean isWakenUpBool = PreferencesHelper.getIsWakenUpBool(context);
        Logger.v("isWakenUp: " + isWakenUpBool);

        if (!GlobalFunction.isRealSleepTime(inputTime, context)) {
            // If current time is not real sleep time
            if (!isWakenUpBool) {
                // If user haven't waken up (turn on screen) yet
                return true;
            }
        }

        return isRealSleepTime(inputTime, context);
    }

    /**
     * If a time is possible sleep time (unlike isPossibleSleepTime, its return value is completely based on sleepTime & wakeTime)
     * @param currentTime current time (Date)
     * @param context Context (preferably application context)
     * @return boolean
     */
    public static boolean isRealSleepTime(Date currentTime, Context context) {
        Date sleepTime = parseTime(PreferencesHelper.getSleepTimeString(context));
        Date wakeTime = parseTime(PreferencesHelper.getWakeTimeString(context));

        boolean afterSleepTime = (currentTime.after(sleepTime) || currentTime.equals(sleepTime));
        boolean beforeWakeTime = (currentTime.before(wakeTime) || currentTime.equals(wakeTime));

        if (wakeTime.before(sleepTime)) {
            // Normal situation: morning wake + night sleep
            return afterSleepTime || beforeWakeTime;
        } else {
            // Special situation: morning sleep + night wake
            return afterSleepTime && beforeWakeTime;
        }
    }

    /**
     * Convert time (String) into Date object
     * @param timeString format: 09:00
     * @return Date object's date will be 01/01/1970
     */
    public static Date parseTime(String timeString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
            return dateFormat.parse(timeString);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    /**
     * Get current time (String)
     * @return String (Format: 09:00)
     */
    public static String getCurrentTimeString() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
    }

}