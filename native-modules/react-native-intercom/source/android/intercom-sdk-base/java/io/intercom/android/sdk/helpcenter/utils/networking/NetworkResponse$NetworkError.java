package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\t\u001a\u00020\u0004HÆ\u0003J\u0013\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u0004HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH×\u0003J\t\u0010\u000f\u001a\u00020\u0010H×\u0001J\t\u0010\u0011\u001a\u00020\u0012H×\u0001R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$NetworkError;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "",
         "error",
         "Ljava/io/IOException;",
         "<init>",
         "(Ljava/io/IOException;)V",
         "getError",
         "()Ljava/io/IOException;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class NetworkResponse$NetworkError extends NetworkResponse {
   @NotNull
   private final IOException error;
   public static final int $stable;

   static { $stable = 8; }

   public NetworkResponse$NetworkError(@NotNull IOException var1) {
      Intrinsics.checkNotNullParameter(var1, "error");
      super(null);
      this.error = var1;
   }

   @NotNull
   public final IOException getError() {
      return this.error;
   }

   @NotNull
   public final IOException component1() {
      return this.error;
   }

   @NotNull
   public final NetworkResponse$NetworkError copy(@NotNull IOException var1) {
      Intrinsics.checkNotNullParameter(var1, "error");
      return new NetworkResponse$NetworkError(var1);
   }

   // $VF: synthetic method
   public static NetworkResponse$NetworkError copy$default(NetworkResponse$NetworkError var0, IOException var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.error;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "NetworkError(error=" + this.error + ')';
   }

   @Override
   public int hashCode() {
      return this.error.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof NetworkResponse$NetworkError)) {
         return false;
      }

      NetworkResponse$NetworkError var2 = (NetworkResponse$NetworkError)var1;
      return Intrinsics.areEqual(this.error, var2.error);
   }
}
