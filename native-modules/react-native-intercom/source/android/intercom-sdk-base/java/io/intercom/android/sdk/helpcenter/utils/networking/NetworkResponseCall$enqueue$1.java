package io.intercom.android.sdk.helpcenter.utils.networking;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000%\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0000*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J$\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0007H\u0016J\u001e\u0010\b\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u00052\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\u000b",
   d2 = {
         "io/intercom/android/sdk/helpcenter/utils/networking/NetworkResponseCall$enqueue$1",
         "Lretrofit2/Callback;",
         "onResponse",
         "",
         "call",
         "Lretrofit2/Call;",
         "response",
         "Lretrofit2/Response;",
         "onFailure",
         "throwable",
         "",
         "intercom-sdk-base_release"
   }
)
public final class NetworkResponseCall$enqueue$1 implements Callback {
   // $VF: synthetic field
   final Callback $callback;
   // $VF: synthetic field
   final NetworkResponseCall this$0;

   NetworkResponseCall$enqueue$1(Callback var1, NetworkResponseCall var2) {
      this.$callback = var1;
      this.this$0 = var2;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      Intrinsics.checkNotNullParameter(var1, "call");
      Intrinsics.checkNotNullParameter(var2, "response");
      Object var3 = var2.body();
      int var4 = var2.code();
      if (var2.isSuccessful()) {
         if (var3 != null) {
            this.$callback.onResponse(this.this$0, Response.success(new NetworkResponse$Success(var3)));
         } else {
            this.$callback.onResponse(this.this$0, Response.success(new NetworkResponse$ClientError(new Throwable())));
         }
      } else {
         this.$callback
            .onResponse(this.this$0, Response.success(new NetworkResponse$ServerError(var4, NetworkResponseCall.access$serverErrorCode(this.this$0, var2))));
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
      Intrinsics.checkNotNullParameter(var1, "call");
      Intrinsics.checkNotNullParameter(var2, "throwable");
      NetworkResponse var10000;
      if (var2 instanceof IOException) {
         var10000 = new NetworkResponse$NetworkError((IOException)var2);
      } else {
         String var4 = var1.request().url().encodedPath();
         NetworkResponseCall.access$getOnClientError$p(this.this$0).invoke(new Exception("Client error on " + var4, var2));
         var10000 = new NetworkResponse$ClientError(var2);
      }

      NetworkResponse var3 = var10000;
      this.$callback.onResponse(this.this$0, Response.success(var3));
   }
}
