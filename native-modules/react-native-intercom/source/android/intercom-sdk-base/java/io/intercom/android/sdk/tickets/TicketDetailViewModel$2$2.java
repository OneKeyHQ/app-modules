package io.intercom.android.sdk.tickets;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$NewComment;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketDetailViewModel$2$2 implements FlowCollector {
   // $VF: synthetic field
   final TicketDetailViewModel this$0;

   TicketDetailViewModel$2$2(TicketDetailViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ParsedNexusEvent$ConversationNexusEvent$NewComment var1, Continuation var2) {
      TicketDetailViewModel var10000 = this.this$0;
      String var10001 = TicketDetailViewModel.access$getTicketId$p(this.this$0);
      Intrinsics.checkNotNull(var10001);
      var10000.fetchTicketDetail$intercom_sdk_base_release(var10001);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$ConversationNexusEvent$NewComment)var1, var2);
   }
}
