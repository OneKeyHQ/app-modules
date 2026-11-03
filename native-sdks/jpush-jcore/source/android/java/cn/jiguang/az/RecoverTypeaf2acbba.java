package cn.jiguang.az;

import cn.jiguang.ba.RecoverTypeaf2ae239;
import cn.jiguang.ba.RecoverTypeaf2ae23a;
import cn.jiguang.ba.RecoverTypeaf2ae23c;
import cn.jiguang.ba.RecoverTypeaf2ae23d;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

final class RecoverTypeaf2acbba {
   private final RecoverTypeaf2acbbb recoverField_a;

   public RecoverTypeaf2acbba(RecoverTypeaf2acbbb var1) {
      this.recoverField_a = var1;
   }

   List recoverMethod_a_be259aa7(Throwable var1) {
      return this.recoverMethod_a_d35a8e1b(this.recoverMethod_b_f8adb791(var1));
   }

   private List recoverMethod_a_d35a8e1b(Deque var1) {
      return new ArrayList(var1);
   }

   private RecoverTypeaf2ae23a recoverMethod_a_2c7298b2(Throwable var1, RecoverTypeaf2ae239 var2, Thread var3, boolean var4) {
      Package var5 = var1.getClass().getPackage();
      String var6 = var1.getClass().getName();
      RecoverTypeaf2ae23a var7 = new RecoverTypeaf2ae23a();
      String var8 = var1.getMessage();
      String var9 = var5 != null ? var6.replace(var5.getName() + ".", "") : var6;
      String var10 = var5 != null ? var5.getName() : null;
      List var11 = this.recoverField_a.recoverMethod_a_55c69211(var1.getStackTrace());
      if (var11 != null && !var11.isEmpty()) {
         List var12 = var11;
         if (!this.recoverMethod_a_58fe156a(var3) && !this.recoverMethod_a_61fb9e6a(var12)) {
            return null;
         }

         RecoverTypeaf2ae23d var13 = new RecoverTypeaf2ae23d(var11);
         if (var4) {
            var13.recoverMethod_a_e8e24db8(true);
         }

         var7.recoverMethod_a_af4cff8f(var13);
      }

      if (var3 != null) {
         var7.recoverMethod_a_f9cd49b4(var3.getId());
      }

      var7.recoverMethod_a_aad3b1ff(var9);
      var7.recoverMethod_a_af4b2e13(var2);
      var7.recoverMethod_c_aad3b1ff(var10);
      var7.recoverMethod_b_aad3b1ff(var8);
      return var7;
   }

   Deque recoverMethod_b_f8adb791(Throwable var1) {
      ArrayDeque var2 = new ArrayDeque();
      HashSet var3 = new HashSet();

      for (Throwable var6 = var1; var6 != null && var3.add(var6); var6 = var6.getCause()) {
         boolean var7 = false;
         RecoverTypeaf2ae239 var4;
         Thread var5;
         if (var6 instanceof RecoverTypeaf2acbb3) {
            RecoverTypeaf2acbb3 var8 = (RecoverTypeaf2acbb3)var6;
            var4 = var8.recoverMethod_a_c69bca8d();
            var6 = var8.recoverMethod_b_1b7ba3da();
            var5 = var8.recoverMethod_c_943922f2();
            var7 = var8.recoverMethod_d_9b79();
         } else {
            var4 = null;
            var5 = Thread.currentThread();
         }

         RecoverTypeaf2ae23a var9 = this.recoverMethod_a_2c7298b2(var6, var4, var5, var7);
         var2.addFirst(var9);
      }

      return var2;
   }

   boolean recoverMethod_a_61fb9e6a(List var1) {
      boolean var2 = false;

      for (Object recoveryElement_90 : var1) {
         RecoverTypeaf2ae23c var4 = (RecoverTypeaf2ae23c)recoveryElement_90;
         var2 = var2 || var4.recoverMethod_h_12a799b2();
         if (var2) {
            break;
         }
      }

      return var2;
   }

   boolean recoverMethod_a_58fe156a(Thread var1) {
      return var1.getName() != null ? var1.getName().contains("jg") : false;
   }
}
