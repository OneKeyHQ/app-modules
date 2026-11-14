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
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\u0003H\u0016J\t\u0010\t\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH×\u0003J\t\u0010\u000f\u001a\u00020\u0010H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer$OtherAnswer$SelectedWithText;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MultipleAnswer$OtherAnswer;",
         "text",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getText",
         "()Ljava/lang/String;",
         "toString",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Answer$MultipleAnswer$OtherAnswer$SelectedWithText extends Answer$MultipleAnswer$OtherAnswer {
   @NotNull
   private final String text;
   public static int $stable;

   static {}

   public Answer$MultipleAnswer$OtherAnswer$SelectedWithText(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "text");
      super(null);
      this.text = var1;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @NotNull
   @Override
   public String toString() {
      return this.text;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   @NotNull
   public final Answer$MultipleAnswer$OtherAnswer$SelectedWithText copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "text");
      return new Answer$MultipleAnswer$OtherAnswer$SelectedWithText(var1);
   }

   // $VF: synthetic method
   public static Answer$MultipleAnswer$OtherAnswer$SelectedWithText copy$default(
      Answer$MultipleAnswer$OtherAnswer$SelectedWithText var0, String var1, int var2, Object var3
   ) {
      if ((var2 & 1) != 0) {
         var1 = var0.text;
      }

      return var0.copy(var1);
   }

   @Override
   public int hashCode() {
      return this.text.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Answer$MultipleAnswer$OtherAnswer$SelectedWithText)) {
         return false;
      }

      Answer$MultipleAnswer$OtherAnswer$SelectedWithText var2 = (Answer$MultipleAnswer$OtherAnswer$SelectedWithText)var1;
      return Intrinsics.areEqual(this.text, var2.text);
   }
}
