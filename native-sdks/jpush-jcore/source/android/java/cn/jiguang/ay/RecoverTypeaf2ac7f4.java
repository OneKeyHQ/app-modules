package cn.jiguang.ay;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RecoverTypeaf2ac7f4 {
   private static List recoverField_a = new ArrayList();

   public static String recoverMethod_a_e501eb0d(Context var0) {
      String var1 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aj_48794abf());
      if (!TextUtils.isEmpty(var1)) {
         var1 = new String(Base64.decode(var1, 2));
      }

      if (recoverMethod_a_aad3b203(var1)) {
         return var1;
      }

      var1 = recoverMethod_b_e501eb0d(var0);
      if (!TextUtils.isEmpty(var1)) {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            var0, RecoverType5a690d1.recoverMethod_aj_48794abf().recoverMethod_a_4e9722a3(Base64.encodeToString(var1.getBytes(), 2))
         );
      }

      return var1;
   }

   private static String recoverMethod_b_e501eb0d(Context var0) {
      try {
         String var1 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_p;
         if (recoverMethod_a_aad3b203(var1)) {
            return var1;
         }

         String var5 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_i;
         if (recoverMethod_a_aad3b203(var5) && !"9774d56d682e549c".equals(var5.toLowerCase(Locale.getDefault()))) {
            return var5;
         }

         String var3 = recoverMethod_c_e501eb0d(var0);
         return recoverMethod_a_aad3b203(var3) ? var3 : "";
      } catch (Exception var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("UDIDUtils", "", var4);
         String var2 = recoverMethod_d_e501eb0d(var0);
         return recoverMethod_a_aad3b203(var2) ? var2 : "";
      }
   }

   private static String recoverMethod_c_e501eb0d(Context var0) {
      String var1 = recoverMethod_d_e501eb0d(var0);
      if (var1 == null) {
         var1 = " ";
      }

      return var1;
   }

   private static String recoverMethod_d_e501eb0d(Context var0) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UDIDUtils", "Action:getSavedUuid");
      String var1 = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, RecoverType5a690d1.recoverMethod_az_48794abf());
      if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1)) {
         return var1;
      }

      if (!RecoverTypeaf2b07c1.recoverMethod_d_9b79()) {
         return recoverMethod_g_e501eb0d(var0);
      }

      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ak_48794abf());
      if (TextUtils.isEmpty(var2)) {
         if (VERSION.SDK_INT >= 23) {
            return recoverMethod_g_e501eb0d(var0);
         }

         var2 = recoverMethod_e_e501eb0d(var0);
      }

      return var2;
   }

   private static String recoverMethod_e_e501eb0d(Context var0) {
      String var1 = recoverMethod_f_e501eb0d(var0);
      File var2 = null;
      if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1)) {
         var2 = new File(var1);
      }

      String var3 = RecoverTypeaf2b07c9.recoverMethod_c_af215df4(var2);
      if (!TextUtils.isEmpty(var3)) {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ak_48794abf().recoverMethod_a_4e9722a3(var3));
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("UDIDUtils", "Got sdcard file saved udid - " + var3);
         return var3;
      } else {
         String var4 = System.currentTimeMillis() + "";
         var3 = UUID.nameUUIDFromBytes(var4.getBytes()).toString();
         var3 = RecoverTypeaf2b07d5.recoverMethod_j_e96ea081(var3);
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ak_48794abf().recoverMethod_a_4e9722a3(var3));
         RecoverTypeaf2b07c9.recoverMethod_b_d9caeafa(var2, var3);
         return var3;
      }
   }

   private static String recoverMethod_f_e501eb0d(Context var0) {
      String var1 = RecoverTypeaf2b07c1.recoverMethod_g_e501eb0d(var0);
      return TextUtils.isEmpty(var1) ? null : var1 + ".push_udid";
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      RecoverType5a690d1 var1 = RecoverType5a690d1.recoverMethod_az_48794abf();
      String var2 = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, var1);
      if (var2 == null) {
         var2 = UUID.randomUUID().toString();
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, var1.recoverMethod_a_4e9722a3(var2));
      }

      return var2;
   }

   private static boolean recoverMethod_a_aad3b203(String var0) {
      if (!RecoverTypeaf2b07d5.recoverMethod_h_aad3b203(var0)) {
         return false;
      }

      if (var0.length() < 10) {
         return false;
      }

      for (Object recoveryElement_142 : recoverField_a) {
         String var2 = (String)recoveryElement_142;
         if (var0.startsWith(var2)) {
            return false;
         }
      }

      return true;
   }

   static {
      recoverField_a.add("358673013795895");
      recoverField_a.add("004999010640000");
      recoverField_a.add("00000000000000");
      recoverField_a.add("000000000000000");
   }
}
