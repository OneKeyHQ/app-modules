package io.intercom.android.sdk.m5.navigation;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TicketsDestinationKt$TicketsOpenGateScaffold$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onBackButtonClick;
   // $VF: synthetic field
   final int $navIcon;

   TicketsDestinationKt$TicketsOpenGateScaffold$1(Function0 var1, int var2) {
      this.$onBackButtonClick = var1;
      this.$navIcon = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C226@9551L57,229@9742L6,230@9802L6,225@9513L319:TicketsDestination.kt#m1zs2i");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1414555, var2, -1, "io.intercom.android.sdk.m5.navigation.TicketsOpenGateScaffold.<anonymous> (TicketsDestination.kt:225)"
            );
         }

         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            StringResources_androidKt.stringResource(string.intercom_tickets_space_title, var1, 0),
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
