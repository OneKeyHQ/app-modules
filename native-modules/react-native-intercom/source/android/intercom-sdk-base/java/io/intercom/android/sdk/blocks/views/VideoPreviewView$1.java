package io.intercom.android.sdk.blocks.views;

import android.graphics.PorterDuff.Mode;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import coil.request.ErrorResult;
import coil.request.ImageRequest;
import coil.request.SuccessResult;
import coil.request.ImageRequest.Listener;
import io.intercom.android.sdk.R.color;
import org.jetbrains.annotations.NotNull;

class VideoPreviewView$1 implements Listener {
   // $VF: synthetic field
   final VideoPreviewView this$0;

   VideoPreviewView$1(VideoPreviewView var1) {
      this.this$0 = var1;
   }

   @Override
   public void onStart(@NotNull ImageRequest var1) {
   }

   @Override
   public void onCancel(@NotNull ImageRequest var1) {
   }

   @Override
   public void onError(@NonNull ImageRequest var1, @NonNull ErrorResult var2) {
      VideoPreviewView.access$000(this.this$0).setVisibility(8);
      VideoPreviewView.access$100(this.this$0).setVisibility(0);
      VideoPreviewView.access$200(this.this$0);
      VideoPreviewView.access$300(this.this$0).setVisibility(8);
   }

   @Override
   public void onSuccess(@NonNull ImageRequest var1, @NonNull SuccessResult var2) {
      VideoPreviewView.access$000(this.this$0).setVisibility(8);
      VideoPreviewView.access$100(this.this$0).setVisibility(0);
      VideoPreviewView.access$200(this.this$0);
      int var3 = ContextCompat.getColor(VideoPreviewView.access$100(this.this$0).getContext(), color.intercom_semi_transparent);
      VideoPreviewView.access$100(this.this$0).setColorFilter(var3, Mode.DARKEN);
      VideoPreviewView.access$300(this.this$0).setVisibility(0);
   }
}
