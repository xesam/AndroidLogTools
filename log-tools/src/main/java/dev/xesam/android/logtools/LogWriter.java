package dev.xesam.android.logtools;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志控制台
 * <p/>
 * Created by xesamguo@gmail.com on 16-3-8.
 */
public final class LogWriter {
    private static final int MSG_1 = 0X1;
    private static Handler writerHandler;
    private static Context writerContext;
    private static FileWriter writer;

    private static HandlerThread handlerThread = new HandlerThread("writer") {
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            writerHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_1: {
                            if (writer != null) {
                                try {
                                    writer.write(msg.obj.toString() + "\n");
                                    writer.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                        default: {

                        }
                    }
                    return true;
                }
            });
        }
    };

    synchronized public static void init(Context context) {
        writerContext = context.getApplicationContext();

        try {
            if (writer != null) {
                writer.close();
            }

            String filename = new SimpleDateFormat("yyyy-MM-dd-HH:mm").format(new Date()) + ".txt";
            File dir = context.getExternalFilesDir(null);
            if (dir != null) {

                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        throw new RuntimeException("create log file fail");
                    }
                }
                File file = new File(dir.getAbsolutePath() + File.separator + filename);
                writer = new FileWriter(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!handlerThread.isAlive()) {
            handlerThread.start();
        }
    }

    public static void write(Parcelable data) {
        if (writerHandler != null) {
            writerHandler.obtainMessage(MSG_1, data).sendToTarget();
        }
    }

}
