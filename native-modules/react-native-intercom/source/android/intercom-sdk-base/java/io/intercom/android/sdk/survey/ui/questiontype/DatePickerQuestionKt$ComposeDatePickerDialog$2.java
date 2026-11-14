package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class DatePickerQuestionKt$ComposeDatePickerDialog$2 implements Function2 {
   // $VF: synthetic field
   final Function0 $onDismiss;

   DatePickerQuestionKt$ComposeDatePickerDialog$2(Function0 var1) {
      this.$onDismiss = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C161@6360L115:DatePickerQuestion.kt#uw1bh6");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1712856051, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.ComposeDatePickerDialog.<anonymous> (DatePickerQuestion.kt:161)"
            );
         }

         ButtonKt.TextButton(
            this.$onDismiss,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$030ea852b673257f(),
            var1,
            805306368,
            510
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
