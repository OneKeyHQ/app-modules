package cn.jiguang.ak;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.ai.RecoverTypeaf2a8be0;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.f.RecoverType5a69492;
import cn.jpush.android.service.WakedResultReceiver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a9368 {
   private static WakedResultReceiver recoverField_b;
   public static boolean recoverField_a = false;

   public static void recoverMethod_a_cf277f22(final Context var0, final Bundle var1, final int var2) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "executeWakedAction.");
         recoverMethod_a_1a54e370(var0, var2);
         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               RecoverTypeaf2a9368.recoverMethod_c_cf277f22(var0, var1, var2);
            }
         });
         RecoverType5a69492.recoverMethod_a_1a54e370(var0, 2);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "executeWakedAction failed:" + var4.getLocalizedMessage());
      }
   }

   private static void recoverMethod_a_1a54e370(Context var0, int var1) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "context is null,can not notify waked");
      } else {
         recoverField_b = recoverMethod_c_625dbd8f(var0);
         if (recoverField_b == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "waked receiver is null");
         } else {
            HashMap var2 = new HashMap(2);
            var2.put("1", var0);
            var2.put("2", var1);
            recoverField_b.onWakeMap(var2);
         }
      }
   }

   private static WakedResultReceiver recoverMethod_c_625dbd8f(Context var0) {
      if (recoverField_b != null) {
         return recoverField_b;
      }

      try {
         PackageInfo var1 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, 0);
         if (null == var1) {
            return null;
         }

         for (ActivityInfo var5 : var1.receivers) {
            try {
               if (WakedResultReceiver.class.isAssignableFrom(Class.forName(var5.name, false, WakedResultReceiver.class.getClassLoader()))) {
                  return (WakedResultReceiver)Class.forName(var5.name).newInstance();
               }
            } catch (Throwable var7) {
            }
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "find waked receiver throwable:" + var8.getMessage());
      }

      return null;
   }

   private static void recoverMethod_c_cf277f22(Context var0, Bundle var1, int var2) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "bundle is null,give up save");
      } else {
         RecoverTypeaf2a8be0 var3 = RecoverTypeaf2a9363.recoverMethod_b_5edd4c44(var0);
         if (!var3.recoverField_x) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "server set do not report waked data,give up save");
         } else {
            String var4 = null;

            try {
               var4 = var1.getString("from_package");
            } catch (Throwable var8) {
            }

            if (var4 == null) {
               var4 = "";
            }

            JSONObject var5 = recoverMethod_a_c8a6a75e(var4, var2, recoverField_a);
            recoverField_a = true;
            if (var5 != null) {
               try {
                  String var6 = var1.getString("jg_extras");
                  if (!TextUtils.isEmpty(var6)) {
                     var5.put("jg_extras", var6);
                  }
               } catch (Throwable var7) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "save waked extras error:" + var7.getMessage());
               }

               recoverMethod_a_3c4029ab(var0, var5);
            }
         }
      }
   }

   private static JSONObject recoverMethod_a_c8a6a75e(String var0, int var1, boolean var2) {
      try {
         if (var0 == null) {
            var0 = "";
         }

         JSONObject var3 = new JSONObject();
         var3.put("wake_type", var1);
         var3.put("package", var0);
         var3.put("app_alive", var2);
         return var3;
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "packageWakedJson error:" + var4.getMessage());
         return null;
      }
   }

   private static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      if (var1 != null) {
         try {
            var1.put("itime", RecoverType5a68d11.recoverMethod_b_f0554de7(var0));
            var1.put("type", "aat3");
            synchronized ("waked_cache_v2.json") {
               JSONObject var2 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("waked_cache_v2.json");
               if (var2 == null) {
                  var2 = new JSONObject();
               }

               JSONArray var4 = var2.optJSONArray("content");
               if (var4 == null) {
                  var4 = new JSONArray();
               }

               var4.put(var1);
               var2.put("content", var4);
               RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("waked_cache_v2.json", var2);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "save waked data : " + var2.toString());
            }
         } catch (Throwable var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "saveWakedData failed:" + var7.getMessage());
         }
      }
   }

   public static JSONArray recoverMethod_a_5d5a71dd(Context var0) {
      JSONObject var1;
      synchronized ("waked_cache_v2.json") {
         var1 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("waked_cache_v2.json");
      }

      if (var1 == null) {
         var1 = new JSONObject();
      }

      JSONArray var5 = var1.optJSONArray("content");
      if (var5 != null && var5.length() != 0) {
         return recoverMethod_a_41013e83(var0, var5);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "no save data");
      return null;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      if (var0 != null && !TextUtils.isEmpty(var1)) {
         try {
            JSONObject var2 = new JSONObject();
            var2.put("di", var1);
            synchronized ("di.json") {
               JSONObject var3 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("di.json");
               if (var3 == null) {
                  var3 = new JSONObject();
               }

               JSONArray var5 = var3.optJSONArray("content");
               if (var5 == null) {
                  var5 = new JSONArray();
               }

               var5.put(var2);
               var3.put("content", var5);
               RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("di.json", var3);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "save di is: " + var1 + ", success!");
            }
         } catch (Throwable var8) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "saveDId failed:" + var8.getMessage());
         }
      }
   }

   public static JSONArray recoverMethod_a_41013e83(Context var0, JSONArray var1) {
      try {
         if (var1 != null && var1.length() > 0) {
            HashMap var2 = new HashMap();

            for (int var3 = 0; var3 < var1.length(); var3++) {
               JSONObject var4 = var1.getJSONObject(var3);
               String var5 = var4.optString("type");
               long var6 = var4.optLong("itime");
               Map var8 = null;

               for (Object recoveryElement_225 : var2.entrySet()) {
                  Entry var10 = (Entry)recoveryElement_225;
                  long var11 = (Long)var10.getKey();
                  if (RecoverTypeaf2b07d7.recoverMethod_a_2566ab9(var6 * 1000L, var11 * 1000L)) {
                     var6 = var11;
                     var8 = (Map)var10.getValue();
                     break;
                  }
               }

               var8 = var8 == null ? new HashMap() : var8;
               if (!"android_awake_target2".equals(var5) && !"aat3".equals(var5)) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakedHelper", "unkown type :" + var5);
               } else {
                  String var27 = var4.optString("package");
                  String var29 = var4.optBoolean("app_alive") ? "active" : "dead";
                  int var31 = var4.optInt("wake_type");
                  if (var8.containsKey(var27)) {
                     Map var12 = (Map)var8.get(var27);
                     if (var12.containsKey(var31)) {
                        Map var13 = (Map)var12.get(var31);
                        if (var13.containsKey(var29)) {
                           Integer var14 = (Integer)var13.get(var29);
                           var14 = var14 + 1;
                           var13.put(var29, var14);
                        } else {
                           var13.put(var29, 1);
                        }

                        var12.put(var31, var13);
                     } else {
                        HashMap var36 = new HashMap();
                        var36.put(var29, 1);
                        var12.put(var31, var36);
                     }

                     var8.put(var27, var12);
                  } else {
                     HashMap var33 = new HashMap();
                     var33.put(var29, 1);
                     HashMap var37 = new HashMap();
                     var37.put(var31, var33);
                     var8.put(var27, var37);
                  }

                  var2.put(var6, var8);
                  String var34 = var4.optString("jg_extras");
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "jg_extras is :" + var34);
                  if (!TextUtils.isEmpty(var34)) {
                     JSONObject var38 = new JSONObject(var34);
                     if (var38 != null) {
                        String var41 = var38.optString("di");
                        if (!TextUtils.isEmpty(var41)) {
                           recoverMethod_a_5ffc00fd(var0, var41);
                        }
                     }
                  }
               }
            }

            JSONArray var20 = new JSONArray();

            for (Object recoveryElement_286 : var2.entrySet()) {
               Entry var22 = (Entry)recoveryElement_286;
               JSONObject var23 = new JSONObject();
               JSONArray var7 = new JSONArray();

               for (Object recoveryElement_290 : ((Map)var22.getValue()).entrySet()) {
                  Entry var28 = (Entry)recoveryElement_290;
                  JSONObject var30 = new JSONObject();
                  var30.put("p", var28.getKey());
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "pkg:" + (String)var28.getKey());
                  Iterator var32 = ((Map)var28.getValue()).entrySet().iterator();
                  JSONArray var35 = new JSONArray();

                  while (var32.hasNext()) {
                     Entry var39 = (Entry)var32.next();
                     int var42 = (Integer)var39.getKey();
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "type:" + var42);
                     Map var15 = (Map)var39.getValue();
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakedHelper", "value:" + var15);
                     int var16 = var15.containsKey("active") ? (Integer)var15.get("active") : 0;
                     int var17 = var15.containsKey("dead") ? (Integer)var15.get("dead") : 0;
                     int var18 = var16 + var17;
                     var35.put(var42 + "_" + var18 + "_" + var16 + "_" + var17);
                  }

                  var30.put("d", var35);
                  var7.put(var30);
               }

               if (var7.length() > 0) {
                  var23.put("f", var7);
                  long var26 = (Long)var22.getKey();
                  var23.put("itime", var26);
                  var23.put("type", "aat3");
                  var20.put(var23);
               }
            }

            return var20.length() > 0 ? var20 : var1;
         } else {
            return var1;
         }
      } catch (Throwable var19) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JWakedHelper", "merge waked json failed:" + var19.getMessage());
         return null;
      }
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("waked_cache_v2.json");
   }
}
