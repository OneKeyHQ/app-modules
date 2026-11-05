package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1$1 implements Function3 {
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;

   ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1$1(FinRowStyle var1) {
      this.$finAnswerStyle = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Block var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "block");
      ComposerKt.sourceInformation(var2, "C145@6519L45,143@6395L188:SegmentedFinLayout.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -379625014,
            var3,
            -1,
            "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$SegmentedFinLayoutKt.lambda-2.<anonymous>.<anonymous> (SegmentedFinLayout.kt:143)"
         );
      }

      BlockViewKt.recovered$5709084683a88fd9(
         ClipKt.clip(Modifier.Companion, this.$finAnswerStyle.getContentShape()),
         SegmentedFinLayoutKt.access$previewBlockRenderData(var1, this.$finAnswerStyle, var2, 14 & var3),
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
