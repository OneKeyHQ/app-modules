package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Shape;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001:\u0001 B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0017\u001a\u00020\tHÆ\u0003J1\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\u001fH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;",
         "",
         "bubbleStyle",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle$BubbleStyle;",
         "rowAlignment",
         "Landroidx/compose/ui/Alignment$Horizontal;",
         "rowPadding",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "contentShape",
         "Landroidx/compose/ui/graphics/Shape;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle$BubbleStyle;Landroidx/compose/ui/Alignment$Horizontal;Landroidx/compose/foundation/layout/PaddingValues;Landroidx/compose/ui/graphics/Shape;)V",
         "getBubbleStyle",
         "()Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle$BubbleStyle;",
         "getRowAlignment",
         "()Landroidx/compose/ui/Alignment$Horizontal;",
         "getRowPadding",
         "()Landroidx/compose/foundation/layout/PaddingValues;",
         "getContentShape",
         "()Landroidx/compose/ui/graphics/Shape;",
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
         "BubbleStyle",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class FinRowStyle {
   @NotNull
   private final FinRowStyle$BubbleStyle bubbleStyle;
   @NotNull
   private final Horizontal rowAlignment;
   @NotNull
   private final PaddingValues rowPadding;
   @NotNull
   private final Shape contentShape;
   public static int $stable;

   static {}

   public FinRowStyle(@NotNull FinRowStyle$BubbleStyle var1, @NotNull Horizontal var2, @NotNull PaddingValues var3, @NotNull Shape var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "bubbleStyle");
      Intrinsics.checkNotNullParameter(var2, "rowAlignment");
      Intrinsics.checkNotNullParameter(var3, "rowPadding");
      Intrinsics.checkNotNullParameter(var4, "contentShape");
      this.bubbleStyle = var1;
      this.rowAlignment = var2;
      this.rowPadding = var3;
      this.contentShape = var4;
   }

   @NotNull
   public final FinRowStyle$BubbleStyle getBubbleStyle() {
      return this.bubbleStyle;
   }

   @NotNull
   public final Horizontal getRowAlignment() {
      return this.rowAlignment;
   }

   @NotNull
   public final PaddingValues getRowPadding() {
      return this.rowPadding;
   }

   @NotNull
   public final Shape getContentShape() {
      return this.contentShape;
   }

   @NotNull
   public final FinRowStyle$BubbleStyle component1() {
      return this.bubbleStyle;
   }

   @NotNull
   public final Horizontal component2() {
      return this.rowAlignment;
   }

   @NotNull
   public final PaddingValues component3() {
      return this.rowPadding;
   }

   @NotNull
   public final Shape component4() {
      return this.contentShape;
   }

   @NotNull
   public final FinRowStyle copy(@NotNull FinRowStyle$BubbleStyle var1, @NotNull Horizontal var2, @NotNull PaddingValues var3, @NotNull Shape var4) {
      Intrinsics.checkNotNullParameter(var1, "bubbleStyle");
      Intrinsics.checkNotNullParameter(var2, "rowAlignment");
      Intrinsics.checkNotNullParameter(var3, "rowPadding");
      Intrinsics.checkNotNullParameter(var4, "contentShape");
      return new FinRowStyle(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static FinRowStyle copy$default(
      FinRowStyle var0, FinRowStyle$BubbleStyle var1, Horizontal var2, PaddingValues var3, Shape var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.bubbleStyle;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.rowAlignment;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.rowPadding;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.contentShape;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "FinRowStyle(bubbleStyle="
         + this.bubbleStyle
         + ", rowAlignment="
         + this.rowAlignment
         + ", rowPadding="
         + this.rowPadding
         + ", contentShape="
         + this.contentShape
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.bubbleStyle.hashCode();
      var1 = var1 * 31 + this.rowAlignment.hashCode();
      var1 = var1 * 31 + this.rowPadding.hashCode();
      return var1 * 31 + this.contentShape.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FinRowStyle)) {
         return false;
      } else {
         FinRowStyle var2 = (FinRowStyle)var1;
         if (!Intrinsics.areEqual(this.bubbleStyle, var2.bubbleStyle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.rowAlignment, var2.rowAlignment)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.rowPadding, var2.rowPadding) ? false : Intrinsics.areEqual(this.contentShape, var2.contentShape);
         }
      }
   }
}
