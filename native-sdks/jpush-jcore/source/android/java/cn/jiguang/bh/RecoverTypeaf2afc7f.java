package cn.jiguang.bh;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.common.app.helper.RecoverType82ecf7ac;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2afc7f {
   private Context recoverField_a = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
   private volatile boolean recoverField_b = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(
      this.recoverField_a, RecoverType5a690d1.recoverMethod_A_48794abf()
   );
   private static String recoverField_c = "SubSDKAPiManager";
   private static ConcurrentHashMap recoverField_d = new ConcurrentHashMap();

   private RecoverTypeaf2afc7f() {
   }

   public static RecoverTypeaf2afc7f recoverMethod_a_c69ef907() {
      return RecoverTypeaf2afc7f.Member905ddd7c.recoverField_a;
   }

   public void recoverMethod_b_9b75() {
      try {
         Boolean var1 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_a, RecoverType5a690d1.recoverMethod_N_48794abf());
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "send mHadReport=" + this.recoverField_b);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "send SubApiSwitch=" + var1);
         if (this.recoverField_b || !var1) {
            return;
         }

         if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            this.recoverMethod_c_9b75();
         } else {
            RecoverTypeaf2aa628.recoverMethod_a_a4fc3973(new Runnable() {
               @Override
               public void run() {
                  RecoverTypeaf2afc7f.this.recoverMethod_c_9b75();
               }
            });
         }
      } catch (Throwable var2) {
      }
   }

   private void recoverMethod_b_af8a350b(String var1, int var2, String var3, Bundle var4, Object[] var5) {
      try {
         String var6 = this.recoverMethod_c_50516f5(var1, var2, var3, var4, var5);
         if (TextUtils.isEmpty(var6)) {
            return;
         }

         String var7 = Arrays.toString(var5);
         JSONObject var8 = RecoverTypeaf2b07d5.recoverMethod_a_fcab3ead(var4);
         JSONObject var9 = new JSONObject();
         var9.put("m", var6);
         var9.put("t", System.currentTimeMillis());
         StringBuilder var10 = new StringBuilder();
         var10.append(var1).append(var2).append(var3).append(var8).append(var7);
         int var11 = var10.toString().hashCode();
         if (!recoverField_d.contains(var11)) {
            recoverField_d.put(var11, var9);
         }
      } catch (Throwable var12) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "throwable=" + var12);
      }
   }

   public void recoverMethod_a_af8a350b(final String var1, final int var2, final String var3, final Bundle var4, final Object[] var5) {
      if (18 != var2) {
         if (!"JCore".equals(var1) || 70 == var2 || 71 == var2) {
            try {
               if (RecoverTypeaf2b07c1.recoverMethod_b_f0554df7(this.recoverField_a)) {
                  Boolean var6 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_a, RecoverType5a690d1.recoverMethod_N_48794abf());
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "save mHadReport=" + this.recoverField_b);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "save SubApiSwitch=" + var6);
                  if (this.recoverField_b || !var6) {
                     return;
                  }

                  if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                     this.recoverMethod_b_af8a350b(var1, var2, var3, var4, var5);
                  } else {
                     RecoverTypeaf2aa628.recoverMethod_a_a4fc3973(new Runnable() {
                        @Override
                        public void run() {
                           RecoverTypeaf2afc7f.this.recoverMethod_b_af8a350b(var1, var2, var3, var4, var5);
                        }
                     });
                  }
               }
            } catch (Throwable var7) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "throwable=" + var7);
            }
         }
      }
   }

   private void recoverMethod_c_9b75() {
      try {
         ConcurrentHashMap var1 = new ConcurrentHashMap(recoverField_d);
         if (var1.isEmpty()) {
            return;
         }

         JSONArray var2 = new JSONArray();

         for (Object var4 : var1.values()) {
            var2.put(var4);
         }

         ArrayList var13 = RecoverType82ecf7ac.recoverMethod_a_48eba5e9(var2);
         if (var13 == null || var13.isEmpty()) {
            return;
         }

         int var14 = 0;

         for (int var5 = var13.size(); var14 < var5; var14++) {
            JSONObject var6 = new JSONObject();
            JSONArray var7 = (JSONArray)var13.get(var14);
            JSONObject var8 = new JSONObject();
            JSONObject var9 = new JSONObject();
            var9.put("api", var7);
            var8.put("exception_type", "5");
            var8.put("extra", var7);
            JSONArray var10 = new JSONArray();
            var10.put(System.currentTimeMillis());
            var8.put("exception_time", var10);
            var8.put("result", "0_1");
            JSONArray var11 = new JSONArray();
            var11.put(var8);
            var6.put("slice_index", var14 + 1);
            var6.put("slice_count", var5);
            var6.put("data", var11);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "reportJson=" + var6);
            RecoverTypeaf2aa628.recoverMethod_b_d97c5eb5(this.recoverField_a, var6, "exception_stats");
         }

         this.recoverField_b = true;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_a, RecoverType5a690d1.recoverMethod_A_48794abf().recoverMethod_a_4e9722a3(true));
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "set had report=" + this.recoverField_b);
      } catch (Throwable var12) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "set had throwable=" + var12);
      }
   }

   private String recoverMethod_c_50516f5(String var1, int var2, String var3, Bundle var4, Object... var5) {
      ArrayList var6 = new ArrayList(Arrays.asList(var5));
      HashMap var7 = RecoverTypeaf2b07d5.recoverMethod_b_a9dc66e2(var4);
      String var8 = "";
      if ("JPUSH".equals(var1) && 3 == var2 && "init".equals(var3)) {
         var8 = "init";
      } else if ("JPUSH".equals(var1)
         && 111 == var2
         && var7.containsKey("enable_c_c")
         && var7.containsKey("disable_c_c")
         && var7.containsKey("enable")
         && var7.containsKey("enable_smart_push")) {
         var8 = "setSmartPushEnable";
      } else if ("JPUSH".equals(var1)
         && 111 == var2
         && var7.containsKey("enable_c_c")
         && var7.containsKey("disable_c_c")
         && var7.containsKey("enable")
         && var7.containsKey("enable_link_push")) {
         var8 = "setLinkMergeEnable";
      } else if ("JPUSH".equals(var1)
         && 111 == var2
         && var7.containsKey("enable_c_c")
         && var7.containsKey("disable_c_c")
         && var7.containsKey("enable")
         && var7.containsKey("enable_geo_push")) {
         var8 = "setGeofenceEnable";
      } else if ("JPUSH".equals(var1) && 111 == var2 && var7.containsKey("enable_c_c") && var7.containsKey("disable_c_c")) {
         var8 = "setCollectControl";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "enable_notification_callback".equals(var3) && var7.containsKey("enable")) {
         var8 = "setNotificationCallBackEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "resume".equals(var3)) {
         var8 = "resumePush";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "4".equals(var7.get("proto_type")) && "0".equals(var7.get("action_type"))) {
         var8 = "stopPush";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "check_stop".equals(var3)) {
         var8 = "isPushStopped";
      } else if ("JPUSH".equals(var1) && 9 == var2) {
         var8 = "setChannel";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "pushtime".equals(var3) && var7.containsKey("time")) {
         var8 = "setPushTime";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "silenceTime".equals(var3) && var7.containsKey("time")) {
         var8 = "setSilenceTime";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "get_rid".equals(var3)) {
         var8 = "getRegistrationID";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "change_foreground".equals(var3)) {
         var8 = "JPushActivityLifeCallBack";
      } else if (8 == var2) {
         var8 = "getUdid";
      } else if (73 == var2) {
         var8 = "JCoreInterface.setWakeEnable";
      } else if (96 == var2) {
         var8 = "JCollectionAuth.setAuth";
      } else if (109 == var2) {
         var8 = "JCollectionAuth.enableAutoWakeup";
      } else if (119 == var2) {
         var8 = "JCollectionAuth.enableAppTerminate";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "max_num".equals(var3) && var7.containsKey("num")) {
         var8 = "setLatestNotificationNumber";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "clear_all_notify".equals(var3)) {
         var8 = "clearAllNotifications";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "clear_by_id".equals(var3) && var7.containsKey("id")) {
         var8 = "clearNotificationById";
      } else if ("JPUSH".equals(var1)
         && 3 == var2
         && "set_custom_notify".equals(var3)
         && var7.containsKey("buidler_string")
         && 0 == (Integer)var7.get("buidler_id")) {
         var8 = "setDefaultPushNotificationBuilder";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "set_custom_notify".equals(var3) && var7.containsKey("buidler_string")) {
         var8 = "setPushNotificationBuilder";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "3".equals(var7.get("proto_type")) && "0".equals(var7.get("action_type"))) {
         var8 = "setProperties";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "3".equals(var7.get("proto_type")) && "1".equals(var7.get("action_type"))) {
         var8 = "deleteProperties";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "3".equals(var7.get("proto_type")) && "2".equals(var7.get("action_type"))) {
         var8 = "cleanProperties";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "1".equals(var7.get("proto_type")) && "2".equals(var7.get("action_type"))) {
         var8 = "setTags";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "1".equals(var7.get("proto_type")) && "1".equals(var7.get("action_type"))) {
         var8 = "addTags";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "1".equals(var7.get("proto_type")) && "3".equals(var7.get("action_type"))) {
         var8 = "deleteTags";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "1".equals(var7.get("proto_type")) && "4".equals(var7.get("action_type"))) {
         var8 = "cleanTags";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "1".equals(var7.get("proto_type")) && "5".equals(var7.get("action_type"))) {
         var8 = "getAllTags";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "1".equals(var7.get("proto_type")) && "6".equals(var7.get("action_type"))) {
         var8 = "checkTagBindState";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "2".equals(var7.get("proto_type")) && "2".equals(var7.get("action_type"))) {
         var8 = "setAlias";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "2".equals(var7.get("proto_type")) && "3".equals(var7.get("action_type"))) {
         var8 = "deleteAlias";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "2".equals(var7.get("proto_type")) && "5".equals(var7.get("action_type"))) {
         var8 = "getAlias";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "set_mobile".equals(var3)) {
         var8 = "setMobileNumber";
      } else if ("JPUSH".equals(var1) && 14 == var2) {
         try {
            JSONObject var9 = (JSONObject)var6.get(0);
            if ("msg_status".equals(var9.optString("type")) && 1028 == var9.optInt("result")) {
               var8 = "reportNotificationOpened";
            } else if ("third_msg_status".equals(var9.optString("type")) && 1028 == var9.optInt("result")) {
               var8 = "reportMessageOpened";
            }
         } catch (Throwable var10) {
         }
      } else if ("JPUSH".equals(var1) && 3 == var2 && "get_connection".equals(var3)) {
         var8 = "getConnectionState";
      } else if ("JPUSH".equals(var1) && 56 == var2 && "resume".equals(var3)) {
         var8 = "onResume";
      } else if ("JPUSH".equals(var1) && 56 == var2 && "pause".equals(var3)) {
         var8 = "onPause";
      } else if ("JPUSH".equals(var1) && 56 == var2 && "f_resume".equals(var3)) {
         var8 = "onFragmentResume";
      } else if ("JPUSH".equals(var1) && 56 == var2 && "f_pause".equals(var3)) {
         var8 = "onFragmentPause";
      } else if ("JPUSH".equals(var1) && 56 == var2 && "kill".equals(var3)) {
         var8 = "onKillProcess";
      } else if ("JCore".equals(var1) && 70 == var2) {
         var8 = "initCrashHandler";
      } else if ("JCore".equals(var1) && 71 == var2) {
         var8 = "stopCrashHandler";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "add_local_notify".equals(var3)) {
         var8 = "addLocalNotification";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "rm_local_notify".equals(var3)) {
         var8 = "removeLocalNotification";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "clear_local_notify".equals(var3)) {
         var8 = "clearLocalNotifications";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "max_num".equals(var3) && 5 == (Integer)var7.get("num")) {
         var8 = "setDefaultNotifactionNumber";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "geo_interval".equals(var3)) {
         var8 = "setGeofenceInterval";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "geo_fence_max_num".equals(var3)) {
         var8 = "setMaxGeofenceNumber";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "delete_geo_fence".equals(var3)) {
         var8 = "deleteGeofence";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "set_badge".equals(var3)) {
         var8 = "setBadgeNumber";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "enable_smart_push".equals(var3)) {
         var8 = "setSmartPushEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "enable_link_push".equals(var3)) {
         var8 = "setLinkMergeEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "enable_geo_push".equals(var3)) {
         var8 = "setGeofenceEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "is_enable".equals(var3)) {
         var8 = "isNotificationEnabled";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "enable_lbs".equals(var3)) {
         var8 = "setLbsEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "go_settings".equals(var3)) {
         var8 = "goToAppNotificationSettings";
      } else if (74 == var2 && var7.containsKey("type")) {
         var8 = "reportWakedData";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "in_app_req".equals(var3) && var7.containsKey("type")) {
         var8 = "pullInAppMessageWithType";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "in_app_req".equals(var3) && var7.containsKey("AdPositionKey")) {
         var8 = "pullInAppMessageWithPosition";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "in_app_req".equals(var3) && var7.containsKey("inapp_param")) {
         var8 = "pullInAppMessageWithParams";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "in_app_req".equals(var3)) {
         var8 = "pullInAppMessage";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "trigger_in_app_event".equals(var3)) {
         var8 = "triggerInAppMsgByEvent";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "si".equals(var3)) {
         var8 = "si";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "check_vas_integrate".equals(var3)) {
         var8 = "checkSupportVasMessage";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "set_hb_period_enable".equals(var3)) {
         var8 = "setHbPeriodEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "set_third_token".equals(var3)) {
         var8 = "setThirdToken";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "third_enable".equals(var3)) {
         var8 = "setThirdPushEnable";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "clear_by_id".equals(var3)) {
         var8 = "clearNotificationById";
      } else if ("JPUSH".equals(var1) && 75 == var2) {
         var8 = "normalExecutor";
      } else if ("JPUSH".equals(var1) && 76 == var2) {
         var8 = "futureExecutor";
      } else if ("JPUSH".equals(var1) && 3 == var2 && "tagalis".equals(var3) && "0".equals(var7.get("proto_type")) && "0".equals(var7.get("action_type"))) {
         var8 = "setAliasAndTags_setAlias_setTags";
      }

      return this.recoverMethod_a_e96ea081(var8);
   }

   private String recoverMethod_a_e96ea081(String var1) {
      String var2 = "";
      if ("init".equals(var1)) {
         var2 = "0";
      } else if ("setSmartPushEnable".equals(var1)) {
         var2 = "1";
      } else if ("setLinkMergeEnable".equals(var1)) {
         var2 = "2";
      } else if ("setGeofenceEnable".equals(var1)) {
         var2 = "3";
      } else if ("setCollectControl".equals(var1)) {
         var2 = "4";
      } else if ("setNotificationCallBackEnable".equals(var1)) {
         var2 = "5";
      } else if ("resumePush".equals(var1)) {
         var2 = "6";
      } else if ("stopPush".equals(var1)) {
         var2 = "7";
      } else if ("isPushStopped".equals(var1)) {
         var2 = "8";
      } else if ("setChannel".equals(var1)) {
         var2 = "9";
      } else if ("setPushTime".equals(var1)) {
         var2 = "10";
      } else if ("setSilenceTime".equals(var1)) {
         var2 = "11";
      } else if ("getRegistrationID".equals(var1)) {
         var2 = "12";
      } else if ("JPushActivityLifeCallBack".equals(var1)) {
         var2 = "13";
      } else if ("getUdid".equals(var1)) {
         var2 = "14";
      } else if ("JCoreInterface.setWakeEnable".equals(var1)) {
         var2 = "15";
      } else if ("JCollectionAuth.setAuth".equals(var1)) {
         var2 = "16";
      } else if ("JCollectionAuth.enableAutoWakeup".equals(var1)) {
         var2 = "17";
      } else if ("JCollectionAuth.enableAppTerminate".equals(var1)) {
         var2 = "18";
      } else if ("setLatestNotificationNumber".equals(var1)) {
         var2 = "19";
      } else if ("clearAllNotifications".equals(var1)) {
         var2 = "20";
      } else if ("clearNotificationById".equals(var1)) {
         var2 = "21";
      } else if ("setDefaultPushNotificationBuilder".equals(var1)) {
         var2 = "22";
      } else if ("setPushNotificationBuilder".equals(var1)) {
         var2 = "23";
      } else if ("setProperties".equals(var1)) {
         var2 = "24";
      } else if ("deleteProperties".equals(var1)) {
         var2 = "25";
      } else if ("cleanProperties".equals(var1)) {
         var2 = "26";
      } else if ("setTags".equals(var1)) {
         var2 = "27";
      } else if ("addTags".equals(var1)) {
         var2 = "28";
      } else if ("deleteTags".equals(var1)) {
         var2 = "29";
      } else if ("cleanTags".equals(var1)) {
         var2 = "30";
      } else if ("getAllTags".equals(var1)) {
         var2 = "31";
      } else if ("checkTagBindState".equals(var1)) {
         var2 = "32";
      } else if ("setAlias".equals(var1)) {
         var2 = "33";
      } else if ("deleteAlias".equals(var1)) {
         var2 = "34";
      } else if ("getAlias".equals(var1)) {
         var2 = "35";
      } else if ("setMobileNumber".equals(var1)) {
         var2 = "36";
      } else if ("reportNotificationOpened".equals(var1)) {
         var2 = "37";
      } else if ("reportMessageOpened".equals(var1)) {
         var2 = "38";
      } else if ("getConnectionState".equals(var1)) {
         var2 = "39";
      } else if ("onResume".equals(var1)) {
         var2 = "40";
      } else if ("onPause".equals(var1)) {
         var2 = "41";
      } else if ("onFragmentResume".equals(var1)) {
         var2 = "42";
      } else if ("onFragmentPause".equals(var1)) {
         var2 = "43";
      } else if ("onKillProcess".equals(var1)) {
         var2 = "44";
      } else if ("initCrashHandler".equals(var1)) {
         var2 = "45";
      } else if ("stopCrashHandler".equals(var1)) {
         var2 = "46";
      } else if ("addLocalNotification".equals(var1)) {
         var2 = "47";
      } else if ("removeLocalNotification".equals(var1)) {
         var2 = "48";
      } else if ("clearLocalNotifications".equals(var1)) {
         var2 = "49";
      } else if ("setDefaultNotifactionNumber".equals(var1)) {
         var2 = "50";
      } else if ("setGeofenceInterval".equals(var1)) {
         var2 = "51";
      } else if ("setMaxGeofenceNumber".equals(var1)) {
         var2 = "52";
      } else if ("deleteGeofence".equals(var1)) {
         var2 = "53";
      } else if ("setBadgeNumber".equals(var1)) {
         var2 = "54";
      } else if ("isNotificationEnabled".equals(var1)) {
         var2 = "55";
      } else if ("setLbsEnable".equals(var1)) {
         var2 = "56";
      } else if ("goToAppNotificationSettings".equals(var1)) {
         var2 = "57";
      } else if ("reportWakedData".equals(var1)) {
         var2 = "58";
      } else if ("pullInAppMessageWithType".equals(var1)) {
         var2 = "59";
      } else if ("pullInAppMessageWithPosition".equals(var1)) {
         var2 = "60";
      } else if ("pullInAppMessageWithParams".equals(var1)) {
         var2 = "61";
      } else if ("pullInAppMessage".equals(var1)) {
         var2 = "62";
      } else if ("triggerInAppMsgByEvent".equals(var1)) {
         var2 = "63";
      } else if ("si".equals(var1)) {
         var2 = "64";
      } else if ("checkSupportVasMessage".equals(var1)) {
         var2 = "65";
      } else if ("setHbPeriodEnable".equals(var1)) {
         var2 = "66";
      } else if ("setThirdToken".equals(var1)) {
         var2 = "67";
      } else if ("setThirdPushEnable".equals(var1)) {
         var2 = "68";
      } else if ("setAliasAndTags_setAlias_setTags".equals(var1)) {
         var2 = "69";
      } else if ("normalExecutor".equals(var1)) {
         var2 = "70";
      } else if ("futureExecutor".equals(var1)) {
         var2 = "71";
      }

      return var2;
   }

   private static final class Member905ddd7c {
      private static final RecoverTypeaf2afc7f recoverField_a = new RecoverTypeaf2afc7f();
   }
}
