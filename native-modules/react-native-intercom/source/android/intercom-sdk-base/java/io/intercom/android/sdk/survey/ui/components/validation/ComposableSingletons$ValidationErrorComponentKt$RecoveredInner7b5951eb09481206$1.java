package io.intercom.android.sdk.survey.ui.components.validation;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.ValidationError$ValidationStringError;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ValidationErrorComponentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ValidationErrorComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ValidationErrorComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C61@2242L6,59@2075L190:ValidationErrorComponent.kt#x5xaaf");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               39859331,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.components.validation.ComposableSingletons$ValidationErrorComponentKt.lambda-1.<anonymous> (ValidationErrorComponent.kt:59)"
            );
         }

         ValidationErrorComponentKt.recovered$f274ad548aeaeedc(
            null,
            new ValidationError$ValidationStringError(string.intercom_inbox_error_state_title, null, 2, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$2bcfe565407cdfb8(),
            var1,
            0,
            1
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
