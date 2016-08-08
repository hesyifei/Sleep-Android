package com.arefly.sleep.applications;

import android.app.Application;

import com.arefly.sleep.BuildConfig;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class SleepApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init("LOGGER-SLEEP")
                .methodCount(0)
                .hideThreadInfo()
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);       // default LogLevel.FULL

        //Logger.v("BuildConfig.DEBUG = " + BuildConfig.DEBUG);
        Logger.i("SleepApplication onCreate()");


        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this.getApplicationContext())
                .deleteRealmIfMigrationNeeded()                 // TODO: Maybe need real migration instead of simply deleting
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}