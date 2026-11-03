package cn.jiguang.e;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c1;

public class RecoverType5a690d3 {
   private static SharedPreferences recoverField_a;

   private static synchronized SharedPreferences recoverMethod_f_c831c207(Context var0) {
      try {
         String var1 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var0);
         if (!TextUtils.isEmpty(var1) && !TextUtils.equals(var1, var0.getPackageName())) {
            String[] var2 = var1.split(":");
            String var3 = var2[1];
            if (recoverField_a == null) {
               recoverField_a = var0.getSharedPreferences("cn.jiguang.sdk.log_config_" + var3, 0);
            }
         } else if (recoverField_a == null) {
            recoverField_a = var0.getSharedPreferences("cn.jiguang.sdk.log_config", 0);
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SpConfig", "getSharedPreferences failed, e: " + var4.getMessage());
         if (recoverField_a == null) {
            recoverField_a = var0.getSharedPreferences("cn.jiguang.sdk.log_config", 0);
         }
      }

      return recoverField_a;
   }

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      recoverMethod_f_c831c207(var0).edit().putBoolean("external_log_enabled", var1).apply();
   }

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      return recoverMethod_f_c831c207(var0).getBoolean("external_log_enabled", false);
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      recoverMethod_f_c831c207(var0).edit().putLong("log_last_timestamp", var1).apply();
   }

   public static long recoverMethod_b_f0554de7(Context var0) {
      return recoverMethod_f_c831c207(var0).getLong("log_last_timestamp", 0L);
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      recoverMethod_f_c831c207(var0).edit().putInt("log_ref_count", var1).apply();
   }

   public static int recoverMethod_c_f0554de6(Context var0) {
      return recoverMethod_f_c831c207(var0).getInt("log_ref_count", 0);
   }

   public static void recoverMethod_b_1a552341(Context var0, boolean var1) {
      recoverMethod_f_c831c207(var0).edit().putBoolean("local_upload_enable", var1).apply();
   }

   public static boolean recoverMethod_d_f0554df7(Context var0) {
      return recoverMethod_f_c831c207(var0).getBoolean("local_upload_enable", false);
   }

   public static void recoverMethod_b_1a54e370(Context var0, int var1) {
      recoverMethod_f_c831c207(var0).edit().putInt("server_upload_state", var1).apply();
   }

   public static int recoverMethod_e_f0554de6(Context var0) {
      return recoverMethod_f_c831c207(var0).getInt("server_upload_state", 2);
   }
}
