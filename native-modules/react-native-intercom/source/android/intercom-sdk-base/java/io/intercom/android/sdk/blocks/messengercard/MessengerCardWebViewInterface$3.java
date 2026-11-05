package io.intercom.android.sdk.blocks.messengercard;

import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;
import java.util.Collections;
import java.util.Map;

class MessengerCardWebViewInterface$3 implements Runnable {
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final MessengerCardWebViewInterface this$0;

   MessengerCardWebViewInterface$3(MessengerCardWebViewInterface var1, Map var2) {
      this.this$0 = var1;
      this.val$payload = var2;
   }

   @Override
   public void run() {
      Double var1 = (Double)this.val$payload.get("messengerAppId");
      if (MessengerCardWebViewInterface.access$300().equals(var1)) {
         IntercomRootActivityLauncher.INSTANCE
            .startHelpCenterCollections(
               MessengerCardWebViewInterface.access$400(this.this$0).getContext(), Collections.emptyList(), "legacy_article_search_app"
            );
      } else {
         MessengerCardWebViewInterface.access$500(this.this$0, this.val$payload, MessengerCardWebViewInterface.access$000(this.this$0));
      }
   }
}
