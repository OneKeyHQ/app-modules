package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator.AnimatorListener;

class DefaultLauncher$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final AnimatorListener val$listener;
   // $VF: synthetic field
   final DefaultLauncher this$0;

   DefaultLauncher$1(DefaultLauncher var1, AnimatorListener var2) {
      this.this$0 = var1;
      this.val$listener = var2;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.val$listener.onAnimationEnd(var1);
      this.this$0.launcherRoot.animate().scaleX(1.0F).scaleY(1.0F).setDuration(100L).start();
   }
}
