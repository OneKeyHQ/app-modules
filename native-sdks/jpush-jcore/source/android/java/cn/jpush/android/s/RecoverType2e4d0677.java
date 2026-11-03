package cn.jpush.android.s;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.u.RecoverType2e4d0df9;

public class RecoverType2e4d0677 implements OnTouchListener {
   private int recoverField_a;
   private int recoverField_b;
   private int recoverField_c;
   private long recoverField_d;
   private View recoverField_e;
   private RecoverType2e4d0677.Membercf254974 recoverField_f;
   private int recoverField_g = 1;
   private int recoverField_h = 1;
   private float recoverField_i;
   private float recoverField_j;
   private boolean recoverField_k;
   private int recoverField_l;
   private Object recoverField_m;
   private VelocityTracker recoverField_n;
   private float recoverField_o;
   private float recoverField_p;
   private int recoverField_q;

   public RecoverType2e4d0677(RecoverType2e4d0df9 var1, Object var2, int var3, RecoverType2e4d0677.Membercf254974 var4) {
      View var5 = var1.recoverMethod_e_d08d2077();
      ViewConfiguration var6 = ViewConfiguration.get(var5.getContext());
      this.recoverField_a = var6.getScaledTouchSlop();
      this.recoverField_b = var6.getScaledMinimumFlingVelocity() * 16;
      this.recoverField_c = var6.getScaledMaximumFlingVelocity();
      this.recoverField_d = 500L;
      this.recoverField_e = var5;
      this.recoverField_q = var3;
      this.recoverField_m = var2;
      this.recoverField_f = var4;
   }

