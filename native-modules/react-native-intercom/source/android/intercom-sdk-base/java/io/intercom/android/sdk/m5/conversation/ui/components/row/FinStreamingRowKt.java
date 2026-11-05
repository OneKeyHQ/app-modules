package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.TableCell$Builder;
import io.intercom.android.sdk.blocks.lib.models.TableRow$Builder;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.StreamingPart;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\u001aK\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\nH\u0001¢\u0006\u0002\u0010\r\u001a\u001d\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0012H\u0003¢\u0006\u0002\u0010\u0013\u001a\b\u0010\u0014\u001a\u00020\u0004H\u0002\u001a\b\u0010\u0015\u001a\u00020\u0004H\u0002\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001a\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001c\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\"\u000e\u0010\u0016\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e",
   d2 = {
         "FinStreamingRow",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "streamingPart",
         "Lio/intercom/android/sdk/models/StreamingPart;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "useRevealAnimation",
         "",
         "isCaughtUp",
         "suppressAvatarHeader",
         "(Ljava/util/List;Lio/intercom/android/sdk/models/StreamingPart;Landroidx/compose/ui/Modifier;ZZZLandroidx/compose/runtime/Composer;II)V",
         "streamingBlockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "block",
         "finAnswerStyle",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "sampleStreamingTableBlock",
         "sampleGeneratingTableBlock",
         "streamingPreviewMetadata",
         "FinStreamingRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "AvatarHeaderSuppressedFinStreamingRowPreview",
         "FinStreamingRowSegmentedTablePreview",
         "FinStreamingRowSegmentedGeneratingTablePreview",
         "FinStreamingRowSegmentedTrailingCodePreview",
         "FinStreamingRowSegmentedCodePreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFinStreamingRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinStreamingRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinStreamingRowKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,307:1\n70#2:308\n67#2,9:309\n77#2:343\n81#3,6:318\n88#3,6:333\n96#3:342\n402#4,9:324\n411#4,3:339\n*S KotlinDebug\n*F\n+ 1 FinStreamingRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinStreamingRowKt\n*L\n60#1:308\n60#1:309,9\n60#1:343\n60#1:318,6\n60#1:333,6\n60#1:342\n60#1:324,9\n60#1:339,3\n*E\n"
)
public final class FinStreamingRowKt {
   @NotNull
   private static final StreamingPart streamingPreviewMetadata = new StreamingPart(
      new io.intercom.android.sdk.models.Metadata(
         "Bot", "AI Agent", 1726738186L, CollectionsKt.listOf(new Avatar$Builder().withInitials("BB").withShape(AvatarShape.SQUIRCLE))
      )
   );

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinStreamingRow(
      @NotNull List var0,
      @NotNull StreamingPart var1,
      @Nullable Modifier var2,
      boolean var3,
      boolean var4,
      boolean var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var0, "blocks");
      Intrinsics.checkNotNullParameter(var1, "streamingPart");
      var6 = var6.startRestartGroup(-810667655);
      ComposerKt.sourceInformation(var6, "C(FinStreamingRow)P(!1,3,2,5)40@1803L43,42@1907L549,59@2502L1726:FinStreamingRow.kt#hr3bi5");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changedInstance(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changed(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var8 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var8 & 8) != 0) {
            var3 = false;
         }

         if ((var8 & 16) != 0) {
            var4 = true;
         }

