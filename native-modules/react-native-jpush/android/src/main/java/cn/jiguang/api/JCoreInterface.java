package cn.jiguang.api;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.internal.ActionManager;
import org.json.JSONObject;

public class JCoreInterface {
    public static String DAEMON_ACTION = "cn.jpush.android.intent.DaemonService";
    private static Context mApplicationContext;

    public static void initAction(String sdkType, Class<?> actionClass) {
        if (actionClass != null) {
            ActionManager.addAction(sdkType, actionClass.getName());
        }
    }

    public static void initActionExtra(String sdkType, Class<?> actionExtraClass) {
        if (actionExtraClass != null) {
            ActionManager.addAction(sdkType, actionExtraClass.getName());
        }
    }

    public static boolean init(Context context, boolean force) {
        if (context != null) {
            try {
                Context app = context.getApplicationContext();
                mApplicationContext = app != null ? app : context;
            } catch (Throwable ignored) {
                mApplicationContext = context;
            }
        }
        JCoreManager.init(context);
        return true;
    }

    public static void register(Context context) {
        init(context, false);
    }

    public static void sendData(Context context, String sdkType, int version, byte[] data) {
        JCoreManager.onEvent(context, sdkType, 50, null, null, data);
    }

    public static void sendRequestData(Context context, String sdkType, int timeout, byte[] data) {
        JCoreManager.onEvent(context, sdkType, 17, null, null, data);
    }

    public static void sendAction(Context context, String sdkType, Bundle bundle) {
        if (bundle != null) {
            JCoreManager.onEvent(context, sdkType, 3, bundle.getString("action"), bundle);
        }
    }

    public static void stop(Context context, String sdkType, Bundle bundle) {
        JCoreStorage.setPushStopped(context, true);
        JCoreTcpEngine.getInstance().stop(context);
    }

    public static void restart(Context context, String sdkType, Bundle bundle, boolean force) {
        JCoreStorage.setPushStopped(context, false);
        if (force) {
            JCoreTcpEngine.getInstance().triggerReconnectNow(context);
        } else {
            JCoreTcpEngine.getInstance().start(context);
        }
    }

    public static String getDeviceId(Context context) {
        return JCoreStorage.getDeviceId(context);
    }

    public static boolean canCallDirect() {
        return true;
    }

    public static void setDebugMode(boolean enable) {
        JCoreManager.setDebugMode(enable);
    }

    public static boolean getDebugMode() {
        return JCoreManager.getDebugMode();
    }

    public static void execute(String name, Runnable task, int... flags) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static void asyncExecute(Runnable task, int... flags) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static void putSingleExecutor(String name) {
    }

    public static void setAnalysisAction(JAnalyticsAction action) {
        JCoreManager.setAnalysisAction(action);
    }

    public static String getRegistrationID(Context context) {
        return JCoreStorage.getRegistrationId(context != null ? context : mApplicationContext);
    }

    public static boolean isValidRegistered() {
        return !getRegistrationID(mApplicationContext).isEmpty();
    }

    public static String getAppKey() {
        return JCoreStorage.getAppKey(mApplicationContext);
    }

    public static long getReportTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public static long getUid() {
        return JCoreStorage.getUid(mApplicationContext);
    }

    public static int getSid() {
        return JCoreTcpEngine.getInstance().getSessionId();
    }

    public static long getNextRid() {
        return JCoreStorage.nextRid();
    }

    public static boolean isTcpConnected() {
        return JCoreStorage.isConnected(mApplicationContext);
    }

    public static boolean getConnectionState(Context context) {
        return JCoreStorage.isConnected(context != null ? context : mApplicationContext);
    }

    public static String getChannel() {
        return JCoreStorage.getChannel(mApplicationContext);
    }

    public static boolean reportHttpData(Context context, Object data, String sdkType) {
        return true;
    }

    public static boolean report(Context context, JSONObject data, boolean checkAuth) {
        return true;
    }

    public static String getHttpConfig(Context context, String key) {
        return "";
    }

    public static boolean getRuningFlag() {
        return false;
    }

    @Deprecated
    public static void setImLBSEnable(Context context, boolean enable) {
    }

    public static void setWakeEnable(Context context, boolean enable) {
    }

    public static void onResume(Context context) {
    }

    public static void onPause(Context context) {
    }

    public static void onFragmentResume(Context context, String fragmentName) {
    }

    public static void onFragmentPause(Context context, String fragmentName) {
    }

    public static void onKillProcess(Context context) {
    }

    public static void initCrashHandler(Context context) {
    }

    public static void stopCrashHandler(Context context) {
    }

    public static void requestPermission(Context context) {
        JCoreManager.requestPermission(context);
    }

    public static void setCanLaunchedStoppedService(boolean enabled) {
    }

    public static void setTestConn(boolean testConn) {
        JCoreTcpEngine.getInstance().setTestConnectionEnabled(testConn);
    }

    public static void setTestConnIPPort(String ip, int port) {
        JCoreTcpEngine.getInstance().setCustomEndpoint(ip, port);
    }

    public static void setPowerSaveMode(Context context, boolean enable) {
    }

    public static void setHeartbeatInterval(Context context, int intervalSeconds) {
        if (intervalSeconds > 0) {
            long clampedSeconds = Math.max(30L, Math.min(290L, (long) intervalSeconds));
            JCoreTcpEngine.getInstance().setHeartbeatIntervalMs(clampedSeconds * 1000L);
        }
    }

    public static void enableAutoWakeup(Context context, boolean enable) {
    }

    public static void setCountryCode(Context context, String countryCode) {
        JCoreStorage.setCountryCode(context, countryCode);
    }

    public static void enableSDKLocalLog(Context context, boolean enable, boolean uploadJgToServer) {
        JCoreStorage.enableSDKLocalLog(context, enable, uploadJgToServer);
    }

    public static String readNewLogs(Context context) {
        return JCoreStorage.readNewLogs(context);
    }

    public static void triggerSceneCheck(Context context, int scene) {
    }
}
