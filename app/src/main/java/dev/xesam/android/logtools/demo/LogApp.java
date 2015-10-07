package dev.xesam.android.logtools.demo;

import android.app.Application;

import dev.xesam.android.logtools.CrashLog;
import dev.xesam.android.logtools.L;

/**
 * Created by xesamguo@gmail.com on 10/7/15.
 */
public class LogApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashLog.register(this);
        L.enable(true);
    }
}
