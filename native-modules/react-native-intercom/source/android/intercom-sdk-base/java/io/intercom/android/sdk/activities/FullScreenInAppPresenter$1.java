package io.intercom.android.sdk.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;

class FullScreenInAppPresenter$1 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final Activity val$activity;
   // $VF: synthetic field
   final FullScreenInAppPresenter this$0;

   FullScreenInAppPresenter$1(FullScreenInAppPresenter var1, Activity var2) {
      this.this$0 = var1;
      this.val$activity = var2;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.val$activity.finish();
      this.val$activity.overridePendingTransition(0, 0);
   }
}
