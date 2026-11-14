package io.intercom.android.sdk.tickets.list.ui;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$NewComment;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsScreenViewModel$3$2 implements FlowCollector {
   // $VF: synthetic field
   final TicketsScreenViewModel this$0;

   TicketsScreenViewModel$3$2(TicketsScreenViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ParsedNexusEvent$ConversationNexusEvent$NewComment var1, Continuation var2) {
      Object var10000 = TicketsScreenViewModel.access$get_effect$p(this.this$0).emit(TicketsScreenEffects$RefreshTickets.INSTANCE, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$ConversationNexusEvent$NewComment)var1, var2);
   }
}
