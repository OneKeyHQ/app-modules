package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

class InAppNotification$2 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final View val$text;
   // $VF: synthetic field
   final Runnable val$updateEndAction;
   // $VF: synthetic field
   final InAppNotification this$0;

   InAppNotification$2(InAppNotification var1, View var2, Runnable var3) {
      this.this$0 = var1;
      this.val$text = var2;
      this.val$updateEndAction = var3;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.updateViewDataDuringReplyPulse(this.this$0.bottomPadding);
      ObjectAnimator var2 = ObjectAnimator.ofFloat(this.val$text, View.ALPHA, 0.0F, 1.0F).setDuration(200L);
      var2.setStartDelay(100L);
      var2.start();
      var2.addListener(new InAppNotification$2$1(this));
   }
}
