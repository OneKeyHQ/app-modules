package io.intercom.android.sdk.overlay;

import android.app.Activity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

class OverlayPresenter$4 implements OnGlobalLayoutListener {
   // $VF: synthetic field
   final ViewGroup val$root;
   // $VF: synthetic field
   final Activity val$activity;
   // $VF: synthetic field
   final OverlayPresenter this$0;

   OverlayPresenter$4(OverlayPresenter var1, ViewGroup var2, Activity var3) {
      this.this$0 = var1;
      this.val$root = var2;
      this.val$activity = var3;
   }

   @Override
   public void onGlobalLayout() {
      this.val$root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      OverlayPresenter.access$100(this.this$0, this.val$root, this.val$activity);
   }
}
