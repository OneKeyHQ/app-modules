package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType;
import io.intercom.android.sdk.survey.ui.models.Answer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nNumericRatingQuestion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NumericRatingQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/NumericRatingQuestionKt$GeneratePreview$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,237:1\n1563#2:238\n1634#2,3:239\n1047#3,6:242\n*S KotlinDebug\n*F\n+ 1 NumericRatingQuestion.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/NumericRatingQuestionKt$GeneratePreview$1\n*L\n228#1:238\n228#1:239,3\n232#1:242,6\n*E\n"
)
final class NumericRatingQuestionKt$GeneratePreview$1 implements Function2 {
   // $VF: synthetic field
   final SurveyData$Step$Question$QuestionData$QuestionSubType $questionSubType;
   // $VF: synthetic field
   final int $start;
   // $VF: synthetic field
   final int $end;
   // $VF: synthetic field
   final Answer $answer;

   NumericRatingQuestionKt$GeneratePreview$1(SurveyData$Step$Question$QuestionData$QuestionSubType var1, int var2, int var3, Answer var4) {
      this.$questionSubType = var1;
      this.$start = var2;
      this.$end = var3;
      this.$answer = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C231@11221L2,207@9714L1583:NumericRatingQuestion.kt#hdztyp");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2103292486, var2, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.GeneratePreview.<anonymous> (NumericRatingQuestion.kt:207)"
            );
         }

         Object var10000 = null;
         String var10001 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var3 = var10001;
         List var4 = CollectionsKt.listOf(new Block$Builder().withText("How cool is this preview?"));
         List var30;
         if (NumericRatingQuestionKt$GeneratePreview$1$WhenMappings.$EnumSwitchMapping$0[this.$questionSubType.ordinal()] == 1) {
            SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption[] var24 = new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption[]{
               new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption(
                  1, "https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83d\ude16"
               ),
               new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption(
                  2, "https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83d\ude15"
               ),
               new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption(
                  3, "https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83d\ude10"
               ),
               new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption(
                  4, "https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83d\ude03"
               ),
               new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption(
                  5, "https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83e\udd29"
               )
            };
            var30 = CollectionsKt.listOf(var24);
         } else {
            Iterable var8 = new IntRange(this.$start, this.$end);
            Object var17 = null;
            int var9 = 0;
            Iterable var10 = var8;
            ArrayList var11 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var8, 10));
            int var12 = 0;
            Iterator var13 = var10.iterator();

            while (var13.hasNext()) {
               int var14 = ((IntIterator)var13).nextInt();
               int var15 = var14;
               Collection var18 = var11;
               int var16 = 0;
               var18.add(new SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption(var15));
            }

            var30 = (List)var11;
            var10000 = var17;
         }

         List var5 = var30;
         SurveyData$Step$Question$NumericRatingQuestionModel var31 = new SurveyData$Step$Question$NumericRatingQuestionModel(
            var3, var4, true, var5, "Not likely", "Very likely", this.$start, this.$end, this.$questionSubType
         );
         Answer var10002 = this.$answer;
         var1.startReplaceGroup(2033387031);
         ComposerKt.sourceInformation(var1, "CC(remember):NumericRatingQuestion.kt#9igjgp");
         Composer var22 = var1;
         boolean var23 = false;
         int var6 = 0;
         Object var7 = var22.rememberedValue();
         int var25 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Answer var19 = var10002;
            SurveyData$Step$Question$NumericRatingQuestionModel var29 = var31;
            Object var28 = var10000;
            int var26 = 0;
            Function1 var20 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1482_0) -> NumericRatingQuestionKt$GeneratePreview$1.invoke$lambda$2$lambda$1((io.intercom.android.sdk.survey.ui.models.Answer)recoveredArgument_1482_0));
            var10000 = var28;
            var31 = var29;
            var10002 = var19;
            Object var27 = var20;
            var22.updateRememberedValue(var27);
            var10003 = var27;
         } else {
            var10003 = var7;
         }

         Function1 var21 = (Function1)var10003;
         var1.endReplaceGroup();
         NumericRatingQuestionKt.NumericRatingQuestion(
            (Modifier)var10000, var31, var10002, var21, SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), null, var1, 3072, 33
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1(Answer var0) {
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
