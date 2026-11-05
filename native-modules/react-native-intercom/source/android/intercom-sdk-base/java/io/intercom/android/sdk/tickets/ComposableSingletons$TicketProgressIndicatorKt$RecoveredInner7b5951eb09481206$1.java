package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TicketProgressIndicatorKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TicketProgressIndicatorKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketProgressIndicatorKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C92@3475L222:TicketProgressIndicator.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1410192629,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$TicketProgressIndicatorKt.lambda-1.<anonymous> (TicketProgressIndicator.kt:92)"
            );
         }

         long var3 = TicketDetailContentKt.getSampleTicketDetailState().getTicketTimelineCardState().recovered$4e526da7fcc96ce2();
         TicketProgressIndicatorKt.recovered$8eea2a3ce3419e18(
            TicketDetailContentKt.getSampleTicketDetailState().getTicketTimelineCardState().getProgressSections(), var3, null, var1, 0, 4
         );
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
