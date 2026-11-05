package io.intercom.android.sdk.carousel.permission;

import androidx.annotation.RequiresApi;
import io.intercom.android.sdk.carousel.PermissionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiresApi(api = 30)
class PermissionRequest30 extends PermissionRequestBefore30 {
   PermissionRequest30(PermissionManager var1) {
      super(var1);
   }

   @Override
   protected void handleRequest(List var1, int var2) {
      var1.remove("android.permission.ACCESS_BACKGROUND_LOCATION");
      super.handleRequest(var1, var2);
   }

   @Override
   protected void handleGranted(String[] var1) {
      if (this.askForBackgroundPermission(Arrays.asList(var1))) {
         if (this.isBackgroundPermissionDeniedPermanently()) {
            this.getListener().showDeniedPermanently();
         } else {
            this.getListener().requestBackgroundLocationPermission();
         }
      } else {
         this.getListener().showGranted();
      }
   }

   private boolean askForBackgroundPermission(List var1) {
      return this.isLocationPermission(var1) && this.isBackgroundPermissionInManifest();
   }

   private boolean isLocationPermission(List var1) {
      return var1.contains("android.permission.ACCESS_COARSE_LOCATION") || var1.contains("android.permission.ACCESS_FINE_LOCATION");
   }

   private boolean isBackgroundPermissionInManifest() {
      return !this.permissionManager.permissionsExistInManifest(Collections.singletonList("android.permission.ACCESS_BACKGROUND_LOCATION")).isEmpty();
   }

   private boolean isBackgroundPermissionDeniedPermanently() {
      return this.permissionManager.getPermissionStatus("android.permission.ACCESS_BACKGROUND_LOCATION") == 2;
   }
}
