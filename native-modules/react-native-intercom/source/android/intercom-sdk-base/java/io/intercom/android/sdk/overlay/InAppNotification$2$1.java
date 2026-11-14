package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class InAppNotification$2$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final InAppNotification$2 this$1;

   InAppNotification$2$1(InAppNotification$2 var1) {
      this.this$1 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$1.val$updateEndAction.run();
   }
}
