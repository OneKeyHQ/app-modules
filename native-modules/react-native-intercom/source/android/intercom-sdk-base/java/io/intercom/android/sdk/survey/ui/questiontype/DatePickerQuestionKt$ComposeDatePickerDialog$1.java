package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.DatePickerState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import io.intercom.android.sdk.utilities.TimeFormatter;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDatePickerQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeDatePickerDialog$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,329:1\n1047#2,6:330\n*S KotlinDebug\n*F\n+ 1 DatePickerQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/DatePickerQuestionKt$ComposeDatePickerDialog$1\n*L\n140#1:330,6\n*E\n"
)
final class DatePickerQuestionKt$ComposeDatePickerDialog$1 implements Function2 {
   // $VF: synthetic field
   final DatePickerState $datePickerState;
   // $VF: synthetic field
   final Answer $answer;
   // $VF: synthetic field
   final Function1 $onAnswer;
   // $VF: synthetic field
   final Function0 $onDismiss;

   DatePickerQuestionKt$ComposeDatePickerDialog$1(DatePickerState var1, Answer var2, Function1 var3, Function0 var4) {
      this.$datePickerState = var1;
      this.$answer = var2;
      this.$onAnswer = var3;
      this.$onDismiss = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C139@5401L828,139@5380L930:DatePickerQuestion.kt#uw1bh6");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               308986865, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.ComposeDatePickerDialog.<anonymous> (DatePickerQuestion.kt:139)"
            );
         }

         var1.startReplaceGroup(312998685);
         ComposerKt.sourceInformation(var1, "CC(remember):DatePickerQuestion.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changed(this.$datePickerState) | var1.changed(this.$answer) | var1.changed(this.$onAnswer) | var1.changed(this.$onDismiss);
         DatePickerState var6 = this.$datePickerState;
         Answer var7 = this.$answer;
         Function1 var8 = this.$onAnswer;
         Function0 var9 = this.$onDismiss;
         int var10 = 0;
         Object var11 = var4.rememberedValue();
         int var12 = 0;
         Object var10000;
         if (!var5 && var11 != Composer.Companion.getEmpty()) {
            var10000 = var11;
         } else {
            int var13 = 0;
            final androidx.compose.material3.DatePickerState recoveredCapture_1385_0 = var6;
            final io.intercom.android.sdk.survey.ui.models.Answer recoveredCapture_1385_1 = var7;
            final kotlin.jvm.functions.Function1 recoveredCapture_1385_2 = var8;
            final kotlin.jvm.functions.Function0 recoveredCapture_1385_3 = var9;
            Object var14 = ((kotlin.jvm.functions.Function0) () -> DatePickerQuestionKt$ComposeDatePickerDialog$1.invoke$lambda$1$lambda$0(recoveredCapture_1385_0, recoveredCapture_1385_1, recoveredCapture_1385_2, recoveredCapture_1385_3));
            var4.updateRememberedValue(var14);
            var10000 = var14;
         }

         Function0 var3 = (Function0)var10000;
         var1.endReplaceGroup();
         ButtonKt.TextButton(
            var3,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableSingletons$DatePickerQuestionKt.INSTANCE.recovered$6532409fab43c00f(),
            var1,
            805306368,
            510
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(DatePickerState var0, Answer var1, Function1 var2, Function0 var3) {
      Long var4 = var0.getSelectedDateMillis();
      if (var4 != null) {
         Answer$DateTimeAnswer var10000;
         if (var1 instanceof Answer$DateTimeAnswer) {
            var10000 = Answer$DateTimeAnswer.copy$default((Answer$DateTimeAnswer)var1, var4, 0, 0, 6, null);
         } else {
            long var6 = Injector.get().getTimeProvider().currentTimeMillis();
            List var8 = DatePickerQuestionKt.access$getUtcTime(TimeFormatter.getHour(var6), TimeFormatter.getMinute(var6));
            var10000 = new Answer$DateTimeAnswer(var4, Integer.parseInt((String)var8.get(0)), Integer.parseInt((String)var8.get(1)));
         }

         Answer$DateTimeAnswer var5 = var10000;
         var2.invoke(var5);
      } else {
         var3.invoke();
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
