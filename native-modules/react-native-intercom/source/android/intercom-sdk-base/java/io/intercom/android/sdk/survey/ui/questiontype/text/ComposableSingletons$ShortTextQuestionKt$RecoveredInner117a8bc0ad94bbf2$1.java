package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
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
   "SMAP\nShortTextQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShortTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ComposableSingletons$ShortTextQuestionKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,263:1\n1047#2,6:264\n*S KotlinDebug\n*F\n+ 1 ShortTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ComposableSingletons$ShortTextQuestionKt$lambda-5$1\n*L\n256#1:264,6\n*E\n"
)
final class ComposableSingletons$ShortTextQuestionKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$ShortTextQuestionKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$ShortTextQuestionKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C255@10865L2,242@10283L768:ShortTextQuestion.kt#z2rnlt");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               34656827,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.lambda-5.<anonymous> (ShortTextQuestion.kt:242)"
            );
         }

         String var10000 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
         String var4 = var10000;
         List var5 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText("Is this a preview?").withType(BlockType.PARAGRAPH.getSerializedName()));
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType var6 = io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION;
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel var3 = new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel(var4, var5, true, "Placeholder text", var6, 250, false, null, null, 384, null);
         SurveyUiColors var18 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         io.intercom.android.sdk.survey.ValidationError$NoValidationError var19 = io.intercom.android.sdk.survey.ValidationError$NoValidationError.INSTANCE;
         io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer var20 = new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer("Answer");
         Object var22 = null;
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel var10001 = var3;
         Answer var10002 = var20;
         var1.startReplaceGroup(-878192312);
         ComposerKt.sourceInformation(var1, "CC(remember):ShortTextQuestion.kt#9igjgp");
         Composer var8 = var1;
         boolean var9 = false;
         int var10 = 0;
         Object var11 = var8.rememberedValue();
         int var12 = 0;
         Object var10003;
         if (var11 == Composer.Companion.getEmpty()) {
            Answer var16 = var10002;
            io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel var15 = var3;
            Object var14 = null;
            int var13 = 0;
            Function1 var17 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1487_0) -> io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer) ((io.intercom.android.sdk.survey.ui.models.Answer)recoveredExpressionArgument_1487_0)));
            var22 = var14;
            var10001 = var15;
            var10002 = var16;
            Object var21 = var17;
            var8.updateRememberedValue(var21);
            var10003 = var21;
         } else {
            var10003 = var11;
         }

         Function1 var7 = (Function1)var10003;
         var1.endReplaceGroup();
         ShortTextQuestionKt.ShortTextQuestion((Modifier)var22, var10001, var10002, var7, var18, var19, null, null, var1, 199680, 193);
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
