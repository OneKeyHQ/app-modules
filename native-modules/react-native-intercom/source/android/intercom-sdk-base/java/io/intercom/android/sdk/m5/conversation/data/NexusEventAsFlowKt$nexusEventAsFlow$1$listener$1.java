package io.intercom.android.sdk.m5.conversation.data;

import android.util.Log;
import io.intercom.android.nexus.NexusEvent;
import io.intercom.android.nexus.NexusListener;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.ProducerScope;
import kotlinx.coroutines.channels.SendChannel.DefaultImpls;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0003H\u0016J\b\u0010\u0007\u001a\u00020\u0003H\u0016J\b\u0010\b\u001a\u00020\u0003H\u0016¨\u0006\t",
   d2 = {
         "io/intercom/android/sdk/m5/conversation/data/NexusEventAsFlowKt$nexusEventAsFlow$1$listener$1",
         "Lio/intercom/android/nexus/NexusListener;",
         "notifyEvent",
         "",
         "nexusEvent",
         "Lio/intercom/android/nexus/NexusEvent;",
         "onConnect",
         "onConnectFailed",
         "onShutdown",
         "intercom-sdk-base_release"
   }
)
public final class NexusEventAsFlowKt$nexusEventAsFlow$1$listener$1 implements NexusListener {
   // $VF: synthetic field
   final ProducerScope $$this$callbackFlow;

   NexusEventAsFlowKt$nexusEventAsFlow$1$listener$1(ProducerScope var1) {
      this.$$this$callbackFlow = var1;
   }

   @Override
   public void notifyEvent(NexusEvent var1) {
      Intrinsics.checkNotNullParameter(var1, "nexusEvent");
      Log.d("nexus", "notifyEvent " + var1.getEventType() + ": " + var1.getEventData());
      this.$$this$callbackFlow.recovered$1b09debba82c0756(NexusEventAsFlowKt.access$asNexusData(var1));
   }

   @Override
   public void onConnect() {
      Log.d("nexus", "onConnect");
      this.$$this$callbackFlow.recovered$1b09debba82c0756(ParsedNexusEvent$NexusConnected.INSTANCE);
   }

   @Override
   public void onConnectFailed() {
      Log.d("nexus", "onConnectFailed");
   }

   @Override
   public void onShutdown() {
      Log.d("nexus", "onShutdown");
      DefaultImpls.close$default(this.$$this$callbackFlow.getChannel(), null, 1, null);
   }
}
