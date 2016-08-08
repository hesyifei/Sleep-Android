package com.arefly.sleep.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.arefly.sleep.R;
import com.arefly.sleep.activities.StatisticActivity;
import com.arefly.sleep.data.objects.ScreenOpsRecord;
import com.arefly.sleep.helpers.GlobalFunction;
import com.arefly.sleep.helpers.PreferencesHelper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("ScreenReceiver onReceive()");

        String action = intent.getAction();
        Logger.v("intent action: " + action);


        if (action.equals(Intent.ACTION_SHUTDOWN) || action.equals(context.getResources().getString(R.string.htc_action_quickboot_poweroff))) {
            Logger.v("ACTION_SHUTDOWN || QUICKBOOT_POWEROFF");
            saveLockData(false, context.getApplicationContext());
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Logger.v("ACTION_SCREEN_OFF");
            saveLockData(false, context.getApplicationContext());
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Logger.v("ACTION_SCREEN_ON");
            saveLockData(true, context.getApplicationContext());
        }

    }

    public static void saveLockData(boolean isScreenOn, Context context) {
        Logger.d("saveLockData(" + isScreenOn + ") called");
        // TODO: Have to judge if current time is inside range of max. sleep time as this method is call-on-boot (seemly done)

        Realm realm = Realm.getDefaultInstance();

        if (GlobalFunction.isCurrentTimePossibleSleepTime(context)) {

            ScreenOpsRecord record = new ScreenOpsRecord();
            record.setOperation(isScreenOn ? "on" : "off");
            record.setTime(new Date());

            realm.beginTransaction();
            //realm.delete(ScreenOpsRecord.class);      // FOR DEBUGGING ONLY
            ScreenOpsRecord realmRecord = realm.copyToRealm(record);
            realm.commitTransaction();
            Logger.d("realmRecord saved: " + realmRecord);

        } else {
            Logger.v("isCurrentTimePossibleSleepTime = false: do nothing");
        }


        Date currentTime = GlobalFunction.parseTime(GlobalFunction.getCurrentTimeString());
        if (!GlobalFunction.isRealSleepTime(currentTime, context)) {
            if (isScreenOn) {
                Logger.i("[Should called once only] !isRealSleepTime + isScreenOn = isWakenUp + stopService");
                PreferencesHelper.setIsWakenUpBool(true, context);
                GlobalFunction.startOrStopScreenServiceIntent(context);

                // TODO: Change StatisticActivity to YesterdayActivity
                Intent notificationIntent = new Intent(context, StatisticActivity.class);
                PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, 0);

                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("早安\n\n\n長文字"))
                        .setContentTitle("早安")
                        .setContentText("輕觸查看詳細信息")
                        .setContentIntent(notificationPendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setCategory(Notification.CATEGORY_EVENT)
                        .setAutoCancel(true)
                        .build();
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, notification);


                ScreenOpsRecord endRecord = realm.where(ScreenOpsRecord.class)
                        .equalTo("operation", "on")
                        .findAllSorted("time", Sort.DESCENDING)
                        .first();
                Logger.v("endRecord: " + endRecord);

                realm.beginTransaction();
                // Can simply set as realm object is lazy
                endRecord.setLastRecord(true);
                realm.commitTransaction();

                Logger.v("endRecord (new): " + endRecord);


                RealmResults<ScreenOpsRecord> allIsLastRecord = realm.where(ScreenOpsRecord.class)
                        .equalTo("isLastRecord", true)
                        .findAllSorted("time", Sort.DESCENDING);
                Logger.v("allIsLastRecord: " + allIsLastRecord);
                Logger.v("allIsLastRecord.size(): " + allIsLastRecord.size());

                ScreenOpsRecord startRecord;
                if (allIsLastRecord.size() <= 1) {
                    // If only 1 (0 is impossible) "isLastRecord" record is found (i.e. it is the first time user wakes up after installing this app)
                    startRecord = realm.where(ScreenOpsRecord.class)
                            .findAllSorted("time", Sort.ASCENDING)
                            .get(0);
                } else {
                    Date secondLastIsLastRecordTime = allIsLastRecord.get(1).getTime();
                    startRecord = realm.where(ScreenOpsRecord.class)
                            .greaterThan("time", secondLastIsLastRecordTime)
                            .findAllSorted("time", Sort.ASCENDING)
                            .get(0);
                }
                Logger.v("startRecord: " + startRecord);


                GlobalFunction.removeRepeatingOperationsInTimeRange(realm, startRecord.getTime(), endRecord.getTime());

            }
        }

        realm.close();


    }

}