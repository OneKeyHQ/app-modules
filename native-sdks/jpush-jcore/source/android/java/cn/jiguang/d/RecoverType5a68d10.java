package cn.jiguang.d;

import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae5;
import cn.jiguang.am.RecoverTypeaf2a9ae7;
import cn.jiguang.am.RecoverTypeaf2a9ae8;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.analytics.page.ActivityLifecycle;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.bk.RecoverTypeaf2b07d0;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.e.RecoverType5a690d3;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JConstants;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a68d10 {
   private static ActivityLifecycle recoverField_a = new ActivityLifecycle();

   public static void recoverMethod_a_331c7aaf(ActivityLifecycleCallbacks var0) {
      recoverField_a.addActivityLifecycleCallbacks(var0);
   }

   public static ActivityLifecycle recoverMethod_a_58872b9c() {
      return recoverField_a;
   }

   public static void recoverMethod_b_331c7aaf(ActivityLifecycleCallbacks var0) {
      recoverField_a.removeActivityLifecycleCallbacks(var0);
   }

   public static synchronized void recoverMethod_a_f0554df3(Context var0) {
      recoverMethod_a_1a552341(var0, true);
   }

   public static synchronized void recoverMethod_a_1a552341(Context var0, boolean var1) {
      try {
         if (JConstants.isInstrumentationHookFailed && VERSION.SDK_INT >= 14) {
            var0 = var0.getApplicationContext();
            String var2 = var0.getPackageName();
            String var3 = var1 ? RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var0) : var2;
            if (!TextUtils.isEmpty(var3) && TextUtils.equals(var2, var3)) {
               try {
                  JConstants.isInstrumentationHookFailed = false;
                  ((Application)var0).registerActivityLifecycleCallbacks(recoverField_a);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                     "JBridgeHelper", "registerActivityLifecycleCallbacks in main process,packageName:" + var2 + ",currentProcessName:" + var3
                  );
               } catch (Throwable var5) {
                  RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JBridgeHelper", "initPageLifecycle throwable=" + var5);
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "need not registerActivityLifecycleCallbacks in other process :" + var3);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "need not registerActivityLifecycleCallbacks");
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JBridgeHelper", "registerActivityLifecycleCallbacks failed:" + var6);
         JConstants.isInstrumentationHookFailed = true;
      }
   }

   public static void recoverMethod_a_e925fa09(String var0, String var1) {
      ActionManager.addAction(var0, var1);
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      return (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_C_48794abf());
   }

   public static String recoverMethod_c_e501eb0d(Context var0) {
      return (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, RecoverType5a690d1.recoverMethod_O_48794abf());
   }

   public static String recoverMethod_d_e501eb0d(Context var0) {
      return (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_P_48794abf());
   }

   public static String recoverMethod_e_e501eb0d(Context var0) {
      return RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
   }

   public static long recoverMethod_f_f0554de7(Context var0) {
      return (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_B_48794abf());
   }

   public static long recoverMethod_g_f0554de7(Context var0) {
      return (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_D_48794abf());
   }

   public static String recoverMethod_h_e501eb0d(Context var0) {
      return RecoverTypeaf2a9723.recoverMethod_c_e501eb0d(var0);
   }

   public static String recoverMethod_i_e501eb0d(Context var0) {
      return RecoverTypeaf2b07c8.recoverMethod_b_e501eb0d(var0);
   }

   public static void recoverMethod_a_127ac70f(Context var0, Object var1) {
      JCoreManager.onEvent(var0, "JCore", 14, null, null, var1);
   }

   public static JSONObject recoverMethod_a_1b76323(Context var0, JSONObject var1, String var2) {
      if (var1 == null) {
         var1 = new JSONObject();
      }

      try {
         if (TextUtils.equals(var2, "app_list")) {
            var1.put("it", RecoverType5a68d11.recoverMethod_b_f0554de7(var0));
         } else {
            var1.put("itime", RecoverType5a68d11.recoverMethod_b_f0554de7(var0));
         }

         var1.put("type", var2);
         String var3 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_P_48794abf());
         if (!TextUtils.isEmpty(var3)) {
            var1.put("account_id", var3);
         }
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JBridgeHelper", "fillBase exception:" + var4);
      }

      return var1;
   }

   public static String recoverMethod_j_e501eb0d(Context var0) {
      String var1 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_E_48794abf());
      if (TextUtils.isEmpty(var1)) {
         var1 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_F_48794abf());
      }

      return var1;
   }

   public static Object recoverMethod_b_810c345d() {
      return ActionManager.getActionMap();
   }

   public static int recoverMethod_k_f0554de6(Context var0) {
      return (Integer)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_G_48794abf());
   }

   public static boolean recoverMethod_l_f0554df7(Context var0) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "canShowLbsPermissionDialog");
      Boolean var1 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ae_48794abf());
      if (var1 != null && !var1) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JBridgeHelper", "lbs disable");
         return false;
      }

      long var2 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ad_48794abf());
      long var4 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ab_48794abf());
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "lbs permission dialog shield, firstInit=" + var4 + ", delay=" + var2);
      if (var4 > 0L) {
         return var2 > 0L ? System.currentTimeMillis() > var4 + var2 : true;
      }

      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ab_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis()));
      return var2 <= 0L;
   }

   public static void recoverMethod_a_4bc5be89(Context var0, JSONObject var1, boolean var2) {
      try {
         JSONObject var3 = var1.optJSONObject("content");
         if (var3 != null) {
            int var4 = var3.optInt("log_enable");
            int var5 = var3.optInt("log_print");
            int var6 = var3.optInt("upload_enable");
            if (var2) {
               int var7 = var3.optInt("server_upload_state", 1);
               RecoverType5a690d3.recoverMethod_b_1a54e370(var0, var7);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JBridgeHelper", "server_upload_state: " + var7 + ", sp state: " + RecoverType5a690d3.recoverMethod_e_f0554de6(var0)
               );
            }

            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aA_48794abf().recoverMethod_a_4e9722a3(var4));
            JConstants.CMD_TO_PRINT_ALL_LOG = var5 == 1;
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aB_48794abf().recoverMethod_a_4e9722a3(var5));
            int var10 = var3.optInt("expire");
            var10 = var10 > 0 ? var10 : 7;
            RecoverType5a690d2.recoverMethod_a_abcb8d40(
               var0, RecoverType5a690d1.recoverMethod_aC_48794abf().recoverMethod_a_4e9722a3(RecoverTypeaf2b07c6.recoverMethod_a_134622(var10))
            );
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "dealCommandInternalDebug: content: " + var3 + ", fromCmd: " + var2);
            if (var4 == 1) {
               RecoverType5a681cd.recoverField_g = true;
               int var8 = var3.optInt("delay_upload");
               var8 = Math.max(var8, 10);
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aD_48794abf().recoverMethod_a_4e9722a3(var8));
               if (var6 == 1) {
                  RecoverTypeaf2a9ae5.recoverMethod_a_f0554df3(var0);
               }

               if (var2 && RecoverType5a690d3.recoverMethod_e_f0554de6(var0) == 1) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "dealCommandInternalDebug: server cmd upload log to server");
                  RecoverTypeaf2a9ae5.recoverMethod_b_f0554df3(var0);
               }
            } else {
               RecoverType5a681cd.recoverField_g = false;
               if (RecoverTypeaf2a9ae7.recoverMethod_c_f0554df7(var0)) {
                  if (RecoverType5a690d3.recoverMethod_d_f0554df7(var0)) {
                     RecoverTypeaf2a9ae5.recoverMethod_b_f0554df3(var0);
                  } else {
                     RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
                        @Override
                        public void recoverMethod_a_9b75() {
                           RecoverTypeaf2a9ae8.recoverMethod_a_9b75();
                        }
                     });
                  }
               }
            }
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JBridgeHelper", "dealCommandInternalDebug failed: " + var9.getMessage());
      }
   }

   public static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      recoverMethod_a_4bc5be89(var0, var1, false);
   }

   public static int recoverMethod_c_9b68() {
      int var0 = 0;

      try {
         Map var1 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_d_604efcea();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JBridgeHelper", "map is " + var1.toString());
         if (var1 != null && var1.containsKey(2)) {
            Bundle var2 = (Bundle)var1.get(2);
            String var3 = var2.getString("code");
            if (TextUtils.isDigitsOnly(var3)) {
               var0 = Integer.parseInt(var3);
            }
         }
      } catch (Throwable var4) {
      }

      return var0;
   }

   public static String recoverMethod_m_e501eb0d(Context var0) {
      Object var1 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_471f464c(var0, "JPUSH", 30000);
      return var1 instanceof String ? (String)var1 : null;
   }

   public static Integer recoverMethod_n_7238765a(Context var0) {
      Object var1 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_471f464c(var0, "JPUSH", 30006);
      return var1 instanceof Integer ? (Integer)var1 : null;
   }

   public static boolean recoverMethod_a_aad3b203(String var0) {
      return RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_aad3b203(var0);
   }

   public static String recoverMethod_b_e96ea081(String var0) {
      return RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var0, RecoverType5a69fd7.Member367e0ad4.recoverField_j);
   }

   public static String recoverMethod_c_e96ea081(String var0) {
      return RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var0, RecoverType5a69fd7.Member367e0ad4.recoverField_j);
   }

   public static boolean recoverMethod_o_f0554df7(Context var0) {
      if (JConstants.isAndroidQ(var0, true, "canGetLbsInBackGround")
         && !RecoverTypeaf2aa628.recoverField_b.get()
         && !RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_BACKGROUND_LOCATION")) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JBridgeHelper", "app is not in foreground and no android.permission.ACCESS_BACKGROUND_LOCATION");
         return false;
      } else {
         return true;
      }
   }
}
