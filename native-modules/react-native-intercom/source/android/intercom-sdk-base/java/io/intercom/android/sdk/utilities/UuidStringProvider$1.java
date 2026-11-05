package io.intercom.android.sdk.utilities;

import java.util.UUID;

class UuidStringProvider$1 implements UuidStringProvider {
   @Override
   public String newUuidString() {
      return UUID.randomUUID().toString();
   }
}
