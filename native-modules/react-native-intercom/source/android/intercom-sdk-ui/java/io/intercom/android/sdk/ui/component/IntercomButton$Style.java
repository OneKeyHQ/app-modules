package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\u000f\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0010\u0010\nJ\u0010\u0010\u0011\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0012\u0010\nJ\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J.\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001¢\u0006\u0004\b\u0015\u0010\u0016J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001a\u001a\u00020\u001bH×\u0001J\t\u0010\u001c\u001a\u00020\u001dH×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/ui/component/IntercomButton$Style;",
         "",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "border",
         "Landroidx/compose/foundation/BorderStroke;",
         "<init>",
         "(JJLandroidx/compose/foundation/BorderStroke;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getBackgroundColor-0d7_KjU",
         "()J",
         "J",
         "getContentColor-0d7_KjU",
         "getBorder",
         "()Landroidx/compose/foundation/BorderStroke;",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component2-0d7_KjU",
         "component3",
         "copy",
         "copy-jxsXWHM",
         "(JJLandroidx/compose/foundation/BorderStroke;)Lio/intercom/android/sdk/ui/component/IntercomButton$Style;",
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
public final class IntercomButton$Style {
   private final long backgroundColor;
   private final long contentColor;
   @NotNull
   private final BorderStroke border;
   public static int $stable;

   static {}

   private IntercomButton$Style(long var1, long var3, BorderStroke var5) {
      super();
      Intrinsics.checkNotNullParameter(var5, "border");
      this.backgroundColor = var1;
      this.contentColor = var3;
      this.border = var5;
   }

   public final long recovered$934f6183e8132565() {
      return this.backgroundColor;
   }

   public final long recovered$6557fe71dfd7d063() {
      return this.contentColor;
   }

   @NotNull
   public final BorderStroke getBorder() {
      return this.border;
   }

   public final long recovered$2b43d556f0094889() {
      return this.backgroundColor;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.contentColor;
   }

   @NotNull
   public final BorderStroke component3() {
      return this.border;
   }

   @NotNull
   public final IntercomButton$Style recovered$bb045e152e228620(long var1, long var3, @NotNull BorderStroke var5) {
      Intrinsics.checkNotNullParameter(var5, "border");
      return new IntercomButton$Style(var1, var3, var5, null);
   }

   // $VF: synthetic method
   public static IntercomButton$Style recovered$4b4f7c4c94a573e5(IntercomButton$Style var0, long var1, long var3, BorderStroke var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.backgroundColor;
      }

      if ((var6 & 2) != 0) {
         var3 = var0.contentColor;
      }

      if ((var6 & 4) != 0) {
         var5 = var0.border;
      }

      return var0.recovered$bb045e152e228620(var1, var3, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Style(backgroundColor="
         + Color.recovered$cfef63bb1d111f9f(this.backgroundColor)
         + ", contentColor="
         + Color.recovered$cfef63bb1d111f9f(this.contentColor)
         + ", border="
         + this.border
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.backgroundColor);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.contentColor);
      return var1 * 31 + this.border.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomButton$Style)) {
         return false;
      } else {
         IntercomButton$Style var2 = (IntercomButton$Style)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else {
            return !Color.recovered$9eab66ea8c46e11c(this.contentColor, var2.contentColor) ? false : Intrinsics.areEqual(this.border, var2.border);
         }
      }
   }

   // $VF: synthetic method
   public IntercomButton$Style(long var1, long var3, BorderStroke var5, DefaultConstructorMarker var6) {
      this(var1, var3, var5);
   }
}
