package cn.jpush.android.api;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.service.JNotifyActivity;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.lang.reflect.Method;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MultiActionsNotificationBuilder extends DefaultPushNotificationBuilder {
   private static final String TAG = "MultiActionsNotificationBuilder";
   private static final String NOTI_ACT_RES_ID = "notification_action_res_id";
   private static final String NOTI_ACT_TEXT = "notification_action_text";
   private static final String NOTI_ACT_EXTRA_STR = "notification_action_extra_string";
   private static final String INTENT_NOTIFICATION_CLICK_ACTION_PROXY = "cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY";
   protected Context mContext;
   private JSONArray actionJSONArray = new JSONArray();

   public MultiActionsNotificationBuilder(Context var1) {
      this.mContext = var1;
   }

   public void addJPushAction(int var1, String var2, String var3) {
      JSONObject var4 = new JSONObject();

      try {
         var4.put("notification_action_res_id", var1);
         var4.put("notification_action_text", var2);
         var4.put("notification_action_extra_string", var3);
         this.actionJSONArray.put(var4);
         Logger.recoverMethod_i_e925fa09("MultiActionsNotificationBuilder", this.actionJSONArray.toString());
      } catch (JSONException var6) {
         Logger.recoverMethod_ww_e925fa09("MultiActionsNotificationBuilder", "Construct action failed!" + var6);
      }
   }

   @TargetApi(11)
   @Override
   Notification getNotification(Builder var1) {
      if (VERSION.SDK_INT >= 16) {
         for (int var2 = 0; var2 < this.actionJSONArray.length(); var2++) {
            try {
               JSONObject var3 = this.actionJSONArray.getJSONObject(var2);
               ActivityOptions var4 = null;
               if (VERSION.SDK_INT >= 34) {
                  try {
                     var4 = ActivityOptions.makeBasic();
                     Class<ActivityOptions> var5 = ActivityOptions.class;
                     int var6 = var5.getField("MODE_BACKGROUND_ACTIVITY_START_ALLOWED").getInt(null);
                     int var7 = var5.getField("MODE_BACKGROUND_ACTIVITY_START_DENIED").getInt(null);
                     Method var8 = var5.getMethod("setPendingIntentCreatorBackgroundActivityStartMode", int.class);
                     var8.invoke(var4, var6);
                  } catch (Throwable var9) {
                  }
               }

               Intent var11 = new Intent("cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY");
               var11.putExtra("cn.jpush.android.NOTIFIACATION_ACTION_EXTRA", var3.getString("notification_action_extra_string"));
               PendingIntent var12 = null;
               var11.setClass(this.mContext, JNotifyActivity.class);
               var11.setFlags(8388608);
               var11.putExtra("isNotification", true);
               var11.putExtra("multi_notification_msg", this.notificationMessage);
               var12 = PendingIntent.getActivity(this.mContext, var2, var11, 201326592, var4 == null ? null : var4.toBundle());
               Logger.recoverMethod_i_e925fa09(
                  "MultiActionsNotificationBuilder",
                  "Add notification action: res - "
                     + var3.getInt("notification_action_res_id")
                     + ", string - "
                     + var3.getString("notification_action_text")
                     + ", extra - "
                     + var3.getString("notification_action_extra_string")
               );
               var1.addAction(var3.getInt("notification_action_res_id"), var3.getString("notification_action_text"), var12).setAutoCancel(true);
            } catch (JSONException var10) {
               Logger.recoverMethod_ww_e925fa09("MultiActionsNotificationBuilder", "Parse Action from preference preference failed!" + var10);
            }
         }

         if (this.actionJSONArray.length() == 0) {
            RecoverType2e4d193c.recoverMethod_a_a37e39a6(
               this.context, this.notificationMessage.customButtons, var1, this.notificationMessage.msgId, (byte)this.notificationMessage.platform
            );
         }

         return var1.build();
      } else {
         Logger.recoverMethod_i_e925fa09("MultiActionsNotificationBuilder", "Device with SDK_VERSION < 16, show original notification style.");
         return var1.getNotification();
      }
   }

   public static PushNotificationBuilder parseFromPreference(Context var0, String var1) {
      MultiActionsNotificationBuilder var2 = new MultiActionsNotificationBuilder(var0);

      try {
         var2.actionJSONArray = new JSONArray(var1);
      } catch (JSONException var4) {
         Logger.recoverMethod_ww_e925fa09("MultiActionsNotificationBuilder", "Parse builder from preference failed!" + var4);
      }

      return var2;
   }

   @Override
   public String toString() {
      return this.actionJSONArray.toString();
   }
}
