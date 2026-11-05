package io.intercom.android.sdk.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

public class AvatarInitialsDrawable extends Drawable {
   private final Rect textBounds = new Rect();
   private final Paint avatarBackground;
   private final Paint textPaint;
   private final String text;

   public AvatarInitialsDrawable(String var1, @ColorInt int var2) {
      this.text = var1;
      this.avatarBackground = new Paint();
      this.avatarBackground.setAntiAlias(true);
      this.avatarBackground.setColor(var2);
      this.avatarBackground.setStyle(Style.FILL);
      this.textPaint = new Paint();
      this.textPaint.setAntiAlias(true);
      this.textPaint.setColor(-1);
      this.textPaint.setFakeBoldText(true);
      this.textPaint.setTextAlign(Align.LEFT);
   }

   @Override
   public void draw(Canvas var1) {
      Rect var2 = this.getBounds();
      this.textPaint.setTextSize(var2.height() / 3);
      this.textPaint.getTextBounds(this.text, 0, this.text.length(), this.textBounds);
      var1.drawCircle(var2.centerX(), var2.centerY(), var2.height() / 2, this.avatarBackground);
      var1.drawText(this.text, var2.centerX() - this.textBounds.exactCenterX(), var2.centerY() - this.textBounds.exactCenterY(), this.textPaint);
   }

   @Override
   public void setAlpha(int var1) {
      this.textPaint.setAlpha(var1);
      this.avatarBackground.setAlpha(var1);
   }

   @Override
   public void setColorFilter(@Nullable ColorFilter var1) {
   }

   @Override
   public int getOpacity() {
      return -3;
   }
}
