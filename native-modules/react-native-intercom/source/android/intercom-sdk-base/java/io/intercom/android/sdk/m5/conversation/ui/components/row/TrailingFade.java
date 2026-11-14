package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J1\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0019H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/TrailingFade;",
         "",
         "top",
         "",
         "bottom",
         "solidX",
         "caretX",
         "<init>",
         "(FFFF)V",
         "getTop",
         "()F",
         "getBottom",
         "getSolidX",
         "getCaretX",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class TrailingFade {
   private final float top;
   private final float bottom;
   private final float solidX;
   private final float caretX;
   public static int $stable;

   static {}

   public TrailingFade(float var1, float var2, float var3, float var4) {
      this.top = var1;
      this.bottom = var2;
      this.solidX = var3;
      this.caretX = var4;
   }

   public final float getTop() {
      return this.top;
   }

   public final float getBottom() {
      return this.bottom;
   }

   public final float getSolidX() {
      return this.solidX;
   }

   public final float getCaretX() {
      return this.caretX;
   }

   public final float component1() {
      return this.top;
   }

   public final float component2() {
      return this.bottom;
   }

   public final float component3() {
      return this.solidX;
   }

   public final float component4() {
      return this.caretX;
   }

   @NotNull
   public final TrailingFade copy(float var1, float var2, float var3, float var4) {
      return new TrailingFade(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static TrailingFade copy$default(TrailingFade var0, float var1, float var2, float var3, float var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.top;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.bottom;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.solidX;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.caretX;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "TrailingFade(top=" + this.top + ", bottom=" + this.bottom + ", solidX=" + this.solidX + ", caretX=" + this.caretX + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Float.hashCode(this.top);
      var1 = var1 * 31 + Float.hashCode(this.bottom);
      var1 = var1 * 31 + Float.hashCode(this.solidX);
      return var1 * 31 + Float.hashCode(this.caretX);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TrailingFade)) {
         return false;
      } else {
         TrailingFade var2 = (TrailingFade)var1;
         if (Float.compare(this.top, var2.top) != 0) {
            return false;
         } else if (Float.compare(this.bottom, var2.bottom) != 0) {
            return false;
         } else {
            return Float.compare(this.solidX, var2.solidX) != 0 ? false : Float.compare(this.caretX, var2.caretX) == 0;
         }
      }
   }
}
