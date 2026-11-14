package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FinAnswerRowKt$FinAnswerRow$3 implements Function5 {
   // $VF: synthetic field
   final SenderMetadata $senderMetadata;
   // $VF: synthetic field
   final FinRowStyle $finAnswerStyle;
   // $VF: synthetic field
   final Function1 $onInlineSourcesClick;
   // $VF: synthetic field
   final boolean $showReportAiAnswerButton;
   // $VF: synthetic field
   final Function0 $onReportAiAnswer;

   FinAnswerRowKt$FinAnswerRow$3(SenderMetadata var1, FinRowStyle var2, Function1 var3, boolean var4, Function0 var5) {
      this.$senderMetadata = var1;
      this.$finAnswerStyle = var2;
      this.$onInlineSourcesClick = var3;
      this.$showReportAiAnswerButton = var4;
      this.$onReportAiAnswer = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Part var2, Function0 var3, Composer var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "$this$ClickableMessageRow");
      Intrinsics.checkNotNullParameter(var2, "part");
      Intrinsics.checkNotNullParameter(var3, "onClick");
      ComposerKt.sourceInformation(var4, "CP(1)93@4288L361:FinAnswerRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            1035960646, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRow.<anonymous> (FinAnswerRow.kt:93)"
         );
      }

      boolean var6 = this.$senderMetadata != null;
      FinAnswerRowKt.access$SegmentedFinContent(
         var2,
         this.$finAnswerStyle,
         var3,
         this.$onInlineSourcesClick,
         this.$showReportAiAnswerButton,
         this.$onReportAiAnswer,
         var6,
         var4,
         14 & var5 >> 3 | 896 & var5,
         0
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4, Object var5) {
      this.invoke((ColumnScope)var1, (Part)var2, (Function0)var3, (Composer)var4, ((Number)var5).intValue());
      return Unit.INSTANCE;
   }
}
