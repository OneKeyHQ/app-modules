package io.intercom.android.sdk.utilities.commons;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class ScreenUtils {
   public static int dpToPx(float var0, Context var1) {
      return (int)(var0 * var1.getResources().getDisplayMetrics().density);
   }

   public static Point getScreenDimensions(Context var0) {
      Point var1 = new Point();
      ((WindowManager)var0.getSystemService("window")).getDefaultDisplay().getSize(var1);
      return var1;
   }
}
