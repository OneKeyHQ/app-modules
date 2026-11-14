package io.intercom.android.sdk.blocks.messengercard;

import java.util.Map;

class MessengerCardWebViewInterface$5 implements Runnable {
   // $VF: synthetic field
   final MessengerCardWebViewAction val$action;
   // $VF: synthetic field
   final MessengerCardWebViewInterface this$0;

   MessengerCardWebViewInterface$5(MessengerCardWebViewInterface var1, MessengerCardWebViewAction var2) {
      this.this$0 = var1;
      this.val$action = var2;
   }

   @Override
   public void run() {
      Map var1 = this.val$action.getPayload();
      String var2 = (String)var1.get("action");
      String var3 = (String)var1.get("object");
      String var4 = (String)var1.get("place");
      Map var5 = null;
      if (var1.get("metadata") instanceof Map) {
         var5 = (Map<String, Object>)var1.get("metadata");
      }

      MessengerCardWebViewInterface.access$800(this.this$0)
         .messengerCardWebViewEvent(var2, var3, var4, var5, MessengerCardWebViewInterface.access$700(this.this$0));
   }
}
