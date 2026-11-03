package cn.jiguang.ad;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd6;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.json.JSONObject;

public class RecoverTypeaf2a791b {
   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JNotificationState", "triggerScene:" + var1);
      if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1800)) {
         if (1 == var1 && !RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_d_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JNotificationState", "ismLoggedIn is false, triggerScene:" + var1);
         } else {
            String var2 = RecoverType5a69fd6.recoverMethod_g_e501eb0d(var0);
            boolean var3 = recoverMethod_a_f0554df7(var0);
            if (!var3) {
               RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JNotificationState", "notification trigger is off, will not show notification");
            }

            boolean var4 = false;
            if (TextUtils.isEmpty(var2)) {
               var4 = true;
            } else {
               boolean var5 = false;
               if (TextUtils.equals("ON", var2)) {
                  var5 = true;
               } else if (TextUtils.equals("OFF", var2)) {
                  var5 = false;
               } else {
                  var4 = true;
               }

               if (!var4) {
                  var4 = var5 != var3;
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JNotificationState", "notification state do not changed");
               }
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "JNotificationState",
               "lastCacheNotificationState:" + var2 + ",currentNotificationSate:" + var3 + ",isNeedReport:" + var4 + ",triggerScene:" + var1
            );
            if (var4) {
               try {
                  JSONObject var7 = new JSONObject();
                  var7.put("notification_state", var3);
                  if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1802)) {
                     var7.put("device_id", RecoverTypeaf2b07c8.recoverMethod_b_e501eb0d(var0));
                  }

                  var7.put("trigger_scene", var1);
                  RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var0, var7, "android_notification_state");
                  RecoverType5a69fd6.recoverMethod_q_5ffc00fd(var0, var3 ? "ON" : "OFF");
               } catch (Throwable var6) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JNotificationState", "report notification state failed, error:" + var6.getMessage());
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JNotificationState", "do not need report notification state");
            }
         }
      }
   }

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      return VERSION.SDK_INT >= 24 ? recoverMethod_b_f0554df7(var0) : recoverMethod_c_f0554df7(var0);
   }

   @TargetApi(24)
   private static boolean recoverMethod_b_f0554df7(Context var0) {
      try {
         NotificationManager var1 = (NotificationManager)var0.getSystemService("notification");
         return var1.areNotificationsEnabled();
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JNotificationState", "invoke areNotificationsEnabled method failed, error:" + var2.getMessage());
         return true;
      }
   }

   private static boolean recoverMethod_c_f0554df7(Context var0) {
      try {
         AppOpsManager var1 = (AppOpsManager)var0.getSystemService("appops");
         ApplicationInfo var2 = var0.getApplicationInfo();
         String var3 = var0.getApplicationContext().getPackageName();
         int var4 = var2.uid;
         Class var5 = Class.forName(AppOpsManager.class.getName());
         Method var6 = var5.getMethod("checkOpNoThrow", int.class, int.class, String.class);
         Field var7 = var5.getDeclaredField("OP_POST_NOTIFICATION");
         int var8 = (Integer)var7.get(Integer.class);
         return (Integer)var6.invoke(var1, var8, var4, var3) == 0;
      } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException var9) {
         return true;
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JNotificationState", "getNotificationStateCommon failed, other error:" + var10.getMessage());
         return true;
      }
   }
}
