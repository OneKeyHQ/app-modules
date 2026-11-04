package cn.jpush.android.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.widget.RemoteViews;

public class CustomPushNotificationBuilder extends BasicPushNotificationBuilder {
    public int layoutTimeId;
    public int layout;
    public int layoutIconId;
    public int layoutTitleId;
    public int layoutContentId;
    public int layoutIconDrawable = 0;

    CustomPushNotificationBuilder(Context context) {
        super(context);
    }

    public CustomPushNotificationBuilder(Context context, int layout, int layoutIconId, int layoutTitleId, int layoutContentId, int layoutTimeId) {
        super(context);
        this.layout = layout;
        this.layoutIconId = layoutIconId;
        this.layoutTitleId = layoutTitleId;
        this.layoutContentId = layoutContentId;
        this.layoutTimeId = layoutTimeId;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            if (appInfo != null) {
                this.layoutIconDrawable = appInfo.icon;
            }
        } catch (Throwable ignored) {
        }
    }

    public CustomPushNotificationBuilder(Context context, int layout, int layoutIconId, int layoutTitleId, int layoutContentId) {
        this(context, layout, layoutIconId, layoutTitleId, layoutContentId, 0);
    }

    @Override
    RemoteViews buildContentView(String content, String title) {
        RemoteViews remoteViews = new RemoteViews(this.builderContext.getPackageName(), this.layout);
        remoteViews.setTextViewText(this.layoutTitleId, title);
        remoteViews.setImageViewResource(this.layoutIconId, this.layoutIconDrawable);
        remoteViews.setTextViewText(this.layoutContentId, content);
        if (this.layoutTimeId != 0) {
            remoteViews.setLong(this.layoutTimeId, "setTime", System.currentTimeMillis());
        }
        return remoteViews;
    }

    @Override
    public String toString() {
        return "custom_____" + serializeFields();
    }

    @Override
    String serializeFields() {
        return super.serializeFields()
            + "_____" + this.layout
            + "_____" + this.layoutIconId
            + "_____" + this.layoutTitleId
            + "_____" + this.layoutContentId
            + "_____" + this.layoutIconDrawable
            + "_____" + this.layoutTimeId;
    }

    @Override
    void parseFields(String[] parts) {
        super.parseFields(parts);
        if (parts.length >= 10) {
            this.layout = Integer.parseInt(parts[5]);
            this.layoutIconId = Integer.parseInt(parts[6]);
            this.layoutTitleId = Integer.parseInt(parts[7]);
            this.layoutContentId = Integer.parseInt(parts[8]);
            this.layoutIconDrawable = Integer.parseInt(parts[9]);
        }
        if (parts.length >= 11) {
            this.layoutTimeId = Integer.parseInt(parts[10]);
        }
    }
}
