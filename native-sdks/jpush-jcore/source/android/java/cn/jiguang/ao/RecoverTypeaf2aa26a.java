package cn.jiguang.ao;

import android.content.Context;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d6;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.internal.JConstants;
import cn.jiguang.m.RecoverType5a6aed9;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2aa26a {
   private static RecoverTypeaf2aa26a.Member8f0bb4a7 recoverField_a;
   private static volatile FutureTask recoverField_b;
   private static final Object recoverField_c = new Object();
   private static final LinkedHashSet recoverField_d = new LinkedHashSet();
   private static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{126, 101, 68, 80, 106, 50, 57, 62, 68, 83, 112, 123, 56, 123, 64, 85, 106, 96, 56, 114, 94}
   );
   private static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{96, 98, 101, 64, 83, 35, 39, 57, 101, 67, 73, 106, 37, 127, 97, 70, 22, 55, 98, 102, 100, 67, 72, 55, 107, 120}
   );
   private static final LinkedHashSet recoverField_g = new LinkedHashSet();

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      if (recoverField_a == null) {
         recoverField_a = new RecoverTypeaf2aa26a.Member8f0bb4a7(var0);
      }

      if (recoverField_b == null || recoverField_b.isCancelled() || recoverField_b.isDone()) {
         synchronized (recoverField_c) {
            if (recoverField_b == null || recoverField_b.isCancelled() || recoverField_b.isDone()) {
               try {
                  recoverField_b = new FutureTask(recoverField_a, null);
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", recoverField_b);
               } catch (Throwable var9) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "new sis task e:" + var9);
               }
            }
         }
      }

      if (var1) {
         try {
            recoverField_b.get(10L, TimeUnit.SECONDS);
         } catch (InterruptedException var5) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "sis task e:" + var5);
         } catch (ExecutionException var6) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "sis task e:" + var6);
         } catch (TimeoutException var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "sis task e:" + var7);
         } catch (Throwable var8) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "sis task e:" + var8);
         }
      }
   }

   public static LinkedHashSet recoverMethod_a_d5ec344b() {
      return JConstants.isTestEnv() && !recoverField_g.isEmpty() ? recoverField_g : recoverField_d;
   }

   private static String recoverMethod_c_e501eb0d(Context var0) {
      JSONObject var1 = new JSONObject();

      try {
         String var2 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
         long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_B_48794abf());
         int var5 = RecoverTypeaf2b07ce.recoverMethod_b_f0554de6(var0);
         String var6 = RecoverTypeaf2b07d6.recoverMethod_a_5e799b7f(var0, false);
         var1.put("type", var5);
         var1.put("appkey", var2);
         var1.put("sdkver", RecoverType5a681cd.recoverField_b);
         var1.put("platform", 0);
         if (var3 != 0L) {
            var1.put("uid", var3);
         }

         if (var6 != null) {
            var1.put("opera", var6);
         }

         double var7 = 200.0;
         double var9 = 200.0;
         long var11 = 0L;
         Object var13 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(var0, "get_loc_info", null);
         if (var13 instanceof Bundle) {
            try {
               Bundle var14 = (Bundle)var13;
               var7 = var14.getDouble("lat");
               var9 = var14.getDouble("lot");
               var11 = var14.getLong("time");
            } catch (Throwable var16) {
            }
         }

         byte var18 = 0;
         byte var15 = 0;
         if (var7 > -90.0 && var7 < 90.0 && var9 > -180.0 && var9 < 180.0) {
            var1.put("lat", var7);
            var1.put("lng", var9);
            var1.put("time", var11);
         }

         if (var18 != 0) {
            var1.put("ips_flag", var18);
         }

         if (var15 != 0) {
            var1.put("report_flag", var15);
         }
      } catch (Throwable var17) {
      }

      return var1.toString();
   }

   private static LinkedHashSet recoverMethod_d_af90ccc9(Context var0) {
      LinkedHashSet var1 = new LinkedHashSet();
      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_W_48794abf());
      if (TextUtils.isEmpty(var2)) {
         return var1;
      }

      try {
         JSONArray var3 = new JSONArray(var2);

         for (int var4 = 0; var4 < var3.length(); var4++) {
            var1.add(var3.optString(var4));
         }
      } catch (JSONException var5) {
      }

      return var1;
   }

   private static boolean recoverMethod_b_9d23c58b(Context var0, String var1, String var2) {
      RecoverTypeaf2aa26c var3 = RecoverTypeaf2aa267.recoverMethod_a_ab3ea53a(var1, var2, var0, true, 3, 2);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "ReportSis", "report sis code[" + var3.recoverMethod_a_9b68() + "] from url=" + var1 + "\n body=" + var3.recoverMethod_b_73cf11cb()
      );
      if (var3.recoverMethod_a_9b68() == 0) {
         String var4 = var3.recoverMethod_b_73cf11cb();
         if (!TextUtils.isEmpty(var4)) {
            try {
               JSONObject var5 = new JSONObject(var4).getJSONObject("ret");
               RecoverTypeaf2aa266.recoverMethod_b_c6941000().recoverMethod_a_3c4029ab(var0, var5);
               return true;
            } catch (Throwable var6) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "getUrls e:" + var6);
            }
         }
      }

      return false;
   }

   static {
      recoverField_d.add(recoverField_e);
      recoverField_d.add(recoverField_f);
   }

   private static class Member8f0bb4a7 extends RecoverTypeaf2b0401 {
      private final Context recoverField_a;

      private Member8f0bb4a7(Context var1) {
         this.recoverField_a = var1;
         this.recoverField_h = "SisTask";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            String var1 = RecoverTypeaf2aa26a.recoverMethod_c_e501eb0d(this.recoverField_a);
            LinkedHashSet var2 = RecoverTypeaf2aa26a.recoverMethod_d_af90ccc9(this.recoverField_a);
            var2.addAll(RecoverTypeaf2aa26a.recoverMethod_a_d5ec344b());
            var2 = RecoverTypeaf2b07d7.recoverMethod_a_647dad69(this.recoverField_a, var2);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportSis", "sis urls=" + var2.toString() + " post json=" + var1);
            if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "give up sis, because network is not connected");
               return;
            }

            for (Object recoveryElement_201 : var2) {
               String var4 = (String)recoveryElement_201;
               if (!TextUtils.isEmpty(var4)) {
                  if (VERSION.SDK_INT >= 28 && !var4.startsWith(RecoverType5a69fd7.Member367e0ad4.recoverField_d)) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportSis", "won't use http at device since 28");
                  } else if (RecoverTypeaf2aa26a.recoverMethod_b_9d23c58b(this.recoverField_a, var4, var1)) {
                     return;
                  }
               }
            }
         } catch (Throwable var5) {
         }
      }
   }
}
