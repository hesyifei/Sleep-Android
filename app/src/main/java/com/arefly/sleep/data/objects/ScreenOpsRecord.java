package com.arefly.sleep.data.objects;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by eflyjason on 4/8/2016.
 */
public class ScreenOpsRecord extends RealmObject {

    /**
     * (String) operation name
     * i.e. "on"/"off"
     */
    private String operation;

    /**
     * (Date) record time
     */
    private Date time;

    /**
     * (boolean) is last record of a sleep cycle
     */
    private boolean isLastRecord;


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isLastRecord() {
        return isLastRecord;
    }

    public void setLastRecord(boolean lastRecord) {
        this.isLastRecord = lastRecord;
    }
}