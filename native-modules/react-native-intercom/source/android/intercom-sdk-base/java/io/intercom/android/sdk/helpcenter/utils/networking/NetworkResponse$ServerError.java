package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001b\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0004HÆ\u0003J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\u001f\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H×\u0003J\t\u0010\u0014\u001a\u00020\u0004H×\u0001J\t\u0010\u0015\u001a\u00020\u0006H×\u0001R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$ServerError;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "",
         "code",
         "",
         "errorCode",
         "",
         "<init>",
         "(ILjava/lang/String;)V",
         "getCode",
         "()I",
         "getErrorCode",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class NetworkResponse$ServerError extends NetworkResponse {
   private final int code;
   @Nullable
   private final String errorCode;
   public static int $stable;

   static {}

   public NetworkResponse$ServerError(int var1, @Nullable String var2) {
      super(null);
      this.code = var1;
      this.errorCode = var2;
   }

   // $VF: synthetic method
   public NetworkResponse$ServerError(int var1, String var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (String) (null) : var2));
   }

   public final int getCode() {
      return this.code;
   }

   @Nullable
   public final String getErrorCode() {
      return this.errorCode;
   }

   public final int component1() {
      return this.code;
   }

   @Nullable
   public final String component2() {
      return this.errorCode;
   }

   @NotNull
   public final NetworkResponse$ServerError copy(int var1, @Nullable String var2) {
      return new NetworkResponse$ServerError(var1, var2);
   }

   // $VF: synthetic method
   public static NetworkResponse$ServerError copy$default(NetworkResponse$ServerError var0, int var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.code;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.errorCode;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ServerError(code=" + this.code + ", errorCode=" + this.errorCode + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.code);
      return var1 * 31 + (this.errorCode == null ? 0 : this.errorCode.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof NetworkResponse$ServerError)) {
         return false;
      }

      NetworkResponse$ServerError var2 = (NetworkResponse$ServerError)var1;
      return this.code != var2.code ? false : Intrinsics.areEqual(this.errorCode, var2.errorCode);
   }
}
