package dev.xesam.android.logtools.demo;

import android.app.Application;

import dev.xesam.android.logtools.CrashLog;

/**
 * Created by xesamguo@gmail.com on 10/7/15.
 */
public class LogApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashLog.register(this);
    }
}
