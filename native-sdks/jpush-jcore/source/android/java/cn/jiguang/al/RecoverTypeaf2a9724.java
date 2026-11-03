package cn.jiguang.al;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ay.RecoverTypeaf2ac7f0;
import cn.jiguang.m.RecoverType5a6aed9;
import cn.jiguang.o.RecoverType5a6b65b;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a9724 {
   private static String recoverField_a;
   private static String recoverField_b = null;

   public static Object recoverMethod_a_f23f0d9f(Context var0) {
      if (null == var0) {
         return null;
      }

      JSONObject var1 = new JSONObject();

      try {
         if (!TextUtils.isEmpty(recoverField_a)) {
            try {
               return new JSONObject(recoverField_a);
            } catch (Throwable var10) {
            }
         }

         RecoverTypeaf2ac7f0 var2 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0);
         var1.put("android_id", var2.recoverField_i);
         var1.put("manufacturer", var2.recoverField_l);
         var1.put("model", var2.recoverField_c);
         Object var3 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(var0, "deviceinfo", null);
         if (var3 instanceof JSONObject) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JAppListProbeHelper", "transfer dev info succeed");
            String var4 = "";
            String var5 = "";
            JSONArray var6 = ((JSONObject)var3).getJSONArray("sim_slots");

            for (int var7 = 0; var7 < var6.length(); var7++) {
               JSONObject var8 = var6.getJSONObject(var7);
               if (null != var8) {
                  if (TextUtils.isEmpty(var4)) {
                     var4 = var8.optString("imei", "");
                     if (!TextUtils.isEmpty(var4)) {
                        var1.put("imei1", var4);
                     }
                  } else {
                     var5 = var8.optString("imei", "");
                     if (!TextUtils.isEmpty(var5)) {
                        var1.put("imei2", var5);
                     }
                  }
               }
            }

            if (TextUtils.isEmpty(var4) && TextUtils.isEmpty(var5)) {
               var1.put("imei1", var2.recoverField_p);
            }

            var1.put("os_version", ((JSONObject)var3).optString("os_version", ""));
            var1.put("language", ((JSONObject)var3).optString("language", ""));
            String var12 = "";

            try {
               JSONObject var13 = var1.getJSONObject("ids");
               var12 = var13.optString("joad", "");
               if (TextUtils.isEmpty(var12) && !TextUtils.isEmpty(recoverField_b)) {
                  var12 = recoverField_b;
               }
            } catch (Throwable var9) {
            }

            if (TextUtils.isEmpty(var12)) {
               JSONObject var14 = RecoverType5a6b65b.recoverMethod_a_16ce4761(var0);
               if (var14 != null) {
                  var12 = var14.optString("joad", "");
                  recoverField_b = var12;
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JAppListProbeHelper", "transfer ids single, ids: " + var12);
               }
            }

            if (!TextUtils.isEmpty(var12)) {
               var1.put("joad", var12);
            }
         }

         recoverField_a = var1.toString().equals("{}") ? null : var1.toString();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JAppListProbeHelper", "force dev info " + var1.toString());
      } catch (Throwable var11) {
      }

      return var1;
   }
}
