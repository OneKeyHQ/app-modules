package io.intercom.android.sdk.utilities;

public class NameUtils {
   public static String getInitial(String var0) {
      String var1 = var0.trim();
      return var1.isEmpty() ? "" : String.valueOf(var1.charAt(0));
   }
}
