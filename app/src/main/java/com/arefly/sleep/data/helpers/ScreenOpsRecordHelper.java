package com.arefly.sleep.data.helpers;

import com.arefly.sleep.data.objects.ScreenOpsRecord;

import java.util.List;

/**
 * Created by eflyjason on 11/8/2016.
 */
public class ScreenOpsRecordHelper {

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