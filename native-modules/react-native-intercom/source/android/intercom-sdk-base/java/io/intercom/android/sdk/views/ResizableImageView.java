package io.intercom.android.sdk.views;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import io.intercom.android.sdk.utilities.ImageUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

public class ResizableImageView extends AppCompatImageView {
   private int totalViewPadding;
   private int imageWidth;
   private int imageHeight;

   public ResizableImageView(Context var1) {
      this(var1, null);
   }

   public ResizableImageView(Context var1, AttributeSet var2) {
      this(var1, var2, 0);
   }

   public ResizableImageView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   public void setDisplayImageDimensions(int var1, int var2) {
      this.imageHeight = var2;
      this.imageWidth = var1;
   }

   public void setTotalViewPadding(int var1) {
      this.totalViewPadding = var1;
   }

   public Point getImageDimens() {
      double var1 = ImageUtils.getAspectRatio(this.imageWidth, this.imageHeight);
      int var3 = this.calculateContainerWidth();
      int var4 = var3 - this.totalViewPadding;
      int var5 = Math.min(this.imageWidth, var4);
      int var6 = ImageUtils.getAspectHeight(var5, var1);
      return new Point(var5, var6);
   }

   private int calculateContainerWidth() {
      Point var1 = ScreenUtils.getScreenDimensions(this.getContext());
      return var1.x;
   }

   @Override
   protected void onMeasure(int var1, int var2) {
      Point var3 = this.getImageDimens();
      if (this.imageWidth > 0 && this.imageHeight > 0) {
         this.setMeasuredDimension(var3.x, var3.y);
      } else {
         super.onMeasure(var1, var2);
      }
   }
}
