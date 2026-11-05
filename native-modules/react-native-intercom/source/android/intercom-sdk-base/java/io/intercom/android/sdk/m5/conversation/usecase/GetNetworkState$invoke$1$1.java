package io.intercom.android.sdk.m5.conversation.usecase;

import io.intercom.android.sdk.m5.conversation.states.NetworkState$Disconnected;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Reconnected;
import io.intercom.android.sdk.utilities.connectivity.NetworkConnectivityMonitor$ConnectivityEventListener;
import kotlin.Metadata;
import kotlinx.coroutines.channels.ProducerScope;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0013\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0003H\u0016¨\u0006\u0005",
   d2 = {
         "io/intercom/android/sdk/m5/conversation/usecase/GetNetworkState$invoke$1$1",
         "Lio/intercom/android/sdk/utilities/connectivity/NetworkConnectivityMonitor$ConnectivityEventListener;",
         "onDisconnect",
         "",
         "onReconnect",
         "intercom-sdk-base_release"
   }
)
public final class GetNetworkState$invoke$1$1 implements NetworkConnectivityMonitor$ConnectivityEventListener {
   // $VF: synthetic field
   final ProducerScope $$this$callbackFlow;

   GetNetworkState$invoke$1$1(ProducerScope var1) {
      this.$$this$callbackFlow = var1;
   }

   @Override
   public void onDisconnect() {
      this.$$this$callbackFlow.recovered$1b09debba82c0756(NetworkState$Disconnected.INSTANCE);
   }

   @Override
   public void onReconnect() {
      this.$$this$callbackFlow.recovered$1b09debba82c0756(NetworkState$Reconnected.INSTANCE);
   }
}
