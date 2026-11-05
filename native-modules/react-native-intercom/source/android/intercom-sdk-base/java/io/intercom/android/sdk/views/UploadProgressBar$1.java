package io.intercom.android.sdk.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class UploadProgressBar$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final UploadProgressBar this$0;

   UploadProgressBar$1(UploadProgressBar var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.hideBar();
   }
}
