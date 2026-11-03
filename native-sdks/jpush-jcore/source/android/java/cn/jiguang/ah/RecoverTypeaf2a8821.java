package cn.jiguang.ah;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.ak.RecoverTypeaf2a9362;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.i.RecoverType5a69fd6;
import java.util.HashMap;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a8821 {
   public static void recoverMethod_a_f0554df3(Context var0) {
      String var1 = RecoverType5a69fd6.recoverMethod_d_e501eb0d(var0);
      boolean var2 = RecoverType5a69fd6.recoverMethod_c_f0554df7(var0);
      boolean var3 = false;
      if (TextUtils.isEmpty(var1)) {
         if (!var2) {
            var3 = true;
         }
      } else {
         boolean var4 = true;
         if (TextUtils.equals("ON", var1)) {
            var4 = true;
         } else if (TextUtils.equals("OFF", var1)) {
            var4 = false;
         } else {
            var3 = true;
         }

         if (!var3) {
            var3 = var4 != var2;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeLocalState", "local wake state do not changed");
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeLocalState", "lastCacheWakeState:" + var1 + ",userWakeupEnable:" + var2 + ",isNeedReport:" + var3);
      if (var3) {
         try {
            JSONObject var6 = new JSONObject();
            var6.put("status", var2 ? 1 : 2);
            RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var0, var6, "aals");
            RecoverType5a69fd6.recoverMethod_l_5ffc00fd(var0, var2 ? "ON" : "OFF");
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeLocalState", "report local wake state failed, error:" + var5.getMessage());
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeLocalState", "do not need report local wake state");
      }
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      try {
         String var3 = "reportWake";
         long var4 = RecoverType5a69fd6.recoverMethod_g_5ffc00f1(var0, var3);
         long var6 = System.currentTimeMillis();
         if (var6 - var4 < var1) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeLocalState", "not report component status business time, interval: " + var1);
            return;
         }

         HashMap var8 = recoverMethod_b_2ad20c96(var0);
         JSONArray var9 = new JSONArray();
         JSONObject var10 = recoverMethod_a_51994523("activity", (Boolean)var8.get("activity"));
         var9 = var10 != null ? var9.put(var10) : var9;
         var10 = recoverMethod_a_51994523("service", (Boolean)var8.get("service"));
         var9 = var10 != null ? var9.put(var10) : var9;
         var10 = recoverMethod_a_51994523("provider", (Boolean)var8.get("provider"));
         var9 = var10 != null ? var9.put(var10) : var9;
         if (var9.length() > 0) {
            JSONArray var11 = new JSONArray();
            JSONObject var12 = new JSONObject();
            JSONArray var13 = new JSONArray();
            long var14 = System.currentTimeMillis();
            var13.put(var14 / 1000L);
            var13.put(var14 / 1000L + 1L);
            var12.put("duration", var13);
            var12.put("statistics", var9);
            var12.put("c_type", "aacs");
            var11.put(var12);
            JSONObject var16 = new JSONObject();
            var16.put("slice_index", 1);
            var16.put("slice_count", 1);
            var16.put("data", var11);
            var16.put("sequence", UUID.randomUUID().toString());
            RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var0, var16, "collect_statistic");
            RecoverType5a69fd6.recoverMethod_f_5ffc00fd(var0, var3);
         }
      } catch (Throwable var17) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeLocalState", "reportWakeComponentsState failed, " + var17.getMessage());
      }
   }

   private static JSONObject recoverMethod_a_51994523(String var0, boolean var1) {
      try {
         if (!var1) {
            JSONObject var2 = new JSONObject();
            var2.put("result", "1_0");
            var2.put("s_type", var0);
            return var2;
         }
      } catch (Throwable var3) {
      }

      return null;
   }

   private static HashMap recoverMethod_b_2ad20c96(Context var0) {
      HashMap var1 = new HashMap();
      String var2 = var0.getPackageName();
      Intent var3 = new Intent();
      var3.setClassName(var2, "cn.jpush.android.service.DActivity");
      Intent var4 = new Intent();
      var4.setClassName(var2, "cn.jpush.android.service.BActivity");
      boolean var5 = RecoverTypeaf2a9362.recoverMethod_a_3441485e(var0, var2, var3, false)
         | RecoverTypeaf2a9362.recoverMethod_a_3441485e(var0, var2, var4, false);
      var1.put("activity", var5);
      PackageManager var6 = var0.getPackageManager();
      var3 = new Intent();
      var3.setPackage(var2);
      var3.setAction("cn.jpush.android.intent.DaemonService");
      short var7 = 512;
      if (VERSION.SDK_INT >= 24) {
         var7 = 512;
      }

      ResolveInfo var8 = var6.resolveService(var3, var7);
      boolean var9 = var8 != null && var8.serviceInfo.exported;
      var1.put("service", var9);
      String var10 = var2 + ".DownloadProvider";
      ProviderInfo var11 = var6.resolveContentProvider(var10, var7);
      boolean var12 = var11 != null && var11.exported;
      var1.put("provider", var12);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JWakeLocalState", "check w component status, activity[" + var5 + "]-service[" + var9 + "]-provider[" + var12 + "]"
      );
      return var1;
   }
}
