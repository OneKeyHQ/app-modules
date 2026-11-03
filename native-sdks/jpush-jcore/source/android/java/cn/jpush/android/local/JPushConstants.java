package cn.jpush.android.local;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.service.JCommonService;
import cn.jpush.android.service.JPushMessageReceiver;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.lang.reflect.Method;
import java.util.List;

public class JPushConstants {
   private static final String TAG = "JPushConstants";
   public static int SDK_VERSION_CODE = 590;
   public static String SDK_VERSION_NAME = "5.9.0";
   public static int BUILD_ID = 94;
   public static String SDK_NAME = "";
   public static boolean SUPPORT_DY = true;
   public static final String SDK_TYPE = "JPUSH";
   public static final String ENCODING_UTF_8 = "UTF-8";
   public static final String HTTP_PRE = "http://";
   public static final String HTTPS_PRE = "https://";
   public static boolean THIRD_ENABLE = true;
   public static boolean IS_HB_PERIOD_ENABLE = true;
   private static boolean tcpConnected;
   public static final boolean recoverField_IG = true;
   public static Context mApplicationContext;
   private static Bundle configBundle;
   public static int isForeGround = -1;
   public static final int FORE_GROUND_UNKNOWN = -1;
   public static final int FORE_GROUND = 0;
   public static final int FORE_GROUND_NO = 1;
   private static boolean isInitLife;
   private static boolean isTestAndroidQ = false;
   private static final String USER_MESSAGE_RECEIVER_ACTION = "cn.jpush.android.intent.RECEIVER_MESSAGE";
   private static String messageReceiverClass;
   private static String messageReceiverProcess;
   private static Boolean isSameProcessWithJCommonReceiver;
   private static JPushMessageReceiver jPushMessageReceiver;

   public static Context getAppContext(Context var0) {
      if (mApplicationContext == null && var0 != null) {
         mApplicationContext = var0.getApplicationContext();
      }

      return mApplicationContext;
   }

   public static void init(Context var0) {
      getAppContext(var0);
      initPageLifecycle(var0);
   }

   public static boolean canGetLbsInBackGround(Context var0) {
      if (isAndroidQ(var0, true, "canGetLbsInBackGround")) {
         if (isForeGround == 1 && !RecoverType9b4c5ae5.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_BACKGROUND_LOCATION")) {
            Logger.recoverMethod_ww_e925fa09("JPushConstants", "app is not in foreground and no android.permission.ACCESS_BACKGROUND_LOCATION");
            return false;
         }

         if (isForeGround == -1 && !RecoverType9b4c5ae5.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_BACKGROUND_LOCATION")) {
            Logger.recoverMethod_ww_e925fa09("JPushConstants", "app is unkonwn foreground stat and no android.permission.ACCESS_BACKGROUND_LOCATION");
            return false;
         }
      }

      return true;
   }

   public static void changeForegroudStat(Context var0, boolean var1) {
      Logger.recoverMethod_d_e925fa09("JPushConstants", "changeForegroudStat:" + var1);
      isForeGround = var1 ? 0 : 1;
      Bundle var2 = new Bundle();
      var2.putBoolean("foreground", var1);
      JCoreHelper.runActionWithService(var0, "JPUSH", "change_foreground", var2);
   }

   public static synchronized void addActivityLifeCallback() {
      if (isInitLife) {
         Logger.recoverMethod_d_e925fa09("JPushConstants", "ActivityLifeCallback already initialized");
      } else {
         try {
            isInitLife = true;
            JCoreManager.addActivityLifeCallback(ActivityLifeCallBack.getInstance());
            Logger.recoverMethod_d_e925fa09("JPushConstants", "addActivityLifeCallback init to JCore ");
         } catch (Throwable var1) {
            isInitLife = false;
         }
      }
   }

   private static synchronized void initPageLifecycle(Context var0) {
      if (null == var0) {
         Logger.recoverMethod_w_e925fa09("JPushConstants", "[initPageLifecycle] context is null");
      } else {
         Logger.recoverMethod_d_e925fa09("JPushConstants", "initPageLifecycle init, isInitLife: " + isInitLife);
         if (!isInitLife) {
            var0 = var0.getApplicationContext();

            try {
               isInitLife = true;
               defaultRegister(var0, ActivityLifeCallBack.getInstance());
               Logger.recoverMethod_d_e925fa09("JPushConstants", "initPageLifecycle init to JPush ");
            } catch (Throwable var2) {
               Logger.recoverMethod_ww_e925fa09("JPushConstants", "registerActivityLifecycleCallbacks failed:" + var2.getMessage());
            }
         }
      }
   }

