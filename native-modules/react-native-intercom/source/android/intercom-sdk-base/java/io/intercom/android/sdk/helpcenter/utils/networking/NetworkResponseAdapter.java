package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import java.lang.reflect.Type;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.CallAdapter;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0001\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u001a\u0012\u0004\u0012\u0002H\u0001\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00010\u00050\u00040\u0003B#\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\t¢\u0006\u0004\b\f\u0010\rJ\b\u0010\u000e\u001a\u00020\u0007H\u0016J\"\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00050\u00042\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponseAdapter;",
         "S",
         "",
         "Lretrofit2/CallAdapter;",
         "Lretrofit2/Call;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "successType",
         "Ljava/lang/reflect/Type;",
         "onClientError",
         "Lkotlin/Function1;",
         "",
         "",
         "<init>",
         "(Ljava/lang/reflect/Type;Lkotlin/jvm/functions/Function1;)V",
         "responseType",
         "adapt",
         "call",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class NetworkResponseAdapter implements CallAdapter {
   @NotNull
   private final Type successType;
   @NotNull
   private final Function1 onClientError;
   public static final int $stable;

   static { $stable = 8; }

   public NetworkResponseAdapter(@NotNull Type var1, @NotNull Function1 var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "successType");
      Intrinsics.checkNotNullParameter(var2, "onClientError");
      this.successType = var1;
      this.onClientError = var2;
   }

   @NotNull
   @Override
   public Type responseType() {
      return this.successType;
   }

   @NotNull
   public Call adapt(@NotNull Call var1) {
      Intrinsics.checkNotNullParameter(var1, "call");
      return new NetworkResponseCall(var1, this.onClientError);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
