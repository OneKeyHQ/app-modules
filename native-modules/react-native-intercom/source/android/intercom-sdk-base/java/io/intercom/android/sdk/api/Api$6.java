package io.intercom.android.sdk.api;

import io.intercom.android.sdk.models.carousel.CarouselResponse$Builder;

class Api$6 extends BaseCallback {
   // $VF: synthetic field
   final Api this$0;

   Api$6(Api var1) {
      this.this$0 = var1;
   }

   protected void onSuccess(CarouselResponse$Builder var1) {
      Api.access$200(this.this$0).updateCarousel(var1.build().carousel().build());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected void onSuccess(Object var1) {
      this.onSuccess((CarouselResponse$Builder)var1);
   }
}
