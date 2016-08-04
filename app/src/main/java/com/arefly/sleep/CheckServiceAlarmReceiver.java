package com.arefly.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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