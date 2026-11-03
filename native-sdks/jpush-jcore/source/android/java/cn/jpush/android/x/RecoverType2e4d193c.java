package cn.jpush.android.x;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import cn.jpush.android.aa.RecoverType9b4c4fa1;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae6;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.ad.RecoverType9b4c5af0;
import cn.jpush.android.ad.RecoverType9b4c5af1;
import cn.jpush.android.api.NotificationCustomButton;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.api.PushNotificationBuilder;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b45582f;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.k.RecoverType2e4ce86e;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.service.JNotifyActivity;
import cn.jpush.android.service.JPushMessageReceiver;
import cn.jpush.android.ui.PopWinActivity;
import cn.jpush.android.ui.PushActivity;
import cn.jpush.android.w.RecoverType2e4d157b;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.UUID;
import java.util.zip.Adler32;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType2e4d193c {
   private static Integer recoverField_a;
   private static Boolean recoverField_b;

   public static void recoverMethod_a_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      Logger.recoverMethod_d_e925fa09("NotificationHelper", "[processMessage] pushEntity:" + var1);
      if (var1.recoverField_ae != 0) {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "Unexpected: unknown show  mode - " + var1.recoverField_ae);
      } else {
         JCoreHelper.majorExecutor(var0, "JPUSH", new RecoverType2e4d193c.Membercf6bbef9(var0, var1));
      }
   }

   public static int recoverMethod_a_73eedead(RecoverType2e4cce2a var0) {
      return recoverMethod_a_afa300d7(TextUtils.isEmpty(var0.recoverField_h) ? var0.recoverField_d : var0.recoverField_h, 0);
   }

   public static int recoverMethod_a_afa300d7(String var0, int var1) {
      if (TextUtils.isEmpty(var0)) {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "action:getNotificationID - empty messageId");
         return 0;
      }

      int var2 = 0;

      try {
         return Integer.valueOf(var0);
      } catch (Exception var4) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "Ths msgId is not a integer");
         Adler32 var3 = new Adler32();
         var3.update(var0.getBytes());
         var2 = (int)var3.getValue();
         if (var2 < 0) {
            var2 = Math.abs(var2);
         }

         return var2;
      }
   }

   public static Uri recoverMethod_a_561e3034(Context var0, String var1) {
      try {
         if (!TextUtils.isEmpty(var1)) {
            int var2 = var0.getResources().getIdentifier(var1, "raw", var0.getPackageName());
            if (var2 != 0) {
               Uri var3 = Uri.parse("android.resource://" + var0.getPackageName() + "/raw/" + var1);
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "found sound uri=" + var3);
               return var3;
            }

            Logger.recoverMethod_d_e925fa09("NotificationHelper", "not found sound=" + var1);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "find sound e:" + var4);
      }

      return null;
   }

   public static void recoverMethod_a_f2a64de9(Context var0, Builder var1, RecoverType2e4d193b.Membercf6bbb38 var2) {
      try {
         if (var2 != null) {
            if (VERSION.SDK_INT >= 26 && var2.recoverField_i) {
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "setChannelId =" + var2.recoverField_a);
               recoverMethod_d_5ffc0101(var0, var2.recoverField_a);
               var1.setChannelId(var2.recoverField_a);
            }

            if (VERSION.SDK_INT >= 16) {
               var1.setPriority(var2.recoverField_c);
               var1.setDefaults(var2.recoverField_f);
               var1.setSound(var2.recoverField_h);
            }
         }
      } catch (Throwable var4) {
      }
   }

   private static boolean recoverMethod_d_5ffc0101(Context var0, String var1) {
      try {
         if (VERSION.SDK_INT >= 26 && !TextUtils.isEmpty(var1)) {
            NotificationManager var2 = (NotificationManager)var0.getSystemService("notification");
            NotificationChannel var3 = var2 != null ? var2.getNotificationChannel(var1) : null;
            if (var3 == null || var3.getImportance() == 0) {
               Logger.recoverMethod_ww_e925fa09("NotificationHelper", var1 + " channel maybe closed,please check it");
               return false;
            }

            Logger.recoverMethod_dd_e925fa09("NotificationHelper", var1 + " channel is opened,will use it");
         }
      } catch (Throwable var4) {
      }

      return true;
   }

   public static void recoverMethod_a_82e04d22(Notification var0, RecoverType2e4d193b.Membercf6bbb38 var1) {
      try {
         if (var1 != null && VERSION.SDK_INT < 16) {
            var0.defaults = var1.recoverField_f;
            var0.sound = var1.recoverField_h;
         }
      } catch (Throwable var3) {
      }
   }

   public static void recoverMethod_b_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      Logger.recoverMethod_d_e925fa09("NotificationHelper", "showNotification start :" + var1);
      JCoreHelper.triggerSceneCheck(var0);
      boolean var2 = false;

      try {
         if (TextUtils.isEmpty(var1.recoverField_y)) {
            recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_ARRIVED", var1, null);
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "notificationContent is empty");
            return;
         }

         String var3 = JPushConstants.getMessageReceiverClass(var0);
         NotificationMessage var4 = var1.recoverMethod_b_4c6584f8();
         if (recoverMethod_a_a8e1cb7d(var0, var3, var1, var4)) {
            return;
         }

         String var5 = "";
         String var6 = !TextUtils.isEmpty(var1.recoverField_K) ? var1.recoverField_L : var1.recoverField_N;
         RecoverType2e4d193d var7 = recoverMethod_a_43728163(
            JPushConstants.mApplicationContext, var1.recoverField_K, var1.recoverField_R, var6, var1.recoverField_d, var1.recoverField_O
         );
         if (var7.recoverMethod_e_9b69() != 0L) {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, var7.recoverMethod_e_9b69(), var0);
            Logger.recoverMethod_ww_e925fa09("NotificationHelper", "build small icon failed,will not notify this notification");
            return;
         }

         RecoverType2e4d193d var8 = new RecoverType2e4d193d();
         if (!TextUtils.isEmpty(var1.recoverField_M)) {
            var8 = recoverMethod_a_4e83d682(JPushConstants.mApplicationContext, var1.recoverField_M, 307200);
            if (var8.recoverMethod_e_9b69() != 0L) {
               var8 = new RecoverType2e4d193d();
            }
         }

         Notification var9 = null;
         JPushMessageReceiver var10 = null;

         try {
            if (!TextUtils.isEmpty(var3)) {
               var10 = (JPushMessageReceiver)Class.forName(var3).newInstance();
               var9 = var10.getNotification(var0, var4);
            }
         } catch (Throwable var15) {
            Logger.recoverMethod_ww_e925fa09("NotificationHelper", "get notification from JPushMessageReceiver failed:" + var15.getMessage());
         }

         if (var9 == null) {
            PushNotificationBuilder var11 = recoverMethod_a_7018dd49(var0, var4);
            if (var11 != null) {
               var9 = var11.buildNotification(var0, var4);
               var5 = var11.getDeveloperArg0();
            }

            if (var9 == null) {
               var2 = true;
               var9 = recoverMethod_a_ec3dde70(var0, var1, var7, var8);
            }
         }

         int var18 = recoverMethod_a_73eedead(var1);
         var1.recoverField_e = var18;
         var1.recoverField_at = var5;
         Intent var12 = null;
         if (var1.recoverField_Y) {
            if (2 == var1.recoverField_ad) {
               var12 = recoverMethod_b_555de491(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1);
            } else {
               var12 = recoverMethod_a_555de491(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1);
            }
         } else {
            var12 = recoverMethod_c_555de491(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1);
         }

         var9.contentIntent = recoverMethod_e_c5c3ea1c(var0, var12);
         Intent var13 = recoverMethod_c_555de491(var0, "cn.jpush.android.intent.NOTIFICATION_DISMISS", var1);
         var9.deleteIntent = recoverMethod_e_c5c3ea1c(var0, var13);
         if (var1.recoverField_m == 1) {
            var1.recoverField_t = 1;
         }

         var9.flags = 1 | recoverMethod_a_134621(var1.recoverField_t);
         if (recoverMethod_c_f0554df7(var0)) {
            var9.defaults = 0;
         }

         NotificationManager var14 = (NotificationManager)var0.getSystemService("notification");
         if (var14 != null) {
            Logger.recoverMethod_dd_e925fa09(
               "NotificationHelper", "notify: id=" + var18 + " messageId=" + var1.recoverField_d + " overmessageid=" + var1.recoverField_h
            );
            recoverMethod_a_11e1441a(var0, var1.recoverField_aj, var9);
            var14.notify(var18, var9);
            recoverMethod_a_1a54e374(var0, var18);
         } else {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "NotificationManager is null");
         }

         if (var1.recoverField_m == 1) {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "local notification not offer to notification queue");
         } else if (var1.recoverField_ai == 0) {
            recoverMethod_b_1a54e370(var0, var18);
            if (recoverMethod_d_f0554de6(var0) == 0) {
               Logger.recoverMethod_kd_e925fa09("NotificationHelper", "notification trigger is off, will not show notification");
               RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1036L, var0);
            }

            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1018L, var0);
         } else {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "Third notification:" + var1.recoverField_ai);
         }

         if (var1.recoverField_ai == 0) {
            recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_ARRIVED", var1, null);
         }
      } catch (Throwable var16) {
         if (!var2) {
            recoverMethod_a_4abe089d(var0, 1000, 0, var16.getMessage());
         }

         RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1080L, var0);
         Logger.recoverMethod_e_e925fa09("NotificationHelper", "showNotification failed:" + var16.getMessage());
      }
   }

   private static ActivityOptions recoverMethod_a_cbf93c65() {
      ActivityOptions var0 = null;
      if (VERSION.SDK_INT >= 34) {
         try {
            var0 = ActivityOptions.makeBasic();
            Class<ActivityOptions> var1 = ActivityOptions.class;
            int var2 = var1.getField("MODE_BACKGROUND_ACTIVITY_START_ALLOWED").getInt(null);
            int var3 = var1.getField("MODE_BACKGROUND_ACTIVITY_START_DENIED").getInt(null);
            Method var4 = var1.getMethod("setPendingIntentCreatorBackgroundActivityStartMode", int.class);
            var4.invoke(var0, var2);
         } catch (Throwable var5) {
         }
      }

      return var0;
   }

   public static boolean recoverMethod_a_1a54e374(Context var0, int var1) {
      boolean var2 = false;

      try {
         if (VERSION.SDK_INT >= 23) {
            NotificationManager var3 = (NotificationManager)var0.getSystemService("notification");
            StatusBarNotification[] var4 = var3.getActiveNotifications();
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "active size:" + var4.length);
            if (var4 != null) {
               for (StatusBarNotification var8 : var4) {
                  if (var8.getId() == var1) {
                     var2 = true;
                     break;
                  }
               }
            }
         }

         if (var2) {
            Logger.recoverMethod_dd_e925fa09("NotificationHelper", "show  notification success:" + var1);
         } else {
            Logger.recoverMethod_dd_e925fa09("NotificationHelper", "show  notification maybe failed:" + var1);
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_ww_e925fa09("NotificationHelper", "[checkNotficationShow] failed:" + var9.getMessage());
      }

      return var2;
   }

   public static void recoverMethod_a_11e1441a(Context var0, Integer var1, Notification var2) {
      if (var1 != null) {
         Key var3 = Key.BadgeCurNum();
         int var4 = (Integer)RecoverTypea0793a19.get(var0, var3);
         if (var4 < 0) {
            var4 = 0;
         }

         if (var1 != 0) {
            if (var4 + var1 < 0) {
               var1 = -var4;
            }

            var4 += var1;
            RecoverTypea0793a19.set(var0, var3.set(var4));
         }

         Logger.recoverMethod_d_e925fa09("NotificationHelper", "curNum：" + var4 + ",addNum:" + var1);
         recoverMethod_a_b04e2dcd(var0, var2, var1, var4);
      }
   }

   public static boolean recoverMethod_a_b04e2dcd(Context var0, Notification var1, int var2, int var3) {
      if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
         return recoverMethod_a_201f25bc(var1, var2);
      }

      if (recoverMethod_b_9b79()) {
         if (var3 == 0 || var2 != 0) {
            return recoverMethod_j_1a54e374(var0, var3);
         }
      } else if (RecoverType9b4c5ae5.recoverMethod_b_9b79()) {
         if (var3 == 0 || var2 != 0) {
            return recoverMethod_g_1a54e374(var0, var3);
         }
      } else if (Build.MANUFACTURER.equalsIgnoreCase("HONOR")) {
         if (var3 == 0 || var2 != 0) {
            return recoverMethod_f_1a54e374(var0, var3);
         }
      } else if (Build.MANUFACTURER.equalsIgnoreCase("NIO")) {
         if (var3 == 0 || var2 != 0) {
            return recoverMethod_e_1a54e374(var0, var3);
         }
      } else {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "not support");
      }

      return false;
   }

   private static boolean recoverMethod_e_1a54e374(Context var0, int var1) {
      try {
         Class var2 = Class.forName("com.skyui.push.sdk.SkyPushAgent");
         Method var3 = var2.getMethod("getPushService");
         Object var4 = var3.invoke(null);
         Method var5 = var4.getClass().getMethod("setAppBadgeCount", Context.class, int.class, String.class);
         var5.invoke(var4, var0, var1, null);
         return true;
      } catch (Throwable var6) {
         return false;
      }
   }

   private static boolean recoverMethod_f_1a54e374(Context var0, int var1) {
      try {
         String var2 = "content://com.huawei.android.launcher.settings/badge/";
         String var3 = "content://com.hihonor.android.launcher.settings/badge/";
         ContentResolver var4 = var0.getContentResolver();
         Uri var5 = Uri.parse(var3);
         String var6 = var4.getType(var5);
         if (TextUtils.isEmpty(var6)) {
            var5 = Uri.parse(var2);
            var6 = var4.getType(var5);
            if (TextUtils.isEmpty(var6)) {
               Object var9 = null;
               Logger.recoverMethod_e_e925fa09("NotificationHelper", " setHonorBadge fail, uri is null");
               return false;
            }
         }

         Bundle var7 = new Bundle();
         var7.putString("package", var0.getPackageName());
         var7.putString("class", recoverMethod_h_e501eb0d(var0));
         var7.putInt("badgenumber", var1);
         var4.call(var5, "change_badge", null, var7);
         return true;
      } catch (Throwable var8) {
         Logger.recoverMethod_e_e925fa09("NotificationHelper", " setHonorBadge wrong error:" + var8);
         return false;
      }
   }

   private static boolean recoverMethod_g_1a54e374(Context var0, int var1) {
      try {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "setViVoBadge curNum:" + var1);
         boolean var2 = false;

         try {
            var2 = recoverMethod_h_1a54e374(var0, var1);
         } catch (Throwable var6) {
         }

         boolean var3 = false;

         try {
            var3 = recoverMethod_i_1a54e374(var0, var1);
         } catch (Throwable var5) {
         }

         return var3 || var2;
      } catch (Throwable var7) {
         Logger.recoverMethod_e_e925fa09("NotificationHelper", " setVivoBadge wrong error:" + var7);
         return false;
      }
   }

   @SuppressLint("WrongConstant")
   private static boolean recoverMethod_h_1a54e374(Context var0, int var1) {
      try {
         Intent var2 = new Intent();
         var2.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
         var2.putExtra("packageName", var0.getPackageName());
         var2.putExtra("className", recoverMethod_h_e501eb0d(var0));
         var2.putExtra("notificationNum", var1);
         var2.addFlags(16777216);
         var0.sendBroadcast(var2);
         return true;
      } catch (Throwable var3) {
         Logger.recoverMethod_e_e925fa09("NotificationHelper", " setVivoBadge wrong error:" + var3);
         return false;
      }
   }

   private static boolean recoverMethod_i_1a54e374(Context var0, int var1) {
      try {
         Uri var2 = Uri.parse("content://com.vivo.abe.provider.launcher.notification.num");
         Bundle var3 = new Bundle();
         String var4 = var0.getPackageName();
         String var5 = recoverMethod_h_e501eb0d(var0);
         var3.putString("package", var4);
         var3.putString("class", var5);
         var3.putInt("badgenumber", var1);
         ContentProviderClient var6 = null;

         try {
            var6 = var0.getContentResolver().acquireUnstableContentProviderClient(var2);
            if (var6 != null) {
               int var7 = var6.call("change_badge", null, var3).getInt("result");
               if (0 == var7) {
                  return true;
               }

               Logger.recoverMethod_w_e925fa09("NotificationHelper", "[setViVoBadgeVOS] result: " + var7);
            } else {
               Logger.recoverMethod_w_e925fa09(
                  "NotificationHelper", "[setViVoBadgeVOS] Failed to get ContentProviderClient, VIVO badge interface not supported"
               );
            }
         } catch (Exception var13) {
            Logger.recoverMethod_e_1765c98c("NotificationHelper", "[setViVoBadgeVOS] Exception calling VIVO badge interface: " + var13.getMessage(), var13);
         } finally {
            if (var6 != null) {
               if (VERSION.SDK_INT >= 24) {
                  var6.close();
               } else {
                  var6.release();
               }
            }
         }
      } catch (Throwable var15) {
         Logger.recoverMethod_e_1765c98c("NotificationHelper", "[setViVoBadgeVOS] Failed to set VIVO badge: " + var15.getMessage(), var15);
      }

      Logger.recoverMethod_w_e925fa09("NotificationHelper", "[setViVoBadgeVOS] VIVO badge setting failed, returning false");
      return false;
   }

   private static boolean recoverMethod_b_9b79() {
      try {
         Class[] var0 = new Class[]{String.class};
         Object[] var1 = new Object[]{"ro.build.version.emui"};
         Class var2 = Class.forName("android.os.SystemProperties");
         Method var3 = var2.getDeclaredMethod("get", var0);
         String var4 = (String)var3.invoke(var2, var1);
         if (!TextUtils.isEmpty(var4)) {
            Logger.recoverMethod_i_e925fa09("NotificationHelper", "get EMUI version is:" + var4);
            var4 = var4.toLowerCase().replace("emotionui_", "");
            return RecoverType9b4c5ae5.recoverMethod_a_e925f9fc(var4, "8.0") >= 0;
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_e_e925fa09("NotificationHelper", " getEmuiVersion wrong error:" + var5);
      }

      return false;
   }

   private static boolean recoverMethod_j_1a54e374(Context var0, int var1) {
      try {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "【setEMUIBadge】number：" + var1);
         if (VERSION.SDK_INT >= 11) {
            Bundle var2 = new Bundle();
            var2.putString("package", var0.getPackageName());
            var2.putString("class", recoverMethod_h_e501eb0d(var0));
            var2.putInt("badgenumber", var1);
            var0.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, var2);
            return true;
         }
      } catch (Exception var3) {
         Logger.recoverMethod_e_e925fa09("NotificationHelper", "e: " + var3);
      }

      return false;
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      try {
         PackageManager var1 = var0.getPackageManager();
         Intent var2 = new Intent("android.intent.action.MAIN");
         var2.setPackage(var0.getPackageName());
         var2.addCategory("android.intent.category.LAUNCHER");
         ResolveInfo var3 = var1.resolveActivity(var2, 65536);
         if (var3 == null) {
            var3 = var1.resolveActivity(var2, 0);
         }

         return var3 != null ? var3.activityInfo.name : null;
      } catch (Throwable var4) {
         return null;
      }
   }

   private static String recoverMethod_h_e501eb0d(Context var0) {
      try {
         Intent var1 = var0.getPackageManager().getLaunchIntentForPackage(var0.getPackageName());
         if (var1 != null && var1.getComponent() != null) {
            return var1.getComponent().getClassName();
         }
      } catch (Throwable var2) {
      }

      return recoverMethod_g_e501eb0d(var0);
   }

   private static boolean recoverMethod_a_201f25bc(Notification var0, int var1) {
      try {
         if (recoverField_a == null) {
            try {
               String var2 = RecoverType9b4c5ae5.recoverMethod_e_e96ea081("ro.miui.ui.version.name");
               recoverField_a = Integer.valueOf(var2);
            } catch (Throwable var5) {
               recoverField_a = 7;
            }
         }

         if (var0 == null) {
            return false;
         }

         if (recoverField_a < 12) {
            Field var7 = var0.getClass().getDeclaredField("extraNotification");
            Object var3 = var7.get(var0);
            Method var4 = var3.getClass().getDeclaredMethod("setMessageCount", int.class);
            var4.invoke(var3, var1);
            return true;
         }

         var0.number = var1;
      } catch (Exception var6) {
      }

      return false;
   }

   @TargetApi(11)
   public static RecoverType2e4d193d recoverMethod_a_4e83d682(Context var0, String var1, int var2) {
      RecoverType2e4d193d var3 = new RecoverType2e4d193d();
      Object var4 = recoverMethod_b_471f464c(var0, var1, var2);
      if (var4 == null) {
         var3.recoverMethod_a_1349ef(973L);
         return var3;
      }

      if (VERSION.SDK_INT >= 23 && var4 instanceof Icon) {
         var3.recoverMethod_a_13462e(102);
         var3.recoverMethod_a_5d527811(var4);
      } else {
         if (!(var4 instanceof Bitmap)) {
            var3.recoverMethod_a_1349ef(973L);
            return var3;
         }

         var3.recoverMethod_a_13462e(100);
         var3.recoverMethod_a_fef8dc59((Bitmap)var4);
      }

      return var3;
   }

   public static Object recoverMethod_b_b2278fd5(Context var0, String var1) {
      return recoverMethod_b_471f464c(var0, var1, 307200);
   }

   public static Object recoverMethod_b_471f464c(Context var0, String var1, int var2) {
      Parcelable var3 = null;
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "large icon is empty");
         return null;
      }

      String var4 = "";
      int var5 = 0;

      try {
         if (!TextUtils.isEmpty(var1)) {
            if (RecoverType9b4c5ae5.recoverMethod_c_aad3b203(var1)) {
               var4 = recoverMethod_d_39e223ba(JPushConstants.mApplicationContext, var1, var2);
            } else {
               var5 = JPushConstants.mApplicationContext.getResources().getIdentifier(var1, "drawable", JPushConstants.mApplicationContext.getPackageName());
            }
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "load large icon failed:" + var9.getMessage());
      }

      try {
         Bitmap var6 = null;
         if (var5 != 0) {
            var6 = BitmapFactory.decodeResource(var0.getResources(), var5);
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "set large icon with res bitmap:" + var5);
         } else if (!TextUtils.isEmpty(var4) && new File(var4).exists()) {
            var6 = BitmapFactory.decodeFile(var4);
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "set large icon with path bitmap:" + var4);
         }

         if (var6 != null) {
            var3 = var6;
         } else if (VERSION.SDK_INT >= 23) {
            Icon var7 = null;
            if (var5 != 0) {
               var7 = Icon.createWithResource(var0, var5);
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "set large icon with res:" + var5);
            } else if (!TextUtils.isEmpty(var4)) {
               var7 = recoverMethod_a_81f26de0(var4);
               if (var7 != null) {
                  Logger.recoverMethod_d_e925fa09("NotificationHelper", "set large icon with path:" + var4);
               }
            }

            if (var7 != null) {
               var3 = var7;
            } else {
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "not set large icon");
            }
         } else {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "not set large icon by bitmap");
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "setLargeIcon failed:" + var8.getMessage());
      }

      return var3;
   }

   public static String recoverMethod_c_39e223ba(Context var0, String var1, int var2) {
      if (RecoverType9b4c5ae5.recoverMethod_a_afa340b9(var1, false)) {
         Logger.recoverMethod_i_e925fa09("NotificationHelper", "The url is a picture resources.");
         String var3 = RecoverType9b4c5ae5.recoverMethod_d_e96ea081(var1);
         if (TextUtils.isEmpty(var3)) {
            var3 = UUID.randomUUID().toString();
         }

         var3 = var3 + RecoverType9b4c5ae5.recoverMethod_b_e96ea081(var1);
         String var4 = RecoverType9b4c5ae7.recoverMethod_c_a4ea6d43(var0, "noti_res") + var3;
         Logger.recoverMethod_i_e925fa09("NotificationHelper", "picture notification resource path: " + var4);
         if (new File(var4).exists()) {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "need not download again with same url:" + var1);
            return var4;
         } else {
            byte[] var5 = RecoverType2e4d157b.recoverMethod_a_87852596(var0, var1, "notify_img", 2, var2);
            if (null != var5) {
               return RecoverType9b4c5ae7.recoverMethod_a_44c5a30a(var4, var5) ? var4 : "";
            } else {
               return "";
            }
         }
      } else {
         Logger.recoverMethod_i_e925fa09("NotificationHelper", "The url is not a picture resources.");
         return "";
      }
   }

   @TargetApi(11)
   public static RecoverType2e4d193d recoverMethod_a_43728163(Context var0, String var1, String var2, String var3, String var4, String var5) {
      RecoverType2e4d193d var6 = new RecoverType2e4d193d();
      String var7 = "";
      int var8 = 0;

      try {
         if (!TextUtils.isEmpty(var3)) {
            if (RecoverType9b4c5ae5.recoverMethod_c_aad3b203(var3)) {
               var7 = recoverMethod_d_39e223ba(JPushConstants.mApplicationContext, var3, 307200);
            } else {
               var8 = JPushConstants.mApplicationContext.getResources().getIdentifier(var3, "drawable", JPushConstants.mApplicationContext.getPackageName());
            }
         } else {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "[buildSmallIcon] smallIcon is empty");
         }
      } catch (Throwable var13) {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "load small icon failed:" + var13.getMessage());
      }

      if (!TextUtils.isEmpty(var7)) {
         try {
            Bitmap var9 = null;
            if (RecoverType9b4c5ae7.recoverMethod_a_aad3b203(var7)) {
               var9 = BitmapFactory.decodeFile(var7);
            }

            if (var9 != null) {
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "set small icon with path bitmap:" + var9);
               var6.recoverMethod_a_13462e(100);
               var6.recoverMethod_a_fef8dc59(var9);
               return var6;
            }

            Logger.recoverMethod_d_e925fa09("NotificationHelper", "not set small icon by bitmap");
            if (VERSION.SDK_INT >= 23) {
               Icon var10 = recoverMethod_a_81f26de0(var7);
               if (var10 != null) {
                  Logger.recoverMethod_d_e925fa09("NotificationHelper", "set small icon success by path:" + var7);
                  var6.recoverMethod_a_13462e(102);
                  var6.recoverMethod_a_5d527811(var10);
                  return var6;
               }
            }
         } catch (Throwable var12) {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "resource not found with sdcard path");
         }
      }

      if (var8 != 0) {
         var6.recoverMethod_a_13462e(101);
         var6.recoverMethod_b_13462e(var8);
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "set small icon success by resId:" + var8 + ",resName:" + var3);
         return var6;
      }

      if (!TextUtils.isEmpty(var2) && !var2.equals(var0.getPackageName())) {
         try {
            if (!RecoverType9b4c5ae5.recoverMethod_a_9d23c58b(var0, var2, var5)) {
               Logger.recoverMethod_w_e925fa09("NotificationHelper", "targetApp ：" + var2 + " is not install,will give up notify the msg");
               var6.recoverMethod_a_1349ef(993L);
               return var6;
            } else {
               Bitmap var15 = recoverMethod_c_f6a468db(var0, var2);
               if (var15 == null) {
                  Logger.recoverMethod_w_e925fa09("NotificationHelper", "not found targetApp small icon bitmap,will give up notify the msg");
                  var6.recoverMethod_a_1349ef(972L);
                  return var6;
               } else {
                  var6.recoverMethod_a_13462e(100);
                  var6.recoverMethod_a_fef8dc59(var15);
                  Logger.recoverMethod_d_e925fa09("NotificationHelper", "set small icon success by targetAppBitmap");
                  return var6;
               }
            }
         } catch (Throwable var11) {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "set small icon by targetApp icon bitmap failed:" + var11.getMessage());
            var6.recoverMethod_a_1349ef(972L);
            return var6;
         }
      } else {
         var8 = RecoverType2e4d1941.recoverMethod_a_f0554de6(var0);
         if (var8 != 0) {
            var6.recoverMethod_a_13462e(101);
            var6.recoverMethod_b_13462e(var8);
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "set small icon success by mine icon resId:" + var8);
            return var6;
         } else {
            var6.recoverMethod_a_1349ef(972L);
            return var6;
         }
      }
   }

   public static synchronized Bitmap recoverMethod_c_f6a468db(Context var0, String var1) {
      try {
         PackageManager var2 = null;
         ApplicationInfo var3 = null;
         var2 = var0.getApplicationContext().getPackageManager();
         var3 = var2.getApplicationInfo(var1, 0);
         Drawable var4 = var2.getApplicationIcon(var3);
         if (var4 != null) {
            if (VERSION.SDK_INT >= 26 && var4 instanceof AdaptiveIconDrawable) {
               Bitmap var5 = Bitmap.createBitmap(var4.getIntrinsicWidth(), var4.getIntrinsicHeight(), Config.ARGB_8888);
               Canvas var6 = new Canvas(var5);
               var4.setBounds(0, 0, var6.getWidth(), var6.getHeight());
               var4.draw(var6);
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "adaptive-icon to bitmap");
               return var5;
            }

            return ((BitmapDrawable)var4).getBitmap();
         }
      } catch (Throwable var7) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "getTargetAppBitmap failed:" + var7.getMessage());
      }

      return null;
   }

   private static String recoverMethod_d_39e223ba(Context var0, String var1, int var2) {
      if (!TextUtils.isEmpty(var1) && (var1.startsWith("http://") || var1.startsWith("https://"))) {
         String var3 = recoverMethod_c_39e223ba(var0, var1, var2);
         if (!TextUtils.isEmpty(var3)) {
            return var3;
         }

         Logger.recoverMethod_ww_e925fa09("NotificationHelper", "Get network picture failed.");
      }

      return null;
   }

   @TargetApi(23)
   private static Icon recoverMethod_a_81f26de0(String var0) {
      try {
         if (new File(var0).exists()) {
            return Icon.createWithFilePath(var0);
         }

         Logger.recoverMethod_w_e925fa09("NotificationHelper", "icon file doesn't exist, path=" + var0);
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "createIcon err:" + var2);
      }

      return null;
   }

   private static PushNotificationBuilder recoverMethod_a_7018dd49(Context var0, NotificationMessage var1) {
      try {
         Class var2 = Class.forName("cn.jpush.android.support.JPushSupport");
         Method var3 = var2.getDeclaredMethod("getNotificationBuilder", Context.class, NotificationMessage.class);
         return (PushNotificationBuilder)var3.invoke(var2, var0, var1);
      } catch (Throwable var4) {
         return null;
      }
   }

   private static int recoverMethod_a_134621(int var0) {
      byte var1 = 1;
      switch (var0) {
         case 0:
            var1 = 1;
            break;
         case 1:
            var1 = 16;
            break;
         case 2:
            var1 = 32;
      }

      return var1;
   }

   private static Notification recoverMethod_a_ec3dde70(Context var0, RecoverType2e4cce2a var1, RecoverType2e4d193d var2, RecoverType2e4d193d var3) {
      RecoverType2e4d193b.Membercf6bbb38 var4 = new RecoverType2e4d193b.Membercf6bbb38(
         var1.recoverField_I, "", var1.recoverField_D, var1.recoverField_H, var1.recoverField_au, var1.recoverField_E
      );
      RecoverType2e4d193b.recoverMethod_a_24a4a06a(var0, var4);
      Notification var5 = null;
      if (VERSION.SDK_INT >= 11) {
         Builder var6 = new Builder(var0);
         var6.setContentTitle(var1.recoverField_v).setContentText(var1.recoverField_y).setTicker(var1.recoverField_y);
         if (VERSION.SDK_INT >= 21 && !TextUtils.isEmpty(var1.recoverField_u)) {
            int var7 = Color.parseColor(var1.recoverField_u);
            var6.setColor(var7);
         }

         recoverMethod_a_126f4e1a(JPushConstants.mApplicationContext, var6, var2, var3);
         if (VERSION.SDK_INT >= 17) {
            var6.setShowWhen(true);
         }

         recoverMethod_a_f2a64de9(var0, var6, var4);
         if (!recoverMethod_d_5ffc0101(var0, var4.recoverField_a)) {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1037L, var0);
         }

         if (VERSION.SDK_INT >= 16) {
            if (var1.recoverField_z == 1 && !TextUtils.isEmpty(var1.recoverField_A)) {
               BigTextStyle var17 = null;
               var17 = new BigTextStyle();
               var17.bigText(var1.recoverField_A);
               var6.setStyle(var17);
            }

            if (var1.recoverField_z == 2 && !TextUtils.isEmpty(var1.recoverField_C)) {
               InboxStyle var19 = new InboxStyle();

               try {
                  TreeMap var8 = new TreeMap();
                  JSONObject var9 = new JSONObject(var1.recoverField_C);
                  Iterator var10 = var9.keys();

                  while (var10.hasNext()) {
                     String var11 = (String)var10.next();
                     String var12 = var9.optString(var11);
                     var8.put(var11, var12);
                  }

                  for (Object recoveryElement_966 : var8.values()) {
                     String var24 = (String)recoveryElement_966;
                     var19.addLine(var24);
                  }

                  var19.setSummaryText(" + " + var9.length() + " new messages");
               } catch (Throwable var15) {
                  Logger.recoverMethod_ee_e925fa09("NotificationHelper", "Set inbox style error: " + var15.getMessage());
               }

               var6.setStyle(var19);
            }

            if (var1.recoverField_z == 3 && !TextUtils.isEmpty(var1.recoverField_B)) {
               Logger.recoverMethod_i_e925fa09("NotificationHelper", "Set notification BPS with picture path:" + var1.recoverField_B);

               try {
                  BigPictureStyle var20 = new BigPictureStyle();
                  Bitmap var22 = BitmapFactory.decodeFile(var1.recoverField_B);
                  var20.bigPicture(var22);
                  var6.setStyle(var20);
               } catch (OutOfMemoryError var13) {
                  Logger.recoverMethod_ww_e925fa09("NotificationHelper", "Create bitmap failed caused by OutOfMemoryError.error:" + var13);
               } catch (Throwable var14) {
                  Logger.recoverMethod_ww_e925fa09("NotificationHelper", "Create big picture style failed. error:" + var14);
               }
            }

            if (!TextUtils.isEmpty(var1.recoverField_F) && VERSION.SDK_INT >= 21) {
               var6.setCategory(var1.recoverField_F);
            }
         }

         JSONArray var21 = var1.recoverField_bX;
         recoverMethod_a_a37e39a6(var0, var21, var6, var1.recoverField_d, var1.recoverField_ai);
         var5 = recoverMethod_a_ee23e758(var6);
      } else {
         var5 = new Notification(RecoverType2e4d1941.recoverMethod_a_f0554de6(var0), var1.recoverField_y, System.currentTimeMillis());
         recoverMethod_a_82e04d22(var5, var4);
         if (null == var1.recoverField_v) {
            var1.recoverField_v = var0.getApplicationInfo().name;
         }

         recoverMethod_a_d55e17cb(var5, var0, var1.recoverField_v, var1.recoverField_y, null);
      }

      return var5;
   }

   public static void recoverMethod_a_a37e39a6(Context var0, JSONArray var1, Builder var2, String var3, byte var4) {
      Logger.recoverMethod_d_e925fa09("NotificationHelper", "addCustomButtons: customButtons = " + (var1 != null ? var1.toString() : "null"));
      if (null != var1 && var1.length() > 0) {
         for (int var5 = 0; var5 < var1.length(); var5++) {
            JSONObject var6 = var1.optJSONObject(var5);
            RecoverType2e4cce2a.Memberce51f067 var7 = RecoverType2e4cce2a.Memberce51f067.recoverMethod_a_fd74075(var6);
            if (null == var7) {
               Logger.recoverMethod_w_e925fa09(
                  "NotificationHelper", "CustomButton parse failed, fromJSON is null, customButton: " + (var6 != null ? var6.toString() : "null")
               );
            } else if (TextUtils.isEmpty(var7.recoverField_c)) {
               Logger.recoverMethod_w_e925fa09("NotificationHelper", "CustomButton name is Empty");
            } else {
               var7.recoverField_a = var3;
               var7.recoverField_b = var4;
               Intent var8 = recoverMethod_a_6556af12(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED_CUSTOM_BUTTON", var7.recoverMethod_a_a7dbfe3());
               PendingIntent var9 = recoverMethod_e_c5c3ea1c(var0, var8);
               var2.addAction(RecoverType2e4d1941.recoverMethod_a_f0554de6(var0), var7.recoverField_c, var9);
            }
         }
      }
   }

   private static PendingIntent recoverMethod_e_c5c3ea1c(Context var0, Intent var1) {
      ActivityOptions var2 = recoverMethod_a_cbf93c65();
      return PendingIntent.getActivity(var0, RecoverType9b4c5af1.recoverMethod_a_9b68(), var1, 1140850688, var2 == null ? null : var2.toBundle());
   }

   @TargetApi(11)
   private static boolean recoverMethod_a_126f4e1a(Context var0, Builder var1, RecoverType2e4d193d var2, RecoverType2e4d193d var3) {
      boolean var4 = false;
      int var5 = var2.recoverMethod_a_9b68();
      if (var5 == 102 && VERSION.SDK_INT >= 23) {
         var1.setSmallIcon((Icon)var2.recoverMethod_b_810c345d());
         var4 = true;
      }

      if (!var4 && var5 == 100 && VERSION.SDK_INT >= 23) {
         var1.setSmallIcon(Icon.createWithBitmap(var2.recoverMethod_c_cf9f1b53()));
         var4 = true;
      }

      if (!var4 && var5 == 101) {
         boolean var6 = false;

         try {
            var0.getResources().getDrawable(var2.recoverMethod_d_9b68());
            var6 = true;
         } catch (Throwable var8) {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "resource not found with local app");
         }

         if (var6) {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "set small icon by local resId:" + var2.recoverMethod_d_9b68());
            var1.setSmallIcon(Integer.valueOf(var2.recoverMethod_d_9b68()));
            var4 = true;
         }
      }

      if (!var4) {
         return false;
      }

      var5 = var3.recoverMethod_a_9b68();
      if (var5 == 102) {
         if (VERSION.SDK_INT >= 23) {
            var1.setLargeIcon((Icon)var3.recoverMethod_b_810c345d());
         }
      } else if (var5 == 100) {
         var1.setLargeIcon(var3.recoverMethod_c_cf9f1b53());
      } else {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "not set large icon");
      }

      return true;
   }

   private static Notification recoverMethod_a_ee23e758(Builder var0) {
      try {
         return VERSION.SDK_INT >= 16 ? var0.build() : var0.getNotification();
      } catch (Throwable var2) {
         Logger.recoverMethod_ww_e925fa09("NotificationHelper", "Build notification error:" + var2.getMessage());
         return null;
      }
   }

   public static void recoverMethod_b_1a54e370(Context var0, int var1) {
      if (!RecoverType2e4d1940.recoverMethod_b_134632(var1)) {
         RecoverType2e4d1940.recoverMethod_a_134632(var1);
      }

      int var2 = RecoverTypea2146d45.recoverMethod_j_f0554de6(var0);
      Logger.recoverMethod_d_e925fa09("NotificationHelper", "queueSize:" + RecoverType2e4d1940.recoverMethod_b_9b68() + ", max num:" + var2);
      if (RecoverType2e4d1940.recoverMethod_b_9b68() > var2) {
         int var3 = RecoverType2e4d1940.recoverMethod_a_9b68();
         if (0 != var3) {
            recoverMethod_d_1a54e370(var0, var3);
         }
      }
   }

   public static void recoverMethod_c_1a54e370(Context var0, int var1) {
      if (var1 > 0) {
         for (int var3 = 0; var3 < var1; var3++) {
            Integer var2;
            if ((var2 = RecoverType2e4d1940.recoverMethod_a_9b68()) != 0) {
               recoverMethod_d_1a54e370(var0, var2);
            }
         }
      }
   }

   public static void recoverMethod_c_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      if (var1 != null) {
         int var2 = recoverMethod_a_73eedead(var1);
         recoverMethod_d_1a54e370(var0, var2);
      }
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      Integer var1;
      while ((var1 = RecoverType2e4d1940.recoverMethod_a_9b68()) != 0) {
         recoverMethod_d_1a54e370(var0, var1);
      }
   }

   public static void recoverMethod_d_1a54e370(Context var0, int var1) {
      try {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "action:cleanNotification - notificationId:" + var1);
         NotificationManager var2 = (NotificationManager)var0.getSystemService("notification");
         var2.cancel(var1);
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "cancelNotification failed:" + var3.getMessage());
      }
   }

   private static void recoverMethod_f_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      String var2 = "";
      String var3 = var1.recoverField_d;
      boolean var4 = false;
      String var5 = var1.recoverField_Z;
      if (!TextUtils.isEmpty(var5)) {
         String var6 = RecoverType2e4d157b.recoverMethod_a_3c5a5db8(var0, var5, "rich", 5, 5000L);
         if (!TextUtils.isEmpty(var6)) {
            var4 = true;
            var2 = var6;
         } else {
            var4 = false;
         }
      } else {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "Show url is Empty! Give up to download!");
      }

      String var11 = "";
      String var7 = RecoverType9b4c5ae7.recoverMethod_c_a4ea6d43(var0, var3);
      if (var4) {
         var11 = var7 + var3 + ".html";
         String var8 = var5.substring(0, var5.lastIndexOf("/") + 1);
         if (!var1.recoverField_af.isEmpty()) {
            boolean var9 = RecoverType2e4d1942.recoverMethod_a_26692411(var1.recoverField_af, var0, var8, var3, true);
            if (!var9) {
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "Loads rich push resources failed, webView will open url!");
               RecoverType8b455831.recoverMethod_a_36ca9ac3(var3, 1014L, var0);
               recoverMethod_b_ae3b7eb8(var0, var1);
               return;
            }

            Logger.recoverMethod_d_e925fa09("NotificationHelper", "Loads rich push resources succeed, webView will open cache!");
            var2 = var2.replaceAll("img src=\"" + var8, "img src=\"" + var7);
            if (RecoverType9b4c5ae7.recoverMethod_b_e925fa0d(var11, var2)) {
               var1.recoverField_ab = "file://" + var11;
               RecoverType8b455831.recoverMethod_a_36ca9ac3(var3, 995L, var0);
               recoverMethod_b_ae3b7eb8(var0, var1);
            } else {
               RecoverType8b455831.recoverMethod_a_36ca9ac3(var3, 1014L, var0);
            }
         } else {
            var1.recoverField_ab = var1.recoverField_Z;
            recoverMethod_b_ae3b7eb8(var0, var1);
         }
      } else {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "NOTE: failed to download html page. Give up this.");
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var3, 1014L, var0);
         RecoverType8b455831.recoverMethod_a_d7d77f79(var3, 1021L, null, var0);
      }
   }

   public static boolean recoverMethod_a_c4d2ecb4(Context var0, Intent var1) {
      String var2 = "";

      try {
         String var3 = var1.getStringExtra("msg_data");
         if (TextUtils.isEmpty(var3)) {
            return true;
         }

         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage string=" + var3);
         JSONObject var4 = new JSONObject(var3);
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage json=" + var4);
         var2 = var4.optString("messageId");
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage msgID=" + var2);
         String var5 = var4.optString("originalMessage");
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage originalMessage=" + var5);
         JSONObject var6 = new JSONObject(var5);
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage jsonObject=" + var6);
         int var7 = var6.optInt("notificaion_type");
         if (1 == var7) {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage local notification return true");
            return true;
         }

         String var8 = var6.optString("n_ssl");
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage server ssl=" + var8);
         String var9 = RecoverType9b4c5af0.recoverMethod_a_e96ea081(JCoreHelper.getUid(var0) + var2);
         if (!TextUtils.isEmpty(var9)) {
            var9 = var9.toLowerCase().replace(":", "");
         }

         Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage local ssl=" + var9);
         if (TextUtils.equals(var9, var8)) {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage check success");
            return true;
         }
      } catch (Throwable var10) {
      }

      Logger.recoverMethod_d_e925fa09("NotificationHelper", "handleOriginalMessage check fail drop it");
      RecoverType8b455831.recoverMethod_a_36ca9ac3(var2, 1107L, var0);
      return false;
   }

   public static RecoverType2e4cce2a recoverMethod_b_f07c8e6b(Context var0, Intent var1) {
      String var2 = var1.getStringExtra("msg_data");
      return TextUtils.isEmpty(var2) ? null : RecoverType2e4cce2a.recoverMethod_b_365d807c(var2);
   }

   public static RecoverType2e4cce2a.Memberce51f067 recoverMethod_c_c392554e(Context var0, Intent var1) {
      String var2 = var1.getStringExtra("custom_button");
      Logger.recoverMethod_d_e925fa09("NotificationHelper", "parseCustomButtonFromIntent: " + var2);
      return RecoverType2e4cce2a.Memberce51f067.recoverMethod_a_14ff071f(var2);
   }

   public static NotificationMessage recoverMethod_d_55f5921d(Context var0, Intent var1) {
      RecoverType2e4cce2a var2 = recoverMethod_b_f07c8e6b(var0, var1);
      NotificationMessage var3 = new NotificationMessage();
      if (null != var2) {
         var3 = var2.recoverMethod_b_4c6584f8();
      }

      if (TextUtils.isEmpty(var3.appkey)) {
         var3.appkey = JCoreHelper.getAppKey(var0);
      }

      return var3;
   }

   public static void recoverMethod_a_74ff4fff(Context var0, String var1, RecoverType2e4cce2a var2, Intent var3) {
      recoverMethod_a_c6e61809(var0, var1, var2, var3, null);
   }

   public static void recoverMethod_a_c6e61809(Context var0, String var1, RecoverType2e4cce2a var2, Intent var3, String var4) {
      try {
         String var5 = JPushConstants.getMessageReceiverClass(var0);
         Intent var6 = new Intent(var0, Class.forName(var5));
         if (var3 != null) {
            Bundle var7 = var3.getExtras();
            if (null != var7) {
               var6.putExtras(var7);
            }
         }

         var6.setAction(var1);
         if (var2 != null) {
            if (!TextUtils.isEmpty(var4)) {
               var2.recoverMethod_a_aad3b1ff(var4);
            }

            var6.putExtra("msg_data", var2.recoverMethod_c_73cf11cb());
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "send broadcast pushEntry: " + var2.recoverMethod_c_73cf11cb());
         }

         JPushMessageReceiver var12 = JPushConstants.getPushMessageReceiver(var0);
         if (var12 != null) {
            if (var1.equals("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY")) {
               var6.setAction("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION");
               var12.onMultiActionClicked(var0, var6);
            } else {
               NotificationMessage var8 = new NotificationMessage();
               if (null != var2) {
                  var8 = var2.recoverMethod_b_4c6584f8();
               }

               if (TextUtils.isEmpty(var8.appkey)) {
                  var8.appkey = JCoreHelper.getAppKey(var0);
               }

               if (var1.equals("cn.jpush.android.intent.NOTIFICATION_OPENED_CUSTOM_BUTTON")) {
                  RecoverType2e4cce2a.Memberce51f067 var9 = recoverMethod_c_c392554e(var0, var6);
                  if (var9 != null) {
                     NotificationCustomButton var10 = new NotificationCustomButton(
                        var9.recoverField_a, var9.recoverField_b, var9.recoverField_c, var9.recoverField_d, var9.recoverField_e, var9.recoverField_f
                     );
                     var12.onNotifyButtonClick(var0, var10);
                  } else {
                     Logger.recoverMethod_e_e925fa09("NotificationHelper", "CustomButton is null");
                  }
               } else if (var1.equals("cn.jpush.android.intent.NOTIFICATION_ARRIVED")) {
                  var12.onNotifyMessageArrived(var0, var8);
               } else if (var1.equals("cn.jpush.android.intent.NOTIFICATION_DISMISS")) {
                  var12.onNotifyMessageDismiss(var0, var8);
               } else if (var1.equals("cn.jpush.android.intent.NOTIFICATION_OPENED")) {
                  var12.onNotifyMessageOpened(var0, var8);
               } else if (var1.equals("cn.jpush.android.intent.NOTIFICATION_UN_SHOW")) {
                  var12.onNotifyMessageUnShow(var0, var8);
               } else if (var1.equals("cn.jpush.android.intent.IN_APP_MSG_SHOW")) {
                  var12.onInAppMessageShow(var0, var8);
               } else if (var1.equals("cn.jpush.android.intent.IN_APP_MSG_CLICK")) {
                  var12.onInAppMessageClick(var0, var8);
               } else {
                  RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var6);
               }
            }
         } else {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "send intent: " + var6);
            RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var6);
         }

         if (var1.equals("cn.jpush.android.intent.NOTIFICATION_DISMISS")) {
            RecoverType2e4ce86e.recoverMethod_b_ae3b7eb8(var0, var2);
         }
      } catch (Throwable var11) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "sendToUserReceiver failed:" + var11.getMessage());
      }
   }

   private static Intent recoverMethod_c_555de491(Context var0, String var1, RecoverType2e4cce2a var2) {
      Intent var3 = new Intent(var1);
      var3.setClass(var0, JNotifyActivity.class);
      var3.setFlags(8388608);
      if (var2 != null) {
         var3.putExtra("msg_data", var2.recoverMethod_c_73cf11cb());
      }

      return var3;
   }

   private static Intent recoverMethod_a_6556af12(Context var0, String var1, JSONObject var2) {
      Intent var3 = new Intent(var1);
      var3.setClass(var0, JNotifyActivity.class);
      var3.setFlags(8388608);
      if (null != var2) {
         var3.putExtra("custom_button", var2.toString());
      } else {
         Logger.recoverMethod_ee_e925fa09("NotificationHelper", "CustomButton parse failed, customButton is null, action: " + var1);
      }

      return var3;
   }

   public static Intent recoverMethod_a_555de491(Context var0, String var1, RecoverType2e4cce2a var2) {
      Intent var3 = new Intent(var1);
      var3.setClass(var0, PushActivity.class);
      if (var2 != null) {
         var3.putExtra("msg_data", var2.recoverMethod_c_73cf11cb());
      }

      if (!RecoverType9b4c5ae5.recoverMethod_j_f0554df7(var0) && VERSION.SDK_INT >= 11) {
         var3.addFlags(32768);
      }

      return var3;
   }

   public static Intent recoverMethod_b_555de491(Context var0, String var1, RecoverType2e4cce2a var2) {
      Intent var3 = new Intent(var1);
      var3.setClass(var0, PopWinActivity.class);
      if (var2 != null) {
         var3.putExtra("msg_data", var2.recoverMethod_c_73cf11cb());
      }

      return var3;
   }

   public static void recoverMethod_a_4abe089d(Context var0, int var1, int var2, String var3) {
      recoverMethod_a_4d98d517(var0, var1, var2, var3, null);
   }

   public static void recoverMethod_a_4d98d517(Context var0, int var1, int var2, String var3, Bundle var4) {
      try {
         String var5 = JPushConstants.getMessageReceiverClass(var0);
         if (!TextUtils.isEmpty(var5)) {
            boolean var6 = true;

            try {
               if (var1 == -1 || var1 == 1) {
                  Class<JPushMessageReceiver> var7 = JPushMessageReceiver.class;
                  Class var8 = Class.forName(var5);
                  String var9 = "onConnected";
                  Class[] var10 = new Class[]{Context.class, boolean.class};
                  var6 = recoverMethod_a_a1ac1eb(var7, var8, var9, var10);
                  Logger.recoverMethod_d_e925fa09("NotificationHelper", "method isImplemented: " + var6);
               }
            } catch (Throwable var11) {
            }

            if (var6) {
               Intent var13 = new Intent();
               var13.setClassName(var0, var5);
               var13.setAction(RecoverType9b4c5ae5.recoverMethod_e_a4ea6d43(var0, "cmd_msg"));
               var13.putExtra("cmd", var1);
               var13.putExtra("code", var2);
               var13.putExtra("message", var3);
               if (var4 != null) {
                  var13.putExtras(var4);
               }

               if (JPushConstants.getPushMessageReceiver(var0) != null) {
                  RecoverType8b45582f.recoverMethod_a_c3a16914(JPushConstants.getPushMessageReceiver(var0), var0, var13);
               } else {
                  RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var13);
               }
            }
         }
      } catch (Throwable var12) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "sendCmdMsgToUser failed:" + var12.getMessage());
      }
   }

   public static boolean recoverMethod_a_a1ac1eb(Class var0, Class var1, String var2, Class... var3) {
      try {
         Method var4 = var0.getDeclaredMethod(var2, var3);
         Method var5 = var1.getDeclaredMethod(var2, var3);
         return !var5.getDeclaringClass().equals(var4.getDeclaringClass());
      } catch (NoSuchMethodException var6) {
         return false;
      } catch (Throwable var7) {
         return true;
      }
   }

   private static void recoverMethod_g_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      String var2 = var1.recoverField_B;
      var1.recoverField_B = "";
      if (var1.recoverField_z == 3 && !TextUtils.isEmpty(var2)) {
         if (!var2.startsWith("http://") && !var2.startsWith("https://")) {
            String var4 = RecoverType9b4c5ae7.recoverMethod_e_a4ea6d43(var0, var2);
            if (!TextUtils.isEmpty(var4)) {
               var1.recoverField_B = var4;
            } else {
               Logger.recoverMethod_ww_e925fa09("NotificationHelper", "Get developer picture failed, show basic notification only.");
            }
         } else {
            String var3 = recoverMethod_a_22670df9(var0, var2, var1.recoverField_d);
            if (!TextUtils.isEmpty(var3)) {
               var1.recoverField_B = var3;
            } else {
               Logger.recoverMethod_ww_e925fa09("NotificationHelper", "Get network picture failed, show basic notification only.");
            }
         }
      }
   }

   private static String recoverMethod_a_22670df9(Context var0, String var1, String var2) {
      if (RecoverType9b4c5ae5.recoverMethod_a_afa340b9(var1, false)) {
         Logger.recoverMethod_i_e925fa09("NotificationHelper", "The url is a picture resources.");
         String var3 = var2 + RecoverType9b4c5ae5.recoverMethod_b_e96ea081(var1);
         String var4 = RecoverType9b4c5ae7.recoverMethod_c_a4ea6d43(var0, var2) + var3;
         Logger.recoverMethod_i_e925fa09("NotificationHelper", "Big picture notification resource path: " + var4);
         byte[] var5 = RecoverType2e4d157b.recoverMethod_a_693b7822(var0, var1, "notify_img", 5, 5000L, 4);
         if (null != var5) {
            return RecoverType9b4c5ae7.recoverMethod_a_44c5a30a(var4, var5) ? var4 : "";
         } else {
            return "";
         }
      } else {
         Logger.recoverMethod_i_e925fa09("NotificationHelper", "The url is not a picture resources.");
         return "";
      }
   }

   public static void recoverMethod_a_d55e17cb(Notification var0, Context var1, String var2, String var3, PendingIntent var4) {
      try {
         Class var5 = Class.forName("android.app.Notification");
         Method var6 = var5.getDeclaredMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
         var6.invoke(var0, var1, var2, var3, var4);
      } catch (Exception var7) {
         Object var8 = null;
      }
   }

   public static boolean recoverMethod_b_f0554df7(Context var0) {
      try {
         boolean var1 = RecoverTypea2146d45.recoverMethod_g_f0554df7(var0);
         if (!var1) {
            Logger.recoverMethod_ii_e925fa09("NotificationHelper", "Notification was disabled by JPushInterface.setPushTime !");
            return false;
         }

         String var2 = RecoverTypea2146d45.recoverMethod_h_e501eb0d(var0);
         if (TextUtils.isEmpty(var2)) {
            Logger.recoverMethod_i_e925fa09("NotificationHelper", "no time limited");
            return true;
         }

         Logger.recoverMethod_i_e925fa09("NotificationHelper", "push time is ：" + var2);
         String[] var3 = var2.split("_");
         String var4 = var3[0];
         String var5 = var3[1];
         char[] var6 = var4.toCharArray();
         String[] var7 = var5.split("\\^");
         Calendar var8 = Calendar.getInstance();
         int var9 = var8.get(7);
         int var10 = var8.get(11);

         for (char var14 : var6) {
            int var15 = Integer.valueOf(String.valueOf(var14));
            if (var9 == var15 + 1) {
               int var16 = Integer.valueOf(var7[0]);
               int var17 = Integer.valueOf(var7[1]);
               if (var10 >= var16 && var10 <= var17) {
                  return true;
               }
            }
         }

         Logger.recoverMethod_ii_e925fa09("NotificationHelper", "Current time is out of the push time - " + var2);
         return false;
      } catch (Exception var18) {
         return true;
      }
   }

   public static boolean recoverMethod_c_f0554df7(Context var0) {
      String var1 = RecoverTypea2146d45.recoverMethod_i_e501eb0d(var0);
      if (TextUtils.isEmpty(var1)) {
         return false;
      }

      try {
         JSONObject var2 = new JSONObject(var1);
         int var3 = var2.optInt("startHour", -1);
         int var4 = var2.optInt("startMins", -1);
         int var5 = var2.optInt("endHour", -1);
         int var6 = var2.optInt("endtMins", -1);
         if (var3 >= 0 && var4 >= 0 && var5 >= 0 && var6 >= 0 && var4 <= 59 && var6 <= 59 && var5 <= 23 && var3 <= 23) {
            Calendar var7 = Calendar.getInstance();
            int var8 = var7.get(11);
            int var9 = var7.get(12);
            Logger.recoverMethod_v_e925fa09(
               "NotificationHelper",
               "nowHour:" + var8 + ", nowMin:" + var9 + ", startHour:" + var3 + ", startMin:" + var4 + ", endHour:" + var5 + ", endMin:" + var6
            );
            if (var3 < var5) {
               if ((var8 <= var3 || var8 >= var5) && (var8 != var3 || var9 < var4) && (var8 != var5 || var9 > var6)) {
                  return false;
               }
            } else if (var3 == var5) {
               if (var4 >= var6) {
                  if (var8 == var3 && var9 > var6 && var9 < var4) {
                     return false;
                  }
               } else if (var8 != var3 || var9 < var4 || var9 > var6) {
                  return false;
               }
            } else {
               if (var3 <= var5) {
                  return false;
               }

               if ((var8 <= var3 || var8 > 23) && (var8 < 0 || var8 >= var5) && (var8 != var3 || var9 < var4) && (var8 != var5 || var9 > var6)) {
                  return false;
               }
            }

            Logger.recoverMethod_ii_e925fa09(
               "NotificationHelper", "Current time is in the range of silence time - " + var3 + ":" + var4 + " ~ " + var5 + ":" + var6
            );
            return true;
         } else {
            return false;
         }
      } catch (JSONException var10) {
         return false;
      }
   }

   public static int recoverMethod_d_f0554de6(Context var0) {
      if (JCoreHelper.checkSdkBanned(var0)) {
         Logger.recoverMethod_ww_e925fa09("NotificationHelper", "sdk is banned, call failed");
         return 0;
      }

      if (VERSION.SDK_INT >= 24) {
         try {
            NotificationManager var12 = (NotificationManager)var0.getSystemService("notification");
            if (var12 != null) {
               boolean var13 = var12.areNotificationsEnabled();
               Logger.recoverMethod_d_e925fa09("NotificationHelper", "from NotificationManager enable=" + var13);
               return var13 ? 1 : 0;
            } else {
               return -1;
            }
         } catch (Throwable var10) {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "isNotificationEnabled e:" + var10);
            return -1;
         }
      } else if (VERSION.SDK_INT >= 19) {
         try {
            AppOpsManager var1 = (AppOpsManager)var0.getSystemService("appops");
            ApplicationInfo var2 = var0.getApplicationInfo();
            String var3 = var0.getApplicationContext().getPackageName();
            int var4 = var2.uid;
            Class var5 = Class.forName(AppOpsManager.class.getName());
            Method var6 = var5.getMethod("checkOpNoThrow", int.class, int.class, String.class);
            Field var7 = var5.getDeclaredField("OP_POST_NOTIFICATION");
            int var8 = (Integer)var7.get(Integer.class);
            boolean var9 = (Integer)var6.invoke(var1, var8, var4, var3) == 0;
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "from AppOpsManager enable=" + var9);
            return var9 ? 1 : 0;
         } catch (Throwable var11) {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "appOps check e:" + var11);
            return -1;
         }
      } else {
         Logger.recoverMethod_d_e925fa09("NotificationHelper", "below 19 return true");
         return 1;
      }
   }

   public static boolean recoverMethod_e_f0554df7(Context var0) {
      try {
         if (JCoreHelper.checkSdkBanned(var0)) {
            Logger.recoverMethod_ww_e925fa09("NotificationHelper", "sdk is banned, call failed");
            return false;
         }

         Intent var1 = new Intent();
         var1.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
         if (VERSION.SDK_INT >= 26) {
            var1.putExtra("android.provider.extra.APP_PACKAGE", var0.getPackageName());
            var1.putExtra("android.provider.extra.CHANNEL_ID", var0.getApplicationInfo().uid);
         } else if (VERSION.SDK_INT >= 21) {
            var1.putExtra("app_package", var0.getPackageName());
            var1.putExtra("app_uid", var0.getApplicationInfo().uid);
         }

         var1.setFlags(268435456);
         var0.startActivity(var1);
         return true;
      } catch (Throwable var2) {
         return false;
      }
   }

   public static boolean recoverMethod_f_f0554df7(Context var0) {
      try {
         Intent var1 = new Intent();
         var1.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
         Uri var2 = Uri.fromParts("package", var0.getPackageName(), null);
         var1.setData(var2);
         var1.setFlags(268435456);
         var0.startActivity(var1);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   public static void recoverMethod_a_30483d64(Context var0, int var1, boolean var2) {
      try {
         boolean var3 = recoverMethod_d_f0554de6(var0) == 1;
         Logger.recoverMethod_d_e925fa09(
            "NotificationHelper", "[checkNotificationSettings] new state:" + var3 + ",last state:" + recoverField_b + ",soucre:" + var1 + ",isFocre:" + var2
         );
         if (!var3) {
            Logger.recoverMethod_kd_e925fa09("NotificationHelper", "notification trigger is off, will not show notification");
         }

         if (recoverField_b != null && var3 == recoverField_b && !var2) {
            Logger.recoverMethod_d_e925fa09("NotificationHelper", "[checkNotificationSettings] no change");
            return;
         }

         recoverField_b = var3;
         JPushMessageReceiver var4 = JPushConstants.getPushMessageReceiver(var0);
         if (var4 != null) {
            var4.onNotificationSettingsCheck(var0, recoverField_b, var1);
         } else {
            try {
               String var5 = JPushConstants.getMessageReceiverClass(var0);
               Intent var6 = new Intent(var0, Class.forName(var5));
               var6.setAction("on_noti_settings_check");
               var6.putExtra("isOn", recoverField_b);
               var6.putExtra("source", var1);
               RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var6);
            } catch (Throwable var7) {
               Logger.recoverMethod_w_e925fa09("NotificationHelper", "checkNotificationSettings send to user e:" + var7);
            }
         }

         RecoverType9b4c4fa1.recoverMethod_a_d8c30de5().recoverMethod_a_5ffc00fd(var0, "on_check");
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("NotificationHelper", "checkNotificationSettings e:" + var8);
      }
   }

   public static boolean recoverMethod_a_a8e1cb7d(Context var0, String var1, RecoverType2e4cce2a var2, NotificationMessage var3) {
      try {
         Method var4 = Class.forName(var1).getMethod("onNotifyMessageUnShow", Context.class, NotificationMessage.class);
         if (var4 == null) {
            Logger.recoverMethod_dd_e925fa09("NotificationHelper", "not found onNotifyMessageUnShow method");
            return false;
         }

         if (!TextUtils.isEmpty(var2.recoverField_J)) {
            if (var2.recoverField_J.equals("0")) {
               if (RecoverType9b4c5ae5.recoverMethod_j_f0554df7(var0)) {
                  Logger.recoverMethod_dd_e925fa09("NotificationHelper", "displayForeground...");
                  recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_UN_SHOW", var2, null);
                  RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1060L, var0);
                  return true;
               }
            } else {
               JPushMessageReceiver var5 = (JPushMessageReceiver)Class.forName(var1).newInstance();
               boolean var6 = var5.isNeedShowNotification(var0, var3, RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var0));
               if (!var6) {
                  Logger.recoverMethod_dd_e925fa09("NotificationHelper", "need not show notication by user");
                  recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_UN_SHOW", var2, null);
                  RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1061L, var0);
                  return true;
               }
            }
         }
      } catch (Throwable var7) {
         Logger.recoverMethod_ww_e925fa09("NotificationHelper", "check display foreground failed:" + var7.getMessage());
      }

      return false;
   }

   static class Membercf6bbef9 extends RecoverType9b4c5aea {
      private Context recoverField_a;
      private RecoverType2e4cce2a recoverField_b;

      public Membercf6bbef9(Context var1, RecoverType2e4cce2a var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_e = "NotificationHelper#NotifyAction";
      }

      public static void recoverMethod_a_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
         RecoverType2e4d193c.recoverMethod_g_ae3b7eb8(var0, var1);
         if (var1.recoverField_ad != 0 && var1.recoverField_ad != 4) {
            RecoverType2e4d193c.recoverMethod_f_ae3b7eb8(var0, var1);
         } else {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 995L, var0);
            RecoverType2e4d193c.recoverMethod_b_ae3b7eb8(var0, var1);
         }
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            long var1 = RecoverType9b4c5ae6.recoverMethod_b_aad3b1f3(this.recoverField_b.recoverField_av);
            long var3 = RecoverType9b4c5ae6.recoverMethod_b_aad3b1f3(this.recoverField_b.recoverField_aw);
            if (var1 > 0L && var1 == var3) {
               Logger.recoverMethod_ww_e925fa09("NotificationHelper", "the beginTime == endTime,not deal this notification");
               return;
            }

            long var5 = System.currentTimeMillis();
            if (var3 > 0L) {
               if (var3 < var1) {
                  Logger.recoverMethod_e_e925fa09("NotificationHelper", "illegal argument, endTime earlier than beginTime");
                  return;
               }

               if (var3 < var5) {
                  Logger.recoverMethod_w_e925fa09("NotificationHelper", this.recoverField_b.recoverField_d + " already end");
                  RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_b.recoverField_d, 1034L, this.recoverField_a);
                  return;
               }
            }

            if (var1 < var5) {
               recoverMethod_a_ae3b7eb8(this.recoverField_a, this.recoverField_b);
               if (var3 > 0L) {
                  RecoverType2e4d193f.Membercf6bca3c var7 = new RecoverType2e4d193f.Membercf6bca3c(
                     var3, RecoverType2e4d193c.recoverMethod_a_73eedead(this.recoverField_b)
                  );
                  RecoverType2e4d193f.recoverMethod_a_cd395451().recoverMethod_a_2b7802e6(this.recoverField_a, var7);
               }
            } else {
               RecoverType2e4d193f.Membercf6bca3c var9 = new RecoverType2e4d193f.Membercf6bca3c(var1, this.recoverField_b, false);
               RecoverType2e4d193f.recoverMethod_a_cd395451().recoverMethod_a_2b7802e6(this.recoverField_a, var9);
               RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_b.recoverField_d, 1035L, this.recoverField_a);
            }
         } catch (Throwable var8) {
            Logger.recoverMethod_w_e925fa09("NotificationHelper", "run NotifyAction failed:" + var8.getMessage());
         }
      }
   }
}
