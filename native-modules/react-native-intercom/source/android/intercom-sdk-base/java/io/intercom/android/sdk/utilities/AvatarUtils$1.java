package io.intercom.android.sdk.utilities;

import androidx.annotation.NonNull;
import coil.request.ErrorResult;
import coil.request.ImageRequest;
import coil.request.SuccessResult;
import coil.request.ImageRequest.Listener;
import org.jetbrains.annotations.NotNull;

class AvatarUtils$1 implements Listener {
   // $VF: synthetic field
   final Runnable val$runnable;

   AvatarUtils$1(Runnable var1) {
      this.val$runnable = var1;
   }

   @Override
   public void onStart(@NotNull ImageRequest var1) {
   }

   @Override
   public void onCancel(@NotNull ImageRequest var1) {
   }

   @Override
   public void onError(@NonNull ImageRequest var1, @NonNull ErrorResult var2) {
      this.val$runnable.run();
   }

   @Override
   public void onSuccess(@NonNull ImageRequest var1, @NonNull SuccessResult var2) {
      this.val$runnable.run();
   }
}
