package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.TextUnit;
import androidx.compose.ui.unit.TextUnitKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u0000 12\u00020\u0001:\u00011BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\f\u0010\rJ\r\u0010\u0019\u001a\u00020\u001aH\u0000¢\u0006\u0002\b\u001bJ\u0010\u0010\u001c\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u001d\u0010\u000fJ\t\u0010\u001e\u001a\u00020\u0005HÆ\u0003J\u0010\u0010\u001f\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b \u0010\u000fJ\u0010\u0010!\u001a\u0004\u0018\u00010\bHÆ\u0003¢\u0006\u0002\b\"J\u0010\u0010#\u001a\u0004\u0018\u00010\bHÆ\u0003¢\u0006\u0002\b$J\u0010\u0010%\u001a\u0004\u0018\u00010\u000bHÆ\u0003¢\u0006\u0002\b&JR\u0010'\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bHÇ\u0001¢\u0006\u0004\b(\u0010)J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010-\u001a\u00020.H×\u0001J\t\u0010/\u001a\u000200H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\u0006\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0013\u0010\u000fR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\t\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u00062",
   d2 = {
         "Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;",
         "",
         "fontSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "lineHeight",
         "textColor",
         "Landroidx/compose/ui/graphics/Color;",
         "linkTextColor",
         "textAlign",
         "Landroidx/compose/ui/text/style/TextAlign;",
         "<init>",
         "(JLandroidx/compose/ui/text/font/FontWeight;JLandroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/text/style/TextAlign;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getFontSize-XSAIIZE",
         "()J",
         "J",
         "getFontWeight",
         "()Landroidx/compose/ui/text/font/FontWeight;",
         "getLineHeight-XSAIIZE",
         "getTextColor-QN2ZGVo",
         "()Landroidx/compose/ui/graphics/Color;",
         "getLinkTextColor-QN2ZGVo",
         "getTextAlign-buA522U",
         "()Landroidx/compose/ui/text/style/TextAlign;",
         "toTextStyle",
         "Landroidx/compose/ui/text/TextStyle;",
         "toTextStyle$intercom_sdk_base_release",
         "component1",
         "component1-XSAIIZE",
         "component2",
         "component3",
         "component3-XSAIIZE",
         "component4",
         "component4-QN2ZGVo",
         "component5",
         "component5-QN2ZGVo",
         "component6",
         "component6-buA522U",
         "copy",
         "copy--ZsBm6Y",
         "(JLandroidx/compose/ui/text/font/FontWeight;JLandroidx/compose/ui/graphics/Color;Landroidx/compose/ui/graphics/Color;Landroidx/compose/ui/text/style/TextAlign;)Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class BlockRenderTextStyle {
   @NotNull
   public static final BlockRenderTextStyle$Companion Companion = new BlockRenderTextStyle$Companion(null);
   private final long fontSize;
   @NotNull
   private final FontWeight fontWeight;
   private final long lineHeight;
   @Nullable
   private final Color textColor;
   @Nullable
   private final Color linkTextColor;
   @Nullable
   private final TextAlign textAlign;
   public static int $stable;

   static {}
   @NotNull
   private static final BlockRenderTextStyle paragraphDefault = new BlockRenderTextStyle(
      TextUnitKt.getSp(16), FontWeight.Companion.getNormal(), 0L, null, null, null, 60, null
   );

   private BlockRenderTextStyle(long var1, FontWeight var3, long var4, Color var6, Color var7, TextAlign var8) {
      super();
      Intrinsics.checkNotNullParameter(var3, "fontWeight");
      this.fontSize = var1;
      this.fontWeight = var3;
      this.lineHeight = var4;
      this.textColor = var6;
      this.linkTextColor = var7;
      this.textAlign = var8;
   }

   // $VF: synthetic method
   public BlockRenderTextStyle(long var1, FontWeight var3, long var4, Color var6, Color var7, TextAlign var8, int var9, DefaultConstructorMarker var10) {
      if ((var9 & 4) != 0) {
         var4 = TextUnit.Companion.recovered$6e123da0375f4f2f();
      }

      if ((var9 & 8) != 0) {
         var6 = null;
      }

      if ((var9 & 16) != 0) {
         var7 = var6;
      }

      if ((var9 & 32) != 0) {
         var8 = null;
      }

      this(var1, var3, var4, var6, var7, var8, null);
   }

   public final long recovered$8f8bc2178e9073c3() {
      return this.fontSize;
   }

   @NotNull
   public final FontWeight getFontWeight() {
      return this.fontWeight;
   }

   public final long recovered$17d1bb629b307c0f() {
      return this.lineHeight;
   }

   @Nullable
   public final Color recovered$1b39cf3894296c39() {
      return this.textColor;
   }

   @Nullable
   public final Color recovered$25e2f784684f6a30() {
      return this.linkTextColor;
   }

   @Nullable
   public final TextAlign recovered$2a7d0969a6d1c1b4() {
      return this.textAlign;
   }

   @NotNull
   public final TextStyle toTextStyle$intercom_sdk_base_release() {
      long var1 = this.textColor != null ? this.textColor.recovered$45f9de7b85e0e0b5() : Color.Companion.recovered$37fe95da52e29a80();
      long var3 = this.fontSize;
      FontWeight var5 = this.fontWeight;
      long var6 = this.lineHeight;
      int var8 = this.textAlign != null ? this.textAlign.recovered$a4a926e155faa205() : TextAlign.Companion.recovered$4fce64874a0d5cf7();
      return new TextStyle(
         var1, var3, var5, null, null, null, null, 0L, null, null, null, 0L, null, null, null, var8, 0, var6, null, null, null, 0, 0, null, 16613368, null
      );
   }

   public final long recovered$77fc9e164b00e5af() {
      return this.fontSize;
   }

   @NotNull
   public final FontWeight component2() {
      return this.fontWeight;
   }

   public final long recovered$25333608b1bc7bd0() {
      return this.lineHeight;
   }

   @Nullable
   public final Color recovered$83d73a3e3c88bbbe() {
      return this.textColor;
   }

   @Nullable
   public final Color recovered$24816913fd54cc67() {
      return this.linkTextColor;
   }

   @Nullable
   public final TextAlign recovered$4893dbd6036720cf() {
      return this.textAlign;
   }

   @NotNull
   public final BlockRenderTextStyle recovered$92bdea79e996002b(
      long var1, @NotNull FontWeight var3, long var4, @Nullable Color var6, @Nullable Color var7, @Nullable TextAlign var8
   ) {
      Intrinsics.checkNotNullParameter(var3, "fontWeight");
      return new BlockRenderTextStyle(var1, var3, var4, var6, var7, var8, null);
   }

   // $VF: synthetic method
   public static BlockRenderTextStyle recovered$58c9c7402a07ca08(
      BlockRenderTextStyle var0, long var1, FontWeight var3, long var4, Color var6, Color var7, TextAlign var8, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.fontSize;
      }

      if ((var9 & 2) != 0) {
         var3 = var0.fontWeight;
      }

      if ((var9 & 4) != 0) {
         var4 = var0.lineHeight;
      }

      if ((var9 & 8) != 0) {
         var6 = var0.textColor;
      }

      if ((var9 & 16) != 0) {
         var7 = var0.linkTextColor;
      }

      if ((var9 & 32) != 0) {
         var8 = var0.textAlign;
      }

      return var0.recovered$92bdea79e996002b(var1, var3, var4, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "BlockRenderTextStyle(fontSize="
         + TextUnit.recovered$cfef63bb1d111f9f(this.fontSize)
         + ", fontWeight="
         + this.fontWeight
         + ", lineHeight="
         + TextUnit.recovered$cfef63bb1d111f9f(this.lineHeight)
         + ", textColor="
         + this.textColor
         + ", linkTextColor="
         + this.linkTextColor
         + ", textAlign="
         + this.textAlign
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = TextUnit.recovered$87e060624084b26b(this.fontSize);
      var1 = var1 * 31 + this.fontWeight.hashCode();
      var1 = var1 * 31 + TextUnit.recovered$87e060624084b26b(this.lineHeight);
      var1 = var1 * 31 + (this.textColor == null ? 0 : Color.recovered$87e060624084b26b(this.textColor.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + (this.linkTextColor == null ? 0 : Color.recovered$87e060624084b26b(this.linkTextColor.recovered$45f9de7b85e0e0b5()));
      return var1 * 31 + (this.textAlign == null ? 0 : TextAlign.recovered$a3c740bc6edecd13(this.textAlign.recovered$a4a926e155faa205()));
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BlockRenderTextStyle)) {
         return false;
      } else {
         BlockRenderTextStyle var2 = (BlockRenderTextStyle)var1;
         if (!TextUnit.recovered$9eab66ea8c46e11c(this.fontSize, var2.fontSize)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fontWeight, var2.fontWeight)) {
            return false;
         } else if (!TextUnit.recovered$9eab66ea8c46e11c(this.lineHeight, var2.lineHeight)) {
            return false;
         } else if (!Intrinsics.areEqual(this.textColor, var2.textColor)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.linkTextColor, var2.linkTextColor) ? false : Intrinsics.areEqual(this.textAlign, var2.textAlign);
         }
      }
   }

   // $VF: synthetic method
   public BlockRenderTextStyle(long var1, FontWeight var3, long var4, Color var6, Color var7, TextAlign var8, DefaultConstructorMarker var9) {
      this(var1, var3, var4, var6, var7, var8);
   }

   // $VF: synthetic method
   public static final BlockRenderTextStyle access$getParagraphDefault$cp() {
      return paragraphDefault;
   }
}
