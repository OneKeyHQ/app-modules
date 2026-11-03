package cn.jiguang.az;

import android.content.Context;
import android.os.Debug;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ba.RecoverTypeaf2ae237;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

final class RecoverTypeaf2acbb1 extends Thread {
   private final boolean recoverField_a;
   private final RecoverTypeaf2acbb1.Member8fa6a82e recoverField_b;
   private final RecoverTypeaf2acbb5 recoverField_c;
   private final long recoverField_d;
   private final AtomicLong recoverField_e = new AtomicLong(0L);
   private final AtomicBoolean recoverField_f = new AtomicBoolean(false);
   private final Context recoverField_g;
   private final RecoverTypeaf2b0401 recoverField_h = new RecoverTypeaf2b0401() {
      @Override
      public void recoverMethod_a_9b75() {
         RecoverTypeaf2acbb1.this.recoverField_e.set(0L);
         RecoverTypeaf2acbb1.this.recoverField_f.set(false);
      }
   };

   RecoverTypeaf2acbb1(long var1, boolean var3, RecoverTypeaf2acbb1.Member8fa6a82e var4, Context var5) {
      this(var1, var3, var4, new RecoverTypeaf2acbb6(), var5);
   }

   RecoverTypeaf2acbb1(long var1, boolean var3, RecoverTypeaf2acbb1.Member8fa6a82e var4, RecoverTypeaf2acbb5 var5, Context var6) {
      this.recoverField_a = var3;
      this.recoverField_b = var4;
      this.recoverField_d = var1;
      this.recoverField_c = var5;
      this.recoverField_g = var6;
   }

   @Override
   public void run() {
      this.setName("jg_anr_watchdog");
      long var1 = this.recoverField_d;

      while (!this.isInterrupted()) {
         if (!RecoverTypeaf2acbb9.recoverMethod_a_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ANRWatchDog", "isSentrySample1 false");
            return;
         }

         boolean var3 = this.recoverField_e.get() == 0L;
         this.recoverField_e.addAndGet(var1);
         if (var3) {
            this.recoverField_c.recoverMethod_a_a4fc3973(this.recoverField_h);
         }

         try {
            Thread.sleep(var1);
         } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ANRWatchDog", String.format("Interrupted: %s", var6.getMessage()));
            return;
         }

         if (this.recoverField_e.get() != 0L && !this.recoverField_f.get()) {
            if (this.recoverField_a || !Debug.isDebuggerConnected() && !Debug.waitingForDebugger()) {
               if (!RecoverTypeaf2acbb9.recoverMethod_a_9b79()) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ANRWatchDog", "isSentrySample2 false");
                  return;
               }

               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ANRWatchDog", "Raising ANR");
               String var4 = "Application Not Responding for at least " + this.recoverField_d + " ms.";
               RecoverTypeaf2ae237 var5 = new RecoverTypeaf2ae237(var4, this.recoverField_c.recoverMethod_a_943922f2());
               this.recoverField_b.recoverMethod_a_af4a4555(var5);
               var1 = this.recoverField_d;
               this.recoverField_f.set(true);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ANRWatchDog", "An ANR was detected but ignored because the debugger is connected.");
               this.recoverField_f.set(true);
            }
         }
      }
   }

   public interface Member8fa6a82e {
      void recoverMethod_a_af4a4555(RecoverTypeaf2ae237 var1);
   }
}
