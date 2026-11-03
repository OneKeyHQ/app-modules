package cn.jpush.android.s;

import android.os.CountDownTimer;

public class RecoverType2e4d0678 {
   private CountDownTimer recoverField_a;

   public void recoverMethod_a_957fb98f(final RecoverType2e4d0678.Membercf254d35 var1, long var2, long var4) {
      this.recoverField_a = (new CountDownTimer(var2, var4) {
         @Override
         public void onTick(long var1x) {
         }

         @Override
         public void onFinish() {
            if (var1 != null) {
               var1.recoverMethod_a_9b75();
            }
         }
      }).start();
   }

   public void recoverMethod_a_9b75() {
      if (this.recoverField_a != null) {
         this.recoverField_a.cancel();
         this.recoverField_a = null;
      }
   }

   public interface Membercf254d35 {
      void recoverMethod_a_9b75();
   }
}
