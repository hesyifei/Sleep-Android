package com.arefly.sleep.data.objects;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by eflyjason on 4/8/2016.
 */
public class ScreenOpsRecord extends RealmObject {

    // TODO: Change operations to operation
    private String operations;
    private Date time;

    private boolean isLastRecord;


    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
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