package cn.jpush.android.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.internal.JConstants;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.m.RecoverType2e4ceff0;
import org.json.JSONObject;

public class JCoreHelper {
   private static Integer recoverField_a;

   public static void runActionWithService(Context var0, String var1, String var2, Bundle var3) {
      JCoreManager.onEvent(var0, var1, 3, var2, var3);
   }

   public static String getRegistrationID(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, "JPUSH", 4, null, null);
      return var1 instanceof String ? (String)var1 : "";
   }

   public static void setChannel(Context var0, String var1) {
      JCoreManager.onEvent(var0, "JPUSH", 9, var1, null);
   }

   public static String getDeviceId(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 8, null, null);
      return var1 instanceof String ? (String)var1 : "";
   }

   public static void openLog(Context var0, Bundle var1) {
      JCoreManager.onEvent(var0, "JPUSH", 105, null, var1);
   }

   public static void sendData(Context var0, String var1, int var2, int var3, long var4, byte[] var6) {
      recoverMethod_a_a6216270(var0, var1, var2, var3, var4, 0L, var6, false);
   }

   public static void sendRequest(Context var0, String var1, int var2, int var3, long var4, long var6, byte[] var8) {
      recoverMethod_a_a6216270(var0, var1, var2, var3, var4, var6, var8, true);
   }

   private static void recoverMethod_a_a6216270(Context var0, String var1, int var2, int var3, long var4, long var6, byte[] var8, boolean var9) {
      Bundle var10 = new Bundle();
      var10.putInt("cmd", var2);
      var10.putInt("ver", var3);
      var10.putLong("rid", var4);
      var10.putLong("timeout", var6);
      var10.putByteArray("body", var8);
      JCoreManager.onEvent(var0, var1, var9 ? 17 : 16, null, var10);
   }

   public static void netMonitor(Context var0, String var1, String var2, long var3, int var5) {
      Bundle var6 = new Bundle();
      var6.putString("url", var1);
      var6.putString("type", var2);
      var6.putLong("req", var3);
      var6.putLong("res", var5);
      JCoreManager.onEvent(var0, "JPUSH", 122, null, var6);
   }

   public static boolean isContinueBusiness(Context var0) {
      if (RecoverType2e4ceff0.recoverField_c < 470) {
         return true;
      }

      Object var1 = JCoreManager.onEvent(var0, null, 123, null, null);
      return var1 instanceof Boolean ? (Boolean)var1 : false;
   }

   public static String getAppKey(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 7, null, null);
      return var1 instanceof String ? (String)var1 : "";
   }

   public static boolean isAuth(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 98, null, null);
      return var1 instanceof Boolean ? (Boolean)var1 : true;
   }

   public static int getJCoreSDKVersionInt() {
      if (recoverField_a != null) {
         return recoverField_a;
      } else {
         Object var0 = JCoreManager.onEvent(null, null, 25, null, null);
         if (var0 instanceof Integer) {
            recoverField_a = (Integer)var0;
            return recoverField_a;
         } else {
            return 0;
         }
      }
   }

   public static boolean geoEnable(Context var0) {
      try {
         if (JConstants.SDK_VERSION_INT < 490) {
            return RecoverTypea2146d45.recoverMethod_s_f0554df7(var0)
               || RecoverTypea2146d45.recoverMethod_o_f0554df7(var0)
               || RecoverTypea2146d45.recoverMethod_q_f0554df7(var0);
         }

         Bundle var1 = new Bundle();
         var1.putInt("configID", 1502);
         var1.putBoolean("isFromJCore", true);
         Object var2 = JCoreManager.onEvent(null, null, 116, null, var1);
         if (var2 instanceof Boolean) {
            return (Boolean)var2;
         }
      } catch (Throwable var3) {
      }

      return true;
   }

   public static int getJPushSDKVersionInt() {
      return RecoverType2e4ceff0.recoverField_a;
   }

   public static JSONObject fillBase(Context var0, JSONObject var1, String var2) {
      Object var3 = JCoreManager.onEvent(var0, "JPUSH", 26, "", null, var1, var2);
      return var3 instanceof JSONObject ? (JSONObject)var3 : null;
   }

   public static void saEvent(Context var0, String var1, Object var2) {
      JCoreManager.onEvent(var0, "JPUSH", 56, var1, null, var2);
   }

   public static void triggerSceneCheck(Context var0) {
      JCoreManager.onEvent(var0, "JPUSH", 29, null, null, 3);
   }

   public static void updateDy(Context var0, int var1) {
      JCoreManager.onEvent(var0, "JPUSH", 65, null, null, var1);
   }

   public static Object getDyInfo(Context var0, int var1) {
      return JCoreManager.onEvent(var0, "JPUSH", 64, null, null, var1);
   }

   public static void execue(Context var0, String var1, Runnable var2) {
      JCoreManager.onEvent(var0, "JPUSH", 11, var1, null, var2);
   }

   public static void majorExecutor(Context var0, String var1, Runnable var2) {
      if (RecoverType2e4ceff0.recoverField_c >= 238) {
         JCoreManager.onEvent(var0, "JPUSH", 75, null, null, var2);
      } else {
         execue(var0, var1, var2);
      }
   }

   public static void futureExecutor(Context var0, String var1, Runnable var2) {
      if (RecoverType2e4ceff0.recoverField_c >= 238) {
         JCoreManager.onEvent(var0, "JPUSH", 76, null, null, var2);
      } else {
         execue(var0, var1, var2);
      }
   }

   public static void futureExecutor(Context var0, Runnable var1) {
      futureExecutor(var0, "FUTURE", var1);
   }

   public static void normalExecutor(Context var0, String var1, Runnable var2) {
      if (RecoverType2e4ceff0.recoverField_c >= 238) {
         JCoreManager.onEvent(var0, "JPUSH", 77, null, null, var2);
      } else {
         execue(var0, var1, var2);
      }
   }

   public static void reportSdkType(Context var0, String var1, int var2, int var3, int var4) {
      JCoreManager.onEvent(var0, "JCore", 72, true, null, null, var1, var2, var3, var4);
   }

   public static Boolean checkSdkBanned(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, "JPUSH", 82, null, null);
      return var1 instanceof Boolean ? (Boolean)var1 : false;
   }

   public static long getUid(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 20, null, null, (Object[])null);
      return var1 instanceof Long ? (Long)var1 : 0L;
   }

   public static PackageInfo getPackageInfo(Context var0, int var1) {
      return null != var0 ? getPackageInfo(var0, var0.getPackageName(), var1) : null;
   }

   public static PackageInfo getPackageInfo(Context var0, String var1, int var2) {
      PackageInfo var3 = null;

      try {
         if (var0 == null || TextUtils.isEmpty(var1) || var2 < 0) {
            Logger.recoverMethod_d_e925fa09("JCoreHelper", "get p info failed, because param is illegal");
            return var3;
         }

         if (JConstants.SDK_VERSION_INT >= 332) {
            Object var4 = JCoreManager.onEvent(var0, "JPUSH", 99, null, null, var1, var2);
            if (var4 instanceof PackageInfo) {
               var3 = (PackageInfo)var4;
            }
         } else {
            var3 = var0.getPackageManager().getPackageInfo(var1, var2);
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("JCoreHelper", "get p info failed, error: " + var5.getMessage());
      }

      return var3;
   }

   public static boolean isIpv6Priority(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, "JPUSH", 121, null, null);
      return var1 instanceof Boolean ? (Boolean)var1 : false;
   }

   public static void initSdkType(Context var0) {
      if (JConstants.SDK_VERSION_INT >= 520) {
         Bundle var1 = new Bundle();
         var1.putString("jg_init_sdk_type", "JPUSH");
         JCoreManager.onEvent(var0, "JPUSH", 132, null, var1);
         JCoreManager.init(var0, var1);
      } else {
         JCoreManager.init(var0);
      }
   }

   public static void registerSdkType(Context var0, String var1) {
      if (JConstants.SDK_VERSION_INT >= 520) {
         Bundle var2 = new Bundle();
         var2.putString("jg_init_sdk_type", "JPUSH");
         JCoreManager.register(var0, var1, var2);
      } else {
         JCoreManager.register(var0, var1);
      }
   }
}
