package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FinAnswerRowKt$SegmentedFinContent$1 implements Function3 {
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final Function1 $onInlineSourcesClick;

   FinAnswerRowKt$SegmentedFinContent$1(FinRowStyle var1, Function0 var2, Function1 var3) {
      this.$finAnswerStyle = var1;
      this.$onClick = var2;
      this.$onInlineSourcesClick = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Block var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "block");
      ComposerKt.sourceInformation(var2, "C150@6677L203:FinAnswerRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            1519437155, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinContent.<anonymous> (FinAnswerRow.kt:150)"
         );
      }

      FinAnswerRowKt.access$FinBlockView(var1, this.$finAnswerStyle, this.$onClick, this.$onInlineSourcesClick, null, var2, 14 & var3, 16);
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
