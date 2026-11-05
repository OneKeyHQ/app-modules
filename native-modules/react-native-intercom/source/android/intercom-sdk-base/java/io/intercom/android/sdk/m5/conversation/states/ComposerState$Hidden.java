package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u00032\b\u0010\u000b\u001a\u0004\u0018\u00010\fH×\u0003J\t\u0010\r\u001a\u00020\u000eH×\u0001J\t\u0010\u000f\u001a\u00020\u0010H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$Hidden;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "hideKeyboard",
         "",
         "<init>",
         "(Z)V",
         "getHideKeyboard",
         "()Z",
         "component1",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ComposerState$Hidden implements ComposerState {
   private final boolean hideKeyboard;
   public static int $stable;

   static {}

   public ComposerState$Hidden(boolean var1) {
      this.hideKeyboard = var1;
   }

   // $VF: synthetic method
   public ComposerState$Hidden(boolean var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (boolean) (true) : var1));
   }

   public final boolean getHideKeyboard() {
      return this.hideKeyboard;
   }

   public final boolean component1() {
      return this.hideKeyboard;
   }

   @NotNull
   public final ComposerState$Hidden copy(boolean var1) {
      return new ComposerState$Hidden(var1);
   }

   // $VF: synthetic method
   public static ComposerState$Hidden copy$default(ComposerState$Hidden var0, boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.hideKeyboard;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Hidden(hideKeyboard=" + this.hideKeyboard + ')';
   }

   @Override
   public int hashCode() {
      return Boolean.hashCode(this.hideKeyboard);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ComposerState$Hidden)) {
         return false;
      }

      ComposerState$Hidden var2 = (ComposerState$Hidden)var1;
      return this.hideKeyboard == var2.hideKeyboard;
   }

   public ComposerState$Hidden() {
      this(false, 1, null);
   }
}
