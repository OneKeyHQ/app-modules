package io.intercom.android.sdk.utilities;

import android.text.TextUtils;

public class ImageUtils {
   public static double getAspectRatio(int var0, int var1) {
      if (var1 != 0 && var0 != 0) {
         double var2 = 1.0 * var1 / var0;
         if (Double.isNaN(var2)) {
            var2 = 0.0;
         }

         return var2;
      } else {
         return 1.0;
      }
   }

   public static int getAspectHeight(int var0, double var1) {
      return (int)(var0 * var1);
   }

   public static boolean isGif(String var0) {
      return !TextUtils.isEmpty(var0) && var0.endsWith(".gif");
   }
}
