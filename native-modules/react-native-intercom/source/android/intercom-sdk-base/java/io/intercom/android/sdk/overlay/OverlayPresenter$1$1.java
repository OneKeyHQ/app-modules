package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class OverlayPresenter$1$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final OverlayPresenter$1 this$1;

   OverlayPresenter$1$1(OverlayPresenter$1 var1) {
      this.this$1 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$1.val$defaultLauncher.removeView();
   }
}
