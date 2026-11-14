package io.intercom.android.sdk.activities;

import androidx.activity.OnBackPressedCallback;

class IntercomCarouselActivity$1 extends OnBackPressedCallback {
   // $VF: synthetic field
   final IntercomCarouselActivity this$0;

   IntercomCarouselActivity$1(IntercomCarouselActivity var1, boolean var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public void handleOnBackPressed() {
      if (this.this$0.carouselPresenter.getCarousel().isDismissible()) {
         this.this$0.dismissCarousel("from_android_back_button");
      }
   }
}
