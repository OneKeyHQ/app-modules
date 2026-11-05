package io.intercom.android.sdk.api;

import android.os.Handler;
import android.os.Looper;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomStatusCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class Api$4 implements Callback {
   // $VF: synthetic field
   final IntercomStatusCallback val$intercomStatusCallback;
   // $VF: synthetic field
   final Api this$0;

   Api$4(Api var1, IntercomStatusCallback var2) {
      this.this$0 = var1;
      this.val$intercomStatusCallback = var2;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful()) {
         new Handler(Looper.getMainLooper()).post(this.val$intercomStatusCallback::onSuccess);
      } else {
         ErrorObject var3 = new ErrorObject(null, var2);
         Handler failureHandler = new Handler(Looper.getMainLooper());
         final IntercomStatusCallback failureCallback = this.val$intercomStatusCallback;
         failureHandler.post(
            () -> failureCallback.onFailure(new IntercomError(1001, "ERROR - [HTTP " + var3.getStatusCode() + "] - " + ErrorStringExtractorKt.extractErrorString(var3)))
         );
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
      Handler failureHandler = new Handler(Looper.getMainLooper());
      final IntercomStatusCallback failureCallback = this.val$intercomStatusCallback;
      failureHandler.post(() -> failureCallback.onFailure(new IntercomError(1001, "ERROR")));
   }
}
