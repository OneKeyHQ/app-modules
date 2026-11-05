package io.intercom.android.sdk.m5.conversation.utils;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B9\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J;\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00032\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0005H×\u0001J\t\u0010\u0018\u001a\u00020\u0019H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u000bR\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\r¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/KeyboardState;",
         "",
         "isAnimating",
         "",
         "bottomDiff",
         "",
         "isVisible",
         "isDismissed",
         "keyboardHeight",
         "<init>",
         "(ZIZZI)V",
         "()Z",
         "getBottomDiff",
         "()I",
         "getKeyboardHeight",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class KeyboardState {
   private final boolean isAnimating;
   private final int bottomDiff;
   private final boolean isVisible;
   private final boolean isDismissed;
   private final int keyboardHeight;
   public static int $stable;

   static {}

   public KeyboardState(boolean var1, int var2, boolean var3, boolean var4, int var5) {
      this.isAnimating = var1;
      this.bottomDiff = var2;
      this.isVisible = var3;
      this.isDismissed = var4;
      this.keyboardHeight = var5;
   }

   // $VF: synthetic method
   public KeyboardState(boolean var1, int var2, boolean var3, boolean var4, int var5, int var6, DefaultConstructorMarker var7) {
      this((((var6 & 1) != 0) ? (boolean) (false) : var1), (((var6 & 2) != 0) ? (int) (0) : var2), (((var6 & 4) != 0) ? (boolean) (false) : var3), (((var6 & 8) != 0) ? (boolean) (false) : var4), (((var6 & 16) != 0) ? (int) (0) : var5));
   }

   public final boolean isAnimating() {
      return this.isAnimating;
   }

   public final int getBottomDiff() {
      return this.bottomDiff;
   }

   public final boolean isVisible() {
      return this.isVisible;
   }

   public final boolean isDismissed() {
      return this.isDismissed;
   }

   public final int getKeyboardHeight() {
      return this.keyboardHeight;
   }

   public final boolean component1() {
      return this.isAnimating;
   }

   public final int component2() {
      return this.bottomDiff;
   }

   public final boolean component3() {
      return this.isVisible;
   }

   public final boolean component4() {
      return this.isDismissed;
   }

   public final int component5() {
      return this.keyboardHeight;
   }

   @NotNull
   public final KeyboardState copy(boolean var1, int var2, boolean var3, boolean var4, int var5) {
      return new KeyboardState(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static KeyboardState copy$default(KeyboardState var0, boolean var1, int var2, boolean var3, boolean var4, int var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.isAnimating;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.bottomDiff;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.isVisible;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.isDismissed;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.keyboardHeight;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "KeyboardState(isAnimating="
         + this.isAnimating
         + ", bottomDiff="
         + this.bottomDiff
         + ", isVisible="
         + this.isVisible
         + ", isDismissed="
         + this.isDismissed
         + ", keyboardHeight="
         + this.keyboardHeight
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.isAnimating);
      var1 = var1 * 31 + Integer.hashCode(this.bottomDiff);
      var1 = var1 * 31 + Boolean.hashCode(this.isVisible);
      var1 = var1 * 31 + Boolean.hashCode(this.isDismissed);
      return var1 * 31 + Integer.hashCode(this.keyboardHeight);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyboardState)) {
         return false;
      } else {
         KeyboardState var2 = (KeyboardState)var1;
         if (this.isAnimating != var2.isAnimating) {
            return false;
         } else if (this.bottomDiff != var2.bottomDiff) {
            return false;
         } else if (this.isVisible != var2.isVisible) {
            return false;
         } else {
            return this.isDismissed != var2.isDismissed ? false : this.keyboardHeight == var2.keyboardHeight;
         }
      }
   }

   public KeyboardState() {
      this(false, 0, false, false, 0, 31, null);
   }
}
