package cn.jpush.android.ad;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.service.JNotifyActivity;
import cn.jpush.android.ui.PopWinActivity;
import cn.jpush.android.ui.PushActivity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecoverType9b4c5ae5 {
   private static List recoverField_a = new ArrayList();
   private static LinkedHashMap recoverField_b = new LinkedHashMap();
   private static String recoverField_c = "";
   private static Boolean recoverField_d;
   private static String recoverField_e;
   private static final byte[] recoverField_f = new byte[]{
      105, 120, 117, 66, 79, 112, 108, 56, 102, 85, 66, 114, 97, 98, 63, 103, 69, 123, 91, 115, 101, 68, 73, 119, 111, 101
   };
   private static final byte[] recoverField_g = new byte[]{83, 124, 124, 87, 125, 92, 79, 110, 78, 127, 125, 85, 97, 122, 107, 115, 98, 67};
   private static final byte[] recoverField_h = new byte[]{123, 115, 101, 122, 65, 111, 105, 69, 114, 66, 73, 105, 124, 83, 127, 81, 66, 117, 109, 114};
   private static String recoverField_i = "";
   private static Class recoverField_j;
   private static Method recoverField_k;
   private static String recoverField_l = "android.permission.POST_NOTIFICATIONS";
   private static String recoverField_m = "";

   private static boolean recoverMethod_f_aad3b203(String var0) {
      try {
         return recoverField_a.contains(var0);
      } catch (Throwable var2) {
         return false;
      }
   }

   private static void recoverMethod_c_e925fa09(String var0, String var1) {
      try {
         if (!recoverField_b.containsKey(var0)) {
            recoverField_b.put(var0, var1);
         }
      } catch (Throwable var3) {
      }
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      try {
         String var1 = (String)recoverField_b.get(var0);
         recoverField_b.remove(var0);
         return var1;
      } catch (Throwable var2) {
         return "";
      }
   }

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      try {
         ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService("connectivity");
         NetworkInfo var2 = var1.getActiveNetworkInfo();
         return var2 != null && var2.isConnected();
      } catch (Throwable var3) {
         return false;
      }
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

            PackageInfo var4 = JCoreHelper.getPackageInfo(var0, var1, var3);
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
            Logger.recoverMethod_ww_e925fa09("AndroidUtil", "hasComponent error:" + var12.getMessage());
         }

         return null;
      } else {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "Action - hasComponent, invalide param, context:" + var0 + ",packageName:" + var1 + ",cls:" + var2);
         return null;
      }
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_c)) {
         return recoverField_c;
      }

      try {
         ApplicationInfo var1 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 0);
         recoverField_c = var0.getPackageManager().getApplicationLabel(var1).toString();
      } catch (Throwable var2) {
      }

      return recoverField_c;
   }

   public static String recoverMethod_b_e96ea081(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return "";
      }

      try {
         int var1 = var0.indexOf("?");
         if (var1 > 0) {
            var0 = var0.substring(0, var1);
            if (TextUtils.isEmpty(var0)) {
               return "";
            }
         }

         var0 = var0.substring(var0.lastIndexOf("."));
      } catch (Throwable var2) {
         var0 = "";
      }

      return var0;
   }

   public static boolean recoverMethod_a_afa340b9(String var0, boolean var1) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      HttpURLConnection var2 = null;
      URL var3 = null;

      try {
         var3 = new URL(var0);
         var2 = (HttpURLConnection)var3.openConnection();
         String var4 = var2.getContentType();
         Logger.recoverMethod_v_e925fa09("AndroidUtil", "httpGetIconBytes contentType:" + var4);
         if (recoverMethod_f_aad3b203(var4)) {
            if (var1) {
               recoverMethod_c_e925fa09(var0, var4);
            }

            return true;
         }

         Logger.recoverMethod_ke_e925fa09("AndroidUtil", "only support suffix jpg/jpeg/png!! current type=" + var4);
      } catch (Throwable var16) {
      } finally {
         try {
            if (var3 != null) {
               Object var18 = null;
            }

            if (var2 != null) {
               var2.disconnect();
            }
         } catch (Throwable var15) {
         }
      }

      return false;
   }

   public static boolean recoverMethod_c_aad3b203(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      var0 = var0.trim();
      String var1 = "^[http|https]+://.*";
      boolean var2 = var0.matches(var1);
      if (!var2) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "Invalid url - " + var0);
      }

      return var2;
   }

   public static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      boolean var2 = false;

      try {
         if (VERSION.SDK_INT >= 23) {
            if (var0.getApplicationInfo().targetSdkVersion >= 23) {
               int var3 = var0.checkSelfPermission(var1);
               var2 = var3 == 0;
            } else {
               String var7 = AppOpsManager.permissionToOp(var1);
               if (var7 == null) {
                  var2 = true;
               } else {
                  AppOpsManager var4 = (AppOpsManager)var0.getSystemService("appops");
                  int var5 = var4.noteProxyOpNoThrow(var7, var0.getPackageName());
                  var2 = var5 == 0;
               }
            }
         } else {
            var2 = true;
         }

         Logger.recoverMethod_w_e925fa09("AndroidUtil", "use " + (var2 ? " had current permission,no need to request" : " need to request permission"));
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "checkPermission error:" + var6.getMessage());
      }

      return var2;
   }

   public static boolean recoverMethod_c_f0554df7(Context var0) {
      if (null == recoverField_d) {
         recoverField_d = var0.getPackageName().equals(recoverMethod_d_e501eb0d(var0));
      }

      return recoverField_d;
   }

   public static String recoverMethod_d_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_e)) {
         return recoverField_e;
      }

      try {
         var0 = JPushConstants.getAppContext(var0);
         recoverField_e = RecoverType9b4c5aed.recoverMethod_a_e501eb0d(var0);
      } catch (Throwable var2) {
         Logger.recoverMethod_ww_e925fa09("AndroidUtil", "#unexcepted - getCurProcessName failed:" + var2.getMessage());
      }

      return recoverField_e;
   }

   public static boolean recoverMethod_a_9b79() {
      return false;
   }

   public static void recoverMethod_e_f0554df3(Context var0) {
      try {
         if (VERSION.SDK_INT >= 28) {
            String var1 = recoverMethod_d_e501eb0d(var0);
            String var2 = var0.getPackageName();
            if (var1 != null && var2 != null && !var2.equals(var1)) {
               WebView.setDataDirectorySuffix(var1);
               Logger.recoverMethod_d_e925fa09("AndroidUtil", "setDataDirectorySuffix =" + var1);
            }
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_e_e925fa09("AndroidUtil", "fixWebViewProcess e:" + var3);
      }
   }

   public static void recoverMethod_a_4221005a(WebSettings var0) {
      try {
         Logger.recoverMethod_d_e925fa09("AndroidUtil", "fix.");
         var0.setAllowFileAccess(false);
         Class var1 = Class.forName(RecoverType9b4c5ae8.recoverMethod_b_79834524(recoverField_f));
         Method var2 = var1.getMethod(RecoverType9b4c5ae8.recoverMethod_b_79834524(recoverField_g), boolean.class);
         var2.invoke(var0, true);
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "something is wrong:" + var3.getMessage());
      }
   }

   public static void recoverMethod_b_4221005a(WebSettings var0) {
      try {
         Class var1 = Class.forName(RecoverType9b4c5ae8.recoverMethod_b_79834524(recoverField_f));
         Method var2 = var1.getMethod(RecoverType9b4c5ae8.recoverMethod_b_79834524(recoverField_h), boolean.class);
         var2.invoke(var0, true);
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "something is wrong:" + var3.getMessage());
      }
   }

   public static void recoverMethod_a_60006d18(final WebView var0) {
      try {
         if (VERSION.SDK_INT >= 11) {
            var0.removeJavascriptInterface("searchBoxJavaBridge_");
            var0.removeJavascriptInterface("accessibility");
            var0.removeJavascriptInterface("accessibilityTraversal");
         }

         if (VERSION.SDK_INT >= 21) {
            var0.getSettings().setMixedContentMode(0);
         }
      } catch (Throwable var2) {
         Logger.recoverMethod_e_e925fa09("AndroidUtil", "fixSecure failed, error:" + var2);
         if (VERSION.SDK_INT >= 12) {
            var0.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
               @Override
               public void onViewAttachedToWindow(View var1) {
                  WebSettings var2x = var0.getSettings();
                  if (var2x != null) {
                     try {
                        Method var3 = var2x.getClass().getDeclaredMethod("removeJavascriptInterface", String.class);
                        if (var3 != null) {
                           var3.invoke(var0, "searchBoxJavaBridge_");
                           var3.invoke(var0, "accessibility");
                           var3.invoke(var0, "accessibilityTraversal");
                        }
                     } catch (Throwable var4) {
                        Logger.recoverMethod_e_e925fa09("AndroidUtil", "removeJavascriptInterface failed, error:" + var4);
                     }
                  }
               }

               @Override
               public void onViewDetachedFromWindow(View var1) {
               }
            });
         }
      }
   }

   public static void recoverMethod_c_4221005a(WebSettings var0) {
      var0.setAllowFileAccess(false);
      var0.setUseWideViewPort(true);
      var0.setLoadWithOverviewMode(true);
      var0.setDomStorageEnabled(true);
      recoverMethod_b_4221005a(var0);
      var0.setDefaultTextEncodingName("UTF-8");
      var0.setSupportZoom(true);
      var0.setBuiltInZoomControls(true);
      if (VERSION.SDK_INT >= 11) {
         var0.setDisplayZoomControls(false);
      }

      var0.setCacheMode(2);
      var0.setSaveFormData(false);
      var0.setSavePassword(false);
   }

   public static void recoverMethod_f_f0554df3(Context var0) {
      recoverMethod_b_5ffc00fd(var0, null);
   }

   public static Intent recoverMethod_g_813b6dc0(Context var0) {
      Intent var1 = null;

      try {
         PackageManager var2 = var0.getPackageManager();
         String var3 = var0.getApplicationContext().getPackageName();
         if (TextUtils.isEmpty(var3)) {
            Logger.recoverMethod_ww_e925fa09("AndroidUtil", "The package with the given name cannot be found!");
            return null;
         }

         var1 = var2.getLaunchIntentForPackage(var3);
         if (null == var1) {
            Logger.recoverMethod_ww_e925fa09("AndroidUtil", "Can't get launch intent for this package!");
            return null;
         }

         int var4 = 268435456;
         if (VERSION.SDK_INT >= 11) {
         }

         var1.addFlags(var4);
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "getLaunchIntent error:" + var5.getMessage());
      }

      return var1;
   }

   public static void recoverMethod_b_5ffc00fd(Context var0, String var1) {
      try {
         Intent var2 = recoverMethod_g_813b6dc0(var0);
         if (var2 != null) {
            if (!TextUtils.isEmpty(var1)) {
               var2.putExtra("extra", var1);
            }

            var0.startActivity(var2);
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_dd_e925fa09("AndroidUtil", "startMainActivity error:" + var3.getMessage());
      }
   }

   public static String recoverMethod_d_e96ea081(String var0) {
      return null != var0 && !"".equals(var0) ? recoverMethod_a_79834524(var0.getBytes()) : null;
   }

   public static String recoverMethod_a_79834524(byte[] var0) {
      if (var0 == null) {
         return null;
      }

      try {
         MessageDigest var1 = MessageDigest.getInstance(RecoverType9b4c5af0.recoverField_a);
         var1.update(var0);
         return recoverMethod_b_79834524(var1.digest());
      } catch (Throwable var2) {
         return null;
      }
   }

   public static String recoverMethod_b_79834524(byte[] var0) {
      if (var0 == null) {
         return "";
      }

      StringBuffer var1 = new StringBuffer(2 * var0.length);

      for (byte var5 : var0) {
         recoverMethod_a_31fc143d(var1, var5);
      }

      return var1.toString();
   }

   private static void recoverMethod_a_31fc143d(StringBuffer var0, byte var1) {
      var0.append("0123456789ABCDEF".charAt(var1 >> 4 & 15)).append("0123456789ABCDEF".charAt(var1 & 15));
   }

   public static boolean recoverMethod_c_5ffc0101(Context var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         return false;
      }

      PackageInfo var2 = JCoreHelper.getPackageInfo(var0, var1, 0);
      return var2 != null;
   }

   public static boolean recoverMethod_a_9d23c58b(Context var0, String var1, String var2) {
      if (recoverMethod_c_5ffc0101(var0, var1)) {
         return true;
      }

      Logger.recoverMethod_d_e925fa09("AndroidUtil", "p not:" + var1);

      try {
         return recoverMethod_b_9d23c58b(var0, var1, var2);
      } catch (Throwable var4) {
         return false;
      }
   }

   public static boolean recoverMethod_b_9d23c58b(Context var0, String var1, String var2) {
      try {
         if (TextUtils.isEmpty(var2)) {
            return false;
         }

         Logger.recoverMethod_d_e925fa09("AndroidUtil", "try d:" + var2);
         Intent var3 = new Intent("android.intent.action.VIEW", Uri.parse(var2));
         if (!TextUtils.isEmpty(var1)) {
            var3.setPackage(var1);
         }

         ResolveInfo var4 = var0.getPackageManager().resolveActivity(var3, 0);
         if (var4 != null) {
            return true;
         }
      } catch (Throwable var5) {
      }

      return false;
   }

   public static void recoverMethod_a_ee584a7a(Context var0, Intent var1, String var2) {
      if (var1 != null) {
         String var3 = var1.getAction();
         if ("cn.jpush.android.intent.NOTIFICATION_RECEIVED".equals(var3) || "cn.jpush.android.intent.NOTIFICATION_OPENED".equals(var3)) {
            List var4 = recoverMethod_b_80d4048d(var0, var1, var2);
            if (var4 != null && !var4.isEmpty()) {
               for (Object recoveryElement_543 : var4) {
                  String var6 = (String)recoveryElement_543;
                  try {
                     Intent var7 = (Intent)var1.clone();
                     var7.setComponent(new ComponentName(var0.getPackageName(), var6));
                     if (TextUtils.isEmpty(var2)) {
                        var0.sendBroadcast(var7);
                     } else {
                        var0.sendBroadcast(var7, var2);
                     }
                  } catch (Exception var8) {
                     Logger.recoverMethod_ww_e925fa09("AndroidUtil", "sendBroadcast failed again:" + var8.getMessage() + ", action:" + var1.getAction());
                  }
               }
            } else {
               Logger.recoverMethod_ww_e925fa09("AndroidUtil", "sendBroadcast failed again: receiver not found, action:" + var1.getAction());
            }
         }
      }
   }

   public static List recoverMethod_b_80d4048d(Context var0, Intent var1, String var2) {
      ArrayList var3 = new ArrayList();

      try {
         List var4 = var0.getPackageManager().queryBroadcastReceivers(var1, 0);
         PackageManager var5 = var0.getPackageManager();

         for (Object recoveryElement_570 : var4) {
            ResolveInfo var7 = (ResolveInfo)recoveryElement_570;
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

   public static boolean recoverMethod_h_f0554df7(Context var0) {
      Logger.recoverMethod_dd_e925fa09("AndroidUtil", "action:checkValidManifest");
      if (!recoverMethod_a_56054dae(var0, PushActivity.class)) {
         Logger.recoverMethod_ww_e925fa09("AndroidUtil", "AndroidManifest.xml missing required activity: " + PushActivity.class.getCanonicalName());
         Logger.recoverMethod_ww_e925fa09("AndroidUtil", "You will unable to use rich push type.");
      }

      if (!recoverMethod_a_56054dae(var0, PopWinActivity.class)) {
         Logger.recoverMethod_ww_e925fa09("AndroidUtil", "AndroidManifest.xml missing activity: " + PopWinActivity.class.getCanonicalName());
         Logger.recoverMethod_ww_e925fa09("AndroidUtil", "You will unable to use pop-window rich push type.");
      }

      if (!recoverMethod_d_5ffc0101(var0, "cn.jpush.android.ui.PushActivity")) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "AndroidManifest.xml missing required intent filter for PushActivity: cn.jpush.android.ui.PushActivity");
      }

      if (!recoverMethod_i_f0554df7(var0)) {
         return false;
      } else {
         String var1 = JPushConstants.getMessageReceiverClass(var0);
         if (TextUtils.isEmpty(var1)) {
            Logger.recoverMethod_ke_e925fa09(
               "AndroidUtil", "AndroidManifest.xml missing required receiver: please custom receiver extends JPushMessageReceiver. Otherwise no service"
            );
            return false;
         } else {
            return true;
         }
      }
   }

   public static boolean recoverMethod_i_f0554df7(Context var0) {
      try {
         if (null != var0) {
            Class<JNotifyActivity> var1 = JNotifyActivity.class;
            String var2 = var0.getPackageName();
            PackageManager var3 = var0.getPackageManager();
            Intent var4 = new Intent(var0, var1);
            ResolveInfo var5 = var3.resolveActivity(var4, 0);
            if (null == var5) {
               Logger.recoverMethod_ke_e925fa09(
                  "AndroidUtil", "AndroidManifest.xml missing required activity: " + var1.getCanonicalName() + ", Otherwise no service"
               );
               return false;
            }

            if (JCoreHelper.getJCoreSDKVersionInt() < 336 && JPushConstants.SDK_VERSION_CODE >= 483) {
               Logger.recoverMethod_ke_e925fa09("AndroidUtil", "JPush current version is " + JPushConstants.SDK_VERSION_CODE + "，must use JCore 336 or higher.");
            }

            int var6 = var5.activityInfo.theme;
            int var7 = var0.getResources().getIdentifier("JPushTheme", "style", var2);
            if (var7 != var6) {
               Logger.recoverMethod_ke_e925fa09(
                  "AndroidUtil",
                  "JNotifyActivity theme must use android:theme=\\\"@style/JPushTheme\\\">\\n\" ,and the JPushTheme name not confused keep it. Otherwise no service"
               );
               return false;
            }

            var4 = new Intent();
            var4.setAction("cn.jpush.android.intent.JNotifyActivity");
            var4.addCategory("android.intent.category.DEFAULT");
            var4.addCategory(var0.getPackageName());
            var5 = var3.resolveActivity(var4, 0);
            if (null == var5) {
               Logger.recoverMethod_ke_e925fa09(
                  "AndroidUtil",
                  "AndroidManifest.xml activity JNotifyActivity Action Must be <action android:name=\"cn.jpush.android.intent.JNotifyActivity\" />"
               );
               Logger.recoverMethod_ke_e925fa09(
                  "AndroidUtil", "AndroidManifest.xml activity JNotifyActivity Action Must be <category android:name=\"android.intent.category.DEFAULT\" />"
               );
               Logger.recoverMethod_ke_e925fa09(
                  "AndroidUtil", "AndroidManifest.xml activity JNotifyActivity Action Must be <category android:name=\"${applicationId}\" />\n"
               );
               Logger.recoverMethod_ke_e925fa09("AndroidUtil", "AndroidManifest.xml activity JNotifyActivity not set <data>. Otherwise no service");
               return false;
            }

            return true;
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_kd_e925fa09("AndroidUtil", "checkJNotifyActivity error:" + var8.getMessage());
      }

      return false;
   }

   public static boolean recoverMethod_a_56054dae(Context var0, Class var1) {
      try {
         if (null != var0) {
            PackageManager var2 = var0.getPackageManager();
            Intent var3 = new Intent(var0, var1);
            ResolveInfo var4 = var2.resolveActivity(var3, 0);
            boolean var5 = var4 != null;
            if (!var5) {
               var5 = recoverMethod_a_e3a6cbf6(var0, var0.getPackageName(), var1) != null;
            }

            return var5;
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_dd_e925fa09("AndroidUtil", "hasActivityResolves error:" + var6.getMessage());
      }

      return false;
   }

   public static boolean recoverMethod_d_5ffc0101(Context var0, String var1) {
      try {
         PackageManager var2 = var0.getPackageManager();
         Intent var3 = new Intent(var1);
         var3.addCategory(var0.getPackageName());
         ResolveInfo var4 = var2.resolveActivity(var3, 0);
         if (var4 == null) {
            return false;
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_dd_e925fa09("AndroidUtil", "hasActivityIntentFilter error:" + var5.getMessage());
      }

      return true;
   }

   public static boolean recoverMethod_j_f0554df7(Context var0) {
      return JPushConstants.isForeGround == 0;
   }

   public static int recoverMethod_a_e925f9fc(String var0, String var1) {
      if (var0.equals(var1)) {
         return 0;
      }

      try {
         String[] var2 = var0.split("[._]");
         String[] var3 = var1.split("[._]");
         if (var2.length > var3.length) {
            var3 = (String[])Arrays.copyOf(var3, var2.length);
         } else if (var2.length < var3.length) {
            var2 = (String[])Arrays.copyOf(var2, var3.length);
         }

         for (int var10 = 0; var10 < var2.length; var10++) {
            long var6;
            if (TextUtils.isEmpty(var2[var10])) {
               var6 = 0L;
            } else {
               var6 = Long.parseLong(var2[var10]);
            }

            long var8;
            if (TextUtils.isEmpty(var3[var10])) {
               var8 = 0L;
            } else {
               var8 = Long.parseLong(var3[var10]);
            }

            long var4 = var6 - var8;
            if (var4 > 0L) {
               return 1;
            }

            if (var4 < 0L) {
               return -1;
            }
         }

         return 0;
      } catch (Throwable var11) {
         return var0.compareTo(var1);
      }
   }

   public static String recoverMethod_e_a4ea6d43(Context var0, String var1) {
      return var0.getPackageName() + "." + var1;
   }

   public static Activity recoverMethod_k_2b1cd295(Context var0) {
      try {
         Activity var1 = RecoverType9b4c5ae4.recoverMethod_a_ad5c6a53();
         if (null != var1) {
            return var1;
         }

         if (null != var0) {
            Class var2 = Class.forName("android.app.ActivityThread");
            Object var3 = var2.getMethod("currentActivityThread").invoke(null);
            Field var4 = var2.getDeclaredField("mActivities");
            var4.setAccessible(true);
            Map var5 = (Map)var4.get(var3);
            if (var5 == null) {
               Logger.recoverMethod_w_e925fa09("AndroidUtil", "get current activities for currentActivityThread, activities is null");
               return null;
            }

            for (Object var7 : var5.values()) {
               Class var8 = var7.getClass();
               Field var9 = var8.getDeclaredField("paused");
               var9.setAccessible(true);
               if (!var9.getBoolean(var7)) {
                  Field var10 = var8.getDeclaredField("activity");
                  var10.setAccessible(true);
                  Activity var11 = (Activity)var10.get(var7);
                  if (var11 == null) {
                     Logger.recoverMethod_w_e925fa09("AndroidUtil", "[getCurrentActivity] activity is null");
                     return null;
                  }

                  if (!var11.getPackageName().equals(var0.getPackageName())) {
                     Logger.recoverMethod_w_e925fa09(
                        "AndroidUtil", "current activity packageName: " + var11.getPackageName() + ", appPackageName: " + var0.getPackageName()
                     );
                     return null;
                  }

                  Logger.recoverMethod_d_e925fa09("AndroidUtil", "getCurrentActivity: " + var11.getLocalClassName());
                  return var11;
               }
            }
         }
      } catch (Throwable var12) {
         Logger.recoverMethod_w_e925fa09("AndroidUtil", "[getCurrentActivity] error: " + var12.getMessage());
      }

      return null;
   }

   public static Intent recoverMethod_a_2e1cfbaf(String var0, int var1) throws java.net.URISyntaxException {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      Intent var2 = Intent.parseUri(var0, var1);
      Intent var3 = new Intent(var2);
      var2.addCategory("android.intent.category.BROWSABLE");
      var2.setComponent(null);
      if (VERSION.SDK_INT >= 15) {
         var2.setSelector(null);
      }

      return var3;
   }

   public static boolean recoverMethod_b_9b79() {
      try {
         String var0 = recoverMethod_d_73cf11cb();
         Logger.recoverMethod_d_e925fa09("AndroidUtil", "vivoVersion:" + var0);
         if (Build.MANUFACTURER.equalsIgnoreCase("vivo") || var0.startsWith("Funtouch")) {
            return true;
         }

         String var1 = recoverMethod_e_e96ea081("ro.iqoo.os.build.display.id");
         Logger.recoverMethod_d_e925fa09("AndroidUtil", "iqooMonsterUI:" + var1);
         if (var1 != null && !TextUtils.isEmpty(var1.trim())) {
            return true;
         }
      } catch (Throwable var2) {
      }

      return false;
   }

   private static String recoverMethod_d_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_i)) {
         return recoverField_i;
      }

      recoverField_i = recoverMethod_e_e96ea081("ro.vivo.os.build.display.id");
      return recoverField_i;
   }

   public static String recoverMethod_e_e96ea081(String var0) {
      return recoverMethod_b_8ea82db7(var0, "");
   }

   public static String recoverMethod_b_8ea82db7(String var0, String var1) {
      try {
         if (recoverField_j == null) {
            recoverField_j = Class.forName("android.os.SystemProperties");
         }

         if (recoverField_k == null) {
            recoverField_k = recoverField_j.getMethod("get", String.class, String.class);
         }

         return (String)recoverField_k.invoke(null, var0, var1);
      } catch (Throwable var3) {
         return var1;
      }
   }

   public static void recoverMethod_l_f0554df3(Context var0) {
      if (!(var0 instanceof Activity)) {
         Logger.recoverMethod_ww_e925fa09("AndroidUtil", "[requestRequiredPermission] context must instanceof Activity");
      } else {
         try {
            LinkedList var1 = new LinkedList();
            if (VERSION.SDK_INT >= 33) {
               var1.add(recoverField_l);
               NotificationManager var2 = (NotificationManager)var0.getSystemService("notification");
               boolean var3 = var2.areNotificationsEnabled();
               if (!var3) {
                  Logger.recoverMethod_ww_e925fa09("AndroidUtil", "request " + recoverField_l + "，need go to settings and turn on app notification switch");
               }
            } else {
               Logger.recoverMethod_d_e925fa09("AndroidUtil", " Build.VERSION.SDK_INT:" + VERSION.SDK_INT + " < 33, not need to request POST_NOTIFICATIONS");
            }

            List var6 = recoverMethod_a_c98d58e3(var0, var1);
            if (var6 == null || var6.isEmpty()) {
               return;
            }

            Logger.recoverMethod_v_e925fa09("AndroidUtil", "lackPermissions:" + var6);
            Class var7 = Class.forName("android.app.Activity");
            Method var4 = var7.getDeclaredMethod("requestPermissions", String[].class, int.class);
            var4.invoke(var0, var6.toArray(new String[var6.size()]), 1);
         } catch (Exception var5) {
            Logger.recoverMethod_ww_e925fa09("AndroidUtil", "#unexcepted - requestRequiredPermission e:" + var5);
         }
      }
   }

   private static List recoverMethod_a_c98d58e3(Context var0, List var1) {
      if (var1 != null && var1.size() != 0) {
         ArrayList var2 = new ArrayList();

         for (Object recoveryElement_919 : var1) {
            String var4 = (String)recoveryElement_919;
            if (!recoverMethod_a_5ffc0101(var0, var4)) {
               var2.add(var4);
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   public static String recoverMethod_c_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_m)) {
         return recoverField_m;
      }

      try {
         recoverField_m = Build.MANUFACTURER;
      } catch (Throwable var1) {
      }

      return recoverField_m;
   }

   public static Bundle recoverMethod_a_2fcac876(Intent var0) {
      Bundle var1 = new Bundle();

      try {
         if (var0 == null) {
            return var1;
         }

         String var2 = var0.getAction();
         if (!TextUtils.isEmpty(var2)) {
            var1.putString("intent_action", var2);
         }

         Uri var3 = var0.getData();
         if (var3 != null) {
            var1.putParcelable("intent_data", var3);
         }

         Set var4 = var0.getCategories();
         if (var4 != null && var4.size() > 0) {
            var1.putStringArray("intent_categories", (String[])var4.toArray(new String[0]));
         }

         ComponentName var5 = var0.getComponent();
         if (var5 != null) {
            var1.putParcelable("intent_component", var5);
         }

         Bundle var6 = var0.getExtras();
         if (var6 != null) {
            var1.putAll(var6);
         }

         var1.putInt("intent_flags", var0.getFlags());
         String var7 = var0.getPackage();
         if (!TextUtils.isEmpty(var7)) {
            var1.putString("intent_package", var7);
         }

         String var8 = var0.getType();
         if (!TextUtils.isEmpty(var8)) {
            var1.putString("intent_type", var8);
         }
      } catch (Throwable var9) {
      }

      return var1;
   }

   public static Intent recoverMethod_a_ee49a2f4(Bundle var0) {
      Intent var1 = new Intent();

      try {
         if (var0 == null) {
            return var1;
         }

         String var2 = var0.getString("intent_action");
         if (!TextUtils.isEmpty(var2)) {
            var1.setAction(var2);
         }

         Uri var3 = (Uri)var0.getParcelable("intent_data");
         if (var3 != null) {
            var1.setData(var3);
         }

         String[] var4 = var0.getStringArray("intent_categories");
         if (var4 != null && var4.length > 0) {
            for (String var8 : var4) {
               var1.addCategory(var8);
            }
         }

         Parcelable var10 = var0.getParcelable("intent_component");
         if (var10 != null) {
            var1.setComponent((ComponentName)var10);
         }

         var1.putExtras(var0);
         var1.setFlags(var0.getInt("intent_flags"));
         String var11 = var0.getString("intent_package");
         if (!TextUtils.isEmpty(var11)) {
            var1.setPackage(var11);
         }

         String var12 = var0.getString("intent_type");
         if (!TextUtils.isEmpty(var12)) {
            var1.setType(var12);
         }
      } catch (Throwable var9) {
      }

      return var1;
   }

   static {
      recoverField_a.add("image/png");
      recoverField_a.add("image/jpg");
      recoverField_a.add("image/jpeg");
      recoverField_a.add("image/webp");
   }
}
