package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$WrapReportingTextKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$WrapReportingTextKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$WrapReportingTextKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C42@1340L10,39@1219L149:WrapReportingText.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               136624489,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$WrapReportingTextKt.lambda-1.<anonymous> (WrapReportingText.kt:39)"
            );
         }

         WrapReportingTextKt.recovered$e51f4221e6440197(
            null,
            "Hello there",
            Color.Companion.recovered$ba858dcfc5e04b9c(),
            IntercomTheme.INSTANCE.getTypography(var1, IntercomTheme.$stable).getType01(),
            null,
            var1,
            432,
            17
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
