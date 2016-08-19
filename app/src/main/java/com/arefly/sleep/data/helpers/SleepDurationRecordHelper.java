package com.arefly.sleep.data.helpers;

import com.arefly.sleep.data.objects.SleepDurationRecord;
import com.arefly.sleep.helpers.GlobalFunction;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eflyjason on 12/8/2016.
 */
public class SleepDurationRecordHelper {
    public static final String DATE_DATA_TO_BE_PASSED_ID = "Date";

    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.US);


    /**
     * remove all repeating date (e.g. 2015/06/07+2015/06/07+2015/06/08 -> 2015/06/07+2015/06/08)
     *
     * @param realm Realm
     * @return RealmResults which is lazy (i.e. can be modified directly)
     */
    public static RealmResults<SleepDurationRecord> removeAllRepeatingDate(Realm realm) {
        RealmResults<SleepDurationRecord> allSleepDurationRecord = realm.where(SleepDurationRecord.class)
                .findAllSorted("startTime", Sort.ASCENDING);
        Logger.v("allSleepDurationRecord: " + allSleepDurationRecord);


        List<Integer> locationNeededToBeRemoved = new ArrayList<>();
        for (int i = 0; i < allSleepDurationRecord.size(); i++) {
            SleepDurationRecord eachRecord = allSleepDurationRecord.get(i);
            Logger.v("allSleepDurationRecord[" + i + "]: " + eachRecord);
            if (i + 1 <= allSleepDurationRecord.size() - 1) {
                // If have next record
                SleepDurationRecord nextRecord = allSleepDurationRecord.get(i + 1);
                String thisRecordDate = eachRecord.getDate();
                String nextRecordDate = nextRecord.getDate();
                if (thisRecordDate.equals(nextRecordDate)) {
                    locationNeededToBeRemoved.add(i);
                }
            }
        }
        Logger.v("locationNeededToBeRemoved: " + locationNeededToBeRemoved);
        if (!locationNeededToBeRemoved.isEmpty()) {
            realm.beginTransaction();
            for (Integer locationInteger : locationNeededToBeRemoved) {
                Logger.v("prepare to remove " + locationInteger + " (" + allSleepDurationRecord.get(locationInteger) + ")");
                // Can simply delete as realm object is lazy
                allSleepDurationRecord.deleteFromRealm(locationInteger);
            }
            realm.commitTransaction();
            Logger.v("locationNeededToBeRemoved removed from realm. allSleepDurationRecord: " + allSleepDurationRecord);
        }

        return allSleepDurationRecord;
    }


    public static class StatisticsData {
        public long averageSleepDuration = 0;
        public String averageStartTime = "";
        public String averageEndTime = "";

        public long getAverageSleepDuration() {
            return averageSleepDuration;
        }

        public void setAverageSleepDuration(long averageSleepDuration) {
            this.averageSleepDuration = averageSleepDuration;
        }

        public String getAverageStartTime() {
            return averageStartTime;
        }

        public void setAverageStartTime(String averageStartTime) {
            this.averageStartTime = averageStartTime;
        }

        public String getAverageEndTime() {
            return averageEndTime;
        }

        public void setAverageEndTime(String averageEndTime) {
            this.averageEndTime = averageEndTime;
        }
    }

    public static StatisticsData getStatisticsData(RealmResults<SleepDurationRecord> realmResults) {
        StatisticsData statisticsData = new StatisticsData();

        int daysCount = realmResults.size();

        if (daysCount <= 0) {
            statisticsData.setAverageSleepDuration(-1);
            return statisticsData;
        }

        long totalDuration = 0;
        long totalStartTime = 0;
        long totalEndTime = 0;
        for (int i = 0; i < realmResults.size(); i++) {
            SleepDurationRecord eachRecord = realmResults.get(i);
            Logger.v("realmResults[" + i + "]: " + eachRecord);
            totalDuration += eachRecord.getDuration();

            totalStartTime += GlobalFunction.getSecondsSinceMidNight(eachRecord.getStartTime());
            totalEndTime += GlobalFunction.getSecondsSinceMidNight(eachRecord.getEndTime());
        }

        statisticsData.setAverageSleepDuration(totalDuration / daysCount);

        long averageStartTimeSeconds = totalStartTime / daysCount;
        statisticsData.setAverageStartTime(GlobalFunction.getTimeStringFromSecondsSinceMidNight(averageStartTimeSeconds));

        long averageEndTimeSeconds = totalEndTime / daysCount;
        statisticsData.setAverageEndTime(GlobalFunction.getTimeStringFromSecondsSinceMidNight(averageEndTimeSeconds));

        return statisticsData;
    }

}