package io.intercom.android.sdk.utilities;

import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.NexusWrapper;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.IntercomEvent;
import io.intercom.android.sdk.m5.data.IntercomEvent$AppEnteredBackground;
import io.intercom.android.sdk.m5.data.IntercomEvent$AppEnteredForeground;
import io.intercom.android.sdk.m5.data.IntercomEvent$BaseResponseReceived;
import io.intercom.android.sdk.m5.data.IntercomEvent$SoftReset;
import io.intercom.android.sdk.models.HostAppState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class NexusClientUtilsKt$handleNexusClientLifecycle$2 implements FlowCollector {
   // $VF: synthetic field
   final NexusWrapper $nexusWrapper;
   // $VF: synthetic field
   final IntercomDataLayer $dataLayer;

   NexusClientUtilsKt$handleNexusClientLifecycle$2(NexusWrapper var1, IntercomDataLayer var2) {
      this.$nexusWrapper = var1;
      this.$dataLayer = var2;
   }

   public final Object emit(IntercomEvent var1, Continuation var2) {
      IntercomEvent var3 = var1;
      if (var3 instanceof IntercomEvent$AppEnteredBackground) {
         this.$nexusWrapper.scheduleDisconnect(((AppConfig)this.$dataLayer.getConfig().getValue()).getNewSessionThresholdMs());
      } else if (var3 instanceof IntercomEvent$AppEnteredForeground) {
         this.$nexusWrapper.connect(((AppConfig)this.$dataLayer.getConfig().getValue()).getRealTimeConfig(), true);
      } else if (var3 instanceof IntercomEvent$BaseResponseReceived) {
         NexusConfig var4 = ((IntercomEvent$BaseResponseReceived)var1).getBaseResponse().getConfig().getRealTimeConfig();
         if (var4.getEndpoints().isEmpty()) {
            this.$nexusWrapper.disconnect();
         } else if (!((HostAppState)this.$dataLayer.getHostAppState().getValue()).isBackgrounded()) {
            this.$nexusWrapper.connect(var4, true);
         }
      } else if (Intrinsics.areEqual(var3, IntercomEvent$SoftReset.INSTANCE)) {
         this.$nexusWrapper.disconnect();
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((IntercomEvent)var1, var2);
   }
}
