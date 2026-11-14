package io.intercom.android.sdk.overlay;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.ViewGroup.MarginLayoutParams;

class DefaultLauncher$3 implements AnimatorUpdateListener {
   // $VF: synthetic field
   final MarginLayoutParams val$params;
   // $VF: synthetic field
   final DefaultLauncher this$0;

   DefaultLauncher$3(DefaultLauncher var1, MarginLayoutParams var2) {
      this.this$0 = var1;
      this.val$params = var2;
   }

   @Override
   public void onAnimationUpdate(ValueAnimator var1) {
      this.val$params.setMargins(this.val$params.leftMargin, this.val$params.topMargin, this.val$params.rightMargin, (Integer)var1.getAnimatedValue());
      this.this$0.launcherRoot.setLayoutParams(this.val$params);
   }
}
