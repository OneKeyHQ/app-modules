package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J-\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyRequest;",
         "",
         "surveyProgressId",
         "",
         "currentStepId",
         "responses",
         "",
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyRequestItem;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
         "getSurveyProgressId",
         "()Ljava/lang/String;",
         "getCurrentStepId",
         "getResponses",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SubmitSurveyRequest {
   @SerializedName("survey_progress_id")
   @NotNull
   private final String surveyProgressId;
   @SerializedName("current_step_id")
   @NotNull
   private final String currentStepId;
   @SerializedName("responses")
   @NotNull
   private final List responses;
   public static final int $stable;

   static { $stable = 8; }

   public SubmitSurveyRequest(@NotNull String var1, @NotNull String var2, @NotNull List var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "surveyProgressId");
      Intrinsics.checkNotNullParameter(var2, "currentStepId");
      Intrinsics.checkNotNullParameter(var3, "responses");
      this.surveyProgressId = var1;
      this.currentStepId = var2;
      this.responses = var3;
   }

   @NotNull
   public final String getSurveyProgressId() {
      return this.surveyProgressId;
   }

   @NotNull
   public final String getCurrentStepId() {
      return this.currentStepId;
   }

   @NotNull
   public final List getResponses() {
      return this.responses;
   }

   @NotNull
   public final String component1() {
      return this.surveyProgressId;
   }

   @NotNull
   public final String component2() {
      return this.currentStepId;
   }

   @NotNull
   public final List component3() {
      return this.responses;
   }

   @NotNull
   public final SubmitSurveyRequest copy(@NotNull String var1, @NotNull String var2, @NotNull List var3) {
      Intrinsics.checkNotNullParameter(var1, "surveyProgressId");
      Intrinsics.checkNotNullParameter(var2, "currentStepId");
      Intrinsics.checkNotNullParameter(var3, "responses");
      return new SubmitSurveyRequest(var1, var2, var3);
   }

   // $VF: synthetic method
   public static SubmitSurveyRequest copy$default(SubmitSurveyRequest var0, String var1, String var2, List var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.surveyProgressId;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.currentStepId;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.responses;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "SubmitSurveyRequest(surveyProgressId=" + this.surveyProgressId + ", currentStepId=" + this.currentStepId + ", responses=" + this.responses + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.surveyProgressId.hashCode();
      var1 = var1 * 31 + this.currentStepId.hashCode();
      return var1 * 31 + this.responses.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SubmitSurveyRequest)) {
         return false;
      } else {
         SubmitSurveyRequest var2 = (SubmitSurveyRequest)var1;
         if (!Intrinsics.areEqual(this.surveyProgressId, var2.surveyProgressId)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.currentStepId, var2.currentStepId) ? false : Intrinsics.areEqual(this.responses, var2.responses);
         }
      }
   }
}
