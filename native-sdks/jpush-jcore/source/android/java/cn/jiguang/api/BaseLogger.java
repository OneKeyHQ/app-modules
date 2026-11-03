package cn.jiguang.api;

import android.util.Log;
import cn.jiguang.core.JCoreStorage;

public abstract class BaseLogger {
    public abstract String getCommonTag();

    private void log(int priority, boolean force, String tag, String msg, Throwable tr) {
        if (!force && !JCoreStorage.isDebugMode()) {
            return;
        }
        String fullTag = "[" + getCommonTag() + "]" + (tag != null ? tag : "");
        String body = msg != null ? msg : "";
        if (tr != null) {
            body = body + '\n' + Log.getStackTraceString(tr);
        }
        Log.println(priority, fullTag, body);
    }

    public void v(String tag, String msg) {
        log(Log.VERBOSE, false, tag, msg, null);
    }

    public void vv(String tag, String msg) {
        log(Log.VERBOSE, true, tag, msg, null);
    }

    public void d(String tag, String msg) {
        log(Log.DEBUG, false, tag, msg, null);
    }

    public void dd(String tag, String msg) {
        log(Log.DEBUG, true, tag, msg, null);
    }

    public void i(String tag, String msg) {
        log(Log.INFO, false, tag, msg, null);
    }

    public void ii(String tag, String msg) {
        log(Log.INFO, true, tag, msg, null);
    }

    public void w(String tag, String msg) {
        log(Log.WARN, false, tag, msg, null);
    }

    public void ww(String tag, String msg) {
        log(Log.WARN, true, tag, msg, null);
    }

    public void ww(String tag, String msg, Throwable tr) {
        log(Log.WARN, true, tag, msg, tr);
    }

    public void e(String tag, String msg) {
        log(Log.ERROR, false, tag, msg, null);
    }

    public void ee(String tag, String msg) {
        log(Log.ERROR, true, tag, msg, null);
    }

    public void e(String tag, String msg, Throwable tr) {
        log(Log.ERROR, false, tag, msg, tr);
    }

    public void ee(String tag, String msg, Throwable tr) {
        log(Log.ERROR, true, tag, msg, tr);
    }
}
