package cn.jiguang.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;
import java.util.List;

public class JCoreManager {
    public static final boolean DEVELOPE_MODE = false;
    public static final String SDK_NAME = "JCore";
    public static final String SDK_VERSION = "5.2.0";

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, Bundle bundle) {
        Context app = JConstants.getAppContext(context);
        if (app == null) {
            return;
        }
        JCoreTcpEngine.getInstance().start(app);
    }

    public static void init(Context context, int flags, Bundle bundle) {
        init(context, bundle);
    }

    public static void register(Context context, String appKey) {
        JCoreStorage.setAppKey(context, appKey);
        init(context, null);
    }

    public static void unRegister(Context context) {
        JCoreStorage.setConnected(context, false);
    }

    public static void changeLiveStatus(boolean live) {
    }

    public static Context getAppContext(Context context) {
        return JConstants.getAppContext(context);
    }

    @Deprecated
    public static boolean getConnectionState(Context context) {
        return JCoreStorage.isConnected(context);
    }

    public static void setDebugMode(boolean debug) {
        JConstants.DEBUG_MODE = debug;
        JCoreStorage.setDebugMode(debug);
    }

    public static boolean getDebugMode() {
        return JCoreStorage.isDebugMode();
    }

    public static boolean isInternal() {
        return JConstants.INTERNAL_USE;
    }

    public static boolean isTestEnv() {
        return JConstants.isTestEnv();
    }

    public static void setAnalysisAction(JAnalyticsAction action) {
        JConstants.jAnalyticsAction = action;
    }

    public static void setSDKConfigs(Context context, Bundle config) {
    }

    public static void setLBSEnable(Context context, boolean enable) {
    }

    public static void initCrashHandler(Context context) {
    }

    public static void stopCrashHandler(Context context) {
    }

    public static void addDispatchAction(String sdkType, String className) {
        ActionManager.addAction(sdkType, className);
    }

    public static void requestPermission(Context context) {
        if (context instanceof Activity) {
            // Permission handling is delegated to Android system / RN layer.
        }
    }

    public static List<String> getau(Context context) {
        return JConstants.TUU_LIST;
    }

    public static Object onEvent(Context context, String sdkType, int cmd, String action, Bundle bundle, Object... extras) {
        return JCoreInternalHelper.getInstance().onEvent(context, sdkType, cmd, true, action, bundle, extras);
    }

    public static Object onEvent(
        Context context,
        String sdkType,
        int cmd,
        boolean async,
        String action,
        Bundle bundle,
        Object... extras
    ) {
        return JCoreInternalHelper.getInstance().onEvent(context, sdkType, cmd, async, action, bundle, extras);
    }
}
