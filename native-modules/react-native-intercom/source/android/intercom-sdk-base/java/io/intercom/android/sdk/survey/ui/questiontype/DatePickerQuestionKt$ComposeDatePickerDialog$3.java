package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.DatePickerKt;
import androidx.compose.material3.DatePickerState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDatePickerQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeDatePickerDialog$3\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,329:1\n118#2:330\n*S KotlinDebug\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeDatePickerDialog$3\n*L\n169#1:330\n*E\n"
)
final class DatePickerQuestionKt$ComposeDatePickerDialog$3 implements Function3 {
   // $VF: synthetic field
   final DatePickerState $datePickerState;

   DatePickerQuestionKt$ComposeDatePickerDialog$3(DatePickerState var1) {
      this.$datePickerState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$DatePickerDialog");
      ComposerKt.sourceInformation(var2, "C166@6503L106:DatePickerQuestion.kt#uw1bh6");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -725444728, var3, -1, "io.intercom.android.sdk.survey.ui.questiontype.ComposeDatePickerDialog.<anonymous> (DatePickerQuestion.kt:166)"
            );
         }

         Modifier var10001 = Modifier.Companion;
         int var4 = 16;
         int var5 = 0;
         DatePickerKt.DatePicker(
            this.$datePickerState,
            PaddingKt.recovered$eb6a744cd98cdbca(var10001, Dp.recovered$95b3d103663bf060(var4)),
            null,
            null,
            null,
            null,
            false,
            null,
            var2,
            48,
            252
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
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
