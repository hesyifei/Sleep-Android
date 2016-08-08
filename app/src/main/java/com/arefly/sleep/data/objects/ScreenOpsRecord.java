package com.arefly.sleep.data.objects;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by eflyjason on 4/8/2016.
 */
public class ScreenOpsRecord extends RealmObject {

    private String operation;
    private Date time;

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