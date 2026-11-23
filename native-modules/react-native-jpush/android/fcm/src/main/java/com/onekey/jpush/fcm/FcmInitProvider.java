package com.onekey.jpush.fcm;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.service.InitProvider;

public final class FcmInitProvider extends InitProvider {
    @Override
    public boolean onCreate() {
        super.onCreate();
        Context context = getContext();
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationManager notifications = context.getSystemService(NotificationManager.class);
            // Firebase caches delegation initialization across upgrades, so manifest opt-out alone
            // does not revoke an existing Play services delegate.
            if (notifications != null && "com.google.android.gms".equals(notifications.getNotificationDelegate())) {
                notifications.setNotificationDelegate(null);
                JLogger.i("FCM notification delegation disabled");
            }
        }
        return true;
    }
}
