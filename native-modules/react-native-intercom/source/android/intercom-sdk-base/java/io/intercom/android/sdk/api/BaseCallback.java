package io.intercom.android.sdk.api;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallback implements Callback {
   private final Twig twig = LumberMill.getLogger();

   protected abstract void onSuccess(Object var1);

   protected void onError(ErrorObject var1) {
   }

   void logFailure(String var1, ErrorObject var2) {
      this.twig.e(var1 + ": " + getDetails(var2));
   }

   @Override
   public final void onResponse(Call var1, Response var2) {
      if (var2 == null) {
         this.handleError(new ErrorObject(new IllegalStateException("No body returned from the server"), null));
      } else if (var2.body() == null) {
         this.handleError(new ErrorObject(new IllegalStateException("No body returned from the server"), var2));
      } else if (!var2.isSuccessful()) {
         this.handleError(new ErrorObject(new Exception("Status code outside the 200-300 range"), var2));
      } else {
         this.onSuccess(var2.body());
      }
   }

   @Override
   public final void onFailure(Call var1, Throwable var2) {
      this.handleError(new ErrorObject(var2, null));
   }

   private void handleError(ErrorObject var1) {
      this.logFailure("Api call failed", var1);
      this.onError(var1);
   }

   static String getDetails(ErrorObject var0) {
      String var1;
      if (var0.hasErrorBody()) {
         var1 = var0.getErrorBody();
      } else {
         var1 = var0.getThrowable().getMessage();
      }

      if (var1 == null) {
         var1 = "unknown error";
      }

      return var1;
   }
}
