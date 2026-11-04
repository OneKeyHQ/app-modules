package cn.jiguang.api;

import android.content.Context;
import android.content.SharedPreferences;
import cn.jiguang.core.JCoreStorage;

public class MultiSpHelper {
    private static final String SP_FILE = "cn.jpush.android.user.profile";
    private static SharedPreferences sharedPref;

    private MultiSpHelper() {
    }

    public static void commitString(Context context, String key, String value) {
        JCoreStorage.putSpValue(context, key, value != null ? value : "");
        SharedPreferences sp = getSp(context);
        if (sp != null) {
            sp.edit().putString(key, value).apply();
        }
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = getSp(context);
        if (sp != null) {
            String val = sp.getString(key, null);
            if (val != null) {
                return val;
            }
        }
        return JCoreStorage.getSpValue(context, key, defaultValue);
    }

    public static void commitLong(Context context, String key, long value) {
        JCoreStorage.putSpValue(context, key, String.valueOf(value));
        SharedPreferences sp = getSp(context);
        if (sp != null) {
            sp.edit().putLong(key, value).apply();
        }
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = getSp(context);
        if (sp != null && sp.contains(key)) {
            return sp.getLong(key, defaultValue);
        }
        String raw = JCoreStorage.getSpValue(context, key, null);
        if (raw != null && !raw.isEmpty()) {
            try {
                return Long.parseLong(raw);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public static void commitInt(Context context, String key, int value) {
        JCoreStorage.putSpValue(context, key, String.valueOf(value));
        SharedPreferences sp = getSp(context);
        if (sp != null) {
            sp.edit().putInt(key, value).apply();
        }
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = getSp(context);
        if (sp != null && sp.contains(key)) {
            return sp.getInt(key, defaultValue);
        }
        String raw = JCoreStorage.getSpValue(context, key, null);
        if (raw != null && !raw.isEmpty()) {
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public static void commitBoolean(Context context, String key, boolean value) {
        JCoreStorage.putSpValue(context, key, String.valueOf(value));
        SharedPreferences sp = getSp(context);
        if (sp != null) {
            sp.edit().putBoolean(key, value).apply();
        }
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = getSp(context);
        if (sp != null && sp.contains(key)) {
            return sp.getBoolean(key, defaultValue);
        }
        String raw = JCoreStorage.getSpValue(context, key, null);
        if (raw != null && !raw.isEmpty()) {
            return Boolean.parseBoolean(raw);
        }
        return defaultValue;
    }

    private static SharedPreferences getSp(Context context) {
        if (context == null) {
            return null;
        }
        if (sharedPref == null) {
            Context appContext = context.getApplicationContext();
            Context target = appContext != null ? appContext : context;
            sharedPref = target.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        }
        return sharedPref;
    }
}
