package io.intercom.android.sdk.carousel;

import io.intercom.android.sdk.models.carousel.ScreenAction;

public interface CarouselListener {
   void dismissCarousel(String var1);

   void selectNextScreen(String var1);

   void requestPermissions(ScreenAction var1);

   void openLink(String var1);

   void startChat();

   void skipPermissionScreen();

   void trackActionButtonTappedStats();
}
