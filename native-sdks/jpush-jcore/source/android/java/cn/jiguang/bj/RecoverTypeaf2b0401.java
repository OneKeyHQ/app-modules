package cn.jiguang.bj;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.az.RecoverTypeaf2acbb8;
import java.lang.Thread.UncaughtExceptionHandler;

public abstract class RecoverTypeaf2b0401 implements Runnable {
   protected String recoverField_h;
   private UncaughtExceptionHandler recoverField_a;

   public abstract void recoverMethod_a_9b75();

   protected RecoverTypeaf2b0401() {
      this.recoverField_a = new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread var1, Throwable var2) {
            RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_a_77d15a4f(var1, var2);
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
               "JCoreRunnable",
               "name: "
                  + RecoverTypeaf2b0401.this.recoverField_h
                  + ", thread id:"
                  + (var1 != null ? var1.getName() : "")
                  + "-"
                  + (var1 != null ? var1.getId() : "")
                  + "\n e:"
                  + var2
            );
         }
      };
   }

   protected RecoverTypeaf2b0401(String var1) {
      this.recoverField_h = var1;
      this.recoverField_a = new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread var1, Throwable var2) {
            RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_a_77d15a4f(var1, var2);
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
               "JCoreRunnable",
               "name: "
                  + RecoverTypeaf2b0401.this.recoverField_h
                  + ", thread id: "
                  + (var1 != null ? var1.getName() : "")
                  + "-"
                  + (var1 != null ? var1.getId() : "")
                  + "\n e:"
                  + var2
            );
         }
      };
   }

   @Override
   public void run() {
      Thread.currentThread().setUncaughtExceptionHandler(this.recoverField_a);
      this.recoverMethod_a_9b75();
      Thread.currentThread().setUncaughtExceptionHandler(null);
   }
}
