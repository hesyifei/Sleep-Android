package com.arefly.sleep.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arefly.sleep.activities.MainActivity;
import com.arefly.sleep.helpers.GlobalFunction;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(ScreenReceiver.HTC_ACTION_QUICKBOOT_POWERON)) {
            Logger.v("ACTION_BOOT_COMPLETED || QUICKBOOT_POWERON");

            MainActivity.initServiceAndAlarm(context.getApplicationContext());

            boolean isScreenOn = GlobalFunction.isScreenOn(context.getApplicationContext());
            Logger.e("isScreenOn: " + isScreenOn);

            if (isScreenOn) {
                ScreenReceiver.saveLockData(true, context.getApplicationContext());
            }
        }
    }
}