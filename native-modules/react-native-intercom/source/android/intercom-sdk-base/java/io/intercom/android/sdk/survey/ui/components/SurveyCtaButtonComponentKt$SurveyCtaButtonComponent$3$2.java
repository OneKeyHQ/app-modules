package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$2 implements Function3 {
   // $VF: synthetic field
   final String $primaryCtaText;
   // $VF: synthetic field
   final long $textColor;

   SurveyCtaButtonComponentKt$SurveyCtaButtonComponent$3$2(String var1, long var2) {
      this.$primaryCtaText = var1;
      this.$textColor = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Button");
      ComposerKt.sourceInformation(var2, "C104@4081L93:SurveyCtaButtonComponent.kt#bmpoeo");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               767351755,
               var3,
               -1,
               "io.intercom.android.sdk.survey.ui.components.SurveyCtaButtonComponent.<anonymous>.<anonymous> (SurveyCtaButtonComponent.kt:104)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            this.$primaryCtaText, null, this.$textColor, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var2, 0, 0, 262138
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
