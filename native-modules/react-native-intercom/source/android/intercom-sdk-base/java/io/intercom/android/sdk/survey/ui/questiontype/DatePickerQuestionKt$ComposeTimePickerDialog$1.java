package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TimePickerState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.ui.models.Answer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDatePickerQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeTimePickerDialog$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,329:1\n118#2:330\n*S KotlinDebug\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeTimePickerDialog$1\n*L\n248#1:330\n*E\n"
)
final class DatePickerQuestionKt$ComposeTimePickerDialog$1 implements Function2 {
   // $VF: synthetic field
   final TimePickerState $timePickerState;
   // $VF: synthetic field
   final Function0 $onDismiss;
   // $VF: synthetic field
   final Answer $answer;
   // $VF: synthetic field
   final Function1 $onAnswer;

   DatePickerQuestionKt$ComposeTimePickerDialog$1(TimePickerState var1, Function0 var2, Answer var3, Function1 var4) {
      this.$timePickerState = var1;
      this.$onDismiss = var2;
      this.$answer = var3;
      this.$onAnswer = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C246@9087L6,249@9199L1857,245@9044L2012:DatePickerQuestion.kt#uw1bh6");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1529528875, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.ComposeTimePickerDialog.<anonymous> (DatePickerQuestion.kt:245)"
            );
         }

         CornerBasedShape var3 = MaterialTheme.INSTANCE.getShapes(var1, MaterialTheme.$stable).getExtraLarge();
         int var5 = 6;
         int var6 = 0;
         float var4 = Dp.recovered$95b3d103663bf060(var5);
         SurfaceKt.recovered$ecd79a99e04a9534(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            var3,
            0L,
            0L,
            var4,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               -1066676752,
               true,
               new DatePickerQuestionKt$ComposeTimePickerDialog$1$1(this.$timePickerState, this.$onDismiss, this.$answer, this.$onAnswer),
               var1,
               54
            ),
            var1,
            12607494,
            108
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
