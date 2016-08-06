package com.arefly.sleep.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 6/8/2016.
 */
public class StartScreenAtEndAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("StartScreenAtEndAlarmReceiver onReceive()");


        boolean isWakenUpBool = PreferencesHelper.getIsWakenUpBool(context);
        Logger.v("isWakenUp: " + isWakenUpBool);

        if (!GlobalFunction.isRealSleepTime(GlobalFunction.parseTime(GlobalFunction.getCurrentTimeString()), context)) {
            // If current time is not real sleep time
            if (!isWakenUpBool) {
                // If user haven't waken up (turn on screen) yet
                ScreenReceiver.saveLockData(true, context);

            }
        }

    }
}