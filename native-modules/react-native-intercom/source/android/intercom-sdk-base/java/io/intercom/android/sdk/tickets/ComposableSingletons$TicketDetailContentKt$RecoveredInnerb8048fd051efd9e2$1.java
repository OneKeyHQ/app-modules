package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TicketDetailContentKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$TicketDetailContentKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$TicketDetailContentKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C301@13161L94:TicketDetailContent.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -312754839,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$TicketDetailContentKt.lambda-3.<anonymous> (TicketDetailContent.kt:301)"
            );
         }

         TicketDetailContentKt.TicketDetailContent(null, TicketDetailContentKt.getSampleTicketDetailState(), null, false, var1, 0, 13);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
