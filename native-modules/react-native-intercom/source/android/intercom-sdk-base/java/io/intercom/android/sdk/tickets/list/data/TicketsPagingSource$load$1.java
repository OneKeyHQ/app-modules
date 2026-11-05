package io.intercom.android.sdk.tickets.list.data;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "TicketsPagingSource.kt",
   l = 14,
   i = 0,
   s = "L$0",
   n = "params",
   m = "load",
   c = "io.intercom.android.sdk.tickets.list.data.TicketsPagingSource"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsPagingSource$load$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final TicketsPagingSource this$0;
   int label;

   TicketsPagingSource$load$1(TicketsPagingSource var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.load(null, this);
   }
}
