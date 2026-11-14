package io.intercom.android.sdk.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.drawable;

public class AvatarDefaultDrawable extends Drawable {
   private final RectF roundRect = new RectF();
   private final Drawable companyIcon;
   private final Paint avatarBackground;

   public AvatarDefaultDrawable(Context var1, @ColorInt int var2) {
      this.companyIcon = ContextCompat.getDrawable(var1, drawable.intercom_default_avatar_icon);
      this.avatarBackground = new Paint();
      this.avatarBackground.setAntiAlias(true);
      this.avatarBackground.setColor(var2);
      this.avatarBackground.setStyle(Style.FILL);
   }

   @Override
   public void draw(Canvas var1) {
      this.roundRect.set(this.getBounds());
      var1.drawRoundRect(this.roundRect, var1.getHeight() / 2, var1.getHeight() / 2, this.avatarBackground);
      int var2 = (var1.getWidth() - this.companyIcon.getIntrinsicWidth()) / 2;
      int var3 = (var1.getHeight() - this.companyIcon.getIntrinsicHeight()) / 2;
      this.companyIcon.setBounds(var2, var3, var2 + this.companyIcon.getIntrinsicWidth(), var3 + this.companyIcon.getIntrinsicHeight());
      this.companyIcon.draw(var1);
   }

   @Override
   public void setAlpha(int var1) {
   }

   @Override
   public void setColorFilter(@Nullable ColorFilter var1) {
   }

   @Override
   public int getOpacity() {
      return -3;
   }
}
