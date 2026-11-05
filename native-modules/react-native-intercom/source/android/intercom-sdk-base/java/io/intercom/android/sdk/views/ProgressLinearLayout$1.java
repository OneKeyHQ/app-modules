package io.intercom.android.sdk.views;

class ProgressLinearLayout$1 implements Runnable {
   // $VF: synthetic field
   final byte val$percentUploaded;
   // $VF: synthetic field
   final ProgressLinearLayout this$0;

   ProgressLinearLayout$1(ProgressLinearLayout var1, byte var2) {
      this.this$0 = var1;
      this.val$percentUploaded = var2;
   }

   @Override
   public void run() {
      if (this.this$0.uploadProgressBar != null) {
         this.this$0.uploadProgressBar.setProgress(this.val$percentUploaded);
      }
   }
}
