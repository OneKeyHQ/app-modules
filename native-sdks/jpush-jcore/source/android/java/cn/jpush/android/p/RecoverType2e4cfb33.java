package cn.jpush.android.p;

import android.content.Context;
import android.content.SharedPreferences;

public class RecoverType2e4cfb33 {
   private static SharedPreferences recoverField_a;

   private static SharedPreferences recoverMethod_c_c831c207(Context var0) {
      if (recoverField_a == null) {
         recoverField_a = var0.getApplicationContext().getSharedPreferences("cn.jiguang.union.ads.core.common.prefs", 0);
      }

      return recoverField_a;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      recoverMethod_c_c831c207(var0).edit().putString("notify_inapp_latest_state", var1).apply();
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      recoverMethod_c_c831c207(var0).edit().putLong("key_in_app_last_show_time", var1).apply();
   }

   public static long recoverMethod_a_f0554de7(Context var0) {
      return recoverMethod_c_c831c207(var0).getLong("key_in_app_last_show_time", -1L);
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      return recoverMethod_c_c831c207(var0).getString("top_banner_latest_state", "");
   }
}
