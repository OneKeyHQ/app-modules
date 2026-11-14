package io.intercom.android.sdk.tickets.create.model;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class CreateTicketViewModel$1$1 implements FlowCollector {
   // $VF: synthetic field
   final CreateTicketViewModel this$0;

   CreateTicketViewModel$1$1(CreateTicketViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(CreateTicketViewModel$CreateTicketFormUiState var1, Continuation var2) {
      if (var1 instanceof CreateTicketViewModel$CreateTicketFormUiState$Content) {
         CreateTicketViewModel.access$setLastKnownContentState$p(this.this$0, (CreateTicketViewModel$CreateTicketFormUiState$Content)var1);
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((CreateTicketViewModel$CreateTicketFormUiState)var1, var2);
   }
}
