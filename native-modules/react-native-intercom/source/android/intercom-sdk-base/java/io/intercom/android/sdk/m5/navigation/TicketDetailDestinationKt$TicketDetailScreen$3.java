package io.intercom.android.sdk.m5.navigation;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.tickets.TicketDetailState;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketDetailDestinationKt$TicketDetailScreen$3 implements Function2 {
   // $VF: synthetic field
   final TicketDetailState $ticketDetailState;
   // $VF: synthetic field
   final Function0 $onBackClicked;

   TicketDetailDestinationKt$TicketDetailScreen$3(TicketDetailState var1, Function0 var2) {
      this.$ticketDetailState = var1;
      this.$onBackClicked = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C229@9516L6,230@9579L6,224@9222L391:TicketDetailDestination.kt#m1zs2i");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2018170598, var2, -1, "io.intercom.android.sdk.m5.navigation.TicketDetailScreen.<anonymous> (TicketDetailDestination.kt:224)"
            );
         }

         String var10001;
         label24: {
            TicketDetailState var4 = this.$ticketDetailState;
            TicketDetailState$TicketDetailContentState var3 = var4 instanceof TicketDetailState$TicketDetailContentState
               ? (TicketDetailState$TicketDetailContentState)var4
               : null;
            if (var3 != null) {
               String var5 = var3.getTicketName();
               if (var5 != null) {
                  var10001 = var5;
                  break label24;
               }
            }

            var10001 = "";
         }

         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            var10001,
            null,
            null,
            null,
            this.$onBackClicked,
            drawable.intercom_ic_close,
            false,
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
            0L,
            null,
            null,
            var1,
            0,
            0,
            7325
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
