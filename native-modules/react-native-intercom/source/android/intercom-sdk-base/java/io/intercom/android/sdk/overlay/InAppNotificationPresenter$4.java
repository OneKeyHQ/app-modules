package io.intercom.android.sdk.overlay;

import android.view.View;
import android.view.animation.OvershootInterpolator;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

class InAppNotificationPresenter$4 implements Runnable {
   // $VF: synthetic field
   final View val$view;
   // $VF: synthetic field
   final InAppNotificationPresenter this$0;

   InAppNotificationPresenter$4(InAppNotificationPresenter var1, View var2) {
      this.this$0 = var1;
      this.val$view = var2;
   }

   @Override
   public void run() {
      if (this.val$view.isAttachedToWindow()) {
         this.val$view.setY(InAppNotificationPresenter.access$000(this.this$0) + ScreenUtils.dpToPx(66.0F, this.val$view.getContext()));
         this.val$view
            .animate()
            .setInterpolator(new OvershootInterpolator())
            .y(InAppNotificationPresenter.access$000(this.this$0))
            .alpha(1.0F)
            .scaleX(1.0F)
            .scaleY(1.0F)
            .start();
      }
   }
}
