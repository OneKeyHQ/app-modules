package io.intercom.android.sdk.lightbox;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class LightBoxImageView$1 implements OnTouchListener {
   // $VF: synthetic field
   final LightBoxImageView this$0;

   LightBoxImageView$1(LightBoxImageView var1) {
      this.this$0 = var1;
   }

   @Override
   public boolean onTouch(View var1, MotionEvent var2) {
      return this.this$0.gestureDetector.onTouchEvent(var2);
   }
}
