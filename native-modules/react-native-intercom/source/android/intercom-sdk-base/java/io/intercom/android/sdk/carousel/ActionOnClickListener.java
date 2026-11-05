package io.intercom.android.sdk.carousel;

import android.view.View;
import android.view.View.OnClickListener;
import io.intercom.android.sdk.models.carousel.ScreenAction;

class ActionOnClickListener implements OnClickListener {
   private final ScreenAction screenAction;
   private final CarouselListener listener;

   ActionOnClickListener(ScreenAction var1, CarouselListener var2) {
      this.screenAction = var1;
      this.listener = var2;
   }

   @Override
   public void onClick(View var1) {
      if (this.screenAction.isPermissionAction()) {
         this.listener.requestPermissions(this.screenAction);
      } else {
         switch (this.screenAction.getType()) {
            case "dismiss":
               this.listener.trackActionButtonTappedStats();
               this.listener.dismissCarousel("from_cta");
               break;
            case "link":
               this.listener.trackActionButtonTappedStats();
               this.listener.openLink(this.screenAction.getUri());
               break;
            case "start_chat":
               this.listener.trackActionButtonTappedStats();
               this.listener.startChat();
               break;
            case "skip":
               this.listener.skipPermissionScreen();
               break;
            case "continue":
            default:
               this.listener.selectNextScreen("from_cta");
         }
      }
   }
}
