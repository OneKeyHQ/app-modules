package io.intercom.android.sdk.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.view.animation.DecelerateInterpolator;

class ProgressLinearLayout$2$1 implements AnimatorListener {
   // $VF: synthetic field
   final ProgressLinearLayout$2 this$1;

   ProgressLinearLayout$2$1(ProgressLinearLayout$2 var1) {
      this.this$1 = var1;
   }

   @Override
   public void onAnimationStart(Animator var1) {
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$1.this$0.uploadProgressBar.hideBar();
      if (this.this$1.this$0.attachmentIcon != null) {
         this.this$1.this$0.attachmentIcon.setVisibility(0);
         ObjectAnimator var2 = ObjectAnimator.ofFloat(this.this$1.this$0.attachmentIcon, "alpha", 0.0F, 1.0F);
         var2.setDuration(300L);
         var2.setInterpolator(new DecelerateInterpolator());
         var2.start();
      }
   }

   @Override
   public void onAnimationCancel(Animator var1) {
   }

   @Override
   public void onAnimationRepeat(Animator var1) {
   }
}
