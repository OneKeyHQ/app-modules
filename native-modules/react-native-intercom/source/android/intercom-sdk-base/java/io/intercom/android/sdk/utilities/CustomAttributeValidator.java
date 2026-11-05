package io.intercom.android.sdk.utilities;

import androidx.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CustomAttributeValidator {
   private static final List ACCEPTED_CUSTOM_ATTRIBUTE_TYPES = Arrays.asList(
      String.class, Character.class, Short.class, Long.class, Float.class, Double.class, Integer.class, Byte.class, Boolean.class
   );

   public static boolean isValid(@Nullable Object var0) {
      return var0 == null || ACCEPTED_CUSTOM_ATTRIBUTE_TYPES.contains(var0.getClass());
   }

   public static String getAcceptedTypes() {
      StringBuilder var0 = new StringBuilder();

      for (java.lang.Object recoveredEntryElement_0_19 : ACCEPTED_CUSTOM_ATTRIBUTE_TYPES) {
         Class var2 = (Class) recoveredEntryElement_0_19;
         if (var0.length() != 0) {
            var0.append(", ");
         }

         var0.append(var2.getSimpleName());
      }

      return var0.toString();
   }
}
