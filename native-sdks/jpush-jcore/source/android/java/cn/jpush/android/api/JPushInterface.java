package cn.jpush.android.api;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.api.JCoreInterface;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.internal.JConstants;
import cn.jpush.android.ab.RecoverType9b4c5363;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.data.JPushCollectControl;
import cn.jpush.android.data.JPushConfig;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455830;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.helper.RecoverType8b455832;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.v.RecoverType2e4d11bc;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class JPushInterface {
   public static final String PUSH_MESSAGE_PERMISSION_POSTFIX = ".permission.JPUSH_MESSAGE";
   public static final String ACTION_CONNECTION_CHANGE = "cn.jpush.android.intent.CONNECTION";
   public static final String ACTION_REGISTRATION_ID = "cn.jpush.android.intent.REGISTRATION";
   public static final String ACTION_MESSAGE_RECEIVED = "cn.jpush.android.intent.MESSAGE_RECEIVED";
   public static final String ACTION_NOTIFICATION_RECEIVED = "cn.jpush.android.intent.NOTIFICATION_RECEIVED";
   public static final String ACTION_NOTIFICATION_OPENED = "cn.jpush.android.intent.NOTIFICATION_OPENED";
   public static final String ACTION_NOTIFICATION_CLICK_ACTION = "cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION";
   public static final String ACTION_NOTIFICATION_CLICK_ACTION_PROXY = "cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY";
   public static final String ACTION_NOTIFICATION_RECEIVED_PROXY = "cn.jpush.android.intent.NOTIFICATION_RECEIVED_PROXY";
   public static final String ACTION_IN_APP_MSG_SHOW = "cn.jpush.android.intent.IN_APP_MSG_SHOW";
   public static final String ACTION_IN_APP_MSG_CLICK = "cn.jpush.android.intent.IN_APP_MSG_CLICK";
   public static final String EXTRA_CONNECTION_CHANGE = "cn.jpush.android.CONNECTION_CHANGE";
   public static final String EXTRA_REGISTRATION_ID = "cn.jpush.android.REGISTRATION_ID";
   public static final String EXTRA_APP_KEY = "cn.jpush.android.APPKEY";
   public static final String EXTRA_NOTIFICATION_DEVELOPER_ARG0 = "cn.jpush.android.NOTIFICATION_DEVELOPER_ARG0";
   public static final String EXTRA_NOTIFICATION_TITLE = "cn.jpush.android.NOTIFICATION_CONTENT_TITLE";
   public static final String EXTRA_NOTIFICATION_URL = "cn.jpush.android.NOTIFICATION_URL";
   public static final String EXTRA_PUSH_ID = "cn.jpush.android.PUSH_ID";
   public static final String EXTRA_MSG_ID = "cn.jpush.android.MSG_ID";
   public static final String EXTRA_NOTI_TYPE = "cn.jpush.android.NOTIFICATION_TYPE";
   public static final String EXTRA_ALERT = "cn.jpush.android.ALERT";
   public static final String EXTRA_ALERT_TYPE = "cn.jpush.android.ALERT_TYPE";
   public static final String EXTRA_MESSAGE = "cn.jpush.android.MESSAGE";
   public static final String EXTRA_CONTENT_TYPE = "cn.jpush.android.CONTENT_TYPE";
   public static final String EXTRA_TITLE = "cn.jpush.android.TITLE";
   public static final String EXTRA_BIG_TEXT = "cn.jpush.android.BIG_TEXT";
   public static final String EXTRA_INBOX = "cn.jpush.android.INBOX";
   public static final String EXTRA_BIG_PIC_PATH = "cn.jpush.android.BIG_PIC_PATH";
   public static final String EXTRA_EXTRA = "cn.jpush.android.EXTRA";
   public static final String EXTRA_NOTI_PRIORITY = "cn.jpush.android.NOTI_PRIORITY";
   public static final String EXTRA_NOTI_IMPORTANCE = "cn.jpush.android.NOTI_IMPORTANCE";
   public static final String EXTRA_NOTI_CATEGORY = "cn.jpush.android.NOTI_CATEGORY";
   public static final String EXTRA_NOTIFICATION_ID = "cn.jpush.android.NOTIFICATION_ID";
   public static final String EXTRA_NOTIFICATION_ACTION_EXTRA = "cn.jpush.android.NOTIFIACATION_ACTION_EXTRA";
   public static final String EXTRA_NOTIFICATION_SMALL_ICON = "cn.jpush.android.NOTIFICATION_SMALL_ICON";
   public static final String EXTRA_NOTIFICATION_LARGET_ICON = "cn.jpush.android.NOTIFICATION_LARGE_ICON";
   public static final String EXTRA_ACTIVITY_PARAM = "cn.jpush.android.ACTIVITY_PARAM";
   public static final String EXTRA_TYPE_PLATFORM = "cn.jpush.android.TYPE_PLATFORM";
   public static final String EXTRA_RICHPUSH_FILE_PATH = "cn.jpush.android.FILE_PATH";
   public static final String EXTRA_RICHPUSH_FILE_TYPE = "cn.jpush.android.FILE_TYPE";
   public static final String EXTRA_RICHPUSH_HTML_PATH = "cn.jpush.android.HTML_PATH";
   public static final String EXTRA_RICHPUSH_HTML_RES = "cn.jpush.android.HTML_RES";
   public static final String EXTRA_STATUS = "cn.jpush.android.STATUS";
   public static int DEFAULT_NOTIFICATION_NUM = 5;
   private static final Integer recoverField_a = 0;
   public static final String ACTION_RICHPUSH_CALLBACK = "cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK";

   public static void init(Context var0) {
      Logger.recoverMethod_ki_e925fa09(
         "JPushInterface",
         "action:init - jpush, SdkVersion:" + JPushConstants.SDK_VERSION_NAME + ", build id:" + JPushConstants.BUILD_ID + ", build type:" + "google play"
      );
      checkContext(var0);
      boolean var1 = RecoverType9b4c5ae5.recoverMethod_h_f0554df7(var0);
      if (var1) {
         Logger.recoverMethod_kd_e925fa09("JPushInterface", "check config suceess!!! ");
         JPushConstants.init(var0);
         JCoreHelper.initSdkType(var0);
         JCoreHelper.runActionWithService(var0, "JPUSH", "init", null);
      }
   }

   public static void init(Context var0, JPushConfig var1) {
      Logger.recoverMethod_ki_e925fa09(
         "JPushInterface",
         "action:init - jpush, SdkVersion:" + JPushConstants.SDK_VERSION_NAME + ", build id:" + JPushConstants.BUILD_ID + ", build type:" + "google play"
      );
      checkContext(var0);
      boolean var2 = RecoverType9b4c5ae5.recoverMethod_h_f0554df7(var0);
      if (var2) {
         Logger.recoverMethod_kd_e925fa09("JPushInterface", "check config suceess!!!");
         if (var1 == null) {
            Logger.recoverMethod_kw_e925fa09("JPushInterface", "JPushConfig is null, return");
         } else {
            JPushConstants.init(var0);
            Bundle var3 = new Bundle();
            var3.putSerializable("config", var1);
            if (!TextUtils.isEmpty(var1.getjAppKey())) {
               JCoreHelper.registerSdkType(var0, var1.getjAppKey());
            } else {
               JCoreHelper.initSdkType(var0);
               Logger.recoverMethod_kw_e925fa09("JPushInterface", "init  initSdkType");
            }

            ActionHelper.getInstance().doSingleAction(var0, "third_init", var3);
            JCoreHelper.runActionWithService(var0, "JPUSH", "init", var3);
         }
      }
   }

   public static void setCollectControl(Context var0, JPushCollectControl var1) {
      checkContext(var0);

      try {
         if (var1 != null) {
            Bundle var2 = new Bundle();
            var2.putIntegerArrayList("enable_c_c", var1.getEnableDatas());
            var2.putIntegerArrayList("disable_c_c", var1.getDisableDatas());
            JCoreManager.onEvent(var0, "JPUSH", 111, null, var2);
         }
      } catch (Throwable var3) {
      }
   }

   public static void setNotificationCallBackEnable(Context var0, boolean var1) {
      Logger.recoverMethod_dd_e925fa09("JPushInterface", "setNotificationCallBackEnable");
      checkContext(var0);

      try {
         RecoverTypea2146d45.recoverMethod_h_1a552341(var0, var1);
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "enable_notification_callback", var2);
      } catch (Throwable var3) {
      }
   }

   public static void resumePush(Context var0) {
      Logger.recoverMethod_kd_e925fa09("JPushInterface", "action:resumePush");
      checkContext(var0);
      JCoreHelper.runActionWithService(var0, "JPUSH", "resume", null);
      ActionHelper.getInstance().doSingleAction(var0, "third_resume", null);
      ActionHelper.getInstance().setPushStatusWithCallBack(var0, 4, 1);
   }

   public static void stopPush(Context var0) {
      Logger.recoverMethod_kd_e925fa09("JPushInterface", "action:stopPush");
      checkContext(var0);
      ActionHelper.getInstance().setPushStatusWithCallBack(var0, 4, 0);
   }

   public static boolean isPushStopped(Context var0) {
      checkContext(var0);
      JCoreHelper.runActionWithService(var0, "JPUSH", "check_stop", null);
      return RecoverTypea2146d45.recoverMethod_e_f0554df7(var0);
   }

   public static void setDebugMode(boolean var0) {
      JCoreManager.setDebugMode(var0);
   }

   public static void setChannel(Context var0, String var1) {
      JCoreHelper.setChannel(var0, var1);
   }

   public static void setPushTime(Context var0, Set var1, int var2, int var3) {
      checkContext(var0);
      if (!RecoverType9b4c5ae5.recoverMethod_a_f0554df7(var0)) {
         Logger.recoverMethod_dd_e925fa09("JPushInterface", "检测到当前没有网络。此动作将在有网络时自动继续执行。");
      }

      if (null == var1) {
         Bundle var7 = new Bundle();
         var7.putString("time", "0123456_0^23");
         JCoreHelper.runActionWithService(var0, "JPUSH", "pushtime", var7);
      } else if (var1.size() == 0 || var1.isEmpty()) {
         JCoreHelper.runActionWithService(var0, "JPUSH", "disable_push", null);
      } else if (var2 > var3) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "Invalid time format - startHour should less than endHour");
      } else {
         StringBuilder var4 = new StringBuilder();

         for (Object recoveryElement_196 : var1) {
            Integer var6 = (Integer)recoveryElement_196;
            if (var6 > 6 || var6 < 0) {
               Logger.recoverMethod_ee_e925fa09("JPushInterface", "Invalid day format - " + var6);
               return;
            }

            var4.append(var6);
         }

         var4.append("_");
         var4.append(var2);
         var4.append("^");
         var4.append(var3);
         Bundle var8 = new Bundle();
         var8.putString("time", var4.toString());
         JCoreHelper.runActionWithService(var0, "JPUSH", "pushtime", var8);
      }
   }

   public static void setSilenceTime(Context var0, int var1, int var2, int var3, int var4) {
      checkContext(var0);
      if (var1 < 0 || var2 < 0 || var3 < 0 || var4 < 0 || var2 > 59 || var4 > 59 || var3 > 23 || var1 > 23) {
         Logger.recoverMethod_ee_e925fa09(
            "JPushInterface", "Invalid parameter format, startHour and endHour should between 0 ~ 23, startMins and endMins should between 0 ~ 59. "
         );
      } else if (var1 == 0 && var2 == 0 && var3 == 0 && var4 == 0) {
         Bundle var8 = new Bundle();
         var8.putString("time", "");
         JCoreHelper.runActionWithService(var0, "JPUSH", "silenceTime", var8);
         Logger.recoverMethod_dd_e925fa09("JPushInterface", "Remove the silence time!");
      } else {
         JSONObject var5 = new JSONObject();

         try {
            var5.put("startHour", var1);
            var5.put("startMins", var2);
            var5.put("endHour", var3);
            var5.put("endtMins", var4);
            Bundle var6 = new Bundle();
            var6.putString("time", var5.toString());
            JCoreHelper.runActionWithService(var0, "JPUSH", "silenceTime", var6);
         } catch (JSONException var7) {
         }
      }
   }

   public static String getRegistrationID(Context var0) {
      checkContext(var0);
      JCoreHelper.runActionWithService(var0, "JPUSH", "get_rid", null);
      return JCoreHelper.getRegistrationID(var0);
   }

   private static void checkContext(Context var0) {
      if (null == var0) {
         throw new IllegalArgumentException("NULL context");
      }

      JPushConstants.mApplicationContext = var0.getApplicationContext();
   }

   public static String getUdid(Context var0) {
      checkContext(var0);
      return JCoreHelper.getDeviceId(var0);
   }

   public static void setLatestNotificationNumber(Context var0, int var1) {
      checkContext(var0);
      Logger.recoverMethod_dd_e925fa09("JPushInterface", "action:setLatestNotificationNumber : " + var1);
      if (var1 <= 0) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "maxNum should > 0, Give up action..");
      } else {
         Bundle var2 = new Bundle();
         var2.putInt("num", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "max_num", var2);
      }
   }

   public static void clearAllNotifications(Context var0) {
      checkContext(var0);
      JCoreHelper.runActionWithService(var0, "JPUSH", "clear_all_notify", null);
      ActionHelper.getInstance().doSingleAction(var0, "third_all_notify", null);
   }

   public static void clearNotificationById(Context var0, int var1) {
      checkContext(var0);
      if (var1 <= 0) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "Invalid notificationId, Give up action..");
      } else {
         NotificationManager var2 = (NotificationManager)var0.getSystemService("notification");
         var2.cancel(var1);
         Bundle var3 = new Bundle();
         var3.putInt("id", var1);
         ActionHelper.getInstance().doSingleAction(var0, "clear_by_id", var3);
      }
   }

   public static void setDefaultPushNotificationBuilder(PushNotificationBuilder var0) {
      if (null == var0) {
         throw new IllegalArgumentException("NULL notification");
      }

      Bundle var1 = new Bundle();
      var1.putInt("buidler_id", recoverField_a);
      var1.putString("buidler_string", var0.toString());
      JCoreHelper.runActionWithService(JPushConstants.mApplicationContext, "JPUSH", "set_custom_notify", var1);
   }

   public static void setPushNotificationBuilder(Integer var0, PushNotificationBuilder var1) {
      Logger.recoverMethod_vv_e925fa09("JPushInterface", "action:setPushNotificationBuilder - id:" + var0);
      if (null == var1) {
         throw new IllegalArgumentException("NULL pushNotificationBuilder");
      }

      if (var0 < 1) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "id should be larger than 0");
      } else {
         Bundle var2 = new Bundle();
         var2.putInt("buidler_id", var0);
         var2.putString("buidler_string", var1.toString());
         JCoreHelper.runActionWithService(JPushConstants.mApplicationContext, "JPUSH", "set_custom_notify", var2);
      }
   }

   @Deprecated
   public void setAliasAndTags(Context var1, String var2, Set var3) {
      checkContext(var1);
      setAliasAndTags(var1, var2, var3, null);
   }

   @Deprecated
   public static void setAliasAndTags(Context var0, String var1, Set var2, TagAliasCallback var3) {
      checkContext(var0);
      ActionHelper.getInstance().setAliasAndTagsWithCallBack(var0, var1, var2, var3, 0, 0);
   }

   @Deprecated
   public static void setTags(Context var0, Set var1, TagAliasCallback var2) {
      checkContext(var0);
      setAliasAndTags(var0, null, var1, var2);
   }

   @Deprecated
   public static void setAlias(Context var0, String var1, TagAliasCallback var2) {
      checkContext(var0);
      setAliasAndTags(var0, var1, null, var2);
   }

   public static Set filterValidTags(Set var0) {
      return RecoverType9b4c5363.recoverMethod_a_40b97519(var0);
   }

   public static void setProperties(Context var0, int var1, Map var2) {
      checkContext(var0);
      ActionHelper.getInstance().setPropertyWithCallBack(var0, var1, var2, 3, 0);
   }

   public static void deleteProperties(Context var0, int var1, Map var2) {
      checkContext(var0);
      ActionHelper.getInstance().setPropertyWithCallBack(var0, var1, var2, 3, 1);
   }

   public static void cleanProperties(Context var0, int var1) {
      checkContext(var0);
      ActionHelper.getInstance().setPropertyWithCallBack(var0, var1, new HashMap(), 3, 2);
   }

   public static void setTags(Context var0, int var1, Set var2) {
      checkContext(var0);
      ActionHelper.getInstance().setTagsWithCallBack(var0, var1, var2, 1, 2);
      Logger.recoverMethod_kd_e925fa09("JPushInterface", "current use set tags is:" + var2);
   }

   public static void addTags(Context var0, int var1, Set var2) {
      checkContext(var0);
      ActionHelper.getInstance().setTagsWithCallBack(var0, var1, var2, 1, 1);
   }

   public static void deleteTags(Context var0, int var1, Set var2) {
      checkContext(var0);
      ActionHelper.getInstance().setTagsWithCallBack(var0, var1, var2, 1, 3);
   }

   public static void cleanTags(Context var0, int var1) {
      checkContext(var0);
      ActionHelper.getInstance().setTagsWithCallBack(var0, var1, new HashSet(), 1, 4);
   }

   public static void getAllTags(Context var0, int var1) {
      checkContext(var0);
      ActionHelper.getInstance().setTagsWithCallBack(var0, var1, new HashSet(), 1, 5);
   }

   public static void checkTagBindState(Context var0, int var1, String var2) {
      checkContext(var0);
      HashSet var3 = null;
      if (!TextUtils.isEmpty(var2)) {
         var3 = new HashSet();
         var3.add(var2);
      }

      ActionHelper.getInstance().setTagsWithCallBack(var0, var1, var3, 1, 6);
   }

   public static void setAlias(Context var0, int var1, String var2) {
      checkContext(var0);
      ActionHelper.getInstance().setAliasWithCallBack(var0, var1, var2, 2, 2);
      Logger.recoverMethod_kd_e925fa09("JPushInterface", "current use set alias is:" + var2);
   }

   public static void deleteAlias(Context var0, int var1) {
      checkContext(var0);
      ActionHelper.getInstance().setAliasWithCallBack(var0, var1, null, 2, 3);
   }

   public static void getAlias(Context var0, int var1) {
      checkContext(var0);
      ActionHelper.getInstance().setAliasWithCallBack(var0, var1, null, 2, 5);
   }

   public static void setMobileNumber(Context var0, int var1, String var2) {
      checkContext(var0);
      RecoverType8b455832.recoverMethod_a_e76c89f4().recoverMethod_a_3f52113a(var0, var1, var2);
   }

   public static void reportMessageOpened(Context var0, String var1, byte var2) {
      checkContext(var0);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "The msgId is not valid - " + var1);
      } else {
         RecoverType8b455831.recoverMethod_a_2ef42797(var1, "", var2, 1028L, var0);
      }
   }

   public static void reportNotificationOpened(Context var0, String var1) {
      checkContext(var0);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "The msgId is not valid - " + var1);
      } else {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var1, 1028L, var0);
      }
   }

   public static void reportNotificationOpened(Context var0, String var1, byte var2) {
   }

   public static void reportNotificationOpened(Context var0, String var1, byte var2, String var3) {
   }

   @Deprecated
   public static boolean getConnectionState(Context var0) {
      checkContext(var0);
      JCoreHelper.runActionWithService(var0, "JPUSH", "get_connection", null);
      return JCoreManager.getConnectionState(var0);
   }

   public static void onResume(Context var0) {
      JCoreHelper.saEvent(var0, "resume", null);
   }

   public static void onPause(Context var0) {
      JCoreHelper.saEvent(var0, "pause", null);
   }

   public static void onFragmentResume(Context var0, String var1) {
      JCoreHelper.saEvent(var0, "f_resume", var1);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "fragmentName can not null");
      } else {
         Bundle var2 = new Bundle();
         var2.putString("activity_name", var1);
         var2.putBoolean("state", true);
         ActionHelper.getInstance().doSingleAction(var0, "sync_fragment_life", var2);
      }
   }

   public static void onFragmentPause(Context var0, String var1) {
      JCoreHelper.saEvent(var0, "f_pause", var1);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "fragmentName can not null");
      } else {
         Bundle var2 = new Bundle();
         var2.putString("activity_name", var1);
         var2.putBoolean("state", false);
         ActionHelper.getInstance().doSingleAction(var0, "sync_fragment_life", var2);
      }
   }

   public static void onKillProcess(Context var0) {
      JCoreHelper.saEvent(var0, "kill", null);
   }

   public static void setStatisticsSessionTimeout(long var0) {
   }

   public static void setStatisticsEnable(boolean var0) {
   }

   public static void initCrashHandler(Context var0) {
      checkContext(var0);
      JCoreManager.initCrashHandler(var0);
   }

   public static void stopCrashHandler(Context var0) {
      checkContext(var0);
      JCoreManager.stopCrashHandler(var0);
   }

   public static void addLocalNotification(Context var0, JPushLocalNotification var1) {
      checkContext(var0);
      RecoverType2e4d11bc.recoverMethod_a_9131a9a(var0, var1);
   }

   public static void removeLocalNotification(Context var0, long var1) {
      checkContext(var0);
      RecoverType2e4d11bc.recoverMethod_a_1a54e731(var0, var1);
   }

   public static void clearLocalNotifications(Context var0) {
      checkContext(var0);
      RecoverType2e4d11bc.recoverMethod_a_f0554df3(var0);
   }

   private static boolean checkLengthOfTags(String var0) {
      int var1 = 0;
      if (!TextUtils.isEmpty(var0)) {
         var1 += var0.getBytes().length;
      }

      Logger.recoverMethod_v_e925fa09("JPushInterface", "tags length:" + var1);
      return var1 <= 7000;
   }

   public static String getStringTags(Set var0) {
      return RecoverType9b4c5363.recoverMethod_b_b555d336(var0);
   }

   private static void setDefaultNotifactionNumber(Context var0) {
      setLatestNotificationNumber(var0, DEFAULT_NOTIFICATION_NUM);
   }

   public static void requestPermission(Context var0) {
      if (var0 == null) {
         Logger.recoverMethod_ww_e925fa09("JPushInterface", "[requestPermission] unexcepted - context was null");
      } else {
         JCoreManager.requestPermission(var0);
      }
   }

   public static void requestRequiredPermission(Activity var0) {
      if (var0 == null) {
         Logger.recoverMethod_ww_e925fa09("JPushInterface", "[requestRequiredPermission] unexcepted - context was null");
      } else {
         RecoverType9b4c5ae5.recoverMethod_l_f0554df3(var0);
      }
   }

   public static void setDaemonAction(String var0) {
   }

   public static void setPowerSaveMode(Context var0, boolean var1) {
   }

   public static void setGeofenceInterval(Context var0, long var1) {
      checkContext(var0);
      if (var1 >= 180000L && var1 <= 86400000L) {
         Bundle var3 = new Bundle();
         var3.putLong("interval", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "geo_interval", var3);
      } else {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "Invalid interval, it should be a ms number between 3 mins and 1 day!");
      }
   }

   public static void setMaxGeofenceNumber(Context var0, int var1) {
      checkContext(var0);
      if (var1 >= 1 && var1 <= 100) {
         Bundle var2 = new Bundle();
         var2.putInt("num", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "geo_fence_max_num", var2);
      } else {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "Invalid maxNumber,it should be a number between 1 and 100!");
      }
   }

   public static void deleteGeofence(Context var0, String var1) {
      checkContext(var0);
      Bundle var2 = new Bundle();
      var2.putString("id", var1);
      JCoreHelper.runActionWithService(var0, "JPUSH", "delete_geo_fence", var2);
   }

   private static void taq() {
      JPushConstants.testAndroidQ();
   }

   public static void setBadgeNumber(Context var0, int var1) {
      var0 = JPushConstants.getAppContext(var0);
      if (var1 < 0) {
         var1 = 0;
      }

      Bundle var2 = new Bundle();
      var2.putInt("num", var1);
      JCoreHelper.runActionWithService(var0, "JPUSH", "set_badge", var2);
   }

   public static void setSmartPushEnable(Context var0, boolean var1) {
      try {
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         var2.putBoolean("enable_smart_push", var1);
         RecoverTypea2146d45.recoverMethod_i_1a552341(var0, var1);

         try {
            if (JConstants.SDK_VERSION_INT >= 490) {
               Logger.recoverMethod_d_e925fa09("JPushInterface", "setSmartPushEnable  for 129 bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT);
               ActionHelper.getInstance().doSingleAction(var0, "enable_smart_push", var2);
            } else {
               Logger.recoverMethod_d_e925fa09(
                  "JPushInterface", "setSmartPushEnable  runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
               );
               JCoreHelper.runActionWithService(var0, "JPUSH", "enable_smart_push", var2);
            }
         } catch (Throwable var4) {
            Logger.recoverMethod_d_e925fa09(
               "JPushInterface", "setSmartPushEnable  err runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
            );
            JCoreHelper.runActionWithService(var0, "JPUSH", "enable_smart_push", var2);
         }
      } catch (Throwable var5) {
      }
   }

   public static void setLinkMergeEnable(Context var0, boolean var1) {
      try {
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         var2.putBoolean("enable_link_push", var1);
         RecoverTypea2146d45.recoverMethod_f_1a552341(var0, var1);
         JCoreInterface.setWakeEnable(var0, var1);

         try {
            if (JConstants.SDK_VERSION_INT >= 490) {
               Logger.recoverMethod_d_e925fa09("JPushInterface", "setLinkMergeEnable  for 129 bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT);
               ActionHelper.getInstance().doSingleAction(var0, "enable_link_push", var2);
            } else {
               Logger.recoverMethod_d_e925fa09(
                  "JPushInterface", "setLinkMergeEnable  runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
               );
               JCoreHelper.runActionWithService(var0, "JPUSH", "enable_link_push", var2);
            }
         } catch (Throwable var4) {
            Logger.recoverMethod_d_e925fa09(
               "JPushInterface", "setLinkMergeEnable  err runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
            );
            JCoreHelper.runActionWithService(var0, "JPUSH", "enable_link_push", var2);
         }
      } catch (Throwable var5) {
      }
   }

   public static void setGeofenceEnable(Context var0, boolean var1) {
      try {
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         var2.putBoolean("enable_geo_push", var1);
         RecoverTypea2146d45.recoverMethod_e_1a552341(var0, var1);

         try {
            if (JConstants.SDK_VERSION_INT >= 490) {
               Logger.recoverMethod_d_e925fa09("JPushInterface", "setGeofenceEnable  for 129 bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT);
               ActionHelper.getInstance().doSingleAction(var0, "enable_geo_push", var2);
            } else {
               Logger.recoverMethod_d_e925fa09(
                  "JPushInterface", "setGeofenceEnable  runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
               );
               JCoreHelper.runActionWithService(var0, "JPUSH", "enable_geo_push", var2);
            }
         } catch (Throwable var4) {
            Logger.recoverMethod_d_e925fa09(
               "JPushInterface", "setGeofenceEnable  err runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
            );
            JCoreHelper.runActionWithService(var0, "JPUSH", "enable_geo_push", var2);
         }
      } catch (Throwable var5) {
      }
   }

   public static void setDataInsightsEnable(Context var0, boolean var1) {
      try {
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         var2.putBoolean("enable_data_insights", var1);
         RecoverTypea2146d45.recoverMethod_g_1a552341(var0, var1);

         try {
            if (JConstants.SDK_VERSION_INT >= 490) {
               Logger.recoverMethod_d_e925fa09("JPushInterface", "setDataInsightsEnable   bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT);
               ActionHelper.getInstance().doSingleAction(var0, "enable_data_insights", var2);
            } else {
               Logger.recoverMethod_d_e925fa09(
                  "JPushInterface", "setDataInsightsEnable  runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
               );
               JCoreHelper.runActionWithService(var0, "JPUSH", "enable_data_insights", var2);
            }
         } catch (Throwable var4) {
            Logger.recoverMethod_d_e925fa09(
               "JPushInterface", "setDataInsightsEnable  err runActionWithService bundle=" + var2 + " jcore version=" + JConstants.SDK_VERSION_INT
            );
            JCoreHelper.runActionWithService(var0, "JPUSH", "enable_data_insights", var2);
         }
      } catch (Throwable var5) {
      }
   }

   public static void attachBaseContext(Context var0) {
      try {
         long var1 = System.currentTimeMillis();
         Class.forName("cn.jpush.android.dy.JPushThirdDyManager").getDeclaredMethod("attachBaseContext", Context.class).invoke(null, var0);
         Logger.recoverMethod_d_e925fa09("JPushInterface", "use time:" + (System.currentTimeMillis() - var1));
      } catch (Throwable var3) {
      }
   }

   public static int isNotificationEnabled(Context var0) {
      JCoreHelper.runActionWithService(var0, "JPUSH", "is_enable", null);
      return RecoverType2e4d193c.recoverMethod_d_f0554de6(var0);
   }

   public static void goToAppNotificationSettings(Context var0) {
      JCoreHelper.runActionWithService(var0, "JPUSH", "go_settings", null);
      boolean var1 = RecoverType2e4d193c.recoverMethod_e_f0554df7(var0);
      if (!var1) {
         RecoverType2e4d193c.recoverMethod_f_f0554df7(var0);
      }
   }

   public static void setThirdPushEnable(Context var0, boolean var1) {
      try {
         JPushConstants.THIRD_ENABLE = var1;
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         ActionHelper.getInstance().doSingleAction(var0, "third_enable", var2);
      } catch (Throwable var3) {
      }
   }

   public static void reportWakedData(Context var0, Bundle var1, int var2) {
      checkContext(var0);

      try {
         JCoreManager.reportWakedData(var0, var1, var2);
      } catch (Throwable var4) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "not found [reportWakedData] in jcore");
      }
   }

   @Deprecated
   public static void pullInAppMessage(Context var0, int var1) {
      checkContext(var0);

      try {
         Bundle var2 = new Bundle();
         var2.putInt("sequence", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "in_app_req", var2);
      } catch (Throwable var3) {
      }
   }

   @Deprecated
   public static void pullInAppMessage(Context var0, int var1, int var2) {
      checkContext(var0);

      try {
         Bundle var3 = new Bundle();
         var3.putInt("sequence", var1);
         var3.putInt("type", var2);
         JCoreHelper.runActionWithService(var0, "JPUSH", "in_app_req", var3);
      } catch (Throwable var4) {
      }
   }

   public static void pullInAppMessage(Context var0, int var1, String var2) {
      checkContext(var0);

      try {
         Bundle var3 = new Bundle();
         var3.putInt("sequence", var1);
         var3.putString("AdPositionKey", var2);
         JCoreHelper.runActionWithService(var0, "JPUSH", "in_app_req", var3);
      } catch (Throwable var4) {
      }
   }

   public static void pullInAppMessageWithParams(Context var0, InAppSlotParams var1) {
      checkContext(var0);

      try {
         Bundle var2 = new Bundle();
         if (null != var1) {
            var2.putString("inapp_param", var1.toJSONString());
         }

         JCoreHelper.runActionWithService(var0, "JPUSH", "in_app_req", var2);
      } catch (Throwable var3) {
      }
   }

   public static void triggerInAppMsgByEvent(Context var0, int var1, String var2) {
      checkContext(var0);

      try {
         Bundle var3 = new Bundle();
         var3.putInt("sequence", var1);
         var3.putString("event_name", var2);
         JCoreHelper.runActionWithService(var0, "JPUSH", "trigger_in_app_event", var3);
      } catch (Throwable var4) {
      }
   }

   public static void recoverMethod_si_69c77401(Context var0, Bundle var1) {
      checkContext(var0);
      JCoreHelper.runActionWithService(var0, "JPUSH", "si", var1);
   }

   public static void checkSupportVasMessage(Context var0, int var1) {
      checkContext(var0);

      try {
         Bundle var2 = new Bundle();
         var2.putInt("type", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "check_vas_integrate", var2);
      } catch (Throwable var3) {
      }
   }

   public static void setHbPeriodEnable(Context var0, boolean var1) {
      checkContext(var0);

      try {
         Bundle var2 = new Bundle();
         var2.putBoolean("enable", var1);
         JCoreHelper.runActionWithService(var0, "JPUSH", "set_hb_period_enable", var2);
      } catch (Throwable var3) {
      }
   }

   public static void setThirdToken(Context var0, String var1) {
      if (!JPushConstants.THIRD_ENABLE) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "thirdEnable is false,won't set third token");
      } else {
         checkContext(var0);

         try {
            Bundle var2 = new Bundle();
            var2.putString("third_token", var1);
            ActionHelper.getInstance().doSingleAction(var0, "set_third_token", var2);
         } catch (Throwable var3) {
         }
      }
   }

   public static void registerByKey(Context var0, String var1) {
      checkContext(var0);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("JPushInterface", "devKey is not valid - " + var1);
      } else {
         Bundle var2 = new Bundle();
         var2.putString("dev_key", var1);
         ActionHelper.getInstance().doSingleAction(var0, "dy_register", var2);
      }
   }

   public static void setHeartbeatInterval(Context var0, int var1) {
      Bundle var2 = new Bundle();
      var2.putInt("heartbeat_interval", var1);
      JCoreManager.onEvent(var0, null, 55, null, var2);
   }

   public static void triggerNotificationStateCheck(Context var0) {
      JCoreManager.onEvent(var0, "JPUSH", 29, null, null, 4);
   }

   static {
      JCoreManager.addDispatchAction("JPUSH", RecoverType8b455830.class.getName());
      JPushConstants.addActivityLifeCallback();
   }

   public static class Member6357d7ea {
      public static int SUCCESS = 0;
      public static int NULL_TAGANDALIAS = 6001;
      public static int TIMEOUT = 6002;
      public static int INVALID_ALIAS = 6003;
      public static int TOO_LONG_ALIAS = 6004;
      public static int INVALID_TAGS = 6005;
      public static int TOO_LONG_TAGS = 6006;
      public static int TOO_MANY_TAGS = 6007;
      public static int TOO_LONG_TAGALIAS = 6008;
      public static int UNKNOWN_ERROR = 6009;
      public static int INVALID_JSON = 6010;
      public static int INVOKE_TOO_SOON = 6011;
      public static int PUSH_STOPED = 6012;
      public static int INCORRECT_TIME = 6013;
      public static int ERROR_CODE_TOO_BUSY = 6014;
      public static int ERROR_CODE_BLACKLIST = 6015;
      public static int ERROR_CODE_INVALIDUSER = 6016;
      public static int ERROR_CODE_INVALIDREQ = 6017;
      public static int ERROR_CODE_TOO_MANY_TAGS = 6018;
      public static int ERROR_CODE_GET_FAILED = 6019;
      public static int ERROR_CODE_SERVER_UNAVAILABLE = 6020;
      public static int ERROR_CODE_TAG_OPERATORING = 6021;
      public static int ERROR_CODE_ALIAS_OPERATORING = 6022;
      public static int ERROR_CODE_INVALID_MOBILENUMBER = 6023;
      public static int ERROR_CODE_INTERNEL_SERVER_ERROR = 6024;
      public static int ERROR_CODE_TOO_LONG_MOBILENUMBER = 6025;
      public static int ERROR_CODE_ALIAS_LIMIT = 6027;
      public static int ERROR_CODE_PROPERTIES_OPERATORING = 6038;
      public static int HAS_IN_APP_DATA = 6028;
      public static int ERROR_CODE_NO_IN_APP_DATA = 6029;
      public static int ERROR_CODE_OVER_FREQUENCY_SRV = 6030;
      public static int ERROR_CODE_OVER_FREQUENCY = 6031;
      public static int ERROR_CODE_STYLE_NOT_SUPPORT = 6032;
      public static int ERROR_CODE_SERVER_INTERNAL_ERROR = 6033;
      public static int ERROR_CODE_MODE_NOT_MATCH = 6034;
      public static int ERROR_CODE_NOT_PULL_IN_BLOCK_LIST = 6035;
      public static int ERROR_CODE_APPKEY_NOT_AUTH_TO_SET_PROPERTY = 6036;
      public static int ERROR_CODE_PROPERTY_ERROR = 6037;
   }

   public static class Memberd7fe2f20 {
      public static int STYLE_BANNER = 1;
      public static int STYLE_MODAL = 2;
      public static int STYLE_FLOAT = 4;
   }
}
