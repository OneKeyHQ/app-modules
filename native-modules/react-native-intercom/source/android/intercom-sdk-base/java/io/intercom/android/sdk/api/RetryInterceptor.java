package io.intercom.android.sdk.api;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;

class RetryInterceptor implements Interceptor {
   private static final int MAX_RETRIES = 3;
   private final RetryInterceptor$Sleeper sleeper;

   public RetryInterceptor(RetryInterceptor$Sleeper var1) {
      this.sleeper = var1;
   }

   @Override
   public Response intercept(Chain var1) throws IOException {
      Call var2 = var1.call();

      for (int var3 = 0; var3 <= 3; var3++) {
         try {
            return var1.proceed(var1.request());
         } catch (IOException var5) {
            if (var2.isCanceled() || var3 == 3) {
               throw var5;
            }

            this.sleeper.sleep(getRetryTimer(var3 + 1), var2);
            if (var2.isCanceled()) {
               throw var5;
            }
         }
      }

      throw new IOException("request failed due to network errors");
   }

   static int getRetryTimer(int var0) {
      return (int)Math.pow(2.0, var0);
   }
}
