package io.intercom.android.sdk.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class IntercomSheetActivity$6 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$6(IntercomSheetActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.finish();
      this.this$0.overridePendingTransition(0, 0);
   }
}
