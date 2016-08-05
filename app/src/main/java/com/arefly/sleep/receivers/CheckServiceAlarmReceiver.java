package com.arefly.sleep.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arefly.sleep.helpers.GlobalFunction;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class CheckServiceAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("CheckServiceAlarmReceiver onReceive()");

        GlobalFunction.startOrStopScreenServiceIntent(context);
    }

}