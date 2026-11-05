package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00032\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0007H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyResponse;",
         "",
         "complete",
         "",
         "nextStep",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "surveyId",
         "",
         "<init>",
         "(ZLio/intercom/android/sdk/survey/model/SurveyData$Step;Ljava/lang/String;)V",
         "getComplete",
         "()Z",
         "getNextStep",
         "()Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "getSurveyId",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SubmitSurveyResponse {
   @SerializedName("complete")
   private final boolean complete;
   @SerializedName("next_step")
   @NotNull
   private final SurveyData$Step nextStep;
   @SerializedName("survey_id")
   @NotNull
   private final String surveyId;
   public static final int $stable;

   static { $stable = 8; }

   public SubmitSurveyResponse(boolean var1, @NotNull SurveyData$Step var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var2, "nextStep");
      Intrinsics.checkNotNullParameter(var3, "surveyId");
      this.complete = var1;
      this.nextStep = var2;
      this.surveyId = var3;
   }

   public final boolean getComplete() {
      return this.complete;
   }

   @NotNull
   public final SurveyData$Step getNextStep() {
      return this.nextStep;
   }

   @NotNull
   public final String getSurveyId() {
      return this.surveyId;
   }

   public final boolean component1() {
      return this.complete;
   }

   @NotNull
   public final SurveyData$Step component2() {
      return this.nextStep;
   }

   @NotNull
   public final String component3() {
      return this.surveyId;
   }

   @NotNull
   public final SubmitSurveyResponse copy(boolean var1, @NotNull SurveyData$Step var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var2, "nextStep");
      Intrinsics.checkNotNullParameter(var3, "surveyId");
      return new SubmitSurveyResponse(var1, var2, var3);
   }

   // $VF: synthetic method
   public static SubmitSurveyResponse copy$default(SubmitSurveyResponse var0, boolean var1, SurveyData$Step var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.complete;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.nextStep;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.surveyId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "SubmitSurveyResponse(complete=" + this.complete + ", nextStep=" + this.nextStep + ", surveyId=" + this.surveyId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.complete);
      var1 = var1 * 31 + this.nextStep.hashCode();
      return var1 * 31 + this.surveyId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SubmitSurveyResponse)) {
         return false;
      } else {
         SubmitSurveyResponse var2 = (SubmitSurveyResponse)var1;
         if (this.complete != var2.complete) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.nextStep, var2.nextStep) ? false : Intrinsics.areEqual(this.surveyId, var2.surveyId);
         }
      }
   }
}
