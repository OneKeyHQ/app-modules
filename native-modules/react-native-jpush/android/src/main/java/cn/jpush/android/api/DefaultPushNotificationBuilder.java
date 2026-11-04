package cn.jpush.android.api;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;
import java.util.HashMap;
import java.util.Map;

public class DefaultPushNotificationBuilder implements PushNotificationBuilder {
    public static final int NOTI_STYLE_BIG_TEXT = 1;
    public static final int NOTI_STYLE_INBOX = 2;
    public static final int NOTI_STYLE_BIG_PICTURE = 3;

    protected Context context;
    protected NotificationMessage notificationMessage;

    @Override
    public Notification buildNotification(Context context, NotificationMessage message) {
        this.context = context;
        this.notificationMessage = message;
        Map<String, String> params = new HashMap<String, String>();
        if (message != null) {
            params.put(JPushInterface.EXTRA_MSG_ID, message.msgId);
            params.put(JPushInterface.EXTRA_ALERT, message.notificationContent);
            params.put(JPushInterface.EXTRA_ALERT_TYPE, String.valueOf(message.notificationAlertType));
            if (!TextUtils.isEmpty(message.notificationTitle)) {
                params.put(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
            }
            if (!TextUtils.isEmpty(message.notificationExtras)) {
                params.put(JPushInterface.EXTRA_EXTRA, message.notificationExtras);
            }
        }
        return buildNotification(params);
    }

    @Override
    public Notification buildNotification(Map<String, String> params) {
        if (this.context == null || params == null) {
            return null;
        }
        String alert = params.get(JPushInterface.EXTRA_ALERT);
        if (TextUtils.isEmpty(alert)) {
            return null;
        }
        String title = params.get(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        if (TextUtils.isEmpty(title)) {
            try {
                title = this.context.getApplicationInfo().loadLabel(this.context.getPackageManager()).toString();
            } catch (Throwable ignored) {
                title = "";
            }
        }

        Notification.Builder builder = new Notification.Builder(this.context);
        builder.setContentTitle(title).setContentText(alert).setTicker(alert);
        int iconRes = this.context.getApplicationInfo() != null ? this.context.getApplicationInfo().icon : 0;
        builder.setSmallIcon(iconRes != 0 ? iconRes : android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= 17) {
            builder.setShowWhen(true);
        }

        RemoteViews customView = buildContentView(alert, title);
        if (customView != null) {
            builder.setContent(customView);
        }
        Notification notification = getNotification(builder);
        if (notification != null) {
            resetNotificationParams(notification);
        }
        return notification;
    }

    RemoteViews buildContentView(String content, String title) {
        return null;
    }

    void resetNotificationParams(Notification notification) {
    }

    Notification getNotification(Notification.Builder builder) {
        return builder.build();
    }

    @Override
    public String getDeveloperArg0() {
        return null;
    }
}
