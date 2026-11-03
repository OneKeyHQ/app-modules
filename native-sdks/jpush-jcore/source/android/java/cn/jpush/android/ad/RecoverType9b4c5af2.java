package cn.jpush.android.ad;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.json.JSONArray;

public class RecoverType9b4c5af2 {
   public static final String recoverField_a = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{85, 118, 97, 33, 60, 84, 32, 0, 98, 46, 73, 85, 85, 116, 18, 44}
   );
   private static final Object recoverField_b = new Object();
   private static ConcurrentHashMap recoverField_c = new ConcurrentHashMap();
   private static final AtomicReference recoverField_d = new AtomicReference();

   public static ArrayList recoverMethod_a_48eba5e9(JSONArray var0) {
      ArrayList var1 = new ArrayList();
      if (null != var0 && var0.length() != 0) {
         for (int var2 = 0; var2 < var0.length(); var2++) {
            String var3 = var0.optString(var2);
            if (!TextUtils.isEmpty(var3)) {
               var1.add(var3);
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   public static String recoverMethod_a_cd13baf0(Collection var0, String var1) {
      StringBuilder var2 = new StringBuilder();

      for (Object recoveryElement_62 : var0) {
         String var4 = (String)recoveryElement_62;
         var2.append(var4).append(var1);
      }

      if (var2.length() > 0) {
         var2.deleteCharAt(var2.length() - 1);
      }

      return var2.toString();
   }

   public static double recoverMethod_a_bbe0f863(double var0, double var2, double var4, double var6) {
      double var8 = 0.008726646259971648;
      double var10 = 1.2756274E7;
      double var12 = var2 * 0.008726646259971648;
      double var14 = var6 * 0.008726646259971648;
      double var16 = var12 - var14;
      double var18 = (var0 - var4) * 0.008726646259971648;
      double var20 = Math.asin(Math.sqrt(Math.pow(Math.sin(var16), 2.0) + Math.cos(var12 * 2.0) * Math.cos(var14 * 2.0) * Math.pow(Math.sin(var18), 2.0)))
         * 1.2756274E7;
      return Math.round(var20 * 10000.0) / 10000L;
   }

   public static void recoverMethod_a_d17885d6(Closeable var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }

   public static byte[] recoverMethod_a_b328ee5f(InputStream var0) throws java.io.IOException {
      if (var0 == null) {
         throw new IOException("InputStream is null");
      }

      byte[] var1 = new byte[var0.available()];
      var0.read(var1);
      return var1;
   }

   public static byte[] recoverMethod_b_b328ee5f(InputStream var0) throws java.io.IOException {
      if (var0 == null) {
         throw new IOException("InputStream is null");
      }

      ByteArrayOutputStream var1 = null;

      try {
         var1 = new ByteArrayOutputStream();
         byte[] var2 = new byte[1024];

         int var3;
         while ((var3 = var0.read(var2)) != -1) {
            var1.write(var2, 0, var3);
         }

         return var1.toByteArray();
      } finally {
         recoverMethod_a_d17885d6(var1);
      }
   }

   public static byte[] recoverMethod_a_4962d961(byte[] var0) throws java.io.IOException {
      if (var0 != null && var0.length != 0) {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         ByteArrayInputStream var2 = new ByteArrayInputStream(var0);
         GZIPInputStream var3 = null;

         try {
            var3 = new GZIPInputStream(var2);
            byte[] var4 = new byte[256];

            int var5;
            while ((var5 = var3.read(var4)) >= 0) {
               var1.write(var4, 0, var5);
            }

            return var1.toByteArray();
         } finally {
            recoverMethod_a_d17885d6(var1);
            recoverMethod_a_d17885d6(var2);
            recoverMethod_a_d17885d6(var3);
         }
      } else {
         return var0;
      }
   }

   public static SimpleDateFormat recoverMethod_a_5318097a(String var0) {
      ThreadLocal var1 = (ThreadLocal)recoverField_c.get(var0);
      if (var1 == null) {
         synchronized (recoverField_b) {
            var1 = (ThreadLocal)recoverField_c.get(var0);
            if (var1 == null) {
               var1 = new RecoverType9b4c5af2.Memberf9a16b2f(var0);
               recoverField_c.put(var0, var1);
            }
         }
      }

      return (SimpleDateFormat)var1.get();
   }

   public static int recoverMethod_a_1a54e363(Context var0, int var1) {
      return null == var0 ? var1 : (int)TypedValue.applyDimension(1, var1, var0.getResources().getDisplayMetrics());
   }

   public static LayoutParams recoverMethod_a_563fae02(Context var0, View var1, boolean var2, int var3, int var4, int var5, int var6) {
      try {
         if (var0 != null && var1 != null) {
            int var7 = var3;
            int var8 = var5;
            int var9 = var4;
            int var10 = var6;
            LayoutParams var11 = var1.getLayoutParams();
            MarginLayoutParams var12 = null;
            if (var11 instanceof MarginLayoutParams) {
               var12 = (MarginLayoutParams)var11;
            } else {
               var12 = new MarginLayoutParams(var11);
            }

            if (var2) {
               var7 = recoverMethod_a_1a54e363(var0, var3);
               var8 = recoverMethod_a_1a54e363(var0, var5);
               var9 = recoverMethod_a_1a54e363(var0, var4);
               var10 = recoverMethod_a_1a54e363(var0, var6);
            }

            var12.setMargins(var7, var9, var8, var10);
            var1.setLayoutParams(var12);
            return var12;
         }
      } catch (Throwable var13) {
         Logger.recoverMethod_w_e925fa09("Utils", "[setViewMargin] failed. err: " + var13.getMessage());
      }

      return null;
   }

   public static DisplayMetrics recoverMethod_a_f8f56096(Context var0) {
      DisplayMetrics var1 = new DisplayMetrics();
      if (null != var0) {
         WindowManager var2 = (WindowManager)var0.getSystemService("window");
         if (null != var2) {
            if (VERSION.SDK_INT >= 17) {
               var2.getDefaultDisplay().getRealMetrics(var1);
            } else {
               var2.getDefaultDisplay().getMetrics(var1);
            }
         }
      }

      return var1;
   }

   public static int recoverMethod_b_1a54e363(Context var0, int var1) {
      DisplayMetrics var2 = recoverMethod_a_f8f56096(var0);
      return (int)(var2.widthPixels * (var1 / 375.0F));
   }

   public static int recoverMethod_c_1a54e363(Context var0, int var1) {
      DisplayMetrics var2 = recoverMethod_a_f8f56096(var0);
      return (int)(var2.heightPixels * (var1 / 667.0F));
   }

   public static int recoverMethod_b_aad3b1f2(String var0) {
      try {
         Pattern var1 = Pattern.compile("[^0-9]");
         return Integer.valueOf(var1.matcher(var0).replaceAll("").trim());
      } catch (Throwable var2) {
         return -1;
      }
   }

   public static void recoverMethod_a_fa4794e4(Context var0, List var1) {
      if (JCoreHelper.isIpv6Priority(var0) && var1 != null && var1.size() >= 2 && !((String)var1.get(0)).contains("ipv6")) {
         Collections.swap(var1, 0, 1);
      }
   }

   public static LinkedList recoverMethod_c_3e0e9f8f(String var0) {
      try {
         LinkedList var1 = new LinkedList();
         if (var0 != null && var0.length() > 0) {
            for (String var5 : var0.split(",")) {
               if (!TextUtils.isEmpty(var5)) {
                  var1.add(var5);
               }
            }
         }

         return var1;
      } catch (Throwable var6) {
         return null;
      }
   }

   public static Context recoverMethod_a_88bf2b6d() {
      return recoverMethod_b_6263c3eb(null);
   }

   public static Context recoverMethod_b_6263c3eb(Context var0) {
      Context var1 = (Context)recoverField_d.get();
      if (var1 == null) {
         if (var0 != null) {
            var1 = var0.getApplicationContext();
         } else {
            try {
               var1 = JPushConstants.getAppContext(var0);
            } catch (Throwable var3) {
            }

            if (null == var1) {
               var1 = recoverMethod_b_c4d1044();
            }
         }

         if (!recoverField_d.compareAndSet(null, var1)) {
            var1 = (Context)recoverField_d.get();
         }
      }

      return var1;
   }

   private static Application recoverMethod_b_c4d1044() {
      try {
         Class var0 = Class.forName("android.app.ActivityThread");
         Object var1 = recoverMethod_c_810c345d();
         Object var2 = var0.getMethod("getApplication").invoke(var1);
         return var2 == null ? null : (Application)var2;
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Object recoverMethod_c_810c345d() {
      Object var0 = recoverMethod_d_810c345d();
      return var0 != null ? var0 : recoverMethod_e_810c345d();
   }

   private static Object recoverMethod_d_810c345d() {
      try {
         Class var0 = Class.forName("android.app.ActivityThread");
         Field var1 = var0.getDeclaredField("sCurrentActivityThread");
         var1.setAccessible(true);
         return var1.get(null);
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Object recoverMethod_e_810c345d() {
      try {
         Class var0 = Class.forName("android.app.ActivityThread");
         return var0.getMethod("currentActivityThread").invoke(null);
      } catch (Throwable var1) {
         return null;
      }
   }

   private static class Memberf9a16b2f extends ThreadLocal {
      private String recoverField_a;

      Memberf9a16b2f(String var1) {
         this.recoverField_a = var1;
      }

      protected SimpleDateFormat recoverMethod_a_627ae644() {
         return new SimpleDateFormat(this.recoverField_a, Locale.ENGLISH);
      }

      // $VF: synthetic method
      @Override
      protected Object initialValue() {
         return this.recoverMethod_a_627ae644();
      }
   }
}
