package io.intercom.android.sdk.carousel.permission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.carousel.PermissionManager;
import io.intercom.android.sdk.models.carousel.ScreenAction;
import java.util.Arrays;
import java.util.List;

class PermissionRequestBefore30 implements PermissionRequest {
   protected final PermissionManager permissionManager;
   @Nullable
   private PermissionResultListener permissionResultListener;
   private final PermissionResultListener nullListener = new PermissionRequestBefore30$NullPermissionResultListener(null);

   PermissionRequestBefore30(PermissionManager var1) {
      this.permissionManager = var1;
   }

   @Override
   public void attach(@Nullable PermissionResultListener var1) {
      this.permissionResultListener = var1;
   }

   @Override
   public void detach() {
      this.permissionResultListener = null;
   }

   @Override
   public void request(ScreenAction var1, int var2) {
      List var3 = var1.getValidPermissions(this.permissionManager);
      if (!var3.isEmpty()) {
         this.handleRequest(var3, var2);
      }
   }

   @Override
   public void handleResult(@NonNull String[] var1, @NonNull int[] var2) {
      this.handleRequestResult(var1, var2);
   }

   protected void handleGranted(String[] var1) {
      this.getListener().showGranted();
   }

   protected void handleRequest(List var1, int var2) {
      String[] var3 = (String[])var1.toArray(new String[0]);
      this.permissionManager.requestPermissions(var3, var2);
   }

   @NonNull
   PermissionResultListener getListener() {
      return this.permissionResultListener == null ? this.nullListener : this.permissionResultListener;
   }

   private void handleRequestResult(@NonNull String[] var1, @NonNull int[] var2) {
      if (this.permissionManager.anyPermissionPermanentlyDeniedInResult(var1, var2)) {
         this.getListener().showDeniedPermanently();
      } else if (this.permissionManager.permissionsGranted(Arrays.asList(var1))) {
         this.handleGranted(var1);
      } else {
         this.getListener().showDeniedTemporarily();
      }
   }
}
