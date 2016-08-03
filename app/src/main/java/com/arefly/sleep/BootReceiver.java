package com.arefly.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(context.getResources().getString(R.string.htc_action_quickboot_poweron))) {
            Logger.v("ACTION_BOOT_COMPLETED || QUICKBOOT_POWERON");
            StatisticActivity.initServiceAndAlarm(context.getApplicationContext());
            ScreenReceiver.saveLockData(true);
        }
    }
}