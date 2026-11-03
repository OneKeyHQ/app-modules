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

public abstract class JPushMessageReceiver extends BroadcastReceiver {
   @Override
   public final void onReceive(Context var1, Intent var2) {
      ActionHelper.getInstance().onJPushMessageReceive(var1, this, var2);
   }

   public Notification getNotification(Context var1, NotificationMessage var2) {
      return null;
   }

   public void onMessage(Context var1, CustomMessage var2) {
      ActionHelper.getInstance().onMessage(var1, var2);
   }

   public void onNotifyMessageOpened(Context var1, NotificationMessage var2) {
      ActionHelper.getInstance().onNotifyMessageOpened(var1, var2);
   }

   public void onNotifyMessageArrived(Context var1, NotificationMessage var2) {
      ActionHelper.getInstance().onNotifyMessageArrived(var1, var2);
   }

   public void onNotifyMessageUnShow(Context var1, NotificationMessage var2) {
   }

   public void onNotifyMessageDismiss(Context var1, NotificationMessage var2) {
   }

   public void onRegister(Context var1, String var2) {
   }

   public void onConnected(Context var1, boolean var2) {
   }

   public void onCommandResult(Context var1, CmdMessage var2) {
   }

   public void onNotifyButtonClick(Context var1, NotificationCustomButton var2) {
   }

   public void onMultiActionClicked(Context var1, Intent var2) {
      ActionHelper.getInstance().onMultiAction(var1, var2);
   }

   public void onPropertyOperatorResult(Context var1, JPushMessage var2) {
   }

   public void onTagOperatorResult(Context var1, JPushMessage var2) {
   }

   public void onCheckTagOperatorResult(Context var1, JPushMessage var2) {
   }

   public void onAliasOperatorResult(Context var1, JPushMessage var2) {
   }

   public void onMobileNumberOperatorResult(Context var1, JPushMessage var2) {
   }

   public void onNotificationSettingsCheck(Context var1, boolean var2, int var3) {
   }

   public boolean isNeedShowNotification(Context var1, NotificationMessage var2, String var3) {
      return true;
   }

   public void onInAppMessageShow(Context var1, NotificationMessage var2) {
      ActionHelper.getInstance().onInAppMessageShow(var1, var2);
   }

   public void onInAppMessageClick(Context var1, NotificationMessage var2) {
      ActionHelper.getInstance().onInAppMessageClick(var1, var2);
   }

   public void onGeofenceRegion(Context var1, String var2, double var3, double var5) {
   }

   public void onGeofenceReceived(Context var1, String var2) {
   }
}
