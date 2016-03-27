package dev.xesam.android.logtools.demo;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.xesam.android.logtools.FileLogger;
import dev.xesam.android.logtools.L;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.CrashLog)
    public void testCrashLog() {
        throw new RuntimeException("test crash log");
    }

    @OnClick(R.id.FileLogger)
    public void testFileLogger() {
        FileLogger.newRound();
        FileLogger.log("this is file log");
    }

    @OnClick(R.id.L)
    public void testL() {
        L.d();
        L.d(null);
        L.d(null, null);
        L.d(this);
        L.d(this, this);
        L.d(1);
        L.d(1, 2);
        L.d("a");
        L.d("a", "b");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
        sb.append("end");
        L.d(this, sb);

        new Thread(new Runnable() {
            @Override
            public void run() {
                L.e(this, "run!run!run!");
            }
        }).start();

    }
}
