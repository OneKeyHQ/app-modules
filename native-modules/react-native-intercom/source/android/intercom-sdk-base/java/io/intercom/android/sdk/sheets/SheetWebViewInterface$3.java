package io.intercom.android.sdk.sheets;

import java.util.Map;

class SheetWebViewInterface$3 implements Runnable {
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final SheetWebViewInterface this$0;

   SheetWebViewInterface$3(SheetWebViewInterface var1, Map var2) {
      this.this$0 = var1;
      this.val$payload = var2;
   }

   @Override
   public void run() {
      SheetWebViewInterface.access$000(this.this$0).onSubmitSheetAction(this.val$payload);
   }
}
