package cn.jpush.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ShadowViewCard extends RelativeLayout {
   private static final int DEFAULT_VALUE_SHADOW_COLOR = Color.parseColor("#1f253044");
   private static final int DEFAULT_VALUE_SHADOW_ROUND = 52;
   private static final int DEFAULT_VALUE_SHADOW_RADIUS = 11;
   private static final int DEFAULT_VALUE_SHADOW_TOP_HEIGHT = 15;
   private static final int DEFAULT_VALUE_SHADOW_LEFT_HEIGHT = 5;
   private static final int DEFAULT_VALUE_SHADOW_RIGHT_HEIGHT = 5;
   private static final int DEFAULT_VALUE_SHADOW_BOTTOM_HEIGHT = 20;
   private static final int DEFAULT_VALUE_SHADOW_OFFSET_X = 0;
   private static final int DEFAULT_VALUE_SHADOW_OFFSET_Y = 8;
   private int shadowRound;
   private int shadowColor;
   private int shadowRadius;
   private int shadowOffsetX;
   private int shadowOffsetY;
   private int shadowTopHeight;
   private int shadowLeftHeight;
   private int shadowRightHeight;
   private int shadowBottomHeight;

   public ShadowViewCard(Context var1) {
      this(var1, null);
   }

   public ShadowViewCard(Context var1, AttributeSet var2) {
      this(var1, var2, 0);
   }

   public ShadowViewCard(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.initView(var1, var2);
   }

   private void initView(Context var1, AttributeSet var2) {
      this.shadowRound = 52;
      this.shadowColor = DEFAULT_VALUE_SHADOW_COLOR;
      this.shadowTopHeight = 15;
      this.shadowRightHeight = 5;
      this.shadowLeftHeight = 5;
      this.shadowBottomHeight = 20;
      this.shadowOffsetX = 0;
      this.shadowOffsetY = 8;
      this.shadowRadius = 11;
      this.setPadding(this.shadowLeftHeight, this.shadowTopHeight, this.shadowRightHeight, this.shadowBottomHeight);
      this.setLayerType(1, null);
   }

   public static int dp2px(Context var0, float var1) {
      float var2 = var0.getResources().getDisplayMetrics().density;
      return (int)(var1 * var2 + 0.5F);
   }

   @Override
   protected void onMeasure(int var1, int var2) {
      super.onMeasure(var1, var2);
   }

   @Override
   protected void onDraw(Canvas var1) {
      super.onDraw(var1);
   }

   @Override
   protected void dispatchDraw(Canvas var1) {
      Paint var2 = new Paint();
      var2.setColor(-1);
      var2.setStyle(Style.FILL);
      var2.setAntiAlias(true);
      float var3 = this.shadowLeftHeight;
      float var4 = this.shadowTopHeight;
      float var5 = this.getWidth() - this.shadowRightHeight;
      float var6 = this.getHeight() - this.shadowBottomHeight;
      var2.setShadowLayer(this.shadowRadius, this.shadowOffsetX, this.shadowOffsetY, this.shadowColor);
      RectF var7 = new RectF(var3, var4, var5, var6);
      var1.drawRoundRect(var7, this.shadowRound, this.shadowRound, var2);
      var1.save();
      super.dispatchDraw(var1);
   }
}
