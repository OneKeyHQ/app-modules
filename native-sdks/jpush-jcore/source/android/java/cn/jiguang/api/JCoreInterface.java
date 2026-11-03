package cn.jiguang.api;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import cn.jiguang.bi.RecoverTypeaf2b003f;
import cn.jiguang.bi.RecoverTypeaf2b0040;
import cn.jiguang.bi.RecoverTypeaf2b0041;
import cn.jiguang.bi.RecoverTypeaf2b0042;
import cn.jiguang.bi.RecoverTypeaf2b0043;
import org.json.JSONObject;

public class JCoreInterface {
   private static final String TAG = "JCoreInterface";
   private static Context mApplicationContext;
   public static String DAEMON_ACTION = "cn.jpush.android.intent.DaemonService";

   public static void initAction(String var0, Class var1) {
      RecoverTypeaf2b003f.recoverMethod_a_c69f6d47().recoverMethod_a_e925fa09(var0, var1.getName());
   }

   public static void initActionExtra(String var0, Class var1) {
      RecoverTypeaf2b003f.recoverMethod_a_c69f6d47().recoverMethod_b_e925fa09(var0, var1.getName());
   }

   public static boolean init(Context var0, boolean var1) {
      if (var0 != null) {
         mApplicationContext = var0;
      }

      JCoreManager.init(var0);
      return true;
   }

   public static void register(Context var0) {
      RecoverTypeaf2b0042.recoverMethod_b_e925fa09("JCoreInterface", "Action - init registerOnly:");
      if (var0 != null) {
         mApplicationContext = var0;
      }

      JCoreManager.init(var0);
   }

   public static void sendData(Context var0, String var1, int var2, byte[] var3) {
      send(var0, var1, var2, var3, 0, false);
   }

   private static void send(Context var0, String var1, int var2, byte[] var3, int var4, boolean var5) {
      try {
         if (var3 != null && var3.length > 24) {
            byte[] var6 = new byte[24];
            byte[] var7 = new byte[var3.length - 24];
            System.arraycopy(var3, 0, var6, 0, 24);
            System.arraycopy(var3, 24, var7, 0, var3.length - 24);
            RecoverTypeaf2b0041 var8 = new RecoverTypeaf2b0041(true, var6);
            Bundle var9 = new Bundle();
            var9.putInt("cmd", var8.recoverMethod_a_9b68());
            var9.putInt("ver", var8.recoverMethod_e_9b68());
            var9.putLong("rid", var8.recoverMethod_b_10c66500());
            var9.putLong("timeout", var4);
            var9.putByteArray("body", var7);
            JCoreManager.onEvent(var0, var1, var5 ? 17 : 50, null, var9);
         }
      } catch (Throwable var10) {
         RecoverTypeaf2b0042.recoverMethod_e_e925fa09("JCoreInterface", "send failed:" + var10.getMessage());
      }
   }

   public static void sendRequestData(Context var0, String var1, int var2, byte[] var3) {
      send(var0, var1, 0, var3, var2, true);
   }

   public static void sendAction(Context var0, String var1, Bundle var2) {
      try {
         if (var2 != null) {
            String var3 = var2.getString("action");
            JCoreManager.onEvent(var0, var1, 3, var3, var2);
         }
      } catch (Throwable var4) {
         RecoverTypeaf2b0042.recoverMethod_e_e925fa09("JCoreInterface", "sendAction failed:" + var4);
      }
   }

   public static void stop(Context var0, String var1, Bundle var2) {
      JCoreManager.onEvent(var0, var1, 0, null, null);
   }

   public static void restart(Context var0, String var1, Bundle var2, boolean var3) {
      JCoreManager.onEvent(var0, var1, 1, null, null);
   }

