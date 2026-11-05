package io.intercom.android.sdk.utilities.commons;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;

public class DeviceUtils {
   public static boolean hasPermission(Context var0, String var1) {
      int var2 = var0.checkCallingOrSelfPermission(var1);
      return var2 == 0;
   }

   public static String getAppVersion(Context var0) {
      String var1 = "";

      try {
         PackageInfo var2 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
         if (var2.versionName != null) {
            var1 = var2.versionName;
         }
      } catch (RuntimeException | NameNotFoundException var3) {
      }

      return var1;
   }

   public static String getAppMinSdkVersionVersion(Context var0) {
      String var1 = "";

      try {
         PackageInfo var2 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
         if (var2.applicationInfo != null && VERSION.SDK_INT > 23) {
            var1 = String.valueOf(var2.applicationInfo.minSdkVersion);
         }
      } catch (RuntimeException | NameNotFoundException var3) {
      }

      return var1;
   }

   public static String getAppName(Context var0) {
      String var1 = var0.getPackageName();
      return var1 == null ? "" : var1;
   }
}
