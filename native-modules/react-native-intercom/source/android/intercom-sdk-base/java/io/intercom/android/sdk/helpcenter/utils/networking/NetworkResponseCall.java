package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.api.ServerErrorCodeKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000J\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00010\u00040\u0003B)\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007¢\u0006\u0004\b\n\u0010\u000bJ\u001c\u0010\f\u001a\u00020\t2\u0012\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00040\u000eH\u0016J\u0014\u0010\u000f\u001a\u0004\u0018\u00010\u0010*\b\u0012\u0004\u0012\u00028\u00000\u0011H\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0016\u0010\u0014\u001a\u0010\u0012\f\u0012\n \u0015*\u0004\u0018\u00018\u00008\u00000\u0000H\u0016J\b\u0010\u0016\u001a\u00020\u0013H\u0016J\b\u0010\u0017\u001a\u00020\tH\u0016J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00040\u0011H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u001cH\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponseCall;",
         "S",
         "",
         "Lretrofit2/Call;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "delegate",
         "onClientError",
         "Lkotlin/Function1;",
         "",
         "",
         "<init>",
         "(Lretrofit2/Call;Lkotlin/jvm/functions/Function1;)V",
         "enqueue",
         "callback",
         "Lretrofit2/Callback;",
         "serverErrorCode",
         "",
         "Lretrofit2/Response;",
         "isExecuted",
         "",
         "clone",
         "kotlin.jvm.PlatformType",
         "isCanceled",
         "cancel",
         "execute",
         "request",
         "Lokhttp3/Request;",
         "timeout",
         "Lokio/Timeout;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nNetworkResponseCall.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NetworkResponseCall.kt\nio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponseCall\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,73:1\n1#2:74\n*E\n"
)
public final class NetworkResponseCall implements Call {
   @NotNull
   private final Call delegate;
   @NotNull
   private final Function1 onClientError;
   public static final int $stable;

   static { $stable = 8; }

   public NetworkResponseCall(@NotNull Call var1, @NotNull Function1 var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "delegate");
      Intrinsics.checkNotNullParameter(var2, "onClientError");
      this.delegate = var1;
      this.onClientError = var2;
   }

   @Override
   public void enqueue(@NotNull Callback var1) {
      Intrinsics.checkNotNullParameter(var1, "callback");
      this.delegate.enqueue(new NetworkResponseCall$enqueue$1(var1, this));
   }

   private final String serverErrorCode(Response var1) {
      Response var2 = var1;

      Object var3;
      try {
         Response recoveredResponse = var2;
         int var4 = 0;
         ResponseBody var10000 = recoveredResponse.errorBody();
         var3 = Result.recovered$52f8e2dc6aafd1c0(var10000 != null ? var10000.string() : null);
      } catch (Throwable var5) {
         var3 = Result.recovered$52f8e2dc6aafd1c0(ResultKt.createFailure(var5));
      }

      Object var6 = var3;
      return ServerErrorCodeKt.parseServerErrorCode((String)(Result.recovered$01d9c4255776faef(var6) ? null : var6));
   }

   @Override
   public boolean isExecuted() {
      return this.delegate.isExecuted();
   }

   @NotNull
   public NetworkResponseCall clone() {
      Call var10002 = this.delegate.clone();
      Intrinsics.checkNotNullExpressionValue(var10002, "clone(...)");
      return new NetworkResponseCall(var10002, this.onClientError);
   }

   @Override
   public boolean isCanceled() {
      return this.delegate.isCanceled();
   }

   @Override
   public void cancel() {
      this.delegate.cancel();
   }

   @NotNull
   @Override
   public Response execute() {
      throw new UnsupportedOperationException("NetworkResponseCall doesn't support execute");
   }

   @NotNull
   @Override
   public Request request() {
      Request var10000 = this.delegate.request();
      Intrinsics.checkNotNullExpressionValue(var10000, "request(...)");
      return var10000;
   }

   @NotNull
   @Override
   public Timeout timeout() {
      Timeout var10000 = this.delegate.timeout();
      Intrinsics.checkNotNullExpressionValue(var10000, "timeout(...)");
      return var10000;
   }


   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   public static final String access$serverErrorCode(NetworkResponseCall var0, Response var1) {
      return var0.serverErrorCode(var1);
   }

   // $VF: synthetic method
   public static final Function1 access$getOnClientError$p(NetworkResponseCall var0) {
      return var0.onClientError;
   }
}
