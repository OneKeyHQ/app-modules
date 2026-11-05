package io.intercom.android.sdk.survey.ui.questiontype.dropdown;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nDropDownQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DropDownQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/dropdown/ComposableSingletons$DropDownQuestionKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,187:1\n1047#2,6:188\n*S KotlinDebug\n*F\n+ 1 DropDownQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/dropdown/ComposableSingletons$DropDownQuestionKt$lambda-4$1\n*L\n182#1:188,6\n*E\n"
)
final class ComposableSingletons$DropDownQuestionKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$DropDownQuestionKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$DropDownQuestionKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C181@7386L2,178@7239L249:DropDownQuestion.kt#ppg8nv");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               157266840,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.dropdown.ComposableSingletons$DropDownQuestionKt.lambda-4.<anonymous> (DropDownQuestion.kt:178)"
            );
         }

         Object var10000 = null;
         SurveyData$Step$Question$DropDownQuestionModel var10001 = DropDownQuestionKt.access$getDropDownQuestionModel$p();
         Answer var10002 = new Answer$SingleAnswer("Option A");
         var1.startReplaceGroup(-1534281769);
         ComposerKt.sourceInformation(var1, "CC(remember):DropDownQuestion.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Answer var12 = var10002;
            SurveyData$Step$Question$DropDownQuestionModel var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1416_0) -> ComposableSingletons$DropDownQuestionKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1416_0));
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var14 = var13;
            var4.updateRememberedValue(var14);
            var10003 = var14;
         } else {
            var10003 = var7;
         }

         Function1 var3 = (Function1)var10003;
         var1.endReplaceGroup();
         DropDownQuestionKt.DropDownQuestion(
            (Modifier)var10000,
            var10001,
            var10002,
            var3,
            SurveyUiColors.recovered$4e1a9ef58cd7c1b4(
               SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)),
               0L,
               0L,
               Color.Companion.recovered$ff90a8751967f2f7(),
               0L,
               null,
               27,
               null
            ),
            null,
            var1,
            3072,
            33
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Answer var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
