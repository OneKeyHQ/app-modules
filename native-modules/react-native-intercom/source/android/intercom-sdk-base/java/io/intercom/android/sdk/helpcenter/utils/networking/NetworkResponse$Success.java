package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u0000*\b\b\u0001\u0010\u0001*\u00020\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u000f\u0012\u0006\u0010\u0004\u001a\u00028\u0001¢\u0006\u0004\b\u0005\u0010\u0006J\u000e\u0010\n\u001a\u00028\u0001HÆ\u0003¢\u0006\u0002\u0010\bJ\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00028\u00010\u00002\b\b\u0002\u0010\u0004\u001a\u00028\u0001HÇ\u0001¢\u0006\u0002\u0010\fJ\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0002H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0013H×\u0001R\u0013\u0010\u0004\u001a\u00028\u0001¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\b¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$Success;",
         "T",
         "",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "body",
         "<init>",
         "(Ljava/lang/Object;)V",
         "getBody",
         "()Ljava/lang/Object;",
         "Ljava/lang/Object;",
         "component1",
         "copy",
         "(Ljava/lang/Object;)Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$Success;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class NetworkResponse$Success extends NetworkResponse {
   @NotNull
   private final Object body;
   public static int $stable;

   static {}

   public NetworkResponse$Success(@NotNull Object var1) {
      Intrinsics.checkNotNullParameter(var1, "body");
      super(null);
      this.body = var1;
   }

   @NotNull
   public final Object getBody() {
      return this.body;
   }

   @NotNull
   public final Object component1() {
      return this.body;
   }

   @NotNull
   public final NetworkResponse$Success copy(@NotNull Object var1) {
      Intrinsics.checkNotNullParameter(var1, "body");
      return new NetworkResponse$Success(var1);
   }

   // $VF: synthetic method
   public static NetworkResponse$Success copy$default(NetworkResponse$Success var0, Object var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.body;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Success(body=" + this.body + ')';
   }

   @Override
   public int hashCode() {
      return this.body.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof NetworkResponse$Success)) {
         return false;
      }

      NetworkResponse$Success var2 = (NetworkResponse$Success)var1;
      return Intrinsics.areEqual(this.body, var2.body);
   }
}
