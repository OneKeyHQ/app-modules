package io.intercom.android.sdk.blocks;

import androidx.annotation.NonNull;
import coil.request.ErrorResult;
import coil.request.ImageRequest;
import coil.request.SuccessResult;
import coil.request.ImageRequest.Listener;
import io.intercom.android.sdk.views.ResizableImageView;
import org.jetbrains.annotations.NotNull;

class LocalImage$1 implements Listener {
   // $VF: synthetic field
   final ResizableImageView val$imageView;
   // $VF: synthetic field
   final LocalImage this$0;

   LocalImage$1(LocalImage var1, ResizableImageView var2) {
      this.this$0 = var1;
      this.val$imageView = var2;
   }

   @Override
   public void onStart(@NotNull ImageRequest var1) {
   }

   @Override
   public void onCancel(@NotNull ImageRequest var1) {
   }

   @Override
   public void onError(@NonNull ImageRequest var1, @NonNull ErrorResult var2) {
      LocalImage.access$000(this.this$0).internal("images", "FAILURE");
   }

   @Override
   public void onSuccess(@NonNull ImageRequest var1, @NonNull SuccessResult var2) {
      LocalImage.access$000(this.this$0).internal("images", "SUCCESS");
      this.val$imageView.setBackgroundResource(17170445);
   }
}
