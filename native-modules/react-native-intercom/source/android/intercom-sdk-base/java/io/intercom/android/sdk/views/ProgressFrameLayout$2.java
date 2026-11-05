package io.intercom.android.sdk.views;

class ProgressFrameLayout$2 implements Runnable {
   // $VF: synthetic field
   final ProgressFrameLayout this$0;

   ProgressFrameLayout$2(ProgressFrameLayout var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      this.this$0.uploadProgressBar.smoothEndAnimation();
   }
}
