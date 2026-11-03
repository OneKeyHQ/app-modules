package cn.jiguang.az;

import cn.jiguang.ba.RecoverTypeaf2ae23d;
import cn.jiguang.ba.RecoverTypeaf2ae23e;
import cn.jiguang.bf.RecoverTypeaf2af4fc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class RecoverTypeaf2acbbc {
   private final RecoverTypeaf2acbbb recoverField_a;

   public RecoverTypeaf2acbbc(RecoverTypeaf2acbbb var1) {
      this.recoverField_a = var1;
   }

   List recoverMethod_a_3038db21(List var1) {
      return this.recoverMethod_a_66c06fc6(Thread.getAllStackTraces(), var1);
   }

   List recoverMethod_a_66c06fc6(Map var1, List var2) {
      ArrayList var3 = null;
      Thread var4 = Thread.currentThread();
      if (!var1.isEmpty()) {
         var3 = new ArrayList();
         if (!var1.containsKey(var4)) {
            var1.put(var4, var4.getStackTrace());
         }

         for (Object recoveryElement_31 : var1.entrySet()) {
            Entry var6 = (Entry)recoveryElement_31;
            Thread var7 = (Thread)var6.getKey();
            boolean var8 = var7 == var4 || var2 != null && var2.contains(var7.getId());
            var3.add(this.recoverMethod_a_c6a56764(var8, (StackTraceElement[])var6.getValue(), (Thread)var6.getKey()));
         }
      }

      return var3;
   }

   private RecoverTypeaf2ae23e recoverMethod_a_c6a56764(boolean var1, StackTraceElement[] var2, Thread var3) {
      RecoverTypeaf2ae23e var4 = new RecoverTypeaf2ae23e();
      var4.recoverMethod_a_aad3b1ff(var3.getName());
      var4.recoverMethod_a_9b5e18e2(var3.getPriority());
      var4.recoverMethod_a_f9cd49b4(var3.getId());
      var4.recoverMethod_c_e8e24db8(var3.isDaemon());
      var4.recoverMethod_b_aad3b1ff(var3.getState().name());
      var4.recoverMethod_a_e8e24db8(var1);
      var4.recoverMethod_b_e8e24db8(RecoverTypeaf2af4fc.recoverMethod_a_58fe156a(var3));
      List var5 = this.recoverField_a.recoverMethod_a_55c69211(var2);
      RecoverTypeaf2ae23d var6 = new RecoverTypeaf2ae23d(var5);
      var6.recoverMethod_a_e8e24db8(true);
      var4.recoverMethod_a_af4cff8f(var6);
      return var4;
   }
}
