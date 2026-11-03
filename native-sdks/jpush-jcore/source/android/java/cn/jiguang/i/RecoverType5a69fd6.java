package cn.jiguang.i;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("CommitPrefEdits")
public class RecoverType5a69fd6 {
   private static List recoverField_a = new ArrayList();
   private static SharedPreferences recoverField_b;
   private static HashMap recoverField_c;

   private static void recoverMethod_k_f0554df3(Context var0) {
      recoverField_b = var0.getSharedPreferences("cn.jiguang.common", 0);
      recoverMethod_a_9b75();
   }

   private static void recoverMethod_a_9b75() {
      recoverField_c = new HashMap();
      recoverField_c.put(recoverMethod_a_8ea82db7("JDataConfigManager", "_bi"), 86400000L);
      recoverField_c.put(recoverMethod_a_8ea82db7("JWakeReport", "_ri"), 3600000L);
   }

   private static SharedPreferences recoverMethod_l_c831c207(Context var0) {
      if (recoverField_b == null) {
         recoverMethod_k_f0554df3(var0);
      }

      return recoverField_b;
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      recoverMethod_l_c831c207(var0).edit().clear().apply();
   }

   public static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      long var2 = System.currentTimeMillis();
      long var4 = recoverMethod_e_5ffc00f1(var0, var1);
      long var6 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_b_aad3b1f3(var1);
      boolean var8 = var2 - var4 > var6;
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(
         "JCommonConfig", "is " + var1 + " businessTime:" + var8 + ",curTime:" + var2 + ",lastBusinessTime:" + var4 + ",businessInterval:" + var6
      );
      return var8;
   }

   public static boolean recoverMethod_b_5ffc0101(Context var0, String var1) {
      long var2 = System.currentTimeMillis();
      long var4 = recoverMethod_g_5ffc00f1(var0, var1);
      long var6 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_aad3b1f3(var1);
      boolean var8 = var2 - var4 > var6;
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(
         "JCommonConfig", "is " + var1 + " reportTime:" + var8 + ",curTime:" + var2 + ",lastReportTime:" + var4 + ",reportInterval:" + var6
      );
      return var8;
   }

   public static boolean recoverMethod_c_5ffc0101(Context var0, String var1) {
      long var2 = System.currentTimeMillis();
      long var4 = recoverMethod_g_5ffc00f1(var0, var1);
      long var6 = recoverMethod_h_5ffc00f1(var0, var1);
      boolean var8 = var2 - var4 > var6;
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(
         "JCommonConfig", "is " + var1 + " reportTime:" + var8 + ",curTime:" + var2 + ",lastReportTime:" + var4 + ",reportInterval:" + var6
      );
      return var8;
   }

   public static void recoverMethod_d_5ffc00fd(Context var0, String var1) {
      long var2 = System.currentTimeMillis();
      recoverMethod_a_9f849567(var0, var1, var2);
   }

   public static void recoverMethod_a_9f849567(Context var0, String var1, long var2) {
      String var4 = recoverMethod_a_8ea82db7(var1, "_blt");
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " lastBusinessTime:" + var2);
      recoverMethod_l_c831c207(var0).edit().putLong(var4, var2).apply();
   }

   public static long recoverMethod_e_5ffc00f1(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7(var1, "_blt");
      return recoverMethod_l_c831c207(var0).getLong(var2, 0L);
   }

   public static void recoverMethod_b_9f849567(Context var0, String var1, long var2) {
      if (var2 >= 0L) {
         if (!var1.contains("JLocation") || var2 <= recoverMethod_h_5ffc00f1(var0, "JLocationv2")) {
            String var4 = recoverMethod_a_8ea82db7(var1, "_bi");
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " businessInterval:" + var2);
            recoverMethod_l_c831c207(var0).edit().putLong(var4, var2).apply();
         }
      }
   }

   public static void recoverMethod_f_5ffc00fd(Context var0, String var1) {
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " lastReportTime");
      long var2 = System.currentTimeMillis();
      String var4 = recoverMethod_a_8ea82db7(var1, "_rlt");
      recoverMethod_l_c831c207(var0).edit().putLong(var4, var2).apply();
   }

   public static long recoverMethod_g_5ffc00f1(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7(var1, "_rlt");
      return recoverMethod_l_c831c207(var0).getLong(var2, 0L);
   }

   public static void recoverMethod_c_9f849567(Context var0, String var1, long var2) {
      if (var2 >= 0L) {
         String var4 = recoverMethod_a_8ea82db7(var1, "_ri");
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " reportInterval:" + var2);
         recoverMethod_l_c831c207(var0).edit().putLong(var4, var2).apply();
      }
   }

   public static long recoverMethod_h_5ffc00f1(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7(var1, "_ri");
      long var3 = recoverMethod_b_e925f9fd(var1, "_ri");
      return recoverMethod_l_c831c207(var0).getLong(var2, var3);
   }

   public static void recoverMethod_a_9f84d177(Context var0, String var1, boolean var2) {
      String var3 = recoverMethod_a_8ea82db7(var1, "_aue");
      recoverMethod_l_c831c207(var0).edit().putBoolean(var3, var2).apply();
   }

   public static boolean recoverMethod_i_5ffc0101(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7(var1, "_aue");
      return recoverMethod_l_c831c207(var0).getBoolean(var2, true);
   }

   private static String recoverMethod_a_8ea82db7(String var0, String var1) {
      return var0 + var1;
   }

   private static long recoverMethod_b_e925f9fd(String var0, String var1) {
      try {
         return (Long)recoverField_c.get(recoverMethod_a_8ea82db7(var0, var1));
      } catch (NullPointerException var3) {
         return 0L;
      }
   }

   private static String recoverMethod_m_e501eb0d(Context var0) {
      return recoverMethod_l_c831c207(var0).getString("current_time_key", null);
   }

   public static void recoverMethod_j_5ffc00fd(Context var0, String var1) {
      if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1)) {
         String var2 = recoverMethod_m_e501eb0d(var0);
         if (var2 != null && !var2.equals(var1)) {
            recoverMethod_l_c831c207(var0).edit().remove(var2).apply();
         }

         recoverMethod_l_c831c207(var0).edit().putString("current_time_key", var1).apply();
      }
   }

   public static int recoverMethod_k_5ffc00f0(Context var0, String var1) {
      return RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1) ? Integer.MAX_VALUE : recoverMethod_l_c831c207(var0).getInt(var1, 0);
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      String var1 = recoverMethod_m_e501eb0d(var0);
      int var2 = recoverMethod_l_c831c207(var0).getInt(var1, 0);
      recoverMethod_l_c831c207(var0).edit().putInt(var1, var2 + 1).apply();
   }

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      String var2 = recoverMethod_a_8ea82db7("user_wake", "_aue");
      recoverMethod_l_c831c207(var0).edit().putBoolean(var2, var1).apply();
   }

   public static boolean recoverMethod_c_f0554df7(Context var0) {
      String var1 = recoverMethod_a_8ea82db7("user_wake", "_aue");
      return recoverMethod_l_c831c207(var0).getBoolean(var1, true);
   }

   public static String recoverMethod_d_e501eb0d(Context var0) {
      return recoverMethod_l_c831c207(var0).getString("JWakeLocalState", "");
   }

   public static void recoverMethod_l_5ffc00fd(Context var0, String var1) {
      recoverMethod_l_c831c207(var0).edit().putString("JWakeLocalState", var1).apply();
   }

   public static void recoverMethod_a_9d23c587(Context var0, String var1, String var2) {
      String var3 = recoverMethod_a_8ea82db7("JType", var1);
      recoverMethod_l_c831c207(var0).edit().putString(var3, var2).apply();
   }

   public static String recoverMethod_m_a4ea6d43(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7("JType", var1);
      return recoverMethod_l_c831c207(var0).getString(var2, "-1,-1");
   }

   public static void recoverMethod_a_9f8491a6(Context var0, String var1, int var2) {
      String var3 = recoverMethod_a_8ea82db7("JType", "ktsv_" + var1);
      recoverMethod_l_c831c207(var0).edit().putInt(var3, var2).apply();
   }

   public static int recoverMethod_n_5ffc00f0(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7("JType", "ktsv_" + var1);
      return recoverMethod_l_c831c207(var0).getInt(var2, 0);
   }

   public static void recoverMethod_o_5ffc00fd(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7("JLocation", "info");
      recoverMethod_l_c831c207(var0).edit().putString(var2, var1).apply();
   }

   public static String recoverMethod_e_e501eb0d(Context var0) {
      String var1 = recoverMethod_a_8ea82db7("JLocation", "info");
      return recoverMethod_l_c831c207(var0).getString(var1, "");
   }

   public static void recoverMethod_p_5ffc00fd(Context var0, String var1) {
      String var2 = recoverMethod_a_8ea82db7("JLocation", "wifiSSID");
      recoverMethod_l_c831c207(var0).edit().putString(var2, var1).apply();
   }

   public static String recoverMethod_f_e501eb0d(Context var0) {
      String var1 = recoverMethod_a_8ea82db7("JLocation", "wifiSSID");
      return recoverMethod_l_c831c207(var0).getString(var1, "");
   }

   public static String recoverMethod_g_e501eb0d(Context var0) {
      return recoverMethod_l_c831c207(var0).getString("JNotificationState", "");
   }

   public static void recoverMethod_q_5ffc00fd(Context var0, String var1) {
      recoverMethod_l_c831c207(var0).edit().putString("JNotificationState", var1).apply();
   }

   public static String recoverMethod_h_e501eb0d(Context var0) {
      return recoverMethod_l_c831c207(var0).getString("JDevicesession", "");
   }

   public static void recoverMethod_r_5ffc00fd(Context var0, String var1) {
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update deviceSession");
      recoverMethod_l_c831c207(var0).edit().putString("JDevicesession", var1).apply();
   }

   public static void recoverMethod_s_5ffc00fd(Context var0, String var1) {
      recoverMethod_l_c831c207(var0).edit().putString("JDeviceIds", var1).apply();
   }

   public static String recoverMethod_i_e501eb0d(Context var0) {
      return recoverMethod_l_c831c207(var0).getString("JDeviceIds", "");
   }

   public static void recoverMethod_t_5ffc00fd(Context var0, String var1) {
      recoverMethod_l_c831c207(var0).edit().putString("lo_cf", var1).apply();
   }

   private static String recoverMethod_b_dd3b7328(int var0, String var1) {
      return var1 + var0;
   }

   public static long recoverMethod_a_1a54e364(Context var0, int var1) {
      String var2 = recoverMethod_b_dd3b7328(var1, "method_freq_time_");
      return recoverMethod_l_c831c207(var0).getLong(var2, 0L);
   }

   public static void recoverMethod_a_30480154(Context var0, int var1, long var2) {
      if (var2 >= 0L) {
         String var4 = recoverMethod_b_dd3b7328(var1, "method_freq_time_");
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " method time :" + var2);
         recoverMethod_l_c831c207(var0).edit().putLong(var4, var2).apply();
      }
   }

   public static long recoverMethod_b_1a54e364(Context var0, int var1) {
      String var2 = recoverMethod_b_dd3b7328(var1, "method_freq_ex_time_");
      return recoverMethod_l_c831c207(var0).getLong(var2, 0L);
   }

   public static void recoverMethod_b_30480154(Context var0, int var1, long var2) {
      if (var2 >= 0L) {
         String var4 = recoverMethod_b_dd3b7328(var1, "method_freq_ex_time_");
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " method time :" + var2);
         recoverMethod_l_c831c207(var0).edit().putLong(var4, var2).apply();
      }
   }

   private static String recoverMethod_c_47921032(int var0) {
      return recoverField_a.contains(var0) ? "method_freq_cache_new" : "method_freq_cache_";
   }

   public static String recoverMethod_a_3dcfabbc(Context var0, int var1, boolean var2) {
      String var3 = recoverMethod_b_dd3b7328(var1, recoverMethod_c_47921032(var1));
      String var4 = recoverMethod_l_c831c207(var0).getString(var3, "");
      String var5 = "";
      if (!TextUtils.isEmpty(var4)) {
         var5 = var2 ? RecoverType5a68d10.recoverMethod_c_e96ea081(var4) : var4;
      }

      return var5;
   }

   public static void recoverMethod_a_aaf0c8da(final Context var0, final int var1, final String var2, final boolean var3) {
      if (!TextUtils.isEmpty(var2)) {
         RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(new Runnable() {
            @Override
            public void run() {
               String var1x = RecoverType5a69fd6.recoverMethod_b_dd3b7328(var1, RecoverType5a69fd6.recoverMethod_c_47921032(var1));
               RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCommonConfig", "update " + var1 + " method cache :" + var2);
               String var2x = var2;
               if (var3) {
                  var2x = RecoverType5a68d10.recoverMethod_b_e96ea081(var2);
               }

               RecoverType5a69fd6.recoverMethod_l_c831c207(var0).edit().putString(var1x, var2x).apply();
            }
         });
      }
   }

   public static boolean recoverMethod_a_134632(int var0) {
      boolean var1 = !RecoverTypeaf2aa628.recoverField_b.get() && RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_c_134621(var0) == 0;
      RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var0, "JCommonConfig", "configId : " + var0 + ", isBackgroundBusiness : " + var1);
      return var1;
   }

   static {
      recoverField_a.add(2012);
      recoverField_a.add(1015);
      recoverField_a.add(2009);
      recoverField_a.add(2002);
      recoverField_a.add(2006);
      recoverField_a.add(2008);
      recoverField_a.add(2014);
      recoverField_a.add(1020);
   }
}
