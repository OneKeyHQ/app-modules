package io.intercom.android.sdk.views;

import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;

public class UploadProgressBar extends View {
   private static final int MAX = 100;
   private static final int STROKE_WIDTH = 8;
   private static final int START_ANGLE = -90;
   private final RectF rectF;
   private final Paint backgroundPaint;
   private final Paint foregroundPaint;
   private final Twig twig = LumberMill.getLogger();
   private int progress = 0;

   public UploadProgressBar(Context var1) {
      this(var1, null);
   }

   public UploadProgressBar(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.rectF = new RectF();
      this.backgroundPaint = new Paint(1);
      this.backgroundPaint.setColor(-2013265920);
      this.backgroundPaint.setStyle(Style.FILL_AND_STROKE);
      this.backgroundPaint.setStrokeWidth(8.0F);
      this.foregroundPaint = new Paint(1);
      this.foregroundPaint.setColor(-1);
      this.foregroundPaint.setStyle(Style.STROKE);
      this.foregroundPaint.setStrokeWidth(8.0F);
   }

   public void setProgress(int var1) {
      this.twig.internal("animation", "received progress of  " + var1);
      if (var1 > this.progress) {
         this.progress = var1;
         this.invalidate();
      }
   }

   private void showBar() {
      ObjectAnimator var1 = ObjectAnimator.ofFloat(this, "alpha", 0.0F, 1.0F).setDuration(300L);
      var1.setInterpolator(new DecelerateInterpolator());
      var1.start();
   }

   public void hideBar() {
      ObjectAnimator var1 = ObjectAnimator.ofFloat(this, "alpha", 1.0F, 0.0F).setDuration(300L);
      var1.setInterpolator(new DecelerateInterpolator());
      var1.start();
   }

   @Override
   protected void onDraw(Canvas var1) {
      super.onDraw(var1);
      float var2 = 360 * this.progress / 100;
      var1.drawOval(this.rectF, this.backgroundPaint);
      var1.drawArc(this.rectF, -90.0F, var2, false, this.foregroundPaint);
   }

   @Override
   protected void onMeasure(int var1, int var2) {
      int var3 = getDefaultSize(this.getSuggestedMinimumHeight(), var2);
      int var4 = getDefaultSize(this.getSuggestedMinimumWidth(), var1);
      int var5 = Math.min(var4, var3);
      this.setMeasuredDimension(var5, var5);
      this.rectF.set(4.0F, 4.0F, var5 - 4, var5 - 4);
   }

   public void smoothStartAnimation() {
      if (this.getAlpha() < 1.0F) {
         this.showBar();
      }

      this.twig.internal("animation", "starting upload start animation");
      ObjectAnimator var1 = ObjectAnimator.ofInt(this, "progress", 0, 10);
      var1.setDuration(1000L);
      var1.start();
   }

   public void smoothEndAnimation() {
      ObjectAnimator var1 = this.getObjectAnimator();
      var1.addListener(new UploadProgressBar$1(this));
      var1.start();
   }

   public void smoothEndAnimation(AnimatorListener var1) {
      ObjectAnimator var2 = this.getObjectAnimator();
      var2.addListener(var1);
      var2.start();
   }

   private ObjectAnimator getObjectAnimator() {
      this.twig.internal("animation", "starting upload end animation");
      return ObjectAnimator.ofInt(this, "progress", 90, 99).setDuration(1000L);
   }
}
