package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FinStreamingRowKt$FinStreamingRow$1$1 implements Function3 {
   // $VF: synthetic field
   final boolean $useRevealAnimation;
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;
   // $VF: synthetic field
   final Block $lastBlock;
   // $VF: synthetic field
   final boolean $isCaughtUp;

   FinStreamingRowKt$FinStreamingRow$1$1(boolean var1, FinRowStyle var2, Block var3, boolean var4) {
      this.$useRevealAnimation = var1;
      this.$finAnswerStyle = var2;
      this.$lastBlock = var3;
      this.$isCaughtUp = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Block var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "block");
      ComposerKt.sourceInformation(var2, "C:FinStreamingRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            457444246, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRow.<anonymous>.<anonymous> (FinStreamingRow.kt:73)"
         );
      }

      if (this.$useRevealAnimation) {
         var2.startReplaceGroup(-499017248);
         ComposerKt.sourceInformation(var2, "76@3428L47,74@3285L292");
         Modifier var4 = ClipKt.clip(Modifier.Companion, this.$finAnswerStyle.getContentShape());
         BlockRenderData var5 = FinStreamingRowKt.access$streamingBlockRenderData(var1, this.$finAnswerStyle, var2, 14 & var3);
         boolean var6 = var1 == this.$lastBlock && !this.$isCaughtUp;
         StreamingMessageContentKt.StreamingRevealBlock(var5, var6, var4, var2, 0, 0);
         var2.endReplaceGroup();
      } else {
         var2.startReplaceGroup(-498684742);
         ComposerKt.sourceInformation(var2, "82@3755L47,80@3623L202");
         BlockViewKt.recovered$5709084683a88fd9(
            ClipKt.clip(Modifier.Companion, this.$finAnswerStyle.getContentShape()),
            FinStreamingRowKt.access$streamingBlockRenderData(var1, this.$finAnswerStyle, var2, 14 & var3),
            false,
            null,
            false,
            null,
            null,
            0L,
            null,
            null,
            false,
            null,
            null,
            false,
            null,
            var2,
            0,
            0,
            32764
         );
         var2.endReplaceGroup();
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((Block)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
