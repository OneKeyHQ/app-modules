package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.ValidationError;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.ui.common.StringProvider;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class QuestionComponentKt$QuestionComponent$questionHeader$1 implements Function2 {
   // $VF: synthetic field
   final QuestionState $questionState;
   // $VF: synthetic field
   final FontWeight $questionFontWeight;
   // $VF: synthetic field
   final long $questionFontSize;

   QuestionComponentKt$QuestionComponent$questionHeader$1(QuestionState var1, FontWeight var2, long var3) {
      this.$questionState = var1;
      this.$questionFontWeight = var2;
      this.$questionFontSize = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C76@4041L462:QuestionComponent.kt#bmpoeo");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1322549775, var2, -1, "io.intercom.android.sdk.survey.ui.components.QuestionComponent.<anonymous> (QuestionComponent.kt:76)"
            );
         }

         List var10000 = this.$questionState.getQuestionModel().getTitle();
         StringProvider var10001 = this.$questionState.getQuestionModel().getDescription();
         boolean var10002 = this.$questionState.getQuestionModel().isRequired();
         ValidationError var10003 = this.$questionState.getValidationError();
         FontWeight var10004 = this.$questionFontWeight;
         long var10005 = this.$questionFontSize;
         SurveyData$Step$Question$QuestionModel var3 = this.$questionState.getQuestionModel();
         QuestionHeaderComponentKt.recovered$5a429c657b5a276d(
            var10000,
            var10001,
            var10002,
            var10003,
            var10004,
            var10005,
            null,
            (var3 instanceof SurveyData$Step$Question$ShortTextQuestionModel ? (SurveyData$Step$Question$ShortTextQuestionModel)var3 : null) != null
               ? (var3 instanceof SurveyData$Step$Question$ShortTextQuestionModel ? (SurveyData$Step$Question$ShortTextQuestionModel)var3 : null)
                  .getTitleStringRes()
               : null,
            var1,
            StringProvider.$stable << 3,
            64
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
