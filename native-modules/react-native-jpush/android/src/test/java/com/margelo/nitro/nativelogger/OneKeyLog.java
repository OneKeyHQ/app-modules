package com.margelo.nitro.nativelogger;

import java.util.ArrayList;
import java.util.List;

public final class OneKeyLog {
    private static final List<String> CAPTURED_LOGS = new ArrayList<String>();

    private OneKeyLog() {
    }

    private static void record(String level, String tag, String message) {
        synchronized (CAPTURED_LOGS) {
            CAPTURED_LOGS.add("[" + level + "] [" + (tag != null ? tag : "") + "] " + (message != null ? message : ""));
        }
    }

    public static void debug(String tag, String message) {
        record("DEBUG", tag, message);
    }

    public static void info(String tag, String message) {
        record("INFO", tag, message);
    }

    public static void warn(String tag, String message) {
        record("WARN", tag, message);
    }

    public static void error(String tag, String message) {
        record("ERROR", tag, message);
    }

    public static List<String> getCapturedLogs() {
        synchronized (CAPTURED_LOGS) {
            return new ArrayList<String>(CAPTURED_LOGS);
        }
    }

    public static void clearCapturedLogs() {
        synchronized (CAPTURED_LOGS) {
            CAPTURED_LOGS.clear();
        }
    }
}
