package cn.jiguang.bk;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.ay.RecoverTypeaf2ac7f2;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import java.util.UUID;

public class RecoverTypeaf2b07c8 {
   public static int recoverField_a = 1;

   public static String recoverMethod_a_e501eb0d(Context var0) {
      String var1 = "";
      var1 = recoverMethod_b_e501eb0d(var0);
      if (RecoverTypeaf2b07d5.recoverMethod_h_aad3b203(var1)) {
         recoverField_a = 3;
         recoverMethod_c_5ffc00fd(var0, var1);
         return var1;
      }

      var1 = recoverMethod_b_a4ea6d43(var0, var1);
      if (RecoverTypeaf2b07d5.recoverMethod_h_aad3b203(var1)) {
         recoverField_a = 1;
         recoverMethod_c_5ffc00fd(var0, var1);
         return var1;
      }

      var1 = recoverMethod_d_e501eb0d(var0);
      if (RecoverTypeaf2b07d5.recoverMethod_h_aad3b203(var1)) {
         recoverField_a = 2;
         recoverMethod_c_5ffc00fd(var0, var1);
         return var1;
      }

      String var2 = "";
      if (VERSION.SDK_INT < 23) {
         var2 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_c_a4ea6d43(var0, var1);
      }

      String var3 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_n_e501eb0d(var0);
      String var4 = UUID.randomUUID().toString();
      long var5 = System.currentTimeMillis();
      var1 = RecoverTypeaf2b07d5.recoverMethod_d_e96ea081(var2 + var3 + var4 + var5);
      if (TextUtils.isEmpty(var1)) {
         var1 = var4;
      }

      recoverField_a = 0;
      new RecoverTypeaf2ac7f2()
         .recoverMethod_b_80115e7e(var2)
         .recoverMethod_c_80115e7e(var3)
         .recoverMethod_d_80115e7e(var4)
         .recoverMethod_a_4883166e(var5)
         .recoverMethod_a_80115e7e(var1)
         .recoverMethod_a_f0554df3(var0);
      recoverMethod_c_5ffc00fd(var0, var1);
      return var1;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      if (!TextUtils.isEmpty(var1)) {
         recoverMethod_a_9f84d177(var0, var1, true);
      }
   }

   private static String recoverMethod_b_a4ea6d43(Context var0, String var1) {
      return null;
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      String var1 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_b_48794abf());
      if (!TextUtils.isEmpty(var1)) {
         return RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var1, RecoverType5a681cd.recoverField_i);
      }

      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_a_48794abf());
      return !TextUtils.isEmpty(var2) ? var2 : null;
   }

   private static void recoverMethod_c_5ffc00fd(Context var0, String var1) {
      recoverMethod_a_9f84d177(var0, var1, false);
   }

   private static void recoverMethod_a_9f84d177(Context var0, String var1, boolean var2) {
      String var3 = RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var1, RecoverType5a681cd.recoverField_i);
      if (var2 || recoverField_a == 1 || recoverField_a == 0) {
         recoverMethod_d_a4ea6d43(var0, var3);
      }

      if (var2 || recoverField_a == 2 || recoverField_a == 0) {
         recoverMethod_e_a4ea6d43(var0, var3);
      }

      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_b_48794abf().recoverMethod_a_4e9722a3(var3));
      recoverMethod_c_f0554df3(var0);
   }

   private static void recoverMethod_c_f0554df3(Context var0) {
      if (!JConstants.isAndroidQ(var0, true, "not write deviceId to SD")) {
      }

      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_a_48794abf().recoverMethod_a_4e9722a3(null));
   }

   private static String recoverMethod_d_a4ea6d43(Context var0, String var1) {
      return null;
   }

   private static String recoverMethod_e_a4ea6d43(Context var0, String var1) {
      return JConstants.isAndroidQ(var0, true, "not write deviceId to SD") ? var1 : null;
   }

   private static String recoverMethod_d_e501eb0d(Context var0) {
      return JConstants.isAndroidQ(var0, true, "do not get deviceId from SD") ? null : null;
   }
}
