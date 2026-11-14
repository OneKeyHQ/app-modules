package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\tH\u0016J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH×\u0003J\t\u0010\u0010\u001a\u00020\tH×\u0001J\t\u0010\u0011\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$SingleAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "answer",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getAnswer",
         "()Ljava/lang/String;",
         "getLength",
         "",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Answer$SingleAnswer extends Answer {
   @NotNull
   private final String answer;
   public static int $stable;

   static {}

   public Answer$SingleAnswer(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "answer");
      super(null);
      this.answer = var1;
   }

   @NotNull
   public final String getAnswer() {
      return this.answer;
   }

   @Override
   public int getLength() {
      return this.answer.length();
   }

   @NotNull
   public final String component1() {
      return this.answer;
   }

   @NotNull
   public final Answer$SingleAnswer copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "answer");
      return new Answer$SingleAnswer(var1);
   }

   // $VF: synthetic method
   public static Answer$SingleAnswer copy$default(Answer$SingleAnswer var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.answer;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "SingleAnswer(answer=" + this.answer + ')';
   }

   @Override
   public int hashCode() {
      return this.answer.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Answer$SingleAnswer)) {
         return false;
      }

      Answer$SingleAnswer var2 = (Answer$SingleAnswer)var1;
      return Intrinsics.areEqual(this.answer, var2.answer);
   }
}
