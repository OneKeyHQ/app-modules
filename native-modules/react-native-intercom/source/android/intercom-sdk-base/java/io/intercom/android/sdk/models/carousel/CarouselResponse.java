package io.intercom.android.sdk.models.carousel;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CarouselResponse {
   static CarouselResponse create(Carousel$Builder var0) {
      return new AutoValue_CarouselResponse(var0);
   }

   public abstract Carousel$Builder carousel();
}
