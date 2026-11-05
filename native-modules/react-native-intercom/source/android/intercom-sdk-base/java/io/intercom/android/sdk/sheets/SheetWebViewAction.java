package io.intercom.android.sdk.sheets;

import java.util.Map;

public class SheetWebViewAction {
   private final String type;
   private final Map payload;
   private final Map metaData;

   public SheetWebViewAction(String var1, Map var2, Map var3) {
      this.type = var1;
      this.payload = var2;
      this.metaData = var3;
   }

   public String getType() {
      return this.type;
   }

   public Map getPayload() {
      return this.payload;
   }

   public Map getMetaData() {
      return this.metaData;
   }
}
