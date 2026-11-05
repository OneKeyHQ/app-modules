package io.intercom.android.sdk.utilities;

import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.ColorInt;

public class BackgroundUtils {
   public static void setBackground(View var0, Drawable var1) {
      var0.setBackground(var1);
   }

   public static void setButtonColor(TextView var0, @ColorInt int var1) {
      int var2 = ColorUtils.buttonTextColorVariant(var1);
      int var3 = ColorUtils.buttonBackgroundColorVariant(var1);
      var0.setTextColor(var2);
      var0.getBackground().setColorFilter(var3, Mode.SRC_ATOP);
   }
}
