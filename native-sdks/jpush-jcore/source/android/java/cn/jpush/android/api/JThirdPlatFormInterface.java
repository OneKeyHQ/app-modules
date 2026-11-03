package cn.jpush.android.api;

import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.os.Bundle;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.x.RecoverType2e4d193b;
import cn.jpush.android.x.RecoverType2e4d193c;

public abstract class JThirdPlatFormInterface {
   public static final String ACTION_NOTIFICATION_SHOW = "action_notification_show";
   public static final String ACTION_NOTIFICATION_ARRIVED = "action_notification_arrived";
   public static final String ACTION_NOTIFICATION_UN_SHOW = "action_notification_unshow";
   public static final String ACTION_NOTIFICATION_CLCKED = "action_notification_clicked";
   public static final String ACTION_NOTIFICATION_CUSTOM_BUTTON_CLCKED = "action_notification_custom_button_clcked";
   public static final String ACTION_REGISTER_TOKEN = "action_register_token";
   public static final String ACTION_SEND_EXCEPTION = "action_send_exception";
   public static final String KEY_DATA = "data";
   public static final String KEY_MSG_ID = "msg_id";
   public static final String KEY_NOTI_ID = "noti_id";
   public static final String KEY_PLATFORM = "platform";
   public static final String KEY_CODE = "code";
   public static final String KEY_MSG = "msg";
   public static final String KEY_ROM_TYPE = "rom_type";
   public static final String EXCEPTION_TYPE = "exception_type";
   public static final String KEY_EXTRA = "extra";
   public static final String KEY_VENDOR_SDK_VER = "vendor_sdk_ver";
   public static final String KEY_PLUGIN_SDK_VER = "plugin_sdk_ver";
   public static final String KEY_VENDOR_PUSH_APP_KEY_MISS = "j10000";
   public static final String KEY_VENDOR_PUSH_INIT_UNKNOWN_EXCEPTION = "j10001";
   public static final String KEY_VENDOR_PUSH_STATUS_EXCEPTION = "j10002";
   public static final String KEY_VENDOR_PUSH_MANUFACTURE_UNKNOWN = "j10003";
   public static final int CMD_CALL_BACK = 10000;
   public static final String KEY_TOKEN = "token";
   public static final String ACTION_PLUGIN_PALTFORM_REQUEST_REGID = "intent.plugin.platform.REQUEST_REGID";
   public static final String ACTION_PLUGIN_PALTFORM_REFRESSH_REGID = "intent.plugin.platform.REFRESSH_REGID";
   public static final String ACTION_PLUGIN_PALTFORM_ON_MESSAGING = "intent.plugin.platform.ON_MESSAGING";

   public abstract void init(Context var1);

   public abstract String getToken(Context var1);

   public abstract boolean isSupport(Context var1);

   public abstract byte getRomType(Context var1);

   public abstract void register(Context var1, Bundle var2);

   public void stopPush(Context var1) {
   }

   public void resumePush(Context var1) {
   }

   public abstract String getRomName();

   public abstract String getAppkey(Context var1);

   public abstract String getAppId(Context var1);

   public abstract boolean isNeedClearToken(Context var1);

   public abstract void clearNotification(Context var1, int var2);

   public abstract void clearAllNotification(Context var1);

   public boolean needSendToMainProcess() {
      return false;
   }

   public static void onEvent(Thread var0, Throwable var1) {
      ActionHelper.getInstance().onEvent(var0, var1);
   }

   public static void doAction(Context var0, String var1, Bundle var2) {
      if (var2 == null) {
         var2 = new Bundle();
      }

      var2.putString("third_key_action", var1);
      ActionHelper.getInstance().doAction(var0, "third_action", var2);
   }

   public static int getNofiticationID(String var0, int var1) {
      return RecoverType2e4d193c.recoverMethod_a_afa300d7(var0, var1);
   }

   public static void sendActionByJCore(Context var0, Bundle var1, String var2) {
      JCoreHelper.runActionWithService(var0, "JPUSH", var2, var1);
   }

   public static String toHash(String var0) {
      return RecoverType9b4c5ae5.recoverMethod_d_e96ea081(var0);
   }

   public static void setNotificationChannel(Context var0, Builder var1, String var2, CharSequence var3, int var4, int var5, int var6) {
      RecoverType2e4d193b.recoverMethod_a_fb8bd7b9(var0, var1, var2, var3, var4, var5, null, var6);
   }

   public static void setNotificationChannel(Context var0, Builder var1, String var2, CharSequence var3, int var4, int var5, String var6, int var7) {
      RecoverType2e4d193b.recoverMethod_a_fb8bd7b9(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void setNotification(Context var0, Notification var1, String var2, CharSequence var3, int var4, int var5, String var6, int var7) {
      RecoverType2e4d193b.recoverMethod_a_3cdf01a2(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void reportThirdSDKMsgActionResult(String var0, String var1, byte var2, long var3, Context var5) {
      RecoverType8b455831.recoverMethod_a_2ef42797(var0, var1, var2, var3, var5);
   }

   public static NotificationMessage parseMessage(Context var0, String var1) {
      return RecoverType2e4cce2a.recoverMethod_a_39f271f4(var0, var1, "").recoverMethod_b_4c6584f8();
   }
}
