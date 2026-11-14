package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.CountryAreaCode;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ShortTextQuestionKt$ShortTextQuestion$3$1$1 implements Function2 {
   // $VF: synthetic field
   final CountryAreaCode $countryAreaCode;

   ShortTextQuestionKt$ShortTextQuestion$3$1$1(CountryAreaCode var1) {
      this.$countryAreaCode = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C110@5260L34:ShortTextQuestion.kt#z2rnlt");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               854302399,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestion.<anonymous>.<anonymous>.<anonymous> (ShortTextQuestion.kt:110)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            this.$countryAreaCode.getEmoji(), null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var1, 0, 0, 262142
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
