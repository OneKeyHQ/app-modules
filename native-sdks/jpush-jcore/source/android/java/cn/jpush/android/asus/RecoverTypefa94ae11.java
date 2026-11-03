package cn.jpush.android.asus;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.ac.RecoverType9b4c5727;
import cn.jpush.android.c.RecoverType2e4cca67;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.e.RecoverType2e4cd1e9;
import cn.jpush.android.e.RecoverType2e4cd1ea;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import java.lang.reflect.Field;
import org.json.JSONObject;

public class RecoverTypefa94ae11 {
   private static boolean recoverField_c;
   private static boolean recoverField_d;
   public static final String recoverField_a = "asus".toLowerCase();
   public static String recoverField_b = "";
   private static String recoverField_e = null;

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      recoverMethod_b_f0554df3(var0);
      return recoverField_c;
   }

   public static synchronized void recoverMethod_b_f0554df3(Context var0) {
      if (!recoverField_d) {
         if (var0 == null) {
            Logger.recoverMethod_ww_e925fa09("AsusPushHelper", "context is null");
         } else {
            if (recoverMethod_a_9b79() && recoverMethod_i_f0554df7(var0) && RecoverType2e4cca67.recoverMethod_a_f0554df7(var0)) {
               recoverField_c = true;
            }

            Logger.recoverMethod_d_e925fa09("AsusPushHelper", (recoverField_c ? "support " : "not support ") + recoverField_a);
            recoverField_d = true;
         }
      }
   }

   private static boolean recoverMethod_a_9b79() {
      boolean var0 = "asus".equalsIgnoreCase(Build.BRAND);
      if (!var0) {
         var0 = "asus".equalsIgnoreCase(Build.MANUFACTURER);
      }

      return var0;
   }

   public static void recoverMethod_c_f0554df3(final Context var0) {
      try {
         recoverMethod_b_f0554df3(var0);
         if (!recoverField_c) {
            return;
         }

         RecoverType2e4cca67.recoverMethod_a_d32e5282(var0, recoverMethod_e_e501eb0d(var0), new RecoverType2e4cd1ea() {
            public void recoverMethod_a_75a2ce9b(RecoverType2e4cd1e9 var1) {
               try {
                  Logger.recoverMethod_dd_e925fa09("AsusPushHelper", "tokenResult:" + var1);
                  RecoverTypefa94ae11.recoverMethod_a_1a54e370(var0, var1.recoverMethod_a_9b68());
                  String var2 = var1.recoverMethod_b_73cf11cb();
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_e7235ab3(var0, (byte)6, var2);
               } catch (Throwable var3) {
               }
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public void recoverMethod_a_5d527811(Object var1) {
               this.recoverMethod_a_75a2ce9b((RecoverType2e4cd1e9)var1);
            }
         });
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("AsusPushHelper", "register error:" + var2.getMessage());
      }
   }

   public static byte recoverMethod_d_f0554ddf(Context var0) {
      return 6;
   }

   public static String recoverMethod_e_e501eb0d(Context var0) {
      return !TextUtils.isEmpty(recoverField_b) ? recoverField_b : JCoreHelper.getAppKey(var0);
   }

   public static String recoverMethod_f_e501eb0d(Context var0) {
      return "";
   }

   private static boolean recoverMethod_i_f0554df7(Context var0) {
      ActivityInfo var1 = recoverMethod_a_10170118(var0, var0.getPackageName(), AsusPushMessageReceiver.class);
      if (var1 != null) {
         Logger.recoverMethod_d_e925fa09("AsusPushHelper", "found receiver:" + var1.name);
         return true;
      } else {
         Logger.recoverMethod_ww_e925fa09("AsusPushHelper", "AndroidManifest.xml missing receiver extends asus's AsusPushMessageReceiver");
         return false;
      }
   }

   private static ActivityInfo recoverMethod_a_10170118(Context var0, String var1, Class var2) {
      if (var0 != null && !TextUtils.isEmpty(var1) && var2 != null) {
         try {
            PackageInfo var3 = JCoreHelper.getPackageInfo(var0, var1, 2);
            if (var3 == null || var3.receivers == null || var3.receivers.length == 0) {
               Logger.recoverMethod_d_e925fa09("AsusPushHelper", "pkgInfo info is null");
               return null;
            }

            for (ActivityInfo var7 : var3.receivers) {
               try {
                  if (var2.isAssignableFrom(Class.forName(var7.name, false, var2.getClassLoader())) && var7.enabled) {
                     return var7;
                  }
               } catch (Throwable var9) {
               }
            }
         } catch (Throwable var10) {
            Logger.recoverMethod_ww_e925fa09("AsusPushHelper", "getReceiver error:" + var10);
         }

         return null;
      } else {
         Logger.recoverMethod_w_e925fa09("AsusPushHelper", "Action - hasComponent, invalid param, context:" + var0 + ",packageName:" + var1 + ",cls:" + var2);
         return null;
      }
   }

   public static String recoverMethod_g_e501eb0d(Context var0) {
      return RecoverType2e4cca67.recoverMethod_d_e501eb0d(var0);
   }

   public static boolean recoverMethod_h_f0554df7(Context var0) {
      String var1 = recoverMethod_e_e501eb0d(var0);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ii_e925fa09("AsusPushHelper", recoverField_a + " appkey or appid is empty,need not clear plugin rid");
         return false;
      } else {
         String var2 = JCoreHelper.getAppKey(var0);
         if (TextUtils.isEmpty(var2)) {
            Logger.recoverMethod_ii_e925fa09("AsusPushHelper", "jpush appkey is empty,need not clear plugin rid");
            return false;
         } else {
            String var3 = (String)RecoverTypea0793a19.get(var0, Key.ThirdPush_ClearFlag());
            String var4 = RecoverTypefa94ae12.toHash(var1 + var2 + JCoreHelper.getJCoreSDKVersionInt() + JCoreHelper.getJPushSDKVersionInt());
            if (TextUtils.isEmpty(var3)) {
               RecoverTypea0793a19.set(var0, Key.ThirdPush_ClearFlag().set(var4));
               return true;
            } else if (TextUtils.isEmpty(var4)) {
               return true;
            } else if (!TextUtils.equals(var3, var4)) {
               Logger.recoverMethod_d_e925fa09("AsusPushHelper", "local set changed,re bind token");
               RecoverTypea0793a19.set(var0, Key.ThirdPush_ClearFlag().set(var4));
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      try {
         Bundle var2 = new Bundle();
         var2.putString("code", var1 + "");
         JSONObject var3 = new JSONObject();
         var3.put("rom_type", String.valueOf(6));
         var3.put("plugin_sdk_ver", JPushConstants.SDK_VERSION_NAME);
         var3.put("vendor_sdk_ver", recoverMethod_b_73cf11cb());
         var2.putString("extra", var3.toString());
         RecoverTypefa94ae12.doAction(var0, "action_send_exception", var2);
      } catch (Throwable var4) {
      }
   }

   private static String recoverMethod_b_73cf11cb() {
      try {
         Class var0 = Class.forName("cn.jpush.android.f.e");
         Field var1 = var0.getDeclaredField("SDK_VERSION");
         var1.setAccessible(true);
         return (String)var1.get(var0);
      } catch (Throwable var3) {
         return "";
      }
   }
}
