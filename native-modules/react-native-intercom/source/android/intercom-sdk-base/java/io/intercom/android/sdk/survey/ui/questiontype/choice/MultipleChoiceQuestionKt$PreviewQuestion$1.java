package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$MultipleChoiceQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer$OtherAnswer$SelectedNoText;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMultipleChoiceQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/MultipleChoiceQuestionKt$PreviewQuestion$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,189:1\n1047#2,6:190\n*S KotlinDebug\n*F\n+ 1 MultipleChoiceQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/MultipleChoiceQuestionKt$PreviewQuestion$1\n*L\n184#1:190,6\n*E\n"
)
final class MultipleChoiceQuestionKt$PreviewQuestion$1 implements Function2 {
   // $VF: synthetic field
   final SurveyUiColors $surveyUiColors;

   MultipleChoiceQuestionKt$PreviewQuestion$1(SurveyUiColors var1) {
      this.$surveyUiColors = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C183@7663L2,161@6839L874:MultipleChoiceQuestion.kt#w8l19x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -245477028, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.choice.PreviewQuestion.<anonymous> (MultipleChoiceQuestion.kt:161)"
            );
         }

         Object var10000 = null;
         String var10001 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var3 = var10001;
         List var4 = CollectionsKt.listOf(new Block$Builder().withText("Question Title"));
         String[] var6 = new String[]{"Option A", "Option B", "Option C", "Option D", "Option E"};
         List var5 = CollectionsKt.listOf(var6);
         SurveyData$Step$Question$MultipleChoiceQuestionModel var20 = new SurveyData$Step$Question$MultipleChoiceQuestionModel(
            var3, var4, true, var5, true, 2, 3
         );
         String[] var14 = new String[]{"Option B", "Option D"};
         Answer var10002 = new Answer$MultipleAnswer(SetsKt.setOf(var14), Answer$MultipleAnswer$OtherAnswer$SelectedNoText.INSTANCE);
         var1.startReplaceGroup(70981782);
         ComposerKt.sourceInformation(var1, "CC(remember):MultipleChoiceQuestion.kt#9igjgp");
         Composer var16 = var1;
         boolean var17 = false;
         int var18 = 0;
         Object var7 = var16.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Answer var12 = var10002;
            SurveyData$Step$Question$MultipleChoiceQuestionModel var11 = var20;
            Object var10 = null;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1404_0) -> MultipleChoiceQuestionKt$PreviewQuestion$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1404_0));
            var10000 = var10;
            var20 = var11;
            var10002 = var12;
            Object var19 = var13;
            var16.updateRememberedValue(var19);
            var10003 = var19;
         } else {
            var10003 = var7;
         }

         Function1 var15 = (Function1)var10003;
         var1.endReplaceGroup();
         MultipleChoiceQuestionKt.MultipleChoiceQuestion((Modifier)var10000, var20, var10002, var15, this.$surveyUiColors, null, var1, 3072, 33);
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
