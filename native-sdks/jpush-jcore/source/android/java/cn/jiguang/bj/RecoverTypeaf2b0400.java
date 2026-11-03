package cn.jiguang.bj;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RecoverTypeaf2b0400 implements RejectedExecutionHandler {
   private int recoverField_a;
   private String recoverField_b;
   private ThreadPoolExecutor recoverField_c;
   private LinkedBlockingQueue recoverField_d;

   RecoverTypeaf2b0400(String var1, int var2) {
      this.recoverField_b = var1;
      if (var2 <= 0) {
         this.recoverField_a = 3;
      }

      this.recoverField_a = var2;
   }

   @Override
   public void rejectedExecution(Runnable var1, ThreadPoolExecutor var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JRejectedExecutionHandler", "poolName: " + this.recoverField_b + ", Exceeded ThreadPoolExecutor pool size");
      if (this.recoverField_c == null) {
         synchronized (this) {
            if (null == this.recoverField_c) {
               this.recoverField_d = new LinkedBlockingQueue();
               this.recoverField_c = new ThreadPoolExecutor(
                  this.recoverField_a, this.recoverField_a, 3L, TimeUnit.SECONDS, this.recoverField_d, new RecoverTypeaf2b0402(this.recoverField_b + "_rjt")
               );
               this.recoverField_c.allowCoreThreadTimeOut(true);
            }
         }
      }

      this.recoverField_c.execute(var1);
   }
}
