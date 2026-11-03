package cn.jpush.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
   private float width;
   private float height;
   private int defaultRadius = 45;
   private int radius;
   private int leftTopRadius;
   private int rightTopRadius;
   private int rightBottomRadius;
   private int leftBottomRadius;

   public RoundedImageView(Context var1) {
      this(var1, null);
      this.init(var1, null);
   }

   public RoundedImageView(Context var1, AttributeSet var2) {
      this(var1, var2, 0);
      this.init(var1, var2);
   }

   public RoundedImageView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.init(var1, var2);
   }

   private void init(Context var1, AttributeSet var2) {
      if (VERSION.SDK_INT < 18) {
         this.setLayerType(1, null);
      }

      this.leftTopRadius = this.defaultRadius;
      this.rightTopRadius = this.defaultRadius;
      this.rightBottomRadius = this.defaultRadius;
      this.leftBottomRadius = this.defaultRadius;
   }

   public void setRadius(int var1, int var2, int var3, int var4) {
      this.leftTopRadius = var1;
      this.rightTopRadius = var2;
      this.rightBottomRadius = var3;
      this.leftBottomRadius = var4;
      this.invalidate();
   }

   @Override
   protected void onLayout(boolean var1, int var2, int var3, int var4, int var5) {
      super.onLayout(var1, var2, var3, var4, var5);
      this.width = this.getWidth();
      this.height = this.getHeight();
   }

   @Override
   protected void onDraw(Canvas var1) {
      int var2 = Math.max(this.leftTopRadius, this.leftBottomRadius);
      int var3 = Math.max(this.rightTopRadius, this.rightBottomRadius);
      int var4 = var2 + var3;
      int var5 = Math.max(this.leftTopRadius, this.rightTopRadius);
      int var6 = Math.max(this.leftBottomRadius, this.rightBottomRadius);
      int var7 = var5 + var6;
      if (this.width >= var4 && this.height > var7) {
         Path var8 = new Path();
         var8.moveTo(this.leftTopRadius, 0.0F);
         var8.lineTo(this.width - this.rightTopRadius, 0.0F);
         var8.quadTo(this.width, 0.0F, this.width, this.rightTopRadius);
         var8.lineTo(this.width, this.height - this.rightBottomRadius);
         var8.quadTo(this.width, this.height, this.width - this.rightBottomRadius, this.height);
         var8.lineTo(this.leftBottomRadius, this.height);
         var8.quadTo(0.0F, this.height, 0.0F, this.height - this.leftBottomRadius);
         var8.lineTo(0.0F, this.leftTopRadius);
         var8.quadTo(0.0F, 0.0F, this.leftTopRadius, 0.0F);
         var1.clipPath(var8);
      }

      super.onDraw(var1);
   }
}
