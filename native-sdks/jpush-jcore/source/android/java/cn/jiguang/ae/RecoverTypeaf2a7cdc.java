package cn.jiguang.ae;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.g.RecoverType5a69853;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a7cdc {
   public static JSONArray recoverMethod_a_5d5a71dd(Context var0) {
      if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(2600)) {
         return new JSONArray();
      }

      JSONObject var1;
      synchronized ("crs.catch") {
         var1 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("crs.catch");
      }

      if (var1 == null) {
         var1 = new JSONObject();
      }

      JSONArray var5 = var1.optJSONArray("data");
      if (var5 != null && var5.length() != 0) {
         return recoverMethod_a_41013e83(var0, var5);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataCollectHelper", "no save data");
      return new JSONArray();
   }

   private static JSONArray recoverMethod_a_41013e83(Context var0, JSONArray var1) {
      try {
         if (var1 == null || var1.length() <= 0) {
            return new JSONArray();
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataCollectHelper", "transfer report data, mergeData: " + var1.toString());
         HashMap var2 = new HashMap();
         HashMap var3 = new HashMap();
         HashMap var4 = new HashMap();

         for (int var7 = 0; var7 < var1.length(); var7++) {
            JSONObject var8 = var1.getJSONObject(var7);
            String var9 = var8.optString("c_type");
            String var10 = var8.optString("s_type");
            if (TextUtils.equals(var9, var10)) {
               var9 = "&" + var9;
            }

            int var11 = var8.optInt("result");
            long var12 = var8.optLong("c_time");
            Map var5;
            if (var2.containsKey(var9)) {
               long var14 = var3.get(var9) == null ? var12 : (Long)var3.get(var9);
               long var16 = var4.get(var9) == null ? var12 : (Long)var4.get(var9);
               if (var12 <= var14) {
                  var3.put(var9, var12);
               }

               if (var12 >= var16) {
                  var4.put(var9, var12);
               }

               var5 = (Map)var2.get(var9);
               if (var5.containsKey(var10)) {
                  Map var6 = (Map)var5.get(var10);
                  if (var6.containsKey(var11)) {
                     var6.put(var11, (Integer)var6.get(var11) + 1);
                  } else {
                     var6.put(var11, 1);
                  }

                  var5.put(var10, var6);
               } else {
                  HashMap var42 = new HashMap();
                  var42.put(var11, 1);
                  var5.put(var10, var42);
               }
            } else {
               var5 = new HashMap();
               HashMap var43 = new HashMap();
               var43.put(var11, 1);
               var5.put(var10, var43);
               var3.put(var9, var12);
               var4.put(var9, var12);
            }

            var2.put(var9, var5);
         }

         JSONArray var44 = new JSONArray();

         for (Object recoveryElement_100 : var2.entrySet()) {
            Entry var46 = (Entry)recoveryElement_100;
            JSONObject var47 = new JSONObject();
            String var48 = (String)var46.getKey();
            long var49 = (Long)var3.get(var48);
            long var50 = (Long)var4.get(var48);
            JSONArray var51 = new JSONArray();
            var51.put(var49 / 1000L);
            var51.put(var50 / 1000L);
            var47.put("duration", var51);
            if (var48.startsWith("&")) {
               var48 = var48.substring(1);
            }

            JSONArray var17 = new JSONArray();

            for (Object recoveryElement_115 : ((Map)var46.getValue()).entrySet()) {
               Entry var19 = (Entry)recoveryElement_115;
               JSONObject var20 = new JSONObject();
               String var21 = (String)var19.getKey();
               Map var22 = (Map)var19.getValue();
               int var23 = var22.containsKey(0) ? (Integer)var22.get(0) : 0;
               int var24 = var22.containsKey(-1) ? (Integer)var22.get(-1) : 0;
               int var25 = var22.containsKey(-2) ? (Integer)var22.get(-2) : 0;
               int var26 = var22.containsKey(-3) ? (Integer)var22.get(-3) : 0;
               int var27 = var22.containsKey(-4) ? (Integer)var22.get(-4) : 0;
               int var28 = var22.containsKey(-5) ? (Integer)var22.get(-5) : 0;
               int var29 = var22.containsKey(-6) ? (Integer)var22.get(-6) : 0;
               int var30 = var23 + var24 + var25 + var26 + var27 + var28 + var29;
               String var31 = "";
               switch (var48) {
                  case "loc_info_v2":
                     int var34 = var22.containsKey(1) ? (Integer)var22.get(1) : 0;
                     int var35 = var22.containsKey(2) ? (Integer)var22.get(2) : 0;
                     var30 += var34 + var35;
                     var31 = "_" + var34 + "_" + var35;
                     break;
                  case "app_active":
                     int var36 = var22.containsKey(1) ? (Integer)var22.get(1) : 0;
                     int var37 = var22.containsKey(2) ? (Integer)var22.get(2) : 0;
                     int var38 = var22.containsKey(3) ? (Integer)var22.get(3) : 0;
                     int var39 = var22.containsKey(4) ? (Integer)var22.get(4) : 0;
                     int var40 = var22.containsKey(5) ? (Integer)var22.get(5) : 0;
                     var30 += var36 + var37 + var38 + var39 + var40;
                     var31 = "_" + var36 + "_" + var37 + "_" + var38 + "_" + var39 + "_" + var40;
               }

               String var52 = var30 + "_" + var23 + "_" + var24 + "_" + var25 + "_" + var26 + "_" + var27 + "_" + var28 + "_" + var29 + var31;
               var20.put("result", var52);
               var20.put("s_type", var21);
               var17.put(var20);
            }

            var47.put("c_type", var48);
            var47.put("statistics", var17);
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JDataCollectHelper", "merge report statistic data: " + var47.toString());
            var44.put(var47);
         }

         if (var44.length() > 0) {
            return var44;
         }
      } catch (Throwable var41) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JDataCollectHelper", "merge report json failed:" + var41.getMessage());
         return null;
      }

      return new JSONArray();
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      synchronized ("crs.catch") {
         RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("crs.catch");
      }
   }
}
