package io.intercom.android.sdk;

class ResetManager$1 implements Runnable {
   // $VF: synthetic field
   final ResetManager this$0;

   ResetManager$1(ResetManager var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      this.this$0.hardReset();
   }
}
