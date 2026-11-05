package io.intercom.android.sdk.lightbox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import androidx.appcompat.widget.AppCompatImageView;

public class LightBoxImageView extends AppCompatImageView {
   GestureDetector gestureDetector;
   LightBoxListener lightBoxListener;

   public LightBoxImageView(Context var1) {
      super(var1);
      this.init();
   }

   public LightBoxImageView(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.init();
   }

   void setLightBoxListener(LightBoxListener var1) {
      this.lightBoxListener = var1;
   }

   private void init() {
      this.gestureDetector = new GestureDetector(this.getContext(), new LightBoxImageView$GestureListener(this));
      this.setOnTouchListener(new LightBoxImageView$1(this));
   }
}
