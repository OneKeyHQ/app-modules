package io.intercom.android.sdk.activities;

import android.app.Activity;
import android.view.View;

class FullScreenInAppPresenter {
   void closeWindow(Activity var1) {
      View var2 = var1.getWindow().getDecorView();
      var2.setAlpha(1.0F);
      var2.animate().alpha(0.0F).setDuration(200L).setListener(new FullScreenInAppPresenter$1(this, var1)).start();
   }
}
