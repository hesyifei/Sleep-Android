package com.arefly.sleep.data.objects;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by eflyjason on 4/8/2016.
 */
public class ScreenOpsRecord extends RealmObject {
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

    private String operations;
    private Date time;
}