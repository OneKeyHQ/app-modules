package io.intercom.android.sdk.m5.navigation;

import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.tickets.list.ui.TicketsScreenEffects;
import io.intercom.android.sdk.tickets.list.ui.TicketsScreenEffects$RefreshTickets;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsDestinationKt$ticketsDestination$7$2$1$1 implements FlowCollector {
   // $VF: synthetic field
   final LazyPagingItems $lazyPagingItems;

   TicketsDestinationKt$ticketsDestination$7$2$1$1(LazyPagingItems var1) {
      this.$lazyPagingItems = var1;
   }

   public final Object emit(TicketsScreenEffects var1, Continuation var2) {
      if (Intrinsics.areEqual(var1, TicketsScreenEffects$RefreshTickets.INSTANCE)) {
         this.$lazyPagingItems.refresh();
         return Unit.INSTANCE;
      } else {
         throw new NoWhenBranchMatchedException();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((TicketsScreenEffects)var1, var2);
   }
}
