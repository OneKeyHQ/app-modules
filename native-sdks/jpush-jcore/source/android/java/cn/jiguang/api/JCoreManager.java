package cn.jiguang.api;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07d0;
import cn.jiguang.c.RecoverType5a68950;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JCoreManager {
   private static final String TAG = "JCoreManager";
   private static final AtomicBoolean isInited = new AtomicBoolean();
   private static int DEFAULT_CMD = -1;

   public static void init(Context var0) {
      init(var0, null);
   }

   public static void init(Context var0, Bundle var1) {
      init(var0, DEFAULT_CMD, var1);
   }

   public static void init(Context var0, int var1, Bundle var2) {
      var0 = JConstants.getAppContext(var0);
      if (var0 != null) {
         if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreManager", "user don't auth, so return init");
         } else {
            JConstants.isCallInit.set(true);
            if (!isInited.get()) {
               isInited.set(true);

               try {
                  boolean var3 = RecoverTypeaf2a972a.recoverMethod_a_f0554df7(var0);
                  if (var3) {
                     isInited.set(false);
                     JConstants.isCallInit.set(false);
                     return;
                  }

                  RecoverType5a68d10.recoverMethod_a_f0554df3(var0);
                  JCoreInternalHelper.getInstance().onEvent(var0, "JCore", 10, true, "tcp_a1", var2);
               } catch (Throwable var4) {
               }
            } else if (96 == var1) {
               JCoreInternalHelper.getInstance().onEvent(var0, "JCore", 10, true, "tcp_a1", var2);
            }
         }
      }
   }

   public static void register(Context var0, String var1) {
      try {
         register(var0, var1, null);
      } catch (Throwable var3) {
      }
   }

   public static void register(Context var0, String var1, Bundle var2) {
      try {
         String var3 = RecoverTypeaf2a9723.recoverMethod_b_e501eb0d(var0);
         if (!TextUtils.isEmpty(var3) && !var3.equals(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[register] not same appkey with manifest,please check it");
         } else {
            JConstants.APP_KEY = var1;
         }

         RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCoreManager", "[register]  appkey =" + JConstants.APP_KEY + " originKep=" + var1);
         boolean var4 = RecoverTypeaf2a972a.recoverMethod_a_f0554df7(var0);
         if (var4) {
            JConstants.isCallInit.set(false);
            return;
         }

         JConstants.isCallInit.set(true);
         RecoverType5a68d10.recoverMethod_a_f0554df3(var0);
         if (null == var2) {
            var2 = new Bundle();
         }

         var2.putString("appkey", var1);
         JCoreInternalHelper.getInstance().onEvent(var0, "JCore", 10, true, "a5", var2);
      } catch (Throwable var5) {
      }
   }

   public static void unRegister(Context var0) {
      JConstants.isCallInit.set(true);
      RecoverType5a68d10.recoverMethod_a_f0554df3(var0);
      JCoreInternalHelper.getInstance().onEvent(var0, "JCore", 10, true, "tcp_a23", null);
   }

   public static void changeLiveStatus(boolean var0) {
   }

   public static void addActivityLifeCallback(ActivityLifecycleCallbacks var0) {
      RecoverType5a68d10.recoverMethod_a_331c7aaf(var0);
   }

   public static void removeActivityLifeCallback(ActivityLifecycleCallbacks var0) {
      RecoverType5a68d10.recoverMethod_b_331c7aaf(var0);
   }

   public static void setLBSEnable(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreManager", "action - setLBSEnable-control:" + var1);
      RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09(
         "JCoreManager",
         "Since the SDK has provided a new geographical location control interface, part of the control logic has become invalid, and this interface will be gradually deprecated. Please adapt to the new interface as soon as possible."
      );

      try {
         if (!var1) {
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ae_48794abf().recoverMethod_a_4e9722a3(false));
         }
      } catch (Throwable var3) {
      }

      onEvent(var0, null, 27, null, null, var1);
   }

   public static void setDebugMode(boolean var0) {
      JConstants.DEBUG_MODE = var0;
   }

   public static boolean getDebugMode() {
      return JConstants.DEBUG_MODE;
   }

   public static boolean isInternal() {
      return false;
   }

   public static void addDispatchAction(String var0, String var1) {
      RecoverType5a68d10.recoverMethod_a_e925fa09(var0, var1);
   }

   public static Object onEvent(Context var0, String var1, int var2, String var3, Bundle var4, Object... var5) {
      boolean var6 = true;
      switch (var2) {
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 11:
         case 12:
         case 13:
         case 18:
         case 19:
         case 20:
         case 21:
         case 25:
         case 26:
         case 32:
         case 33:
         case 34:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         case 49:
         case 52:
         case 53:
         case 58:
         case 60:
         case 63:
         case 64:
         case 67:
         case 68:
         case 69:
         case 75:
         case 76:
         case 77:
         case 78:
         case 82:
         case 83:
         case 90:
         case 91:
         case 92:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 108:
         case 109:
         case 110:
         case 119:
         case 120:
         case 121:
         case 123:
         case 130:
         case 134:
            var6 = false;
         case 9:
         case 10:
         case 14:
         case 15:
         case 16:
         case 17:
         case 22:
         case 23:
         case 24:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 44:
         case 48:
         case 50:
         case 51:
         case 54:
         case 55:
         case 56:
         case 57:
         case 59:
         case 61:
         case 62:
         case 65:
         case 66:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 79:
         case 80:
         case 81:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 93:
         case 105:
         case 106:
         case 107:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 122:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 131:
         case 132:
         case 133:
         default:
            return onEvent(var0, var1, var2, var6, var3, var4, var5);
      }
   }

   public static Object onEvent(Context var0, String var1, int var2, boolean var3, String var4, Bundle var5, Object... var6) {
      if (96 == var2) {
         try {
            boolean var7 = (Boolean)var6[0];
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreManager", "saveAuth：" + var7);
            RecoverTypeaf2b07c2.recoverMethod_a_25decb5(var7, true);
         } catch (Throwable var8) {
         }
      }

      if (!RecoverType5a68950.recoverField_a.contains(var2)) {
         init(var0, var2, var5);
      }

      return JCoreInternalHelper.getInstance().onEvent(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void setAnalysisAction(JAnalyticsAction var0) {
      JConstants.jAnalyticsAction = var0;
   }

   public static void requestPermission(Context var0) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[requestPermission] context was null");
      } else if (!(var0 instanceof Activity)) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[requestPermission] context must instanceof Activity");
      } else if (VERSION.SDK_INT < 23) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[requestPermission] android.os.Build.VERSION.SDK_INT<23");
      } else if (var0.getApplicationInfo().targetSdkVersion < 23) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[requestPermission] app targetSdkVersion<23");
      } else {
         if (VERSION.SDK_INT >= 23 && var0.getApplicationInfo().targetSdkVersion >= 23) {
            try {
               LinkedList var1 = new LinkedList();
               List var2 = RecoverTypeaf2b07d0.recoverMethod_a_c98d58e3(var0, var1);
               if (var2 == null || var2.isEmpty()) {
                  return;
               }

               RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JCoreManager", "lackPermissions:" + var2);
               Class var3 = Class.forName("android.app.Activity");
               Method var4 = var3.getDeclaredMethod("requestPermissions", String[].class, int.class);
               var4.invoke(var0, var2.toArray(new String[var2.size()]), 1);
            } catch (Exception var5) {
               RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "#unexcepted - requestPermission e:" + var5);
            }
         }
      }
   }

   public static void initCrashHandler(Context var0) {
      onEvent(var0, "JCore", 70, true, null, null);
   }

   public static void stopCrashHandler(Context var0) {
      onEvent(var0, "JCore", 71, true, null, null);
   }

   public static boolean isTestEnv() {
      return JConstants.isTestEnv();
   }

   public static void setSDKConfigs(Context var0, Bundle var1) {
      JCoreInternalHelper.getInstance().onEvent(var0, "JCore", 55, true, null, var1);
   }

   public static Context getAppContext(Context var0) {
      return JConstants.getAppContext(var0);
   }

   @Deprecated
   public static boolean getConnectionState(Context var0) {
      Object var1 = JCoreInternalHelper.getInstance().onEvent(var0, "JCore", 67, false, null, null);
      return var1 instanceof Boolean ? (Boolean)var1 : false;
   }

   public static void reportWakedData(Context var0, Bundle var1, int var2) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[reportWakedData] context was null");
      } else if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[reportWakedData] bundle was null");
      } else {
         int var3 = var1.getInt("type", -1);
         if (var3 == -1) {
            var3 = var2;
         }

         if (var3 != 1 && var3 != 2 && var3 != 4 && var3 != 8 && var3 != 16 && var3 != 32) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreManager", "[reportWakedData] wrong type:" + var3);
         } else {
            var1.putInt("type", var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreManager", "action - reportWakedData" + var1.toString());
            onEvent(var0, null, 74, null, var1);
         }
      }
   }
}
