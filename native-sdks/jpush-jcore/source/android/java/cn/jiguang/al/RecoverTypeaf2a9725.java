package cn.jiguang.al;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.concurrent.atomic.AtomicInteger;

public class RecoverTypeaf2a9725 {
   private static boolean recoverField_a = false;
   private static boolean recoverField_b = false;
   private static boolean recoverField_c = false;
   private static boolean recoverField_d = false;
   private static boolean recoverField_e = false;
   private static boolean recoverField_f = false;
   private static boolean recoverField_g = false;
   private static boolean recoverField_h = false;
   private static AtomicInteger recoverField_i = new AtomicInteger(0);

   private RecoverTypeaf2a9725() {
      recoverField_a = recoverMethod_h_9b79();
      recoverField_b = recoverMethod_i_9b79();
      recoverField_c = recoverMethod_j_9b79();
      recoverField_d = recoverMethod_k_9b79();
      recoverField_e = recoverMethod_l_9b79();
      recoverField_f = recoverMethod_m_9b79();
      recoverField_g = recoverMethod_n_9b79();
      recoverField_h = recoverMethod_o_9b79();
   }

   public static RecoverTypeaf2a9725 recoverMethod_a_c692b321() {
      return RecoverTypeaf2a9725.Member8ee166a2.recoverField_a;
   }

   public static boolean recoverMethod_b_9b79() {
      return !recoverField_e
         ? false
         : !recoverField_a && !recoverField_b && !recoverField_c && !recoverField_d && !recoverField_f && !recoverField_g && !recoverField_h;
   }

   public static boolean recoverMethod_c_9b79() {
      return !recoverField_a
         ? false
         : !recoverField_e && !recoverField_b && !recoverField_c && !recoverField_d && !recoverField_f && !recoverField_g && !recoverField_h;
   }

   public static boolean recoverMethod_d_9b79() {
      return recoverField_a;
   }

   public static boolean recoverMethod_e_9b79() {
      return !recoverField_h
         ? false
         : !recoverField_e && !recoverField_b && !recoverField_c && !recoverField_d && !recoverField_f && !recoverField_g && !recoverField_a;
   }

   private static boolean recoverMethod_h_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 1) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p jpush init");
            return recoverField_a;
         }

         var0 = recoverField_i.addAndGet(1);
         Class var2 = Class.forName("cn.jpush.android.api.JPushInterface");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJpushSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJpushSDK:" + var1 + ", mPluginInitState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_i_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 2) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p jmessage init");
            return recoverField_b;
         }

         var0 = recoverField_i.addAndGet(2);
         Class var2 = Class.forName("cn.jpush.im.android.api.JMessageClient");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJMessageSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJMessageSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_j_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 4) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p janalytics init");
            return recoverField_c;
         }

         var0 = recoverField_i.addAndGet(4);
         Class var2 = Class.forName("cn.jiguang.analytics.android.api.JAnalyticsInterface");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJanalyticsSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJanalyticsSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_k_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 8) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p jshare init");
            return recoverField_d;
         }

         var0 = recoverField_i.addAndGet(8);
         Class var2 = Class.forName("cn.jiguang.share.android.api.JShareInterface");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJshareSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJshareSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_l_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 64) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p jverification init");
            return recoverField_e;
         }

         var0 = recoverField_i.addAndGet(64);
         Class var2 = Class.forName("cn.jiguang.verifysdk.api.JVerificationInterface");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJVerificationSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJVerificationSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_m_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 128) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p jmlink init");
            return recoverField_f;
         }

         var0 = recoverField_i.addAndGet(128);
         Class var2 = Class.forName("cn.jiguang.jmlinksdk.core.JMlinkInterfaceImpl");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJMLinkSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJMLinkSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_n_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 256) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p junion init");
            return recoverField_g;
         }

         var0 = recoverField_i.addAndGet(256);
         Class var2 = Class.forName("cn.jiguang.junion.JUnionInterface");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJUnionSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJUnionSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   private static boolean recoverMethod_o_9b79() {
      int var0 = 0;
      boolean var1 = false;

      try {
         if ((recoverField_i.intValue() & 2048) != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "p joperate init");
            return recoverField_h;
         }

         var0 = recoverField_i.addAndGet(2048);
         Class var2 = Class.forName("cn.jiguang.joperate.api.JOperateInterface");
         var1 = true;
      } catch (ClassNotFoundException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJOperateSDK:" + var3.getMessage());
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JClientsHelper", "isPluginJOperateSDK:" + var1 + ", pluginState: " + var0);
      return var1;
   }

   public boolean recoverMethod_f_9b79() {
      return recoverField_b;
   }

   public boolean recoverMethod_g_9b79() {
      return recoverField_a;
   }

   public boolean recoverMethod_a_f0554df7(Context var1) {
      boolean var2 = recoverField_b || recoverField_a;
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JClientsHelper", "isNeedKeepTcpConnect:" + var2);
      return var2;
   }

   private static final class Member8ee166a2 {
      private static final RecoverTypeaf2a9725 recoverField_a = new RecoverTypeaf2a9725();
   }
}
