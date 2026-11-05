package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TicketTimelineCardKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$TicketTimelineCardKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$TicketTimelineCardKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C120@4925L6,119@4848L857:TicketTimelineCard.kt#3ofz5f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1948582297,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.ComposableSingletons$TicketTimelineCardKt.lambda-3.<anonymous> (TicketTimelineCard.kt:119)"
            );
         }

         Modifier var3 = BackgroundKt.recovered$42a3174180b3fb7a(
            Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
         );
         TicketTimelineCardState var10000 = TicketTimelineCardKt.getSampleTicketTimelineCardState();
         long var10004 = TicketStatus.Resolved.recovered$6f15fcf6c761ad06();
         TicketTimelineCardState$ProgressSection[] var4 = new TicketTimelineCardState$ProgressSection[]{
            new TicketTimelineCardState$ProgressSection(true, false),
            new TicketTimelineCardState$ProgressSection(true, false),
            new TicketTimelineCardState$ProgressSection(true, false)
         };
         TicketTimelineCardKt.TicketTimelineCard(
            TicketTimelineCardState.recovered$a30edbfd18d4c1f7(var10000, null, null, null, var10004, CollectionsKt.listOf(var4), "Resolved", null, 71, null),
            var3,
            var1,
            0,
            0
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
