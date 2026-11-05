package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.FloatState;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.PrimitiveSnapshotStateKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\n\u001a#\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0001¢\u0006\u0002\u0010\f\u001a0\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0001H\u0000\u001a$\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0017\u001a\u00020\u0018H\u0000\u001a\f\u0010\u0019\u001a\u00020\u0018*\u00020\u000bH\u0002\u001a\u001a\u0010\u001a\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u0018H\u0002\u001a\u0018\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\u0018H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006 ²\u0006\n\u0010!\u001a\u00020\u0018X\u008a\u0084\u0002²\u0006\n\u0010\"\u001a\u00020\u0001X\u008a\u008e\u0002²\u0006\n\u0010\u0017\u001a\u00020\u0018X\u008a\u0084\u0002",
   d2 = {
         "CatchUpSeconds",
         "",
         "RateSmoothingSeconds",
         "MinUnitsPerSecond",
         "MaxUnitsPerSecond",
         "rememberRevealedBlocks",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/RevealedBlocks;",
         "uuid",
         "",
         "targetBlocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "(Ljava/lang/String;Ljava/util/List;Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/m5/conversation/ui/components/row/RevealedBlocks;",
         "trailingFade",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/TrailingFade;",
         "lineTop",
         "lineBottom",
         "caretX",
         "isRtl",
         "",
         "fadeWidth",
         "truncateBlocksForReveal",
         "blocks",
         "revealedUnits",
         "",
         "revealUnits",
         "partialBlock",
         "block",
         "units",
         "safeHtmlCutIndex",
         "text",
         "index",
         "intercom-sdk-base_release",
         "currentTotal",
         "revealedUnitsRaw"
   }
)
@SourceDebugExtension(
   "SMAP\nStreamingTextReveal.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingTextReveal.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/StreamingTextRevealKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 5 SnapshotFloatState.kt\nandroidx/compose/runtime/PrimitiveSnapshotStateKt__SnapshotFloatStateKt\n*L\n1#1,175:1\n1047#2,3:176\n1050#2,3:180\n1047#2,6:183\n1047#2,6:189\n1047#2,6:195\n1047#2,6:201\n1#3:179\n85#4:207\n85#4:211\n80#5:208\n113#5,2:209\n*S KotlinDebug\n*F\n+ 1 StreamingTextReveal.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/StreamingTextRevealKt\n*L\n36#1:176,3\n36#1:180,3\n38#1:183,6\n40#1:189,6\n61#1:195,6\n62#1:201,6\n37#1:207\n61#1:211\n38#1:208\n38#1:209,2\n*E\n"
)
public final class StreamingTextRevealKt {
   private static final float CatchUpSeconds = 0.4F;
   private static final float RateSmoothingSeconds = 0.3F;
   private static final float MinUnitsPerSecond = 15.0F;
   private static final float MaxUnitsPerSecond = 2000.0F;

