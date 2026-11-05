package io.intercom.android.sdk.carousel.permission;

import android.os.Build.VERSION;
import io.intercom.android.sdk.carousel.PermissionManager;

public final class PermissionRequestFactory {
   private PermissionRequestFactory() {
   }

   public static PermissionRequest create(PermissionManager var0) {
      int var1 = VERSION.SDK_INT;
      return var1 >= 30 ? new PermissionRequest30(var0) : new PermissionRequestBefore30(var0);
   }
}
