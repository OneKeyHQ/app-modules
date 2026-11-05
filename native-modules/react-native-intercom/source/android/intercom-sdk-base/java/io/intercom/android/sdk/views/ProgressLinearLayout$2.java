package io.intercom.android.sdk.views;

class ProgressLinearLayout$2 implements Runnable {
   // $VF: synthetic field
   final ProgressLinearLayout this$0;

   ProgressLinearLayout$2(ProgressLinearLayout var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      if (this.this$0.uploadProgressBar != null) {
         this.this$0.uploadProgressBar.smoothEndAnimation(new ProgressLinearLayout$2$1(this));
      }
   }
}
