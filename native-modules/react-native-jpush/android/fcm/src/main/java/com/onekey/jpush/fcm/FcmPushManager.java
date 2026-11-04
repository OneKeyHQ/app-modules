package com.onekey.jpush.fcm;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JThirdPlatFormInterface;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public final class FcmPushManager extends JThirdPlatFormInterface {
    @Override
    public void init(Context context) {
        FirebaseApp.initializeApp(context);
    }

    @Override
    public void register(Context context, Bundle options) {
        init(context);
    }

    @Override
    public String getToken(final Context context) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception error = task.getException();
                JLogger.w("FCM token request failed type="
                    + (error != null ? error.getClass().getSimpleName() : "unknown"));
                return;
            }
            acceptToken(context, task.getResult());
        });
        return JCoreStorage.getThirdToken(context, (byte) 8);
    }

    static void acceptToken(Context context, String token) {
        if (token == null || token.isEmpty() || JCoreStorage.isPushStopped(context)
            || !JCoreStorage.isCollectionAuth(context)) {
            return;
        }
        Bundle result = new Bundle();
        result.putString(KEY_TOKEN, token);
        result.putByte(KEY_PLATFORM, (byte) 8);
        doAction(context, ACTION_REGISTER_TOKEN, result);
        JLogger.i("FCM token submitted to JCore");
    }

    @Override
    public boolean isSupport(Context context) {
        if (context == null) {
            return false;
        }
        int status = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context);
        if (status != 0) {
            JLogger.w("FCM Google Play services unavailable code=" + status);
        }
        return status == 0;
    }

    @Override public byte getRomType(Context context) { return 8; }
    @Override public String getRomName() { return "fcm"; }
    @Override public boolean isNeedClearToken(Context context) { return false; }
    @Override public String getAppkey(Context context) { return FirebaseApp.getInstance().getOptions().getApiKey(); }
    @Override public String getAppId(Context context) { return FirebaseApp.getInstance().getOptions().getApplicationId(); }
    @Override public void clearNotification(Context context, int id) { JPushInterface.clearNotificationById(context, id); }
    @Override public void clearAllNotification(Context context) { JPushInterface.clearAllNotifications(context); }
}
