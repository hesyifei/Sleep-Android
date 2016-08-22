package com.arefly.sleep.data.helpers;

import android.graphics.Color;

import com.arefly.sleep.data.objects.SleepDurationRecord;
import com.arefly.sleep.helpers.GlobalFunction;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

    public static List<String> getAllAvailableDate(Realm realm) {
        List<String> allDates = new ArrayList<>();

        RealmResults<SleepDurationRecord> allSleepDurationRecord = realm.where(SleepDurationRecord.class)
                .findAllSorted("startTime", Sort.ASCENDING);
        Logger.v("allSleepDurationRecord: " + allSleepDurationRecord);

        for (int i = 0; i < allSleepDurationRecord.size(); i++) {
            SleepDurationRecord eachRecord = allSleepDurationRecord.get(i);
            allDates.add(eachRecord.getDate());
        }

        return allDates;
    }

    public static boolean isAvailableDate(Realm realm, String date) {
        SleepDurationRecord thatDaySleepDurationRecord = realm.where(SleepDurationRecord.class)
                .equalTo("date", date)
                .findFirst();
        if (thatDaySleepDurationRecord == null) {
            Logger.v("isAvailableDate thatDaySleepDurationRecord == null");
            return false;
        } else {
            Logger.v("isAvailableDate thatDaySleepDurationRecord: " + thatDaySleepDurationRecord);
            return true;
        }
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


    public static CombinedData setCombinedData(CombinedData combinedData, RealmResults<SleepDurationRecord> realmResults) {
        List<Entry> lineEntries = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < realmResults.size(); i++) {
            SleepDurationRecord eachRecord = realmResults.get(i);

            int daySince2016 = GlobalFunction.getDateDifference(eachRecord.getDate(), "2016/01/01", SIMPLE_DATE_FORMAT);
            Logger.v("getSleepDurationList daySince2016: " + daySince2016);

            float sleepDurationInHours = (float) eachRecord.getDuration() / 1000 / 60 / 60;
            Logger.v("getSleepDurationList sleepDurationInHours: " + sleepDurationInHours);
            barEntries.add(new BarEntry(daySince2016, sleepDurationInHours));

            // TODO: time >00:00 will become really small (i.e. 00:10<23:50)
            float sleepStartHour = GlobalFunction.getSecondsSinceMidNight(eachRecord.getStartTime());
            Logger.v("getSleepDurationList sleepStartHour: " + sleepStartHour);
            lineEntries.add(new Entry(daySince2016, sleepStartHour));
        }

        if ((barEntries.size() == 0) || (lineEntries.size() == 0)) {
            return null;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Bar");
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setColor(Color.LTGRAY);
        barDataSet.setDrawValues(false);
        barDataSet.setHighLightColor(Color.GRAY);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.4f);
        combinedData.setData(barData);

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Line");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setCircleHoleRadius(3f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setDrawValues(false);
        LineData lineData = new LineData(lineDataSet);
        combinedData.setData(lineData);

        return combinedData;
    }

}