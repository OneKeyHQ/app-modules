package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
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
   "SMAP\nShortTextQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShortTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ComposableSingletons$ShortTextQuestionKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,263:1\n1047#2,6:264\n*S KotlinDebug\n*F\n+ 1 ShortTextQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/text/ComposableSingletons$ShortTextQuestionKt$lambda-4$1\n*L\n232#1:264,6\n*E\n"
)
final class ComposableSingletons$ShortTextQuestionKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$ShortTextQuestionKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$ShortTextQuestionKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C231@10020L2,219@9474L687:ShortTextQuestion.kt#z2rnlt");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -172731134,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.text.ComposableSingletons$ShortTextQuestionKt.lambda-4.<anonymous> (ShortTextQuestion.kt:219)"
            );
         }

         Object var10000 = null;
         String var10001 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var3 = var10001;
         List var4 = CollectionsKt.listOf(new Block$Builder().withText("Enter your phone number").withType(BlockType.PARAGRAPH.getSerializedName()));
         SurveyData$Step$Question$QuestionValidation$ValidationType var5 = SurveyData$Step$Question$QuestionValidation$ValidationType.PHONE;
         SurveyData$Step$Question$ShortTextQuestionModel var18 = new SurveyData$Step$Question$ShortTextQuestionModel(
            var3, var4, true, "Placeholder text", var5, 250, false, null, null, 448, null
         );
         Object var10002 = null;
         var1.startReplaceGroup(1909196135);
         ComposerKt.sourceInformation(var1, "CC(remember):ShortTextQuestion.kt#9igjgp");
         Composer var15 = var1;
         boolean var16 = false;
         int var6 = 0;
         Object var7 = var15.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var12 = null;
            SurveyData$Step$Question$ShortTextQuestionModel var11 = var18;
            Object var10 = null;
            int var9 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1488_0) -> ComposableSingletons$ShortTextQuestionKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1488_0));
            var10000 = var10;
            var18 = var11;
            var10002 = var12;
            Object var17 = var13;
            var15.updateRememberedValue(var17);
            var10003 = var17;
         } else {
            var10003 = var7;
         }

         Function1 var14 = (Function1)var10003;
         var1.endReplaceGroup();
         ShortTextQuestionKt.ShortTextQuestion(
            (Modifier)var10000,
            var18,
            (Answer)var10002,
            var14,
            SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)),
            ValidationError$NoValidationError.INSTANCE,
            null,
            null,
            var1,
            199680,
            197
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
