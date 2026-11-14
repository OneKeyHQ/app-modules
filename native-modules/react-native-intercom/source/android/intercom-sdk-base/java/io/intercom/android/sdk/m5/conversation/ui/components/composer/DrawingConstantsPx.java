package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003JE\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001c\u001a\u00020\u001dHÖ\u0001J\t\u0010\u001e\u001a\u00020\u001fHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\f¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/DrawingConstantsPx;",
         "",
         "barWidth",
         "",
         "dotRadius",
         "minBarHeight",
         "amplitudeThreshold",
         "amplitudeDeadZone",
         "amplitudeSmoothingFactor",
         "<init>",
         "(FFFFFF)V",
         "getBarWidth",
         "()F",
         "getDotRadius",
         "getMinBarHeight",
         "getAmplitudeThreshold",
         "getAmplitudeDeadZone",
         "getAmplitudeSmoothingFactor",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
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
final class DrawingConstantsPx {
   private final float barWidth;
   private final float dotRadius;
   private final float minBarHeight;
   private final float amplitudeThreshold;
   private final float amplitudeDeadZone;
   private final float amplitudeSmoothingFactor;

   public DrawingConstantsPx(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.barWidth = var1;
      this.dotRadius = var2;
      this.minBarHeight = var3;
      this.amplitudeThreshold = var4;
      this.amplitudeDeadZone = var5;
      this.amplitudeSmoothingFactor = var6;
   }

   public final float getBarWidth() {
      return this.barWidth;
   }

   public final float getDotRadius() {
      return this.dotRadius;
   }

   public final float getMinBarHeight() {
      return this.minBarHeight;
   }

   public final float getAmplitudeThreshold() {
      return this.amplitudeThreshold;
   }

   public final float getAmplitudeDeadZone() {
      return this.amplitudeDeadZone;
   }

   public final float getAmplitudeSmoothingFactor() {
      return this.amplitudeSmoothingFactor;
   }

   public final float component1() {
      return this.barWidth;
   }

   public final float component2() {
      return this.dotRadius;
   }

   public final float component3() {
      return this.minBarHeight;
   }

   public final float component4() {
      return this.amplitudeThreshold;
   }

   public final float component5() {
      return this.amplitudeDeadZone;
   }

   public final float component6() {
      return this.amplitudeSmoothingFactor;
   }

   @NotNull
   public final DrawingConstantsPx copy(float var1, float var2, float var3, float var4, float var5, float var6) {
      return new DrawingConstantsPx(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static DrawingConstantsPx copy$default(
      DrawingConstantsPx var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.barWidth;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.dotRadius;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.minBarHeight;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.amplitudeThreshold;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.amplitudeDeadZone;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.amplitudeSmoothingFactor;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "DrawingConstantsPx(barWidth="
         + this.barWidth
         + ", dotRadius="
         + this.dotRadius
         + ", minBarHeight="
         + this.minBarHeight
         + ", amplitudeThreshold="
         + this.amplitudeThreshold
         + ", amplitudeDeadZone="
         + this.amplitudeDeadZone
         + ", amplitudeSmoothingFactor="
         + this.amplitudeSmoothingFactor
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Float.hashCode(this.barWidth);
      var1 = var1 * 31 + Float.hashCode(this.dotRadius);
      var1 = var1 * 31 + Float.hashCode(this.minBarHeight);
      var1 = var1 * 31 + Float.hashCode(this.amplitudeThreshold);
      var1 = var1 * 31 + Float.hashCode(this.amplitudeDeadZone);
      return var1 * 31 + Float.hashCode(this.amplitudeSmoothingFactor);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DrawingConstantsPx)) {
         return false;
      } else {
         DrawingConstantsPx var2 = (DrawingConstantsPx)var1;
         if (Float.compare(this.barWidth, var2.barWidth) != 0) {
            return false;
         } else if (Float.compare(this.dotRadius, var2.dotRadius) != 0) {
            return false;
         } else if (Float.compare(this.minBarHeight, var2.minBarHeight) != 0) {
            return false;
         } else if (Float.compare(this.amplitudeThreshold, var2.amplitudeThreshold) != 0) {
            return false;
         } else {
            return Float.compare(this.amplitudeDeadZone, var2.amplitudeDeadZone) != 0
               ? false
               : Float.compare(this.amplitudeSmoothingFactor, var2.amplitudeSmoothingFactor) == 0;
         }
      }
   }
}
