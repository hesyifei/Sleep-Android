package com.arefly.sleep.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 5/8/2016.
 */
public class PreferencesHelper {

    public static final String BOOL_IS_WAKEN_UP = "isWakenUp";
    public static final String STRING_EARLIEST_SLEEP_TIME = "earliestSleepTime";
    public static final String STRING_EARLIEST_WAKE_TIME = "earliestWakeTime";
    public static final String LONG_LONGEST_IGNORE_SECONDS = "longestIgnoreSeconds";


    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static boolean getIsWakenUpBool(Context context) {
        return getPreferences(context).getBoolean(BOOL_IS_WAKEN_UP, false);
    }

    public static String getSleepTimeString(Context context) {
        return getPreferences(context).getString(STRING_EARLIEST_SLEEP_TIME, "20:00");
    }

    public static String getWakeTimeString(Context context) {
        return getPreferences(context).getString(STRING_EARLIEST_WAKE_TIME, "06:00");
    }

    public static long getLongestIgnoreSeconds(Context context) {
        String preferenceString = getPreferences(context).getString(LONG_LONGEST_IGNORE_SECONDS, "15");
        try {
            long returnLong = Long.parseLong(preferenceString);
            return returnLong;
        } catch (NumberFormatException nfe) {
            Logger.w("getLongestIgnoreSeconds NumberFormatException: " + nfe.getMessage());
            return 15;
        }
    }


    public static void setIsWakenUpBool(boolean isWakenUp, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(BOOL_IS_WAKEN_UP, isWakenUp);
        editor.apply();
    }

    public static void setSleepTimeString(String time, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(STRING_EARLIEST_SLEEP_TIME, time);
        editor.apply();
    }

    public static void setWakeTimeString(String time, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(STRING_EARLIEST_WAKE_TIME, time);
        editor.apply();
    }

    public static void setLongestIgnoreSeconds(long seconds, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LONG_LONGEST_IGNORE_SECONDS, String.valueOf(seconds));
        editor.apply();
    }

}