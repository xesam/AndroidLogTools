package dev.xesam.android.logtools;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志控制台
 * Created by xesamguo@gmail.com on 16-3-8.
 */
public final class FileLogger {

    public static final String TAG = FileLogger.class.getSimpleName();

    private static final int MSG_1 = 0X1;
    private static Handler writerHandler;
    private static Context writerContext;
    private static volatile Writer writer;

    private static boolean mInit = false;

    private static HandlerThread handlerThread;

    synchronized public static void init(Context context) {
        if (mInit) {
            throw new RuntimeException("init twice");
        }

        mInit = true;
        writerContext = context.getApplicationContext();
        handlerThread = new HandlerThread(TAG) {
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
                                        writer.write(msg.obj.toString());
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
        handlerThread.start();
    }

    synchronized public static void recycle() {
        mInit = false;
        writerContext = null;

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = null;
        }

        if (handlerThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                handlerThread.quitSafely();
            } else {
                handlerThread.quit();
            }
            handlerThread = null;
        }
    }

    private static void createNewLog(File file, boolean append) {
        try {
            createNewLog(new FileWriter(file, append));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createNewLog(Writer fileWriter) {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writer = fileWriter;
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound() {
        newRound(false);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(boolean append) {
        String dateTime = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)).format(new Date());
        String filename = String.format(Locale.CHINA, "log-%s.txt", dateTime);
        newRound(filename, append);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(String filename) {
        newRound(filename, false);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(String filename, boolean append) {
        File dir = writerContext.getExternalFilesDir(null);
        if (dir != null) {
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e(TAG, "FileLogger.newRound(" + filename + ") fail!");
                    return;
                }
            }
        }
        newRound(dir, filename);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(File dir, String filename) {
        newRound(dir, filename, false);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(File dir, String filename, boolean append) {
        if (!mInit) {
            return;
        }
        File file = new File(dir.getAbsolutePath() + File.separator + filename);
        createNewLog(file, append);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(File file) {
        createNewLog(file, false);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(File file, boolean append) {
        if (!mInit) {
            return;
        }
        createNewLog(file, append);
    }

    /**
     * 新一轮日志记录
     */
    public synchronized static void newRound(Writer writer) {
        if (!mInit) {
            return;
        }
        createNewLog(writer);
    }

    public static void log(String data) {
        if (!mInit) {
            return;
        }

        if (writer == null) {
            newRound();
        }

        if (writerHandler != null) {
            writerHandler.obtainMessage(MSG_1, data).sendToTarget();
        }
    }

}
