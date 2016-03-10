package dev.xesam.android.logtools.demo;

import android.app.Application;

import dev.xesam.android.logtools.CrashLogger;
import dev.xesam.android.logtools.L;
import dev.xesam.android.logtools.FileLogger;

/**
 * Created by xesamguo@gmail.com on 10/7/15.
 */
public class LogApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashLogger.register(this);
        L.enable(true);
        FileLogger.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FileLogger.recycle();
    }
}
