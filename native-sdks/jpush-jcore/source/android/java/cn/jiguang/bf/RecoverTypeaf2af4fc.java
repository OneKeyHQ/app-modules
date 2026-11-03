package cn.jiguang.bf;

import android.os.Looper;

public final class RecoverTypeaf2af4fc {
   public static boolean recoverMethod_a_58fe156a(Thread var0) {
      return recoverMethod_a_1349f3(var0.getId());
   }

   private static boolean recoverMethod_a_1349f3(long var0) {
      return Looper.getMainLooper().getThread().getId() == var0;
   }
}
