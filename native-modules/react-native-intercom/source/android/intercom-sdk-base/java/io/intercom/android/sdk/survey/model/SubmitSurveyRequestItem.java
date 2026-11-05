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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005HÆ\u0003J#\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyRequestItem;",
         "",
         "questionId",
         "",
         "response",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;)V",
         "getQuestionId",
         "()Ljava/lang/String;",
         "getResponse",
         "()Ljava/util/List;",
         "component1",
         "component2",
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
public final class SubmitSurveyRequestItem {
   @SerializedName("question_id")
   @NotNull
   private final String questionId;
   @SerializedName("response")
   @NotNull
   private final List response;
   public static final int $stable;

   static { $stable = 8; }

   public SubmitSurveyRequestItem(@NotNull String var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "questionId");
      Intrinsics.checkNotNullParameter(var2, "response");
      this.questionId = var1;
      this.response = var2;
   }

   @NotNull
   public final String getQuestionId() {
      return this.questionId;
   }

   @NotNull
   public final List getResponse() {
      return this.response;
   }

   @NotNull
   public final String component1() {
      return this.questionId;
   }

   @NotNull
   public final List component2() {
      return this.response;
   }

   @NotNull
   public final SubmitSurveyRequestItem copy(@NotNull String var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var1, "questionId");
      Intrinsics.checkNotNullParameter(var2, "response");
      return new SubmitSurveyRequestItem(var1, var2);
   }

   // $VF: synthetic method
   public static SubmitSurveyRequestItem copy$default(SubmitSurveyRequestItem var0, String var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.questionId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.response;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "SubmitSurveyRequestItem(questionId=" + this.questionId + ", response=" + this.response + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.questionId.hashCode();
      return var1 * 31 + this.response.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SubmitSurveyRequestItem)) {
         return false;
      }

      SubmitSurveyRequestItem var2 = (SubmitSurveyRequestItem)var1;
      return !Intrinsics.areEqual(this.questionId, var2.questionId) ? false : Intrinsics.areEqual(this.response, var2.response);
   }
}
