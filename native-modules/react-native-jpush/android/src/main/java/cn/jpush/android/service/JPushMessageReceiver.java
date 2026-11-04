package cn.jpush.android.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationCustomButton;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.local.ActionHelper;
import cn.jiguang.core.JCoreStorage;

public abstract class JPushMessageReceiver extends BroadcastReceiver {
    @Override
    public final void onReceive(Context context, Intent intent) {
        ActionHelper.getInstance().onJPushMessageReceive(context, this, intent);
    }

    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
        return null;
    }

    public void onMessage(Context context, CustomMessage customMessage) {
        ActionHelper.getInstance().onMessage(context, customMessage);
    }

    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        ActionHelper.getInstance().onNotifyMessageOpened(context, notificationMessage);
    }

    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        ActionHelper.getInstance().onNotifyMessageArrived(context, notificationMessage);
    }

    public void onNotifyMessageUnShow(Context context, NotificationMessage notificationMessage) {
    }

    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
    }

    public void onRegister(Context context, String registrationId) {
    }

    public void onConnected(Context context, boolean isConnected) {
    }

    public void onCommandResult(Context context, CmdMessage cmdMessage) {
    }

    public void onNotifyButtonClick(Context context, NotificationCustomButton button) {
    }

    public void onMultiActionClicked(Context context, Intent intent) {
        ActionHelper.getInstance().onMultiAction(context, intent);
    }

    public void onPropertyOperatorResult(Context context, JPushMessage jPushMessage) {
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
    }

    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
    }

    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
    }

    public boolean isNeedShowNotification(Context context, NotificationMessage notificationMessage, String processName) {
        return !JCoreStorage.isPushStopped(context) && JCoreStorage.isCollectionAuth(context);
    }

    public void onInAppMessageShow(Context context, NotificationMessage notificationMessage) {
        ActionHelper.getInstance().onInAppMessageShow(context, notificationMessage);
    }

    public void onInAppMessageClick(Context context, NotificationMessage notificationMessage) {
        ActionHelper.getInstance().onInAppMessageClick(context, notificationMessage);
    }

    public void onGeofenceRegion(Context context, String geofenceId, double longitude, double latitude) {
    }

    public void onGeofenceReceived(Context context, String geofenceJson) {
    }
}
