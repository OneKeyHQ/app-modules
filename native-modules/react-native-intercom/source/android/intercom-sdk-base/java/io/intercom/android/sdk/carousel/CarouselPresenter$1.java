package io.intercom.android.sdk.carousel;

import androidx.annotation.NonNull;
import io.intercom.android.sdk.api.BaseCallback;
import io.intercom.android.sdk.api.ErrorObject;
import io.intercom.android.sdk.models.carousel.CarouselResponse$Builder;

class CarouselPresenter$1 extends BaseCallback {
   // $VF: synthetic field
   final String val$carouselId;
   // $VF: synthetic field
   final CarouselPresenter this$0;

   CarouselPresenter$1(CarouselPresenter var1, String var2) {
      this.this$0 = var1;
      this.val$carouselId = var2;
   }

   protected void onSuccess(@NonNull CarouselResponse$Builder var1) {
      CarouselPresenter.access$002(this.this$0, var1.build().carousel().build());
      if (CarouselPresenter.access$100(this.this$0) != null) {
         CarouselPresenter.access$100(this.this$0).showSuccess(CarouselPresenter.access$000(this.this$0));
      }
   }

   @Override
   protected void onError(@NonNull ErrorObject var1) {
      CarouselPresenter.access$300(this.this$0).failedCarousel(this.val$carouselId, CarouselPresenter.access$200(this.this$0), var1.getStatusCode());
      if (CarouselPresenter.access$100(this.this$0) != null) {
         if (var1.getStatusCode() == 404) {
            CarouselPresenter.access$100(this.this$0).showNotFoundError();
         } else {
            CarouselPresenter.access$100(this.this$0).showGenericError();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected void onSuccess(@NonNull Object var1) {
      this.onSuccess((CarouselResponse$Builder)var1);
   }
}
