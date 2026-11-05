package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TextBlockKt$OrderedListWithEmptyItemTextBlockPreview$1$1 implements Function2 {
   // $VF: synthetic field
   final Block $block;

   TextBlockKt$OrderedListWithEmptyItemTextBlockPreview$1$1(Block var1) {
      this.$block = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C498@16932L51:TextBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -403764057, var2, -1, "io.intercom.android.sdk.survey.block.OrderedListWithEmptyItemTextBlockPreview.<anonymous>.<anonymous> (TextBlock.kt:498)"
            );
         }

         Block var3 = this.$block;
         Intrinsics.checkNotNull(var3);
         TextBlockKt.TextBlock(null, new BlockRenderData(var3, null, null, null, null, 30, null), null, false, null, null, var1, 0, 61);
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
