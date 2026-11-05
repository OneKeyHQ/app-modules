package cn.jpush.android.helper;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jpush.android.local.JPushConstants;

public class JCoreHelper {
    public static void runActionWithService(Context context, String sdkType, String action, Bundle bundle) {
        JCoreManager.onEvent(context, sdkType, 3, action, bundle);
    }

    public static void execue(Context context, String threadName, Runnable task) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static void majorExecutor(Context context, String threadName, Runnable task) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static void normalExecutor(Context context, Object tag, Runnable task) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static void futureExecutor(Context context, Runnable task) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static void futureExecutor(Context context, String tag, Runnable task) {
        JCoreTcpEngine.getInstance().executeAsync(task);
    }

    public static String getAppKey(Context context) {
        return JCoreStorage.getAppKey(context);
    }

    public static String getRegistrationID(Context context) {
        return JCoreStorage.getRegistrationId(context);
    }

    public static String getDeviceId(Context context) {
        return JCoreStorage.getDeviceId(context);
    }

    public static long getUid(Context context) {
        return JCoreStorage.getUid(context);
    }

    public static boolean isAuth(Context context) {
        return JCoreStorage.isCollectionAuth(context);
    }

    public static void setChannel(Context context, String channel) {
        JCoreStorage.setChannel(context, channel);
    }

    public static void registerSdkType(Context context, String appKey) {
        JCoreManager.register(context, appKey);
    }

    public static int getJPushSDKVersionInt() {
        return JPushConstants.SDK_VERSION_CODE;
    }

    public static int getJCoreSDKVersionInt() {
        return 520;
    }
}
