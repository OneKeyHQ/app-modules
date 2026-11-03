package cn.jiguang.bj;

import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RecoverTypeaf2b0402 implements ThreadFactory {
   private final ThreadGroup recoverField_a;
   private final AtomicInteger recoverField_b = new AtomicInteger(1);
   private final String recoverField_c;

   public RecoverTypeaf2b0402(String var1) {
      SecurityManager var2 = System.getSecurityManager();
      this.recoverField_a = var2 != null ? var2.getThreadGroup() : Thread.currentThread().getThreadGroup();
      String var3 = TextUtils.isEmpty(var1) ? "default" : var1.toLowerCase();
      this.recoverField_c = "jg_" + var3 + "_pool_thread";
   }

   @Override
   public Thread newThread(Runnable var1) {
      Thread var2 = null;

      try {
         String var3 = this.recoverField_c + this.recoverField_b.getAndIncrement();
         var2 = new Thread(this.recoverField_a, var1, var3, 0L);
         if (var2.isDaemon()) {
            var2.setDaemon(false);
         }

         if (var2.getPriority() != 5) {
            var2.setPriority(5);
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JThreadFactory", "JThreadFactory new Thread error, " + var6.getMessage());

         try {
            String var4 = this.recoverField_c + this.recoverField_b.getAndIncrement();
            var2 = new Thread(this.recoverField_a, var1, var4, 0L);
            if (var2.isDaemon()) {
               var2.setDaemon(false);
            }

            if (var2.getPriority() != 5) {
               var2.setPriority(5);
            }
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JThreadFactory", "JThreadFactory new Thread again error, " + var5.getMessage());
         }
      }

      return var2;
   }
}
