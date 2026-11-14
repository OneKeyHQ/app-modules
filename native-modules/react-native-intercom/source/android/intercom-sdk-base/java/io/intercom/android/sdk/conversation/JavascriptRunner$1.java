package io.intercom.android.sdk.conversation;

class JavascriptRunner$1 implements Runnable {
   // $VF: synthetic field
   final String val$script;
   // $VF: synthetic field
   final JavascriptRunner this$0;

   JavascriptRunner$1(JavascriptRunner var1, String var2) {
      this.this$0 = var1;
      this.val$script = var2;
   }

   @Override
   public void run() {
      JavascriptRunner.access$000(this.this$0).loadUrl("javascript:" + this.val$script);
   }
}
