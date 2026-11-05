package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.widget.ImageView.ScaleType;
import androidx.annotation.NonNull;
import coil.request.ErrorResult;
import coil.request.ImageRequest;
import coil.request.SuccessResult;
import coil.request.ImageRequest.Listener;
import com.facebook.shimmer.ShimmerFrameLayout;
import io.intercom.android.sdk.views.ResizableImageView;
import org.jetbrains.annotations.NotNull;

class NetworkImage$1 implements Listener {
   // $VF: synthetic field
   final ResizableImageView val$imageView;
   // $VF: synthetic field
   final ShimmerFrameLayout val$shimmerView;
   // $VF: synthetic field
   final Context val$context;
   // $VF: synthetic field
   final String val$url;
   // $VF: synthetic field
   final String val$linkUrl;
   // $VF: synthetic field
   final int val$widthPx;
   // $VF: synthetic field
   final int val$heightPx;
   // $VF: synthetic field
   final NetworkImage this$0;

   NetworkImage$1(NetworkImage var1, ResizableImageView var2, ShimmerFrameLayout var3, Context var4, String var5, String var6, int var7, int var8) {
      this.this$0 = var1;
      this.val$imageView = var2;
      this.val$shimmerView = var3;
      this.val$context = var4;
      this.val$url = var5;
      this.val$linkUrl = var6;
      this.val$widthPx = var7;
      this.val$heightPx = var8;
   }

   @Override
   public void onStart(@NotNull ImageRequest var1) {
   }

   @Override
   public void onCancel(@NotNull ImageRequest var1) {
   }

   @Override
   public void onError(@NonNull ImageRequest var1, @NonNull ErrorResult var2) {
      this.val$imageView.setScaleType(ScaleType.CENTER);
      this.this$0.hideLoadingState(this.val$shimmerView, this.val$imageView);
      NetworkImage.access$000(this.this$0).internal("images", "FAILURE");
      this.val$imageView.setScaleType(ScaleType.CENTER);
   }

   @Override
   public void onSuccess(@NonNull ImageRequest var1, @NonNull SuccessResult var2) {
      NetworkImage.access$000(this.this$0).internal("images", "SUCCESS");
      this.this$0.hideLoadingState(this.val$shimmerView, this.val$imageView);
      if (this.this$0.getStyle() != StyleType.CHAT_FULL && NetworkImage.access$100(this.this$0) != null) {
         this.val$imageView.setOnClickListener(new NetworkImage$1$1(this));
      }
   }
}
