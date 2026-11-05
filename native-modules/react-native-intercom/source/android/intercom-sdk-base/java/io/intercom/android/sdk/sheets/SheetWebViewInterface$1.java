package io.intercom.android.sdk.sheets;

import java.util.Map;

class SheetWebViewInterface$1 implements Runnable {
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final SheetWebViewInterface this$0;

   SheetWebViewInterface$1(SheetWebViewInterface var1, Map var2) {
      this.this$0 = var1;
      this.val$payload = var2;
   }

   @Override
   public void run() {
      SheetWebViewInterface.access$000(this.this$0).onSheetTitleAction(this.val$payload.get("title").toString());
   }
}