   public static String getDeviceId(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 8, null, null);
      return var1 instanceof String ? (String)var1 : "";
   }

   public static boolean canCallDirect() {
      return false;
   }

   public static void setDebugMode(boolean var0) {
      JCoreManager.setDebugMode(var0);
   }

   public static boolean getDebugMode() {
      return JCoreManager.getDebugMode();
   }

   public static Bundle recoverMethod_si_9c9f3728(Context var0, int var1, Bundle var2) {
      if (var0 != null) {
         mApplicationContext = var0.getApplicationContext();
      }

      return RecoverTypeaf2b0040.recoverMethod_a_9c9f3728(var0, var1, var2);
   }

   public static void execute(String var0, Runnable var1, int... var2) {
      JCoreManager.onEvent(null, null, 11, var0, null, var1);
   }

   public static void asyncExecute(Runnable var0, int... var1) {
      JCoreManager.onEvent(null, null, 12, null, null, var0);
   }

   public static void putSingleExecutor(String var0) {
      JCoreManager.onEvent(null, null, 13, var0, null);
   }

   public static void setAnalysisAction(JAnalyticsAction var0) {
      if (var0 != null) {
         JCoreManager.setAnalysisAction(var0);
      }
   }

   public static String getRegistrationID(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 4, null, null);
      return var1 instanceof String ? (String)var1 : "";
   }

   public static boolean isValidRegistered() {
      Object var0 = JCoreManager.onEvent(mApplicationContext, null, 21, null, null);
      return var0 instanceof Boolean ? (Boolean)var0 : false;
   }

   public static String getAppKey() {
      Object var0 = JCoreManager.onEvent(mApplicationContext, null, 7, null, null);
      return var0 instanceof String ? (String)var0 : "";
   }

   public static long getReportTime() {
      Object var0 = JCoreManager.onEvent(mApplicationContext, null, 19, null, null);
      return var0 instanceof Long ? (Long)var0 : System.currentTimeMillis() / 1000L;
   }

   public static long getUid() {
      Object var0 = JCoreManager.onEvent(mApplicationContext, null, 20, null, null);
      return var0 instanceof Long ? (Long)var0 : 0L;
   }

   public static int getSid() {
      return 0;
   }

   public static long getNextRid() {
      return RecoverTypeaf2b0043.recoverMethod_b_f0554de7(mApplicationContext);
   }

   public static boolean isTcpConnected() {
      return JCoreManager.getConnectionState(mApplicationContext);
   }

   public static boolean getConnectionState(Context var0) {
      return JCoreManager.getConnectionState(mApplicationContext);
   }

   public static String getChannel() {
      Object var0 = JCoreManager.onEvent(mApplicationContext, null, 6, null, null);
      return var0 instanceof String ? (String)var0 : "";
   }

   public static boolean reportHttpData(Context var0, Object var1, String var2) {
      JCoreManager.onEvent(var0, var2, 14, null, null, var1);
      return true;
   }

   public static String getHttpConfig(Context var0, String var1) {
      return "";
   }

   public static boolean getRuningFlag() {
      try {
         if (Build.BRAND.equals("nubia")) {
            return true;
         }
      } catch (Throwable var1) {
      }

      return false;
   }

   @Deprecated
   public static void setImLBSEnable(Context var0, boolean var1) {
      RecoverTypeaf2b0042.recoverMethod_b_e925fa09("JCoreInterface", "action - setImLBSEnable-control");
      JCoreManager.setLBSEnable(var0, var1);
   }

   public static void setWakeEnable(Context var0, boolean var1) {
      JCoreManager.onEvent(var0, null, 73, null, null, var1);
   }

   public static void onResume(Context var0) {
      JCoreManager.onEvent(var0, SdkType.JPUSH.name(), 56, "resume", null);
   }

   public static void onPause(Context var0) {
      JCoreManager.onEvent(var0, SdkType.JPUSH.name(), 56, "pause", null);
   }

   public static void onFragmentResume(Context var0, String var1) {
      JCoreManager.onEvent(var0, SdkType.JPUSH.name(), 56, "f_resume", null, var1);
   }

   public static void onFragmentPause(Context var0, String var1) {
      JCoreManager.onEvent(var0, SdkType.JPUSH.name(), 56, "f_pause", null, var1);
   }

   public static void onKillProcess(Context var0) {
      JCoreManager.onEvent(var0, SdkType.JPUSH.name(), 56, "kill", null);
   }

   public static void initCrashHandler(Context var0) {
      JCoreManager.initCrashHandler(var0);
   }

   public static void stopCrashHandler(Context var0) {
      JCoreManager.stopCrashHandler(var0);
   }

   public static void requestPermission(Context var0) {
      JCoreManager.requestPermission(var0);
   }

   public static void setCanLaunchedStoppedService(boolean var0) {
   }

   public static void setTestConn(boolean var0) {
   }

   public static void setTestConnIPPort(String var0, int var1) {
      RecoverTypeaf2b0042.recoverMethod_b_e925fa09("JCoreInterface", "Action - setTestConnIPPort ip:" + var0 + " port:" + var1);
   }

   public static int getJCoreSDKVersionInt() {
      Object var0 = JCoreManager.onEvent(null, null, 25, null, null);
      return var0 instanceof Integer ? (Integer)var0 : 0;
   }

   public static void processCtrlReport(int var0) {
      JCoreManager.onEvent(null, null, 24, null, null, var0);
   }

   public static void setDaemonAction(String var0) {
      DAEMON_ACTION = var0;
   }

   public static String getDaemonAction() {
      return DAEMON_ACTION;
   }

   public static void recoverMethod_ci_1a552341(Context var0, boolean var1) {
      JCoreManager.onEvent(var0, null, 120, null, null, var1);
   }

   public static void setLocationReportDelay(Context var0, long var1) {
      JCoreManager.onEvent(var0, null, 28, null, null, var1);
   }

   public static void setPowerSaveMode(Context var0, boolean var1) {
   }

   public static void testCountryCode(Context var0, String var1) {
      JCoreManager.onEvent(var0, null, 23, var1, null);
   }

   public static void setCountryCode(Context var0, String var1) {
      JCoreManager.onEvent(var0, null, 128, var1, null);
   }

   public static void setHeartbeatInterval(Context var0, int var1) {
      Bundle var2 = new Bundle();
      var2.putInt("heartbeat_interval", var1);
      JCoreManager.onEvent(var0, null, 55, null, var2);
   }

   public static void report(Context var0, JSONObject var1, boolean var2) {
      JCoreManager.onEvent(var0, "JSupport", 14, null, null, var1);
   }

   public static JSONObject fillBaseReport(JSONObject var0, String var1) {
      Object var2 = JCoreManager.onEvent(null, null, 26, null, null, var0, var1);
      return var2 instanceof JSONObject ? (JSONObject)var2 : null;
   }

   public static void setAccountId(String var0) {
      JCoreManager.onEvent(null, null, 22, var0, null);
   }

   public static String getAccountId() {
      Object var0 = JCoreManager.onEvent(null, null, 5, null, null);
      return var0 instanceof String ? (String)var0 : "";
   }

   public static String getCommonConfigAppkey() {
      Object var0 = JCoreManager.onEvent(mApplicationContext, null, 7, null, null);
      return var0 instanceof String ? (String)var0 : "";
   }

   public static void triggerSceneCheck(Context var0, int var1) {
      JCoreManager.onEvent(var0, null, 29, null, null, var1);
   }

   public static void enableSDKLocalLog(Context var0, boolean var1, boolean var2) {
      Bundle var3 = new Bundle();
      var3.putBoolean("enable", var1);
      var3.putBoolean("upload", var2);
      JCoreManager.onEvent(var0, null, 130, null, var3);
   }

   public static String readNewLogs(Context var0) {
      Object var1 = JCoreManager.onEvent(var0, null, 131, null, null);
      return var1 instanceof String ? (String)var1 : "";
   }
}
