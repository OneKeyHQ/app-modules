package cn.jpush.android.z;

import android.text.TextUtils;
import cn.jpush.android.helper.Logger;

public class RecoverType2e4d20be {
   public static byte[] recoverMethod_a_917ffcef(String var0, int var1, String var2) {
      RecoverType2e4d20c0 var3 = new RecoverType2e4d20c0(8192);
      if (var1 == 10) {
         var3.recoverMethod_a_25e06fc(recoverMethod_b_afa28ebe(var0));
      }

      var3.recoverMethod_a_25e06fc(recoverMethod_b_afa28ebe(var2));
      return var3.recoverMethod_a_12d408();
   }

   public static byte[] recoverMethod_a_9e9f8f65(int var0, String var1) {
      RecoverType2e4d20c0 var2 = new RecoverType2e4d20c0(8192);
      var2.recoverMethod_a_13462e(var0);
      var2.recoverMethod_a_aad3b1ff(var1);
      return var2.recoverMethod_a_12d408();
   }

   public static byte[] recoverMethod_a_255806f(int var0) {
      RecoverType2e4d20c0 var1 = new RecoverType2e4d20c0(128);
      var1.recoverMethod_a_13462e(var0);
      return var1.recoverMethod_a_12d408();
   }

   public static byte[] recoverMethod_a_322ca05d(int var0, byte var1, long var2, String var4) {
      RecoverType2e4d20c0 var5 = new RecoverType2e4d20c0(128);
      var5.recoverMethod_b_13462e(var0);
      var5.recoverMethod_a_13462e(var1);
      var5.recoverMethod_a_1349ef(var2);
      if (!TextUtils.isEmpty(var4)) {
         var5.recoverMethod_a_25e06fc(recoverMethod_b_afa28ebe(var4));
      }

      return var5.recoverMethod_a_12d408();
   }

   public static byte[] recoverMethod_a_52995be6(String var0, byte var1, boolean var2) {
      RecoverType2e4d20c0 var3 = new RecoverType2e4d20c0(8192);
      var3.recoverMethod_a_25e06fc(TextUtils.isEmpty(var0) ? new byte[0] : recoverMethod_b_afa28ebe(var0));
      var3.recoverMethod_a_13462e(var1);
      var3.recoverMethod_a_13462e(var2 ? 0 : 1);
      return var3.recoverMethod_a_12d408();
   }

   public static byte[] recoverMethod_a_afa28ebe(String var0) {
      RecoverType2e4d20c0 var1 = new RecoverType2e4d20c0(128);
      var1.recoverMethod_a_13462e(7);
      var1.recoverMethod_a_13462e(1);
      var1.recoverMethod_a_25e06fc(var0 != null ? recoverMethod_b_afa28ebe(var0) : new byte[0]);
      return var1.recoverMethod_a_12d408();
   }

   public static byte[] recoverMethod_b_afa28ebe(String var0) {
      try {
         return var0.getBytes("UTF-8");
      } catch (Throwable var2) {
         Logger.recoverMethod_ee_e925fa09("PushPackage", "stringToUtf8Bytes error:" + var2.getMessage());
         return var0.getBytes();
      }
   }

   public static byte[] recoverMethod_c_afa28ebe(String var0) {
      RecoverType2e4d20c0 var1 = new RecoverType2e4d20c0(8192);
      var1.recoverMethod_a_25e06fc(recoverMethod_b_afa28ebe(var0));
      return var1.recoverMethod_a_12d408();
   }
}
