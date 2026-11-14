package io.intercom.android.sdk.m5.navigation;

import io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsDestinationKt$ticketsDestination$7$1$1$1 extends FunctionReferenceImpl implements Function0 {
   TicketsDestinationKt$ticketsDestination$7$1$1$1(Object var1) {
      super(0, var1, TicketsScreenViewModel.class, "onRetryOpen", "onRetryOpen()V", 0);
   }

   public final void recoveredVoidInvoke$3ec68c5cf9b7a244() {
      ((TicketsScreenViewModel)this.receiver).onRetryOpen();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$3ec68c5cf9b7a244();
      return Unit.INSTANCE;
   }
}
