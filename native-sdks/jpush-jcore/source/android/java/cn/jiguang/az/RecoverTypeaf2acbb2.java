package cn.jiguang.az;

import android.content.Context;
import cn.jiguang.ba.RecoverTypeaf2ae237;
import cn.jiguang.ba.RecoverTypeaf2ae239;

public class RecoverTypeaf2acbb2 {
   private static RecoverTypeaf2acbb1 recoverField_a;
   private static final Object recoverField_b = new Object();

   public static void recoverMethod_a_f0554df3(Context var0) {
      synchronized (recoverField_b) {
         recoverField_a = new RecoverTypeaf2acbb1(10000L, false, new RecoverTypeaf2acbb1.Member8fa6a82e() {
            @Override
            public void recoverMethod_a_af4a4555(RecoverTypeaf2ae237 var1) {
               RecoverTypeaf2acbb2.recoverMethod_b_af4a4555(var1);
            }
         }, var0);
         recoverField_a.start();
      }
   }

   private static void recoverMethod_b_af4a4555(RecoverTypeaf2ae237 var0) {
      RecoverTypeaf2ae239 var1 = new RecoverTypeaf2ae239();
      var1.recoverMethod_a_aad3b1ff("ANR");
      RecoverTypeaf2acbb3 var2 = new RecoverTypeaf2acbb3(var1, var0, var0.recoverMethod_a_943922f2(), true);
      RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_a_879483a0(var2);
   }
}
