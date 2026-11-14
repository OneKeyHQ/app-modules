package io.intercom.android.sdk.carousel;

import io.intercom.android.sdk.models.carousel.Carousel;

public interface CarouselView {
   void showSuccess(Carousel var1);

   void showLoading();

   void showNotFoundError();

   void showGenericError();

   void logEmptyCarouselError();

   void logUserNotRegisteredError();
}
