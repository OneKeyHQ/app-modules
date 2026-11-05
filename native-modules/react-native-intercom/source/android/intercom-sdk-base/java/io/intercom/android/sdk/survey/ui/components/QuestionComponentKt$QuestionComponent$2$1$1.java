package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionHeaderKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class QuestionComponentKt$QuestionComponent$2$1$1 implements Function2 {
   // $VF: synthetic field
   final QuestionState $questionState;
   // $VF: synthetic field
   final FontWeight $questionFontWeight;
   // $VF: synthetic field
   final long $questionFontSize;

   QuestionComponentKt$QuestionComponent$2$1$1(QuestionState var1, FontWeight var2, long var3) {
      this.$questionState = var1;
      this.$questionFontWeight = var2;
      this.$questionFontSize = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C186@8637L188:QuestionComponent.kt#bmpoeo");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1590070470,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.components.QuestionComponent.<anonymous>.<anonymous>.<anonymous> (QuestionComponent.kt:186)"
            );
         }

         UploadFileQuestionHeaderKt.recovered$31647a00fc08b5cc(this.$questionState, this.$questionFontWeight, this.$questionFontSize, var1, 0);
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
