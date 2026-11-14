package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class InAppNotification$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final InAppNotification this$0;

   InAppNotification$1(InAppNotification var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.listener.onInAppNotificationDismiss(this.this$0);
   }
}
