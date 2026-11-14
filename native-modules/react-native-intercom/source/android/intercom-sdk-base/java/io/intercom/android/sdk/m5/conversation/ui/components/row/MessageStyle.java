package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001:\u0001\"B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J1\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001e\u001a\u00020\u001fHÖ\u0001J\t\u0010 \u001a\u00020!HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0012\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle;",
         "",
         "bubbleStyle",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle$BubbleStyle;",
         "rowAlignment",
         "Landroidx/compose/ui/Alignment$Horizontal;",
         "rowPadding",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "bubbleGutter",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle$BubbleStyle;Landroidx/compose/ui/Alignment$Horizontal;Landroidx/compose/foundation/layout/PaddingValues;Landroidx/compose/foundation/layout/PaddingValues;)V",
         "getBubbleStyle",
         "()Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle$BubbleStyle;",
         "getRowAlignment",
         "()Landroidx/compose/ui/Alignment$Horizontal;",
         "getRowPadding",
         "()Landroidx/compose/foundation/layout/PaddingValues;",
         "getBubbleGutter",
         "contentShape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "getContentShape",
         "()Landroidx/compose/foundation/shape/RoundedCornerShape;",
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
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,1611:1\n118#2:1612\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle\n*L\n512#1:1612\n*E\n"
)
final class MessageStyle {
   @NotNull
   private final MessageStyle$BubbleStyle bubbleStyle;
   @NotNull
   private final Horizontal rowAlignment;
   @NotNull
   private final PaddingValues rowPadding;
   @NotNull
   private final PaddingValues bubbleGutter;

   public MessageStyle(@NotNull MessageStyle$BubbleStyle var1, @NotNull Horizontal var2, @NotNull PaddingValues var3, @NotNull PaddingValues var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "bubbleStyle");
      Intrinsics.checkNotNullParameter(var2, "rowAlignment");
      Intrinsics.checkNotNullParameter(var3, "rowPadding");
      Intrinsics.checkNotNullParameter(var4, "bubbleGutter");
      this.bubbleStyle = var1;
      this.rowAlignment = var2;
      this.rowPadding = var3;
      this.bubbleGutter = var4;
   }

   // $VF: synthetic method
   public MessageStyle(MessageStyle$BubbleStyle var1, Horizontal var2, PaddingValues var3, PaddingValues var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (PaddingValues) (PaddingKt.recovered$0eefed217002276c(0.0F, 0.0F, 3, null)) : var4));
   }

   @NotNull
   public final MessageStyle$BubbleStyle getBubbleStyle() {
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
   public final PaddingValues getBubbleGutter() {
      return this.bubbleGutter;
   }

   @NotNull
   public final RoundedCornerShape getContentShape() {
      int var1 = 8;
      int var2 = 0;
      return RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var1));
   }

   @NotNull
   public final MessageStyle$BubbleStyle component1() {
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
   public final PaddingValues component4() {
      return this.bubbleGutter;
   }

   @NotNull
   public final MessageStyle copy(@NotNull MessageStyle$BubbleStyle var1, @NotNull Horizontal var2, @NotNull PaddingValues var3, @NotNull PaddingValues var4) {
      Intrinsics.checkNotNullParameter(var1, "bubbleStyle");
      Intrinsics.checkNotNullParameter(var2, "rowAlignment");
      Intrinsics.checkNotNullParameter(var3, "rowPadding");
      Intrinsics.checkNotNullParameter(var4, "bubbleGutter");
      return new MessageStyle(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static MessageStyle copy$default(
      MessageStyle var0, MessageStyle$BubbleStyle var1, Horizontal var2, PaddingValues var3, PaddingValues var4, int var5, Object var6
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
         var4 = var0.bubbleGutter;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "MessageStyle(bubbleStyle="
         + this.bubbleStyle
         + ", rowAlignment="
         + this.rowAlignment
         + ", rowPadding="
         + this.rowPadding
         + ", bubbleGutter="
         + this.bubbleGutter
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.bubbleStyle.hashCode();
      var1 = var1 * 31 + this.rowAlignment.hashCode();
      var1 = var1 * 31 + this.rowPadding.hashCode();
      return var1 * 31 + this.bubbleGutter.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MessageStyle)) {
         return false;
      } else {
         MessageStyle var2 = (MessageStyle)var1;
         if (!Intrinsics.areEqual(this.bubbleStyle, var2.bubbleStyle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.rowAlignment, var2.rowAlignment)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.rowPadding, var2.rowPadding) ? false : Intrinsics.areEqual(this.bubbleGutter, var2.bubbleGutter);
         }
      }
   }
}
