package cn.jiguang.bf;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ba.RecoverTypeaf2ae238;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d1;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RecoverTypeaf2af4fd {
   public static JSONObject recoverMethod_a_16ce4761(Context var0) {
      try {
         JSONObject var1 = new JSONObject();
         Pair var2 = RecoverTypeaf2a9723.recoverMethod_d_49a4c97d(var0);
         var1.put("app_build", Long.toString((Long)var2.second));
         var1.put("app_identifier", RecoverTypeaf2b07cf.recoverMethod_a_e501eb0d(var0));
         var1.put("app_name", RecoverTypeaf2b07c1.recoverMethod_j_e501eb0d(var0));
         var1.put("app_start_time", RecoverType5a681cd.recoverField_h);
         var1.put("app_version", var2.first);
         return var1;
      } catch (JSONException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting App.", var3);
         return null;
      }
   }

   public static JSONObject recoverMethod_a_a7dbfe3() {
      try {
         JSONObject var0 = new JSONObject();
         var0.put("name", "sentry.java.android");
         var0.put("version", "5.0.1");
         JSONArray var1 = new JSONArray();
         JSONObject var2 = new JSONObject();
         var2.put("name", "maven:io.sentry:sentry");
         var2.put("version", "5.0.1");
         var1.put(var2);
         JSONObject var3 = new JSONObject();
         var3.put("name", "maven:io.sentry:sentry-android-core");
         var3.put("version", "5.0.1");
         var1.put(var3);
         var0.put("packages", var1);
         return var0;
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting SDK version.", var4);
         return null;
      }
   }

   public static JSONObject recoverMethod_b_16ce4761(Context var0) {
      try {
         JSONObject var1 = new JSONObject();
         String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         String var3 = RecoverType5a68d10.recoverMethod_b_e501eb0d(var0);
         var1.put("id", var2 + "|" + var3);
         return var1;
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting user.", var4);
         return null;
      }
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return "";
      } else if (var0.contains("cn.jpush.im")) {
         return "JMessage";
      } else if (var0.contains("cn.jiguang.analytics")) {
         return "JAnalysis";
      } else if (var0.contains("cn.jiguang.share")) {
         return "JShare";
      } else if (var0.contains("cn.jiguang.union.ads") || var0.contains("cn.jiguang.ads")) {
         return "JAd";
      } else if (var0.contains("cn.jiguang.verifysdk")) {
         return "JVerification";
      } else if (var0.contains("cn.jiguang.jmlinksdk")) {
         return "JMlink";
      } else if (var0.contains("cn.jiguang.junion")) {
         return "JUnion";
      } else if (var0.contains("cn.jiguang.portrait")) {
         return "JPortrait";
      } else if (var0.contains("cn.jpush.android") || var0.contains("cn.jpush")) {
         return "JPush";
      } else {
         return !var0.contains("cn.jiguang.common") && !var0.contains("cn.jiguang") ? "" : "JCore";
      }
   }

   private static String recoverMethod_b_73cf11cb() {
      return Build.CPU_ABI;
   }

   private static String recoverMethod_c_73cf11cb() {
      return Build.CPU_ABI2;
   }

   private static JSONArray recoverMethod_d_6d78e11b() {
      JSONArray var0 = new JSONArray();
      String[] var1;
      if (VERSION.SDK_INT >= 21) {
         var1 = Build.SUPPORTED_ABIS;
      } else {
         var1 = new String[]{recoverMethod_b_73cf11cb(), recoverMethod_c_73cf11cb()};
      }

      for (String var5 : var1) {
         var0.put(var5);
      }

      return var0;
   }

   private static Long recoverMethod_a_bd474454(MemoryInfo var0) {
      return VERSION.SDK_INT >= 16 ? var0.totalMem : Runtime.getRuntime().totalMemory();
   }

   private static MemoryInfo recoverMethod_e_ecd9d1ef(Context var0) {
      try {
         ActivityManager var1 = (ActivityManager)var0.getSystemService("activity");
         MemoryInfo var2 = new MemoryInfo();
         if (var1 != null) {
            var1.getMemoryInfo(var2);
            return var2;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryUtils", "Error getting MemoryInfo.");
            return null;
         }
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting MemoryInfo.", var3);
         return null;
      }
   }

   private static Intent recoverMethod_f_813b6dc0(Context var0) {
      return RecoverTypeaf2b07d1.recoverMethod_a_81560598(var0, null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), false);
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      try {
         String var1 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var0);
         if (!TextUtils.isEmpty(var1)) {
            return var1.split(" ", -1)[0];
         }
      } catch (Exception var2) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting device family.", var2);
      }

      return null;
   }

   private static Float recoverMethod_a_93c5e47b(Intent var0) {
      try {
         int var1 = var0.getIntExtra("level", -1);
         int var2 = var0.getIntExtra("scale", -1);
         if (var1 != -1 && var2 != -1) {
            float var3 = 100.0F;
            return (float)var1 / var2 * var3;
         } else {
            return null;
         }
      } catch (Exception var4) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting device battery level.", var4);
         return null;
      }
   }

   private static Boolean recoverMethod_b_b9a17d2f(Intent var0) {
      try {
         int var1 = var0.getIntExtra("plugged", -1);
         return var1 == 1 || var1 == 2;
      } catch (Exception var2) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting device charging state.", var2);
         return null;
      }
   }

   private static Float recoverMethod_c_93c5e47b(Intent var0) {
      try {
         int var1 = var0.getIntExtra("temperature", -1);
         if (var1 != -1) {
            return var1 / 10.0F;
         }
      } catch (Exception var2) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting battery temperature.", var2);
      }

      return null;
   }

   private static RecoverTypeaf2ae238.Member8ffb38f5 recoverMethod_h_43faeb8f(Context var0) {
      RecoverTypeaf2ae238.Member8ffb38f5 var1 = null;

      try {
         var1 = RecoverTypeaf2ae238.recoverMethod_a_336f6b0a(var0.getResources().getConfiguration().orientation);
         if (var1 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("SentryUtils", "No device orientation available (ORIENTATION_SQUARE|ORIENTATION_UNDEFINED)");
            return null;
         }
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting device orientation.", var3);
      }

      return var1;
   }

   private static Boolean recoverMethod_i_c7cfe8b0(Context var0) {
      try {
         boolean var1 = Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu");
         String var2 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_p_e501eb0d(var0);
         if (!TextUtils.isEmpty(var2)) {
            var1 = var1
               || var2.contains("sdk_google")
               || var2.contains("google_sdk")
               || var2.contains("sdk")
               || var2.contains("sdk_x86")
               || var2.contains("vbox86p")
               || var2.contains("emulator")
               || var2.contains("simulator");
         }

         String var3 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var0);
         if (!TextUtils.isEmpty(var3)) {
            var1 = var1 || var3.contains("Genymotion");
         }

         String var4 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var0);
         if (!TextUtils.isEmpty(var4)) {
            var1 = var1 || var4.contains("google_sdk") || var4.contains("Emulator") || var4.contains("Android SDK built for x86");
         }

         return var1;
      } catch (Exception var5) {
         RecoverTypeaf2a9ae9.recoverMethod_b_1765c98c("SentryUtils", "Error checking whether application is running in an emulator.", var5);
         return null;
      }
   }

   private static DisplayMetrics recoverMethod_j_f8f56096(Context var0) {
      try {
         return var0.getResources().getDisplayMetrics();
      } catch (Exception var2) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting DisplayMetrics.", var2);
         return null;
      }
   }

   public static JSONObject recoverMethod_c_16ce4761(Context var0) {
      try {
         JSONObject var1 = new JSONObject();
         if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1005)) {
            var1.put("archs", recoverMethod_d_6d78e11b());
         }

         var1.put("name", RecoverTypeaf2b07c1.recoverMethod_B_e501eb0d(var0));
         var1.put("manufacturer", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var0));
         var1.put("brand", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_v_e501eb0d(var0));
         var1.put("family", recoverMethod_g_e501eb0d(var0));
         var1.put("model", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var0));
         var1.put("model_id", Build.ID);
         Intent var2 = recoverMethod_f_813b6dc0(var0);
         if (var2 != null) {
            var1.put("battery_level", recoverMethod_a_93c5e47b(var2));
            var1.put("charging", recoverMethod_b_b9a17d2f(var2));
            var1.put("battery_temperature", recoverMethod_c_93c5e47b(var2));
         }

         var1.put("online", RecoverTypeaf2b07ce.recoverMethod_a_9b79());
         var1.put("orientation", recoverMethod_h_43faeb8f(var0));
         var1.put("simulator", recoverMethod_i_c7cfe8b0(var0));
         if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1016)) {
            MemoryInfo var7 = recoverMethod_e_ecd9d1ef(var0);
            if (var7 != null) {
               var1.put("memory_size", recoverMethod_a_bd474454(var7));
               var1.put("free_memory", var7.availMem);
               var1.put("low_memory", var7.lowMemory);
            }
         }

         String var8 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_k_e501eb0d(var0);
         if (!TextUtils.isEmpty(var8)) {
            String[] var3 = var8.split("\\*");

            try {
               var1.put("screen_width_pixels", var3[0]);
               var1.put("screen_height_pixels", var3[1]);
            } catch (Throwable var5) {
            }
         }

         DisplayMetrics var9 = recoverMethod_j_f8f56096(var0);
         if (var9 != null) {
            var1.put("screen_density", var9.density);
            var1.put("screen_dpi", var9.densityDpi);
         }

         var1.put("boot_time", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_A_e501eb0d(var0));
         var1.put("timezone", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_y_e501eb0d(var0));
         var1.put("id", RecoverType5a68d10.recoverMethod_i_e501eb0d(var0));
         var1.put("language", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_w_e501eb0d(var0));
         var1.put("connection_type", RecoverTypeaf2b07ce.recoverMethod_d_73cf11cb());
         return var1;
      } catch (JSONException var6) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting Device.", var6);
         return null;
      }
   }

   public static JSONObject recoverMethod_d_16ce4761(Context var0) {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("build", Build.DISPLAY);
         var1.put("kernel_version", RecoverTypeaf2b07c1.recoverMethod_g_73cf11cb());
         var1.put("name", "Android");
         var1.put("rooted", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_o_f0554df7(var0));
         var1.put("version", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_z_e501eb0d(var0));
         return var1;
      } catch (JSONException var2) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("SentryUtils", "Error getting OperatingSystem.", var2);
         return null;
      }
   }
}
