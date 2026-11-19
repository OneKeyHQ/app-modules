package cn.jiguang.plugins.push.common;

import android.util.Log;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class JLogger {

    public static final String TAG = "RN-JPush";

    private static volatile boolean isLoggerEnable = true;

    private static volatile boolean sOneKeyLogResolved = false;
    private static Method sDebugMethod;
    private static Method sInfoMethod;
    private static Method sWarnMethod;
    private static Method sErrorMethod;

    private static void resolveOneKeyLogIfNeeded() {
        if (sOneKeyLogResolved) {
            return;
        }
        synchronized (JLogger.class) {
            if (sOneKeyLogResolved) {
                return;
            }
            try {
                Class<?> cls = Class.forName("com.margelo.nitro.nativelogger.OneKeyLog");
                sDebugMethod = cls.getMethod("debug", String.class, String.class);
                sInfoMethod = cls.getMethod("info", String.class, String.class);
                sWarnMethod = cls.getMethod("warn", String.class, String.class);
                sErrorMethod = cls.getMethod("error", String.class, String.class);
            } catch (Throwable ignored) {
                sDebugMethod = null;
                sInfoMethod = null;
                sWarnMethod = null;
                sErrorMethod = null;
            } finally {
                sOneKeyLogResolved = true;
            }
        }
    }

    private static Method selectOneKeyMethod(int priority) {
        switch (priority) {
            case Log.INFO:
                return sInfoMethod;
            case Log.WARN:
                return sWarnMethod;
            case Log.ERROR:
                return sErrorMethod;
            default:
                return sDebugMethod;
        }
    }

    public static List<String> getRecentLogs() {
        return Collections.emptyList();
    }

    public static void clearRecentLogs() {
    }

    public static boolean isLoggerEnable() {
        return isLoggerEnable;
    }

    public static void setLoggerEnable(boolean loggerEnable) {
        isLoggerEnable = loggerEnable;
    }

    private static void dispatch(int priority, String msg) {
        if (!isLoggerEnable) {
            return;
        }
        String safeMsg = msg != null ? msg : "";
        resolveOneKeyLogIfNeeded();
        Method oneKeyMethod = selectOneKeyMethod(priority);
        if (oneKeyMethod != null) {
            try {
                oneKeyMethod.invoke(null, TAG, safeMsg);
                return;
            } catch (Throwable ignored) {
            }
        }
        try {
            Log.println(priority, TAG, safeMsg);
        } catch (Throwable ignored) {
        }
    }

    public static void i(String msg) {
        dispatch(Log.INFO, msg);
    }

    public static void d(String msg) {
        dispatch(Log.DEBUG, msg);
    }

    public static void v(String msg) {
        dispatch(Log.VERBOSE, msg);
    }

    public static void w(String msg) {
        dispatch(Log.WARN, msg);
    }

    public static void e(String error) {
        dispatch(Log.ERROR, error);
    }

}

