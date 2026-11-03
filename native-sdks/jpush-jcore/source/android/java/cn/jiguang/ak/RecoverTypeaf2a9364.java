package cn.jiguang.ak;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.ai.RecoverTypeaf2a8be0;
import cn.jiguang.ai.RecoverTypeaf2a8be1;
import cn.jiguang.ai.RecoverTypeaf2a8be2;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d11;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2a9364 {
   private static final Object recoverField_a = new Object();
   private static HashMap recoverField_b;

   public static HashMap recoverMethod_a_2ad20c96(Context var0) {
      try {
         HashMap var1 = new HashMap();
         PackageManager var2 = var0.getPackageManager();
         Intent var3 = new Intent();
         var3.setAction("cn.jpush.android.intent.DaemonService");
         List var4 = var2.queryIntentServices(var3, 0);
         if (null != var4 && 0 != var4.size()) {
            for (int var5 = 0; var5 < var4.size(); var5++) {
               ServiceInfo var6 = ((ResolveInfo)var4.get(var5)).serviceInfo;
               String var7 = var6.name;
               String var8 = var6.packageName;
               if (null != var7
                  && null != var8
                  && !TextUtils.isEmpty(var7)
                  && !TextUtils.isEmpty(var8)
                  && var6.exported
                  && var6.enabled
                  && !var0.getPackageName().equals(var8)) {
                  RecoverTypeaf2a8be2 var9 = RecoverTypeaf2a9362.recoverMethod_a_35f2ae(var0, var2, var8, var7);
                  if (var9 != null) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "wakeTarget:" + var9.toString());
                     var1.put(var9.recoverField_a, var9);
                  }
               }
            }

            return var1;
         } else {
            return null;
         }
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeHelper", "getWakeTargetMap throwable:" + var10.getMessage());
         return null;
      }
   }

   public static HashMap recoverMethod_a_68955e43(Context var0, HashMap var1) {
      try {
         if (null != recoverField_b && !recoverField_b.isEmpty()) {
            return recoverField_b;
         }

         HashMap var2 = new HashMap();
         PackageManager var3 = var0.getPackageManager();
         Intent var4 = new Intent();
         var4.setAction("cn.jpush.android.intent.PushService");
         List var5 = var3.queryIntentServices(var4, 0);
         if (null != var5 && 0 != var5.size()) {
            for (int var6 = 0; var6 < var5.size(); var6++) {
               ServiceInfo var7 = ((ResolveInfo)var5.get(var6)).serviceInfo;
               String var8 = var7.name;
               String var9 = var7.packageName;
               if (null != var8
                  && null != var9
                  && !TextUtils.isEmpty(var8)
                  && !TextUtils.isEmpty(var9)
                  && (var1 == null || !var1.containsKey(var9))
                  && !var0.getPackageName().equals(var9)) {
                  int var10 = var3.checkPermission(var9 + ".permission.JPUSH_MESSAGE", var9);
                  ApplicationInfo var11 = var3.getApplicationInfo(var9, 128);
                  if (var11 != null && var11.metaData != null) {
                     String var12 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
                     if (0 == var10 && !TextUtils.isEmpty(var12) && var12.length() == 24 && !var0.getPackageName().equals(var11.packageName)) {
                        RecoverTypeaf2a8be2 var13 = new RecoverTypeaf2a8be2(var9, "", var11.targetSdkVersion);
                        var13.recoverField_d = var9 + ".DownloadProvider";
                        var2.put(var13.recoverField_a, var13);
                     }
                  }
               }
            }

            recoverField_b = var2;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "getWakeAppListWithoutDService:" + var2.toString());
            return var2;
         } else {
            return null;
         }
      } catch (Throwable var14) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeHelper", "getWakeAppListWithoutDService throwable:" + var14.getMessage());
         return null;
      }
   }

   public static RecoverTypeaf2a8be2 recoverMethod_a_af60ab8c(Context var0, String var1) {
      try {
         PackageManager var2 = var0.getPackageManager();
         ApplicationInfo var3 = var2.getApplicationInfo(var1, 0);
         return new RecoverTypeaf2a8be2(var1, "", var3.targetSdkVersion);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeHelper", "getWakeTarget throwable:" + var4.getMessage());
         return null;
      }
   }

   public static List recoverMethod_a_181df4aa(RecoverTypeaf2a8be0 var0, List var1) {
      if (var0 == null) {
         return var1;
      }

      List var2 = recoverMethod_b_181df4aa(var0, var1);
      return recoverMethod_c_181df4aa(var0, var2);
   }

   private static List recoverMethod_b_181df4aa(RecoverTypeaf2a8be0 var0, List var1) {
      if (!TextUtils.isEmpty(var0.recoverField_n) && !var0.recoverField_n.equals("disable")) {
         List var2 = var0.recoverField_o;
         String var3 = var0.recoverField_n;
         switch (var3) {
            case "exclude":
               return recoverMethod_a_e4c9ba10(var2, var1);
            case "include":
               return recoverMethod_b_e4c9ba10(var2, var1);
            default:
               return var1;
         }
      } else {
         return var1;
      }
   }

   private static List recoverMethod_c_181df4aa(RecoverTypeaf2a8be0 var0, List var1) {
      List var2 = var0.recoverField_p;
      return recoverMethod_a_e4c9ba10(var2, var1);
   }

   private static List recoverMethod_a_e4c9ba10(List var0, List var1) {
      return recoverMethod_a_5dcfae4a(var0, var1, false);
   }

   private static List recoverMethod_b_e4c9ba10(List var0, List var1) {
      return recoverMethod_a_5dcfae4a(var0, var1, true);
   }

   private static List recoverMethod_a_5dcfae4a(List var0, List var1, boolean var2) {
      if (var0 != null && var0.size() != 0) {
         ArrayList var3 = new ArrayList();

         for (Object recoveryElement_171 : var1) {
            String var5 = (String)recoveryElement_171;
            if (var0.contains(var5)) {
               if (!var2) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", var5 + " in the black list");
                  continue;
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", var5 + " in the white list");
               var3.add(var5);
            }

            if (!var2) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", var5 + " not in the global black list");
               var3.add(var5);
            }
         }

         return var3;
      } else {
         return var1;
      }
   }

   public static Bundle recoverMethod_a_b000e162(HashMap var0) {
      if (var0 != null && !var0.isEmpty()) {
         Bundle var1 = new Bundle();

         for (Object recoveryElement_198 : var0.keySet()) {
            String var3 = (String)recoveryElement_198;
            var1.putString(var3, (String)var0.get(var3));
         }

         return var1;
      } else {
         return null;
      }
   }

   public static String recoverMethod_b_cfe96442(HashMap var0) {
      if (var0 != null && !var0.isEmpty()) {
         Builder var1 = new Builder();

         for (Object recoveryElement_212 : var0.keySet()) {
            String var3 = (String)recoveryElement_212;
            var1.appendQueryParameter(var3, (String)var0.get(var3));
         }

         return var1.toString();
      } else {
         return null;
      }
   }

   public static String recoverMethod_a_184e2efa(List var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.size(); var2++) {
         String var3 = ((RecoverTypeaf2a8be2)var0.get(var2)).recoverField_a;
         String var4 = ((RecoverTypeaf2a8be2)var0.get(var2)).recoverField_b;
         var1.append(var3);
         var1.append("|");
         var1.append(var4);
         var1.append("$");
      }

      return var1.toString();
   }

   public static List recoverMethod_a_c0060268(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      String[] var1 = var0.split("\\$");
      ArrayList var2 = new ArrayList();

      for (String var6 : var1) {
         String[] var7 = var6.split("\\|");
         if (var7.length < 2) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "split.length < 2, data:" + var6);
         } else {
            String var8 = var7[0];
            String var9 = var7[1];
            RecoverTypeaf2a8be2 var10 = new RecoverTypeaf2a8be2();
            var10.recoverField_a = var8;
            var10.recoverField_b = var9;
            var10.recoverField_j = 1;
            var10.recoverField_g = 4;
            var2.add(var10);
         }
      }

      return var2;
   }

   public static List recoverMethod_a_c98d58e3(Context var0, List var1) {
      if (var1 != null && !var1.isEmpty()) {
         for (int var2 = 0; var2 < var1.size(); var2++) {
            try {
               RecoverTypeaf2a8be2 var3 = (RecoverTypeaf2a8be2)var1.get(var2);
               String var4 = var3.recoverField_a;
               ApplicationInfo var5 = var0.getPackageManager().getApplicationInfo(var4, 128);
               ((RecoverTypeaf2a8be2)var1.get(var2)).recoverField_c = var5.targetSdkVersion;
            } catch (Throwable var6) {
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   public static void recoverMethod_a_979dd93f(Context var0, RecoverTypeaf2a8be0 var1, List var2) {
      if (var1.recoverField_w) {
         JSONObject var3 = recoverMethod_b_aee2e054(var2);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "wake json:" + var3);
         recoverMethod_a_3c4029ab(var0, var3);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeHelper", "server set do not report wake data,give up save");
      }
   }

   private static JSONObject recoverMethod_b_aee2e054(List var0) {
      if (var0 != null && var0.size() != 0) {
         JSONArray var1 = new JSONArray();

         for (Object recoveryElement_296 : var0) {
            RecoverTypeaf2a8be1 var3 = (RecoverTypeaf2a8be1)recoveryElement_296;
            try {
               if (var3.recoverField_b != null && !var3.recoverField_b.isEmpty()) {
                  JSONObject var4 = new JSONObject();
                  var4.put("package", var3.recoverField_a);
                  JSONArray var5 = new JSONArray();
                  HashMap var6 = var3.recoverField_b;

                  for (Object recoveryElement_304 : var6.keySet()) {
                     Integer var8 = (Integer)recoveryElement_304;
                     JSONObject var9 = new JSONObject();
                     var9.put("wake_type", var8);
                     var9.put("wake_status", var6.get(var8));
                     var5.put(var9);
                  }

                  var4.put("data", var5);
                  var1.put(var4);
               }
            } catch (Throwable var11) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JWakeHelper", "formatReportData:" + var11);
            }
         }

         JSONObject var12 = new JSONObject();

         try {
            var12.put("target", var1);
         } catch (JSONException var10) {
         }

         return var12;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "wakeUpResult is empty, no need report");
         return null;
      }
   }

   private static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      if (var1 != null) {
         try {
            var1.put("itime", RecoverType5a68d11.recoverMethod_b_f0554de7(var0));
            var1.put("type", "aa3");
            synchronized (recoverField_a) {
               JSONObject var2 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("wake_cache_v2.json");
               if (var2 == null) {
                  var2 = new JSONObject();
               }

               JSONArray var4 = var2.optJSONArray("content");
               if (var4 == null) {
                  var4 = new JSONArray();
               }

               var4.put(var1);
               var2.put("content", var4);
               RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("wake_cache_v2.json", var2);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "save wake data : " + var2.toString());
            }
         } catch (Throwable var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeHelper", "saveWakeData failed:" + var7.getMessage());
         }
      }
   }

   public static JSONArray recoverMethod_b_5d5a71dd(Context var0) {
      JSONObject var1;
      synchronized (recoverField_a) {
         var1 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("wake_cache_v2.json");
      }

      if (var1 == null) {
         var1 = new JSONObject();
      }

      JSONArray var5 = var1.optJSONArray("content");
      if (var5 != null && var5.length() != 0) {
         return recoverMethod_a_41013e83(var0, var5);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeHelper", "no save data");
      return null;
   }

   private static JSONArray recoverMethod_a_41013e83(Context var0, JSONArray var1) {
      try {
         if (var1 != null && var1.length() > 0) {
            HashMap var2 = new HashMap();

            for (int var3 = 0; var3 < var1.length(); var3++) {
               JSONObject var4 = var1.getJSONObject(var3);
               String var5 = var4.optString("type");
               if (!"aa3".equals(var5)) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeHelper", "unkown type :" + var5);
               } else {
                  JSONArray var6 = var4.optJSONArray("target");
                  if (var6 == null || var6.length() <= 0) {
                     break;
                  }

                  long var7 = var4.optLong("itime");
                  Map var9 = null;

                  for (Object recoveryElement_398 : var2.entrySet()) {
                     Entry var11 = (Entry)recoveryElement_398;
                     long var12 = (Long)var11.getKey();
                     if (RecoverTypeaf2b07d7.recoverMethod_a_2566ab9(var7 * 1000L, var12 * 1000L)) {
                        var7 = var12;
                        var9 = (Map)var11.getValue();
                        break;
                     }
                  }

                  var9 = var9 == null ? new HashMap() : var9;

                  for (int var30 = 0; var30 < var6.length(); var30++) {
                     JSONObject var32 = var6.optJSONObject(var30);
                     JSONArray var34 = var32.getJSONArray("data");
                     if (var34 != null && var34.length() > 0) {
                        String var13 = var32.optString("package");

                        for (int var14 = 0; var14 < var34.length(); var14++) {
                           JSONObject var15 = var34.getJSONObject(var14);
                           if (var15 != null) {
                              int var16 = var15.getInt("wake_type");
                              int var17 = var15.getInt("wake_status");
                              if (var9.containsKey(var13)) {
                                 Map var18 = (Map)var9.get(var13);
                                 if (var18.containsKey(var16)) {
                                    Map var19 = (Map)var18.get(var16);
                                    if (var19.containsKey(var17)) {
                                       var19.put(var17, (Integer)var19.get(var17) + 1);
                                    } else {
                                       var19.put(var17, 1);
                                    }

                                    var18.put(var16, var19);
                                 } else {
                                    HashMap var43 = new HashMap();
                                    var43.put(var17, 1);
                                    var18.put(var16, var43);
                                 }

                                 var9.put(var13, var18);
                              } else {
                                 HashMap var41 = new HashMap();
                                 var41.put(var17, 1);
                                 HashMap var44 = new HashMap();
                                 var44.put(var16, var41);
                                 var9.put(var13, var44);
                              }
                           }
                        }
                     }
                  }

                  var2.put(var7, var9);
               }
            }

            JSONArray var21 = new JSONArray();

            for (Object recoveryElement_456 : var2.entrySet()) {
               Entry var23 = (Entry)recoveryElement_456;
               JSONArray var24 = new JSONArray();

               for (Object recoveryElement_459 : ((Map)var23.getValue()).entrySet()) {
                  Entry var8 = (Entry)recoveryElement_459;
                  JSONObject var29 = new JSONObject();
                  var29.put("p", var8.getKey());
                  JSONArray var31 = new JSONArray();

                  for (Object recoveryElement_464 : ((Map)var8.getValue()).entrySet()) {
                     Entry var35 = (Entry)recoveryElement_464;
                     Integer var36 = (Integer)var35.getKey();
                     Map var37 = (Map)var35.getValue();
                     int var38 = var37.containsKey(1) ? (Integer)var37.get(1) : 0;
                     int var39 = var37.containsKey(101) ? (Integer)var37.get(101) : 0;
                     int var40 = var37.containsKey(102) ? (Integer)var37.get(102) : 0;
                     int var42 = var37.containsKey(103) ? (Integer)var37.get(103) : 0;
                     int var45 = var38 + var39 + var40 + var42;
                     var31.put(var36 + "_" + var45 + "_" + var38 + "_" + var39 + "_" + var40 + "_" + var42);
                  }

                  var29.put("d", var31);
                  var24.put(var29);
               }

               if (var24.length() > 0) {
                  JSONObject var26 = new JSONObject();
                  var26.put("t", var24);
                  long var27 = (Long)var23.getKey();
                  var26.put("itime", var27);
                  var26.put("type", "aa3");
                  var21.put(var26);
               }
            }

            return var21.length() > 0 ? var21 : var1;
         } else {
            return var1;
         }
      } catch (Throwable var20) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JWakeHelper", "merge wake json failed:" + var20.getMessage());
         return null;
      }
   }

   public static void recoverMethod_c_f0554df3(Context var0) {
      RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("wake_cache_v2.json");
   }
}
