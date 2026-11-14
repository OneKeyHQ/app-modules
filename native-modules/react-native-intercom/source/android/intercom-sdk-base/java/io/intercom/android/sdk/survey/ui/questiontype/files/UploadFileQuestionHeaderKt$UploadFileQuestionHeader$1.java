package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.component.ErrorMessageLayoutKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class UploadFileQuestionHeaderKt$UploadFileQuestionHeader$1 implements Function2 {
   // $VF: synthetic field
   final List $errorMessages;

   UploadFileQuestionHeaderKt$UploadFileQuestionHeader$1(List var1) {
      this.$errorMessages = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C33@1283L49:UploadFileQuestionHeader.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1052404026,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFileQuestionHeader.<anonymous> (UploadFileQuestionHeader.kt:33)"
            );
         }

         ErrorMessageLayoutKt.ErrorMessageLayout(null, this.$errorMessages, var1, 0, 1);
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
