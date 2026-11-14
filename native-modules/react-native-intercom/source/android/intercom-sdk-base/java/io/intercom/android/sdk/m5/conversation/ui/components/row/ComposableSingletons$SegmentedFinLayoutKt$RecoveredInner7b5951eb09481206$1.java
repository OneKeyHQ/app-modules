package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(boolean var1, Composer var2, int var3) {
      ComposerKt.sourceInformation(var2, "C141@6315L22:SegmentedFinLayout.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               101602958,
               var3,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$SegmentedFinLayoutKt.lambda-1.<anonymous> (SegmentedFinLayout.kt:141)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            "Fin • AI Agent", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var2, 6, 0, 262142
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke(((Boolean)var1).booleanValue(), (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
