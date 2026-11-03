package cn.jpush.android.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.ab.RecoverType9b4c5365;
import cn.jpush.android.ac.RecoverType9b4c5727;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationCustomButton;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.d.RecoverType2e4cce28;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.m.RecoverType2e4ceff0;
import cn.jpush.android.q.RecoverType2e4cfef4;
import cn.jpush.android.service.JPushMessageReceiver;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RecoverType8b45582f {
   private static boolean recoverField_a = false;
   private ThreadPoolExecutor recoverField_b;
   private static Boolean recoverField_c;

   private RecoverType8b45582f() {
      this.recoverMethod_b_9b75();
   }

   private void recoverMethod_b_9b75() {
      this.recoverField_b = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque());
      this.recoverField_b.allowCoreThreadTimeOut(true);
   }

   public static RecoverType8b45582f recoverMethod_a_e76c8997() {
      return RecoverType8b45582f.Membercf500d2d.recoverField_a;
   }

   public void recoverMethod_a_79196310(Context var1, JPushMessageReceiver var2, Intent var3) {
      if (var1 != null) {
         if (this.recoverField_b == null || this.recoverField_b.isShutdown()) {
            this.recoverMethod_b_9b75();
         }

         this.recoverField_b.execute(new RecoverType8b45582f.Membercf500d2c(var1.getApplicationContext(), var2, var3));
      }
   }

   public static void recoverMethod_a_64b14134(Context var0, NotificationMessage var1, String var2) {
      if (var0 != null && var1 != null && !TextUtils.isEmpty(var2)) {
         Intent var3 = new Intent(var2);

         try {
            Logger.recoverMethod_dd_e925fa09("JMessageReceiverHelper", "Send push received broadcast to developer defined receiver");
            Bundle var4 = new Bundle();
            var4.putString("cn.jpush.android.MSG_ID", var1.msgId);
            var4.putInt("cn.jpush.android.NOTIFICATION_ID", var1.notificationId);
            var4.putString("cn.jpush.android.ALERT", var1.notificationContent);
            var4.putString("cn.jpush.android.ALERT_TYPE", var1.notificationAlertType + "");
            if (!TextUtils.isEmpty(var1.notificationTitle)) {
               var4.putString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE", var1.notificationTitle);
            }

            if (!TextUtils.isEmpty(var1.notificationExtras)) {
               var4.putString("cn.jpush.android.EXTRA", var1.notificationExtras);
            }

            if (var1.notificationStyle == 1 && !TextUtils.isEmpty(var1.notificationBigText)) {
               var4.putString("cn.jpush.android.BIG_TEXT", var1.notificationBigText);
            } else if (var1.notificationStyle == 2 && !TextUtils.isEmpty(var1.notificationInbox)) {
               var4.putString("cn.jpush.android.INBOX", var1.notificationInbox);
            } else if (var1.notificationStyle == 3 && !TextUtils.isEmpty(var1.notificationBigPicPath)) {
               var4.putString("cn.jpush.android.BIG_PIC_PATH", var1.notificationBigPicPath);
            }

            if (var1.notificationPriority != 0) {
               var4.putString("cn.jpush.android.NOTI_PRIORITY", var1.notificationPriority + "");
            }

            if (!TextUtils.isEmpty(var1.notificationCategory)) {
               var4.putString("cn.jpush.android.NOTI_CATEGORY", var1.notificationCategory);
            }

            if (!TextUtils.isEmpty(var1.notificationSmallIcon)) {
               var4.putString("cn.jpush.android.NOTIFICATION_SMALL_ICON", var1.notificationSmallIcon);
            }

            if (!TextUtils.isEmpty(var1.notificationLargeIcon)) {
               var4.putString("cn.jpush.android.NOTIFICATION_LARGE_ICON", var1.notificationLargeIcon);
            }

            if (!TextUtils.isEmpty(var1.developerArg0)) {
               var3.putExtra("cn.jpush.android.NOTIFICATION_DEVELOPER_ARG0", var1.developerArg0);
            }

            if (var1.isRichPush) {
               if (var1.richType != 0 && var1.richType != 4 && var1._webPagePath != null && var1._webPagePath.startsWith("file://")) {
                  var1._webPagePath = var1._webPagePath.replaceFirst("file://", "");
                  var3.putExtra("cn.jpush.android.HTML_PATH", var1._webPagePath);
               }

               if (null != var1.showResourceList && var1.showResourceList.size() > 0) {
                  StringBuilder var5 = new StringBuilder();
                  String var6 = RecoverType9b4c5ae7.recoverMethod_c_a4ea6d43(var0, var1.msgId);

                  for (Object recoveryElement_117 : var1.showResourceList) {
                     String var8 = (String)recoveryElement_117;
                     if (var8.startsWith("http://")) {
                        var8 = RecoverType9b4c5ae7.recoverMethod_a_e7a3948b(var8, false);
                     }

                     if (TextUtils.isEmpty(var5.toString())) {
                        var5.append(var6).append(var8);
                     } else {
                        var5.append(",").append(var6).append(var8);
                     }
                  }

                  var3.putExtra("cn.jpush.android.HTML_RES", var5.toString());
               }
            }

            var3.putExtras(var4);
            var3.addCategory(var1.appId);
            var3.setPackage(var0.getPackageName());
            var0.sendBroadcast(var3, var1.appId + ".permission.JPUSH_MESSAGE");
         } catch (Throwable var9) {
            Logger.recoverMethod_ww_e925fa09("JMessageReceiverHelper", "sendNotificationReceivedBroadcast error:" + var9.getMessage());
            RecoverType9b4c5ae5.recoverMethod_a_ee584a7a(var0, var3, var1.appId + ".permission.JPUSH_MESSAGE");
         }
      } else {
         Logger.recoverMethod_dd_e925fa09("JMessageReceiverHelper", "Send broad cast to app failed,the params are incorrect!");
      }
   }

   public static void recoverMethod_a_c4d2ecb0(Context var0, Intent var1) {
      try {
         if (var0 == null) {
            return;
         }

         String var2 = var0.getPackageName();
         if (JPushConstants.isForeGround == 0) {
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "foreground no need provider,use sendBroadcast");
            var0.sendBroadcast(var1, var2 + ".permission.JPUSH_MESSAGE");
         } else if (recoverMethod_a_f0554df7(var0)) {
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "send by provider intent=" + var1);
            String var3 = var2 + ".jiguang.InitProvider";
            String var4 = "content://" + var3 + "/";
            var0.getContentResolver().call(Uri.parse(var4), "null", "null", RecoverType9b4c5ae5.recoverMethod_a_2fcac876(var1));
         } else {
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "no support provider ,use sendBroadcast");
            var0.sendBroadcast(var1, var2 + ".permission.JPUSH_MESSAGE");
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "send Throwable: " + var5);
      }
   }

   private static boolean recoverMethod_a_f0554df7(Context var0) {
      try {
         if (RecoverType2e4ceff0.recoverField_c < 470) {
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "jcore < 470");
            return false;
         }

         if (recoverField_c != null) {
            return recoverField_c;
         }

         ComponentInfo var1 = RecoverType9b4c5ae5.recoverMethod_a_e3a6cbf6(var0, var0.getPackageName(), Class.forName("cn.jpush.android.service.InitProvider"));
         if (var1 != null) {
            recoverField_c = var0.getPackageName().equals(var1.processName);
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "isSupportProvider info=" + recoverField_c);
         } else {
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "ComponentInfo is null");
            recoverField_c = false;
         }
      } catch (Throwable var2) {
         recoverField_c = false;
         Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "throwable=" + var2);
      }

      return recoverField_c;
   }

   public static void recoverMethod_a_f1105770(Context var0, CustomMessage var1) {
      if (var0 != null && var1 != null) {
         Logger.recoverMethod_dd_e925fa09("JMessageReceiverHelper", "not found user push message,use old action to user");
         Intent var2 = new Intent("cn.jpush.android.intent.MESSAGE_RECEIVED");
         var2.putExtra("cn.jpush.android.APPKEY", var1.senderId);
         var2.putExtra("cn.jpush.android.MESSAGE", var1.message);
         var2.putExtra("cn.jpush.android.CONTENT_TYPE", var1.contentType);
         var2.putExtra("cn.jpush.android.TITLE", var1.title);
         var2.putExtra("cn.jpush.android.EXTRA", var1.extra);
         var2.putExtra("cn.jpush.android.MSG_ID", var1.messageId);
         if (JPushConstants.SDK_VERSION_CODE >= 387) {
            var2.putExtra("cn.jpush.android.TYPE_PLATFORM", var1.platform);
         }

         var2.addCategory(var1.appId);
         var2.setPackage(var0.getPackageName());
         var0.sendBroadcast(var2, String.format(Locale.ENGLISH, "%s.permission.JPUSH_MESSAGE", var1.appId));
         Logger.recoverMethod_i_e925fa09(
            "JMessageReceiverHelper", "Send broadcast to app: " + String.format(Locale.ENGLISH, "%s.permission.JPUSH_MESSAGE", var1.appId)
         );
      } else {
         Logger.recoverMethod_dd_e925fa09("JMessageReceiverHelper", "Send custom message to app failed, param is invalid!");
      }
   }

   public static void recoverMethod_b_c4d2ecb0(Context var0, Intent var1) {
      try {
         Logger.recoverMethod_i_e925fa09(
            "JMessageReceiverHelper", "Click notification action with extra: " + var1.getExtras().getString("cn.jpush.android.NOTIFIACATION_ACTION_EXTRA")
         );
         Intent var2 = new Intent("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION");
         var2.putExtras(var1.getExtras());
         var2.addCategory(var0.getPackageName());
         var2.setPackage(var0.getPackageName());
         var0.sendBroadcast(var2, var0.getPackageName() + ".permission.JPUSH_MESSAGE");
      } catch (Throwable var3) {
         Logger.recoverMethod_ww_e925fa09("JMessageReceiverHelper", "Click notification sendBroadcast :" + var3.getMessage());
      }
   }

   public static void recoverMethod_b_79196310(Context var0, JPushMessageReceiver var1, Intent var2) {
      try {
         if (var2 == null) {
            Logger.recoverMethod_w_e925fa09("JMessageReceiverHelper", "intent  was null");
            return;
         }

         if (var1 == null) {
            var1 = JPushConstants.getPushMessageReceiver(var0);
            if (var1 == null) {
               Logger.recoverMethod_w_e925fa09("JMessageReceiverHelper", "jPushMessageReceiver  was null");
               return;
            }
         }

         String var3 = var2.getAction();
         Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "do receiver action :" + var3);
         if (var3 == null) {
            Logger.recoverMethod_w_e925fa09("JMessageReceiverHelper", "Unexpected error, action is null");
            return;
         }

         if (var3.equals("tag_alias_message")) {
            int var4 = var2.getIntExtra("message_type", -1);
            JPushMessage var5 = null;
            if (1 == var4 || 2 == var4 || 3 == var4) {
               var5 = RecoverType9b4c5365.recoverMethod_a_d8c382a1().recoverMethod_a_ec80629a(var0, var2);
            }

            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "messageType:" + var4 + ",jPushMessage:" + var5);
            if (var5 == null) {
               Logger.recoverMethod_w_e925fa09("JMessageReceiverHelper", "parse tagalias message failed");
               return;
            }

            if (var4 == 1) {
               if (var5.isTagCheckOperator()) {
                  var1.onCheckTagOperatorResult(var0, var5);
               } else {
                  var1.onTagOperatorResult(var0, var5);
               }
            } else if (var4 == 2) {
               var1.onAliasOperatorResult(var0, var5);
            } else if (var4 == 3) {
               var1.onPropertyOperatorResult(var0, var5);
            } else {
               Logger.recoverMethod_w_e925fa09("JMessageReceiverHelper", "unsupport message type");
            }
         } else if (var3.equals("mobile_result")) {
            JPushMessage var11 = RecoverType8b455832.recoverMethod_a_e76c89f4().recoverMethod_a_ec80629a(var0, var2);
            var1.onMobileNumberOperatorResult(var0, var11);
         } else if (var3.equals(RecoverType9b4c5ae5.recoverMethod_e_a4ea6d43(var0, "custom_msg"))) {
            CustomMessage var12 = RecoverType2e4cce28.recoverMethod_a_a6521e61(var2);
            var1.onMessage(var0, var12);
         } else if (var3.equals(RecoverType9b4c5ae5.recoverMethod_e_a4ea6d43(var0, "cmd_msg"))) {
            recoverMethod_a_c3a16914(var1, var0, var2);
         } else if (var3.equals("cn.jpush.android.intent.NOTIFICATION_ARRIVED")) {
            NotificationMessage var13 = RecoverType2e4d193c.recoverMethod_d_55f5921d(var0, var2);
            var1.onNotifyMessageArrived(var0, var13);
         } else if (var3.equals("cn.jpush.android.intent.NOTIFICATION_UN_SHOW")) {
            try {
               NotificationMessage var14 = RecoverType2e4d193c.recoverMethod_d_55f5921d(var0, var2);
               var1.onNotifyMessageUnShow(var0, var14);
            } catch (Throwable var9) {
            }
         } else if (var3.equals("cn.jpush.android.intent.NOTIFICATION_OPENED")) {
            NotificationMessage var15 = RecoverType2e4d193c.recoverMethod_d_55f5921d(var0, var2);
            var1.onNotifyMessageOpened(var0, var15);
         } else if (var3.equals("cn.jpush.android.intent.NOTIFICATION_OPENED_CUSTOM_BUTTON")) {
            RecoverType2e4cce2a.Memberce51f067 var16 = RecoverType2e4d193c.recoverMethod_c_c392554e(var0, var2);
            if (var16 != null) {
               NotificationCustomButton var28 = new NotificationCustomButton(
                  var16.recoverField_a, var16.recoverField_b, var16.recoverField_c, var16.recoverField_d, var16.recoverField_e, var16.recoverField_f
               );
               var1.onNotifyButtonClick(var0, var28);
            } else {
               Logger.recoverMethod_ee_e925fa09("JMessageReceiverHelper", "CustomButton is null");
            }
         } else if (var3.equals("cn.jpush.android.intent.NOTIFICATION_DISMISS")) {
            NotificationMessage var17 = RecoverType2e4d193c.recoverMethod_d_55f5921d(var0, var2);
            var1.onNotifyMessageDismiss(var0, var17);
         } else if (var3.equals("cn.jpush.android.intent.IN_APP_MSG_SHOW")) {
            NotificationMessage var18 = RecoverType2e4d193c.recoverMethod_d_55f5921d(var0, var2);
            var1.onInAppMessageShow(var0, var18);
         } else if (var3.equals("cn.jpush.android.intent.IN_APP_MSG_CLICK")) {
            NotificationMessage var19 = RecoverType2e4d193c.recoverMethod_d_55f5921d(var0, var2);
            var1.onInAppMessageClick(var0, var19);
         } else if ("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY".equals(var2.getAction())) {
            Intent var20 = new Intent("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION");
            if (var2.getExtras() != null) {
               var20.putExtras(var2.getExtras());
            }

            var1.onMultiActionClicked(var0, var20);
         } else if ("on_noti_settings_check".equals(var3)) {
            boolean var21 = var2.getBooleanExtra("isOn", false);
            int var29 = var2.getIntExtra("source", 0);
            var1.onNotificationSettingsCheck(var0, var21, var29);
         } else if ("cn.jpush.android.intent.GEO_RECEIVED".equals(var3)) {
            String var22 = var2.getStringExtra("geo");
            if (TextUtils.isEmpty(var22)) {
               var22 = "";
            }

            var1.onGeofenceReceived(var0, var22);
         } else if ("cn.jpush.android.intent.GEO_REGION".equals(var3)) {
            String var23 = var2.getStringExtra("geo");
            double var30 = var2.getDoubleExtra("geoLat", 200.0);
            double var7 = var2.getDoubleExtra("geoLng", 200.0);
            var1.onGeofenceRegion(var0, var23, var7, var30);
         } else if ("cn.jpush.android.intent.NOTIFY_INAPP_MESSAGE_ARRIVED".equals(var3)) {
            RecoverType2e4cce2a var24 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var0, var2);
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "notify inapp received by receiver");
            if (null != var24) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1934cb4b(var0, var24, 1001);
            }
         } else if ("check_can_show_message".equals(var3)) {
            RecoverType2e4cce2a var25 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var0, var2);
            if (null != var25) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1934cb4b(var0, var25, 1006);
            }
         } else if ("handle_message_failed".equals(var3)) {
            RecoverType2e4cce2a var26 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var0, var2);
            if (null != var26) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1934cb4b(var0, var26, 1007);
            }
         } else if ("cn.jpush.android.intent.NOTIFY_INAPP_MESSAGE_CANCEL".equals(var3)) {
            RecoverType2e4cce2a var27 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var0, var2);
            if (null != var27) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1934cb4b(var0, var27, 1004);
            }

            Logger.recoverMethod_dd_e925fa09(
               "JMessageReceiverHelper", "notify inapp cancel cmd, cancel messageId: " + (var27 == null ? "" : var27.recoverField_d)
            );
         } else {
            Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "unsupport action type, action: " + var3);
         }
      } catch (Throwable var10) {
         Logger.recoverMethod_ww_e925fa09("JMessageReceiverHelper", "MessageReceiver run failed:" + var10.getMessage());
      }
   }

   public static void recoverMethod_a_c3a16914(JPushMessageReceiver var0, Context var1, Intent var2) {
      try {
         int var3 = var2.getIntExtra("code", -2);
         int var4 = var2.getIntExtra("cmd", -2);
         String var5 = var2.getStringExtra("message");
         Logger.recoverMethod_d_e925fa09("JMessageReceiverHelper", "[callCmdMessage] code:" + var3 + ",cmd:" + var4 + ",message:" + var5);
         Bundle var6 = var2.getExtras();
         if (var4 == -1) {
            var0.onConnected(var1, false);
         } else if (var4 == 0) {
            if (var3 == 0) {
               var0.onRegister(var1, var5);
            } else {
               CmdMessage var7 = new CmdMessage(var4, var3, var5, var6);
               var0.onCommandResult(var1, var7);
            }
         } else if (var4 == 1) {
            var0.onConnected(var1, true);
            if (!recoverField_a) {
               recoverField_a = true;
               Bundle var10 = new Bundle();
               var10.putInt("cmd", 2008);
               var10.putInt("code", 0);
               var10.putString("message", "success");
               CmdMessage var8 = new CmdMessage(2008, 0, var5, var10);
               var0.onCommandResult(var1, var8);
            }
         } else {
            switch (var4) {
               case 2001:
                  break;
               case 2003:
               case 2004:
               case 2005:
               case 2006:
               case 2007:
               case 2009:
               case 10000:
               case 100001:
               case 100002:
               case 100003:
               case 100004:
               case 100005:
                  CmdMessage var11 = new CmdMessage(var4, var3, var5, var6);
                  var0.onCommandResult(var1, var11);
                  break;
               case 2011:
                  Logger.recoverMethod_d_e925fa09("debug_110", "CMD_TCP_STATE code=" + var3);
                  RecoverTypea2146d45.recoverMethod_b_1a552341(var1, var3 == 0);
                  break;
               default:
                  Logger.recoverMethod_dd_e925fa09("JMessageReceiverHelper", "unkown cmd:" + var4);
            }
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_e_e925fa09("JMessageReceiverHelper", "callCmdMessage failed:" + var9.getMessage());
      }
   }

   private class Membercf500d2c extends RecoverType9b4c5aea {
      private Context recoverField_b;
      private JPushMessageReceiver recoverField_c;
      private Intent recoverField_d;

      public Membercf500d2c(Context var2, JPushMessageReceiver var3, Intent var4) {
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_e = "JMessageReceiverHelper#MessageReceiverRunable";
      }

      @Override
      public void recoverMethod_a_9b75() {
         RecoverType8b45582f.recoverMethod_b_79196310(this.recoverField_b, this.recoverField_c, this.recoverField_d);
         RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_c4d2ecb0(this.recoverField_b, this.recoverField_d);
      }
   }

   private static final class Membercf500d2d {
      private static final RecoverType8b45582f recoverField_a = new RecoverType8b45582f();
   }
}
