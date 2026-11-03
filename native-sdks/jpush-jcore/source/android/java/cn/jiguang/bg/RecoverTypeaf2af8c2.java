package cn.jiguang.bg;

import android.text.TextUtils;
import cn.jiguang.bk.RecoverTypeaf2b07cb;

public class RecoverTypeaf2af8c2 {
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{96, 89, 98, 99, 101, 89, 76, 106, 38, 96, 116, 66, 83, 112, 103, 120, 63, 67, 84, 120, 122, 98, 81, 112}
   );
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{81, 112, 74, 108, 124, 127, 125, 67, 14, 111, 109, 100, 98, 89, 79, 119, 38, 115, 127, 84, 96, 89}
   );

   public static String recoverMethod_a_79834524(byte[] var0) {
      if (var0 != null && var0.length != 0) {
         try {
            byte[] var1 = recoverField_a.getBytes("UTF-8");
            byte[] var2 = recoverField_b.getBytes("UTF-8");
            return recoverMethod_a_c240e516(var0, var1, var2);
         } catch (Exception var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String recoverMethod_a_c240e516(byte[] var0, byte[] var1, byte[] var2) {
      try {
         int var3 = recoverMethod_a_e30eacb6(var0, var1);
         if (var3 == -1) {
            return null;
         }

         int var4 = recoverMethod_a_7ec75e93(var0, var2, var3 + var1.length);
         if (var4 == -1) {
            return null;
         }

         int var5 = var3 + var1.length;
         int var6 = var4 - var5;
         if (var6 < 0) {
            return null;
         }

         if (var6 == 0) {
            return "";
         }

         if (var5 + var6 > var0.length) {
            return null;
         }

         byte[] var7 = new byte[var6];
         System.arraycopy(var0, var5, var7, 0, var6);
         String var8 = new String(var7, "UTF-8");
         return TextUtils.isEmpty(var8) ? null : var8.trim();
      } catch (Throwable var9) {
         return null;
      }
   }

   private static int recoverMethod_a_7ec75e93(byte[] var0, byte[] var1, int var2) {
      try {
         if (var0 == null || var1 == null) {
            return -1;
         }

         if (var1.length == 0) {
            return var2;
         }

         if (var2 < 0) {
            return -1;
         }

         if (var0.length < var1.length) {
            return -1;
         }

         for (int var3 = var2; var3 <= var0.length - var1.length; var3++) {
            boolean var4 = true;

            for (int var5 = 0; var5 < var1.length; var5++) {
               if (var0[var3 + var5] != var1[var5]) {
                  var4 = false;
                  break;
               }
            }

            if (var4) {
               return var3;
            }
         }
      } catch (Throwable var6) {
      }

      return -1;
   }

   private static int recoverMethod_a_e30eacb6(byte[] var0, byte[] var1) {
      return recoverMethod_a_7ec75e93(var0, var1, 0);
   }
}
