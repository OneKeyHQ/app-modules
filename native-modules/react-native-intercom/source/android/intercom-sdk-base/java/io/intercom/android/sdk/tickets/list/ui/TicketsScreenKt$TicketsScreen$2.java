package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsScreenKt$TicketsScreen$2 implements Function2 {
   // $VF: synthetic field
   final TicketsScreenUiState $uiState;
   // $VF: synthetic field
   final Function0 $onBackButtonClick;
   // $VF: synthetic field
   final int $navIcon;

   TicketsScreenKt$TicketsScreen$2(TicketsScreenUiState var1, Function0 var2, int var3) {
      this.$uiState = var1;
      this.$onBackButtonClick = var2;
      this.$navIcon = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C53@2434L6,54@2494L6,49@2243L281:TicketsScreen.kt#yl74uv");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1473942521, var2, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsScreen.<anonymous> (TicketsScreen.kt:49)");
         }

         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            this.$uiState.getScreenLabel(),
            null,
            null,
            null,
            this.$onBackButtonClick,
            this.$navIcon,
            false,
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4(),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5(),
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
