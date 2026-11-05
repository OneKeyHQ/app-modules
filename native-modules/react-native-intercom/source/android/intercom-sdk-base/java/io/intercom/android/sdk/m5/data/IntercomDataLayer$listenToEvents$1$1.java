package io.intercom.android.sdk.m5.data;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomDataLayer$listenToEvents$1$1 implements FlowCollector {
   // $VF: synthetic field
   final Function1 $onNewEvent;

   IntercomDataLayer$listenToEvents$1$1(Function1 var1) {
      this.$onNewEvent = var1;
   }

   public final Object emit(IntercomEvent var1, Continuation var2) {
      this.$onNewEvent.invoke(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((IntercomEvent)var1, var2);
   }
}
