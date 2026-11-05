package io.intercom.android.sdk.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.StateListDrawable;

public class ButtonSelector extends StateListDrawable {
   private final int color;

   public ButtonSelector(Context var1, int var2, int var3) {
      this.color = var3;
      this.addState(new int[]{16842910}, var1.getResources().getDrawable(var2));
      this.addState(new int[]{16842908}, var1.getResources().getDrawable(var2));
      this.addState(new int[]{16842919}, var1.getResources().getDrawable(var2));
   }

   @Override
   protected boolean onStateChange(int[] var1) {
      boolean var2 = false;

      for (int var6 : var1) {
         if (var6 == 16842919 || var6 == 16842908) {
            var2 = true;
         }
      }

      if (var2) {
         this.setColorFilter(darken(this.color, 0.9), Mode.SRC);
      } else {
         this.setColorFilter(this.color, Mode.SRC);
      }

      return super.onStateChange(var1);
   }

   private static int darken(int var0, double var1) {
      int var3 = (int)(Color.red(var0) * var1);
      int var4 = (int)(Color.green(var0) * var1);
      int var5 = (int)(Color.blue(var0) * var1);
      return Color.argb(255, var3, var4, var5);
   }
}
