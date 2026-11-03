package cn.jpush.android.ad;

import cn.jiguang.api.JCoreManager;
import cn.jpush.android.helper.Logger;
import java.lang.Thread.UncaughtExceptionHandler;

public abstract class RecoverType9b4c5aea implements Runnable {
   protected String recoverField_e;
   private UncaughtExceptionHandler recoverField_a;

   public abstract void recoverMethod_a_9b75();

   protected RecoverType9b4c5aea() {
      this.recoverField_a = new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread var1, Throwable var2) {
            JCoreManager.onEvent(null, "JPUSH", 93, null, null, var1, var2);
            Logger.recoverMethod_e_e925fa09(
               "JPushRunnable",
               "name: "
                  + RecoverType9b4c5aea.this.recoverField_e
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

   protected RecoverType9b4c5aea(String var1) {
      this.recoverField_e = var1;
      this.recoverField_a = new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread var1, Throwable var2) {
            JCoreManager.onEvent(null, "JPUSH", 93, null, null, var1, var2);
            Logger.recoverMethod_e_e925fa09(
               "JPushRunnable",
               "name: "
                  + RecoverType9b4c5aea.this.recoverField_e
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
