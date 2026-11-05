package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.internal.StabilityInferred;
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
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0014\u001a\u00020\u0005HÆ\u0003¢\u0006\u0004\b\u0015\u0010\u000eJ\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\u0010\u0010\u0017\u001a\u00020\u0005HÆ\u0003¢\u0006\u0004\b\u0018\u0010\u000eJ8\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0005HÇ\u0001¢\u0006\u0004\b\u001a\u0010\u001bJ\u0013\u0010\u001c\u001a\u00020\u00072\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u001fH×\u0001J\t\u0010 \u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\b\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\u0012\u0010\u000e¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/BottomMetadata;",
         "",
         "text",
         "",
         "padding",
         "Landroidx/compose/ui/unit/Dp;",
         "alwaysShow",
         "",
         "startIndent",
         "<init>",
         "(Ljava/lang/String;FZFLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getText",
         "()Ljava/lang/String;",
         "getPadding-D9Ej5fM",
         "()F",
         "F",
         "getAlwaysShow",
         "()Z",
         "getStartIndent-D9Ej5fM",
         "component1",
         "component2",
         "component2-D9Ej5fM",
         "component3",
         "component4",
         "component4-D9Ej5fM",
         "copy",
         "copy-vfC735M",
         "(Ljava/lang/String;FZF)Lio/intercom/android/sdk/m5/conversation/ui/components/row/BottomMetadata;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nClickableMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClickableMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BottomMetadata\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,127:1\n118#2:128\n118#2:129\n*S KotlinDebug\n*F\n+ 1 ClickableMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BottomMetadata\n*L\n108#1:128\n110#1:129\n*E\n"
)
public final class BottomMetadata {
   @NotNull
   private final String text;
   private final float padding;
   private final boolean alwaysShow;
   private final float startIndent;
   public static int $stable;

   static {}

   private BottomMetadata(String var1, float var2, boolean var3, float var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      this.text = var1;
      this.padding = var2;
      this.alwaysShow = var3;
      this.startIndent = var4;
   }

   // $VF: synthetic method
   public BottomMetadata(String var1, float var2, boolean var3, float var4, int var5, DefaultConstructorMarker var6) {
      if ((var5 & 2) != 0) {
         int var7 = 4;
         int var8 = 0;
         var2 = Dp.recovered$95b3d103663bf060(var7);
      }

      if ((var5 & 4) != 0) {
         var3 = false;
      }

      if ((var5 & 8) != 0) {
         int var9 = 0;
         int var10 = 0;
         var4 = Dp.recovered$95b3d103663bf060(var9);
      }

      this(var1, var2, var3, var4, null);
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final float recovered$9cbfa2f73aae0193() {
      return this.padding;
   }

   public final boolean getAlwaysShow() {
      return this.alwaysShow;
   }

   public final float recovered$2c49dee5a543a836() {
      return this.startIndent;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   public final float recovered$d183220c61a258aa() {
      return this.padding;
   }

   public final boolean component3() {
      return this.alwaysShow;
   }

   public final float recovered$dc44ddbb74a522bf() {
      return this.startIndent;
   }

   @NotNull
   public final BottomMetadata recovered$fa33b33470874aba(@NotNull String var1, float var2, boolean var3, float var4) {
      Intrinsics.checkNotNullParameter(var1, "text");
      return new BottomMetadata(var1, var2, var3, var4, null);
   }

   // $VF: synthetic method
   public static BottomMetadata recovered$396b712fe93bcad1(BottomMetadata var0, String var1, float var2, boolean var3, float var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.padding;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.alwaysShow;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.startIndent;
      }

      return var0.recovered$fa33b33470874aba(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "BottomMetadata(text="
         + this.text
         + ", padding="
         + Dp.recovered$2203d269d96243d7(this.padding)
         + ", alwaysShow="
         + this.alwaysShow
         + ", startIndent="
         + Dp.recovered$2203d269d96243d7(this.startIndent)
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      var1 = var1 * 31 + Dp.recovered$6944ff41afc8fe34(this.padding);
      var1 = var1 * 31 + Boolean.hashCode(this.alwaysShow);
      return var1 * 31 + Dp.recovered$6944ff41afc8fe34(this.startIndent);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BottomMetadata)) {
         return false;
      } else {
         BottomMetadata var2 = (BottomMetadata)var1;
         if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else if (!Dp.recovered$d4343afa8a8ebb0d(this.padding, var2.padding)) {
            return false;
         } else {
            return this.alwaysShow != var2.alwaysShow ? false : Dp.recovered$d4343afa8a8ebb0d(this.startIndent, var2.startIndent);
         }
      }
   }

   // $VF: synthetic method
   public BottomMetadata(String var1, float var2, boolean var3, float var4, DefaultConstructorMarker var5) {
      this(var1, var2, var3, var4);
   }
}
