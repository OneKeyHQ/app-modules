package io.intercom.android.sdk.utilities;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

class ViewUtils$1 implements OnGlobalLayoutListener {
   // $VF: synthetic field
   final View val$view;
   // $VF: synthetic field
   final Runnable val$runnable;

   ViewUtils$1(View var1, Runnable var2) {
      this.val$view = var1;
      this.val$runnable = var2;
   }

   @Override
   public void onGlobalLayout() {
      ViewUtils.removeGlobalLayoutListener(this.val$view, this);
      this.val$runnable.run();
   }
}
