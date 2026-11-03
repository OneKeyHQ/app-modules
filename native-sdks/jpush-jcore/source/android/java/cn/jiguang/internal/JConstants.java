package cn.jiguang.internal;

import android.content.Context;
import android.os.Build.VERSION;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JAnalyticsAction;
import cn.jiguang.bk.RecoverTypeaf2b07d2;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class JConstants {
   public static String APP_KEY = "";
   public static LinkedList TUU = new LinkedList();
   public static boolean AT_I = false;
   private static final String TAG = "JConstants";
   public static JAnalyticsAction jAnalyticsAction;
   public static final boolean SUPPORT_DY = false;
   public static String SDK_NAME = "";
   public static String SDK_VERSION = "5.2.0";
   public static int SDK_VERSION_INT = 520;
   public static final boolean LOG_ENABLE = true;
   public static boolean DEBUG_MODE = false;
   public static final boolean LOG_FILE_ENABLE = true;
   public static final boolean INTERNAL_USE = false;
   public static final int LOG_ENABLED_LEVEL = 2;
   public static boolean CMD_TO_PRINT_ALL_LOG = false;
   public static final boolean IS_FOR_GOOGLE_PLAY_USE = true;
   public static final boolean IS_FOR_GOOGLE_PURE_USE = false;
   public static final boolean IS_FOR_JBASIC = false;
   public static final String USER_SERVICE_ACTION = "cn.jiguang.user.service.action";
   public static final String DATA_PROVIDER_SUFFIX = ".DataProvider";
   public static final String SDK_TYPE = "JCore";
   public static final long SECOND = 1000L;
   public static final long MIN = 60000L;
   public static final long HOUR = 3600000L;
   public static final long DAY = 86400000L;
   public static final boolean REPORT_USE_HTTPS = true;
   public static final boolean REPORT_SUPPORT_HTTP = false;
   public static boolean isInstrumentationHookFailed = true;
   public static AtomicBoolean isCallInit = new AtomicBoolean();
   public static final String ENCODING_UTF_8 = "UTF-8";
   public static int DEFAULT_HEARTBEAT_INTERVAL = 285;
   public static int DEFAULT_ALARM_DELAY = DEFAULT_HEARTBEAT_INTERVAL + 5;
   public static final byte AES_CBC = 1;
   public static final byte SM4_CBC = 2;
   public static byte tcpAlgorithm = 1;
   public static int tcpSessionId;
   private static boolean testEnv = false;
   public static String testCountry = "";
   public static String testBannedConfigUrl = "";
   public static String testLimitConfigUrl = "";
   public static String testJOperateBaseUrl;
   private static boolean isTestAndroidQ = false;
   private static final AtomicReference mApplicationContext = new AtomicReference();

   public static boolean isTestEnv() {
      return false;
   }

   public static void setTestEnv(boolean var0) {
   }

   public static void testAndroidQ() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JConstants", "call testAndroidQ");
      isTestAndroidQ = true;
   }

   public static boolean isAndroidQ(Context var0, boolean var1, String var2) {
      boolean var3 = false;
      if (!var1) {
         var3 = isTestAndroidQ ? VERSION.SDK_INT >= 28 : VERSION.SDK_INT > 28;
      } else {
         var3 = (isTestAndroidQ ? VERSION.SDK_INT >= 28 : VERSION.SDK_INT > 28) && var0.getApplicationInfo().targetSdkVersion > 28;
      }

      if (var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JConstants", "is Android Q, msg: " + var2);
      }

      return var3;
   }

   public static Context getAppContext() {
      return getAppContext(null);
   }

   public static Context getAppContext(Context var0) {
      Context var1 = (Context)mApplicationContext.get();
      if (var1 == null) {
         if (var0 != null) {
            var1 = var0.getApplicationContext();
         } else {
            var1 = RecoverTypeaf2b07d2.recoverMethod_a_c4d1044();
         }

         mApplicationContext.set(var1);
      }

      return var1;
   }
}
