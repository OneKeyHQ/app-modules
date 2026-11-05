package io.intercom.android.sdk.api;

import java.util.concurrent.TimeUnit;
import okhttp3.Call;

public class RetryInterceptor$Sleeper {
   private static final long POLL_INTERVAL_MILLIS = 100L;

   public void sleep(int var1, Call var2) {
      long var3 = TimeUnit.SECONDS.toMillis(var1);

      try {
         while (var3 > 0L && !var2.isCanceled()) {
            long var5 = Math.min(var3, 100L);
            this.waitFor(var5);
            var3 -= var5;
         }
      } catch (InterruptedException var7) {
         Thread.currentThread().interrupt();
      }
   }

   protected void waitFor(long var1) throws InterruptedException {
      TimeUnit.MILLISECONDS.sleep(var1);
   }
}
