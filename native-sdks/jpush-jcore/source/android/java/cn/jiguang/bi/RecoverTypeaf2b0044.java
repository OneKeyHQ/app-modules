package cn.jiguang.bi;

public class RecoverTypeaf2b0044 {
   private static void recoverMethod_a_31fc143d(StringBuffer var0, byte var1) {
      var0.append("0123456789ABCDEF".charAt(var1 >> 4 & 15)).append("0123456789ABCDEF".charAt(var1 & 15));
   }

   public static String recoverMethod_a_79834524(byte[] var0) {
      if (var0 == null) {
         return "";
      }

      StringBuffer var1 = new StringBuffer(2 * var0.length);

      for (byte var5 : var0) {
         recoverMethod_a_31fc143d(var1, var5);
         var1.append(' ');
      }

      return var1.toString();
   }
}
