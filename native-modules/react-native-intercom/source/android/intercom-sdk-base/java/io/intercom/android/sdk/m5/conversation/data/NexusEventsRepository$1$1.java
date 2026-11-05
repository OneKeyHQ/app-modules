package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.nexus.NexusEvent;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class NexusEventsRepository$1$1 implements FlowCollector {
   // $VF: synthetic field
   final NexusEventsRepository this$0;

   NexusEventsRepository$1$1(NexusEventsRepository var1) {
      this.this$0 = var1;
   }

   public final Object emit(NexusEvent var1, Continuation var2) {
      NexusEventsRepository.access$getNexusClient$p(this.this$0).fire(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((NexusEvent)var1, var2);
   }
}
