package io.intercom.android.sdk.lightbox;

import androidx.activity.OnBackPressedCallback;

class LightBoxActivity$1 extends OnBackPressedCallback {
   // $VF: synthetic field
   final LightBoxActivity this$0;

   LightBoxActivity$1(LightBoxActivity var1, boolean var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public void handleOnBackPressed() {
      this.this$0.closeLightBox();
   }
}
