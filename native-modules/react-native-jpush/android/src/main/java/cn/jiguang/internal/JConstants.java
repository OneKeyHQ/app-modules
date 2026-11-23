package cn.jiguang.internal;

import android.content.Context;
import cn.jiguang.api.JAnalyticsAction;
import cn.jiguang.core.JCoreStorage;
import java.util.LinkedList;

public class JConstants {
    public static final byte USER_SERVICE_ACTION_ON_CREATE = 1;
    public static final byte USER_SERVICE_ACTION_ON_START_COMMAND = 2;
    public static final String SDK_TYPE = "JCore";
    public static String SDK_NAME = "JCore";
    public static final String SDK_VERSION = "5.2.0";
    public static int SDK_VERSION_INT = 520;
    public static boolean DEBUG_MODE = false;
    public static boolean CMD_TO_PRINT_ALL_LOG = false;
    public static boolean INTERNAL_USE = false;
    public static boolean isInstrumentationHook = true;
    public static final boolean isDynamicLoad = false;
    public static final long SECOND = 1000L;
    public static final long MIN = 60000L;
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static int DEFAULT_HEARTBEAT_INTERVAL = 285;
    public static int DEFAULT_ALARM_DELAY = 290;
    public static String TUU = "";
    public static boolean AT_I = false;
    public static String APP_KEY = "";
    public static LinkedList<String> TUU_LIST = new LinkedList<String>();
    public static JAnalyticsAction jAnalyticsAction;
    public static Context mApplicationContext;

    public static Context getAppContext(Context context) {
        if (mApplicationContext == null && context != null) {
            try {
                Context app = context.getApplicationContext();
                mApplicationContext = app != null ? app : context;
            } catch (Throwable ignored) {
                mApplicationContext = context;
            }
            JCoreStorage.setAppContext(mApplicationContext);
        }
        return mApplicationContext != null ? mApplicationContext : JCoreStorage.getAppContext(context);
    }

    public static Context getAppContext() {
        return getAppContext(null);
    }

    public static boolean isTestEnv() {
        return false;
    }

    public static void setTestEnv(boolean testEnv) {
    }

    public static boolean isAndroidQ(Context context, boolean checkTargetSdk, String caller) {
        return true;
    }
}
