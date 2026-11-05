package io.intercom.android.sdk.models;

public class Sheet {
   private final String body;

   Sheet(Sheet$Builder var1) {
      this.body = var1.sheet_request_body == null ? "" : var1.sheet_request_body.toString();
   }

   public String getBody() {
      return this.body;
   }
}
