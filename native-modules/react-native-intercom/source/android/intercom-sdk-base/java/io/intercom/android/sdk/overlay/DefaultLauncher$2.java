package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

class DefaultLauncher$2 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final DefaultLauncher this$0;

   DefaultLauncher$2(DefaultLauncher var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.listener.onLauncherClicked(this.this$0.launcherRoot.getContext());
   }
}
