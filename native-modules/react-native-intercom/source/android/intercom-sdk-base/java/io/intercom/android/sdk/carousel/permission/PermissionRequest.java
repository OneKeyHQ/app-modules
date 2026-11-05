package io.intercom.android.sdk.carousel.permission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.models.carousel.ScreenAction;

public interface PermissionRequest {
   void attach(@Nullable PermissionResultListener var1);

   void detach();

   void request(ScreenAction var1, int var2);

   void handleResult(@NonNull String[] var1, @NonNull int[] var2);
}
