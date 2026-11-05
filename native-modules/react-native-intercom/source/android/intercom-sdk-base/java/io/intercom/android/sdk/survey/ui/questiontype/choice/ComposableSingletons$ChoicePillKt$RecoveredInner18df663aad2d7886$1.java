package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ChoicePillKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$ChoicePillKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$ChoicePillKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C117@3527L726:ChoicePill.kt#w8l19x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2037921924,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.choice.ComposableSingletons$ChoicePillKt.lambda-4.<anonymous> (ChoicePill.kt:117)"
            );
         }

         long var3 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)).recovered$923dd289d74a1df8();
         ChoicePillKt.recovered$b36a01a674e93ba3(
            true,
            null,
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
            0L,
            0.0F,
            var3,
            null,
            0L,
            var1,
            390,
            218
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
