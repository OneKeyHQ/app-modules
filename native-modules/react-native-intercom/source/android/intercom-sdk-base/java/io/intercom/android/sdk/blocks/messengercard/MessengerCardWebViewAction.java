package io.intercom.android.sdk.blocks.messengercard;

import java.util.Map;

public class MessengerCardWebViewAction {
   private final String type;
   private final Map payload;

   public MessengerCardWebViewAction(String var1, Map var2) {
      this.type = var1;
      this.payload = var2;
   }

   public String getType() {
      return this.type;
   }

   public Map getPayload() {
      return this.payload;
   }
}
