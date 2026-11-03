package cn.jpush.android.k;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import org.json.JSONObject;

public class RecoverType2e4ce86e {
   public static void recoverMethod_a_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      Logger.recoverMethod_d_e925fa09("JAnalytics", "sendClickNotificationMessage");
      JSONObject var2 = recoverMethod_c_457163a6(var0, var1);
      recoverMethod_a_f310a0e1(var0, "jg_message_click", var2);
   }

   public static void recoverMethod_b_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      Logger.recoverMethod_d_e925fa09("JAnalytics", "sendClearNotificationMessage");
      JSONObject var2 = recoverMethod_c_457163a6(var0, var1);
      recoverMethod_a_f310a0e1(var0, "jg_message_clean", var2);
   }

   public static void recoverMethod_a_f310a0e1(Context var0, String var1, JSONObject var2) {
      Logger.recoverMethod_d_e925fa09("JAnalytics", "report eventId:" + var1 + ", eventProperty:" + var2);
      Bundle var3 = new Bundle();
      var3.putString("event_id", var1);
      if (null != var2) {
         var3.putString("event_property", var2.toString());
      }

      JCoreManager.onEvent(var0, "JPUSH", 89, "report_reserved", var3);
   }

   private static JSONObject recoverMethod_c_457163a6(Context var0, RecoverType2e4cce2a var1) {
      JSONObject var2 = new JSONObject();

      try {
         NotificationMessage var3 = var1.recoverMethod_b_4c6584f8();
         if (TextUtils.isEmpty(var3.appkey)) {
            var3.appkey = JCoreHelper.getAppKey(var0);
         }

         Logger.recoverMethod_w_e925fa09("JAnalytics", "notificationMessage:" + var3);
         var2.put("jg_msg_id", var3.msgId);
         var2.put("jg_msg_title", var3.notificationTitle);
         var2.put("jg_msg_content", var3.notificationContent);
         var2.put("jg_msg_type", var1.recoverField_a);
         var2.put("jg_msg_url", var3.deeplink);
         var2.put("jg_target_event", var3.notificationTargetEvent);
         String var4 = var3.notificationExtras;
         if (null != var4) {
            try {
               JSONObject var5 = new JSONObject(var4);
               var2.put("jg_task_id", var5.optString("_jg_taskid", ""));
               var2.put("jg_plan_id", var5.optString("_jg_planid", ""));
            } catch (Throwable var6) {
            }
         }
      } catch (Throwable var7) {
         Logger.recoverMethod_ee_1765c98c("JAnalytics", "getJsonNotificationMessage:", var7);
      }

      return var2;
   }
}
