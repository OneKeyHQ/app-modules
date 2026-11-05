package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.utilities.connectivity.NetworkConnectivityMonitor;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/GetNetworkState;",
         "",
         "networkConnectivityMonitor",
         "Lio/intercom/android/sdk/utilities/connectivity/NetworkConnectivityMonitor;",
         "<init>",
         "(Lio/intercom/android/sdk/utilities/connectivity/NetworkConnectivityMonitor;)V",
         "invoke",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/states/NetworkState;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class GetNetworkState {
   @NotNull
   private final NetworkConnectivityMonitor networkConnectivityMonitor;
   public static final int $stable;

   static { $stable = 8; }

   public GetNetworkState(@NotNull NetworkConnectivityMonitor var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "networkConnectivityMonitor");
      this.networkConnectivityMonitor = var1;
   }

   @NotNull
   public final Flow invoke() {
      return FlowKt.callbackFlow(new GetNetworkState$invoke$1(this, null));
   }

   // $VF: synthetic method
   public static final NetworkConnectivityMonitor access$getNetworkConnectivityMonitor$p(GetNetworkState var0) {
      return var0.networkConnectivityMonitor;
   }
}
