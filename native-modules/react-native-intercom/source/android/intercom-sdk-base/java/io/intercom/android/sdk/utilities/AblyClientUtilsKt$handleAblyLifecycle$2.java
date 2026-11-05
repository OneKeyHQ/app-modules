package io.intercom.android.sdk.utilities;

import io.intercom.android.sdk.AblyManager;
import io.intercom.android.sdk.m5.data.IntercomEvent;
import io.intercom.android.sdk.m5.data.IntercomEvent$SoftReset;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class AblyClientUtilsKt$handleAblyLifecycle$2 implements FlowCollector {
   // $VF: synthetic field
   final AblyManager $ablyManager;

   AblyClientUtilsKt$handleAblyLifecycle$2(AblyManager var1) {
      this.$ablyManager = var1;
   }

   public final Object emit(IntercomEvent var1, Continuation var2) {
      if (Intrinsics.areEqual(var1, IntercomEvent$SoftReset.INSTANCE)) {
         this.$ablyManager.disconnect();
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
