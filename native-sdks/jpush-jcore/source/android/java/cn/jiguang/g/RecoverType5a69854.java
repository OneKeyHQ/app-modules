package cn.jiguang.g;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RecoverType5a69854 {
   public static ArrayList recoverField_a = new ArrayList();
   public static ArrayList recoverField_b = new ArrayList();
   public static ArrayList recoverField_c = new ArrayList();
   public static ArrayList recoverField_d = new ArrayList();
   public static ArrayList recoverField_e = new ArrayList();
   public static int recoverField_f = 0;
   public static int recoverField_g = 0;
   public static int recoverField_h = 0;
   private static boolean recoverField_i;
   private static boolean recoverField_j;
   private static boolean recoverField_k;
   private static AtomicInteger recoverField_l = new AtomicInteger(0);

   private static boolean recoverMethod_b_9b79() {
      boolean var0 = false;

      try {
         if ((recoverField_l.intValue() & 1) != 0) {
            return recoverField_i;
         }

         recoverField_l.addAndGet(1);
         Class.forName("cn.jpush.android.api.JPushInterface");
         var0 = true;
      } catch (ClassNotFoundException var2) {
      }

      return var0;
   }

   private static boolean recoverMethod_c_9b79() {
      boolean var0 = false;

      try {
         if ((recoverField_l.intValue() & 64) != 0) {
            return recoverField_k;
         }

         recoverField_l.addAndGet(64);
         Class.forName("cn.jiguang.verifysdk.api.JVerificationInterface");
         var0 = true;
      } catch (ClassNotFoundException var2) {
      }

      return var0;
   }

   private static boolean recoverMethod_d_9b79() {
      boolean var0 = false;

      try {
         if ((recoverField_l.intValue() & 2048) != 0) {
            return recoverField_j;
         }

         recoverField_l.addAndGet(2048);
         Class.forName("cn.jiguang.joperate.api.JOperateInterface");
         var0 = true;
      } catch (ClassNotFoundException var2) {
      }

      return var0;
   }

   public static synchronized void recoverMethod_a_9b75() {
      try {
         recoverField_d.clear();
         recoverField_e.clear();
         recoverField_i = recoverMethod_b_9b79();
         recoverField_j = recoverMethod_d_9b79();
         recoverField_k = recoverMethod_c_9b79();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JDataConfigManager",
            "updateCollectConfig isPluginJPus=" + recoverField_i + " isPluginJOperate=" + recoverField_j + " isPluginVerification=" + recoverField_k
         );
         if (recoverField_i) {
            recoverField_d.addAll(recoverField_a);
         }

         if (recoverField_j) {
            recoverField_d.addAll(recoverField_b);
         }

         if (recoverField_k) {
            recoverField_d.addAll(recoverField_c);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "updateCollectConfig default mDisableDatas=" + recoverField_e);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "updateCollectConfig default mEnableDatas=" + recoverField_d);
      } catch (Throwable var1) {
      }
   }

   public static synchronized void recoverMethod_a_b19c9f57(List var0, List var1) {
      try {
         if (var1 == null) {
            var1 = new ArrayList();
         }

         if (var0 == null) {
            var0 = new ArrayList();
         }

         HashSet var2 = new HashSet(var1);
         HashSet var3 = new HashSet(var0);
         if (recoverField_d.isEmpty() && recoverField_e.isEmpty()) {
            if (recoverField_i) {
               var2.addAll(recoverField_a);
            }

            if (recoverField_j) {
               var2.addAll(recoverField_b);
            }

            if (recoverField_k) {
               var2.addAll(recoverField_c);
            }
         } else {
            var2.addAll(recoverField_d);
            var3.addAll(recoverField_e);
         }

         recoverField_d.clear();
         recoverField_e.clear();

         for (Object recoveryElement_135 : var3) {
            int var5 = (int)recoveryElement_135;
            if (!var1.contains(var5)) {
               recoverField_e.add(var5);
            }
         }

         for (Object recoveryElement_141 : var2) {
            int var8 = (int)recoveryElement_141;
            if (!var0.contains(var8)) {
               recoverField_d.add(var8);
            }
         }
      } catch (Throwable var6) {
      }
   }

   public static synchronized boolean recoverMethod_a_5d527815(Object var0) {
      try {
         recoverField_f = -1;
         recoverField_g = -1;
         recoverField_h = -1;
         if (var0 instanceof List) {
            for (Object recoveryElement_156 : (List)var0) {
               int var3 = (int)recoveryElement_156;
               recoverMethod_a_13462e(var3);
            }
         } else {
            recoverMethod_a_13462e((Integer)var0);
         }

         if (recoverField_i && !recoverField_a.contains(recoverField_f)) {
            RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(10000, "JDataConfigManager", "isPluginJPush is true,not can control");
            return false;
         } else if (recoverField_j && !recoverField_b.contains(recoverField_g)) {
            RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(10000, "JDataConfigManager", "isIsPluginJOperate is true,not can control");
            return false;
         } else if (recoverField_k && !recoverField_c.contains(recoverField_h)) {
            RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(10000, "JDataConfigManager", "isPluginVerification is true,not can control");
            return false;
         } else {
            return true;
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "isUserCanControlData throwable=" + var4);
         return false;
      }
   }

   private static void recoverMethod_a_13462e(int var0) {
      String var1 = String.valueOf(var0);
      if (var1.startsWith(String.valueOf(1000).substring(0, 1))) {
         recoverField_f = var0;
      } else if (var1.startsWith(String.valueOf(2000).substring(0, 1))) {
         recoverField_g = var0;
      } else if (var1.startsWith(String.valueOf(3000).substring(0, 1))) {
         recoverField_h = var0;
      }
   }

   static {
      recoverField_b.add(2002);
      recoverField_b.add(2001);
      recoverField_b.add(2000);
      recoverField_b.add(2004);
      recoverField_b.add(2003);
      recoverField_b.add(2005);
      recoverField_b.add(2006);
      recoverField_b.add(2007);
      recoverField_b.add(2008);
      recoverField_b.add(2009);
      recoverField_b.add(2010);
      recoverField_b.add(2011);
      recoverField_b.add(2012);
      recoverField_b.add(2013);
      recoverField_a.add(1001);
      recoverField_a.add(1000);
      recoverField_a.add(1007);
      recoverField_a.add(1003);
      recoverField_a.add(1005);
      recoverField_a.add(1008);
      recoverField_a.add(1006);
      recoverField_a.add(1010);
      recoverField_a.add(1012);
      recoverField_a.add(1013);
      recoverField_a.add(1014);
      recoverField_c.add(3006);
      recoverField_c.add(3008);
      recoverField_c.add(3003);
      recoverField_c.add(3002);
      recoverField_c.add(3004);
      recoverField_c.add(3001);
      recoverField_c.add(3012);
      recoverField_c.add(3013);
      recoverField_c.add(3000);
      recoverField_c.add(3007);
      recoverField_c.add(3005);
      recoverField_c.add(3011);
      recoverField_c.add(3010);
      recoverField_c.add(3009);
      recoverField_c.add(3014);
      recoverField_c.add(3015);
      recoverField_c.add(3016);
      recoverField_c.add(3017);
   }
}
