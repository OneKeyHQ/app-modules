package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class BlockViewKt$BlockView$textBlock$1 implements Function2 {
   // $VF: synthetic field
   final Modifier $modifier;
   // $VF: synthetic field
   final BlockRenderData $blockRenderData;
   // $VF: synthetic field
   final SuffixText $suffixText;
   // $VF: synthetic field
   final boolean $inlineSourcesVisible;
   // $VF: synthetic field
   final Function1 $onInlineSourcesClick;
   // $VF: synthetic field
   final Function1 $onTextLayout;

   BlockViewKt$BlockView$textBlock$1(Modifier var1, BlockRenderData var2, SuffixText var3, boolean var4, Function1 var5, Function1 var6) {
      this.$modifier = var1;
      this.$blockRenderData = var2;
      this.$suffixText = var3;
      this.$inlineSourcesVisible = var4;
      this.$onInlineSourcesClick = var5;
      this.$onTextLayout = var6;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C69@3020L320:BlockView.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1601042320, var2, -1, "io.intercom.android.sdk.survey.block.BlockView.<anonymous> (BlockView.kt:69)");
         }

         TextBlockKt.TextBlock(
            this.$modifier, this.$blockRenderData, this.$suffixText, this.$inlineSourcesVisible, this.$onInlineSourcesClick, this.$onTextLayout, var1, 0, 0
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
