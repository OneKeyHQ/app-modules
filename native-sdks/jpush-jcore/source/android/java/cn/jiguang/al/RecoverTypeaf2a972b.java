package cn.jiguang.al;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa267;
import cn.jiguang.ao.RecoverTypeaf2aa26c;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JConstants;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public class RecoverTypeaf2a972b {
   private static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         121,
         68,
         84,
         105,
         123,
         44,
         62,
         31,
         67,
         118,
         102,
         112,
         120,
         87,
         14,
         115,
         120,
         99,
         98,
         88,
         14,
         122,
         102,
         57,
         103,
         1,
         15,
         111,
         109,
         100,
         98,
         89,
         79,
         119,
         37,
         117,
         121,
         85,
         67,
         114
      }
   );
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         126,
         101,
         68,
         80,
         106,
         50,
         57,
         62,
         83,
         79,
         119,
         110,
         127,
         118,
         29,
         73,
         105,
         126,
         32,
         63,
         90,
         80,
         108,
         123,
         126,
         63,
         83,
         78,
         54,
         126,
         39,
         62,
         70,
         69,
         107,
         123,
         127,
         126,
         94,
         13,
         122,
         96,
         115,
         114,
         91
      }
   );
   private static AtomicBoolean recoverField_c = new AtomicBoolean(false);
   private static long recoverField_d = 0L;
   private static Boolean recoverField_e = null;
   private static LinkedList recoverField_f = new LinkedList();

   public static boolean recoverMethod_a_9b79() {
      try {
         Context var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (var0 == null || !RecoverTypeaf2b07c2.recoverMethod_a_9b79() || !RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_f0554df7(var0)) {
            return false;
         }

         String var1 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         if (TextUtils.isEmpty(var1)) {
            return recoverField_c.get();
         }

         if (RecoverType5a681cd.recoverField_c == JConstants.SDK_VERSION_INT && null == recoverField_e) {
            if (ActionManager.wrapSdkTypeVersionInfo(new JSONObject())) {
               if (null != var0) {
                  return recoverMethod_a_f0554df7(var0);
               }
            } else {
               recoverField_e = true;
            }
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "isSDKLimit error:" + var2);
      }

      return recoverField_c.get();
   }

   public static int recoverMethod_b_9b68() {
      byte var0 = 0;
      if (!TextUtils.isEmpty(RecoverType5a681cd.recoverField_d)) {
         boolean var1 = true;
         var0 = 4;
      } else {
         var0 = 2;
      }

      return var0;
   }

   public static boolean recoverMethod_a_134632(int var0) {
      return var0 == 1 || var0 == 4 || var0 == 5 || var0 == 3;
   }

   private static void recoverMethod_a_304fbba3(Context var0, boolean var1, long var2) {
      recoverMethod_b_304fbba3(var0, var1, var2);
      recoverField_d = var2;
      if (recoverField_c.get() != var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "sdk limit status changed, update status, sdkLimit: " + var1);
         if (!var1) {
            recoverField_c = new AtomicBoolean(var1);
         }

         Bundle var4 = new Bundle();
         var4.putBoolean("sdk_banned", var1);
         JCoreManager.onEvent(var0, "", 81, null, var4);
      }

      if (var1) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JSDKDeveloperIdLimitHelper", "[key-step]You use the special sdk type is " + recoverMethod_b_9b68());
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
            "JSDKDeveloperIdLimitHelper", "[key-step]You use the special sdk, but it is limited now. If you want to enable it, please contact us."
         );
      }

      recoverField_c = new AtomicBoolean(var1);
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JSDKDeveloperIdLimitHelper", "sdk limit status, limit: " + var1 + ", nextTime: " + recoverField_d);
   }

   public static synchronized boolean recoverMethod_a_f0554df7(Context var0) {
      if (null != recoverField_e) {
         return recoverField_c.get();
      }

      recoverField_e = true;
      recoverMethod_c_f0554df7(var0);
      if (System.currentTimeMillis() > recoverField_d) {
         RecoverTypeaf2b0403.recoverMethod_a_135d7bee("FUTURE_TASK", new RecoverTypeaf2a972b.Member8ee17d28(var0), 0, false);
      } else {
         if (recoverField_c.get()) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JSDKDeveloperIdLimitHelper", "[key-step]You use the special sdk type is " + recoverMethod_b_9b68());
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
               "JSDKDeveloperIdLimitHelper", "[key-step]You use the special sdk, but it is limited now. If you want to enable it, please contact us."
            );
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "request sdk limit config not time up, not request");
      }

      return recoverField_c.get();
   }

   private static void recoverMethod_b_3c4029ab(Context var0, JSONObject var1) {
      try {
         if (null == var1) {
            return;
         }

         boolean var2 = var1.optInt("code") == 1;
         long var3 = var1.optLong("nexttime", 43200L);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "from server sdk limit status: " + var2 + ", next request time: " + var3);
         var3 = var3 <= 0L ? 0L : var3;
         var3 = var3 >= 86400L ? 86400L : var3;
         var3 = System.currentTimeMillis() + var3 * 1000L;
         recoverMethod_a_304fbba3(var0, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "parse sdk limit config failed, " + var5.getMessage());
      }
   }

   private static void recoverMethod_b_304fbba3(Context var0, boolean var1, long var2) {
      try {
         String var4 = var1 + "," + var2;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "save sdk limit status: " + var4);
         SharedPreferences var5 = var0.getSharedPreferences("cn.jiguang.prefs", 0);
         var5.edit().putString("sdk_limit_status", var4).apply();
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "[saveSdkLimitStatus] parse json error, " + var6.getMessage());
      }
   }

   private static boolean recoverMethod_c_f0554df7(Context var0) {
      try {
         SharedPreferences var1 = var0.getSharedPreferences("cn.jiguang.prefs", 0);
         String var2 = var1.getString("sdk_limit_status", "");
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JSDKDeveloperIdLimitHelper", "local sdk limit config: " + var2);
         if (!TextUtils.isEmpty(var2)) {
            String[] var3 = var2.split(",");
            boolean var4 = Boolean.parseBoolean(var3[0]);
            long var5 = Long.parseLong(var3[1]);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "local limit config, isLimit: " + var4 + ", nextReqTime: " + var5);
            recoverField_d = var5;
            recoverField_c = new AtomicBoolean(var4);
            return true;
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "read config json from cache failed !! error:" + var7);
      }

      return false;
   }

   public static Integer recoverMethod_c_bd10275c() {
      return null;
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "updateImmediately failed: context is null");
      } else {
         boolean var1 = recoverField_e != null || System.currentTimeMillis() <= recoverField_d;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JSDKDeveloperIdLimitHelper",
            "updateImmediately: needReset=" + var1 + ", gIsConfigRequested=" + recoverField_e + ", gNextReqTime=" + recoverField_d
         );
         if (var1) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "Reset request status and continue update");
            recoverField_e = null;
            recoverField_d = 0L;
            recoverMethod_a_304fbba3(var0, recoverField_c.get(), recoverField_d);
         }

         recoverMethod_a_9b79();
      }
   }

   static {
      recoverField_f.clear();
      recoverField_f.add(recoverField_a);
      recoverField_f.add(recoverField_b);
   }

   private static class Member8ee17d28 extends RecoverTypeaf2b0401 {
      private Context recoverField_a;

      Member8ee17d28(Context var1) {
         this.recoverField_a = var1;
         this.recoverField_h = "JLimitConfig";
      }

      @Override
      public void recoverMethod_a_9b75() {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "start request sdk limit config");
         int var1 = this.recoverMethod_a_f0554de6(this.recoverField_a);
         if (var1 != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "request limit config failed, code: " + var1);
         }
      }

      private int recoverMethod_a_f0554de6(Context var1) {
         try {
            try {
               String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var1);
               if (TextUtils.isEmpty(var2)) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "request bannedConfig failed because can't get appKey");
                  return -1;
               }

               RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(var1, RecoverTypeaf2a972b.recoverField_f);

               for (Object recoveryElement_324 : RecoverTypeaf2a972b.recoverField_f) {
                  String var4 = (String)recoveryElement_324;
                  JSONObject var5 = new JSONObject();
                  ActionManager.getInstance();
                  if (!ActionManager.wrapSdkTypeVersionInfo(var5)) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "request bannedConfig wrapSdkTypeVersionInfo is false");
                     return -1;
                  }

                  if (var5 != null && var5.length() > 0) {
                     var5.put("appkey", var2);
                     var5.put("platform", "android");
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "request url: " + var4 + ", param json:" + var5.toString());
                  if (TextUtils.isEmpty(var4)) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKDeveloperIdLimitHelper", "can't get url, give up upload");
                     return -1;
                  }

                  RecoverTypeaf2aa26c var6 = RecoverTypeaf2aa267.recoverMethod_a_ab3ea53a(var4, var5.toString(), var1, false, 3, 3);
                  int var7 = var6.recoverMethod_a_9b68();
                  String var8 = var6.recoverMethod_b_73cf11cb();
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "code: " + var7 + ", responseBody:" + var8);
                  switch (var6.recoverMethod_a_9b68()) {
                     case -3:
                        return -2;
                     case -2:
                     default:
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKDeveloperIdLimitHelper", "upload" + var5.toString() + " failed");
                        break;
                     case -1:
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                           "JSDKDeveloperIdLimitHelper", "upload" + var5.toString() + " error:" + var6.recoverMethod_b_73cf11cb()
                        );
                        break;
                     case 0:
                        if (!TextUtils.isEmpty(var8)) {
                           RecoverTypeaf2a972b.recoverMethod_b_3c4029ab(var1, new JSONObject(var8));
                        }

                        return 0;
                     case 3006:
                        RecoverTypeaf2a972b.recoverField_e = null;
                  }
               }
            } catch (Throwable var13) {
            }

            return -1;
         } finally {
            ;
         }
      }
   }
}
