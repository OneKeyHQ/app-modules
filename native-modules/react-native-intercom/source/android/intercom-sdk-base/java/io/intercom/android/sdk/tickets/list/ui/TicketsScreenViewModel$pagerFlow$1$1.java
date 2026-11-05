package io.intercom.android.sdk.tickets.list.ui;

import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.tickets.list.reducers.TicketRowReducerKt;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.AdaptedFunctionReference;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsScreenViewModel$pagerFlow$1$1 extends AdaptedFunctionReference implements Function2, SuspendFunction {
   public static final TicketsScreenViewModel$pagerFlow$1$1 INSTANCE = new TicketsScreenViewModel$pagerFlow$1$1();

   TicketsScreenViewModel$pagerFlow$1$1() {
      super(
         2,
         TicketRowReducerKt.class,
         "reduceTicketRowData",
         "reduceTicketRowData(Lio/intercom/android/sdk/models/Ticket;)Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         5
      );
   }

   public final Object invoke(Ticket var1, Continuation var2) {
      return TicketsScreenViewModel.access$pagerFlow$lambda$1$reduceTicketRowData(var1, var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((Ticket)var1, (Continuation)var2);
   }
}
