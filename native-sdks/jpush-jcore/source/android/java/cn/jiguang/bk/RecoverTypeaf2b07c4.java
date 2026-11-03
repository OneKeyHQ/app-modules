package cn.jiguang.bk;

import android.content.Context;
import java.util.concurrent.atomic.AtomicReference;

public class RecoverTypeaf2b07c4 {
   private static final AtomicReference recoverField_a = new AtomicReference();

   public static Context recoverMethod_a_88bf2b6d() {
      return recoverMethod_a_6263c3eb(null);
   }

   public static Context recoverMethod_a_6263c3eb(Context var0) {
      Context var1 = (Context)recoverField_a.get();
      if (var1 == null) {
         if (var0 != null) {
            var1 = var0.getApplicationContext();
         } else {
            var1 = RecoverTypeaf2b07d2.recoverMethod_a_c4d1044();
         }

         recoverField_a.set(var1);
      }

      return var1;
   }
}
