package io.intercom.android.sdk.survey.ui.questiontype.dropdown;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class DropDownQuestionKt$DropDownQuestion$1$1$1$4$2$1 implements Function2 {
   // $VF: synthetic field
   final String $optionText;

   DropDownQuestionKt$DropDownQuestion$1$1$1$4$2$1(String var1) {
      this.$optionText = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C125@5860L10,125@5814L64:DropDownQuestion.kt#ppg8nv");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -864903633,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.dropdown.DropDownQuestion.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (DropDownQuestion.kt:125)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            this.$optionText,
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            IntercomTheme.INSTANCE.getTypography(var1, IntercomTheme.$stable).getType04(),
            var1,
            0,
            0,
            131070
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
