package io.intercom.android.sdk.models;

import com.google.gson.JsonObject;

public final class Sheet$Builder {
   String sheet_title;
   JsonObject sheet_request_body;

   public Sheet build() {
      return new Sheet(this);
   }
}
