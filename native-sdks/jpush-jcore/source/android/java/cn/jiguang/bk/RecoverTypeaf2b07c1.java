package cn.jiguang.bk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Notification.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.j.RecoverType5a6a399;
import cn.jpush.android.service.PushReceiver;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.security.auth.x500.X500Principal;

public class RecoverTypeaf2b07c1 {
   private static String recoverField_a;
   private static RecoverTypeaf2b07c1 recoverField_b;
   private static Boolean recoverField_c;
   private static int recoverField_d;
   private static String recoverField_e = "";
   private String recoverField_f;
   private static String recoverField_g = "";
   private final int recoverField_h = 600000;
   private final int recoverField_i = 3;
   private String recoverField_j = "";
   private String recoverField_k = "";
   private String recoverField_l = "";
   private String recoverField_m = "";
   private String recoverField_n = "";
   private String recoverField_o = "";
   private String recoverField_p = "";
   private Boolean recoverField_q = null;
   private Boolean recoverField_r = null;
   private String recoverField_s = "";
   private String recoverField_t = "";
   private String recoverField_u;
   private String recoverField_v;
   private String recoverField_w;
   private String recoverField_x;
   private String recoverField_y;
   private String recoverField_z;
   private String recoverField_A;
   private String recoverField_B;
   private String recoverField_C;
   private String recoverField_D;
   private String recoverField_E;
   private String recoverField_F;

   public static RecoverTypeaf2b07c1 recoverMethod_a_c6a05605() {
      if (null == recoverField_b) {
         synchronized (RecoverTypeaf2b07c1.class) {
            if (null == recoverField_b) {
               recoverField_b = new RecoverTypeaf2b07c1();
            }
         }
      }

      return recoverField_b;
   }

   private RecoverTypeaf2b07c1() {
   }

