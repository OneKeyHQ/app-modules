package io.intercom.android.sdk.ui.theme;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\r\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u000e\u0010\tJ\u0010\u0010\u000f\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0010\u0010\tJ\u0010\u0010\u0011\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0012\u0010\tJ.\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÇ\u0001¢\u0006\u0004\b\u0014\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0019\u001a\u00020\u001aH×\u0001J\t\u0010\u001b\u001a\u00020\u001cH×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\u000b\u0010\tR\u0013\u0010\u0005\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\f\u0010\t¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/ui/theme/IntercomBaseColors;",
         "",
         "base",
         "Landroidx/compose/ui/graphics/Color;",
         "input",
         "inputAlt",
         "<init>",
         "(JJJLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getBase-0d7_KjU",
         "()J",
         "J",
         "getInput-0d7_KjU",
         "getInputAlt-0d7_KjU",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component2-0d7_KjU",
         "component3",
         "component3-0d7_KjU",
         "copy",
         "copy-ysEtTa8",
         "(JJJ)Lio/intercom/android/sdk/ui/theme/IntercomBaseColors;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomBaseColors {
   private final long base;
   private final long input;
   private final long inputAlt;
   public static int $stable;

   static {}

   private IntercomBaseColors(long var1, long var3, long var5) {
      this.base = var1;
      this.input = var3;
      this.inputAlt = var5;
   }

   public final long recovered$00296b21a1edce05() {
      return this.base;
   }

   public final long recovered$892fd2926e63e046() {
      return this.input;
   }

   public final long recovered$ad18c207ea721de5() {
      return this.inputAlt;
   }

   public final long recovered$2b43d556f0094889() {
      return this.base;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.input;
   }

   public final long recovered$46bfddf50387e286() {
      return this.inputAlt;
   }

   @NotNull
   public final IntercomBaseColors recovered$2960e560c6da8951(long var1, long var3, long var5) {
      return new IntercomBaseColors(var1, var3, var5, null);
   }

   // $VF: synthetic method
   public static IntercomBaseColors recovered$f46c58e49d4ce24a(IntercomBaseColors var0, long var1, long var3, long var5, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.base;
      }

      if ((var7 & 2) != 0) {
         var3 = var0.input;
      }

      if ((var7 & 4) != 0) {
         var5 = var0.inputAlt;
      }

      return var0.recovered$2960e560c6da8951(var1, var3, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "IntercomBaseColors(base="
         + Color.recovered$cfef63bb1d111f9f(this.base)
         + ", input="
         + Color.recovered$cfef63bb1d111f9f(this.input)
         + ", inputAlt="
         + Color.recovered$cfef63bb1d111f9f(this.inputAlt)
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.base);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.input);
      return var1 * 31 + Color.recovered$87e060624084b26b(this.inputAlt);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomBaseColors)) {
         return false;
      } else {
         IntercomBaseColors var2 = (IntercomBaseColors)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.base, var2.base)) {
            return false;
         } else {
            return !Color.recovered$9eab66ea8c46e11c(this.input, var2.input) ? false : Color.recovered$9eab66ea8c46e11c(this.inputAlt, var2.inputAlt);
         }
      }
   }

   // $VF: synthetic method
   public IntercomBaseColors(long var1, long var3, long var5, DefaultConstructorMarker var7) {
      this(var1, var3, var5);
   }
}
