package io.intercom.android.sdk.lightbox;

import android.os.Handler;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;

class LightBoxActivity$2 implements TransitionListener {
   // $VF: synthetic field
   final LightBoxImageView val$fullImage;
   // $VF: synthetic field
   final LightBoxActivity this$0;

   LightBoxActivity$2(LightBoxActivity var1, LightBoxImageView var2) {
      this.this$0 = var1;
      this.val$fullImage = var2;
   }

   @Override
   public void onTransitionStart(Transition var1) {
   }

   @Override
   public void onTransitionEnd(Transition var1) {
      Handler var2 = new Handler();
      var2.postDelayed(new LightBoxActivity$2$1(this), 10L);
   }

   @Override
   public void onTransitionCancel(Transition var1) {
   }

   @Override
   public void onTransitionPause(Transition var1) {
   }

   @Override
   public void onTransitionResume(Transition var1) {
   }
}
