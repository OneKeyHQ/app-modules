package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Shape;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1$2 implements Function4 {
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;

   ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1$2(FinRowStyle var1) {
      this.$finAnswerStyle = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Block var1, Shape var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "block");
      Intrinsics.checkNotNullParameter(var2, "shape");
      ComposerKt.sourceInformation(var3, "C150@6712L45,149@6663L157:SegmentedFinLayout.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            767924054,
            var4,
            -1,
            "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$SegmentedFinLayoutKt.lambda-2.<anonymous>.<anonymous> (SegmentedFinLayout.kt:149)"
         );
      }

      BlockViewKt.recovered$5709084683a88fd9(
         null,
         SegmentedFinLayoutKt.access$previewBlockRenderData(var1, this.$finAnswerStyle, var3, 14 & var4),
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
         var2,
         false,
         null,
         var3,
         0,
         896 & var4 << 3,
         28669
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((Block)var1, (Shape)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
