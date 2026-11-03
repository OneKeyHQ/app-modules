package cn.jpush.android.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5af0;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.helper.Logger;

public class RecoverTypea2146d45 {
   private static SharedPreferences recoverField_a;

   public static void recoverMethod_a_f0554df3(Context var0) {
      recoverField_a = var0.getSharedPreferences("cn.jpush.config", 0);
   }

   private static SharedPreferences recoverMethod_v_c831c207(Context var0) {
      if (recoverField_a == null) {
         recoverMethod_a_f0554df3(var0);
      }

      return recoverField_a;
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      recoverMethod_v_c831c207(var0).edit().putInt("service_stoped", var1).apply();
   }

   public static int recoverMethod_b_f0554de6(Context var0) {
      SharedPreferences var1 = RecoverTypea0793a19.reload(var0, "cn.jpush.config");
      if (var1 == null) {
         var1 = recoverMethod_v_c831c207(var0);
      }

      int var2 = var1.getInt("service_stoped", -1);
      if (var2 < 0) {
         var2 = var0.getSharedPreferences("cn.jpush.android.user.profile", 0).getInt("service_stoped", 0);
         recoverMethod_a_1a54e370(var0, var2);
      }

      return var2;
   }

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("ups_state", var1).apply();
   }

   public static boolean recoverMethod_c_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("ups_state", true);
   }

   public static void recoverMethod_b_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("tcp_state", var1).apply();
   }

   public static boolean recoverMethod_d_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("tcp_state", false);
   }

   public static boolean recoverMethod_e_f0554df7(Context var0) {
      if (recoverMethod_b_f0554de6(var0) >= 1 || recoverMethod_d_f0554df7(var0)) {
         Logger.recoverMethod_dd_e925fa09(
            "PushConfig", "The service is stopped, it will give up all the actions until you call resumePush method to resume the service."
         );
         return true;
      } else if (!recoverMethod_c_f0554df7(var0)) {
         Logger.recoverMethod_dd_e925fa09(
            "PushConfig", "The service is stopped, it will give up all the actions until you call JPushUPSManager.trunOn method to resume the service."
         );
         return true;
      } else if (!(Boolean)RecoverTypea0793a19.get(var0, Key.UPSRegister())) {
         Logger.recoverMethod_dd_e925fa09(
            "PushConfig", "The service is stopped, it will give up all the actions until you call JPushUPSManager.registerToken method to resume the service."
         );
         return true;
      } else {
         return false;
      }
   }

   public static boolean recoverMethod_b_1a54e374(Context var0, int var1) {
      if (var1 != 0 && recoverMethod_b_f0554de6(var0) >= 1) {
         Logger.recoverMethod_ww_e925fa09(
            "PushConfig", "The service is stopped, it will give up all the actions until you call resumePush method to resume the service."
         );
         return true;
      } else if (var1 != 1 && !recoverMethod_c_f0554df7(var0)) {
         Logger.recoverMethod_ww_e925fa09(
            "PushConfig", "The service is stopped, it will give up all the actions until you call JPushUPSManager.trunOn method to resume the service."
         );
         return true;
      } else if (var1 != 2 && !(Boolean)RecoverTypea0793a19.get(var0, Key.UPSRegister())) {
         Logger.recoverMethod_ww_e925fa09(
            "PushConfig", "The service is stopped, it will give up all the actions until you call JPushUPSManager.registerToken method to resume the service."
         );
         return true;
      } else {
         return false;
      }
   }

   public static String recoverMethod_f_e501eb0d(Context var0) {
      String var1 = recoverMethod_v_c831c207(var0).getString("mobile_number", null);
      if (!TextUtils.isEmpty(var1)) {
         var1 = RecoverType9b4c5af0.recoverMethod_b_8ea82db7(var1, RecoverType9b4c5af2.recoverField_a);
      }

      return var1;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      if (!TextUtils.isEmpty(var1)) {
         var1 = RecoverType9b4c5af0.recoverMethod_a_8ea82db7(var1, RecoverType9b4c5af2.recoverField_a);
      }

      recoverMethod_v_c831c207(var0).edit().putString("mobile_number", var1).apply();
   }

   public static void recoverMethod_c_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("notification_enabled", var1).apply();
   }

   public static boolean recoverMethod_g_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("notification_enabled", true);
   }

   public static String recoverMethod_h_e501eb0d(Context var0) {
      return recoverMethod_v_c831c207(var0).getString("setting_push_time", "");
   }

   public static void recoverMethod_b_5ffc00fd(Context var0, String var1) {
      recoverMethod_v_c831c207(var0).edit().putString("setting_push_time", var1).apply();
   }

   public static String recoverMethod_i_e501eb0d(Context var0) {
      return recoverMethod_v_c831c207(var0).getString("setting_silence_push_time", "");
   }

   public static void recoverMethod_c_5ffc00fd(Context var0, String var1) {
      recoverMethod_v_c831c207(var0).edit().putString("setting_silence_push_time", var1).apply();
   }

   public static void recoverMethod_c_1a54e370(Context var0, int var1) {
      recoverMethod_v_c831c207(var0).edit().putInt("notification_num", var1).apply();
   }

   public static int recoverMethod_j_f0554de6(Context var0) {
      return recoverMethod_v_c831c207(var0).getInt("notification_num", JPushInterface.DEFAULT_NOTIFICATION_NUM);
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      if (var1 < 0L) {
         Logger.recoverMethod_w_e925fa09("PushConfig", "freeze end time was incorrect!");
      } else {
         if (var1 > 1800L) {
            Logger.recoverMethod_i_e925fa09("PushConfig", "freeze end time was greate than half an hour");
            var1 = 1800L;
         }

         recoverMethod_v_c831c207(var0).edit().putLong("TAFreezeSetTime", System.currentTimeMillis()).putLong("TAFreezeEndTime", var1 * 1000L).apply();
      }
   }

   public static boolean recoverMethod_k_f0554df7(Context var0) {
      long var1 = recoverMethod_v_c831c207(var0).getLong("TAFreezeEndTime", -1L);
      if (var1 > 1800L) {
         var1 = 0L;
      }

      long var3 = recoverMethod_v_c831c207(var0).getLong("TAFreezeSetTime", -1L);
      if (var1 != -1L && var3 != -1L) {
         if (System.currentTimeMillis() - var3 >= 0L && System.currentTimeMillis() - var3 <= var1) {
            return false;
         }

         recoverMethod_v_c831c207(var0).edit().putLong("TAFreezeSetTime", -1L).putLong("TAFreezeEndTime", -1L).apply();
         Logger.recoverMethod_w_e925fa09("PushConfig", "incorrect timestamp");
         return true;
      } else {
         return false;
      }
   }

   public static void recoverMethod_a_9d23c587(Context var0, String var1, String var2) {
      recoverMethod_v_c831c207(var0).edit().putString("jpush_save_custom_builder" + var1, var2).apply();
   }

   public static String recoverMethod_d_a4ea6d43(Context var0, String var1) {
      String var2 = recoverMethod_v_c831c207(var0).getString("jpush_save_custom_builder" + var1, "");
      if (TextUtils.isEmpty(var2)) {
         var2 = var0.getSharedPreferences("cn.jpush.android.user.profile", 0).getString("jpush_save_custom_builder" + var1, "");
         recoverMethod_a_9d23c587(var0, var1, var2);
      }

      return var2;
   }

   public static void recoverMethod_b_1a54e731(Context var0, long var1) {
      recoverMethod_v_c831c207(var0).edit().putLong("geofence_interval", var1).apply();
   }

   public static long recoverMethod_c_1a54e725(Context var0, long var1) {
      long var3 = recoverMethod_v_c831c207(var0).getLong("geofence_interval", -1L);
      if (var3 == -1L) {
         var3 = var0.getSharedPreferences("cn.jpush.android.user.profile", 0).getLong("geofence_interval", -1L);
         if (var3 != -1L) {
            recoverMethod_b_1a54e731(var0, var3);
         }
      }

      if (var3 == -1L) {
         var3 = var1;
      }

      return var3;
   }

   public static void recoverMethod_d_1a54e370(Context var0, int var1) {
      recoverMethod_v_c831c207(var0).edit().putInt("geofence_max_num", var1).apply();
   }

   public static int recoverMethod_e_1a54e363(Context var0, int var1) {
      int var2 = recoverMethod_v_c831c207(var0).getInt("geofence_max_num", -1);
      if (var2 == -1) {
         var2 = var0.getSharedPreferences("cn.jpush.android.user.profile", 0).getInt("geofence_max_num", -1);
         if (var2 != -1) {
            recoverMethod_d_1a54e370(var0, var2);
         }
      }

      if (var2 == -1) {
         var2 = var1;
      }

      return var2;
   }

   public static void recoverMethod_d_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("geofence_pull_enable", var1).apply();
   }

   public static boolean recoverMethod_l_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("geofence_pull_enable", false);
   }

   public static void recoverMethod_e_5ffc00fd(Context var0, String var1) {
      recoverMethod_v_c831c207(var0).edit().putString("geofence_pull_limit", var1).apply();
   }

   public static String recoverMethod_m_e501eb0d(Context var0) {
      return recoverMethod_v_c831c207(var0).getString("geofence_pull_limit", "");
   }

   public static void recoverMethod_f_5ffc00fd(Context var0, String var1) {
      recoverMethod_v_c831c207(var0).edit().putString("geofence_last_pull_state", var1).apply();
   }

   public static String recoverMethod_n_e501eb0d(Context var0) {
      return recoverMethod_v_c831c207(var0).getString("geofence_last_pull_state", "");
   }

   public static void recoverMethod_e_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("push_geofence_enable", var1).apply();
   }

   public static boolean recoverMethod_o_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("push_geofence_enable", true);
   }

   public static void recoverMethod_f_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("push_link_enable", var1).apply();
   }

   public static boolean recoverMethod_p_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("push_link_enable", true);
   }

   public static void recoverMethod_g_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("push_data_insights_enable", var1).apply();
   }

   public static boolean recoverMethod_q_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("push_data_insights_enable", true);
   }

   public static void recoverMethod_h_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("push_notification_callback_enable", var1).apply();
   }

   public static boolean recoverMethod_r_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("push_notification_callback_enable", false);
   }

   public static void recoverMethod_i_1a552341(Context var0, boolean var1) {
      recoverMethod_v_c831c207(var0).edit().putBoolean("push_smart_enable", var1).apply();
   }

   public static boolean recoverMethod_s_f0554df7(Context var0) {
      return recoverMethod_v_c831c207(var0).getBoolean("push_smart_enable", true);
   }

   public static void recoverMethod_d_1a54e731(Context var0, long var1) {
      recoverMethod_v_c831c207(var0).edit().putLong("p_last_time", var1).apply();
   }

   public static long recoverMethod_t_f0554de7(Context var0) {
      return recoverMethod_v_c831c207(var0).getLong("p_last_time", 0L);
   }

   public static void recoverMethod_f_1a54e370(Context var0, int var1) {
      RecoverTypea0793a19.set(var0, Key.registerType().set(var1));
   }

   public static int recoverMethod_u_f0554de6(Context var0) {
      return (Integer)RecoverTypea0793a19.get(var0, Key.registerType());
   }

   public static void recoverMethod_b_9d23c587(Context var0, String var1, String var2) {
      RecoverTypea0793a19.set(var0, Key.JAppKey(var1).set(var2));
   }

   public static String recoverMethod_g_a4ea6d43(Context var0, String var1) {
      return (String)RecoverTypea0793a19.get(var0, Key.JAppKey(var1));
   }

   public static void recoverMethod_c_9d23c587(Context var0, String var1, String var2) {
      RecoverTypea0793a19.set(var0, Key.MAppKey(var1).set(var2));
   }

   public static String recoverMethod_h_a4ea6d43(Context var0, String var1) {
      return (String)RecoverTypea0793a19.get(var0, Key.MAppKey(var1));
   }

   public static void recoverMethod_d_9d23c587(Context var0, String var1, String var2) {
      RecoverTypea0793a19.set(var0, Key.MAppId(var1).set(var2));
   }

   public static String recoverMethod_i_a4ea6d43(Context var0, String var1) {
      return (String)RecoverTypea0793a19.get(var0, Key.MAppId(var1));
   }

   public static void recoverMethod_e_9d23c587(Context var0, String var1, String var2) {
      RecoverTypea0793a19.set(var0, Key.MAppSecret(var1).set(var2));
   }

   public static String recoverMethod_j_a4ea6d43(Context var0, String var1) {
      return (String)RecoverTypea0793a19.get(var0, Key.MAppSecret(var1));
   }
}
