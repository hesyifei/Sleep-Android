package com.arefly.sleep.helpers;

import android.content.Context;
import android.content.Intent;

import com.arefly.sleep.data.helpers.ScreenOpsRecordHelper;
import com.arefly.sleep.data.objects.ScreenOpsRecord;
import com.arefly.sleep.services.ScreenService;
import com.orhanobut.logger.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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
     * get screen off time and duration
     *
     * @param realm     Realm
     * @param startTime start time
     * @param endTime   end time
     * @return time (Date) and duration (Long in milliseconds)
     */
    public static Map<Date, Long> getScreenOffTimeAndDuration(Realm realm, Date startTime, Date endTime) {
        RealmResults<ScreenOpsRecord> allThisSleepCycleOffRecord = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", startTime)
                .lessThanOrEqualTo("time", endTime)
                .equalTo("operation", "off")
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleOffRecord: " + allThisSleepCycleOffRecord);

        RealmResults<ScreenOpsRecord> allThisSleepCycleOnRecord = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", allThisSleepCycleOffRecord.get(0).getTime())
                .lessThanOrEqualTo("time", endTime)
                .equalTo("operation", "on")
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleOnRecord: " + allThisSleepCycleOnRecord);

        Map<Date, Long> screenOffTimeAndDuration = new HashMap<>();
        for (int i = 0; i < allThisSleepCycleOffRecord.size(); i++) {
            ScreenOpsRecord eachOffRecord = allThisSleepCycleOffRecord.get(i);
            ScreenOpsRecord eachOnRecord = allThisSleepCycleOnRecord.get(i);
            long timeDiff = eachOnRecord.getTime().getTime() - eachOffRecord.getTime().getTime();       // First getTime() is from class ScreenOpsRecord
            Logger.v("timeDiff: " + timeDiff);
            screenOffTimeAndDuration.put(eachOffRecord.getTime(), timeDiff);
        }
        Logger.v("screenOffTimeAndDuration: " + screenOffTimeAndDuration);
        return screenOffTimeAndDuration;
    }


    /**
     * get combined screen off time and duration (i.e. remove screen off intervals which are too short (<= getLongestIgnoreSeconds))
     *
     * @param realm     Realm
     * @param startTime start time
     * @param endTime   end time
     * @param context   Context (preferably application context)
     * @return time (Date) and duration (Long in milliseconds)
     */
    public static Map<Date, Long> getCombinedScreenOffTimeAndDuration(Realm realm, Date startTime, Date endTime, Context context) {
        /*Map<Date, Long> combinedScreenOffTimeAndDuration = getScreenOffTimeAndDuration(realm, startTime, endTime);
        List<Date> dateNeededToBeRemoved = new ArrayList<>();*/
        /*for (Map.Entry<Date, Long> entry : combinedScreenOffTimeAndDuration.entrySet()) {
            if (entry.getValue() <= PreferencesHelper.getLongestIgnoreSeconds(context)*1000) {
                RealmResults<ScreenOpsRecord> previousScreenOpsRecords = realm.where(ScreenOpsRecord.class)
                        .lessThan("time", entry.getKey())
                        .greaterThanOrEqualTo("time", startTime)
                        .findAllSorted("time", Sort.DESCENDING);
                Logger.v("previousScreenOpsRecords: " + previousScreenOpsRecords);
                if (previousScreenOpsRecords.size() >= 1) {
                    ScreenOpsRecord lastScreenOpsRecord = previousScreenOpsRecords.get(0);
                    combinedScreenOffTimeAndDuration.put(lastScreenOpsRecord.getTime(), combinedScreenOffTimeAndDuration.get(lastScreenOpsRecord.getTime()) + entry.getValue());
                    dateNeededToBeRemoved.add(entry.getKey());
                }
            }
        }*/
        /*int i = 0;
        for (Map.Entry<Date, Long> entry : combinedScreenOffTimeAndDuration.entrySet()) {
            if (i > 0) {
                RealmResults<ScreenOpsRecord> previousScreenOpsRecords = realm.where(ScreenOpsRecord.class)
                        .lessThan("time", entry.getKey())
                        .greaterThanOrEqualTo("time", startTime)
                        .equalTo("operation", "off")
                        .findAllSorted("time", Sort.DESCENDING);
                Logger.v("previousScreenOpsRecords: " + previousScreenOpsRecords);
                if (previousScreenOpsRecords.size() >= 1) {
                    ScreenOpsRecord lastScreenOpsRecord = previousScreenOpsRecords.get(0);

                    combinedScreenOffTimeAndDuration.get

                    combinedScreenOffTimeAndDuration.put(lastScreenOpsRecord.getTime(), combinedScreenOffTimeAndDuration.get(lastScreenOpsRecord.getTime()) + entry.getValue());
                    dateNeededToBeRemoved.add(entry.getKey());
                }
            }*/

            /*RealmResults<ScreenOpsRecord> previousScreenOpsRecords = realm.where(ScreenOpsRecord.class)
                    .lessThan("time", entry.getKey())
                    .greaterThanOrEqualTo("time", startTime)
                    .findAllSorted("time", Sort.DESCENDING);
            Logger.v("previousScreenOpsRecords: " + previousScreenOpsRecords);
            if (previousScreenOpsRecords.size() >= 1) {
                ScreenOpsRecord lastScreenOpsRecord = previousScreenOpsRecords.get(0);
                combinedScreenOffTimeAndDuration.put(lastScreenOpsRecord.getTime(), combinedScreenOffTimeAndDuration.get(lastScreenOpsRecord.getTime()) + entry.getValue());
                dateNeededToBeRemoved.add(entry.getKey());
            }*/

        /*    i++;
        }

        if (!dateNeededToBeRemoved.isEmpty()) {
            for (Date keyDate : dateNeededToBeRemoved) {
                combinedScreenOffTimeAndDuration.remove(keyDate);
            }
        }
        Logger.v("combinedScreenOffTimeAndDuration: " + combinedScreenOffTimeAndDuration);
        return combinedScreenOffTimeAndDuration;*/

        RealmResults<ScreenOpsRecord> allThisSleepCycleOffRecordResult = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", startTime)
                .lessThanOrEqualTo("time", endTime)
                .equalTo("operation", "off")
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleOffRecordResult: " + allThisSleepCycleOffRecordResult);

        RealmResults<ScreenOpsRecord> allThisSleepCycleOnRecordResult = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", allThisSleepCycleOffRecordResult.get(0).getTime())
                .lessThanOrEqualTo("time", endTime)
                .equalTo("operation", "on")
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleOnRecordResult: " + allThisSleepCycleOnRecordResult);

        List<ScreenOpsRecord> allThisSleepCycleOffRecordList = realm.copyFromRealm(allThisSleepCycleOffRecordResult);
        Logger.v("allThisSleepCycleOffRecordList: " + ScreenOpsRecordHelper.printScreenOpsRecords(allThisSleepCycleOffRecordList));
        List<ScreenOpsRecord> allThisSleepCycleOnRecordList = realm.copyFromRealm(allThisSleepCycleOnRecordResult);
        Logger.v("allThisSleepCycleOnRecordList: " + ScreenOpsRecordHelper.printScreenOpsRecords(allThisSleepCycleOnRecordList));


        List<ScreenOpsRecord> screenOpsRecordOnNeededToBeRemoved = new ArrayList<>();
        List<ScreenOpsRecord> screenOpsRecordOffNeededToBeRemoved = new ArrayList<>();

        // reverse the loop
        for (int i = allThisSleepCycleOffRecordList.size() - 1; i >= 0; i--) {
            ScreenOpsRecord eachOffRecord = allThisSleepCycleOffRecordList.get(i);
            Logger.v("allThisSleepCycleOffRecordList[" + i + "]: " + ScreenOpsRecordHelper.printScreenOpsRecord(eachOffRecord));
            ScreenOpsRecord eachOnRecord = allThisSleepCycleOnRecordList.get(i);
            Logger.v("allThisSleepCycleOnRecordList[" + i + "]: " + ScreenOpsRecordHelper.printScreenOpsRecord(eachOnRecord));

            long timeDiff = eachOnRecord.getTime().getTime() - eachOffRecord.getTime().getTime();       // First getTime() is from class ScreenOpsRecord
            Logger.v("timeDiff: " + timeDiff);

            if (i > 0) {
                ScreenOpsRecord lastOffRecord = allThisSleepCycleOffRecordList.get(i - 1);
                Logger.v("allThisSleepCycleOffRecordList[" + i + "-1]: " + ScreenOpsRecordHelper.printScreenOpsRecord(lastOffRecord));
                ScreenOpsRecord lastOnRecord = allThisSleepCycleOnRecordList.get(i - 1);
                Logger.v("allThisSleepCycleOnRecordList[" + i + "-1]: " + ScreenOpsRecordHelper.printScreenOpsRecord(lastOnRecord));

                long thisOffToLastOnDuration = eachOffRecord.getTime().getTime() - lastOnRecord.getTime().getTime();
                Logger.v("thisOffToLastOnDuration: " + thisOffToLastOnDuration);
                long longestIgnoreMilliseconds = PreferencesHelper.getLongestIgnoreSeconds(context) * 1000;
                Logger.v("longestIgnoreMilliseconds: " + longestIgnoreMilliseconds);
                if (thisOffToLastOnDuration <= longestIgnoreMilliseconds) {
                    // as it's copyFromRealm, it's no longer lazy (i.e. it's a new list with no connection to realm)!
                    lastOnRecord.setTime(new Date(lastOnRecord.getTime().getTime() + timeDiff));
                    allThisSleepCycleOnRecordList.set(i - 1, lastOnRecord);
                    Logger.v("allThisSleepCycleOnRecordList[" + i + "-1] (modified): " + ScreenOpsRecordHelper.printScreenOpsRecord(allThisSleepCycleOnRecordList.get(i - 1)));

                    screenOpsRecordOffNeededToBeRemoved.add(eachOffRecord);
                    screenOpsRecordOnNeededToBeRemoved.add(eachOnRecord);
                }
            }

            Logger.d("allThisSleepCycle list loop " + i + " ends\n---");

        }

        Logger.v("screenOpsRecordOffNeededToBeRemoved: " + ScreenOpsRecordHelper.printScreenOpsRecords(screenOpsRecordOffNeededToBeRemoved));
        if (!screenOpsRecordOffNeededToBeRemoved.isEmpty()) {
            allThisSleepCycleOffRecordList.removeAll(screenOpsRecordOffNeededToBeRemoved);
        }

        Logger.v("screenOpsRecordOnNeededToBeRemoved: " + ScreenOpsRecordHelper.printScreenOpsRecords(screenOpsRecordOnNeededToBeRemoved));
        if (!screenOpsRecordOnNeededToBeRemoved.isEmpty()) {
            allThisSleepCycleOnRecordList.removeAll(screenOpsRecordOnNeededToBeRemoved);
        }


        Map<Date, Long> combinedScreenOffTimeAndDuration = new HashMap<>();
        for (int i = 0; i < allThisSleepCycleOffRecordList.size(); i++) {
            ScreenOpsRecord eachOffRecord = allThisSleepCycleOffRecordList.get(i);
            ScreenOpsRecord eachOnRecord = allThisSleepCycleOnRecordList.get(i);
            long timeDiff = eachOnRecord.getTime().getTime() - eachOffRecord.getTime().getTime();       // First getTime() is from class ScreenOpsRecord
            Logger.v("timeDiff: " + timeDiff);
            combinedScreenOffTimeAndDuration.put(eachOffRecord.getTime(), timeDiff);
        }
        Logger.v("combinedScreenOffTimeAndDuration: " + combinedScreenOffTimeAndDuration);
        return combinedScreenOffTimeAndDuration;
    }

    /**
     * get max sleep duration entry
     *
     * @param screenOffTimeAndDuration screen off time (Date) and duration(Long) in Map
     * @return max sleep duration in Map.Entry
     */
    public static Map.Entry getMaxSleepDurationEntry(Map<Date, Long> screenOffTimeAndDuration) {
        // http://stackoverflow.com/a/5911199/2603230
        Map.Entry<Date, Long> maxSleepDurationEntry = null;
        for (Map.Entry<Date, Long> entry : screenOffTimeAndDuration.entrySet()) {
            if (maxSleepDurationEntry == null || entry.getValue().compareTo(maxSleepDurationEntry.getValue()) >= 0) {
                maxSleepDurationEntry = entry;
            }
        }
        Logger.v("maxSleepDurationEntry: " + maxSleepDurationEntry);
        return maxSleepDurationEntry;
    }


    /**
     * remove repeating operations (e.g. on1/on2/on3/off1 -> on1/off1) in time range
     *
     * @param realm     Realm
     * @param startTime start time
     * @param endTime   end time
     * @return RealmResults which is lazy (i.e. can be modified directly)
     */
    public static RealmResults<ScreenOpsRecord> removeRepeatingOperationsInTimeRange(Realm realm, Date startTime, Date endTime) {
        RealmResults<ScreenOpsRecord> allThisSleepCycleRecord = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", startTime)
                .lessThanOrEqualTo("time", endTime)
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleRecord: " + allThisSleepCycleRecord);


        List<Integer> locationNeededToBeRemoved = new ArrayList<>();
        for (int i = 0; i < allThisSleepCycleRecord.size(); i++) {
            ScreenOpsRecord eachRecord = allThisSleepCycleRecord.get(i);
            Logger.v("allThisSleepCycleRecordArray[" + i + "]: " + eachRecord);
            if (i + 1 <= allThisSleepCycleRecord.size() - 1) {
                // If have next record
                ScreenOpsRecord nextRecord = allThisSleepCycleRecord.get(i + 1);
                String eachRecordOperation = eachRecord.getOperation();
                String nextRecordOperation = nextRecord.getOperation();
                if ((eachRecordOperation.equals("on") && !nextRecordOperation.equals("off"))
                        || (eachRecordOperation.equals("off") && !nextRecordOperation.equals("on"))) {
                    locationNeededToBeRemoved.add(i);
                }
            }
        }
        Logger.v("locationNeededToBeRemoved: " + locationNeededToBeRemoved);
        if (!locationNeededToBeRemoved.isEmpty()) {
            realm.beginTransaction();
            for (Integer locationInteger : locationNeededToBeRemoved) {
                // Can simply delete as realm object is lazy
                allThisSleepCycleRecord.deleteFromRealm(locationInteger);
            }
            realm.commitTransaction();
            Logger.v("locationNeededToBeRemoved removed from realm. allThisSleepCycleRecord: " + allThisSleepCycleRecord);
        }

        return allThisSleepCycleRecord;
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

    /**
     * Get current time (String)
     *
     * @return String (Format: 09:00)
     */
    public static String getCurrentTimeString() {
        Calendar now = GregorianCalendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
    }


    public static String getCalendarDateString(Calendar calendar) {
        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        return format.format(calendar.getTime());
    }

}