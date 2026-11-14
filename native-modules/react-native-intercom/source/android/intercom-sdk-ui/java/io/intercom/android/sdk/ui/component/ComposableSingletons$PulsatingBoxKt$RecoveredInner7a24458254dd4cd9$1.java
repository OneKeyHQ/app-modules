package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$PulsatingBoxKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$PulsatingBoxKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$PulsatingBoxKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C66@2130L240:PulsatingBox.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -579145229, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$PulsatingBoxKt.lambda-2.<anonymous> (PulsatingBox.kt:66)"
            );
         }

         PulsatingBoxKt.recovered$a3e4cf41d9d237f7(
            null, 1.5F, 0L, null, true, ComposableSingletons$PulsatingBoxKt.INSTANCE.recovered$a60c4be83e6680c6(), var1, 221232, 13
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
