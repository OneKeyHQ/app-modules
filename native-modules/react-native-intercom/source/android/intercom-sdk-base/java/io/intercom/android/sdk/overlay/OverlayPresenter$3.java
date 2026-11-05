package io.intercom.android.sdk.overlay;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;

class OverlayPresenter$3 implements OnAttachStateChangeListener {
   // $VF: synthetic field
   final OverlayPresenter this$0;

   OverlayPresenter$3(OverlayPresenter var1) {
      this.this$0 = var1;
   }

   @Override
   public void onViewAttachedToWindow(View var1) {
      var1.post(() -> {
         WindowInsetsCompat var2 = ViewCompat.getRootWindowInsets(var1);
         if (var2 != null) {
            Insets var3 = var2.getInsets(Type.systemBars());
            OverlayPresenter.access$000(this.this$0, var1, var3);
         }
      });
   }

   @Override
   public void onViewDetachedFromWindow(View var1) {
   }
}
