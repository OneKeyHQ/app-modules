package io.intercom.android.sdk.survey.ui.questiontype.text;

import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ShortTextQuestionKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[SurveyData$Step$Question$QuestionValidation$ValidationType.values().length];

      try {
         var0[SurveyData$Step$Question$QuestionValidation$ValidationType.NUMBER.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionValidation$ValidationType.PHONE.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[SurveyData$Step$Question$QuestionValidation$ValidationType.FLOAT.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
