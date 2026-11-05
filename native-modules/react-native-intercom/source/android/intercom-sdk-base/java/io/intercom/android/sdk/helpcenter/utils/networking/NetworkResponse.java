package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u0000*\n\b\u0000\u0010\u0001 \u0001*\u00020\u00022\u00020\u0002:\u0004\u0005\u0006\u0007\bB\t\b\u0005¢\u0006\u0004\b\u0003\u0010\u0004\u0082\u0001\u0004\t\n\u000b\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "T",
         "",
         "<init>",
         "()V",
         "Success",
         "ServerError",
         "NetworkError",
         "ClientError",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$ClientError;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$NetworkError;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$ServerError;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse$Success;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 2)
public abstract class NetworkResponse {
   public static int $stable;

   static {}

   private NetworkResponse() {
   }

   // $VF: synthetic method
   public NetworkResponse(DefaultConstructorMarker var1) {
      this();
   }
}
