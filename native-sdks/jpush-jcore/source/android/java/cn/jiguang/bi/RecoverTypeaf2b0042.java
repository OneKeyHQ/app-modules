package cn.jiguang.bi;

import cn.jiguang.api.JCoreManager;

public class RecoverTypeaf2b0042 {
   private static void recoverMethod_a_996fb7bb(int var0, boolean var1, String var2, String var3, Throwable var4) {
      JCoreManager.onEvent(null, "JSupport", 18, var2, null, var0, var1, var3, var4);
   }

   public static void recoverMethod_a_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(2, true, var0, var1, null);
   }

   public static void recoverMethod_b_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(3, true, var0, var1, null);
   }

   public static void recoverMethod_c_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(5, true, var0, var1, null);
   }

   public static void recoverMethod_d_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(5, false, var0, var1, null);
   }

   public static void recoverMethod_e_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(6, true, var0, var1, null);
   }

   public static void recoverMethod_f_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(6, false, var0, var1, null);
   }
}
