package com.arefly.sleep.data.objects;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by eflyjason on 10/8/2016.
 */
public class SleepDurationRecord extends RealmObject {

    /**
     * (String) date
     * e.g. "2016-08-08"
     */
    private String date;

    /**
     * (long) duration in milliseconds
     */
    private long duration;

    /**
     * (Date) sleep start time
     */
    private Date startTime;

    /**
     * (Date) sleep end time
     */
    private Date endTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}