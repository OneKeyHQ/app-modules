package cn.jpush.android.r;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.view.View;
import cn.jpush.android.helper.Logger;

public class RecoverType2e4d02b5 {
   public static void recoverMethod_a_30959f21(View var0, Point var1, long var2, final RecoverType2e4d02b5.Membercf172e32 var4) {
      if (var0 == null) {
         Logger.recoverMethod_w_e925fa09("InAppAnimator", "[slideOutOfView] view is null");
      } else {
         var0.animate().translationX(var1.x).translationY(var1.y).setDuration(var2).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator var1) {
               super.onAnimationEnd(var1);

               try {
                  if (null != var4) {
                     var4.recoverMethod_a_9b75();
                  }
               } catch (Throwable var3) {
               }
            }
         });
      }
   }

   public static void recoverMethod_a_a0c706de(final View var0, Point var1, final Point var2, final long var3, final RecoverType2e4d02b5.Membercf172e32 var5) {
      if (null == var0) {
         Logger.recoverMethod_w_e925fa09("InAppAnimator", "[slideIntoView] view is null");
      } else {
         var0.setAlpha(0.0F);
         var0.animate().translationX(var1.x).translationY(var1.y).setDuration(10L).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator var1) {
               super.onAnimationEnd(var1);

               try {
                  var0.animate().translationX(var2.x).translationY(var2.y).alpha(1.0F).setDuration(var3).setListener(null);
                  if (null != var5) {
                     var5.recoverMethod_a_9b75();
                  }
               } catch (Throwable var3x) {
               }
            }
         });
      }
   }

   public interface Membercf172e32 {
      void recoverMethod_a_9b75();
   }
}
