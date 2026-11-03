package cn.jiguang.av;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ay.RecoverTypeaf2ac7f1;
import cn.jiguang.bk.RecoverTypeaf2b07d3;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.internal.JConstants;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class RecoverTypeaf2abcae {
   private static Integer recoverField_a = null;

   public static byte[] recoverMethod_a_980fbf3(
      long var0,
      long var2,
      String var4,
      String var5,
      String var6,
      long var7,
      byte var9,
      int var10,
      String var11,
      String var12,
      String var13,
      String var14,
      int var15,
      int var16,
      String var17
   ) {
      RecoverTypeaf2ac7f1 var18 = new RecoverTypeaf2ac7f1(20480);
      var18.recoverMethod_b_13462e(0);
      var18.recoverMethod_a_13462e(27);
      var18.recoverMethod_a_13462e(1);
      var18.recoverMethod_b_1349ef(var0);
      var18.recoverMethod_a_1349ef(0L);
      var18.recoverMethod_b_1349ef(var2);
      var18.recoverMethod_a_13462e(97);
      var18.recoverMethod_a_13462e(0);
      var18.recoverMethod_b_13462e(0);
      var18.recoverMethod_a_aad3b1ff(var4);
      var18.recoverMethod_a_aad3b1ff(var6);
      var18.recoverMethod_a_aad3b1ff(var5);
      var18.recoverMethod_a_13462e(0);
      var18.recoverMethod_a_1349ef(var7);
      var18.recoverMethod_a_13462e(var9);
      var18.recoverMethod_a_13462e(var10);
      var18.recoverMethod_a_aad3b1ff(var11);
      var18.recoverMethod_a_aad3b1ff(var12);
      var18.recoverMethod_a_aad3b1ff(var13);
      var18.recoverMethod_a_aad3b1ff(var14);
      var18.recoverMethod_a_13462e(var15);
      var18.recoverMethod_a_13462e(var16);
      var18.recoverMethod_a_aad3b1ff(var17);
      var18.recoverMethod_b_255f295(var18.recoverMethod_a_9b68(), 0);
      return var18.recoverMethod_b_12d408();
   }

   public static byte[] recoverMethod_a_3501eca6(long var0, String var2, String var3, String var4, String var5, long var6, String var8) {
      int var9 = recoverMethod_a_9b68();
      RecoverTypeaf2ac7f1 var10 = new RecoverTypeaf2ac7f1(20480);
      var10.recoverMethod_b_13462e(0);
      var10.recoverMethod_a_13462e(29);
      var10.recoverMethod_a_13462e(0);
      var10.recoverMethod_b_1349ef(var0);
      var10.recoverMethod_a_1349ef(var9);
      var10.recoverMethod_b_1349ef(0L);
      var10.recoverMethod_a_aad3b1ff(var2);
      var10.recoverMethod_a_aad3b1ff(var3);
      var10.recoverMethod_a_aad3b1ff(var4);
      var10.recoverMethod_a_13462e(0);
      var10.recoverMethod_a_aad3b1ff(var5);
      var10.recoverMethod_a_1349ef(var6);
      var10.recoverMethod_a_aad3b1ff(var8);
      var10.recoverMethod_b_255f295(var10.recoverMethod_a_9b68(), 0);
      return var10.recoverMethod_b_12d408();
   }

   public static byte[] recoverMethod_a_d6eec457(long var0, int var2, long var3, short var5, int var6, int var7, int var8) {
      RecoverTypeaf2ac7f1 var9 = new RecoverTypeaf2ac7f1(20480);
      var9.recoverMethod_b_13462e(0);
      var9.recoverMethod_a_13462e(8);
      var9.recoverMethod_a_13462e(2);
      var9.recoverMethod_b_1349ef(var0);
      var9.recoverMethod_a_1349ef(var2);
      var9.recoverMethod_b_1349ef(var3);
      var9.recoverMethod_a_1349ef(var5);
      var9.recoverMethod_a_13462e(var6);
      var9.recoverMethod_b_13462e(var7);
      var9.recoverMethod_a_13462e(var8);
      var9.recoverMethod_b_255f295(var9.recoverMethod_a_9b68(), 0);
      return var9.recoverMethod_b_12d408();
   }

   public static byte[] recoverMethod_a_7700bcb1(Context var0, int var1, int var2, long var3, byte[] var5, long var6) {
      RecoverTypeaf2ac7f1 var8 = new RecoverTypeaf2ac7f1(20480);
      var8.recoverMethod_b_13462e(0);
      var8.recoverMethod_a_13462e(var2);
      var8.recoverMethod_a_13462e(var1);
      var8.recoverMethod_b_1349ef(var3);
      var8.recoverMethod_a_1349ef(JConstants.tcpSessionId);
      if (var6 == 0L) {
         var6 = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
      }

      var8.recoverMethod_b_1349ef(var6);
      var8.recoverMethod_a_25e06fc(var5);
      var8.recoverMethod_b_255f295(var8.recoverMethod_a_9b68(), 0);
      byte[] var9 = var8.recoverMethod_b_12d408();
      return recoverMethod_a_d9b22e23(var0, var9);
   }

   public static byte[] recoverMethod_a_d9b22e23(Context var0, byte[] var1) {
      if (var1 != null && var1.length != 0) {
         int var2 = var1.length;
         int var3 = var2 - 24;
         byte[] var4 = new byte[24];
         byte[] var5 = new byte[var3];
         System.arraycopy(var1, 0, var4, 0, 24);
         System.arraycopy(var1, 24, var5, 0, var3);
         String var6 = recoverMethod_a_e501eb0d(var0);

         try {
            byte var8 = JConstants.tcpAlgorithm;
            byte[] var7;
            if (var8 == 2) {
               var7 = new RecoverTypeaf2b07d3().recoverMethod_a_19174fd7(var5, var6);
            } else {
               var7 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var5, var6, var6.substring(0, 16), true);
            }

            int var9 = var7.length + 24;
            byte[] var10 = new byte[var9];
            System.arraycopy(var4, 0, var10, 0, 24);
            System.arraycopy(var7, 0, var10, 24, var7.length);
            var10[0] = (byte)(var9 >>> 8 & 0xFF);
            var10[1] = (byte)(var9 & 0xFF);
            var10[0] = (byte)(var10[0] | 128);
            var10[4] = var8;
            return var10;
         } catch (Exception var11) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("CorePackage", "e:" + var11);
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("CorePackage", "encrpt data failed");
            return null;
         }
      } else {
         return var1;
      }
   }

   public static byte[] recoverMethod_a_b66bea5e(short var0, short var1, String var2) {
      RecoverTypeaf2ac7f1 var3 = new RecoverTypeaf2ac7f1(20480);
      var3.recoverMethod_a_13462e(var0);
      var3.recoverMethod_a_13462e(var1);
      var3.recoverMethod_a_aad3b1ff(var2);
      return var3.recoverMethod_b_12d408();
   }

   public static String recoverMethod_a_51ac9724(ByteBuffer var0) {
      short var1 = var0.getShort();
      byte[] var2 = new byte[var1];
      var0.get(var2);

      try {
         return new String(var2, "UTF-8");
      } catch (Throwable var4) {
         return null;
      }
   }

   public static int recoverMethod_a_9b68() {
      if (recoverField_a != null) {
         return recoverField_a;
      }

      recoverField_a = Math.abs(new SecureRandom().nextInt());
      return recoverField_a;
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      long var1 = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
      return var1 > 0L ? RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(var1) : RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(recoverMethod_a_9b68());
   }
}
