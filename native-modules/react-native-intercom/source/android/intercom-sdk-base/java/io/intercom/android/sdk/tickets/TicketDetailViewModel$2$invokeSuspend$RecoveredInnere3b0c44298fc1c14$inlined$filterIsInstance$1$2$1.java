package io.intercom.android.sdk.tickets;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@DebugMetadata(
   f = "TicketDetailViewModel.kt",
   l = 50,
   i = {},
   s = {},
   n = {},
   m = "emit",
   c = "io.intercom.android.sdk.tickets.TicketDetailViewModel$2$invokeSuspend$$inlined$filterIsInstance$1$2"
)
public final class TicketDetailViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   final TicketDetailViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2 this$0;

   public TicketDetailViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2$1(
      TicketDetailViewModel$2$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1$2 var1, Continuation var2
   ) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.emit(null, this);
   }
}
