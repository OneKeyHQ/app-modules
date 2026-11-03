package cn.jiguang.bk;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab52b;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2b07c2 {
   private static boolean recoverField_a = true;
   private static Boolean recoverField_b;
   private static volatile boolean recoverField_c = false;

   public static boolean recoverMethod_a_9b79() {
      return recoverField_a;
   }

   public static boolean recoverMethod_b_9b79() {
      return recoverField_c;
   }

   public static void recoverMethod_a_25decb5(boolean var0, boolean var1) {
      recoverField_c = var1;
      recoverField_a = var0;
   }

   public static void recoverMethod_a_f0554df3(final Context var0) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", " sync status");
      RecoverTypeaf2b0403.recoverMethod_a_63b8aba6(
         "DELAY_TASK",
         new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  long var1 = System.currentTimeMillis();
                  long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_J_48794abf());
                  if (Math.abs(var1 - var3) < 432000000L) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "sync status no need report");
                     return;
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "start sync status");
                  JSONObject var5 = new JSONObject();
                  JSONArray var6 = new JSONArray();
                  JSONObject var7 = new JSONObject();
                  JSONArray var8 = new JSONArray();
                  var8.put(System.currentTimeMillis());
                  var8.put(System.currentTimeMillis());
                  var7.put("duration", var8);
                  var7.put("c_type", "set_auth");
                  JSONArray var9 = new JSONArray();
                  JSONObject var10 = new JSONObject();
                  var10.put("result", "" + (RecoverTypeaf2b07c2.recoverField_c ? 0 : 1));
                  var10.put("s_type", "set_auth");
                  var9.put(var10);
                  var7.put("statistics", var9);
                  var6.put(var7);
                  var5.put("slice_index", 1);
                  var5.put("slice_count", 1);
                  var5.put("data", var6);
                  var5.put("sequence", UUID.randomUUID().toString());
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "reportJSON=" + var5);
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(
                     var0, RecoverType5a690d1.recoverMethod_J_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                  );
                  RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var0, var5, "collect_statistic");
               } catch (Throwable var11) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "sync status  throwable=" + var11);
               }
            }
         },
         8000
      );
   }

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "enableAutoWakeup status: " + var1);
      recoverMethod_d_1a552341(var0, var1);
      RecoverTypeaf2ab52b.recoverMethod_a_f0554df3(var0);
      Bundle var2 = new Bundle();
      var2.putBoolean("enable_auto_wakeup", var1);
      RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "a8", var2);
   }

   public static void recoverMethod_b_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "enableipv6 priority status: " + var1);
      recoverMethod_f_1a552341(var0, var1);
      Bundle var2 = new Bundle();
      var2.putBoolean("enable_ci_priority", var1);
      RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "a11", var2);
   }

   public static void recoverMethod_c_1a552341(Context var0, boolean var1) {
   }

   public static void recoverMethod_d_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "enableAutoWakeup config status: " + var1);
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_f_48794abf().recoverMethod_a_4e9722a3(var1));
   }

   public static void recoverMethod_e_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "setDynamicLoad config enable: " + var1);
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_h_48794abf().recoverMethod_a_4e9722a3(var1));
   }

   public static void recoverMethod_f_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "IPV6First config enable: " + var1);
      recoverField_b = var1;
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_g_48794abf().recoverMethod_a_4e9722a3(var1));
   }

   public static void recoverMethod_g_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AuthUtils", "setAppTerminate config enable: " + var1);
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_i_48794abf().recoverMethod_a_4e9722a3(var1));
   }

   public static boolean recoverMethod_b_f0554df7(Context var0) {
      try {
         return (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_f_48794abf());
      } catch (Throwable var2) {
         return true;
      }
   }

   public static boolean recoverMethod_c_f0554df7(Context var0) {
      try {
         return (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_i_48794abf());
      } catch (Throwable var2) {
         return true;
      }
   }

   public static boolean recoverMethod_d_f0554df7(Context var0) {
      try {
         if (recoverField_b == null) {
            recoverField_b = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_g_48794abf());
         }

         return recoverField_b;
      } catch (Throwable var2) {
         return false;
      }
   }
}
