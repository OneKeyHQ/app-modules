package cn.jiguang.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;

public class MultiSpHelper {
   private static final String TAG = "MultiSpHelper";
   private static final String SP_FILE = "cn.jpush.android.user.profile";
   private static SharedPreferences sharedPref;

   private MultiSpHelper() {
   }

   private static void init(Context var0) {
      sharedPref = var0.getSharedPreferences("cn.jpush.android.user.profile", 0);
   }

   private static SharedPreferences getSp(Context var0) {
      if (sharedPref == null) {
         init(var0);
      }

      return sharedPref;
   }

   private static SharedPreferences reload(Context var0) {
      if (var0 != null) {
         if (VERSION.SDK_INT >= 11) {
            var0.getSharedPreferences("cn.jpush.android.user.profile", 4);
         }

         return var0.getSharedPreferences("cn.jpush.android.user.profile", 0);
      } else {
         return null;
      }
   }

   public static void commitString(Context var0, String var1, String var2) {
      getSp(var0).edit().putString(var1, var2).apply();
   }

   public static String getString(Context var0, String var1, String var2) {
      return getSp(var0).getString(var1, var2);
   }

   public static void commitLong(Context var0, String var1, long var2) {
      getSp(var0).edit().putLong(var1, var2).apply();
   }

   public static long getLong(Context var0, String var1, long var2) {
      return getSp(var0).getLong(var1, var2);
   }

   public static void commitInt(Context var0, String var1, int var2) {
      getSp(var0).edit().putInt(var1, var2).apply();
   }

   public static int getInt(Context var0, String var1, int var2) {
      if (var1.equals("jpush_register_code") || var1.equals("service_stoped")) {
         SharedPreferences var3 = reload(var0);
         if (var3 != null) {
            return var3.getInt(var1, var2);
         }
      }

      return getSp(var0).getInt(var1, var2);
   }

   public static void commitBoolean(Context var0, String var1, boolean var2) {
      getSp(var0).edit().putBoolean(var1, var2).apply();
   }

   public static boolean getBoolean(Context var0, String var1, boolean var2) {
      return getSp(var0).getBoolean(var1, var2);
   }
}
