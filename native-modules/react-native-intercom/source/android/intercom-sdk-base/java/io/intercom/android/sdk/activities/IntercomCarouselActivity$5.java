package io.intercom.android.sdk.activities;

class IntercomCarouselActivity$5 implements Runnable {
   // $VF: synthetic field
   final IntercomCarouselActivity this$0;

   IntercomCarouselActivity$5(IntercomCarouselActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      this.this$0.selectNextScreen("from_permission");
   }
}
