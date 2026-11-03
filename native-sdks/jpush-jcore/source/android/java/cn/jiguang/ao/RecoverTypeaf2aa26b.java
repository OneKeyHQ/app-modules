package cn.jiguang.ao;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.api.ReportCallBack;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.at.RecoverTypeaf2ab532;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c7;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07cd;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.c.RecoverType5a68950;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JConstants;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2aa26b {
   private static final String recoverField_j = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{62, 82, 38, 113, 19, 93, 73, 85, 84, 2, 23, 92, 58, 34, 40, 115, 19, 47, 78, 84, 32, 3, 19, 40, 57, 83, 32, 114, 23, 93, 49, 82}
   );
   private static String recoverField_k = "";
   private static String recoverField_l = "/v3/report";
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{33, 1, 16, 43, 56, 37, 33, 4, 16, 44, 56, 32, 33, 7, 16, 33}
   );
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{120, 95, 80, 43, 56, 37, 33, 4, 16, 44, 56, 32, 112, 96, 75, 56}
   );
   public static JSONObject recoverField_c = null;
   public static boolean recoverField_d = true;
   private static ConcurrentHashMap recoverField_m = new ConcurrentHashMap();
   private static Handler recoverField_n = new Handler(recoverMethod_b_8fa5950a());
   public static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{123, 98, 112, 68, 83, 55, 98, 102, 100, 67, 72, 55, 107, 120}
   );
   public static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{106, 124, 119, 101, 67, 13, 112, 120, 96, 39, 30, 74, 105, 125, 101, 121, 30, 67, 119}
   );
   public static String recoverField_g = "";
   public static String recoverField_h = "";
   private static LinkedList recoverField_o = new LinkedList();
   private static boolean recoverField_p = false;
   public static boolean recoverField_i = true;
   private static Map recoverField_q = new HashMap();

   public static void recoverMethod_a_4fa28b99(Context var0, String var1, Object var2) {
      try {
         try {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "going to report data at push service");
            Bundle var3 = new Bundle();
            if (!TextUtils.isEmpty(var1)) {
               var3.putString("sdk_type", var1);
            }

            if (recoverMethod_b_5d527815(var2)) {
               var3.putString("report_data", var2.toString());
            }

            RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "a1", var3);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("ReportUtils", "reportAtPushService", var4);
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("ReportUtils", "reportAtPushService", var5);
      }
   }

   public static void recoverMethod_a_127ac70f(final Context var0, final Object var1) {
      try {
         if (recoverMethod_b_5d527815(var1)) {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("UPLOAD_REPORT", new RecoverTypeaf2b0401("ReportUtils#report") {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     JSONArray var1x = RecoverTypeaf2aa26b.recoverMethod_c_ff6026ff(var1);
                     if (var1x != null) {
                        Set var2 = RecoverTypeaf2aa26b.recoverMethod_c_c8b8153e(var1x);
                        if (RecoverTypeaf2aa26b.recoverMethod_b_abc98aae(var2)) {
                           RecoverTypeaf2aa26b.recoverMethod_c_ba8dc12(var0, var1x, var2);
                        } else {
                           RecoverTypeaf2aa26b.recoverMethod_d_ba8dc12(var0, var1x, var2);
                        }
                     } else {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "data" + var1 + " is empty");
                     }
                  } catch (Throwable var3) {
                  }
               }
            });
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "data is invalid or empty");
            RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "report e:" + var3);
      }
   }

   private static boolean recoverMethod_b_abc98aae(Set var0) {
      return var0 == null
         ? false
         : var0.contains("active_launch")
            || var0.contains("active_terminate")
            || var0.contains("sdk_type")
            || var0.contains("android_notification_state")
            || var0.contains("operate_event");
   }

   public static void recoverMethod_a_6892eae2(Context var0, JSONObject var1, ReportCallBack var2) {
      recoverMethod_b_6892eae2(var0, var1, var2);
   }

   public static void recoverMethod_a_77286e9d(
      Context var0, int var1, JSONObject var2, byte[] var3, int var4, File var5, Set var6, ReportCallBack var7, String var8
   ) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "onTcpReportResult, types=" + var6 + " code=" + var1 + " id=" + var8 + " ,store:" + var5);
         switch (var1) {
            case -3:
               RecoverTypeaf2aa269.recoverMethod_a_2213544c(var0, RecoverType5a68d10.recoverMethod_e_e501eb0d(var0));
               RecoverTypeaf2aa269.recoverMethod_a_1385ff(true);
               break;
            case 0:
               if (JConstants.DEBUG_MODE) {
               }

               if (TextUtils.isEmpty(var8)) {
                  RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var5);
               } else {
                  recoverMethod_a_afa340b5(var8, true);
               }

               RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
               break;
            default:
               var1 = recoverMethod_a_c1ac349(var0, var3, var4, var6);
               if (var1 == 0) {
                  if (JConstants.DEBUG_MODE) {
                  }

                  if (TextUtils.isEmpty(var8)) {
                     RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var5);
                  } else {
                     recoverMethod_a_afa340b5(var8, true);
                  }

                  RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
               } else {
                  recoverMethod_a_afa340b5(var8, false);
                  RecoverTypeaf2aa269.recoverMethod_a_1385ff(true);
               }
         }
      } catch (Throwable var13) {
      } finally {
         if (0 == var1) {
            recoverMethod_a_afa340b5(var8, true);
         } else {
            recoverMethod_a_afa340b5(var8, false);
         }

         if (var7 != null) {
            var7.onFinish(var1);
         }

         if (TextUtils.isEmpty(var8)) {
            RecoverTypeaf2aa269.recoverMethod_a_4ad1f42c(var5);
         }
      }
   }

   private static boolean recoverMethod_b_5d527815(Object var0) {
      if (var0 instanceof String) {
         return ((String)var0).length() > 2;
      } else if (var0 instanceof JSONObject) {
         return ((JSONObject)var0).length() > 0;
      } else {
         return var0 instanceof JSONArray ? recoverMethod_b_759da2c1((JSONArray)var0).length() > 0 : false;
      }
   }

   private static JSONArray recoverMethod_c_ff6026ff(Object var0) {
      JSONArray var1 = null;

      try {
         if (var0 instanceof String) {
            try {
               var1 = new JSONArray((String)var0);
            } catch (Throwable var5) {
               try {
                  var1 = new JSONArray().put(new JSONObject((String)var0));
               } catch (Throwable var4) {
               }
            }
         } else if (var0 instanceof JSONObject) {
            var1 = new JSONArray().put(var0);
         } else if (var0 instanceof JSONArray) {
            var1 = (JSONArray)var0;
         }

         var1 = recoverMethod_b_759da2c1(var1);
         if (var1 != null && var1.length() > 0) {
            return var1;
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "adapt JSONArray e:" + var6);
      }

      return null;
   }

   private static JSONArray recoverMethod_b_759da2c1(JSONArray var0) {
      if (var0 != null && var0.length() != 0) {
         JSONArray var1 = new JSONArray();

         for (int var2 = 0; var2 < var0.length(); var2++) {
            JSONObject var3 = var0.optJSONObject(var2);
            if (var3 != null && var3.length() > 0) {
               var1.put(var3);
            }
         }

         return var1;
      } else {
         return var0;
      }
   }

   private static void recoverMethod_c_ba8dc12(Context var0, JSONArray var1, Set var2) {
      try {
         String var3 = RecoverTypeaf2aa266.recoverMethod_b_c6941000().recoverMethod_b_5ee0067e(var0).recoverMethod_a_b555d336(var2);
         JSONObject var4 = recoverMethod_a_16ce4761(var0);
         boolean var5 = var4 != null;
         String var6 = File.separator + var3 + File.separator + (var5 ? "tmp" : "nowrap");
         Integer var7 = null;

         for (Object recoveryElement_276 : recoverMethod_a_e3dce89(var1, 40960, 204800)) {
            JSONArray var10 = (JSONArray)recoveryElement_276;
            try {
               JSONObject var11 = recoverMethod_a_c699c4fd(var10, var4);
               File var12 = RecoverTypeaf2aa269.recoverMethod_a_d8504dda(var0, var6, var11, var5);
               if (var5) {
                  var7 = recoverMethod_a_c08b9df7(var0, var2, var11, var12, null);
               }
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_c_1765c98c("ReportUtils", "buildReport [for item]", var13);
            }
         }

         if (null != var7 && var7 == 0) {
            RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
         }
      } catch (Throwable var14) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportUtils", "report exception:" + var14);
      }
   }

   private static void recoverMethod_d_ba8dc12(Context var0, JSONArray var1, Set var2) {
      try {
         String var3 = RecoverTypeaf2aa266.recoverMethod_b_c6941000().recoverMethod_b_5ee0067e(var0).recoverMethod_a_b555d336(var2);
         JSONObject var4 = recoverMethod_a_16ce4761(var0);
         boolean var5 = var4 != null;
         String var6 = File.separator + var3 + File.separator + (var5 ? "tmp" : "nowrap");
         Integer var7 = null;

         for (Object recoveryElement_304 : recoverMethod_a_e3dce89(var1, 40960, 204800)) {
            JSONArray var10 = (JSONArray)recoveryElement_304;
            try {
               JSONObject var11 = recoverMethod_a_c699c4fd(var10, var4);
               if (var5) {
                  String var12 = UUID.randomUUID().toString();
                  recoverMethod_a_34bcc529(var6, var11, var12);
                  var7 = recoverMethod_a_b58ecec1(var0, var2, var11, null, null, var12);
               } else {
                  File var15 = RecoverTypeaf2aa269.recoverMethod_a_d8504dda(var0, var6, var11, false);
               }
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_c_1765c98c("ReportUtils", "buildReport [for item]", var13);
            }
         }

         if (null != var7 && var7 == 0) {
            RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
         }
      } catch (Throwable var14) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportUtils", "report exception:" + var14);
      }
   }

   private static Looper recoverMethod_b_8fa5950a() {
      HandlerThread var0 = new HandlerThread("jg_timer") {
         @Override
         public void run() {
            try {
               super.run();
            } catch (Throwable var2) {
            }
         }
      };
      var0.start();
      return var0.getLooper();
   }

   private static void recoverMethod_a_34bcc529(String var0, JSONObject var1, String var2) {
      Context var3 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
      if (var3 != null) {
         try {
            RecoverTypeaf2aa26b.Member8f0bb868 var4 = new RecoverTypeaf2aa26b.Member8f0bb868(var3, var2, var0, var1);
            recoverField_m.put(var4.recoverField_b, var4);
            recoverField_n.postDelayed(var4, 500L);
         } catch (Throwable var5) {
         }
      }
   }

   private static void recoverMethod_a_afa340b5(String var0, boolean var1) {
      if (!TextUtils.isEmpty(var0)) {
         RecoverTypeaf2aa26b.Member8f0bb868 var2 = (RecoverTypeaf2aa26b.Member8f0bb868)recoverField_m.get(var0);
         if (var2 != null) {
            recoverField_n.removeCallbacks(var2);
            if (var1) {
               var2.recoverMethod_b_9b75();
            } else {
               var2.recoverMethod_c_9b75();
            }

            recoverField_m.remove(var2.recoverField_b);
            Object var3 = null;
         }
      }
   }

   private static LinkedList recoverMethod_c_d4e15bd9() {
      return recoverField_o;
   }

   public static LinkedHashSet recoverMethod_a_eafbb042(Context var0, Set var1) {
      LinkedHashSet var2 = recoverMethod_b_eafbb042(var0, var1);
      return recoverMethod_a_eb505c27(var2);
   }

   public static LinkedHashSet recoverMethod_b_eafbb042(Context var0, Set var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      LinkedList var3 = recoverMethod_c_d4e15bd9();
      if (JCoreManager.isTestEnv() && var3 != null) {
         var2.addAll(var3);
         return var2;
      }

      Set var4 = RecoverTypeaf2aa266.recoverMethod_b_c6941000().recoverMethod_c_5ee0067e(var0).recoverMethod_b_40b97519(var1);
      if (var4 != null) {
         for (Object recoveryElement_389 : var4) {
            String var6 = (String)recoveryElement_389;
            if (!TextUtils.isEmpty(var6)) {
               var2.add(var6);
            }
         }
      }

      String var14 = RecoverType5a69fd7.Member367e0ad4.recoverField_d;

      for (Object recoveryElement_398 : var3) {
         String var8 = (String)recoveryElement_398;
         if (!TextUtils.isEmpty(var8)) {
            var2.add(var14 + var8);
         }
      }

      String var16 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_a_cf96baf5(true));
      if (!TextUtils.isEmpty(var16)) {
         String[] var17 = var16.split(",");

         for (String var12 : var17) {
            if (RecoverTypeaf2b07d5.recoverMethod_f_aad3b203(var12)) {
               if (recoverField_i) {
                  var2.add(var14 + var12);
               }
            } else if (RecoverTypeaf2b07d5.recoverMethod_g_aad3b203(var12)) {
               if (!recoverField_i) {
                  var2.add(var14 + var12);
               }
            } else {
               var2.add(var14 + var12);
            }
         }
      }

      var2 = RecoverTypeaf2b07d7.recoverMethod_a_647dad69(var0, var2);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "types=" + var1 + " find after urls=" + var2);
      return var2;
   }

   public static void recoverMethod_a_aad3b1ff(String var0) {
      if (!TextUtils.isEmpty(var0)) {
         recoverField_q.put(var0, System.currentTimeMillis());
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "add failed http url to blacklist: " + var0);
      }
   }

   private static void recoverMethod_d_9b75() {
      try {
         long var0 = System.currentTimeMillis();
         Iterator var2 = recoverField_q.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            if (var0 - (Long)var3.getValue() > 600000L) {
               var2.remove();
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("ReportUtils", "clean expired failed urls error", var4);
      }
   }

   private static LinkedHashSet recoverMethod_a_eb505c27(LinkedHashSet var0) {
      if (var0 != null && !var0.isEmpty()) {
         recoverMethod_d_9b75();
         LinkedHashSet var1 = new LinkedHashSet();

         for (Object recoveryElement_456 : var0) {
            String var3 = (String)recoveryElement_456;
            if (!recoverField_q.containsKey(var3)) {
               var1.add(var3);
            }
         }

         if (var1.isEmpty()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "all urls are in blacklist, return original urls");
            return var0;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "ReportUtils", "removed " + (var0.size() - var1.size()) + " failed urls from " + var0.size() + " total urls"
            );
            return var1;
         }
      } else {
         return var0;
      }
   }

   private static void recoverMethod_b_6892eae2(Context var0, JSONObject var1, ReportCallBack var2) {
      try {
         if (var1 != null && var1.length() > 0) {
            JSONObject var3 = recoverMethod_a_16ce4761(var0);
            if (var3 == null) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportUtils", "wrap data failed");
               if (var2 != null) {
                  var2.onFinish(-1);
               }
            } else {
               String var4 = recoverMethod_b_13ea1257(var1);
               JSONObject var5 = recoverMethod_a_c699c4fd(new JSONArray().put(var1), var3);
               HashSet var6 = new HashSet();
               var6.add(var4);
               int var7 = recoverMethod_a_c08b9df7(var0, var6, var5, null, var2);
               if (var7 == 0) {
                  RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
               }
            }
         } else {
            RecoverTypeaf2aa269.recoverMethod_b_f0554df3(var0);
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportUtils", "reportWithoutStore exception:" + var8);
      }
   }

   private static JSONObject recoverMethod_a_c699c4fd(JSONArray var0, JSONObject var1) {
      JSONObject var2 = new JSONObject();

      try {
         var2.put("content", var0);
      } catch (JSONException var4) {
      }

      RecoverTypeaf2b07cd.recoverMethod_a_e0cf9bf5(var2, var1);
      return var2;
   }

   public static JSONObject recoverMethod_a_16ce4761(Context var0) {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("platform", "a");
         long var2 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_B_48794abf());
         if (var2 == 0L) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "miss uid when wrap container info");
            return null;
         }

         var1.put("uid", var2);
         String var4 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
         if (RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var4)) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportUtils", "miss app_key when wrap container info");
            return null;
         }

         var1.put("app_key", var4);
         ActionManager.getInstance().wrapSdkVersionInfo(var1);
         var1.put("core_sdk_ver", RecoverType5a681cd.recoverField_b);
         String var5 = RecoverType5a68d10.recoverMethod_h_e501eb0d(var0);
         if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var5)) {
            var1.put("channel", var5);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("ReportUtils", "miss channel when wrap container info,but continue report...");
         }

         String var6 = RecoverType5a68950.recoverMethod_a_e501eb0d(var0);
         if (!TextUtils.isEmpty(var6)) {
            var1.put("app_version", var6);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("ReportUtils", "miss app version when wrap container info,but continue report...");
         }

         String var7 = RecoverTypeaf2b07ce.recoverMethod_c_e501eb0d(var0);
         if (TextUtils.isEmpty(var7)) {
            var7 = RecoverTypeaf2b07ce.recoverMethod_a_e501eb0d(var0);
         }

         if (!TextUtils.isEmpty(var7)) {
            var1.put("network_type", var7);
         }

         JSONArray var8 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_5d5a71dd(var0);
         var1.put("g_sdk_type", var8);
         return var1;
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "wrapContainerInfo exception:" + var9);
         return null;
      }
   }

   private static Pair recoverMethod_a_9d81bb8(String var0, boolean var1, int var2) {
      byte[] var3;
      try {
         var3 = var0.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var10) {
         return null;
      }

      if (var1) {
         try {
            var3 = RecoverTypeaf2b07d7.recoverMethod_a_4962d961(var3);
         } catch (IOException var9) {
            return null;
         }
      }

      int var4 = RecoverTypeaf2b07d4.recoverMethod_a_9b68();
      String var5 = RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(var4);
      String var6;
      switch (var2) {
         case 1:
            var6 = recoverField_a;
            break;
         case 2:
            var6 = recoverField_b;
            break;
         default:
            return null;
      }

      try {
         byte[] var7 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var3, var5, var6, true);
         return new Pair(var7, var4);
      } catch (Exception var8) {
         return null;
      }
   }

   private static int recoverMethod_a_c1ac349(Context var0, byte[] var1, int var2, Set var3) {
      LinkedHashSet var4 = recoverMethod_a_eafbb042(var0, var3);
      if (var4 != null && !var4.isEmpty()) {
         if (recoverMethod_c_abc98aae(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "types=" + var3 + " support tcp report only");
            return -2;
         }

         String var5 = " type=" + var3;

         for (Object recoveryElement_615 : var4) {
            String var7 = (String)recoveryElement_615;
            if (TextUtils.isEmpty(var7)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "can't get url, give up upload");
            } else if (VERSION.SDK_INT >= 28 && !var7.startsWith(RecoverType5a69fd7.Member367e0ad4.recoverField_d)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "won't use http at device since 28");
            } else {
               String var8 = var7;
               if (!var7.endsWith(recoverField_l)) {
                  var7 = var7 + recoverField_l;
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "upload" + var5 + " to url:" + var7);
               RecoverTypeaf2aa26c var9 = RecoverTypeaf2aa267.recoverMethod_a_7e961685(var0, var7, var1, var2, 3, 1, var3);
               switch (var9.recoverMethod_a_9b68()) {
                  case -3:
                     RecoverTypeaf2aa269.recoverMethod_a_2213544c(var0, RecoverType5a68d10.recoverMethod_e_e501eb0d(var0));
                     return -2;
                  case -2:
                  default:
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "upload" + var5 + " failed");
                     recoverMethod_a_aad3b1ff(var8);
                     break;
                  case -1:
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "upload" + var5 + " error:" + var9.recoverMethod_b_73cf11cb());
                     recoverMethod_a_aad3b1ff(var8);
                     break;
                  case 0:
                     return 0;
               }
            }
         }

         return -1;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "can't get url, give up upload");
         return -2;
      }
   }

   private static boolean recoverMethod_a_50d301a1(Context var0, Set var1, int var2, boolean var3) {
      int var4 = var3 ? 30650 : 30680;
      return recoverField_d
         && (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_l_48794abf())
         && var2 < var4
         && var1 != null
         && !var1.contains("crash_log")
         && !var1.contains("operate_event");
   }

   private static boolean recoverMethod_c_abc98aae(Set var0) {
      return var0.contains("sdk_jid") && var0.size() == 1;
   }

   public static int recoverMethod_a_c08b9df7(Context var0, Set var1, JSONObject var2, File var3, ReportCallBack var4) {
      return recoverMethod_a_b58ecec1(var0, var1, var2, var3, var4, "");
   }

   public static int recoverMethod_a_b58ecec1(Context var0, Set var1, JSONObject var2, File var3, ReportCallBack var4, String var5) {
      int var6 = 0;

      try {
         if (var2 == null || var2.length() == 0) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "upload content is empty, do nothing");
            var6 = -1;
            return -1;
         }

         if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "no network, give up upload");
            var6 = -2;
            return -2;
         }

         if (!RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_f0554df7(var0)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "report sdk limit");
            var6 = -2;
            return -2;
         }

         Pair var7 = recoverMethod_a_9d81bb8(var2.toString(), true, 2);
         if (var7 != null && var7.first != null && ((byte[])var7.first).length != 0) {
            byte[] var8 = (byte[])var7.first;
            int var9 = (Integer)var7.second;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "will upload length=" + var8.length);
            boolean var10 = RecoverTypeaf2aadab.recoverMethod_d_f0554df7(var0);
            if (recoverMethod_a_50d301a1(var0, var1, var8.length, var10)) {
               RecoverTypeaf2ab532.Member8f523571 var11 = RecoverTypeaf2ab532.recoverMethod_a_c69656b4()
                  .recoverMethod_a_31c0f640(var0, var2, var8, var9, var3, var1, var4, var5);
               RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_a_21e9879d(var0, var11);
               var6 = 1;
               return 1;
            }

            var6 = recoverMethod_a_c1ac349(var0, var8, var9, var1);
            if (var6 == 0) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "http upload success json=" + RecoverTypeaf2b07cd.recoverMethod_a_13ea1257(var2));
               if (!TextUtils.isEmpty(var5)) {
                  recoverMethod_a_afa340b5(var5, true);
               } else {
                  RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var3);
               }
            }

            return var6;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "package body failed, give up upload");
            var6 = -1;
            return -1;
         }
      } catch (Throwable var16) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "upload failed, error:" + var16);
         var6 = -1;
         return -1;
      } finally {
         if (var6 != 1) {
            if (var6 == 0) {
               recoverMethod_a_afa340b5(var5, true);
            } else {
               recoverMethod_a_afa340b5(var5, false);
            }

            if (TextUtils.isEmpty(var5)) {
               RecoverTypeaf2aa269.recoverMethod_a_4ad1f42c(var3);
            }

            if (var4 != null) {
               var4.onFinish(var6);
            }
         }
      }
   }

   private static String recoverMethod_b_13ea1257(JSONObject var0) {
      return var0 != null ? var0.optString("type") : null;
   }

   private static Set recoverMethod_c_c8b8153e(JSONArray var0) {
      HashSet var1 = new HashSet();
      if (var0 != null) {
         for (int var2 = 0; var2 < var0.length(); var2++) {
            String var3 = recoverMethod_b_13ea1257(var0.optJSONObject(var2));
            if (var3 == null) {
               var3 = "";
            }

            var1.add(var3);
         }
      }

      return var1;
   }

   public static Set recoverMethod_a_b00d7198(JSONObject var0) {
      if (var0 == null) {
         return new HashSet();
      }

      JSONArray var1 = var0.optJSONArray("content");
      return recoverMethod_c_c8b8153e(var1);
   }

   public static String recoverMethod_b_e96ea081(String var0) {
      String var1 = null;

      try {
         var1 = RecoverTypeaf2b07d4.recoverMethod_b_f169196d(var0, RecoverTypeaf2b07c7.recoverMethod_a_73cf11cb(), RecoverTypeaf2b07c7.recoverField_a);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("getBasicAuthorization", "basic authorization encode failed");
      }

      return var1;
   }

   public static String recoverMethod_c_e96ea081(String var0) {
      String var1 = null;

      try {
         var1 = RecoverTypeaf2b07d4.recoverMethod_b_f169196d(var0, RecoverTypeaf2b07c7.recoverMethod_b_73cf11cb(), RecoverTypeaf2b07c7.recoverField_b);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("getBasicAuthorization", "basic authorization encode failed");
      }

      return var1;
   }

   public static String recoverMethod_a_cdb8d613(Context var0, String var1, String var2, boolean var3) {
      if (TextUtils.isEmpty(var1)) {
         return null;
      }

      long var4 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_B_48794abf());
      if (var4 == 0L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", " miss uid,generate report token failed");
         return null;
      }

      String var6 = "";
      if (var3) {
         var6 = recoverField_j;
      } else {
         var6 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_E_48794abf());
         if (TextUtils.isEmpty(var6)) {
            var6 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_F_48794abf());
         }

         var6 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var6);
      }

      String var7 = RecoverTypeaf2b07d5.recoverMethod_k_e96ea081(var4 + var6 + var1);
      if (RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var7)) {
         return null;
      }

      String var8 = var4 + ":" + var7 + ":" + var2;
      String var9 = null;

      try {
         var9 = Base64.encodeToString(var8.getBytes(), 10);
      } catch (Exception var11) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("getBasicAuthorization", "basic authorization encode failed");
      }

      return var9;
   }

   public static String recoverMethod_a_22670df9(Context var0, String var1, String var2) {
      return recoverMethod_a_cdb8d613(var0, var1, var2, false);
   }

   private static ArrayList recoverMethod_a_e3dce89(JSONArray var0, int var1, int var2) {
      ArrayList var3 = new ArrayList();
      if (var0 != null && var0.length() != 0) {
         if (var0.length() == 1) {
            var3.add(var0);
            return var3;
         }

         int var4 = 0;
         int var5 = 0;
         JSONArray var6 = new JSONArray();

         for (int var7 = var0.length() - 1; var7 >= 0; var7--) {
            JSONObject var8 = var0.optJSONObject(var7);

            try {
               int var9 = RecoverTypeaf2b07cd.recoverMethod_b_be7fc15c(var8);
               if (var9 != 0) {
                  if (var5 + var9 > var2) {
                     break;
                  }

                  if (var4 + var9 > var1) {
                     if (var6.length() > 0) {
                        var3.add(var6);
                     }

                     var6 = new JSONArray();
                     var6.put(var8);
                     var4 = var9;
                  } else {
                     var6.put(var8);
                     var4 += var9;
                  }

                  var5 += var9;
               }
            } catch (Throwable var10) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportUtils", "partition exception:" + var10);
            }
         }

         if (var6.length() > 0) {
            var3.add(var6);
         }

         return var3;
      } else {
         return var3;
      }
   }

   static {
      recoverField_o.clear();
      recoverField_o.add(recoverField_e);
      recoverField_o.add(recoverField_f);
   }

   private static class Member8f0bb868 extends RecoverTypeaf2b0401 {
      public Context recoverField_a;
      public String recoverField_b;
      public String recoverField_c;
      public JSONObject recoverField_d;
      public File recoverField_e;
      public long recoverField_f;
      public boolean recoverField_g = false;

      public Member8f0bb868(Context var1, String var2, String var3, JSONObject var4) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_f = SystemClock.elapsedRealtime();
      }

      @Override
      public synchronized void recoverMethod_a_9b75() {
         if (this.recoverField_g) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "task id is : " + this.recoverField_b + " running save file is End");
         } else {
            this.recoverField_g = true;
            this.recoverField_e = RecoverTypeaf2aa269.recoverMethod_a_d8504dda(this.recoverField_a, this.recoverField_c, this.recoverField_d, true);
            if (this.recoverField_e != null) {
               RecoverTypeaf2aa26b.Member8f0bb868 var1 = (RecoverTypeaf2aa26b.Member8f0bb868)RecoverTypeaf2aa26b.recoverField_m.get(this.recoverField_b);
               if (var1 != null) {
                  var1.recoverField_e = this.recoverField_e;
                  RecoverTypeaf2aa26b.recoverField_m.replace(this.recoverField_b, var1);
               }
            }
         }
      }

      public synchronized void recoverMethod_b_9b75() {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "onTaskSuccess task id is : " + this.recoverField_b);
         this.recoverField_g = true;
         if (null != this.recoverField_e) {
            RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(this.recoverField_e);
         }

         this.recoverMethod_d_9b75();
      }

      public synchronized void recoverMethod_c_9b75() {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportUtils", "onTaskFailed task id is : " + this.recoverField_b);
         this.recoverMethod_a_9b75();
         this.recoverMethod_d_9b75();
      }

      private void recoverMethod_d_9b75() {
         if (null != this.recoverField_e) {
            RecoverTypeaf2aa269.recoverMethod_a_4ad1f42c(this.recoverField_e);
         }
      }
   }
}
