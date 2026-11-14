package io.intercom.android.sdk.models.carousel;

public final class CarouselResponse$Builder {
   Carousel$Builder carousel;

   public CarouselResponse$Builder withCarousel(Carousel$Builder var1) {
      this.carousel = var1;
      return this;
   }

   public CarouselResponse build() {
      return CarouselResponse.create(this.carousel == null ? Carousel.NULL_BUILDER : this.carousel);
   }
}
