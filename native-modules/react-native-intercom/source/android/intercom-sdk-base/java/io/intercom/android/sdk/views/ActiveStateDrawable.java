package io.intercom.android.sdk.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

public class ActiveStateDrawable extends Drawable {
   private final RectF roundRect = new RectF();
   private final Paint paint = new Paint();
   private final int stateColor;
   private final int strokeColor;
   private final float strokeWidth;

   public ActiveStateDrawable(int var1, int var2, float var3) {
      this.stateColor = var1;
      this.strokeColor = var2;
      this.strokeWidth = var3;
      this.paint.setAntiAlias(true);
   }

   @Override
   public void draw(Canvas var1) {
      this.roundRect.set(this.getBounds());
      this.roundRect.inset(this.strokeWidth, this.strokeWidth);
      this.paint.setColor(this.stateColor);
      this.paint.setStyle(Style.FILL);
      var1.drawRoundRect(this.roundRect, var1.getHeight() / 2, var1.getHeight() / 2, this.paint);
      this.paint.setColor(this.strokeColor);
      this.paint.setStyle(Style.STROKE);
      this.paint.setStrokeWidth(this.strokeWidth);
      var1.drawRoundRect(this.roundRect, var1.getHeight() / 2, var1.getHeight() / 2, this.paint);
   }

   @Override
   public void setAlpha(int var1) {
      this.paint.setAlpha(var1);
      this.invalidateSelf();
   }

   @Override
   public void setColorFilter(@Nullable ColorFilter var1) {
      this.paint.setColorFilter(var1);
      this.invalidateSelf();
   }

   @Override
   public int getOpacity() {
      return -3;
   }
}