   public static synchronized void recoverMethod_b_9b75() {
      recoverField_b = null;
      recoverMethod_a_c6a05605();
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_a)) {
         return recoverField_a;
      }

      try {
         if (VERSION.SDK_INT >= 28) {
            recoverField_a = Application.getProcessName();
            if (!TextUtils.isEmpty(recoverField_a)) {
               return recoverField_a;
            }
         }

         recoverField_a = RecoverTypeaf2b07d2.recoverMethod_b_73cf11cb();
         if (!TextUtils.isEmpty(recoverField_a)) {
            return recoverField_a;
         }

         recoverField_a = recoverMethod_c_73cf11cb();
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("AndroidUtil", "#unexcepted - getCurProcessName failed:" + var2.getMessage());
      }

      return recoverField_a;
   }

   public static boolean recoverMethod_b_f0554df7(Context var0) {
      if (null == recoverField_c) {
         recoverField_c = var0.getPackageName().equals(recoverMethod_a_e501eb0d(var0));
      }

      return recoverField_c;
   }

   public static String recoverMethod_c_73cf11cb() {
      StringBuilder var0 = new StringBuilder();

      try {
         FileInputStream var1 = new FileInputStream("/proc/self/cmdline");

         try {
            byte[] var2 = new byte[256];

            int var3;
            while ((var3 = var1.read(var2)) > 0) {
               for (int var4 = 0; var4 < var3 && var2[var4] != 0; var4++) {
                  var0.append((char)var2[var4]);
               }
            }
         } catch (Throwable var6) {
            try {
               var1.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         var1.close();
      } catch (IOException var7) {
         return "";
      }

      return var0.toString();
   }

   public static String recoverMethod_a_a4ea6d43(Context var0, String var1) {
      try {
         PackageManager var2 = var0.getPackageManager();
         ComponentName var3 = new ComponentName(var0.getPackageName(), var1);
         ServiceInfo var4 = var2.getServiceInfo(var3, 128);
         return var4.processName;
      } catch (Throwable var5) {
         return "";
      }
   }

   public static String recoverMethod_b_a4ea6d43(Context var0, String var1) {
      try {
         PackageManager var2 = var0.getPackageManager();
         ComponentName var3 = new ComponentName(var0.getPackageName(), var1);
         ActivityInfo var4 = var2.getReceiverInfo(var3, 128);
         return var4.processName;
      } catch (Throwable var5) {
         return "";
      }
   }

   public static ProviderInfo recoverMethod_a_49ff051d(Context var0, String var1, String var2) {
      if (var0 != null && !TextUtils.isEmpty(var1) && !TextUtils.isEmpty(var2)) {
         try {
            PackageInfo var3 = RecoverTypeaf2b07cf.recoverMethod_a_afbf9147(var0, var1, 8);
            if (var3 == null || var3.providers == null || var3.providers.length == 0) {
               return null;
            }

            for (ProviderInfo var7 : var3.providers) {
               if (var2.equals(var7.authority)) {
                  return var7;
               }
            }
         } catch (Throwable var8) {
         }

         return null;
      } else {
         return null;
      }
   }

   public static String recoverMethod_a_aec4ad66(Context var0, Intent var1, String var2) {
      try {
         ResolveInfo var3 = var0.getPackageManager().resolveService(var1, 0);
         PackageManager var4 = var0.getPackageManager();
         if (var3.serviceInfo != null) {
            String var5 = var3.serviceInfo.name;
            if (!TextUtils.isEmpty(var5)) {
               boolean var6 = true;
               if (!TextUtils.isEmpty(var2) && 0 != var4.checkPermission(var2, var3.activityInfo.packageName)) {
                  var6 = false;
               }

               if (var6) {
                  return var5;
               }
            }
         }
      } catch (Throwable var7) {
      }

      return "";
   }

   public static int recoverMethod_c_f0554de6(Context var0) {
      if (var0 == null) {
         return -1;
      }

      if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(1200)) {
         return -1;
      }

      Intent var1 = null;
      IntentFilter var2 = new IntentFilter("android.intent.action.BATTERY_CHANGED");

      try {
         var1 = RecoverTypeaf2b07d1.recoverMethod_a_6169dcd2(var0, null, var2, var0.getPackageName() + RecoverType5a681cd.recoverField_a, null, false);
      } catch (SecurityException var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AndroidUtil", "getChargedStatus SecurityException");
      } catch (Exception var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AndroidUtil", "getChargedStatus unkown exception:" + var6.getMessage());
      }

      if (var1 == null) {
         return -1;
      }

      int var3 = var1.getIntExtra("status", -1);
      boolean var4 = var3 == 2 || var3 == 5;
      return !var4 ? -1 : var1.getIntExtra("plugged", -1);
   }

   public static boolean recoverMethod_d_f0554df7(Context var0) {
      String var1 = var0.getApplicationInfo().sourceDir;
      if (RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("AndroidUtil", "Unexpected: cannot get pk installed path");
         return false;
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "Current pk installed path: " + var1);
      if (var1.startsWith("/system/app/")) {
         return true;
      }

      if (var1.startsWith("/data/app/")) {
         return false;
      }

      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("AndroidUtil", "NOTE: the pk does not installed in system/data. ");
      return false;
   }

   public static boolean recoverMethod_e_f0554df7(Context var0) {
      boolean var1 = false;

      try {
         ApplicationInfo var2 = var0.getApplicationInfo();
         boolean var3 = (var2.flags & 2) != 0;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "isDebug:" + var3);
         X500Principal var4 = new X500Principal("CN=Android Debug,O=Android,C=US");
         String[] var5 = new String[]{"CN=Android Debug", "O=Android", "C=US"};
         PackageInfo var6 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, 64);
         if (null != var6) {
            Signature[] var7 = var6.signatures;
            CertificateFactory var8 = CertificateFactory.getInstance("X.509");

            for (Signature var12 : var7) {
               ByteArrayInputStream var13 = new ByteArrayInputStream(var12.toByteArray());
               X509Certificate var14 = (X509Certificate)var8.generateCertificate(var13);
               var1 = var14.getSubjectX500Principal().equals(var4);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "debuggable :" + var1);
               if (var1) {
                  break;
               }

               String var15 = null;

               try {
                  var15 = var14.getSubjectX500Principal().getName();
               } catch (Exception var17) {
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "certName:" + var15);
               if (var15 != null && var15.contains(var5[0]) && var15.contains(var5[1]) && var15.contains(var5[2])) {
                  var1 = true;
                  break;
               }
            }
         }
      } catch (Throwable var18) {
      }

      return var1;
   }

   public static ComponentInfo recoverMethod_a_e3a6cbf6(Context var0, String var1, Class var2) {
      if (var0 != null && !TextUtils.isEmpty(var1) && var2 != null) {
         try {
            byte var3 = 0;
            if (Service.class.isAssignableFrom(var2)) {
               var3 = 4;
            } else if (BroadcastReceiver.class.isAssignableFrom(var2)) {
               var3 = 2;
            } else if (Activity.class.isAssignableFrom(var2)) {
               var3 = 1;
            } else if (ContentProvider.class.isAssignableFrom(var2)) {
               var3 = 8;
            }

            PackageInfo var4 = RecoverTypeaf2b07cf.recoverMethod_a_afbf9147(var0, var1, var3);
            if (null == var4) {
               return null;
            }

            ComponentInfo[] var5;
            switch (var3) {
               case 1:
                  var5 = var4.activities;
                  break;
               case 2:
                  var5 = var4.receivers;
                  break;
               case 3:
               case 5:
               case 6:
               case 7:
               default:
                  var5 = null;
                  break;
               case 4:
                  var5 = var4.services;
                  break;
               case 8:
                  var5 = var4.providers;
            }

            if (var5 == null) {
               return null;
            }

            for (ComponentInfo var9 : var5) {
               try {
                  if (var2.isAssignableFrom(Class.forName(var9.name, false, var2.getClassLoader()))) {
                     return var9;
                  }
               } catch (Throwable var11) {
               }
            }
         } catch (Throwable var12) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("AndroidUtil", "hasComponent error:" + var12);
         }

         return null;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
            "AndroidUtil", "Action - hasComponent, invalide param, context:" + var0 + ",packageName:" + var1 + ",cls:" + var2
         );
         return null;
      }
   }

   public static void recoverMethod_a_9f8491a6(Context var0, String var1, int var2) {
      if (!recoverMethod_e_f0554df7(var0)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "not debuggable");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "action:showPermanentNotification");
         Intent var3 = new Intent(var0, PushReceiver.class);
         var3.setAction("noti_open_proxy");
         var3.addCategory(var0.getPackageName());
         var3.putExtra("debug_notification", true);
         var3.putExtra("toastText", var1);
         var3.putExtra("type", var2);
         PendingIntent var4 = PendingIntent.getBroadcast(var0, 0, var3, 201326592);
         NotificationManager var5 = (NotificationManager)var0.getSystemService("notification");

         int var6;
         try {
            var6 = var0.getPackageManager().getApplicationInfo(var0.getApplicationContext().getPackageName(), 0).icon;
         } catch (Throwable var16) {
            RecoverTypeaf2a9ae9.recoverMethod_e_1765c98c("AndroidUtil", "failed to get application info and icon.", var16);
            var6 = 17301586;
         }

         String var7 = "Jiguang提示：包名和AppKey不匹配";
         String var8 = "请到 Portal 上获取您的包名和AppKey并更新AndroidManifest相应字段";
         long var9 = System.currentTimeMillis();
         Notification var11;
         if (VERSION.SDK_INT >= 11) {
            Builder var12 = new Builder(var0.getApplicationContext())
               .setContentTitle(var7)
               .setContentText(var8)
               .setContentIntent(var4)
               .setSmallIcon(var6)
               .setTicker(var1)
               .setWhen(var9);
            if (VERSION.SDK_INT >= 26) {
               NotificationChannel var13 = var5.getNotificationChannel("JPush_Notification");
               if (var13 == null) {
                  NotificationChannel var14 = new NotificationChannel("JPush_Notification", "JPush_Notification", 3);
                  var5.createNotificationChannel(var14);
               }

               var12.setChannelId("JPush_Notification");
            }

            var11 = var12.getNotification();
            var11.flags = 34;
         } else {
            var11 = new Notification(var6, var1, var9);
            var11.flags = 34;

            try {
               RecoverTypeaf2b07d2.recoverMethod_a_1564af74(
                  var11,
                  "setLatestEventInfo",
                  new Object[]{var0, var7, var8, var4},
                  new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class}
               );
            } catch (Exception var15) {
            }
         }

         recoverField_d = 10000;
         var5.notify(recoverField_d, var11);
      }
   }

   public static void recoverMethod_f_f0554df3(Context var0) {
      try {
         if (recoverField_d != 0) {
            NotificationManager var1 = (NotificationManager)var0.getSystemService("notification");
            var1.cancel(recoverField_d);
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AndroidUtil", "[canclePermanentNotification] failed:" + var2.getMessage());
      }
   }

   public static boolean recoverMethod_d_9b79() {
      return false;
   }

   public static String recoverMethod_g_e501eb0d(Context var0) {
      return null;
   }

   public static boolean recoverMethod_h_f0554df7(Context var0) {
      String var1 = RecoverTypeaf2b07d5.recoverMethod_a_8ea82db7("ro.product.brand", "");
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "brand = " + var1);
      String var2 = RecoverTypeaf2b07d5.recoverMethod_a_8ea82db7("ro.miui.ui.version.name", "");
      if (!TextUtils.isEmpty(var1) && "Xiaomi".equals(var1) && !TextUtils.isEmpty(var2)) {
         String var3 = RecoverTypeaf2b07d5.recoverMethod_a_8ea82db7("ro.build.version.incremental", "");
         if (!TextUtils.isEmpty(var3) && var3.startsWith("V7.1")) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AndroidUtil", "7.1 will not get wifi list");
            return false;
         }
      }

      return true;
   }

   public static String recoverMethod_i_e501eb0d(Context var0) {
      String var1 = null;
      int var2 = -1;

      try {
         if (var0 != null) {
            Object var3 = var0.getApplicationContext().getSystemService("country_detector");
            if (var3 != null) {
               Method var4 = var3.getClass().getDeclaredMethod("detectCountry");
               if (var4 != null) {
                  Object var5 = var4.invoke(var3);
                  if (var5 != null) {
                     Method var6 = var5.getClass().getDeclaredMethod("getCountryIso");
                     var1 = (String)var6.invoke(var5);
                     Method var7 = var5.getClass().getDeclaredMethod("getSource");
                     var2 = (Integer)var7.invoke(var5);
                  }
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "country_detector is null");
            }
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("AndroidUtil", "getCountryCode failed, error :" + var8);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "get CountCode = " + var1 + " source = " + var2);
      return var2 != 0 && var2 != 1 ? "" : var1;
   }

   public static String recoverMethod_j_e501eb0d(Context var0) {
      try {
         ApplicationInfo var1 = var0.getApplicationInfo();
         int var2 = var1.labelRes;
         if (var2 == 0) {
            return var1.nonLocalizedLabel != null ? var1.nonLocalizedLabel.toString() : var0.getPackageManager().getApplicationLabel(var1).toString();
         } else {
            return var0.getString(var2);
         }
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("AndroidUtil", "Error getting application name.", var3);
         return null;
      }
   }

   public static void recoverMethod_b_9d23c587(Context var0, String var1, String var2) {
      try {
         Intent var3 = new Intent(var0, PushReceiver.class);
         var3.setAction(var1);
         Bundle var4 = new Bundle();
         var4.putString("data", var2);
         var3.putExtras(var4);
         var3.setPackage(var0.getPackageName());
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "send config  intent=" + var3);
         var0.sendBroadcast(var3, var0.getPackageName() + RecoverType5a681cd.recoverField_a);
      } catch (Throwable var5) {
      }
   }

   public String recoverMethod_k_e501eb0d(final Context var1) {
      if (var1 != null && var1.getResources() != null) {
         Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 2012, true, false, new RecoverType5a6a399.Member368c2616() {
            @Override
            public Object recoverMethod_c_810c345d() {
               return RecoverTypeaf2b07c1.this.recoverField_f;
            }

            @Override
            public void recoverMethod_b_5d527811(Object var1x) {
               if (var1x instanceof String) {
                  RecoverTypeaf2b07c1.this.recoverField_f = (String)var1x;
               }
            }

            @Override
            public Object recoverMethod_b_810c345d() {
               DisplayMetrics var1x = var1.getResources().getDisplayMetrics();
               if (var1x == null) {
                  return "0*0";
               }

               int var2x = var1x.widthPixels;
               int var3 = var1x.heightPixels;
               return var2x + "*" + var3;
            }
         });
         return var2 instanceof String ? (String)var2 : "0*0";
      } else {
         return "0*0";
      }
   }

   public synchronized String recoverMethod_a_5e799b7f(Context var1, boolean var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "getWifiSsid, not can get ssid");
      return "";
   }

   public synchronized String recoverMethod_b_5e799b7f(Context var1, boolean var2) {
      return "";
   }

   public synchronized int recoverMethod_l_f0554de6(Context var1) {
      return 0;
   }

   public synchronized int recoverMethod_m_f0554de6(Context var1) {
      return -1;
   }

   @SuppressLint("MissingPermission")
   public synchronized String recoverMethod_c_a4ea6d43(Context var1, String var2) {
      return this.recoverMethod_a_9ba16009(var1, var2, true);
   }

   public synchronized String recoverMethod_a_9ba16009(Context var1, String var2, boolean var3) {
      String var4 = "";
      if (!RecoverTypeaf2b07d5.recoverMethod_h_aad3b203(var4)) {
         var4 = var2;
      }

      return var4;
   }

   @SuppressLint("MissingPermission")
   public synchronized String recoverMethod_d_a4ea6d43(Context var1, String var2) {
      String var3 = "";
      if (TextUtils.isEmpty(var3)) {
         var3 = var2;
      }

      return var3;
   }

   public String recoverMethod_n_e501eb0d(Context var1) {
      return this.recoverMethod_c_5e799b7f(var1, false);
   }

   public String recoverMethod_c_5e799b7f(final Context var1, boolean var2) {
      String var3 = "";

      try {
         new RecoverType5a6a399.Member368c2616() {
            @Override
            public Object recoverMethod_c_810c345d() {
               return RecoverTypeaf2b07c1.this.recoverField_p;
            }

            @Override
            public void recoverMethod_b_5d527811(Object var1x) {
               if (var1x instanceof String) {
                  RecoverTypeaf2b07c1.this.recoverField_p = (String)var1x;
               }
            }

            @Override
            public Object recoverMethod_b_810c345d() {
               return RecoverTypeaf2b07c1.this.recoverMethod_C_e501eb0d(var1);
            }
         };
         boolean var5 = RecoverTypeaf2a9725.recoverMethod_b_9b79();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "onlyVerifiSDK=" + var5);
      } catch (Throwable var6) {
      }

      return var3;
   }

   private String recoverMethod_C_e501eb0d(Context var1) {
      String var2 = "";

      try {
         var2 = Secure.getString(var1.getContentResolver(), "android_id");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "android id is " + var2);
      } catch (Throwable var4) {
      }

      return RecoverTypeaf2b07d5.recoverMethod_h_aad3b203(var2) ? var2 : "";
   }

   public boolean recoverMethod_o_f0554df7(Context var1) {
      return false;
   }

   public String recoverMethod_b_9ba16009(Context var1, String var2, boolean var3) {
      return recoverField_e;
   }

   public String recoverMethod_a_a9514c81(boolean var1) {
      return this.recoverField_s;
   }

   public String recoverMethod_e_73cf11cb() {
      return "";
   }

   private static List recoverMethod_b_80d4048d(Context var0, Intent var1, String var2) {
      ArrayList var3 = new ArrayList();

      try {
         List var4 = var0.getPackageManager().queryBroadcastReceivers(var1, 0);
         PackageManager var5 = var0.getPackageManager();

         for (Object recoveryElement_705 : var4) {
            ResolveInfo var7 = (ResolveInfo)recoveryElement_705;
            if (var7.activityInfo != null) {
               String var8 = var7.activityInfo.name;
               if (!TextUtils.isEmpty(var8)) {
                  boolean var9 = true;
                  if (!TextUtils.isEmpty(var2) && 0 != var5.checkPermission(var2, var7.activityInfo.packageName)) {
                     var9 = false;
                  }

                  if (var9) {
                     var3.add(var8);
                  }
               }
            }
         }
      } catch (Throwable var10) {
      }

      return var3;
   }

   public static void recoverMethod_a_c4d2ecb0(Context var0, Intent var1) {
      try {
         var0.sendBroadcast(var1);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("AndroidUtil", "send broadcast failed: " + var3.getMessage());
         recoverMethod_b_c4d2ecb0(var0, var1);
      }
   }

   public static void recoverMethod_b_c4d2ecb0(Context var0, Intent var1) {
      try {
         List var2 = recoverMethod_b_80d4048d(var0, var1, null);
         if (var2 != null && !var2.isEmpty()) {
            for (Object recoveryElement_739 : var2) {
               String var4 = (String)recoveryElement_739;
               try {
                  Intent var5 = (Intent)var1.clone();
                  var5.setComponent(new ComponentName(var0.getPackageName(), var4));
                  var0.sendBroadcast(var5);
               } catch (Exception var6) {
                  RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09(
                     "AndroidUtil", "sendBroadcast failed again:" + var6.getMessage() + ", action:" + var1.getAction()
                  );
               }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("AndroidUtil", "sendBroadcast failed again: receiver not found, action:" + var1.getAction());
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("AndroidUtil", "tryAgainSendBrocast failed:" + var7.getMessage());
      }
   }

   public String recoverMethod_p_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 1015, true, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_u;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_u = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverMethod_a_e96ea081(Build.PRODUCT);
         }
      });
      if (var2 instanceof String) {
         this.recoverField_u = (String)var2;
      }

      return this.recoverField_u;
   }

   public String recoverMethod_q_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_8f0f26ca(var1, 1036, 1800000L, 86400000L, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_v;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_v = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            Configuration var1x = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d().getResources().getConfiguration();
            float var2x = var1x.fontScale;
            return var2x + "";
         }
      });
      if (var2 instanceof String) {
         this.recoverField_v = (String)var2;
      }

      return this.recoverField_v;
   }

   public String recoverMethod_r_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_b350334a(var1, 1007, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_w;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_w = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverMethod_a_e96ea081(Build.FINGERPRINT);
         }
      });
      if (var2 instanceof String) {
         this.recoverField_w = (String)var2;
      }

      return this.recoverField_w;
   }

   public String recoverMethod_s_e501eb0d(Context var1) {
      if (!TextUtils.isEmpty(this.recoverField_x)) {
         return this.recoverField_x;
      }

      if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2008)) {
         this.recoverField_x = this.recoverMethod_a_e96ea081(Build.MANUFACTURER);
      }

      return this.recoverField_x;
   }

   public String recoverMethod_t_e501eb0d(Context var1) {
      if (!TextUtils.isEmpty(this.recoverField_y)) {
         return this.recoverField_y;
      }

      if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2009)) {
         this.recoverField_y = this.recoverMethod_a_e96ea081(Build.MODEL);
      }

      return this.recoverField_y;
   }

   public String recoverMethod_u_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 2009, true, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_z;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_z = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverMethod_a_e96ea081(Build.MODEL);
         }
      });
      if (var2 instanceof String) {
         this.recoverField_z = (String)var2;
      }

      return this.recoverField_z;
   }

   public String recoverMethod_v_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 2002, true, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_A;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_A = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverMethod_a_e96ea081(String.format(Locale.ENGLISH, Build.BRAND));
         }
      });
      if (var2 instanceof String) {
         this.recoverField_A = (String)var2;
      }

      return this.recoverField_A;
   }

   public String recoverMethod_w_e501eb0d(final Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 2006, true, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_B;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1x) {
            if (var1x instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_B = (String)var1x;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverMethod_a_e96ea081(var1.getResources().getConfiguration().locale.toString());
         }
      });
      if (var2 instanceof String) {
         this.recoverField_B = (String)var2;
      }

      return this.recoverField_B;
   }

   public String recoverMethod_x_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 2008, true, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_C;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_C = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverMethod_a_e96ea081(Build.MANUFACTURER);
         }
      });
      if (var2 instanceof String) {
         this.recoverField_C = (String)var2;
      }

      return this.recoverField_C;
   }

   public String recoverMethod_y_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_344864ca(var1, 2014, false, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_D;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_D = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            String var1x = "";
            long var2x = TimeZone.getDefault().getRawOffset() / 3600000L;
            if (var2x > 0L) {
               var1x = "+" + var2x;
            } else if (var2x < 0L) {
               var1x = "-" + var2x;
            } else {
               var1x = "" + var2x;
            }

            return var1x.replace("--", "-");
         }
      });
      if (var2 instanceof String) {
         this.recoverField_D = (String)var2;
      }

      return this.recoverField_D;
   }

   public String recoverMethod_z_e501eb0d(Context var1) {
      Object var2 = RecoverType5a6a399.recoverMethod_a_8f0f26ca(var1, 1014, 1800000L, 259200000L, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverTypeaf2b07c1.this.recoverField_E;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverTypeaf2b07c1.this.recoverField_E = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            String var1x = String.format(Locale.ENGLISH, VERSION.RELEASE);
            return !TextUtils.isEmpty(var1x) ? var1x.trim() : null;
         }
      });
      if (var2 instanceof String) {
         this.recoverField_E = (String)var2;
      }

      return this.recoverField_E;
   }

   private String recoverMethod_a_e96ea081(String var1) {
      return var1 != null ? var1.trim() : null;
   }

   public String recoverMethod_A_e501eb0d(Context var1) {
      try {
         new RecoverType5a6a399.Member368c2616() {
            @Override
            public Object recoverMethod_c_810c345d() {
               return RecoverTypeaf2b07c1.this.recoverField_F;
            }

            @Override
            public Object recoverMethod_b_810c345d() {
               return RecoverTypeaf2b07c1.this.recoverMethod_f_73cf11cb();
            }

            @Override
            public void recoverMethod_b_5d527811(Object var1) {
               if (var1 instanceof String) {
                  RecoverTypeaf2b07c1.this.recoverField_F = (String)var1;
               }
            }
         };
         Object var3 = null;
         if (var3 instanceof String) {
            this.recoverField_F = (String)var3;
         }
      } catch (Throwable var4) {
      }

      return this.recoverField_F;
   }

   public String recoverMethod_f_73cf11cb() {
      return String.valueOf(System.currentTimeMillis() - SystemClock.elapsedRealtime());
   }

   public static boolean recoverMethod_e_5ffc0101(Context var0, String var1) {
      PackageManager var2 = var0.getPackageManager();

      try {
         int var3 = var2.checkPermission("android.permission.INTERNET", var1);
         if (var3 == 0) {
            return true;
         }
      } catch (Throwable var6) {
      }

      try {
         ApplicationInfo var7 = var2.getApplicationInfo(var1, 0);
         if (var7 != null) {
            return true;
         }
      } catch (Throwable var5) {
      }

      try {
         Intent var8 = recoverMethod_a_6bf5260(var2, var1);
         if (var8 != null) {
            return true;
         }
      } catch (Throwable var4) {
      }

      return false;
   }

   private static Intent recoverMethod_a_6bf5260(PackageManager var0, String var1) {
      Intent var2 = new Intent("android.intent.action.MAIN");
      var2.addCategory("android.intent.category.INFO");
      var2.setPackage(var1);
      ResolveInfo var3 = var0.resolveActivity(var2, 0);
      if (var3 == null) {
         var2.removeCategory("android.intent.category.INFO");
         var2.addCategory("android.intent.category.LAUNCHER");
         var2.setPackage(var1);
         var3 = var0.resolveActivity(var2, 0);
      }

      if (var3 == null) {
         return null;
      }

      Intent var4 = new Intent(var2);
      var4.setFlags(268435456);
      var4.setClassName(var3.activityInfo.packageName, var3.activityInfo.name);
      return var4;
   }

   public static String recoverMethod_B_e501eb0d(Context var0) {
      if (VERSION.SDK_INT >= 17) {
         try {
            return Global.getString(var0.getContentResolver(), "device_name");
         } catch (Throwable var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static String recoverMethod_g_73cf11cb() {
      String var0 = "Exception while attempting to read kernel information";
      String var1 = System.getProperty("os.version");
      File var2 = new File("/proc/version");
      if (!var2.canRead()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AndroidUtil", "Cannot read /proc/version, returning default version.");
         return var1;
      }

      try {
         BufferedReader var3 = new BufferedReader(new FileReader(var2));

         String var4;
         try {
            var4 = var3.readLine();
         } catch (Throwable var7) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         var3.close();
         return var4;
      } catch (IOException var8) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("AndroidUtil", var0, var8);
         return var1;
      }
   }
}
