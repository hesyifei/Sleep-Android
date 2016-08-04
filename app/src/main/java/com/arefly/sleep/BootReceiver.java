package com.arefly.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

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

            PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isInteractive() || Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isScreenOn();
            Logger.e("isScreenOn: " + isScreenOn);

            if (isScreenOn) {
                ScreenReceiver.saveLockData(true, context.getApplicationContext());
            }
        }
    }
}