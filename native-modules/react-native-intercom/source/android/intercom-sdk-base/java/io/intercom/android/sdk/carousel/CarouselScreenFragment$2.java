package io.intercom.android.sdk.carousel;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import io.intercom.android.sdk.views.ContentAwareScrollView;

class CarouselScreenFragment$2 implements OnGlobalLayoutListener {
   // $VF: synthetic field
   final ContentAwareScrollView val$contentAwareScrollView;
   // $VF: synthetic field
   final View val$gradientView;
   // $VF: synthetic field
   final CarouselScreenFragment this$0;

   CarouselScreenFragment$2(CarouselScreenFragment var1, ContentAwareScrollView var2, View var3) {
      this.this$0 = var1;
      this.val$contentAwareScrollView = var2;
      this.val$gradientView = var3;
   }

   @Override
   public void onGlobalLayout() {
      if (this.val$contentAwareScrollView.isAtBottom()) {
         this.val$gradientView.setVisibility(8);
      }
   }
}
