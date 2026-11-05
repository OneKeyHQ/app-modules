package io.intercom.android.sdk.carousel.permission;

import androidx.annotation.RequiresApi;

public interface PermissionResultListener {
   void showGranted();

   void showDeniedTemporarily();

   void showDeniedPermanently();

   @RequiresApi(api = 30)
   void requestBackgroundLocationPermission();
}
