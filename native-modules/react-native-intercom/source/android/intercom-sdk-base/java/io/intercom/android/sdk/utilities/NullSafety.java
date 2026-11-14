package io.intercom.android.sdk.utilities;

import androidx.annotation.Nullable;

public class NullSafety {
   public static String valueOrEmpty(@Nullable String var0) {
      return var0 == null ? "" : var0;
   }

   public static boolean valueOrDefault(@Nullable Boolean var0, boolean var1) {
      return var0 == null ? var1 : var0;
   }
}
