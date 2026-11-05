package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$LegacyMessengerAppCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$LegacyMessengerAppCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$LegacyMessengerAppCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C126@5257L27:LegacyMessengerAppCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1061808800,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$LegacyMessengerAppCardKt.lambda-1.<anonymous> (LegacyMessengerAppCard.kt:126)"
            );
         }

         ProgressIndicatorKt.recovered$276fbc1f701bb35f(null, 0L, 0.0F, 0L, 0, 0.0F, var1, 0, 63);
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
