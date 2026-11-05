package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001:\u0001\u001cB\u001f\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u000e\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u0004J\u000e\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006J\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0010J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J#\u0010\u0015\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H×\u0003J\t\u0010\u001a\u001a\u00020\u0012H×\u0001J\t\u0010\u001b\u001a\u00020\u0004H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "answers",
         "",
         "",
         "otherAnswer",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer$OtherAnswer;",
         "<init>",
         "(Ljava/util/Set;Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer$OtherAnswer;)V",
         "getAnswers",
         "()Ljava/util/Set;",
         "getOtherAnswer",
         "()Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer$OtherAnswer;",
         "copyWithAnswerToggled",
         "answer",
         "copyWithOther",
         "",
         "getLength",
         "",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "OtherAnswer",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Answer$MultipleAnswer extends Answer {
   @NotNull
   private final Set answers;
   @NotNull
   private final Answer$MultipleAnswer$OtherAnswer otherAnswer;
   public static final int $stable;

   static { $stable = 8; }

   public Answer$MultipleAnswer(@NotNull Set var1, @NotNull Answer$MultipleAnswer$OtherAnswer var2) {
      Intrinsics.checkNotNullParameter(var1, "answers");
      Intrinsics.checkNotNullParameter(var2, "otherAnswer");
      super(null);
      this.answers = var1;
      this.otherAnswer = var2;
   }

   // $VF: synthetic method
   public Answer$MultipleAnswer(Set var1, Answer$MultipleAnswer$OtherAnswer var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (Answer$MultipleAnswer$OtherAnswer) (Answer$MultipleAnswer$OtherAnswer$NotSelected.INSTANCE) : var2));
   }

   @NotNull
   public final Set recoveredReturn$bf3409efca2cfb1e() {
      return this.answers;
   }

   @NotNull
   public final Answer$MultipleAnswer$OtherAnswer getOtherAnswer() {
      return this.otherAnswer;
   }

   @NotNull
   public final Answer copyWithAnswerToggled(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "answer");
      Set var2 = CollectionsKt.toMutableSet(this.answers);
      if (this.answers.contains(var1)) {
         var2.remove(var1);
      } else {
         var2.add(var1);
      }

      return var2.isEmpty() && this.otherAnswer instanceof Answer$MultipleAnswer$OtherAnswer$NotSelected
         ? Answer$NoAnswer$ResetNoAnswer.INSTANCE
         : copy$default(this, var2, null, 2, null);
   }

   @NotNull
   public final Answer copyWithOther(@NotNull Answer$MultipleAnswer$OtherAnswer var1) {
      Intrinsics.checkNotNullParameter(var1, "otherAnswer");
      return this.answers.isEmpty() && var1 instanceof Answer$MultipleAnswer$OtherAnswer$NotSelected
         ? Answer$NoAnswer$ResetNoAnswer.INSTANCE
         : copy$default(this, null, var1, 1, null);
   }

   @NotNull
   public final List getAnswers() {
      List var1 = CollectionsKt.createListBuilder();
      List var2 = var1;
      int var3 = 0;
      var2.addAll(CollectionsKt.toList(this.answers));
      Answer$MultipleAnswer$OtherAnswer var4 = this.otherAnswer;
      if (!Intrinsics.areEqual(var4, Answer$MultipleAnswer$OtherAnswer$NotSelected.INSTANCE)) {
         if (Intrinsics.areEqual(var4, Answer$MultipleAnswer$OtherAnswer$SelectedNoText.INSTANCE)) {
            var2.add("Other");
         } else {
            if (!(var4 instanceof Answer$MultipleAnswer$OtherAnswer$SelectedWithText)) {
               throw new NoWhenBranchMatchedException();
            }

            var2.add(((Answer$MultipleAnswer$OtherAnswer$SelectedWithText)this.otherAnswer).getText());
         }
      }

      return CollectionsKt.build(var1);
   }

   @Override
   public int getLength() {
      return CollectionsKt.joinToString$default(this.answers, null, null, null, 0, null, ((kotlin.jvm.functions.Function1) (recoveredArgument_1371_0) -> Answer$MultipleAnswer.getLength$lambda$1((java.lang.String)recoveredArgument_1371_0)), 31, null).length();
   }

   @NotNull
   public final Set component1() {
      return this.answers;
   }

   @NotNull
   public final Answer$MultipleAnswer$OtherAnswer component2() {
      return this.otherAnswer;
   }

   @NotNull
   public final Answer$MultipleAnswer copy(@NotNull Set var1, @NotNull Answer$MultipleAnswer$OtherAnswer var2) {
      Intrinsics.checkNotNullParameter(var1, "answers");
      Intrinsics.checkNotNullParameter(var2, "otherAnswer");
      return new Answer$MultipleAnswer(var1, var2);
   }

   // $VF: synthetic method
   public static Answer$MultipleAnswer copy$default(Answer$MultipleAnswer var0, Set var1, Answer$MultipleAnswer$OtherAnswer var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.answers;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.otherAnswer;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "MultipleAnswer(answers=" + this.answers + ", otherAnswer=" + this.otherAnswer + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.answers.hashCode();
      return var1 * 31 + this.otherAnswer.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Answer$MultipleAnswer)) {
         return false;
      }

      Answer$MultipleAnswer var2 = (Answer$MultipleAnswer)var1;
      return !Intrinsics.areEqual(this.answers, var2.answers) ? false : Intrinsics.areEqual(this.otherAnswer, var2.otherAnswer);
   }

   private static final CharSequence getLength$lambda$1(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return var0;
   }
}
