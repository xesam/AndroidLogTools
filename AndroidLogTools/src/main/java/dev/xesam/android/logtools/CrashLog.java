package dev.xesam.android.logtools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * write crash log to file
 */
public class CrashLog implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Context mContext;
    private Map<String, String> logs = new HashMap<>();

    private CrashLog(Context context) {
        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static void register(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashLog(context));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(thread, ex);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        collectDeviceInfo();
        saveCrashInfo(ex);
        return true;
    }

    private void collectDeviceInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "unknown" : pi.versionName;
                int versionCode = pi.versionCode;
                logs.put("versionName", versionName);
                logs.put("versionCode", versionCode + "");
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    logs.put(field.getName(), field.get(null).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存错误信息到文件中
     */
    private void saveCrashInfo(Throwable ex) {

        //打印设备信息
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : logs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        //打印错误信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        sb.append("\n\n").append(writer.toString());

        //写文件
        try {
            String datetime = (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA)).format(new Date());
            String logFileName = String.format("crash-%s.txt", datetime);

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = mContext.getExternalFilesDir(null);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File logFile = new File(file.getAbsolutePath() + File.separator + logFileName);
                FileOutputStream fos = new FileOutputStream(logFile);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}