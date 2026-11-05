package io.intercom.android.sdk.helpcenter.utils.networking;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u0001*\u0006\u0012\u0002\b\u00030\u0002H\u0000\u001a\u0010\u0010\u0003\u001a\u00020\u0001*\u0006\u0012\u0002\b\u00030\u0002H\u0000¨\u0006\u0004",
   d2 = {"isRetryable", "", "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;", "isUserJwtExpired", "intercom-sdk-base_release"}
)
public final class NetworkResponseKt {
   public static final boolean isRetryable(@NotNull NetworkResponse var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      NetworkResponse var1 = var0;
      boolean var10000;
      if (var1 instanceof NetworkResponse$Success) {
         var10000 = false;
      } else if (var1 instanceof NetworkResponse$NetworkError) {
         var10000 = true;
      } else if (var1 instanceof NetworkResponse$ClientError) {
         var10000 = false;
      } else {
         if (!(var1 instanceof NetworkResponse$ServerError)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = ((NetworkResponse$ServerError)var0).getCode() >= 500;
      }

      return var10000;
   }

   public static final boolean isUserJwtExpired(@NotNull NetworkResponse var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return var0 instanceof NetworkResponse$ServerError
         && Intrinsics.areEqual(((NetworkResponse$ServerError)var0).getErrorCode(), "intercom_user_jwt_expired");
   }
}
