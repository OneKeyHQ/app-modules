package io.intercom.android.sdk.views;

class ProgressFrameLayout$1 implements Runnable {
   // $VF: synthetic field
   final byte val$percentUploaded;
   // $VF: synthetic field
   final ProgressFrameLayout this$0;

   ProgressFrameLayout$1(ProgressFrameLayout var1, byte var2) {
      this.this$0 = var1;
      this.val$percentUploaded = var2;
   }

   @Override
   public void run() {
      this.this$0.uploadProgressBar.setProgress(this.val$percentUploaded);
   }
}
