package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSingleChoiceQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SingleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/SingleChoiceQuestionKt$SingleChoiceQuestionPreview$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,145:1\n1047#2,6:146\n*S KotlinDebug\n*F\n+ 1 SingleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/SingleChoiceQuestionKt$SingleChoiceQuestionPreview$1\n*L\n140#1:146,6\n*E\n"
)
final class SingleChoiceQuestionKt$SingleChoiceQuestionPreview$1 implements Function2 {
   // $VF: synthetic field
   final SurveyUiColors $surveyUiColors;

   SingleChoiceQuestionKt$SingleChoiceQuestionPreview$1(SurveyUiColors var1) {
      this.$surveyUiColors = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C139@6125L2,128@5621L554:SingleChoiceQuestion.kt#w8l19x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1452787289,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.choice.SingleChoiceQuestionPreview.<anonymous> (SingleChoiceQuestion.kt:128)"
            );
         }

         Object var10000 = null;
         String var10001 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var3 = var10001;
         List var4 = CollectionsKt.listOf(new Block$Builder().withText("Question title"));
         String[] var6 = new String[]{"Option 1", "Option 2", "Option 3", "Option 4"};
         List var5 = CollectionsKt.listOf(var6);
         SurveyData$Step$Question$SingleChoiceQuestionModel var19 = new SurveyData$Step$Question$SingleChoiceQuestionModel(var3, var4, true, var5, false);
         Answer var10002 = new Answer$SingleAnswer("Option 2");
         var1.startReplaceGroup(126566529);
         ComposerKt.sourceInformation(var1, "CC(remember):SingleChoiceQuestion.kt#9igjgp");
         Composer var15 = var1;
         boolean var16 = false;
         int var17 = 0;
         Object var7 = var15.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Answer var12 = var10002;
            SurveyData$Step$Question$SingleChoiceQuestionModel var11 = var19;
            Object var10 = null;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1415_0) -> SingleChoiceQuestionKt$SingleChoiceQuestionPreview$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1415_0));
            var10000 = var10;
            var19 = var11;
            var10002 = var12;
            Object var18 = var13;
            var15.updateRememberedValue(var18);
            var10003 = var18;
         } else {
            var10003 = var7;
         }

         Function1 var14 = (Function1)var10003;
         var1.endReplaceGroup();
         SingleChoiceQuestionKt.SingleChoiceQuestion((Modifier)var10000, var19, var10002, var14, this.$surveyUiColors, null, var1, 3072, 33);
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
