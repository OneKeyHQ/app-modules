package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption;",
         "value",
         "",
         "<init>",
         "(I)V",
         "getValue",
         "()I",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption
   extends SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption {
   private final int value;
   public static int $stable;

   static {}

   public SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$NumericRatingOption(int var1) {
      super(null);
      this.value = var1;
   }

   public final int getValue() {
      return this.value;
   }
}
