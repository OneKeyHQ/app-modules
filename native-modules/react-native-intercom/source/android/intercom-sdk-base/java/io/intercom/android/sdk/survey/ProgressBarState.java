package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u00032\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0013H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/survey/ProgressBarState;",
         "",
         "isVisible",
         "",
         "progress",
         "",
         "<init>",
         "(ZF)V",
         "()Z",
         "getProgress",
         "()F",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ProgressBarState {
   private final boolean isVisible;
   private final float progress;
   public static int $stable;

   static {}

   public ProgressBarState(boolean var1, float var2) {
      this.isVisible = var1;
      this.progress = var2;
   }

   // $VF: synthetic method
   public ProgressBarState(boolean var1, float var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (boolean) (false) : var1), (((var3 & 2) != 0) ? (float) (0.0F) : var2));
   }

   public final boolean isVisible() {
      return this.isVisible;
   }

   public final float getProgress() {
      return this.progress;
   }

   public final boolean component1() {
      return this.isVisible;
   }

   public final float component2() {
      return this.progress;
   }

   @NotNull
   public final ProgressBarState copy(boolean var1, float var2) {
      return new ProgressBarState(var1, var2);
   }

   // $VF: synthetic method
   public static ProgressBarState copy$default(ProgressBarState var0, boolean var1, float var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.isVisible;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.progress;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ProgressBarState(isVisible=" + this.isVisible + ", progress=" + this.progress + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.isVisible);
      return var1 * 31 + Float.hashCode(this.progress);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ProgressBarState)) {
         return false;
      }

      ProgressBarState var2 = (ProgressBarState)var1;
      return this.isVisible != var2.isVisible ? false : Float.compare(this.progress, var2.progress) == 0;
   }

   public ProgressBarState() {
      this(false, 0.0F, 3, null);
   }
}
