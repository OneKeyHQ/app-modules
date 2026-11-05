package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TopActionBarKt$RecoveredInner62a3a4eb0c801934$1 implements Function2 {
   public static final ComposableSingletons$TopActionBarKt$RecoveredInner62a3a4eb0c801934$1 INSTANCE = new ComposableSingletons$TopActionBarKt$RecoveredInner62a3a4eb0c801934$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C243@8411L324:TopActionBar.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1748428390, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$TopActionBarKt.lambda-10.<anonymous> (TopActionBar.kt:243)"
            );
         }

         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            "Hannah",
            null,
            null,
            null,
            null,
            null,
            false,
            0L,
            0L,
            0L,
            null,
            ComposableSingletons$TopActionBarKt.INSTANCE.recovered$371c8c732f560e7c(),
            var1,
            48,
            384,
            4093
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
