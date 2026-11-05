package io.intercom.android.sdk.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class IntercomToolbar$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final IntercomToolbar this$0;

   IntercomToolbar$1(IntercomToolbar var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.title.setText(null);
   }
}
