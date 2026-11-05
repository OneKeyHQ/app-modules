package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007¢\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0007J\u0010\u0010\u0017\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0018\u0010\rJ\u0010\u0010\u0019\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u001a\u0010\rJ\u0010\u0010\u001b\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u001c\u0010\rJ\t\u0010\u001d\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0007HÆ\u0003JL\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0007HÆ\u0001¢\u0006\u0004\b!\u0010\"J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010&\u001a\u00020'HÖ\u0001J\t\u0010(\u001a\u00020)HÖ\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u000f\u0010\rR\u0013\u0010\u0005\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\t\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\r¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/DrawingConstants;",
         "",
         "barWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "dotRadius",
         "minBarHeight",
         "amplitudeThreshold",
         "",
         "amplitudeDeadZone",
         "amplitudeSmoothingFactor",
         "<init>",
         "(FFFFFFLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getBarWidth-D9Ej5fM",
         "()F",
         "F",
         "getDotRadius-D9Ej5fM",
         "getMinBarHeight-D9Ej5fM",
         "getAmplitudeThreshold",
         "getAmplitudeDeadZone",
         "getAmplitudeSmoothingFactor",
         "toPx",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/DrawingConstantsPx;",
         "density",
         "component1",
         "component1-D9Ej5fM",
         "component2",
         "component2-D9Ej5fM",
         "component3",
         "component3-D9Ej5fM",
         "component4",
         "component5",
         "component6",
         "copy",
         "copy-yajeYGU",
         "(FFFFFF)Lio/intercom/android/sdk/m5/conversation/ui/components/composer/DrawingConstants;",
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
final class DrawingConstants {
   private final float barWidth;
   private final float dotRadius;
   private final float minBarHeight;
   private final float amplitudeThreshold;
   private final float amplitudeDeadZone;
   private final float amplitudeSmoothingFactor;

   private DrawingConstants(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.barWidth = var1;
      this.dotRadius = var2;
      this.minBarHeight = var3;
      this.amplitudeThreshold = var4;
      this.amplitudeDeadZone = var5;
      this.amplitudeSmoothingFactor = var6;
   }

   public final float recovered$a4d63ea200f43d50() {
      return this.barWidth;
   }

   public final float recovered$a890fe1f11b59dea() {
      return this.dotRadius;
   }

   public final float recovered$9ebb1a3327aa1efa() {
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

   @NotNull
   public final DrawingConstantsPx toPx(float var1) {
      return new DrawingConstantsPx(
         this.barWidth * var1, this.dotRadius * var1, this.minBarHeight * var1, this.amplitudeThreshold, this.amplitudeDeadZone, this.amplitudeSmoothingFactor
      );
   }

   public final float recovered$3ccb65eabf18bd51() {
      return this.barWidth;
   }

   public final float recovered$d183220c61a258aa() {
      return this.dotRadius;
   }

   public final float recovered$792f6b6d48d93e76() {
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
   public final DrawingConstants recovered$5ea5bc7dbf7ee91f(float var1, float var2, float var3, float var4, float var5, float var6) {
      return new DrawingConstants(var1, var2, var3, var4, var5, var6, null);
   }

   // $VF: synthetic method
   public static DrawingConstants recovered$ec6b521bbba9964c(
      DrawingConstants var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, Object var8
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

      return var0.recovered$5ea5bc7dbf7ee91f(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "DrawingConstants(barWidth="
         + Dp.recovered$2203d269d96243d7(this.barWidth)
         + ", dotRadius="
         + Dp.recovered$2203d269d96243d7(this.dotRadius)
         + ", minBarHeight="
         + Dp.recovered$2203d269d96243d7(this.minBarHeight)
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
      int var1 = Dp.recovered$6944ff41afc8fe34(this.barWidth);
      var1 = var1 * 31 + Dp.recovered$6944ff41afc8fe34(this.dotRadius);
      var1 = var1 * 31 + Dp.recovered$6944ff41afc8fe34(this.minBarHeight);
      var1 = var1 * 31 + Float.hashCode(this.amplitudeThreshold);
      var1 = var1 * 31 + Float.hashCode(this.amplitudeDeadZone);
      return var1 * 31 + Float.hashCode(this.amplitudeSmoothingFactor);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DrawingConstants)) {
         return false;
      } else {
         DrawingConstants var2 = (DrawingConstants)var1;
         if (!Dp.recovered$d4343afa8a8ebb0d(this.barWidth, var2.barWidth)) {
            return false;
         } else if (!Dp.recovered$d4343afa8a8ebb0d(this.dotRadius, var2.dotRadius)) {
            return false;
         } else if (!Dp.recovered$d4343afa8a8ebb0d(this.minBarHeight, var2.minBarHeight)) {
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

   // $VF: synthetic method
   public DrawingConstants(float var1, float var2, float var3, float var4, float var5, float var6, DefaultConstructorMarker var7) {
      this(var1, var2, var3, var4, var5, var6);
   }
}
