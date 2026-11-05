package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.EmptyState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TicketsEmptyScreenKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TicketsEmptyScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketsEmptyScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C29@816L197:TicketsEmptyScreen.kt#yl74uv");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               222286787,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.list.ui.ComposableSingletons$TicketsEmptyScreenKt.lambda-1.<anonymous> (TicketsEmptyScreen.kt:29)"
            );
         }

         TicketsEmptyScreenKt.TicketsEmptyScreen(new EmptyState("No Tickets", "Your tickets will be shown here", null, 4, null), null, var1, 0, 2);
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
