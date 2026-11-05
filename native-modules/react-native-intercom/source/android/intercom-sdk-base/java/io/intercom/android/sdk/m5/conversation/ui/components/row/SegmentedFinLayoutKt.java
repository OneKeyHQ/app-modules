package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\\\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aÂ\u0001\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2&\u0010\t\u001a\"\u0012\u0013\u0012\u00110\b¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u00010\n¢\u0006\u0002\b\u000e2&\u0010\u000f\u001a\"\u0012\u0013\u0012\u00110\u0010¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\u00010\n¢\u0006\u0002\b\u000e2;\u0010\u0012\u001a7\u0012\u0013\u0012\u00110\u0010¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0014¢\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0015\u0012\u0004\u0012\u00020\u00010\u0013¢\u0006\u0002\b\u000e2\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0001¢\u0006\u0002\u0010\u0018\u001a<\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0015\u001a\u00020\u00142\u0011\u0010\u001a\u001a\r\u0012\u0004\u0012\u00020\u00010\u001b¢\u0006\u0002\b\u000eH\u0001¢\u0006\u0002\u0010\u001c\u001a\r\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\u001d\u0010\u001f\u001a\u00020 2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010!¨\u0006\"",
   d2 = {
         "SegmentedFinLayout",
         "",
         "segments",
         "",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/ContentSegment;",
         "finAnswerStyle",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;",
         "hasMetadata",
         "",
         "metadata",
         "Lkotlin/Function1;",
         "Lkotlin/ParameterName;",
         "name",
         "compact",
         "Landroidx/compose/runtime/Composable;",
         "textRunBlock",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "block",
         "standaloneBlock",
         "Lkotlin/Function2;",
         "Landroidx/compose/ui/graphics/Shape;",
         "shape",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/util/List;Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;ZLkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function4;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "FinBubble",
         "content",
         "Lkotlin/Function0;",
         "(Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;Landroidx/compose/ui/Modifier;Landroidx/compose/ui/graphics/Shape;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "SegmentedFinLayoutPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "previewBlockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSegmentedFinLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SegmentedFinLayoutKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,168:1\n87#2:169\n85#2,8:170\n94#2:206\n81#3,6:178\n88#3,6:193\n96#3:205\n402#4,9:184\n411#4:199\n412#4,2:203\n1878#5,3:200\n*S KotlinDebug\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SegmentedFinLayoutKt\n*L\n67#1:169\n67#1:170,8\n67#1:206\n67#1:178,6\n67#1:193,6\n67#1:205\n67#1:184,9\n67#1:199\n67#1:203,2\n80#1:200,3\n*E\n"
)
public final class SegmentedFinLayoutKt {
   @Composable
   @ComposableInferredTarget(
      scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable]]"
   )
   public static final void SegmentedFinLayout(
      @NotNull List var0,
      @NotNull FinRowStyle var1,
      boolean var2,
      @NotNull Function3 var3,
      @NotNull Function3 var4,
      @NotNull Function4 var5,
      @Nullable Modifier var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var0, "segments");
      Intrinsics.checkNotNullParameter(var1, "finAnswerStyle");
      Intrinsics.checkNotNullParameter(var3, "metadata");
      Intrinsics.checkNotNullParameter(var4, "textRunBlock");
      Intrinsics.checkNotNullParameter(var5, "standaloneBlock");
      var7 = var7.startRestartGroup(-1076903475);
      ComposerKt.sourceInformation(var7, "C(SegmentedFinLayout)P(4!3,6,5)66@3377L1196:SegmentedFinLayout.kt#hr3bi5");
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var7.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var7.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var7.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var7.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var7.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 64) != 0) {
         var10 |= 1572864;
      } else if ((var8 & 1572864) == 0) {
         var10 |= var7.changed(var6) ? 1048576 : 524288;
      }

      if ((var10 & 599187) == 599186 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         if ((var9 & 64) != 0) {
            var6 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1076903475, var10, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinLayout (SegmentedFinLayout.kt:49)"
            );
         }

         ContentSegment var11 = (ContentSegment)CollectionsKt.firstOrNull(var0);
         boolean var12 = var11 instanceof ContentSegment$TextRun;
         boolean var13 = !var12 && var2;
         boolean var14 = var11 instanceof ContentSegment$Standalone && ((ContentSegment$Standalone)var11).getBlock().getType() == BlockType.TABLE;
         Vertical var15 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(StreamingMessageListKt.getGroupedMessageGap());
         int var17 = 48 | 14 & var10 >> 18;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var7, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var16 = Alignment.Companion.getStart();
         MeasurePolicy var19 = ColumnKt.columnMeasurePolicy(var15, var16, var7, 14 & var17 >> 3 | 112 & var17 >> 3);
         Modifier var20 = var6;
         MeasurePolicy var21 = var19;
         Composer var22 = var7;
         int var23 = 112 & var17 << 3;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
         CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var22, var20);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var22;
         int var30 = 6 | 896 & var23 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var37 = 6 | 112 & var17 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, -1143442123, "C:SegmentedFinLayout.kt#hr3bi5");
         var38.startReplaceGroup(-175433216);
         ComposerKt.sourceInformation(var38, "76@3875L28,68@3522L381");
         if (var13) {
            FinBubble(
               var1,
               null,
               BubbleMessageRowKt.calculateGroupedCornerShape(false, true, true, var1.getBubbleStyle().getShape()),
               ComposableLambdaKt.rememberComposableLambda(-354473133, true, new SegmentedFinLayoutKt$SegmentedFinLayout$1$1(var3, var14), var38, 54),
               var38,
               3072 | 14 & var10 >> 3,
               2
            );
         }

         var38.endReplaceGroup();
         var38.startReplaceGroup(-175418488);
         ComposerKt.sourceInformation(var38, "");
         Iterable var41 = var0;
         int var42 = 0;
         int var43 = 0;

         for (Object var45 : var41) {
            int var46 = var43++;
            if (var46 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            ContentSegment var47 = (ContentSegment)var45;
            int var48 = var46;
            int var49 = 0;
            ContentSegment var50 = var47;
            if (var50 instanceof ContentSegment$TextRun) {
               var38.startReplaceGroup(1706014394);
               ComposerKt.sourceInformation(var38, "84@4175L264,81@4041L398");
               FinBubble(
                  var1,
                  null,
                  SegmentedFinLayout$shapeForSegment(var13, var0, var1, var48),
                  ComposableLambdaKt.rememberComposableLambda(
                     256662105, true, new SegmentedFinLayoutKt$SegmentedFinLayout$1$2$1(var48, var12, var3, var47, var4), var38, 54
                  ),
                  var38,
                  3072 | 14 & var10 >> 3,
                  2
               );
               var38.endReplaceGroup();
            } else {
               if (!(var50 instanceof ContentSegment$Standalone)) {
                  var38.startReplaceGroup(1706012590);
                  var38.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var38.startReplaceGroup(1706028386);
               ComposerKt.sourceInformation(var38, "93@4489L54");
               var5.invoke(((ContentSegment$Standalone)var47).getBlock(), SegmentedFinLayout$shapeForSegment(var13, var0, var1, var48), var38, 896 & var10 >> 9);
               var38.endReplaceGroup();
            }
         }

         var38.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var52 = var7.endRestartGroup();
      if (var52 != null) {
         final java.util.List recoveredCapture_780_0 = var0;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle recoveredCapture_780_1 = var1;
         final boolean recoveredCapture_780_2 = var2;
         final kotlin.jvm.functions.Function3 recoveredCapture_780_3 = var3;
         final kotlin.jvm.functions.Function3 recoveredCapture_780_4 = var4;
         final kotlin.jvm.functions.Function4 recoveredCapture_780_5 = var5;
         final androidx.compose.ui.Modifier recoveredCapture_780_6 = var6;
         final int recoveredCapture_780_7 = var8;
         final int recoveredCapture_780_8 = var9;
         var52.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_780_0, recoveredArgument_780_1) -> SegmentedFinLayoutKt.SegmentedFinLayout$lambda$2(recoveredCapture_780_0, recoveredCapture_780_1, recoveredCapture_780_2, recoveredCapture_780_3, recoveredCapture_780_4, recoveredCapture_780_5, recoveredCapture_780_6, recoveredCapture_780_7, recoveredCapture_780_8, (androidx.compose.runtime.Composer)recoveredArgument_780_0, ((java.lang.Integer)recoveredArgument_780_1).intValue())));
      }
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void FinBubble(
      @NotNull FinRowStyle var0, @Nullable Modifier var1, @Nullable Shape var2, @NotNull Function2 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "finAnswerStyle");
      Intrinsics.checkNotNullParameter(var3, "content");
      var4 = var4.startRestartGroup(311357894);
      ComposerKt.sourceInformation(var4, "C(FinBubble)P(1,2,3)112@5035L207,107@4860L382:SegmentedFinLayout.kt#hr3bi5");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         var4.startDefaults();
         if ((var5 & 1) != 0 && !var4.getDefaultsInvalid()) {
            var4.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = var0.getBubbleStyle().getShape();
               var7 &= -897;
            }
         }

         var4.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(311357894, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinBubble (SegmentedFinLayout.kt:106)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            var1,
            var2,
            var0.getBubbleStyle().recovered$6f15fcf6c761ad06(),
            0L,
            0.0F,
            0.0F,
            var0.getBubbleStyle().getBorderStroke(),
            ComposableLambdaKt.rememberComposableLambda(2000397323, true, new SegmentedFinLayoutKt$FinBubble$1(var0, var3), var4, 54),
            var4,
            12582912 | 14 & var7 >> 3 | 112 & var7 >> 3,
            56
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle recoveredCapture_779_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_779_1 = var1;
         final androidx.compose.ui.graphics.Shape recoveredCapture_779_2 = var2;
         final kotlin.jvm.functions.Function2 recoveredCapture_779_3 = var3;
         final int recoveredCapture_779_4 = var5;
         final int recoveredCapture_779_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_779_0, recoveredArgument_779_1) -> SegmentedFinLayoutKt.FinBubble$lambda$3(recoveredCapture_779_0, recoveredCapture_779_1, recoveredCapture_779_2, recoveredCapture_779_3, recoveredCapture_779_4, recoveredCapture_779_5, (androidx.compose.runtime.Composer)recoveredArgument_779_0, ((java.lang.Integer)recoveredArgument_779_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedFinLayoutPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(289825507);
      ComposerKt.sourceInformation(var0, "C(SegmentedFinLayoutPreview)130@5810L1041:SegmentedFinLayout.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               289825507, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinLayoutPreview (SegmentedFinLayout.kt:129)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SegmentedFinLayoutKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_781_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_781_0, recoveredArgument_781_1) -> SegmentedFinLayoutKt.SegmentedFinLayoutPreview$lambda$4(recoveredCapture_781_0, (androidx.compose.runtime.Composer)recoveredArgument_781_0, ((java.lang.Integer)recoveredArgument_781_1).intValue())));
      }
   }

   @Composable
   private static final BlockRenderData previewBlockRenderData(Block var0, FinRowStyle var1, Composer var2, int var3) {
      var2.startReplaceGroup(-1424318963);
      ComposerKt.sourceInformation(var2, "C(previewBlockRenderData)162@7028L49,164@7194L6:SegmentedFinLayout.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1424318963, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.previewBlockRenderData (SegmentedFinLayout.kt:160)"
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

   private static final Shape SegmentedFinLayout$shapeForSegment(boolean var0, List var1, FinRowStyle var2, int var3) {
      return BubbleMessageRowKt.calculateGroupedCornerShape(var3 > 0 || var0, var3 < CollectionsKt.getLastIndex(var1), true, var2.getBubbleStyle().getShape());
   }

   private static final Unit SegmentedFinLayout$lambda$2(
      List var0, FinRowStyle var1, boolean var2, Function3 var3, Function3 var4, Function4 var5, Modifier var6, int var7, int var8, Composer var9, int var10
   ) {
      SegmentedFinLayout(var0, var1, var2, var3, var4, var5, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit FinBubble$lambda$3(FinRowStyle var0, Modifier var1, Shape var2, Function2 var3, int var4, int var5, Composer var6, int var7) {
      FinBubble(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinLayoutPreview$lambda$4(int var0, Composer var1, int var2) {
      SegmentedFinLayoutPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final BlockRenderData access$previewBlockRenderData(Block var0, FinRowStyle var1, Composer var2, int var3) {
      return previewBlockRenderData(var0, var1, var2, var3);
   }
}
