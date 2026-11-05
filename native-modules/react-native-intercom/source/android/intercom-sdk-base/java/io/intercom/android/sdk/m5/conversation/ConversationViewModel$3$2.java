package io.intercom.android.sdk.m5.conversation;

import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationViewModel$3$2 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$3$2(ConversationViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(Set var1, Continuation var2) {
      ConversationViewModel.access$getSettleStreamingPartUseCase$p(this.this$0)
         .invoke(ConversationViewModel.access$getClientState$p(this.this$0), ConversationViewModel.access$getFinStreamingStateHolder$p(this.this$0), var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Set)var1, var2);
   }
}
