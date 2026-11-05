package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$DatePickerQuestionKt$RecoveredInner18df663aad2d7886$1 implements Function3 {
   public static final ComposableSingletons$DatePickerQuestionKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$DatePickerQuestionKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TextButton");
      ComposerKt.sourceInformation(var2, "C266@9914L45,266@9909L51:DatePickerQuestion.kt#uw1bh6");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1146728825,
               var3,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.ComposableSingletons$DatePickerQuestionKt.lambda-4.<anonymous> (DatePickerQuestion.kt:266)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(string.intercom_cancel, var2, 0),
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
            null,
            var2,
            0,
            0,
            262142
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
