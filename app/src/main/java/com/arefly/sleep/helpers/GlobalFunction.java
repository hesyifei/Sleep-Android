package com.arefly.sleep.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.arefly.sleep.R;
import com.arefly.sleep.services.ScreenService;
import com.orhanobut.logger.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by eflyjason on 4/8/2016.
 */
public class GlobalFunction {

    /**
     * Start or stop (based on isCurrentTimePossibleSleepTime()) screen service
     *
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
     *
     * @param context Context (preferably application context)
     * @return isPossibleSleepTime()
     */
    public static boolean isCurrentTimePossibleSleepTime(Context context) {
        Date currentTime = parseTime(getCurrentTimeString());
        return isPossibleSleepTime(currentTime, context);
    }

    /**
     * If a time is possible sleep time (NOTE: return true if user haven't waken up (turn on screen) yet)
     *
     * @param inputTime input time (Date)
     * @param context   Context (preferably application context)
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
     *
     * @param currentTime current time (Date)
     * @param context     Context (preferably application context)
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
     *
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

    public static String getTimeStringFromDate(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return dateFormat.format(time);
    }

    /**
     * Get current time (String)
     * (WARNING: This method should only use with parseTime() instead of showing it user)
     *
     * @return String (Format: 09:1)
     */
    public static String getCurrentTimeString() {
        Calendar now = GregorianCalendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
    }


    /**
     * Get calendar date in readable String
     *
     * @param calendar Calendar
     * @return String (Format: device date format)
     */
    public static String getCalendarDateString(Calendar calendar) {
        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        return format.format(calendar.getTime());
    }


    public static String getHoursAndMinutesString(long milliseconds, boolean needBreakline, boolean needShortForm, Context context) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours);

        int stringRes = R.string.hours_and_minutes;
        String hourUnit = singlePlural(hours, context.getResources().getString(R.string.hour), context.getResources().getString(R.string.hours));
        String minuteUnit = singlePlural(minutes, context.getResources().getString(R.string.minute), context.getResources().getString(R.string.minutes));

        if (needBreakline) {
            stringRes = R.string.hours_and_minutes_with_breakline;
        }
        if (needShortForm) {
            stringRes = R.string.hours_and_minutes_short;
            hourUnit = singlePlural(hours, context.getResources().getString(R.string.hour_short), context.getResources().getString(R.string.hours_short));
            minuteUnit = singlePlural(minutes, context.getResources().getString(R.string.minute_short), context.getResources().getString(R.string.minutes_short));
        }

        return context.getResources().getString(stringRes, String.valueOf(hours), String.format(Locale.US, "%02d", minutes), hourUnit, minuteUnit);
    }


    public static long getSecondsSinceMidNight(Date time) {
        Calendar startTimeCal = GregorianCalendar.getInstance();
        startTimeCal.setTime(time);
        int hours = startTimeCal.get(Calendar.HOUR_OF_DAY);
        int minutes = startTimeCal.get(Calendar.MINUTE);
        int seconds = startTimeCal.get(Calendar.SECOND);

        long secondsSinceMidNight = TimeUnit.HOURS.toSeconds(hours) + TimeUnit.MINUTES.toSeconds(minutes) + seconds;
        return secondsSinceMidNight;
    }

    public static String getTimeStringFromSecondsSinceMidNight(long secondsSinceMidNight) {
        long hours = TimeUnit.SECONDS.toHours(secondsSinceMidNight);
        long minutes = TimeUnit.SECONDS.toMinutes(secondsSinceMidNight) - TimeUnit.HOURS.toMinutes(hours);

        return String.format(Locale.US, "%02d:%02d", hours, minutes);
    }


    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();
    }


    public static String singlePlural(long count, String singular, String plural) {
        return count == 1 ? singular : plural;
    }


    public static int getDateDifference(String startDateString, String endDateString, SimpleDateFormat dateFormat) {
        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(startDateString);
            endDate = dateFormat.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

        Calendar sDate = timestampToCalendar(startDate.getTime());
        Calendar eDate = timestampToCalendar(endDate.getTime());

        long sDateMillis = sDate.getTimeInMillis();
        long eDateMillis = eDate.getTimeInMillis();

        long diff = Math.abs(eDateMillis - sDateMillis);

        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public static Calendar timestampToCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

}