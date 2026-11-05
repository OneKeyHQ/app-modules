package io.intercom.android.sdk.sheets;

class SheetWebViewInterface$2 implements Runnable {
   // $VF: synthetic field
   final SheetWebViewInterface this$0;

   SheetWebViewInterface$2(SheetWebViewInterface var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      SheetWebViewInterface.access$000(this.this$0).onCloseSheetAction();
   }
}
