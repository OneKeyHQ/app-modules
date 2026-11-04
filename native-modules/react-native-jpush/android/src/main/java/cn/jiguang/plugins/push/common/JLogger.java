package cn.jiguang.plugins.push.common;

import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JLogger {

    public static final String TAG = "RN-JPush";

    private static volatile boolean isLoggerEnable = true;
    private static final int MAX_RECENT_LOGS = 200;
    private static final List<String> RECENT_LOGS = new ArrayList<String>();

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

    private static void recordRecentLog(String level, String msg) {
        String entry = "[" + level + "] " + (msg != null ? msg : "");
        synchronized (RECENT_LOGS) {
            if (RECENT_LOGS.size() >= MAX_RECENT_LOGS) {
                RECENT_LOGS.remove(0);
            }
            RECENT_LOGS.add(entry);
        }
    }

    public static List<String> getRecentLogs() {
        synchronized (RECENT_LOGS) {
            return new ArrayList<String>(RECENT_LOGS);
        }
    }

    public static void clearRecentLogs() {
        synchronized (RECENT_LOGS) {
            RECENT_LOGS.clear();
        }
    }

    public static boolean isLoggerEnable() {
        return isLoggerEnable;
    }

    public static void setLoggerEnable(boolean loggerEnable) {
        isLoggerEnable = loggerEnable;
        dispatch("INFO", sInfoMethod, Log.INFO, "setLoggerEnable=" + loggerEnable, true);
    }

    private static void dispatch(String level, Method oneKeyMethod, int priority, String msg, boolean force) {
        if (!force && !isLoggerEnable) {
            return;
        }
        String safeMsg = msg != null ? msg : "";
        recordRecentLog(level, safeMsg);
        if (cn.jiguang.core.JCoreStorage.isLocalLogEnabled(null)) {
            cn.jiguang.core.JCoreStorage.appendLocalLog("[" + level + "] " + safeMsg);
        }
        resolveOneKeyLogIfNeeded();
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
        dispatch("INFO", sInfoMethod, Log.INFO, msg, false);
    }

    public static void d(String msg) {
        dispatch("DEBUG", sDebugMethod, Log.DEBUG, msg, false);
    }

    public static void v(String msg) {
        dispatch("DEBUG", sDebugMethod, Log.VERBOSE, msg, false);
    }

    public static void w(String msg) {
        dispatch("WARN", sWarnMethod, Log.WARN, msg, false);
    }

    public static void e(String error) {
        dispatch("ERROR", sErrorMethod, Log.ERROR, error, false);
    }

}

