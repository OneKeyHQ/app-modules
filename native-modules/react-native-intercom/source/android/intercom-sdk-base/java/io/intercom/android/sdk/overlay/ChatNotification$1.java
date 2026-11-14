package io.intercom.android.sdk.overlay;

import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;

class ChatNotification$1 implements AnimatorUpdateListener {
   boolean hasSentUpdate;
   // $VF: synthetic field
   final int val$diff;
   // $VF: synthetic field
   final AnimatorListener val$animatorListener;
   // $VF: synthetic field
   final ChatNotification this$0;

   ChatNotification$1(ChatNotification var1, int var2, AnimatorListener var3) {
      this.this$0 = var1;
      this.val$diff = var2;
      this.val$animatorListener = var3;
      this.hasSentUpdate = false;
   }

   @Override
   public void onAnimationUpdate(ValueAnimator var1) {
      int var2 = (Integer)var1.getAnimatedValue();
      int var3 = this.val$diff > 0 ? var2 / this.val$diff * 100 : 100;
      if (var3 > 80 && !this.hasSentUpdate) {
         this.hasSentUpdate = true;
         this.val$animatorListener.onAnimationEnd(null);
      }
   }
}
