package io.intercom.android.sdk.lightbox;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

class LightBoxImageView$GestureListener extends SimpleOnGestureListener {
   // $VF: synthetic field
   final LightBoxImageView this$0;

   LightBoxImageView$GestureListener(LightBoxImageView var1) {
      this.this$0 = var1;
   }

   @Override
   public boolean onDown(MotionEvent var1) {
      return true;
   }

   @Override
   public boolean onSingleTapConfirmed(MotionEvent var1) {
      if (this.this$0.lightBoxListener != null) {
         this.this$0.lightBoxListener.closeLightBox();
      }

      return super.onSingleTapConfirmed(var1);
   }
}
