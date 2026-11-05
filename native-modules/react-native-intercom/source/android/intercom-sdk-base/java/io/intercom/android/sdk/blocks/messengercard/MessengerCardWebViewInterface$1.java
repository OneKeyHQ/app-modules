package io.intercom.android.sdk.blocks.messengercard;

import java.util.Map;

class MessengerCardWebViewInterface$1 implements Runnable {
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final MessengerCardWebViewInterface this$0;

   MessengerCardWebViewInterface$1(MessengerCardWebViewInterface var1, Map var2) {
      this.this$0 = var1;
      this.val$payload = var2;
   }

   @Override
   public void run() {
      MessengerCardWebViewInterface.access$100(this.this$0, this.val$payload, MessengerCardWebViewInterface.access$000(this.this$0));
   }
}
