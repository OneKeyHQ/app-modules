package io.intercom.android.sdk.tickets.create.data;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "TicketRepository.kt",
   l = 36,
   i = 0,
   s = "L$0",
   n = "this",
   m = "createTicket",
   c = "io.intercom.android.sdk.tickets.create.data.TicketRepository"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketRepository$createTicket$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final TicketRepository this$0;
   int label;

   TicketRepository$createTicket$1(TicketRepository var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.createTicket(null, 0L, null, this);
   }
}
