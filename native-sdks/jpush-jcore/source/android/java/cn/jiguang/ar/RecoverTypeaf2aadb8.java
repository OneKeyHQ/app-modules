package cn.jiguang.ar;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0402;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecoverTypeaf2aadb8 {
   private final int recoverField_a;
   private final ThreadPoolExecutor recoverField_b;
   private final Handler recoverField_c;
   private final AtomicBoolean recoverField_d = new AtomicBoolean(true);
   private final RecoverTypeaf2aada9 recoverField_e;
   private volatile long recoverField_f = 0L;
   private Object recoverField_g;
   private CountDownLatch recoverField_h = new CountDownLatch(1);

   public RecoverTypeaf2aadb8(int var1, int var2, RecoverTypeaf2aada9 var3, String var4) {
      this.recoverField_a = var2;
      this.recoverField_e = var3;
      this.recoverField_b = new ThreadPoolExecutor(var1, var1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new RecoverTypeaf2b0402(var4)) {
         @Override
         protected void afterExecute(Runnable var1, Throwable var2x) {
            boolean var3x;
            try {
               var3x = this.getActiveCount() <= 1 && this.getQueue().isEmpty() && !RecoverTypeaf2aadb8.this.recoverField_c.hasMessages(233);
            } catch (Throwable var6) {
               var3x = true;
            }

            if (var3x) {
               try {
                  RecoverTypeaf2aadb8.this.recoverField_d.set(true);
                  RecoverTypeaf2aadb8.this.recoverField_h.countDown();
               } catch (Throwable var5) {
               }
            }
         }
      };
      this.recoverField_c = new RecoverTypeaf2aadb8.Member8f362475(this.recoverField_b);
   }

   private synchronized long recoverMethod_b_1349e3(long var1) {
      long var3 = this.recoverField_f + var1;
      long var5 = SystemClock.uptimeMillis();
      if (var3 < var5) {
         this.recoverField_f = var5;
         return -1L;
      } else {
         this.recoverField_f = var3;
         return var3;
      }
   }

   public void recoverMethod_a_f64309fc(Callable var1) {
      try {
         long var2 = this.recoverMethod_b_1349e3(this.recoverField_a);
         if (var2 < 0L) {
            this.recoverField_b.submit(var1);
         } else {
            Message var4 = Message.obtain();
            var4.what = 233;
            var4.obj = var1;
            this.recoverField_c.sendMessageAtTime(var4, var2);
         }

         this.recoverField_d.set(false);
      } catch (Throwable var5) {
      }
   }

   public boolean recoverMethod_a_9b79() {
      return this.recoverField_g != null;
   }

   public void recoverMethod_a_5d527811(Object var1) {
      if (!this.recoverMethod_a_9b79()) {
         if (var1 != null) {
            this.recoverField_g = var1;
            this.recoverField_h.countDown();
            if (this.recoverField_e != null) {
               this.recoverField_e.recoverMethod_a_5d527811(var1);
            }

            this.recoverMethod_a_1385ff(true);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Step", "done!");
         }
      }
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_c.removeMessages(233);
      if (!this.recoverField_b.isShutdown()) {
         if (var1) {
            this.recoverField_b.shutdownNow();
         } else {
            this.recoverField_b.shutdown();
         }
      }

      try {
         this.recoverField_c.getLooper().quit();
      } catch (Throwable var3) {
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Step", "StepParallelScheduler stop !");
   }

   public synchronized Object recoverMethod_a_3c710923(long var1) {
      if (this.recoverMethod_a_9b79()) {
         return this.recoverField_g;
      }

      if (this.recoverField_d.get()) {
         return null;
      }

      try {
         if (var1 != -1L) {
            this.recoverField_h.await(var1, TimeUnit.MILLISECONDS);
         } else {
            this.recoverField_h.await();
         }
      } catch (InterruptedException var4) {
      }

      if (this.recoverField_h.getCount() == 0L) {
         this.recoverField_h = new CountDownLatch(1);
      }

      return this.recoverField_g;
   }

   public void recoverMethod_b_9b75() {
      try {
         if (this.recoverField_b != null) {
            this.recoverField_b.getQueue().clear();
            this.recoverField_c.removeMessages(233);
         }
      } catch (Throwable var2) {
         Log.w("Step", "clean executor e:" + var2);
      }
   }

   private static Looper recoverMethod_d_8fa5950a() {
      HandlerThread var0 = new HandlerThread("jg_step_thread") {
         @Override
         public void run() {
            try {
               super.run();
            } catch (Throwable var2) {
            }
         }
      };
      var0.start();
      Looper var1 = var0.getLooper();
      if (var1 == null) {
         var1 = Looper.getMainLooper();
      }

      return var1;
   }

   static class Member8f362475 extends Handler {
      private final ExecutorService recoverField_a;

      Member8f362475(ExecutorService var1) {
         super(RecoverTypeaf2aadb8.recoverMethod_d_8fa5950a());
         this.recoverField_a = var1;
      }

      @Override
      public void handleMessage(Message var1) {
         try {
            if (!this.recoverField_a.isShutdown()) {
               this.recoverField_a.submit((Callable)var1.obj);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Step", "executor is shutdown");
            }
         } catch (Throwable var3) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("Step", "handleMessage e:" + var3);
         }
      }
   }
}
