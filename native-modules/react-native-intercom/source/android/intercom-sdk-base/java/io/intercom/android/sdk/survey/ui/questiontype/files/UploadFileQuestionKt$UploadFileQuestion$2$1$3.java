package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class UploadFileQuestionKt$UploadFileQuestion$2$1$3 implements Function2 {
   // $VF: synthetic field
   final SurveyData$Step$Question$UploadFileQuestionModel $questionModel;

   UploadFileQuestionKt$UploadFileQuestion$2$1$3(SurveyData$Step$Question$UploadFileQuestionModel var1) {
      this.$questionModel = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C85@3975L134:UploadFileQuestion.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2026251827,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestion.<anonymous>.<anonymous>.<anonymous> (UploadFileQuestion.kt:85)"
            );
         }

         AddFileButtonKt.AddFileButton(
            null, this.$questionModel.getMaxSelection() == 1 ? string.intercom_tickets_add_file : string.intercom_tickets_add_files, var1, 0, 1
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
