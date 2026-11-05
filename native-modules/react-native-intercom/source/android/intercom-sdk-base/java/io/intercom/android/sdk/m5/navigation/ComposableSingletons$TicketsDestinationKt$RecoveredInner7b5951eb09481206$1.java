package io.intercom.android.sdk.m5.navigation;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.tickets.list.ui.TicketsLoadingScreenKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TicketsDestinationKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TicketsDestinationKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TicketsDestinationKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C124@5429L22:TicketsDestination.kt#m1zs2i");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1433294616,
               var2,
               -1,
               "io.intercom.android.sdk.m5.navigation.ComposableSingletons$TicketsDestinationKt.lambda-1.<anonymous> (TicketsDestination.kt:124)"
            );
         }

         TicketsLoadingScreenKt.TicketsLoadingScreen(null, false, var1, 0, 3);
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
