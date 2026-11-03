package cn.jpush.android.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.CallBackParams;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.m.RecoverType2e4ceff0;
import cn.jpush.android.service.JPushMessageReceiver;
import java.util.Set;

public abstract class JPushAction {
   public abstract Object handleAction(Context var1, String var2, Object var3);

   public boolean isSupportedCMD(String var1, int var2) {
      return var2 == 3 || var2 == 29 || var2 == 28 || var2 == 27 || var2 == 10 || var2 == 26 || var2 == 25 || var2 == 34 || var2 == 36 || var2 == 37;
   }

   public String getSdkVersion(String var1) {
      return RecoverType2e4ceff0.recoverField_b;
   }

   public abstract Object beforLogin(Context var1, String var2, int var3, String var4);

   public abstract void handleNotificationIntent(Context var1, Intent var2);

   public abstract void onJPushMessageReceive(Context var1, JPushMessageReceiver var2, Intent var3);

   public abstract void onJPushMessageReceive(Context var1, String var2, String var3, Bundle var4);

   public abstract void onMessage(Context var1, CustomMessage var2);

   public abstract void onNotifyMessageOpened(Context var1, NotificationMessage var2);

   public abstract void onNotifyMessageArrived(Context var1, NotificationMessage var2);

   public abstract void onMultiAction(Context var1, Intent var2);

   public abstract void onTagAliasResponse(Context var1, long var2, int var4, Intent var5);

   public abstract ProxyActivityAction getPopWinActivity(Context var1);

   public abstract ProxyActivityAction getPushActivity(Context var1);

   public abstract void setAliasAndTags(Context var1, String var2, Set var3, CallBackParams var4);

   public abstract void onInAppMessageShow(Context var1, NotificationMessage var2);

   public abstract void onInAppMessageClick(Context var1, NotificationMessage var2);

   public abstract void onActivityLifeCallback(Activity var1, String var2);

   public Object getDataByCmd(Context var1, int var2) {
      return null;
   }
}
