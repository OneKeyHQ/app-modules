package io.intercom.android.sdk.models;

import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.models.carousel.Carousel$Builder;

public class LogEventResponse extends UsersResponse {
   private final Carousel carousel;

   LogEventResponse(LogEventResponse$Builder var1) {
      super(var1);
      this.carousel = var1.carousel == null ? new Carousel$Builder().build() : var1.carousel.build();
   }

   public Carousel getCarousel() {
      return this.carousel;
   }
}
