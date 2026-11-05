package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Shape;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FinAnswerRowKt$SegmentedFinContent$2 implements Function4 {
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final Function1 $onInlineSourcesClick;

   FinAnswerRowKt$SegmentedFinContent$2(FinRowStyle var1, Function0 var2, Function1 var3) {
      this.$finAnswerStyle = var1;
      this.$onClick = var2;
      this.$onInlineSourcesClick = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Block var1, Shape var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "block");
      Intrinsics.checkNotNullParameter(var2, "shape");
      ComposerKt.sourceInformation(var3, "C158@6948L243:FinAnswerRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            214045783, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinContent.<anonymous> (FinAnswerRow.kt:158)"
         );
      }

      FinAnswerRowKt.access$FinBlockView(var1, this.$finAnswerStyle, this.$onClick, this.$onInlineSourcesClick, var2, var3, 14 & var4 | 57344 & var4 << 9, 0);
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
