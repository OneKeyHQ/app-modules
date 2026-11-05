package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TicketDetailsLoadingScreenKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TicketDetailsLoadingScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketDetailsLoadingScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C24@741L28:TicketDetailsLoadingScreen.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1191759107,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$TicketDetailsLoadingScreenKt.lambda-1.<anonymous> (TicketDetailsLoadingScreen.kt:24)"
            );
         }

         TicketDetailsLoadingScreenKt.TicketDetailsLoadingScreen(null, var1, 0, 1);
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
