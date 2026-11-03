package cn.jiguang.bg;

import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.internal.JNAction;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RecoverTypeaf2af8c1 {
   private static final AtomicBoolean recoverField_a = new AtomicBoolean(false);
   private static final AtomicBoolean recoverField_b = new AtomicBoolean(false);

   public static void recoverMethod_a_9b75() {
      try {
         if (!recoverField_b.compareAndSet(false, true)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "loaded, skip");
            return;
         }

         boolean var0 = RecoverTypeaf2af8c0.recoverMethod_e_9b79();
         recoverField_a.set(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "usage: " + var0);
         if (var0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "dy load on");
            RecoverTypeaf2af8bf.recoverMethod_a_c69e84c7().recoverMethod_a_aad3b203(RecoverTypeaf2af8bd.recoverField_g);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "load static");
            JNAction.load();
            recoverMethod_b_9b79();
         }
      } catch (Throwable var1) {
      }
   }

   public static void recoverMethod_a_b7d3a948(String var0, long var1, int var3, String var4) {
      boolean var5 = RecoverTypeaf2af8c0.recoverMethod_d_9b79();
      if (var5 || var3 == 168) {
         try {
            if (JNAction.isNativeLibraryLoaded) {
               JNAction.recoverMethod_s_b7d3a948(var0, var1, var3, var4);
            }
         } catch (Throwable var6) {
         }
      }
   }

   public static boolean recoverMethod_a_d9caeafa(File var0, String var1) {
      return false;
   }

   public static boolean recoverMethod_a_e925fa0d(String var0, String var1) {
      return false;
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      return null;
   }

   public static boolean recoverMethod_b_aad3b203(String var0) {
      return false;
   }

   public static boolean recoverMethod_b_9b79() {
      try {
         String var0 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
         long var1 = RecoverType5a68d10.recoverMethod_f_f0554de7(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
         int var3 = recoverMethod_d_9b68();
         if (!JNAction.isNativeLibraryLoaded) {
            return false;
         }

         JNAction.recoverMethod_i_44be02fe(var0, var1, var3);
         return true;
      } catch (Throwable var4) {
         return false;
      }
   }

   private static int recoverMethod_d_9b68() {
      if (RecoverTypeaf2a9725.recoverMethod_b_9b79()) {
         return 1;
      } else {
         return RecoverTypeaf2a9725.recoverMethod_d_9b79() ? 2 : 1;
      }
   }

   public static String recoverMethod_c_73cf11cb() {
      try {
         return !JNAction.isNativeLibraryLoaded ? "" : JNAction.recoverMethod_v_73cf11cb();
      } catch (Throwable var1) {
         return "";
      }
   }
}
