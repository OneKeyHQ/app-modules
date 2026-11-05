package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.survey.SurveyState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SurveyComponentKt$SurveyComponent$3$1 implements Function2 {
   // $VF: synthetic field
   final SurveyState $state;
   // $VF: synthetic field
   final Function0 $onClose;

   SurveyComponentKt$SurveyComponent$3$1(SurveyState var1, Function0 var2) {
      this.$state = var1;
      this.$onClose = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C94@4498L184:SurveyComponent.kt#bmpoeo");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -365860662, var2, -1, "io.intercom.android.sdk.survey.ui.components.SurveyComponent.<anonymous>.<anonymous> (SurveyComponent.kt:94)"
            );
         }

         Modifier var3 = WindowInsetsPadding_androidKt.systemBarsPadding(Modifier.Companion);
         SurveyTopBarComponentKt.SurveyTopBar(this.$state.getTopBarState(), this.$onClose, var3, var1, 0, 0);
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
