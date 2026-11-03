package cn.jiguang.bk;

public class RecoverTypeaf2b07cb {
   public static byte[] recoverMethod_a_4962d961(byte[] var0) {
      try {
         byte[] var1 = new byte[]{32, 25, 8, 22, 17, 48};
         byte[] var2 = new byte[var0.length];

         for (int var3 = 0; var3 < var0.length; var3++) {
            var2[var3] = (byte)(var0[var3] ^ var1[(var0.length + var3) % var1.length]);
         }

         return var2;
      } catch (Throwable var4) {
         return new byte[0];
      }
   }

   public static String recoverMethod_b_79834524(byte[] var0) {
      return new String(recoverMethod_a_4962d961(var0));
   }
}
