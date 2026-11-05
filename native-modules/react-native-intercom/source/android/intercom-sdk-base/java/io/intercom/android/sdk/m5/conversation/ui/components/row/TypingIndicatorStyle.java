package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BorderStroke;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u0010\u0010\u0013\u001a\u00020\u0007HÆ\u0003¢\u0006\u0004\b\u0014\u0010\u000fJ0\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001¢\u0006\u0004\b\u0016\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001b\u001a\u00020\u001cHÖ\u0001J\t\u0010\u001d\u001a\u00020\u001eHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0006\u001a\u00020\u0007¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/TypingIndicatorStyle;",
         "",
         "shape",
         "Landroidx/compose/ui/graphics/Shape;",
         "borderStroke",
         "Landroidx/compose/foundation/BorderStroke;",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "<init>",
         "(Landroidx/compose/ui/graphics/Shape;Landroidx/compose/foundation/BorderStroke;JLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getShape",
         "()Landroidx/compose/ui/graphics/Shape;",
         "getBorderStroke",
         "()Landroidx/compose/foundation/BorderStroke;",
         "getColor-0d7_KjU",
         "()J",
         "J",
         "component1",
         "component2",
         "component3",
         "component3-0d7_KjU",
         "copy",
         "copy-mxwnekA",
         "(Landroidx/compose/ui/graphics/Shape;Landroidx/compose/foundation/BorderStroke;J)Lio/intercom/android/sdk/m5/conversation/ui/components/row/TypingIndicatorStyle;",
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
final class TypingIndicatorStyle {
   @NotNull
   private final Shape shape;
   @Nullable
   private final BorderStroke borderStroke;
   private final long color;

   private TypingIndicatorStyle(Shape var1, BorderStroke var2, long var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "shape");
      this.shape = var1;
      this.borderStroke = var2;
      this.color = var3;
   }

   @NotNull
   public final Shape getShape() {
      return this.shape;
   }

   @Nullable
   public final BorderStroke getBorderStroke() {
      return this.borderStroke;
   }

   public final long recovered$6f15fcf6c761ad06() {
      return this.color;
   }

   @NotNull
   public final Shape component1() {
      return this.shape;
   }

   @Nullable
   public final BorderStroke component2() {
      return this.borderStroke;
   }

   public final long recovered$46bfddf50387e286() {
      return this.color;
   }

   @NotNull
   public final TypingIndicatorStyle recovered$4a6550436b6e0f89(@NotNull Shape var1, @Nullable BorderStroke var2, long var3) {
      Intrinsics.checkNotNullParameter(var1, "shape");
      return new TypingIndicatorStyle(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static TypingIndicatorStyle recovered$a442cf96fbbfcf04(TypingIndicatorStyle var0, Shape var1, BorderStroke var2, long var3, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.shape;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.borderStroke;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.color;
      }

      return var0.recovered$4a6550436b6e0f89(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "TypingIndicatorStyle(shape="
         + this.shape
         + ", borderStroke="
         + this.borderStroke
         + ", color="
         + Color.recovered$cfef63bb1d111f9f(this.color)
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.shape.hashCode();
      var1 = var1 * 31 + (this.borderStroke == null ? 0 : this.borderStroke.hashCode());
      return var1 * 31 + Color.recovered$87e060624084b26b(this.color);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TypingIndicatorStyle)) {
         return false;
      } else {
         TypingIndicatorStyle var2 = (TypingIndicatorStyle)var1;
         if (!Intrinsics.areEqual(this.shape, var2.shape)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.borderStroke, var2.borderStroke) ? false : Color.recovered$9eab66ea8c46e11c(this.color, var2.color);
         }
      }
   }

   // $VF: synthetic method
   public TypingIndicatorStyle(Shape var1, BorderStroke var2, long var3, DefaultConstructorMarker var5) {
      this(var1, var2, var3);
   }
}
