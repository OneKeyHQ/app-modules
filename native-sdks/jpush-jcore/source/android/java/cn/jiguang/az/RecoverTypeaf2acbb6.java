package cn.jiguang.az;

import android.os.Handler;
import android.os.Looper;

final class RecoverTypeaf2acbb6 implements RecoverTypeaf2acbb5 {
   private final Handler recoverField_a;

   RecoverTypeaf2acbb6() {
      this(Looper.getMainLooper());
   }

   RecoverTypeaf2acbb6(Looper var1) {
      this.recoverField_a = new Handler(var1);
   }

   @Override
   public void recoverMethod_a_a4fc3973(Runnable var1) {
      this.recoverField_a.post(var1);
   }

   @Override
   public Thread recoverMethod_a_943922f2() {
      return this.recoverField_a.getLooper().getThread();
   }
}
