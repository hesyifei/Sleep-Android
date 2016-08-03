package com.arefly.sleep;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

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
    }
}