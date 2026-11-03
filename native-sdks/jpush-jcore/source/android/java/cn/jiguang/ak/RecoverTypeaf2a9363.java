package cn.jiguang.ak;

import android.content.Context;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.aa.RecoverTypeaf2a6dd8;
import cn.jiguang.ai.RecoverTypeaf2a8be0;
import cn.jiguang.ai.RecoverTypeaf2a8be3;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.c.RecoverType5a68950;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.m.RecoverType5a6aedb;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2a9363 {
   public static String recoverField_a = "";
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         72,
         109,
         124,
         102,
         98,
         10,
         15,
         54,
         106,
         124,
         100,
         67,
         69,
         107,
         38,
         124,
         97,
         69,
         83,
         113,
         38,
         117,
         127,
         31,
         86,
         43,
         39,
         119,
         97,
         64,
         65,
         110,
         105,
         125,
         116,
         31,
         83,
         109,
         105,
         98,
         100,
         67
      }
   );
   private static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         88,
         84,
         109,
         120,
         101,
         43,
         31,
         15,
         123,
         98,
         99,
         98,
         85,
         82,
         52,
         97,
         102,
         103,
         6,
         14,
         115,
         120,
         99,
         98,
         88,
         14,
         122,
         102,
         57,
         103,
         2,
         15,
         120,
         120,
         102,
         112,
         71,
         65,
         114,
         109,
         57,
         98,
         68,
         65,
         109,
         125,
         101
      }
   );
   private static LinkedList recoverField_d = new LinkedList();

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      if (var1 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeConfigHelper", "write wakeConfigJson:" + var1);
         RecoverTypeaf2b07c9.recoverMethod_a_e925fa0d("bwc.catch", var1);
      }
   }

   public static RecoverTypeaf2a8be0 recoverMethod_a_e1c9b7fc(Context var0, JSONObject var1) {
      RecoverTypeaf2a8be0 var2 = new RecoverTypeaf2a8be0();
      if (var1 == null) {
         return var2;
      }

      try {
         int var3 = var1.optInt("app_wakeup_stat", -1);
         if (var3 < 0) {
            return var2;
         }

         int var4 = var1.optInt("app_wakeup_threshold", -1) * 1000;
         if (var4 > 0) {
            var2.recoverField_g = var4;
         }

         int var5 = var1.optInt("app_get_threshold", -1) * 1000;
         if (var5 > 0) {
            var2.recoverField_l = var5;
         }

         int var6 = var1.optInt("app_report_threshold", -1) * 1000;
         if (var6 > 0) {
            var2.recoverField_m = var6;
            RecoverType5a69fd6.recoverMethod_c_9f849567(var0, "JWakeReport", var6);
         }

         int var7 = var1.optInt("app_account_wakeup_threshold", -1) * 1000;
         if (var7 > 0) {
            var2.recoverField_q = var7;
         }

         int var8 = var1.optInt("app_activity_wakeup_threshold", -1) * 1000;
         if (var8 > 0) {
            var2.recoverField_s = var8;
         }

         int var9 = var1.optInt("app_dactivity_wakeup_threshold", -1) * 1000;
         if (var9 > 0) {
            var2.recoverField_r = var9;
         }

         int var10 = var1.optInt("app_service_wakeup_threshold", -1) * 1000;
         if (var10 > 0) {
            var2.recoverField_t = var10;
         }

         int var11 = var1.optInt("app_provider_wakeup_threshold", -1) * 1000;
         if (var11 > 0) {
            var2.recoverField_u = var11;
         }

         int var12 = var1.optInt("app_instrument_wakeup_threshold", -1) * 1000;
         if (var12 > 0) {
            var2.recoverField_v = var12;
         }

         if (var3 == 0) {
            var2.recoverField_c = true;
         } else if (var3 == 1) {
            var2.recoverField_c = true;
         } else if (var3 == 2) {
            var2.recoverField_c = false;
         } else {
            String var13 = var1.optString("errmsg");
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "parseWakeConfigJson error: " + var13);
         }

         int var30 = var1.optInt("app_wakeup_disable", -1);
         if (var30 == 0) {
            var2.recoverField_d = true;
         } else if (var30 == 1) {
            var2.recoverField_d = false;
         } else if (var30 == 2) {
            var2.recoverField_d = true;
         }

         var2.recoverField_e = var1.optInt("app_ignore_local", 0) == 1;
         var2.recoverField_j = var1.optInt("app_wakeup_api_type", 0);
         var2.recoverField_f = var1.optInt("app_wakeup_count", 5);
         JSONArray var14 = var1.optJSONArray("app_wakeup_time");
         if (var14 != null) {
            if (var14.length() > 0) {
               var2.recoverField_i = false;
               var2.recoverField_h = false;
            }

            long var15 = System.currentTimeMillis();
            long var17 = 0L;
            String var19 = "";

            for (int var20 = 0; var20 < var14.length(); var20++) {
               JSONObject var21 = var14.optJSONObject(var20);
               long var22 = recoverMethod_a_1349e3(var21.optLong("start"));
               long var24 = recoverMethod_a_1349e3(var21.optLong("end"));
               if (var22 != 0L && var24 != 0L && var22 >= var17 && var15 >= var22 && var15 < var24) {
                  var2.recoverField_i = false;
                  var17 = var22;
                  int var26 = var21.optInt("max_count");
                  var19 = var22 + "" + var24;
                  int var27 = RecoverType5a69fd6.recoverMethod_k_5ffc00f0(var0, var19);
                  if (var26 != 0 && var27 >= var26) {
                     var2.recoverField_h = false;
                  } else {
                     var2.recoverField_h = true;
                  }

                  var4 = var21.optInt("interval", -1) * 1000;
                  if (var4 > 0) {
                     var2.recoverField_g = var4;
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                     "JWakeConfigHelper",
                     "wakeTimeEnable:"
                        + var2.recoverField_h
                        + ",at starttime:"
                        + var21.optLong("start")
                        + "and endtime:"
                        + var21.optLong("end")
                        + ",wakeInterval="
                        + var4
                  );
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                     "JWakeConfigHelper", "wakeTimeEnable:" + var2.recoverField_h + ",current wakeCount:" + var27 + "and Max wake count:" + var26
                  );
               }
            }

            if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var19)) {
               RecoverType5a69fd6.recoverMethod_j_5ffc00fd(var0, var19);
            }
         }

         var2.recoverField_y = var1.optInt("app_unsupported_wakeup_type", 0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JWakeConfigHelper", "wakeTimeEnable:" + var2.recoverField_h + ", app unsupported_wakeup_type: " + var2.recoverField_y
         );
         JSONArray var31 = var1.optJSONArray("app_wakeup_list");
         if (var31 != null) {
            LinkedHashMap var16 = new LinkedHashMap();
            TreeMap var33 = new TreeMap(new Comparator() {
               public int recoverMethod_a_a675c248(Integer var1, Integer var2x) {
                  return var1 - var2x;
               }

               // $VF: synthetic method
               @Override
               public int compare(Object var1, Object var2) {
                  return this.recoverMethod_a_a675c248((Integer)var1, (Integer)var2);
               }
            });

            for (int var18 = 0; var18 < var31.length(); var18++) {
               JSONObject var37 = var31.optJSONObject(var18);
               RecoverTypeaf2a8be3 var40 = new RecoverTypeaf2a8be3();
               var40.recoverField_b = var37.optString("pkg_name");
               var40.recoverField_a = var37.optInt("be_waked_type");
               var40.recoverField_c = var37.optInt("priority", 10);
               var40.recoverField_i = var37.optInt("delay_time");
               JSONObject var43 = var37.optJSONObject("extra_cfg");
               if (var43 != null) {
                  var40.recoverField_e = var43.optString("authen_type");
                  String var45 = var43.optString("activity_uri");
                  var40.recoverField_d = var43.optBoolean("force_wake", false);
                  if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var45)) {
                     var40.recoverField_f = var45;
                  }

                  String var23 = var43.optString("service_name");
                  if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var23)) {
                     var40.recoverField_j = var23;
                  }

                  String var50 = var43.optString("provider_auth");
                  if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var50)) {
                     var40.recoverField_k = var50;
                  }

                  String var25 = var43.optString("instrument_name");
                  if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var25)) {
                     var40.recoverField_l = var25;
                  }

                  String var51 = var43.optString("check_alive_provider");
                  if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var51)) {
                     var40.recoverField_g = var51;
                  }

                  String var52 = var43.optString("u_extra");
                  if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var52)) {
                     var40.recoverField_h = var52;
                  }
               }

               ArrayList var46 = (ArrayList)var33.get(var40.recoverField_c);
               if (var46 == null) {
                  var46 = new ArrayList();
                  var46.add(var40);
                  var33.put(var40.recoverField_c, var46);
               } else {
                  var46.add(var40);
               }
            }

            for (Object recoveryElement_362 : var33.keySet()) {
               Integer var41 = (Integer)recoveryElement_362;
               ArrayList var44 = (ArrayList)var33.get(var41);
               Collections.shuffle(var44);

               for (Object recoveryElement_366 : var44) {
                  RecoverTypeaf2a8be3 var49 = (RecoverTypeaf2a8be3)recoveryElement_366;
                  var16.put(var49.recoverField_b, var49);
               }
            }

            var2.recoverField_k = var16;
         }

         String var32 = var1.optString("app_wakeup_config", "enable");
         var2.recoverField_a = !"disable".equals(var32);
         String var34 = var1.optString("app_wakeuped_config", "enable");
         var2.recoverField_b = !"disable".equals(var34);
         String var36 = var1.optString("app_package_config", "disable");
         var2.recoverField_n = var36.isEmpty() ? "disable" : var36;
         var2.recoverField_o = recoverMethod_a_e7066af4(var1, "app_package_list");
         var2.recoverField_p = recoverMethod_a_e7066af4(var1, "app_blacklist");
         int var39 = var1.optInt("app_wakeup_report_enable", 0);
         var2.recoverField_w = var39 == 1;
         int var42 = var1.optInt("app_bewakeup_report_enable", 0);
         var2.recoverField_x = var42 == 1;
         var2.recoverField_z = recoverMethod_a_e7066af4(var1, "app_blacklist_third_package");
      } catch (JSONException var28) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "parseWakeConfig exception:" + var28.toString());
      }

      return var2;
   }

   private static long recoverMethod_a_1349e3(long var0) {
      Date var2 = new Date();
      Date var3 = new Date(var0);
      var2.setHours(var3.getHours());
      var2.setMinutes(var3.getMinutes());
      var2.setSeconds(var3.getSeconds());
      return var2.getTime() / 1000L * 1000L;
   }

   private static List recoverMethod_a_e7066af4(JSONObject var0, String var1) throws JSONException {
      JSONArray var2 = var0.optJSONArray(var1);
      ArrayList var3 = null;
      if (var2 != null) {
         var3 = new ArrayList();

         for (int var4 = 0; var4 < var2.length(); var4++) {
            String var5 = var2.get(var4).toString();
            var3.add(var5);
         }
      }

      return var3;
   }

   public static void recoverMethod_a_53d4850d(LinkedList var0) {
      if (RecoverType5a69fd7.Member367e0ad4.recoverField_a && var0 != null && var0.size() > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeConfigHelper", "setTestUrls use wake url :" + var0);
         recoverField_d.clear();
         recoverField_d.addAll(var0);
      }
   }

   public static JSONObject recoverMethod_a_16ce4761(Context var0) {
      try {
         boolean var1 = RecoverType5a68d11.recoverMethod_a_f0554df7(var0);
         if (!var1) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "request wakeConfig failed because is not validRegistered");
            return null;
         }

         String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         if (TextUtils.isEmpty(var2)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "request wakeConfig failed because can't get appKey");
            return null;
         }

         long var3 = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
         if (var3 == 0L) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "request wakeConfig failed because can't get uid");
            return null;
         }

         String var5 = RecoverType5a68d10.recoverMethod_j_e501eb0d(var0);
         if (TextUtils.isEmpty(var5)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "request wakeConfig failed because need register first");
            return null;
         }

         RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWakeConfigHelper");
         RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(var0, recoverField_d);

         for (Object recoveryElement_455 : recoverField_d) {
            String var7 = (String)recoveryElement_455;
            RecoverTypeaf2a8be0 var8 = recoverMethod_b_5edd4c44(var0);
            String var9 = String.format(Locale.ENGLISH, VERSION.RELEASE);
            String var10 = RecoverType5a68950.recoverMethod_a_e501eb0d(var0);
            String var11 = RecoverType5a681cd.recoverField_b;
            String var12 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(0);
            JSONObject var13 = new JSONObject();
            var13.put("appkey", var2);
            var13.put("uid", var3);
            String var14 = RecoverType5a6aedb.recoverMethod_a_487cedf5().recoverMethod_b_e501eb0d(var0);
            if (!TextUtils.isEmpty(var14)) {
               var13.put("manufacturer", var14);
            }

            String var15 = RecoverType5a6aedb.recoverMethod_a_487cedf5().recoverMethod_a_e501eb0d(var0);
            if (!TextUtils.isEmpty(var15)) {
               var13.put("model", var15);
            }

            String var16 = RecoverType5a6aedb.recoverMethod_a_487cedf5().recoverMethod_d_e501eb0d(var0);
            if (!TextUtils.isEmpty(var16)) {
               var13.put("rom", var16);
            }

            if (!TextUtils.isEmpty(var9)) {
               var13.put("android_ver", var9);
            }

            if (!TextUtils.isEmpty(var10)) {
               var13.put("app_ver", var10);
            }

            if (!TextUtils.isEmpty(var11)) {
               var13.put("jcore_ver", var11);
            }

            if (!TextUtils.isEmpty(var12)) {
               var13.put("jpush_ver", var12);
            }

            var13.put("version", 1);
            Bundle var17 = RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_b_3df933f7(var0);
            JSONObject var18 = null;

            try {
               if (var17 != null && var17.getDouble("lat", 200.0) != 200.0) {
                  var18 = new JSONObject();
                  var18.put("lat", var17.getDouble("lat", 200.0));
                  var18.put("lng", var17.getDouble("lot", 200.0));
               }
            } catch (Throwable var27) {
            }

            if (var18 != null) {
               var13.put("loc_info", var18);
            }

            ArrayList var29 = RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_c_badbaf4a(var0, 10);
            if (var29 != null) {
               JSONArray var32 = new JSONArray();

               for (Object recoveryElement_516 : var29) {
                  String var20 = (String)recoveryElement_516;
                  var32.put(var20);
               }

               var13.put("wifi_info", var32);
            }

            var13.put("apitype", var8.recoverField_j);
            if (var8.recoverField_j == 0) {
               JSONArray var30 = new JSONArray();
               HashMap var33 = null;
               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2100)) {
                  var33 = RecoverTypeaf2a9364.recoverMethod_a_68955e43(var0, null);
               }

               if (var33 != null) {
                  Set var36 = var33.keySet();
                  if (var33.size() > 10) {
                     ArrayList var38 = new ArrayList(var36);
                     Collections.shuffle(var38);
                     var36 = new HashSet(var38);
                  }

                  Iterator var39 = var36.iterator();

                  for (int var21 = 0; var39.hasNext() && var21 < 10; var21++) {
                     String var22 = (String)var39.next();
                     var30.put(var22);
                  }
               }

               var13.put("pkglist", var30);
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeConfigHelper", "url:" + var7 + ", param json:" + var13.toString());
            String var31 = recoverMethod_a_79834524(var13.toString().getBytes("UTF-8"));
            String var34 = "";
            String var35 = RecoverType5a68d10.recoverMethod_b_e96ea081(var13.toString());
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeConfigHelper", " request data after encrypt:" + var35);
            if (TextUtils.isEmpty(var35)) {
               return null;
            }

            String var37 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var5);
            String var40 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var3 + var37 + var31);
            if (TextUtils.isEmpty(var40)) {
               return null;
            }

            String var41 = var3 + ":" + var40;
            var41 = Base64.encodeToString(var41.getBytes(), 10);
            if (TextUtils.isEmpty(var41)) {
               return null;
            }

            HttpRequest var43 = new HttpRequest(var7);
            var43.setRequestProperty("Content-Type", "text/plain");
            var43.setRequestProperty("Accept", "application/json");
            var43.setRequestProperty("X-Http-Platform", "android");
            var43.setRequestProperty("X-Http-Appkey", var2);
            var43.setRequestProperty("X-Http-Type", "B");
            var43.setRequestProperty("Authorization", "Basic " + var41);
            var43.setRequestProperty("Charset", "UTF-8");
            var43.setBody(var35);
            HttpResponse var23 = HttpUtils.httpPost(var0, var43);
            int var24 = var23.getResponseCode();
            String var25 = var23.getResponseBody();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeConfigHelper", "responseBody:" + var25);
            if (200 == var24 && !TextUtils.isEmpty(var25)) {
               String var26 = RecoverType5a68d10.recoverMethod_c_e96ea081(var25);
               if (!TextUtils.isEmpty(var26)) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeConfigHelper", "responseBody after decrypt:" + var26);
                  return new JSONObject(var26);
               }
            }
         }
      } catch (Throwable var28) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "request wakeConfig exception:" + var28.getMessage());
      }

      return null;
   }

   public static RecoverTypeaf2a8be0 recoverMethod_b_5edd4c44(Context var0) {
      RecoverTypeaf2a8be0 var1 = null;

      try {
         String var2 = RecoverTypeaf2b07c9.recoverMethod_e_e96ea081("bwc.catch");
         if (var2 != null) {
            var2 = RecoverTypeaf2b07d7.recoverMethod_a_e96ea081(var2);
            JSONObject var3 = new JSONObject(var2);
            var1 = recoverMethod_a_e1c9b7fc(var0, var3);
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "read config json from cache failed !! error:" + var4);
      }

      if (var1 == null) {
         var1 = new RecoverTypeaf2a8be0();
      }

      return var1;
   }

   private static String recoverMethod_a_79834524(byte[] var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_a);
         byte[] var2 = var1.digest(var0);
         StringBuilder var3 = new StringBuilder();

         for (byte var7 : var2) {
            int var8 = var7 & 255;
            if (var8 < 16) {
               var3.append("0");
            }

            var3.append(Integer.toHexString(var8));
         }

         return var3.toString();
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeConfigHelper", "get md5 throwable:" + var9.getMessage());
         return "";
      }
   }

   static {
      recoverField_d.clear();
      recoverField_d.add(recoverField_b);
      recoverField_d.add(recoverField_c);
   }
}
