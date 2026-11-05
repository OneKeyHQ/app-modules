package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0006HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0018H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformElement;",
         "",
         "x",
         "",
         "amplitude",
         "timestamp",
         "",
         "<init>",
         "(FFJ)V",
         "getX",
         "()F",
         "getAmplitude",
         "getTimestamp",
         "()J",
         "component1",
         "component2",
         "component3",
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
public final class WaveformElement {
   private final float x;
   private final float amplitude;
   private final long timestamp;
   public static int $stable;

   static {}

   public WaveformElement(float var1, float var2, long var3) {
      this.x = var1;
      this.amplitude = var2;
      this.timestamp = var3;
   }

   public final float getX() {
      return this.x;
   }

   public final float getAmplitude() {
      return this.amplitude;
   }

   public final long getTimestamp() {
      return this.timestamp;
   }

   public final float component1() {
      return this.x;
   }

   public final float component2() {
      return this.amplitude;
   }

   public final long component3() {
      return this.timestamp;
   }

   @NotNull
   public final WaveformElement copy(float var1, float var2, long var3) {
      return new WaveformElement(var1, var2, var3);
   }

   // $VF: synthetic method
   public static WaveformElement copy$default(WaveformElement var0, float var1, float var2, long var3, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.x;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.amplitude;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.timestamp;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "WaveformElement(x=" + this.x + ", amplitude=" + this.amplitude + ", timestamp=" + this.timestamp + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Float.hashCode(this.x);
      var1 = var1 * 31 + Float.hashCode(this.amplitude);
      return var1 * 31 + Long.hashCode(this.timestamp);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof WaveformElement)) {
         return false;
      } else {
         WaveformElement var2 = (WaveformElement)var1;
         if (Float.compare(this.x, var2.x) != 0) {
            return false;
         } else {
            return Float.compare(this.amplitude, var2.amplitude) != 0 ? false : this.timestamp == var2.timestamp;
         }
      }
   }
}
