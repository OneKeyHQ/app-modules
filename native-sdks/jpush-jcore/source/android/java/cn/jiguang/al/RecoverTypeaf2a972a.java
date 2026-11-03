package cn.jiguang.al;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public class RecoverTypeaf2a972a {
   private static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         126, 101, 68, 80, 106, 50, 57, 62, 83, 79, 119, 110, 127, 118, 30, 74, 105, 125, 101, 121, 30, 67, 119, 39, 96, 32, 31, 83, 109, 105, 98, 100, 67
      }
   );
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         96,
         98,
         101,
         64,
         83,
         35,
         39,
         57,
         114,
         95,
         78,
         127,
         97,
         113,
         60,
         89,
         80,
         111,
         62,
         56,
         123,
         64,
         85,
         106,
         96,
         56,
         114,
         94,
         15,
         111,
         57,
         57,
         98,
         68,
         65,
         109,
         125,
         101
      }
   );
   private static AtomicBoolean recoverField_c = new AtomicBoolean(false);
   private static long recoverField_d = 0L;
   private static Boolean recoverField_e = null;
   private static LinkedList recoverField_f = new LinkedList();
   private static Boolean recoverField_g = null;
   private static int recoverField_h = 1;
   private static int recoverField_i = 2;

   public static boolean recoverMethod_a_9b79() {
      if (null == recoverField_g) {
         Context var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (null == var0) {
            return false;
         }

         SharedPreferences var1 = var0.getSharedPreferences("cn.jiguang.prefs", 0);
         int var2 = var1.getInt("sdk_reg_finger", recoverField_i);
         recoverMethod_a_1a54e370(var0, var2);
      }

      return null == recoverField_g ? false : recoverField_g;
   }

   public static boolean recoverMethod_b_9b79() {
      Context var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
      if (var0 == null || !RecoverTypeaf2b07c2.recoverMethod_a_9b79() || !RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_f0554df7(var0)) {
         return false;
      } else {
         return null == recoverField_e && null != var0 ? recoverMethod_a_f0554df7(var0) : recoverField_c.get();
      }
   }

   private static void recoverMethod_a_304fbba3(Context var0, boolean var1, long var2) {
      recoverMethod_b_304fbba3(var0, var1, var2);
      recoverField_d = var2;
      if (recoverField_c.get() != var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "sdk banned status changed, update status, sdkBanned: " + var1);
         if (!var1) {
            recoverField_c = new AtomicBoolean(var1);
         }

         Bundle var4 = new Bundle();
         var4.putBoolean("sdk_banned", var1);
         JCoreManager.onEvent(var0, "", 81, null, var4);
      }

      if (var1) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
            "JSDKBannedHelper", "[key-step]Your sdk is disabled already. If you want to enable it, please contact us."
         );
      }

      recoverField_c = new AtomicBoolean(var1);
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JSDKBannedHelper", "sdk banned status, banned: " + var1 + ", nextTime: " + recoverField_d);
   }

   public static synchronized boolean recoverMethod_a_f0554df7(Context var0) {
      if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
         return recoverField_c.get();
      }

      String var1 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
      if (TextUtils.isEmpty(var1)) {
         return recoverField_c.get();
      }

      if (null != recoverField_e) {
         return recoverField_c.get();
      }

      recoverField_e = true;
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "[isSDKBanned] first request banned config");
      recoverMethod_c_f0554df7(var0);
      if (System.currentTimeMillis() > recoverField_d) {
         RecoverTypeaf2b0403.recoverMethod_a_135d7bee("FUTURE_TASK", new RecoverTypeaf2a972a.Member8ee17967(var0), 0, false);
      } else {
         if (recoverField_c.get()) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
               "JSDKBannedHelper", "[key-step]Your sdk is disabled already. If you want to enable it, please contact us."
            );
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "request sdk banned config not time up, not request");
      }

      return recoverField_c.get();
   }

   private static void recoverMethod_b_3c4029ab(Context var0, JSONObject var1) {
      try {
         if (null == var1) {
            return;
         }

         JSONObject var2 = var1.optJSONObject("values");
         if (null != var2) {
            boolean var3 = "banned".equals(var2.optString("status"));
            long var4 = var2.optLong("nexttime", 43200L);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "from server sdk banned status: " + var3 + ", next request time: " + var4);
            var4 = var4 <= 0L ? 0L : var4;
            var4 = var4 >= 86400L ? 86400L : var4;
            var4 = System.currentTimeMillis() + var4 * 1000L;
            recoverMethod_a_304fbba3(var0, var3, var4);
         }

         int var7 = var1.optInt("reg_finger", recoverField_i);
         recoverMethod_b_1a54e370(var0, var7);
         recoverMethod_a_1a54e370(var0, var7);
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "parse sdk banned config failed, " + var6.getMessage());
      }
   }

   private static void recoverMethod_a_1a54e370(Context var0, int var1) {
      recoverField_g = var1 == recoverField_h;
   }

   private static void recoverMethod_b_1a54e370(Context var0, int var1) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "save sdk regFinger: " + var1);
         SharedPreferences var2 = var0.getSharedPreferences("cn.jiguang.prefs", 0);
         var2.edit().putInt("sdk_reg_finger", var1).apply();
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "[saveSdkRegFinger] error, " + var3);
      }
   }

   private static void recoverMethod_b_304fbba3(Context var0, boolean var1, long var2) {
      try {
         String var4 = var1 + "," + var2;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "save sdk banned status: " + var4);
         SharedPreferences var5 = var0.getSharedPreferences("cn.jiguang.prefs", 0);
         var5.edit().putString("sdk_banned_status", var4).apply();
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "[saveBannedConfigJson] parse json error, " + var6.getMessage());
      }
   }

   private static boolean recoverMethod_c_f0554df7(Context var0) {
      try {
         SharedPreferences var1 = var0.getSharedPreferences("cn.jiguang.prefs", 0);
         String var2 = var1.getString("sdk_banned_status", "");
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JSDKBannedHelper", "local sdk banned config: " + var2);
         if (!TextUtils.isEmpty(var2)) {
            String[] var3 = var2.split(",");
            boolean var4 = Boolean.parseBoolean(var3[0]);
            long var5 = Long.parseLong(var3[1]);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "local banned config, isBanned: " + var4 + ", nextReqTime: " + var5);
            recoverField_d = var5;
            recoverField_c = new AtomicBoolean(var4);
            return true;
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "read config json from cache failed !! error:" + var7);
      }

      return false;
   }

   public static Integer recoverMethod_c_bd10275c() {
      return 2;
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "updateImmediately start");
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "updateImmediately failed: context is null");
      } else {
         try {
            boolean var1 = recoverField_e != null || System.currentTimeMillis() <= recoverField_d;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "JSDKBannedHelper", "updateImmediately: needReset=" + var1 + ", gIsConfigRequested=" + recoverField_e + ", gNextReqTime=" + recoverField_d
            );
            if (var1) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "Reset request status and continue update");
               recoverField_e = null;
               recoverField_d = 0L;
               recoverMethod_a_304fbba3(var0, recoverField_c.get(), recoverField_d);
            }

            recoverMethod_a_f0554df7(var0);
         } catch (Throwable var2) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSDKBannedHelper", "updateImmediately failed: " + var2.getMessage());
         }
      }
   }

   static {
      recoverField_f.clear();
      recoverField_f.add(recoverField_a);
      recoverField_f.add(recoverField_b);
   }

   private static class Member8ee17967 extends RecoverTypeaf2b0401 {
      private Context recoverField_a;

      Member8ee17967(Context var1) {
         this.recoverField_a = var1;
         this.recoverField_h = "JBannedConfig";
      }

      @Override
      public void recoverMethod_a_9b75() {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "start request sdk banned config");
         int var1 = this.recoverMethod_a_f0554de6(this.recoverField_a);
         if (var1 != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "request banned config failed, code: " + var1);
         }
      }

      private int recoverMethod_a_f0554de6(Context var1) {
         try {
            String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var1);
            if (TextUtils.isEmpty(var2)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "request bannedConfig failed because can't get appKey");
               return -1;
            }

            long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_B_48794abf());
            RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(var1, RecoverTypeaf2a972a.recoverField_f);

            for (Object recoveryElement_294 : RecoverTypeaf2a972a.recoverField_f) {
               String var6 = (String)recoveryElement_294;
               String var7 = UUID.randomUUID().toString();
               JSONObject var8 = new JSONObject();
               var8.put("appkey", var2);
               var8.put("uid", var3);
               var8.put("stoken", var7);
               var8.put("ver", 2);
               var8.put("platform", "a");
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "request url: " + var6 + ", param json:" + var8.toString());
               if (TextUtils.isEmpty(var6)) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSDKBannedHelper", "can't get url, give up upload");
                  return -1;
               }

               RecoverTypeaf2aa26c var9 = RecoverTypeaf2aa267.recoverMethod_a_ab3ea53a(var6, var8.toString(), var1, false, 3, 3);
               int var10 = var9.recoverMethod_a_9b68();
               String var11 = var9.recoverMethod_b_73cf11cb();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "code: " + var10 + ", responseBody:" + var11);
               switch (var9.recoverMethod_a_9b68()) {
                  case -3:
                     return -2;
                  case -2:
                  default:
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "upload" + var8.toString() + " failed");
                     break;
                  case -1:
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSDKBannedHelper", "upload" + var8.toString() + " error:" + var9.recoverMethod_b_73cf11cb());
                     break;
                  case 0:
                     if (!TextUtils.isEmpty(var11)) {
                        JSONObject var12 = new JSONObject(var11);
                        String var13 = var12.optString("stoken");
                        if (var7.equals(var13)) {
                           RecoverTypeaf2a972a.recoverMethod_b_3c4029ab(var1, new JSONObject(var11));
                        } else {
                           RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                              "JSDKBannedHelper", "banned config uuid not match, reqUuid: " + var7 + ", resUuid: " + var13
                           );
                        }
                     }

                     return 0;
                  case 3006:
                     RecoverTypeaf2a972a.recoverField_e = null;
               }
            }
         } catch (Throwable var14) {
         }

         return -1;
      }
   }
}
