package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u0007\u0018\u00002\u00020\u0001:\u0001\u001eB[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e\u0012\u0006\u0010\u0010\u001a\u00020\u0011¢\u0006\u0004\b\u0012\u0010\u0013R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u000b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u000f\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001aR\u0011\u0010\u0010\u001a\u00020\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "isRequired",
         "",
         "options",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption;",
         "lowerLabel",
         "upperLabel",
         "scaleStart",
         "",
         "scaleEnd",
         "questionSubType",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData$QuestionSubType;",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZLjava/util/List;Ljava/lang/String;Ljava/lang/String;IILio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData$QuestionSubType;)V",
         "getOptions",
         "()Ljava/util/List;",
         "getLowerLabel",
         "()Ljava/lang/String;",
         "getUpperLabel",
         "getScaleStart",
         "()I",
         "getScaleEnd",
         "getQuestionSubType",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData$QuestionSubType;",
         "RatingOption",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step$Question$NumericRatingQuestionModel extends SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final List options;
   @NotNull
   private final String lowerLabel;
   @NotNull
   private final String upperLabel;
   private final int scaleStart;
   private final int scaleEnd;
   @NotNull
   private final SurveyData$Step$Question$QuestionData$QuestionSubType questionSubType;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$NumericRatingQuestionModel(
      @NotNull String var1,
      @NotNull List var2,
      boolean var3,
      @NotNull List var4,
      @NotNull String var5,
      @NotNull String var6,
      int var7,
      int var8,
      @NotNull SurveyData$Step$Question$QuestionData$QuestionSubType var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var4, "options");
      Intrinsics.checkNotNullParameter(var5, "lowerLabel");
      Intrinsics.checkNotNullParameter(var6, "upperLabel");
      Intrinsics.checkNotNullParameter(var9, "questionSubType");
      super(var1, var2, null, var3, 4, null);
      this.options = var4;
      this.lowerLabel = var5;
      this.upperLabel = var6;
      this.scaleStart = var7;
      this.scaleEnd = var8;
      this.questionSubType = var9;
   }

   @NotNull
   public final List getOptions() {
      return this.options;
   }

   @NotNull
   public final String getLowerLabel() {
      return this.lowerLabel;
   }

   @NotNull
   public final String getUpperLabel() {
      return this.upperLabel;
   }

   public final int getScaleStart() {
      return this.scaleStart;
   }

   public final int getScaleEnd() {
      return this.scaleEnd;
   }

   @NotNull
   public final SurveyData$Step$Question$QuestionData$QuestionSubType getQuestionSubType() {
      return this.questionSubType;
   }
}
