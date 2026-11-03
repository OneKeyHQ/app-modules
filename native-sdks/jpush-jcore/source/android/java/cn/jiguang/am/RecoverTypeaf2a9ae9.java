package cn.jiguang.am;

import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RecoverTypeaf2a9ae9 {
   private static Map recoverField_a = new HashMap();
   private static long recoverField_b = 0L;
   private static String recoverField_c = "[key-step]";

   public static void recoverMethod_a_6ef37c42(int var0, String var1, String var2) {
      try {
         if (recoverField_b > 102400L) {
            Log.d("debug_internal", "log cache over =102400 , clean it");
            recoverField_a.clear();
            recoverField_b = 0L;
         }

         if (!recoverField_a.containsKey(var0)) {
            recoverField_a.put(var0, new HashSet());
         }

         Set var3 = (Set)recoverField_a.get(var0);
         boolean var4 = var3.contains(var2);
         if (!var4) {
            recoverMethod_c_e925fa09(var1, var2);
            recoverField_b = recoverField_b + var2.length();
            recoverField_b = recoverField_b + String.valueOf(var0).length();
            var3.add(var2);
         }
      } catch (Throwable var5) {
      }
   }

   public static void recoverMethod_a_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 2, var1, null);
   }

   public static void recoverMethod_b_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 2, var1, null);
   }

   public static void recoverMethod_c_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 3, var1, null);
   }

   public static void recoverMethod_d_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 3, var1, null);
   }

   public static void recoverMethod_e_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 3, recoverField_c + var1, null);
   }

   public static void recoverMethod_f_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 4, var1, null);
   }

   public static void recoverMethod_g_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 4, var1, null);
   }

   public static void recoverMethod_h_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 4, recoverField_c + var1, null);
   }

   public static void recoverMethod_i_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 5, var1, null);
   }

   public static void recoverMethod_j_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 5, var1, null);
   }

   public static void recoverMethod_k_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 5, recoverField_c + var1, null);
   }

   public static void recoverMethod_l_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 6, var1, null);
   }

   public static void recoverMethod_m_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 6, var1, null);
   }

   public static void recoverMethod_n_e925fa09(String var0, String var1) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 6, recoverField_c + var1, null);
   }

   public static void recoverMethod_a_1765c98c(String var0, String var1, Throwable var2) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 3, var1, var2);
   }

   public static void recoverMethod_b_1765c98c(String var0, String var1, Throwable var2) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 5, var1, var2);
   }

   public static void recoverMethod_c_1765c98c(String var0, String var1, Throwable var2) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 5, var1, var2);
   }

   public static void recoverMethod_d_1765c98c(String var0, String var1, Throwable var2) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, true, 6, var1, var2);
   }

   public static void recoverMethod_e_1765c98c(String var0, String var1, Throwable var2) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3("JCore", var0, false, 6, var1, var2);
   }

   public static void recoverMethod_f_1765c98c(String var0, String var1, Throwable var2) {
      recoverMethod_e_1765c98c(var0, var1, var2);
   }
}
