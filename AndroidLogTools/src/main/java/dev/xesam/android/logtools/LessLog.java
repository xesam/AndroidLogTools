package dev.xesam.android.logtools;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

/**
 * 简单的日志类
 */
public final class LessLog {
    private final static int TYPE_E = 0;
    private final static int TYPE_W = 1;
    private final static int TYPE_D = 2;
    private final static int TYPE_I = 3;
    private final static int TYPE_V = 4;

    private LessLog() {
    }

    private static boolean mEnable = false;

    public static void enable(boolean enable) {
        mEnable = enable;
    }

    public static void e(Object obj, Object... content) {
        log(TYPE_E, obj, content);

    }

    public static void w(Object obj, Object... content) {
        log(TYPE_W, obj, content);
    }

    public static void d(Object obj, Object... content) {
        log(TYPE_D, obj, content);
    }

    public static void i(Object obj, Object... content) {
        log(TYPE_I, obj, content);
    }

    public static void v(Object obj, Object... content) {
        log(TYPE_V, obj, content);
    }

    private static void _log(int type, String tag, String contentString) {

        switch (type) {
            case TYPE_E:
                Log.e(tag, contentString);
                break;
            case TYPE_W:
                Log.w(tag, contentString);
                break;
            case TYPE_D:
                Log.d(tag, contentString);
                break;
            case TYPE_I:
                Log.i(tag, contentString);
                break;
            case TYPE_V:
                Log.v(tag, contentString);
                break;
            default:
                break;
        }
    }

    private static void log(int type, Object obj, Object... content) {
        if (!mEnable) {
            return;
        }

        String tag = getTag(obj);
        String contentString = makeContent(content);

        int contentStringLength = contentString.length();
        int limit = 200;

        if (contentStringLength < limit) {
            _log(type, tag, contentString);
        } else {
            //修改日志意外中断错误
            int round = (int) Math.ceil(contentStringLength * 1f / limit * 1f);
            for (int i = 0; i < round; i++) {
                _log(type, tag, contentString.substring(limit * i, limit * (i + 1) < contentStringLength ? limit * (i + 1) : contentStringLength));
            }
        }
    }

    private static String makeContent(Object... content) {
        if (null == content) {
            return "null";
        }

        StringBuffer sb = new StringBuffer();
        for (Object c : content) {
            sb.append(null == c ? "null" : c.toString()).append(":");
        }
        return sb.toString();
    }

    private static String getTag(Object obj) {
        String tag = null;
        if (obj instanceof String) {
            tag = obj.toString();
        } else if (obj instanceof View) {
            tag = ((View) obj).getTag() != null ? ((View) obj).getTag().toString() : obj.getClass().getSimpleName();
        } else {
            String simpleName = obj.getClass().getSimpleName();
            tag = TextUtils.isEmpty(simpleName) ? obj.getClass().getName() : simpleName;
        }
        return tag;
    }
}