         if ((var8 & 32) != 0) {
            var5 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -810667655, var9, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRow (FinStreamingRow.kt:39)"
            );
         }

         FinRowStyle var10 = FinAnswerRowKt.getFinRowStyle(GroupingPosition.STANDALONE, var6, 6);
         Function3 var11 = ComposableLambdaKt.rememberComposableLambda(
            -1358447106, true, new FinStreamingRowKt$FinStreamingRow$metadata$1(var5, var1), var6, 54
         );
         Block var12 = (Block)CollectionsKt.lastOrNull(var0);
         Modifier var13 = PaddingKt.padding(var2, var10.getRowPadding());
         byte var16 = 0;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var6, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var14 = Alignment.Companion.getTopStart();
         boolean var15 = false;
         MeasurePolicy var18 = BoxKt.maybeCachedBoxMeasurePolicy(var14, var15);
         Modifier var19 = var13;
         MeasurePolicy var20 = var18;
         Composer var21 = var6;
         int var22 = 112 & var16 << 3;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var24 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var21, 0));
         CompositionLocalMap var25 = var21.getCurrentCompositionLocalMap();
         Modifier var26 = ComposedModifierKt.materializeModifier(var21, var19);
         Function0 var27 = ComposeUiNode.Companion.getConstructor();
         Composer var28 = var21;
         int var29 = 6 | 896 & var22 << 6;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var28.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var28.startReusableNode();
         if (var28.getInserting()) {
            var28.createNode(var27);
         } else {
            var28.useNode();
         }

         Composer var31 = Updater.recovered$676aa17b44fd6280(var28);
         int var32 = 0;
         Updater.recovered$89682b199a142880(var31, var20, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var31, var25, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var31, var24, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var31, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var31, var26, ComposeUiNode.Companion.getSetModifier());
         int var33 = 14 & var29 >> 6;
         Composer var34 = var28;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var36 = 6 | 112 & var16 >> 6;
         Composer var37 = var34;
         BoxScope var38 = BoxScopeInstance.INSTANCE;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, -744375503, "C72@3212L645,86@3889L322,67@2947L1275:FinStreamingRow.kt#hr3bi5");
         SegmentedFinLayoutKt.SegmentedFinLayout(
            ContentSegmentKt.segmentStandaloneBlocks(var0),
            var10,
            var1.getMetadata() != null && !var5,
            var11,
            ComposableLambdaKt.rememberComposableLambda(457444246, true, new FinStreamingRowKt$FinStreamingRow$1$1(var3, var10, var12, var4), var37, 54),
            ComposableLambdaKt.rememberComposableLambda(305700234, true, new FinStreamingRowKt$FinStreamingRow$1$2(var10), var37, 54),
            null,
            var37,
            224256,
            64
         );
         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var28.endNode();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var6.endRestartGroup();
      if (var10000 != null) {
         final java.util.List recoveredCapture_730_0 = var0;
         final io.intercom.android.sdk.models.StreamingPart recoveredCapture_730_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_730_2 = var2;
         final boolean recoveredCapture_730_3 = var3;
         final boolean recoveredCapture_730_4 = var4;
         final boolean recoveredCapture_730_5 = var5;
         final int recoveredCapture_730_6 = var7;
         final int recoveredCapture_730_7 = var8;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_730_0, recoveredArgument_730_1) -> FinStreamingRowKt.FinStreamingRow$lambda$1(recoveredCapture_730_0, recoveredCapture_730_1, recoveredCapture_730_2, recoveredCapture_730_3, recoveredCapture_730_4, recoveredCapture_730_5, recoveredCapture_730_6, recoveredCapture_730_7, (androidx.compose.runtime.Composer)recoveredArgument_730_0, ((java.lang.Integer)recoveredArgument_730_1).intValue())));
      }
   }

   @Composable
   private static final BlockRenderData streamingBlockRenderData(Block var0, FinRowStyle var1, Composer var2, int var3) {
      var2.startReplaceGroup(-1966094399);
      ComposerKt.sourceInformation(var2, "C(streamingBlockRenderData)102@4407L49,104@4573L6:FinStreamingRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1966094399, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.streamingBlockRenderData (FinStreamingRow.kt:100)"
         );
      }

      BlockRenderData var4 = new BlockRenderData(
         var0,
         Color.recovered$f46ef195ad4a2363(ColorSchemeKt.recovered$e73d735ab97f03b1(var1.getBubbleStyle().recovered$6f15fcf6c761ad06(), var2, 0)),
         null,
         null,
         BlockRenderTextStyle.recovered$58c9c7402a07ca08(
            BlockRenderTextStyle.Companion.getParagraphDefault(),
            0L,
            null,
            0L,
            null,
            Color.recovered$f46ef195ad4a2363(IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d()),
            null,
            47,
            null
         ),
         12,
         null
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var4;
   }

   private static final Block sampleStreamingTableBlock() {
      Block$Builder var10000 = new Block$Builder().withType(BlockType.TABLE.getSerializedName());
      TableRow$Builder[] var0 = new TableRow$Builder[3];
      TableCell$Builder[] var1 = new TableCell$Builder[]{sampleStreamingTableBlock$cell("Plan"), sampleStreamingTableBlock$cell("Price")};
      var0[0] = sampleStreamingTableBlock$row(var1);
      var1 = new TableCell$Builder[]{sampleStreamingTableBlock$cell("Starter"), sampleStreamingTableBlock$cell("$0")};
      var0[1] = sampleStreamingTableBlock$row(var1);
      var1 = new TableCell$Builder[]{sampleStreamingTableBlock$cell("Pro"), sampleStreamingTableBlock$cell("$49")};
      var0[2] = sampleStreamingTableBlock$row(var1);
      Block var4 = var10000.withRows(CollectionsKt.listOf(var0)).build();
      Intrinsics.checkNotNullExpressionValue(var4, "build(...)");
      return var4;
   }

   private static final Block sampleGeneratingTableBlock() {
      Block$Builder var10000 = new Block$Builder().withType(BlockType.TABLE.getSerializedName()).withCaption("Pricing plans");
      TableCell$Builder[] var0 = new TableCell$Builder[]{sampleGeneratingTableBlock$cell$2("Plan"), sampleGeneratingTableBlock$cell$2("Price")};
      Block var1 = var10000.withRows(CollectionsKt.listOf(sampleGeneratingTableBlock$row$3(var0))).build();
      Intrinsics.checkNotNullExpressionValue(var1, "build(...)");
      return var1;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FinStreamingRowPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1248993407);
      ComposerKt.sourceInformation(var0, "C(FinStreamingRowPreview)157@6561L783:FinStreamingRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1248993407, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRowPreview (FinStreamingRow.kt:155)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(false);
         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FinStreamingRowKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_731_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_731_0, recoveredArgument_731_1) -> FinStreamingRowKt.FinStreamingRowPreview$lambda$4(recoveredCapture_731_0, (androidx.compose.runtime.Composer)recoveredArgument_731_0, ((java.lang.Integer)recoveredArgument_731_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AvatarHeaderSuppressedFinStreamingRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2038866587);
      ComposerKt.sourceInformation(var0, "C(AvatarHeaderSuppressedFinStreamingRowPreview)184@7576L672:FinStreamingRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2038866587,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.AvatarHeaderSuppressedFinStreamingRowPreview (FinStreamingRow.kt:182)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(false);
         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FinStreamingRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_729_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_729_0, recoveredArgument_729_1) -> FinStreamingRowKt.AvatarHeaderSuppressedFinStreamingRowPreview$lambda$5(recoveredCapture_729_0, (androidx.compose.runtime.Composer)recoveredArgument_729_0, ((java.lang.Integer)recoveredArgument_729_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinStreamingRowSegmentedTablePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2054063813);
      ComposerKt.sourceInformation(var0, "C(FinStreamingRowSegmentedTablePreview)211@8522L622:FinStreamingRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2054063813, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRowSegmentedTablePreview (FinStreamingRow.kt:209)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(true);
         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FinStreamingRowKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_734_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_734_0, recoveredArgument_734_1) -> FinStreamingRowKt.FinStreamingRowSegmentedTablePreview$lambda$6(recoveredCapture_734_0, (androidx.compose.runtime.Composer)recoveredArgument_734_0, ((java.lang.Integer)recoveredArgument_734_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinStreamingRowSegmentedGeneratingTablePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-915334761);
      ComposerKt.sourceInformation(var0, "C(FinStreamingRowSegmentedGeneratingTablePreview)238@9497L523:FinStreamingRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -915334761,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRowSegmentedGeneratingTablePreview (FinStreamingRow.kt:236)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(true);
         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FinStreamingRowKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_733_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_733_0, recoveredArgument_733_1) -> FinStreamingRowKt.FinStreamingRowSegmentedGeneratingTablePreview$lambda$7(recoveredCapture_733_0, (androidx.compose.runtime.Composer)recoveredArgument_733_0, ((java.lang.Integer)recoveredArgument_733_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinStreamingRowSegmentedTrailingCodePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1459010864);
      ComposerKt.sourceInformation(var0, "C(FinStreamingRowSegmentedTrailingCodePreview)263@10281L512:FinStreamingRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1459010864,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRowSegmentedTrailingCodePreview (FinStreamingRow.kt:261)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(false);
         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FinStreamingRowKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_735_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_735_0, recoveredArgument_735_1) -> FinStreamingRowKt.FinStreamingRowSegmentedTrailingCodePreview$lambda$8(recoveredCapture_735_0, (androidx.compose.runtime.Composer)recoveredArgument_735_0, ((java.lang.Integer)recoveredArgument_735_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinStreamingRowSegmentedCodePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(856244204);
      ComposerKt.sourceInformation(var0, "C(FinStreamingRowSegmentedCodePreview)287@10994L560:FinStreamingRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               856244204, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRowSegmentedCodePreview (FinStreamingRow.kt:285)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(false);
         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FinStreamingRowKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_732_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_732_0, recoveredArgument_732_1) -> FinStreamingRowKt.FinStreamingRowSegmentedCodePreview$lambda$9(recoveredCapture_732_0, (androidx.compose.runtime.Composer)recoveredArgument_732_0, ((java.lang.Integer)recoveredArgument_732_1).intValue())));
      }
   }

   private static final Unit FinStreamingRow$lambda$1(
      List var0, StreamingPart var1, Modifier var2, boolean var3, boolean var4, boolean var5, int var6, int var7, Composer var8, int var9
   ) {
      FinStreamingRow(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final TableCell$Builder sampleStreamingTableBlock$cell(String var0) {
      return new TableCell$Builder().withContent(CollectionsKt.listOf(new Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final TableRow$Builder sampleStreamingTableBlock$row(TableCell$Builder... var0) {
      return new TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   private static final TableCell$Builder sampleGeneratingTableBlock$cell$2(String var0) {
      return new TableCell$Builder().withContent(CollectionsKt.listOf(new Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final TableRow$Builder sampleGeneratingTableBlock$row$3(TableCell$Builder... var0) {
      return new TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   private static final Unit FinStreamingRowPreview$lambda$4(int var0, Composer var1, int var2) {
      FinStreamingRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AvatarHeaderSuppressedFinStreamingRowPreview$lambda$5(int var0, Composer var1, int var2) {
      AvatarHeaderSuppressedFinStreamingRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FinStreamingRowSegmentedTablePreview$lambda$6(int var0, Composer var1, int var2) {
      FinStreamingRowSegmentedTablePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FinStreamingRowSegmentedGeneratingTablePreview$lambda$7(int var0, Composer var1, int var2) {
      FinStreamingRowSegmentedGeneratingTablePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FinStreamingRowSegmentedTrailingCodePreview$lambda$8(int var0, Composer var1, int var2) {
      FinStreamingRowSegmentedTrailingCodePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FinStreamingRowSegmentedCodePreview$lambda$9(int var0, Composer var1, int var2) {
      FinStreamingRowSegmentedCodePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final StreamingPart access$getStreamingPreviewMetadata$p() {
      return streamingPreviewMetadata;
   }

   // $VF: synthetic method
   public static final Block access$sampleStreamingTableBlock() {
      return sampleStreamingTableBlock();
   }

   // $VF: synthetic method
   public static final Block access$sampleGeneratingTableBlock() {
      return sampleGeneratingTableBlock();
   }

   // $VF: synthetic method
   public static final BlockRenderData access$streamingBlockRenderData(Block var0, FinRowStyle var1, Composer var2, int var3) {
      return streamingBlockRenderData(var0, var1, var2, var3);
   }
}
