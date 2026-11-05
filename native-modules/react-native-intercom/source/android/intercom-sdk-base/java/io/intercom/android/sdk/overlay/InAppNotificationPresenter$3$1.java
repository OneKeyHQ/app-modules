package io.intercom.android.sdk.overlay;

class InAppNotificationPresenter$3$1 implements Runnable {
   // $VF: synthetic field
   final InAppNotificationPresenter$3 this$1;

   InAppNotificationPresenter$3$1(InAppNotificationPresenter$3 var1) {
      this.this$1 = var1;
   }

   @Override
   public void run() {
      if (this.this$1.val$root.isAttachedToWindow()) {
         this.this$1.this$0.syncUpdates(this.this$1.val$conversations, this.this$1.val$notifications, this.this$1.val$root);
      }
   }
}
