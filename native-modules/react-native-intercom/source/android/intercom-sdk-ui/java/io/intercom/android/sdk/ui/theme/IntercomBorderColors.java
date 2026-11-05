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
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u000b\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\f\u0010\bJ\u0010\u0010\r\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u000e\u0010\bJ$\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001¢\u0006\u0004\b\u0010\u0010\u0011J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0018H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\n\u0010\b¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/ui/theme/IntercomBorderColors;",
         "",
         "neutral",
         "Landroidx/compose/ui/graphics/Color;",
         "emphasis",
         "<init>",
         "(JJLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getNeutral-0d7_KjU",
         "()J",
         "J",
         "getEmphasis-0d7_KjU",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component2-0d7_KjU",
         "copy",
         "copy--OWjLjI",
         "(JJ)Lio/intercom/android/sdk/ui/theme/IntercomBorderColors;",
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
public final class IntercomBorderColors {
   private final long neutral;
   private final long emphasis;
   public static int $stable;

   static {}

   private IntercomBorderColors(long var1, long var3) {
      this.neutral = var1;
      this.emphasis = var3;
   }

   public final long recovered$e2b74d614aa09788() {
      return this.neutral;
   }

   public final long recovered$556707ad0606947e() {
      return this.emphasis;
   }

   public final long recovered$2b43d556f0094889() {
      return this.neutral;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.emphasis;
   }

   @NotNull
   public final IntercomBorderColors recovered$83a432900619e155(long var1, long var3) {
      return new IntercomBorderColors(var1, var3, null);
   }

   // $VF: synthetic method
   public static IntercomBorderColors recovered$94a6da645d6ea976(IntercomBorderColors var0, long var1, long var3, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.neutral;
      }

      if ((var5 & 2) != 0) {
         var3 = var0.emphasis;
      }

      return var0.recovered$83a432900619e155(var1, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "IntercomBorderColors(neutral="
         + Color.recovered$cfef63bb1d111f9f(this.neutral)
         + ", emphasis="
         + Color.recovered$cfef63bb1d111f9f(this.emphasis)
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.neutral);
      return var1 * 31 + Color.recovered$87e060624084b26b(this.emphasis);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomBorderColors)) {
         return false;
      }

      IntercomBorderColors var2 = (IntercomBorderColors)var1;
      return !Color.recovered$9eab66ea8c46e11c(this.neutral, var2.neutral) ? false : Color.recovered$9eab66ea8c46e11c(this.emphasis, var2.emphasis);
   }

   // $VF: synthetic method
   public IntercomBorderColors(long var1, long var3, DefaultConstructorMarker var5) {
      this(var1, var3);
   }
}
