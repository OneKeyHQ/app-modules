package io.intercom.android.sdk.carousel;

import android.view.View;
import io.intercom.android.sdk.views.ContentAwareScrollView;
import io.intercom.android.sdk.views.ContentAwareScrollView$Listener;

class CarouselScreenFragment$1 implements ContentAwareScrollView$Listener {
   // $VF: synthetic field
   final View val$gradientView;
   // $VF: synthetic field
   final ContentAwareScrollView val$contentAwareScrollView;
   // $VF: synthetic field
   final CarouselScreenFragment this$0;

   CarouselScreenFragment$1(CarouselScreenFragment var1, View var2, ContentAwareScrollView var3) {
      this.this$0 = var1;
      this.val$gradientView = var2;
      this.val$contentAwareScrollView = var3;
   }

   @Override
   public void onBottomReached() {
      this.val$gradientView.setVisibility(8);
   }

   @Override
   public void onScrollChanged(int var1) {
      if (!this.val$contentAwareScrollView.isAtBottom()) {
         this.val$gradientView.setVisibility(0);
      }
   }
}
