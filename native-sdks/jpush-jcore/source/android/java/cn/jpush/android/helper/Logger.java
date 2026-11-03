package cn.jpush.android.helper;

import cn.jiguang.api.JCoreManager;

public class Logger {
   private static String recoverField_a = "[key-step]";

   private static void recoverMethod_a_996fb7bb(int var0, boolean var1, String var2, String var3, Throwable var4) {
      JCoreManager.onEvent(null, "JPush", 18, var2, null, var0, var1, var3, var4);
   }

   public static void recoverMethod_v_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(2, true, var0, var1, null);
   }

   public static void recoverMethod_vv_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(2, false, var0, var1, null);
   }

   public static void recoverMethod_kv_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(2, false, var0, recoverField_a + var1, null);
   }

   public static void recoverMethod_d_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(3, true, var0, var1, null);
   }

   public static void recoverMethod_dd_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(3, false, var0, var1, null);
   }

   public static void recoverMethod_kd_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(3, false, var0, recoverField_a + var1, null);
   }

   public static void recoverMethod_i_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(4, true, var0, var1, null);
   }

   public static void recoverMethod_ii_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(4, false, var0, var1, null);
   }

   public static void recoverMethod_ki_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(4, false, var0, recoverField_a + var1, null);
   }

   public static void recoverMethod_w_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(5, true, var0, var1, null);
   }

   public static void recoverMethod_ww_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(5, false, var0, var1, null);
   }

   public static void recoverMethod_kw_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(5, false, var0, recoverField_a + var1, null);
   }

   public static void recoverMethod_e_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(6, true, var0, var1, null);
   }

   public static void recoverMethod_ee_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(6, false, var0, var1, null);
   }

   public static void recoverMethod_ke_e925fa09(String var0, String var1) {
      recoverMethod_a_996fb7bb(6, false, var0, recoverField_a + var1, null);
   }

   public static void recoverMethod_ww_1765c98c(String var0, String var1, Throwable var2) {
      recoverMethod_a_996fb7bb(5, false, var0, var1, var2);
   }

   public static void recoverMethod_kw_1765c98c(String var0, String var1, Throwable var2) {
      recoverMethod_a_996fb7bb(5, false, var0, recoverField_a + var1, var2);
   }

   public static void recoverMethod_e_1765c98c(String var0, String var1, Throwable var2) {
      recoverMethod_a_996fb7bb(6, true, var0, var1, var2);
   }

   public static void recoverMethod_ee_1765c98c(String var0, String var1, Throwable var2) {
      recoverMethod_a_996fb7bb(6, false, var0, var1, var2);
   }

   public static void recoverMethod_ke_1765c98c(String var0, String var1, Throwable var2) {
      recoverMethod_a_996fb7bb(6, false, var0, recoverField_a + var1, var2);
   }
}
