package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionData$QuestionSubType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class NumericRatingQuestionKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[SurveyData$Step$Question$QuestionData$QuestionSubType.values().length];

      try {
         var0[SurveyData$Step$Question$QuestionData$QuestionSubType.NPS.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionData$QuestionSubType.UNSUPPORTED.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionData$QuestionSubType.NUMERIC.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionData$QuestionSubType.STARS.ordinal()] = 4;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionData$QuestionSubType.EMOJI.ordinal()] = 5;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
