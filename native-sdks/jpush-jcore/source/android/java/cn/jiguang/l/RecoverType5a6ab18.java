package cn.jiguang.l;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.ay.RecoverTypeaf2ac7f0;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d2;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.j.RecoverType5a6a399;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6ab18 {
   private static String recoverField_b = "";
   private static String recoverField_c = "";
   private static final byte[] recoverField_d = new byte[]{123, 98, 112, 68, 0, 52, 110, 54};
   private static final byte[] recoverField_e = new byte[]{67, 84, 120, 124, 54};
   private static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{54});
   private static final String recoverField_g = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{62, 84, 65, 109, 105, 57, 117, 81, 84, 120});
   private static final String recoverField_h = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{31, 83, 96, 123, 98, 116, 93, 15, 124, 124, 117});
   private static final String recoverField_i = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{31, 83, 109, 103, 100, 112, 87, 69, 54, 109, 123, 100, 92, 65, 109, 109, 114}
   );
   private static final String recoverField_j = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{54, 123, 98, 126, 66, 65, 126, 109, 57, 116, 93, 85, 117, 105, 98, 116, 84, 15, 41}
   );
   private static final String recoverField_k = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{62, 84, 65, 109, 105, 57, 100, 67, 69, 107});
   private static final String recoverField_l = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{62, 93, 78, 109});
   private static final String recoverField_m = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{31, 83, 96, 123, 98, 116, 93, 15, 120, 120, 102});
   private static final String recoverField_n = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{62, 67, 89, 106, 124, 115, 124, 31, 80, 107, 97, 96, 60, 81, 80, 105}
   );
   private static final String recoverField_o = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{31, 83, 96, 123, 98, 116, 93, 15, 123, 97, 120});
   private static final String recoverField_p = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{31, 83, 96, 123, 98, 116, 93, 15, 108, 123, 100});
   private static final String recoverField_q = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{31, 65, 122, 107, 98});
   private static final String recoverField_r = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{62, 84, 69, 111});
   private static final String recoverField_s = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{54, 107, 121, 127, 86, 73, 126});
   private static final String recoverField_t = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{31, 68, 120, 124, 119});
   private static final String recoverField_u = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{39, 102, 99, 95, 68, 108, 107, 98});
   private static final String recoverField_v = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{62, 67, 89, 106});
   private static final String recoverField_w = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{54, 123, 111, 98, 68, 69, 116, 39, 112, 126, 94, 84, 106}
   );
   private static final String recoverField_x = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{31, 83, 96, 123, 98, 116, 93, 15, 127, 122, 119, 124, 85, 87, 118, 122, 125}
   );
   private static Map recoverField_y = new HashMap();
   private static List recoverField_z = new ArrayList();
   private static List recoverField_A = new ArrayList();
   private static SimpleDateFormat recoverField_B = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z");
   private static long recoverField_C = 31536000000L;
   private static String recoverField_D;
   private static final FileFilter recoverField_E = new FileFilter() {
      @Override
      public boolean accept(File var1) {
         String var2 = var1.getName();
         if (!var2.startsWith("cpu")) {
            return false;
         }

         for (int var3 = 3; var3 < var2.length(); var3++) {
            if (var2.charAt(var3) < '0' || var2.charAt(var3) > '9') {
               return false;
            }
         }

         return true;
      }
   };
   public static JSONArray recoverField_a = new JSONArray();

   public static String recoverMethod_a_e501eb0d(final Context var0) {
      Object var1 = RecoverType5a6a399.recoverMethod_a_344864ca(var0, 1020, true, false, new RecoverType5a6a399.Member368c2616() {
         @Override
         public Object recoverMethod_c_810c345d() {
            return RecoverType5a6ab18.recoverField_D;
         }

         @Override
         public void recoverMethod_b_5d527811(Object var1) {
            if (var1 instanceof String) {
               RecoverType5a6ab18.recoverField_D = (String)var1;
            }
         }

         @Override
         public Object recoverMethod_b_810c345d() {
            return RecoverType5a6ab18.recoverMethod_g_e501eb0d(var0);
         }
      });
      if (var1 instanceof String) {
         recoverField_D = (String)var1;
      }

      return recoverField_D;
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      Point var1 = new Point();
      if (var0 instanceof Activity) {
         Display var2 = ((Activity)var0).getWindowManager().getDefaultDisplay();
         if (VERSION.SDK_INT >= 31) {
            try {
               Object var3 = RecoverTypeaf2b07d2.recoverMethod_a_1564af74(((Activity)var0).getWindowManager(), "getMaximumWindowMetrics", null, null);
               Rect var4 = (Rect)RecoverTypeaf2b07d2.recoverMethod_a_1564af74(var3, "getBounds", null, null);
               var1.x = var4.right - var4.left;
               var1.y = var4.bottom - var4.top;
            } catch (Exception var7) {
            }
         } else if (VERSION.SDK_INT >= 17) {
            var2.getRealSize(var1);
         } else if (VERSION.SDK_INT >= 13) {
            var2.getSize(var1);
         } else {
            var1.x = var2.getWidth();
            var1.y = var2.getHeight();
         }
      }

      DisplayMetrics var8 = var0.getResources().getDisplayMetrics();
      double var5;
      double var9;
      if (var0 instanceof Activity) {
         var9 = Math.pow(var1.x / var8.xdpi, 2.0);
         var5 = Math.pow(var1.y / var8.ydpi, 2.0);
      } else {
         var9 = Math.pow(var8.widthPixels / var8.xdpi, 2.0);
         var5 = Math.pow(var8.heightPixels / var8.ydpi, 2.0);
      }

      return String.format(Locale.ENGLISH, "%.1f", Math.sqrt(var9 + var5));
   }

   public static JSONArray recoverMethod_b_5d5a71dd(Context var0) {
      return new JSONArray();
   }

   public static String recoverMethod_c_e501eb0d(Context var0) {
      String var1 = null;

      try {
         RecoverTypeaf2ac7f0 var2 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0);
         JSONObject var3 = new JSONObject();
         var3.put("sim_slots", recoverMethod_b_5d5a71dd(var0));
         var3.put("packagename", var0.getPackageName());
         var3.put("appkey", RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0));
         var3.put("platform", 0);
         var3.put("apkversion", var2.recoverField_a);
         var3.put("systemversion", var2.recoverField_b);
         var3.put("modelnumber", var2.recoverField_c);
         var3.put("basebandversion", var2.recoverField_d);
         var3.put("buildnumber", var2.recoverField_e);
         var3.put("channel", var2.recoverField_f);
         JSONObject var4 = new JSONObject();
         var4.put("PushSDKVer", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(0));
         var4.put("StatisticSDKVer", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(1));
         var4.put("ShareSDKVer", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(2));
         var4.put("CoreSDKVer", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(3));
         var4.put("SspSDKVer", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(4));
         var4.put("VerificationSDKVer", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(5));
         var3.put("sdkver", var4);
         var3.put("installation", var2.recoverField_g);
         var3.put("business", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_9b72());
         var3.put("device_id_status", RecoverTypeaf2b07c8.recoverField_a);
         var3.put("device_id", RecoverTypeaf2b07c8.recoverMethod_b_e501eb0d(var0));
         var3.put("resolution", var2.recoverField_h);
         var3.put("android_id", var2.recoverField_i);
         var3.put("mac_address", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_b_9ba16009(var0, "", false));
         var1 = var3.toString();
      } catch (JSONException var5) {
      }

      return var1;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      String var2 = Base64.encodeToString(var1.getBytes(), 2);
      RecoverType5a690d1 var3 = new RecoverType5a690d1("cn.jpush.preferences.v2", "n_udp_report_device_info", "");
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, var3.recoverMethod_a_4e9722a3(var2));
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceHelper", "save device info:" + var1);
   }

   public static String recoverMethod_d_e501eb0d(Context var0) {
      RecoverType5a690d1 var1 = new RecoverType5a690d1("cn.jpush.preferences.v2", "n_udp_report_device_info", "");
      String var2 = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, var1);
      if (TextUtils.isEmpty(var2)) {
         RecoverType5a690d1 var3 = new RecoverType5a690d1("cn.jpush.preferences.v2", "udp_report_device_info", "");
         var2 = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, var3);
         if (!TextUtils.isEmpty(var2)) {
            recoverMethod_a_5ffc00fd(var0, var2);
         }
      } else {
         var2 = new String(Base64.decode(var2, 2));
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceHelper", "read deviceinfo:" + var2);
      return var2;
   }

   public static String recoverMethod_a_fcba5f30(Context var0, int var1) {
      if (var1 != 53) {
         return null;
      }

      Object var2 = null;

      try {
         JSONObject var3 = null;
         String var4 = recoverMethod_d_e501eb0d(var0);
         if (TextUtils.isEmpty(var4)) {
            var4 = recoverMethod_c_e501eb0d(var0);
         }

         var3 = new JSONObject(var4);
         JSONObject var5 = new JSONObject();
         var5.put("rom_type", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_f0554ddf(var0));
         var5.put("regid", RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_c_e501eb0d(var0));
         var3.put("rom_info", var5);
         JSONObject var6 = new JSONObject();
         var6.put("cmd", 53);
         var6.put("content", var3);
         return var6.toString();
      } catch (JSONException var7) {
         return (String)var2;
      }
   }

   public static JSONObject recoverMethod_e_16ce4761(Context var0) {
      return null;
   }

   static {
      recoverField_y.put(recoverField_f, "p_root");
      recoverField_y.put(recoverField_g, "p_data");
      recoverField_y.put(recoverField_h, "p_etc");
      recoverField_y.put(recoverField_i, "p_emu");
      recoverField_y.put(recoverField_j, "p_emu0");
      recoverField_y.put(recoverField_k, "p_user");
      recoverField_y.put(recoverField_l, "p_mnt");
      recoverField_y.put(recoverField_m, "p_app");
      recoverField_y.put(recoverField_n, "p_papp");
      recoverField_y.put(recoverField_o, "p_bin");
      recoverField_y.put(recoverField_p, "p_usr");
      recoverField_y.put(recoverField_q, "p_acct");
      recoverField_y.put(recoverField_r, "p_dev");
      recoverField_y.put(recoverField_s, "p_con");
      recoverField_y.put(recoverField_t, "p_datas");
      recoverField_y.put(recoverField_u, "p_pro");
      recoverField_y.put(recoverField_v, "p_sys");
      recoverField_y.put(recoverField_w, "p_sfont");
      recoverField_y.put(recoverField_x, "p_fram");
      recoverField_z.add(recoverField_g);
      recoverField_z.add(recoverField_k);
      recoverField_A.add(":00:00.");
   }
}
