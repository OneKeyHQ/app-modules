package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.shape.RoundedCornerShape;
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
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\u0015\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0016\u0010\rJ\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0019\u001a\u00020\tHÆ\u0003J8\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001¢\u0006\u0004\b\u001b\u0010\u001cJ\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010 \u001a\u00020!H×\u0001J\t\u0010\"\u001a\u00020#H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle$BubbleStyle;",
         "",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "padding",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "shape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "borderStroke",
         "Landroidx/compose/foundation/BorderStroke;",
         "<init>",
         "(JLandroidx/compose/foundation/layout/PaddingValues;Landroidx/compose/foundation/shape/RoundedCornerShape;Landroidx/compose/foundation/BorderStroke;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getColor-0d7_KjU",
         "()J",
         "J",
         "getPadding",
         "()Landroidx/compose/foundation/layout/PaddingValues;",
         "getShape",
         "()Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "getBorderStroke",
         "()Landroidx/compose/foundation/BorderStroke;",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component3",
         "component4",
         "copy",
         "copy-Iv8Zu3U",
         "(JLandroidx/compose/foundation/layout/PaddingValues;Landroidx/compose/foundation/shape/RoundedCornerShape;Landroidx/compose/foundation/BorderStroke;)Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle$BubbleStyle;",
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
public final class FinRowStyle$BubbleStyle {
   private final long color;
   @NotNull
   private final PaddingValues padding;
   @NotNull
   private final RoundedCornerShape shape;
   @NotNull
   private final BorderStroke borderStroke;
   public static int $stable;

   static {}

   private FinRowStyle$BubbleStyle(long var1, PaddingValues var3, RoundedCornerShape var4, BorderStroke var5) {
      super();
      Intrinsics.checkNotNullParameter(var3, "padding");
      Intrinsics.checkNotNullParameter(var4, "shape");
      Intrinsics.checkNotNullParameter(var5, "borderStroke");
      this.color = var1;
      this.padding = var3;
      this.shape = var4;
      this.borderStroke = var5;
   }

   public final long recovered$6f15fcf6c761ad06() {
      return this.color;
   }

   @NotNull
   public final PaddingValues getPadding() {
      return this.padding;
   }

   @NotNull
   public final RoundedCornerShape getShape() {
      return this.shape;
   }

   @NotNull
   public final BorderStroke getBorderStroke() {
      return this.borderStroke;
   }

   public final long recovered$2b43d556f0094889() {
      return this.color;
   }

   @NotNull
   public final PaddingValues component2() {
      return this.padding;
   }

   @NotNull
   public final RoundedCornerShape component3() {
      return this.shape;
   }

   @NotNull
   public final BorderStroke component4() {
      return this.borderStroke;
   }

   @NotNull
   public final FinRowStyle$BubbleStyle recovered$eae4de7fe097f99c(
      long var1, @NotNull PaddingValues var3, @NotNull RoundedCornerShape var4, @NotNull BorderStroke var5
   ) {
      Intrinsics.checkNotNullParameter(var3, "padding");
      Intrinsics.checkNotNullParameter(var4, "shape");
      Intrinsics.checkNotNullParameter(var5, "borderStroke");
      return new FinRowStyle$BubbleStyle(var1, var3, var4, var5, null);
   }

   // $VF: synthetic method
   public static FinRowStyle$BubbleStyle recovered$4b7f5c1f0c570bcb(
      FinRowStyle$BubbleStyle var0, long var1, PaddingValues var3, RoundedCornerShape var4, BorderStroke var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.color;
      }

      if ((var6 & 2) != 0) {
         var3 = var0.padding;
      }

      if ((var6 & 4) != 0) {
         var4 = var0.shape;
      }

      if ((var6 & 8) != 0) {
         var5 = var0.borderStroke;
      }

      return var0.recovered$eae4de7fe097f99c(var1, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "BubbleStyle(color="
         + Color.recovered$cfef63bb1d111f9f(this.color)
         + ", padding="
         + this.padding
         + ", shape="
         + this.shape
         + ", borderStroke="
         + this.borderStroke
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.color);
      var1 = var1 * 31 + this.padding.hashCode();
      var1 = var1 * 31 + this.shape.hashCode();
      return var1 * 31 + this.borderStroke.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FinRowStyle$BubbleStyle)) {
         return false;
      } else {
         FinRowStyle$BubbleStyle var2 = (FinRowStyle$BubbleStyle)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.color, var2.color)) {
            return false;
         } else if (!Intrinsics.areEqual(this.padding, var2.padding)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.shape, var2.shape) ? false : Intrinsics.areEqual(this.borderStroke, var2.borderStroke);
         }
      }
   }

   // $VF: synthetic method
   public FinRowStyle$BubbleStyle(long var1, PaddingValues var3, RoundedCornerShape var4, BorderStroke var5, DefaultConstructorMarker var6) {
      this(var1, var3, var4, var5);
   }
}
