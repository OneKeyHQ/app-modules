package io.intercom.android.sdk.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewGroup;
import java.util.List;

class OverlayPresenter$2 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final DefaultLauncher val$defaultLauncher;
   // $VF: synthetic field
   final ViewGroup val$rootView;
   // $VF: synthetic field
   final List val$conversations;
   // $VF: synthetic field
   final OverlayPresenter this$0;

   OverlayPresenter$2(OverlayPresenter var1, DefaultLauncher var2, ViewGroup var3, List var4) {
      this.this$0 = var1;
      this.val$defaultLauncher = var2;
      this.val$rootView = var3;
      this.val$conversations = var4;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.val$defaultLauncher.removeView();
      this.this$0.inAppNotificationPresenter.displayNotifications(this.val$rootView, this.val$conversations);
   }
}
