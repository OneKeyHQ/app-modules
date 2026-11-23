package cn.jpush.android.api;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;

public class BasicPushNotificationBuilder extends DefaultPushNotificationBuilder {
    public int notificationDefaults = -2;
    public int notificationFlags = 16;
    public int statusBarDrawable = 0;
    public String developerArg0 = "developerArg0";
    protected Context builderContext;

    public BasicPushNotificationBuilder(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("NULL context");
        }
        this.builderContext = context;
        this.context = context;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            if (appInfo != null) {
                this.statusBarDrawable = appInfo.icon;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public String getDeveloperArg0() {
        return this.developerArg0;
    }

    @Override
    Notification getNotification(Notification.Builder builder) {
        if (this.notificationDefaults != -2) {
            builder.setDefaults(this.notificationDefaults);
        }
        if (this.statusBarDrawable != 0) {
            builder.setSmallIcon(this.statusBarDrawable);
        }
        Notification notification = builder.build();
        notification.flags = 1 | this.notificationFlags;
        return notification;
    }

    @Override
    void resetNotificationParams(Notification notification) {
        notification.defaults = this.notificationDefaults;
        notification.flags = this.notificationFlags;
        notification.icon = this.statusBarDrawable;
    }

    @Override
    public String toString() {
        return "basic_____" + serializeFields();
    }

    String serializeFields() {
        return this.notificationDefaults + "_____" + this.notificationFlags + "_____" + this.statusBarDrawable + "_____" + this.developerArg0;
    }

    void parseFields(String[] parts) {
        if (parts.length >= 4) {
            this.notificationDefaults = Integer.parseInt(parts[1]);
            this.notificationFlags = Integer.parseInt(parts[2]);
            this.statusBarDrawable = Integer.parseInt(parts[3]);
        }
        if (parts.length >= 5) {
            this.developerArg0 = parts[4];
        }
    }

    public static PushNotificationBuilder parseFromPreference(Context context, String serialized) {
        String[] parts = serialized != null ? serialized.split("_____") : new String[0];
        String type = parts.length > 0 ? parts[0] : "basic";
        BasicPushNotificationBuilder builder = "custom".equals(type)
            ? new CustomPushNotificationBuilder(context)
            : new BasicPushNotificationBuilder(context);
        builder.parseFields(parts);
        return builder;
    }
}
