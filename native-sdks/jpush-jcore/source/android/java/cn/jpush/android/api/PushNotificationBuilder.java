package cn.jpush.android.api;

import android.app.Notification;
import android.content.Context;
import java.util.Map;

public interface PushNotificationBuilder {
   Notification buildNotification(Map var1);

   Notification buildNotification(Context var1, NotificationMessage var2);

   String getDeveloperArg0();
}
