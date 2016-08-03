package com.arefly.sleep;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("ScreenReceiver onReceive()");

        Logger.v("intent action: " + intent.getAction());

        // http://baroqueworksdev.blogspot.hk/2012/09/how-to-handle-screen-onoff-and-keygurad.html
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SHUTDOWN) || action.equals(context.getResources().getString(R.string.htc_action_quickboot_poweroff))) {
            Logger.v("ACTION_SHUTDOWN || QUICKBOOT_POWEROFF");
            saveLockData(false);
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(context.getResources().getString(R.string.htc_action_quickboot_poweron))) {
            Logger.v("ACTION_BOOT_COMPLETED || QUICKBOOT_POWERON");
            saveLockData(true);
        }

        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Logger.v("ACTION_SCREEN_OFF");
            saveLockData(false);
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            KeyguardManager mKeyguard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (mKeyguard.inKeyguardRestrictedInputMode()) {
                Logger.v("ACTION_SCREEN_ON: locked");
            } else {
                Logger.v("ACTION_SCREEN_ON: unlocked");
                saveLockData(true);
            }
        } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
            Logger.v("ACTION_USER_PRESENT");
            saveLockData(true);
        }

    }

    private void saveLockData(boolean isUnlock) {
        Logger.d("saveLockStatus("+isUnlock+") called");
    }

}