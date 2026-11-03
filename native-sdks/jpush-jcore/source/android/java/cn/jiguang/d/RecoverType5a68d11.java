package cn.jiguang.d;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;

public class RecoverType5a68d11 {
   private static volatile Long recoverField_a;
   private static volatile Long recoverField_b;

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      long var1 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_B_48794abf());
      if (var1 <= 0L) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("SpHelper", "isValidRegistered uid <= 0");
         return false;
      } else {
         String var3 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_C_48794abf());
         if (TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("SpHelper", "isValidRegistered regId is empty");
            return false;
         } else {
            return true;
         }
      }
   }

   public static long recoverMethod_b_f0554de7(Context var0) {
      return recoverMethod_a_1a54e725(var0, System.currentTimeMillis());
   }

   public static long recoverMethod_a_1a54e725(Context var0, long var1) {
      return (var1 + recoverMethod_c_f0554de7(var0)) / 1000L;
   }

   public static long recoverMethod_c_f0554de7(Context var0) {
      if (recoverField_a != null && recoverField_b != null) {
         return recoverField_a - recoverField_b;
      } else {
         long var1 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_H_48794abf());
         long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_I_48794abf());
         if (var1 != 0L && var3 != 0L) {
            recoverField_a = var3;
            recoverField_b = var1;
            return var3 - var1;
         } else {
            return 0L;
         }
      }
   }

   public static void recoverMethod_b_1a54e731(Context var0, long var1) {
      if (var1 > 0L) {
         long var3 = System.currentTimeMillis();
         recoverField_a = var1;
         recoverField_b = var3;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            var0,
            RecoverType5a690d1.recoverMethod_I_48794abf().recoverMethod_a_4e9722a3(var1),
            RecoverType5a690d1.recoverMethod_H_48794abf().recoverMethod_a_4e9722a3(var3)
         );
      }
   }
}
