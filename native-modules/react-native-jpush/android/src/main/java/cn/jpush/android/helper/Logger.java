package cn.jpush.android.helper;

import android.util.Log;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.plugins.push.common.JLogger;

public class Logger {
    private static final String PREFIX = "[JPush]";

    private static void log(int priority, boolean force, String tag, String msg, Throwable tr) {
        if (!force && !JCoreStorage.isDebugMode() && !JLogger.isLoggerEnable()) {
            return;
        }
        String fullTag = PREFIX + (tag != null ? tag : "");
        String body = msg != null ? msg : "";
        if (tr != null) {
            body = body + '\n' + Log.getStackTraceString(tr);
        }
        String combined = fullTag + " " + body;
        if (priority >= Log.ERROR) {
            JLogger.e(combined);
        } else if (priority == Log.WARN) {
            JLogger.w(combined);
        } else if (priority == Log.INFO) {
            JLogger.i(combined);
        } else {
            JLogger.d(combined);
        }
    }

    public static void v(String tag, String msg) {
        log(Log.VERBOSE, false, tag, msg, null);
    }

    public static void vv(String tag, String msg) {
        log(Log.VERBOSE, true, tag, msg, null);
    }

    public static void d(String tag, String msg) {
        log(Log.DEBUG, false, tag, msg, null);
    }

    public static void dd(String tag, String msg) {
        log(Log.DEBUG, true, tag, msg, null);
    }

    public static void i(String tag, String msg) {
        log(Log.INFO, false, tag, msg, null);
    }

    public static void ii(String tag, String msg) {
        log(Log.INFO, true, tag, msg, null);
    }

    public static void w(String tag, String msg) {
        log(Log.WARN, false, tag, msg, null);
    }

    public static void ww(String tag, String msg) {
        log(Log.WARN, true, tag, msg, null);
    }

    public static void ww(String tag, String msg, Throwable tr) {
        log(Log.WARN, true, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        log(Log.ERROR, false, tag, msg, null);
    }

    public static void ee(String tag, String msg) {
        log(Log.ERROR, true, tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        log(Log.ERROR, false, tag, msg, tr);
    }

    public static void ee(String tag, String msg, Throwable tr) {
        log(Log.ERROR, true, tag, msg, tr);
    }
}
