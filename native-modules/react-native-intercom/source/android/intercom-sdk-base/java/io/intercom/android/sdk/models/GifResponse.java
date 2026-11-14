package io.intercom.android.sdk.models;

import io.intercom.android.sdk.blocks.lib.models.Block;
import java.util.List;

public class GifResponse {
   private final List results;

   GifResponse(List var1) {
      this.results = var1;
   }

   public List results() {
      return this.results;
   }
}
