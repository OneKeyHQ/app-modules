package io.intercom.android.sdk.survey.ui.questiontype;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/questiontype/AnswerClickData;",
         "",
         "clickedItem",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "questionId",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;Ljava/lang/String;)V",
         "getClickedItem",
         "()Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "getQuestionId",
         "()Ljava/lang/String;",
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
public final class AnswerClickData {
   @NotNull
   private final Answer$MediaAnswer$MediaItem clickedItem;
   @NotNull
   private final String questionId;
   public static final int $stable;

   static { $stable = 8; }

   public AnswerClickData(@NotNull Answer$MediaAnswer$MediaItem var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "clickedItem");
      Intrinsics.checkNotNullParameter(var2, "questionId");
      this.clickedItem = var1;
      this.questionId = var2;
   }

   @NotNull
   public final Answer$MediaAnswer$MediaItem getClickedItem() {
      return this.clickedItem;
   }

   @NotNull
   public final String getQuestionId() {
      return this.questionId;
   }

   @NotNull
   public final Answer$MediaAnswer$MediaItem component1() {
      return this.clickedItem;
   }

   @NotNull
   public final String component2() {
      return this.questionId;
   }

   @NotNull
   public final AnswerClickData copy(@NotNull Answer$MediaAnswer$MediaItem var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "clickedItem");
      Intrinsics.checkNotNullParameter(var2, "questionId");
      return new AnswerClickData(var1, var2);
   }

   // $VF: synthetic method
   public static AnswerClickData copy$default(AnswerClickData var0, Answer$MediaAnswer$MediaItem var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.clickedItem;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.questionId;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AnswerClickData(clickedItem=" + this.clickedItem + ", questionId=" + this.questionId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.clickedItem.hashCode();
      return var1 * 31 + this.questionId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AnswerClickData)) {
         return false;
      }

      AnswerClickData var2 = (AnswerClickData)var1;
      return !Intrinsics.areEqual(this.clickedItem, var2.clickedItem) ? false : Intrinsics.areEqual(this.questionId, var2.questionId);
   }
}
