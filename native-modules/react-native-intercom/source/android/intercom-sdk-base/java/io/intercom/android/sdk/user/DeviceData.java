package io.intercom.android.sdk.user;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import io.intercom.android.sdk.api.PlatformIdentifierUtilKt;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.utilities.commons.DeviceUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeviceData {
   public static Map generateDeviceData(Context var0, PushTokenStore var1) {
      Map var2 = new HashMap();
      var2.put("platform_version", VERSION.RELEASE);
      var2.put("sdk_type", PlatformIdentifierUtilKt.getPlatformIdentifier(var0));
      var2.put("platform", Build.MODEL);
      var2.put("browser", "Intercom-Android-SDK");
      var2.put("version", DeviceUtils.getAppVersion(var0));
      var2.put("application", getApplicationName(var0));
      var2.put("application_id", DeviceUtils.getAppName(var0));
      var2.put("language", Locale.getDefault().getDisplayLanguage());
      String var3 = var1.getPushToken();
      if (!var3.isEmpty()) {
         var2.put("device_token", var3);
      }

      return var2;
   }

   public static String getApplicationName(Context var0) {
      PackageManager var1 = var0.getPackageManager();

      ApplicationInfo var2;
      try {
         var2 = var1.getApplicationInfo(var0.getApplicationInfo().packageName, 0);
      } catch (NameNotFoundException var4) {
         return "";
      }

      CharSequence var3 = var2 != null ? var1.getApplicationLabel(var2) : "";
      return var3.toString();
   }
}
