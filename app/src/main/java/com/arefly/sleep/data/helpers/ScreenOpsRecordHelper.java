package com.arefly.sleep.data.helpers;

import android.content.Context;

import com.arefly.sleep.data.objects.ScreenOpsRecord;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eflyjason on 11/8/2016.
 */
public class ScreenOpsRecordHelper {

    /**
     * get screen off time and duration
     *
     * @param realm     Realm
     * @param startTime start time
     * @param endTime   end time
     * @return time (Date) and duration (Long in milliseconds)
     */
    public static Map<Date, Long> getScreenOffTimeAndDuration(Realm realm, Date startTime, Date endTime) {
        RealmResults<ScreenOpsRecord> allThisSleepCycleOffRecordResult = getAllThisSleepCycleOffRecord(realm, startTime, endTime);
        Logger.v("allThisSleepCycleOffRecordResult: " + allThisSleepCycleOffRecordResult);
        if (allThisSleepCycleOffRecordResult.isEmpty()) {
            return new HashMap<>();
        }

        RealmResults<ScreenOpsRecord> allThisSleepCycleOnRecordResult = getAllThisSleepCycleOnRecord(realm, startTime, endTime, allThisSleepCycleOffRecordResult);
        Logger.v("allThisSleepCycleOnRecordResult: " + allThisSleepCycleOnRecordResult);

        Map<Date, Long> screenOffTimeAndDuration = convertScreenOnAndOffToScreenOffAndDuration(allThisSleepCycleOffRecordResult, allThisSleepCycleOnRecordResult);
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

        RealmResults<ScreenOpsRecord> allThisSleepCycleOffRecordResult = getAllThisSleepCycleOffRecord(realm, startTime, endTime);
        Logger.v("allThisSleepCycleOffRecordResult: " + allThisSleepCycleOffRecordResult);
        if (allThisSleepCycleOffRecordResult.isEmpty()) {
            return new HashMap<>();
        }

        RealmResults<ScreenOpsRecord> allThisSleepCycleOnRecordResult = getAllThisSleepCycleOnRecord(realm, startTime, endTime, allThisSleepCycleOffRecordResult);
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


        Map<Date, Long> combinedScreenOffTimeAndDuration = convertScreenOnAndOffToScreenOffAndDuration(allThisSleepCycleOffRecordList, allThisSleepCycleOnRecordList);
        Logger.v("combinedScreenOffTimeAndDuration: " + combinedScreenOffTimeAndDuration);
        return combinedScreenOffTimeAndDuration;
    }


    /**
     * convert screen on & off record to screen off and duration
     *
     * @param offRecord (List<ScreenOpsRecord>) screen off record
     * @param onRecord  (List<ScreenOpsRecord>) screen on record
     * @return (Map<Date, Long>) screen off and duration map
     */
    public static Map<Date, Long> convertScreenOnAndOffToScreenOffAndDuration(List<ScreenOpsRecord> offRecord, List<ScreenOpsRecord> onRecord) {
        Map<Date, Long> screenOffTimeAndDuration = new HashMap<>();
        for (int i = 0; i < offRecord.size(); i++) {
            ScreenOpsRecord eachOffRecord = offRecord.get(i);
            ScreenOpsRecord eachOnRecord = onRecord.get(i);
            long timeDiff = eachOnRecord.getTime().getTime() - eachOffRecord.getTime().getTime();       // First getTime() is from class ScreenOpsRecord
            Logger.v("timeDiff: " + timeDiff);
            screenOffTimeAndDuration.put(eachOffRecord.getTime(), timeDiff);
        }
        Logger.v("screenOffTimeAndDuration: " + screenOffTimeAndDuration);
        return screenOffTimeAndDuration;
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
     * get screen off records in this sleep cycle (from startTime to endTime)
     *
     * @param realm     Realm
     * @param startTime start time
     * @param endTime   end time
     * @return RealmResults<ScreenOpsRecord> screen off records in this sleep cycle (which is lazy)
     */
    public static RealmResults<ScreenOpsRecord> getAllThisSleepCycleOffRecord(Realm realm, Date startTime, Date endTime) {
        RealmResults<ScreenOpsRecord> allThisSleepCycleOffRecord = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", startTime)
                .lessThanOrEqualTo("time", endTime)
                .equalTo("operation", "off")
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleOffRecord: " + allThisSleepCycleOffRecord);
        return allThisSleepCycleOffRecord;
    }

    /**
     * get screen on records in this sleep cycle (from startTime to endTime)
     *
     * @param realm     Realm
     * @param startTime start time
     * @param endTime   end time
     * @return RealmResults<ScreenOpsRecord> screen on records in this sleep cycle (which is lazy)
     */
    public static RealmResults<ScreenOpsRecord> getAllThisSleepCycleOnRecord(Realm realm, Date startTime, Date endTime, RealmResults<ScreenOpsRecord> allThisSleepCycleOffRecord) {
        RealmResults<ScreenOpsRecord> allThisSleepCycleOnRecord = realm.where(ScreenOpsRecord.class)
                .greaterThanOrEqualTo("time", allThisSleepCycleOffRecord.get(0).getTime())
                .lessThanOrEqualTo("time", endTime)
                .equalTo("operation", "on")
                .findAllSorted("time", Sort.ASCENDING);
        Logger.v("allThisSleepCycleOnRecord: " + allThisSleepCycleOnRecord);
        return allThisSleepCycleOnRecord;
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


    public static String printScreenOpsRecord(ScreenOpsRecord record) {
        return record.getTime() + ": " + record.getOperation() + " (" + record.isLastRecord() + ")";
    }

    public static String printScreenOpsRecords(List<ScreenOpsRecord> records) {
        String returnString = "";
        for (ScreenOpsRecord record : records) {
            returnString += printScreenOpsRecord(record) + " | ";
        }
        return returnString;
    }

}