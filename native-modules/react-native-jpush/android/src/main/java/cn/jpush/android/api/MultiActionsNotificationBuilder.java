package cn.jpush.android.api;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import cn.jpush.android.service.JNotifyActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MultiActionsNotificationBuilder extends DefaultPushNotificationBuilder {
    protected Context mContext;
    private JSONArray actionJSONArray = new JSONArray();

    public MultiActionsNotificationBuilder(Context context) {
        this.mContext = context;
        this.context = context;
    }

    public void addJPushAction(int iconResId, String actionText, String actionExtra) {
        JSONObject action = new JSONObject();
        try {
            action.put("notification_action_res_id", iconResId);
            action.put("notification_action_text", actionText);
            action.put("notification_action_extra_string", actionExtra);
            this.actionJSONArray.put(action);
        } catch (JSONException ignored) {
        }
    }

    @Override
    Notification getNotification(Notification.Builder builder) {
        for (int i = 0; i < this.actionJSONArray.length(); i++) {
            try {
                JSONObject action = this.actionJSONArray.getJSONObject(i);
                Intent intent = new Intent("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY");
                intent.putExtra(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA, action.optString("notification_action_extra_string"));
                intent.setClass(this.mContext, JNotifyActivity.class);
                intent.putExtra("isNotification", true);
                intent.putExtra("multi_notification_msg", this.notificationMessage);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                    this.mContext,
                    i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                builder.addAction(
                    action.optInt("notification_action_res_id"),
                    action.optString("notification_action_text"),
                    pendingIntent
                ).setAutoCancel(true);
            } catch (Throwable ignored) {
            }
        }
        return builder.build();
    }

    public static PushNotificationBuilder parseFromPreference(Context context, String rawJson) {
        MultiActionsNotificationBuilder builder = new MultiActionsNotificationBuilder(context);
        try {
            builder.actionJSONArray = new JSONArray(rawJson);
        } catch (JSONException ignored) {
        }
        return builder;
    }

    @Override
    public String toString() {
        return this.actionJSONArray.toString();
    }
}
