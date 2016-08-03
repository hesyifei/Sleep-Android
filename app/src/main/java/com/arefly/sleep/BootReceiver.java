package com.arefly.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(context.getResources().getString(R.string.htc_action_quickboot_poweron))) {
            StatisticActivity.initServiceAndAlarm(context.getApplicationContext());
        }
    }
}