package io.intercom.android.sdk.models.carousel;

import org.jspecify.annotations.Nullable;

final class AutoValue_CarouselResponse extends CarouselResponse {
   private final Carousel$Builder carousel;

   AutoValue_CarouselResponse(Carousel$Builder var1) {
      if (var1 == null) {
         throw new NullPointerException("Null carousel");
      }

      this.carousel = var1;
   }

   @Override
   public Carousel$Builder carousel() {
      return this.carousel;
   }

   @Override
   public String toString() {
      return "CarouselResponse{carousel=" + this.carousel + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof CarouselResponse) {
         CarouselResponse var2 = (CarouselResponse)var1;
         return this.carousel.equals(var2.carousel());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      return var1 ^ this.carousel.hashCode();
   }
}
