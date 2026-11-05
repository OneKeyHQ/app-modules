package io.intercom.android.sdk.helpcenter.webview;

import java.util.Map;

public class HelpCenterWebViewAction {
   private final String type;
   private final Map value;

   public HelpCenterWebViewAction(String var1, Map var2) {
      this.type = var1;
      this.value = var2;
   }

   public String getType() {
      return this.type;
   }

   public Map getValue() {
      return this.value;
   }
}
