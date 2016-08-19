package com.arefly.sleep.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.arefly.sleep.R;
import com.arefly.sleep.activities.MainActivity;
import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.receivers.ScreenReceiver;
import com.orhanobut.logger.Logger;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class ScreenService extends Service {

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("ScreenService onCreate()");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        // TODO: Hide notification on morning? (seemly done)
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setStyle(new NotificationCompat.BigTextStyle()
                //        .bigText("晚上好！今晚多睡會吧！\n\n輕觸查看詳細統計記錄..."))
                .setContentTitle("晚上好！祝你睡個好覺！")
                .setContentText("輕觸查看詳細記錄...")
                .setContentIntent(notificationPendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(88, notification);


        boolean isScreenOn = GlobalFunction.isScreenOn(this.getApplicationContext());
        Logger.e("isScreenOn: " + isScreenOn);

        if (isScreenOn) {
            ScreenReceiver.saveLockData(true, this.getApplicationContext());
        } else {
            ScreenReceiver.saveLockData(false, this.getApplicationContext());
        }


        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_SHUTDOWN);
        screenStateFilter.addAction(ScreenReceiver.HTC_ACTION_QUICKBOOT_POWEROFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, screenStateFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("ScreenService onStartCommand()");

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i("ScreenService onBind()");
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("ScreenService onDestroy()");

        unregisterReceiver(mReceiver);
    }

}