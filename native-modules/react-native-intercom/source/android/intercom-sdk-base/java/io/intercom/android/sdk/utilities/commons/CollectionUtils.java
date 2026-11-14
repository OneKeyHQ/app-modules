package io.intercom.android.sdk.utilities.commons;

import java.util.Collection;

public class CollectionUtils {
   public static int capacityFor(Collection var0) {
      return var0 == null ? 0 : var0.size();
   }
}
