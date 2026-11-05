package io.intercom.android.sdk.survey.block;

import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$EnhancedCodeBlockKt$RecoveredInner62a3a4eb0c801934$1 implements Function2 {
   public static final ComposableSingletons$EnhancedCodeBlockKt$RecoveredInner62a3a4eb0c801934$1 INSTANCE = new ComposableSingletons$EnhancedCodeBlockKt$RecoveredInner62a3a4eb0c801934$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C468@17695L380:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1558244220,
               var2,
               -1,
               "io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.lambda-10.<anonymous> (EnhancedCodeBlock.kt:468)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$db063fabb945fca0(), var1, 12582912, 127
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
