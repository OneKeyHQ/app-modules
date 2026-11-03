package cn.jpush.android.ad;

import android.os.SystemClock;
import java.util.concurrent.atomic.AtomicInteger;

public class RecoverType9b4c5af1 {
   private static final AtomicInteger recoverField_a = new AtomicInteger((int)SystemClock.elapsedRealtime());

   public static int recoverMethod_a_9b68() {
      return recoverField_a.getAndIncrement();
   }
}
