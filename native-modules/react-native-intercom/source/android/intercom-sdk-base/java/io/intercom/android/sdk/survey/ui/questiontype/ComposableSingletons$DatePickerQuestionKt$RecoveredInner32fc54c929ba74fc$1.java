package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$DatePickerQuestionKt$RecoveredInner32fc54c929ba74fc$1 implements Function2 {
   public static final ComposableSingletons$DatePickerQuestionKt$RecoveredInner32fc54c929ba74fc$1 INSTANCE = new ComposableSingletons$DatePickerQuestionKt$RecoveredInner32fc54c929ba74fc$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C313@11525L466:DatePickerQuestion.kt#uw1bh6");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               450290332,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.ComposableSingletons$DatePickerQuestionKt.lambda-7.<anonymous> (DatePickerQuestion.kt:313)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$5a1850a8ff371bfb(), var1, 12582912, 127
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