   @Override
   public boolean onTouch(View var1, MotionEvent var2) {
      try {
         var2.offsetLocation(0.0F, this.recoverField_p);
         if (this.recoverField_h < 2) {
            this.recoverField_h = this.recoverField_e.getHeight();
         }

         switch (var2.getActionMasked()) {
            case 0:
               this.recoverField_i = var2.getRawX();
               this.recoverField_j = var2.getRawY();
               if (null != this.recoverField_f && this.recoverField_f.recoverMethod_a_5d527815(this.recoverField_m)) {
                  this.recoverField_n = VelocityTracker.obtain();
                  this.recoverField_n.addMovement(var2);
               }

               return false;
            case 1:
               if (this.recoverField_n != null) {
                  float var9 = var2.getRawY() - this.recoverField_i;
                  this.recoverField_n.addMovement(var2);
                  this.recoverField_n.computeCurrentVelocity(1000);
                  boolean var3 = false;
                  boolean var4 = false;
                  if (Math.abs(var9) > this.recoverField_h / 2 && this.recoverField_k) {
                     var3 = true;
                     var4 = var9 < 0.0F;
                  }

                  if (var3) {
                     this.recoverMethod_a_1385ff(var4);
                  } else if (this.recoverField_k) {
                     this.recoverMethod_b_9b75();
                  }

                  if (this.recoverField_n != null) {
                     this.recoverField_n.recycle();
                  }

                  this.recoverField_n = null;
                  this.recoverField_o = 0.0F;
                  this.recoverField_p = 0.0F;
                  this.recoverField_i = 0.0F;
                  this.recoverField_j = 0.0F;
                  this.recoverField_k = false;
               }
               break;
            case 2:
               if (this.recoverField_n != null) {
                  this.recoverField_n.addMovement(var2);
                  float var6 = var2.getRawX() - this.recoverField_i;
                  float var5 = var2.getRawY() - this.recoverField_j;
                  if (Math.abs(var5) > this.recoverField_a && Math.abs(var6) < Math.abs(var5) / 2.0F) {
                     if (this.recoverField_q == 0 ? var5 > 0.0F : this.recoverField_q == 1 && var5 < 0.0F) {
                        return false;
                     }

                     this.recoverField_k = true;
                     this.recoverField_l = var5 > 0.0F ? this.recoverField_a : -this.recoverField_a;
                     this.recoverField_e.getParent().requestDisallowInterceptTouchEvent(true);
                     MotionEvent var7 = MotionEvent.obtain(var2);
                     var7.setAction(3 | var2.getActionIndex() << 8);
                     this.recoverField_e.onTouchEvent(var7);
                     var7.recycle();
                  }

                  if (this.recoverField_k) {
                     this.recoverField_p = var5;
                     this.recoverMethod_a_133aeb(var5 - this.recoverField_l);
                     this.recoverMethod_b_133aeb(Math.max(0.0F, Math.min(1.0F, 1.0F - 2.0F * Math.abs(var5) / this.recoverField_h)));
                     return true;
                  }
               }
               break;
            case 3:
               if (this.recoverField_n != null) {
                  this.recoverField_n.recycle();
                  this.recoverField_n = null;
                  this.recoverField_o = 0.0F;
                  this.recoverField_p = 0.0F;
                  this.recoverField_i = 0.0F;
                  this.recoverField_j = 0.0F;
                  this.recoverField_k = false;
               }
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("InAppSWTouchListener", "onTouch error." + var8.getMessage());
      }

      return false;
   }

   protected void recoverMethod_a_133aeb(float var1) {
      this.recoverField_e.setTranslationY(var1);
   }

   protected float recoverMethod_a_9b65() {
      return this.recoverField_e.getTranslationY();
   }

   protected void recoverMethod_b_133aeb(float var1) {
      this.recoverField_e.setAlpha(var1);
   }

   protected void recoverMethod_a_1385ff(boolean var1) {
      float var2 = var1 ? -this.recoverField_h : this.recoverField_h;
      Logger.recoverMethod_d_e925fa09("InAppSWTouchListener", "[startDismissAnimationY] dismissTop: " + var1 + ", viewHeight: " + this.recoverField_h);
      this.recoverMethod_a_3be735c3(var2, 0.0F, new AnimatorListenerAdapter() {
         @Override
         public void onAnimationEnd(Animator var1) {
            RecoverType2e4d0677.this.recoverMethod_c_9b75();
         }
      });
   }

   protected void recoverMethod_b_9b75() {
      this.recoverMethod_a_3be735c3(0.0F, 1.0F, null);
   }

   private void recoverMethod_a_3be735c3(float var1, float var2, AnimatorListenerAdapter var3) {
      try {
         final float var4 = this.recoverMethod_a_9b65();
         final float var5 = var1 - var4;
         final float var6 = this.recoverField_e.getAlpha();
         final float var7 = var2 - var6;
         Logger.recoverMethod_i_e925fa09(
            "InAppSWTouchListener", "animateTo, beginTranslation: " + var4 + ", translationDiff: " + var5 + ", beginAlpha: , alphaDiff: " + var7
         );
         ValueAnimator var8 = ValueAnimator.ofFloat(0.0F, 1.0F);
         if (null != var8) {
            var8.setDuration(this.recoverField_d);
            var8.addUpdateListener(new AnimatorUpdateListener() {
               @Override
               public void onAnimationUpdate(ValueAnimator var1) {
                  float var2x = var1.getAnimatedFraction();
                  float var3x = var4 + var2x * var5;
                  float var4x = var6 + var1.getAnimatedFraction() * var7;
                  RecoverType2e4d0677.this.recoverMethod_a_133aeb(var3x);
                  RecoverType2e4d0677.this.recoverMethod_b_133aeb(var4x);
               }
            });
            if (var3 != null) {
               var8.addListener(var3);
            }

            var8.start();
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_w_e925fa09("InAppSWTouchListener", "[animateTo] failed" + var9.getMessage());
      }
   }

   private void recoverMethod_c_9b75() {
      try {
         if (null != this.recoverField_e) {
            final LayoutParams var1 = this.recoverField_e.getLayoutParams();
            int var2 = this.recoverField_e.getHeight();
            final int var3 = this.recoverField_e.getWidth();
            ValueAnimator var4 = ValueAnimator.ofInt(var3, 1).setDuration(this.recoverField_d);
            var4.addListener(
               new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator var1x) {
                     Logger.recoverMethod_d_e925fa09("InAppSWTouchListener", "[onAnimationEnd] ...");
                     if (null != RecoverType2e4d0677.this.recoverField_f) {
                        RecoverType2e4d0677.this.recoverField_f
                           .recoverMethod_a_171eb1d9(RecoverType2e4d0677.this.recoverField_e, RecoverType2e4d0677.this.recoverField_m);
                     }

                     RecoverType2e4d0677.this.recoverField_e.setAlpha(1.0F);
                     RecoverType2e4d0677.this.recoverField_e.setTranslationY(0.0F);
                     var1.width = var3;
                     RecoverType2e4d0677.this.recoverField_e.setLayoutParams(var1);
                  }
               }
            );
            var4.addUpdateListener(new AnimatorUpdateListener() {
               @Override
               public void onAnimationUpdate(ValueAnimator var1x) {
                  var1.width = (Integer)var1x.getAnimatedValue();
                  RecoverType2e4d0677.this.recoverField_e.setLayoutParams(var1);
               }
            });
            var4.start();
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("InAppSWTouchListener", "[performDismiss] failed." + var5.getMessage());
      }
   }

   public interface Membercf254974 {
      boolean recoverMethod_a_5d527815(Object var1);

      void recoverMethod_a_171eb1d9(View var1, Object var2);
   }
}