   private static void defaultRegister(Context var0, ActivityLifeCallBack var1) {
      if (VERSION.SDK_INT >= 14 && var0 instanceof Application) {
         String var2 = RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var0);
         String var3 = var0.getPackageName();
         if (var2 != null && var3 != null && var0.getPackageName().equals(var2)) {
            ((Application)var0).registerActivityLifecycleCallbacks(var1);
            if (ActivityLifeCallBack.activityTaskCount == 0) {
               Activity var5 = RecoverType9b4c5ae5.recoverMethod_k_2b1cd295(var0);
               if (var5 != null) {
                  var1.onActivityCreated(var5, null);
                  var1.onActivityStarted(var5);
                  var1.onActivityResumed(var5);
               }
            }

            Logger.recoverMethod_d_e925fa09("JPushConstants", "initPageLifecycle in main process, packageName:" + var3 + ", currentProcessName:" + var2);
         } else {
            Logger.recoverMethod_d_e925fa09("JPushConstants", "need not initPageLifecycle in other process :" + var2);
            RecoverType2e4cce2a var4 = new RecoverType2e4cce2a();
            var4.recoverField_r = 1;
            RecoverType2e4d193c.recoverMethod_a_74ff4fff(var0, "sync_reg_lifecycle", var4, null);
         }
      }
   }

   public static boolean isTcpConnected() {
      return tcpConnected;
   }

   public static void setTcpConnected(boolean var0) {
      tcpConnected = var0;
   }

   public static void testAndroidQ() {
   }

   public static boolean isAndroidQ(Context var0, boolean var1, String var2) {
      boolean var3 = false;
      if (!var1) {
         var3 = isTestAndroidQ ? VERSION.SDK_INT >= 28 : VERSION.SDK_INT > 28;
      } else {
         var3 = (isTestAndroidQ ? VERSION.SDK_INT >= 28 : VERSION.SDK_INT > 28) && var0.getApplicationInfo().targetSdkVersion > 28;
      }

      if (var3) {
         Logger.recoverMethod_d_e925fa09("JPushConstants", "is Android Q, msg: " + var2);
      }

      return var3;
   }

   public static Bundle getBundle() {
      return configBundle;
   }

   public static void setBundle(Bundle var0) {
      configBundle = var0;
   }

   public static JPushMessageReceiver getPushMessageReceiver(Context var0) {
      getMessageReceiverClass(getAppContext(var0));
      return jPushMessageReceiver;
   }

   public static boolean isSameProcessWithJCommonService(Context var0) {
      try {
         if (isSameProcessWithJCommonReceiver != null) {
            return isSameProcessWithJCommonReceiver;
         }

         if (messageReceiverProcess == null) {
            getMessageReceiverClass(var0);
         }

         ComponentInfo var1 = RecoverType9b4c5ae5.recoverMethod_a_e3a6cbf6(var0, var0.getPackageName(), JCommonService.class);
         if (var1 != null) {
            if (var1.processName.equals(messageReceiverProcess)) {
               Logger.recoverMethod_dd_e925fa09("JPushConstants", "same process with JCommonService");
               isSameProcessWithJCommonReceiver = true;
            } else {
               Logger.recoverMethod_dd_e925fa09("JPushConstants", "is not same process with JCommonService");
               isSameProcessWithJCommonReceiver = false;
            }

            return isSameProcessWithJCommonReceiver;
         }
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("JPushConstants", "call isSameProcessWithJCommonService failed:" + var2.getMessage());
      }

      return false;
   }

   public static String getMessageReceiverClass(Context var0) {
      if (messageReceiverClass != null) {
         return messageReceiverClass;
      }

      try {
         ComponentInfo var1 = RecoverType9b4c5ae5.recoverMethod_a_e3a6cbf6(var0, var0.getPackageName(), JPushMessageReceiver.class);
         if (var1 != null) {
            messageReceiverClass = var1.name;
            messageReceiverProcess = var1.processName;
            Logger.recoverMethod_ii_e925fa09(
               "JPushConstants", "found messageServiceClass :" + messageReceiverClass + " at " + messageReceiverProcess + " by getComponentInfo"
            );

            try {
               if (messageReceiverProcess != null && messageReceiverProcess.equals(RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var0))) {
                  jPushMessageReceiver = (JPushMessageReceiver)Class.forName(messageReceiverClass).newInstance();
               }
            } catch (Throwable var11) {
            }
         }
      } catch (Throwable var12) {
      }

      try {
         if (TextUtils.isEmpty(messageReceiverClass)) {
            Intent var14 = new Intent();
            var14.setAction("cn.jpush.android.intent.RECEIVER_MESSAGE");
            var14.setPackage(var0.getPackageName());
            PackageManager var2 = var0.getPackageManager();
            Method var3 = var2.getClass().getMethod(new String(Base64.decode("cXVlcnlCcm9hZGNhc3RSZWNlaXZlcnM=", 2)), Intent.class, int.class);

            for (Object recoveryElement_249 : (List)var3.invoke(var2, var14, 0)) {
               ResolveInfo var6 = (ResolveInfo)recoveryElement_249;
               if (var6.activityInfo != null) {
                  String var7 = var6.activityInfo.name;
                  if (!TextUtils.isEmpty(var7)) {
                     Class var8 = Class.forName(var7);
                     if (JPushMessageReceiver.class.isAssignableFrom(var8)) {
                        messageReceiverClass = var7;
                        messageReceiverProcess = var6.activityInfo.processName;
                        Logger.recoverMethod_ii_e925fa09(
                           "JPushConstants",
                           "found messageReceiverClass :" + messageReceiverClass + " at " + messageReceiverProcess + " by getCommonReceiverNames"
                        );

                        try {
                           if (messageReceiverProcess != null && messageReceiverProcess.equals(RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var0))) {
                              jPushMessageReceiver = (JPushMessageReceiver)var8.newInstance();
                           }
                        } catch (Throwable var10) {
                        }
                        break;
                     }
                  }
               }
            }
         }
      } catch (Throwable var13) {
         Logger.recoverMethod_d_e925fa09("JPushConstants", "getUserServiceClass failed:" + var13.getMessage());
      }

      if (TextUtils.isEmpty(messageReceiverClass)) {
         messageReceiverClass = "";
      }

      if (TextUtils.isEmpty(messageReceiverProcess)) {
         messageReceiverProcess = "";
      }

      return messageReceiverClass;
   }

   public static class Member623641a9 {
      public static String TEST_FILE_URL = "";
      public static String TEST_FILE_VERSION = "";
   }
}
