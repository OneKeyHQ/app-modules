package io.intercom.android.sdk.activities;

import androidx.activity.OnBackPressedCallback;

class IntercomSheetActivity$1 extends OnBackPressedCallback {
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$1(IntercomSheetActivity var1, boolean var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public void handleOnBackPressed() {
      if (this.this$0.webView.canGoBack()) {
         this.this$0.webView.goBack();
      } else {
         this.this$0.closeSheet();
      }
   }
}
