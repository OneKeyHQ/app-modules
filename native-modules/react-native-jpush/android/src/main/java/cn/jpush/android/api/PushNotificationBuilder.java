package cn.jpush.android.api;

import android.app.Notification;
import android.content.Context;
import java.util.Map;

public interface PushNotificationBuilder {
    Notification buildNotification(Map<String, String> params);

    Notification buildNotification(Context context, NotificationMessage message);

    String getDeveloperArg0();
}
