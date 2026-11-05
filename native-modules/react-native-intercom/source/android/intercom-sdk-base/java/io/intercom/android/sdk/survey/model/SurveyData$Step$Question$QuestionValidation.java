package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001:\u0001\u0012B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\n\u001a\u00020\u0003HÂ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u000f\u001a\u00020\u0003H×\u0001J\t\u0010\u0010\u001a\u00020\u0011H×\u0001R\u0010\u0010\u0002\u001a\u00020\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u00078F¢\u0006\u0006\u001a\u0004\b\b\u0010\t¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation;",
         "",
         "_type",
         "",
         "<init>",
         "(I)V",
         "validationType",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "getValidationType",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "",
         "ValidationType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyData$Step$Question$QuestionValidation {
   @SerializedName("type")
   private final int _type;
   public static int $stable;

   static {}

   public SurveyData$Step$Question$QuestionValidation(int var1) {
      this._type = var1;
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionValidation$ValidationType getValidationType() {
      int var1 = this._type;
      return var1 == SurveyData$Step$Question$QuestionValidation$ValidationType.TEXT.ordinal()
         ? SurveyData$Step$Question$QuestionValidation$ValidationType.TEXT
         : (
            var1 == SurveyData$Step$Question$QuestionValidation$ValidationType.NUMBER.ordinal()
               ? SurveyData$Step$Question$QuestionValidation$ValidationType.NUMBER
               : (
                  var1 == SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL.ordinal()
                     ? SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL
                     : (
                        var1 == SurveyData$Step$Question$QuestionValidation$ValidationType.PHONE.ordinal()
                           ? SurveyData$Step$Question$QuestionValidation$ValidationType.PHONE
                           : (
                              var1 == SurveyData$Step$Question$QuestionValidation$ValidationType.FLOAT.ordinal()
                                 ? SurveyData$Step$Question$QuestionValidation$ValidationType.FLOAT
                                 : (
                                    var1 == SurveyData$Step$Question$QuestionValidation$ValidationType.INTEGER.ordinal()
                                       ? SurveyData$Step$Question$QuestionValidation$ValidationType.INTEGER
                                       : SurveyData$Step$Question$QuestionValidation$ValidationType.NO_VALIDATION
                                 )
                           )
                     )
               )
         );
   }

   private final int component1() {
      return this._type;
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionValidation copy(int var1) {
      return new SurveyData$Step$Question$QuestionValidation(var1);
   }

   // $VF: synthetic method
   public static SurveyData$Step$Question$QuestionValidation copy$default(SurveyData$Step$Question$QuestionValidation var0, int var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0._type;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "QuestionValidation(_type=" + this._type + ')';
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(this._type);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SurveyData$Step$Question$QuestionValidation)) {
         return false;
      }

      SurveyData$Step$Question$QuestionValidation var2 = (SurveyData$Step$Question$QuestionValidation)var1;
      return this._type == var2._type;
   }
}
