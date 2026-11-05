package io.intercom.android.sdk.utilities;

import androidx.annotation.Nullable;
import java.io.Closeable;
import java.io.File;

public class IoUtils {
   public static void closeQuietly(@Nullable Closeable var0) {
      try {
         if (var0 != null) {
            var0.close();
         }
      } catch (Exception var2) {
      }
   }

   public static void safelyDelete(File var0) {
      try {
         if (!var0.delete()) {
            var0.deleteOnExit();
         }
      } catch (Exception var2) {
      }
   }
}
