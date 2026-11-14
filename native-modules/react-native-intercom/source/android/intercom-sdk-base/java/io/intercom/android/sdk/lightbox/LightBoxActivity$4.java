package io.intercom.android.sdk.lightbox;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

class LightBoxActivity$4 implements AnimatorUpdateListener {
   // $VF: synthetic field
   final LightBoxActivity this$0;

   LightBoxActivity$4(LightBoxActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationUpdate(ValueAnimator var1) {
      this.this$0.rootView.setBackgroundColor((Integer)var1.getAnimatedValue());
   }
}
