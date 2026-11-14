package io.intercom.android.sdk.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

class BackButtonCountDrawable extends Drawable {
   private static final String COUNT_BACKGROUND_COLOR = "#FE536C";
   private static final int ICON_PADDING_DP = 16;
   private static final int COUNT_BACKGROUND_RADIUS_DP = 8;
   private static final int COUNT_TEXT_SIZE_DP = 11;
   private static final int COUNT_TEXT_PADDING_TOP = 4;
   private static final int ICON_SIZE_DP = 24;
   private final Drawable backIcon;
   private final int iconPadding;
   private final int iconSize;
   private final Paint countBackgroundPaint;
   private final int countBackgroundRadius;
   private final int textTopPadding;
   private final Paint textPaint;
   @Nullable
   private String text;

   BackButtonCountDrawable(Context var1, @Nullable String var2) {
      this.text = var2;
      this.backIcon = ContextCompat.getDrawable(var1, drawable.intercom_ic_back);
      this.backIcon.setLayoutDirection(var1.getResources().getConfiguration().getLayoutDirection());
      this.iconPadding = ScreenUtils.dpToPx(16.0F, var1);
      this.iconSize = ScreenUtils.dpToPx(24.0F, var1);
      this.countBackgroundPaint = new Paint();
      this.countBackgroundPaint.setAntiAlias(true);
      this.countBackgroundPaint.setColor(Color.parseColor("#FE536C"));
      this.countBackgroundPaint.setStyle(Style.FILL);
      this.countBackgroundRadius = ScreenUtils.dpToPx(8.0F, var1);
      this.textPaint = new Paint();
      this.textPaint.setAntiAlias(true);
      this.textPaint.setColor(-1);
      this.textPaint.setTextSize(ScreenUtils.dpToPx(11.0F, var1));
      this.textPaint.setFakeBoldText(true);
      this.textPaint.setStyle(Style.FILL);
      this.textPaint.setTextAlign(Align.CENTER);
      this.textTopPadding = ScreenUtils.dpToPx(4.0F, var1);
   }

   @Override
   public void draw(Canvas var1) {
      Rect var2 = this.getBounds();
      int var3 = var2.left + this.iconPadding;
      int var4 = var2.top + this.iconPadding;
      int var5 = var3 + this.iconSize;
      int var6 = var4 + this.iconSize;
      this.backIcon.setBounds(var3, var4, var5, var6);
      this.backIcon.draw(var1);
      if (!TextUtils.isEmpty(this.text)) {
         var1.drawCircle(var5, var4, this.countBackgroundRadius, this.countBackgroundPaint);
         var1.drawText(this.text, var5, var4 + this.textTopPadding, this.textPaint);
      }
   }

   @Override
   public void setAlpha(int var1) {
      this.textPaint.setAlpha(var1);
      this.countBackgroundPaint.setAlpha(var1);
      this.backIcon.setAlpha(var1);
   }

   @Override
   public void setColorFilter(@Nullable ColorFilter var1) {
      this.backIcon.setColorFilter(var1);
   }

   @Override
   public int getOpacity() {
      return -3;
   }

   public void setText(@Nullable String var1) {
      this.text = var1;
      this.invalidateSelf();
   }
}
