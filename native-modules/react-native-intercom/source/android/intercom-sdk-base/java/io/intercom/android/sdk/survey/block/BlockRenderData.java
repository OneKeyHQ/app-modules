package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\u0007¢\u0006\u0004\b\n\u0010\u000bJ\u0006\u0010\u0014\u001a\u00020\u0007J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\b\u0017J\t\u0010\u0018\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003JB\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0007HÇ\u0001¢\u0006\u0002\b\u001cJ\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010 \u001a\u00020!H×\u0001J\t\u0010\"\u001a\u00020#H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\t\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "textColor",
         "Landroidx/compose/ui/graphics/Color;",
         "headingTextStyle",
         "Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;",
         "subHeadingTextStyle",
         "paragraphTextStyle",
         "<init>",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Landroidx/compose/ui/graphics/Color;Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getBlock",
         "()Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "getTextColor-QN2ZGVo",
         "()Landroidx/compose/ui/graphics/Color;",
         "getHeadingTextStyle",
         "()Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;",
         "getSubHeadingTextStyle",
         "getParagraphTextStyle",
         "getTextStyle",
         "component1",
         "component2",
         "component2-QN2ZGVo",
         "component3",
         "component4",
         "component5",
         "copy",
         "copy-ZLcQsz0",
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
@StabilityInferred(parameters = 0)
public final class BlockRenderData {
   @NotNull
   private final Block block;
   @Nullable
   private final Color textColor;
   @NotNull
   private final BlockRenderTextStyle headingTextStyle;
   @NotNull
   private final BlockRenderTextStyle subHeadingTextStyle;
   @NotNull
   private final BlockRenderTextStyle paragraphTextStyle;
   public static final int $stable;

   static { $stable = 8; }

   private BlockRenderData(Block var1, Color var2, BlockRenderTextStyle var3, BlockRenderTextStyle var4, BlockRenderTextStyle var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "block");
      Intrinsics.checkNotNullParameter(var3, "headingTextStyle");
      Intrinsics.checkNotNullParameter(var4, "subHeadingTextStyle");
      Intrinsics.checkNotNullParameter(var5, "paragraphTextStyle");
      this.block = var1;
      this.textColor = var2;
      this.headingTextStyle = var3;
      this.subHeadingTextStyle = var4;
      this.paragraphTextStyle = var5;
   }

   // $VF: synthetic method
   public BlockRenderData(
      Block var1, Color var2, BlockRenderTextStyle var3, BlockRenderTextStyle var4, BlockRenderTextStyle var5, int var6, DefaultConstructorMarker var7
   ) {
      if ((var6 & 2) != 0) {
         var2 = null;
      }

      if ((var6 & 4) != 0) {
         var3 = new BlockRenderTextStyle(TextUnitKt.getSp(48), FontWeight.Companion.getBold(), TextUnitKt.getSp(56), null, null, null, 56, null);
      }

      if ((var6 & 8) != 0) {
         var4 = new BlockRenderTextStyle(TextUnitKt.getSp(36), FontWeight.Companion.getSemiBold(), TextUnitKt.getSp(44), null, null, null, 56, null);
      }

      if ((var6 & 16) != 0) {
         var5 = BlockRenderTextStyle.Companion.getParagraphDefault();
      }

      this(var1, var2, var3, var4, var5, null);
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   @Nullable
   public final Color recovered$1b39cf3894296c39() {
      return this.textColor;
   }

   @NotNull
   public final BlockRenderTextStyle getHeadingTextStyle() {
      return this.headingTextStyle;
   }

   @NotNull
   public final BlockRenderTextStyle getSubHeadingTextStyle() {
      return this.subHeadingTextStyle;
   }

   @NotNull
   public final BlockRenderTextStyle getParagraphTextStyle() {
      return this.paragraphTextStyle;
   }

   @NotNull
   public final BlockRenderTextStyle getTextStyle() {
      BlockType var10000 = this.block.getType();
      BlockRenderTextStyle var1;
      switch (var10000 == null ? -1 : BlockRenderData$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
            var1 = this.headingTextStyle;
            break;
         case 2:
            var1 = this.subHeadingTextStyle;
            break;
         case 3:
            var1 = this.paragraphTextStyle;
            break;
         default:
            var1 = BlockRenderTextStyle.Companion.getParagraphDefault();
      }

      return var1;
   }

   @NotNull
   public final Block component1() {
      return this.block;
   }

   @Nullable
   public final Color recovered$0d3a0f8188124050() {
      return this.textColor;
   }

   @NotNull
   public final BlockRenderTextStyle component3() {
      return this.headingTextStyle;
   }

   @NotNull
   public final BlockRenderTextStyle component4() {
      return this.subHeadingTextStyle;
   }

   @NotNull
   public final BlockRenderTextStyle component5() {
      return this.paragraphTextStyle;
   }

   @NotNull
   public final BlockRenderData recovered$17feac2208085ba5(
      @NotNull Block var1, @Nullable Color var2, @NotNull BlockRenderTextStyle var3, @NotNull BlockRenderTextStyle var4, @NotNull BlockRenderTextStyle var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "block");
      Intrinsics.checkNotNullParameter(var3, "headingTextStyle");
      Intrinsics.checkNotNullParameter(var4, "subHeadingTextStyle");
      Intrinsics.checkNotNullParameter(var5, "paragraphTextStyle");
      return new BlockRenderData(var1, var2, var3, var4, var5, null);
   }

   // $VF: synthetic method
   public static BlockRenderData recovered$f8fc62cc766d26ed(
      BlockRenderData var0, Block var1, Color var2, BlockRenderTextStyle var3, BlockRenderTextStyle var4, BlockRenderTextStyle var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.block;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.textColor;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.headingTextStyle;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.subHeadingTextStyle;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.paragraphTextStyle;
      }

      return var0.recovered$17feac2208085ba5(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "BlockRenderData(block="
         + this.block
         + ", textColor="
         + this.textColor
         + ", headingTextStyle="
         + this.headingTextStyle
         + ", subHeadingTextStyle="
         + this.subHeadingTextStyle
         + ", paragraphTextStyle="
         + this.paragraphTextStyle
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.block.hashCode();
      var1 = var1 * 31 + (this.textColor == null ? 0 : Color.recovered$87e060624084b26b(this.textColor.recovered$45f9de7b85e0e0b5()));
      var1 = var1 * 31 + this.headingTextStyle.hashCode();
      var1 = var1 * 31 + this.subHeadingTextStyle.hashCode();
      return var1 * 31 + this.paragraphTextStyle.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BlockRenderData)) {
         return false;
      } else {
         BlockRenderData var2 = (BlockRenderData)var1;
         if (!Intrinsics.areEqual(this.block, var2.block)) {
            return false;
         } else if (!Intrinsics.areEqual(this.textColor, var2.textColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.headingTextStyle, var2.headingTextStyle)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.subHeadingTextStyle, var2.subHeadingTextStyle)
               ? false
               : Intrinsics.areEqual(this.paragraphTextStyle, var2.paragraphTextStyle);
         }
      }
   }

   // $VF: synthetic method
   public BlockRenderData(
      Block var1, Color var2, BlockRenderTextStyle var3, BlockRenderTextStyle var4, BlockRenderTextStyle var5, DefaultConstructorMarker var6
   ) {
      this(var1, var2, var3, var4, var5);
   }
}