   @Composable
   @NotNull
   public static final RevealedBlocks rememberRevealedBlocks(@NotNull String var0, @NotNull List var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "uuid");
      Intrinsics.checkNotNullParameter(var1, "targetBlocks");
      var2.startReplaceGroup(579727885);
      ComposerKt.sourceInformation(
         var2, "C(rememberRevealedBlocks)P(1)35@1400L66,36@1491L32,37@1552L42,39@1621L853,39@1600L874,60@2592L62,61@2666L294:StreamingTextReveal.kt#hr3bi5"
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            579727885, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.rememberRevealedBlocks (StreamingTextReveal.kt:34)"
         );
      }

      var2.startReplaceGroup(355458252);
      ComposerKt.sourceInformation(var2, "CC(remember):StreamingTextReveal.kt#9igjgp");
      Composer var6 = var2;
      boolean var7 = var2.changed(var1);
      int var8 = 0;
      Object var9 = var6.rememberedValue();
      int var10 = 0;
      Object var10000;
      if (!var7 && var9 != Composer.Companion.getEmpty()) {
         var10000 = var9;
      } else {
         int var11 = 0;
         Iterable var12 = var1;
         int var13 = 0;

         for (Object var15 : var12) {
            Block var16 = (Block)var15;
            int var17 = var13;
            int var18 = 0;
            int var19 = revealUnits(var16);
            var13 = var17 + var19;
         }

         Object var20 = var13;
         var6.updateRememberedValue(var20);
         var10000 = var20;
      }

      int var5 = ((Number)var10000).intValue();
      var2.endReplaceGroup();
      int var4 = var5;
      State var22 = SnapshotStateKt.rememberUpdatedState(var4, var2, 0);
      var2.startReplaceGroup(355463092);
      ComposerKt.sourceInformation(var2, "CC(remember):StreamingTextReveal.kt#9igjgp");
      Composer var27 = var2;
      boolean invalid$ivx = (var3 & 14 ^ 6) > 4 && var2.changed(var0) || (var3 & 6) == 4;
      int $i$f$cachex = 0;
      Object it$ivx = var27.rememberedValue();
      int var43/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var59;
      if (!invalid$ivx && it$ivx != Composer.Companion.getEmpty()) {
         var59 = it$ivx;
      } else {
         int var47 = 0;
         Object var48 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
         var27.updateRememberedValue(var48);
         var59 = var48;
      }

      MutableFloatState var24 = (MutableFloatState)var59;
      var2.endReplaceGroup();
      MutableFloatState var23 = var24;
      String var60 = var0;
      var2.startReplaceGroup(355466111);
      ComposerKt.sourceInformation(var2, "CC(remember):StreamingTextReveal.kt#9igjgp");
      Composer var28 = var2;
      boolean invalid$ivxx = var2.changed(var22) | var2.changed(var23);
      int $i$f$cachexx = 0;
      Object it$ivxx = var28.rememberedValue();
      int var44/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
      Object var63;
      if (!invalid$ivxx && it$ivxx != Composer.Companion.getEmpty()) {
         var63 = it$ivxx;
      } else {
         String var21 = var0;
         int var49 = 0;
         Function2 var10001 = new StreamingTextRevealKt$rememberRevealedBlocks$1$1(var22, var23, null);
         var60 = var21;
         Object var52 = var10001;
         var28.updateRememberedValue(var52);
         var63 = var52;
      }

      Function2 var25 = (Function2)var63;
      var2.endReplaceGroup();
      EffectsKt.LaunchedEffect(var60, var25, var2, 14 & var3);
      var2.startReplaceGroup(355496392);
      ComposerKt.sourceInformation(var2, "CC(remember):StreamingTextReveal.kt#9igjgp");
      Composer var33 = var2;
      boolean invalid$ivxxx = (var3 & 14 ^ 6) > 4 && var2.changed(var0) || (var3 & 6) == 4;
      int $i$f$cachexxx = 0;
      Object it$ivxxx = var33.rememberedValue();
      int var50/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
      Object var61;
      if (!invalid$ivxxx && it$ivxxx != Composer.Companion.getEmpty()) {
         var61 = it$ivxxx;
      } else {
         int var53 = 0;
         final androidx.compose.runtime.MutableFloatState recoveredCapture_792_0 = var23;
         Object var55 = SnapshotStateKt.derivedStateOf(((kotlin.jvm.functions.Function0) () -> StreamingTextRevealKt.rememberRevealedBlocks$lambda$8$lambda$7(recoveredCapture_792_0)));
         var33.updateRememberedValue(var55);
         var61 = var55;
      }

      State var29 = (State)var61;
      var2.endReplaceGroup();
      State var26 = var29;
      int var34 = rememberRevealedBlocks$lambda$9(var26);
      var2.startReplaceGroup(355498992);
      ComposerKt.sourceInformation(var2, "CC(remember):StreamingTextReveal.kt#9igjgp");
      Composer var42 = var2;
      boolean invalid$ivxxxx = var2.changed(var1) | var2.changed(var34);
      int $i$f$cachexxxx = 0;
      Object it$ivxxxx = var42.rememberedValue();
      int var56/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
      Object var62;
      if (!invalid$ivxxxx && it$ivxxxx != Composer.Companion.getEmpty()) {
         var62 = it$ivxxxx;
      } else {
         int var57 = 0;
         Object var58 = rememberRevealedBlocks$lambda$9(var26) >= var4
            ? new RevealedBlocks(var1, true)
            : new RevealedBlocks(truncateBlocksForReveal(var1, rememberRevealedBlocks$lambda$9(var26)), false);
         var42.updateRememberedValue(var58);
         var62 = var58;
      }

      RevealedBlocks var38 = (RevealedBlocks)var62;
      var2.endReplaceGroup();
      RevealedBlocks var30 = var38;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var30;
   }

   @NotNull
   public static final TrailingFade trailingFade(float var0, float var1, float var2, boolean var3, float var4) {
      return new TrailingFade(var0, var1, var3 ? var2 + var4 : var2 - var4, var2);
   }

   @NotNull
   public static final List truncateBlocksForReveal(@NotNull List var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "blocks");
      ArrayList var2 = new ArrayList(var0.size());
      int var3 = var1;

      for (java.lang.Object recoveredEntryElement_0_233 : var0) {
         Block var5 = (Block) recoveredEntryElement_0_233;
         int var6 = revealUnits(var5);
         if (var3 < var6) {
            if (var3 > 0) {
               Block var10000 = partialBlock(var5, var3);
               if (var10000 != null) {
                  Block var7 = var10000;
                  int var8 = 0;
                  var2.add(var7);
               }
            }
            break;
         }

         var2.add(var5);
         var3 -= var6;
      }

      return var2;
   }

   private static final int revealUnits(Block var0) {
      String var1 = var0.getText();
      CharSequence var2 = var1;
      if (var2 != null && var2.length() != 0) {
         return var1.length();
      }

      List var11 = var0.getItems();
      Collection var3 = var11;
      if (var3 == null || var3.isEmpty()) {
         return 1;
      }

      Iterable var12 = var11;
      int var4 = 0;

      for (Object var6 : var12) {
         String var7 = (String)var6;
         int var9 = var4;
         int var8 = 0;
         int var10 = Math.max(1, var7.length());
         var4 = var9 + var10;
      }

      return var4;
   }

   private static final Block partialBlock(Block var0, int var1) {
      String var2 = var0.getText();
      CharSequence var3 = var2;
      if (var3 != null && var3.length() != 0) {
         int var11 = safeHtmlCutIndex(var2, var1);
         if (var11 <= 0) {
            return null;
         }

         io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(var0.getType().getSerializedName());
         String var13 = var2.substring(0, var11);
         Intrinsics.checkNotNullExpressionValue(var13, "substring(...)");
         return var10000.withText(var13).build();
      } else {
         List var10 = var0.getItems();
         Collection var4 = var10;
         if (var4 == null || var4.isEmpty()) {
            return null;
         }

         int var12 = var1;
         List var5 = new ArrayList();

         for (java.lang.Object recoveredEntryElement_0_304 : var10) {
            String var7 = (String) recoveredEntryElement_0_304;
            int var8 = Math.max(1, var7.length());
            if (var12 < var8) {
               if (var12 > 0) {
                  int var9 = safeHtmlCutIndex(var7, var12);
                  if (var9 > 0) {
                     String var10001 = var7.substring(0, var9);
                     Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
                     var5.add(var10001);
                  }
               }
               break;
            }

            var5.add(var7);
            var12 -= var8;
         }

         return var5.isEmpty() ? null : new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(var0.getType().getSerializedName()).withItems(var5).build();
      }
   }

   private static final int safeHtmlCutIndex(String var0, int var1) {
      int var2 = 0;
      int var8 = RangesKt.coerceIn(var1, 0, var0.length());
      int var3 = StringsKt.lastIndexOf$default(var0, '<', var8 - 1, false, 4, null);
      if (var3 >= 0) {
         int var5 = StringsKt.indexOf$default(var0, '>', var3, false, 4, null);
         int var6 = 0;
         if (var5 == -1 || var5 >= var8) {
            var8 = var3;
         }
      }

      int var4 = StringsKt.lastIndexOf$default(var0, '&', var8 - 1, false, 4, null);
      if (var4 >= 0 && var8 - var4 <= 10) {
         int var10 = StringsKt.indexOf$default(var0, ';', var4, false, 4, null);
         int var7 = 0;
         if (var10 == -1 || var10 >= var8) {
            var8 = var4;
         }
      }

      if ((1 <= var8 ? var8 < var0.length() : false) && Character.isHighSurrogate(var0.charAt(var8 - 1)) && Character.isLowSurrogate(var0.charAt(var8))) {
         int var9 = var8;
         var8 = var9 + -1;
      }

      return var8;
   }

   private static final int rememberRevealedBlocks$lambda$2(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).intValue();
   }

   private static final float rememberRevealedBlocks$lambda$4(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void rememberRevealedBlocks$lambda$5(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final int rememberRevealedBlocks$lambda$8$lambda$7(MutableFloatState var0) {
      return (int)rememberRevealedBlocks$lambda$4(var0);
   }

   private static final int rememberRevealedBlocks$lambda$9(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).intValue();
   }

   // $VF: synthetic method
   public static final int access$rememberRevealedBlocks$lambda$2(State var0) {
      return rememberRevealedBlocks$lambda$2(var0);
   }

   // $VF: synthetic method
   public static final float access$rememberRevealedBlocks$lambda$4(MutableFloatState var0) {
      return rememberRevealedBlocks$lambda$4(var0);
   }

   // $VF: synthetic method
   public static final void access$rememberRevealedBlocks$lambda$5(MutableFloatState var0, float var1) {
      rememberRevealedBlocks$lambda$5(var0, var1);
   }
}
