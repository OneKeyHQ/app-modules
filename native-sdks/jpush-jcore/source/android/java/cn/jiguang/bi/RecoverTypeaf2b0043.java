package cn.jiguang.bi;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.SecureRandom;

public class RecoverTypeaf2b0043 {
   private static SharedPreferences recoverField_a;

   public static void recoverMethod_a_f0554df3(Context var0) {
      recoverField_a = var0.getSharedPreferences("cn.jpush.preferences.support.rid", 0);
   }

   private static SharedPreferences recoverMethod_c_c831c207(Context var0) {
      if (recoverField_a == null) {
         recoverMethod_a_f0554df3(var0);
      }

      return recoverField_a;
   }

   private static long recoverMethod_a_1a54e725(Context var0, long var1) {
      long var3 = recoverMethod_c_c831c207(var0).getLong("next_rid", var1);
      if (var3 != var1) {
         var3 = recoverMethod_a_1349e3(var3);
         recoverMethod_c_c831c207(var0).edit().putLong("next_rid", var3).apply();
      }

      return var3;
   }

   private static long recoverMethod_a_1349e3(long var0) {
      return (var0 % 2L == 0L ? var0 + 1L : var0 + 2L) % 32767L;
   }

   public static synchronized long recoverMethod_b_f0554de7(Context var0) {
      long var1 = recoverMethod_a_1a54e725(var0, -1L);
      if (var1 == -1L) {
         long var3 = Math.abs(new SecureRandom().nextInt(32767));
         var1 = recoverMethod_a_1349e3(var3);
         recoverMethod_c_c831c207(var0).edit().putLong("next_rid", var1).apply();
      }

      return var1;
   }
}
