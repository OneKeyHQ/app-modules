package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.animation.core.AnimateAsStateKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.geometry.Size;
import androidx.compose.ui.graphics.BlendMode;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.CompositingStrategy;
import androidx.compose.ui.graphics.GraphicsLayerModifierKt;
import androidx.compose.ui.graphics.GraphicsLayerScope;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.graphics.drawscope.ContentDrawScope;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.style.ResolvedTextDirection;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming;
import io.intercom.android.sdk.models.StreamingPart;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.survey.block.ImageRenderType;
import io.intercom.android.sdk.survey.block.SuffixText;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.flow.StateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000D\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\u001a/\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\f\u001a'\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000b2\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0014\u001a\u00020\u0004H\u0003¢\u0006\u0002\u0010\u0015\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016²\u0006\n\u0010\u0017\u001a\u00020\u0007X\u008a\u0084\u0002²\u0006\n\u0010\u0018\u001a\u00020\u000bX\u008a\u008e\u0002²\u0006\n\u0010\u0019\u001a\u00020\u001aX\u008a\u0084\u0002²\u0006\n\u0010\u001b\u001a\u00020\u001aX\u008a\u0084\u0002²\u0006\f\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u008a\u008e\u0002",
   d2 = {
         "TrailingFadeWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "StreamingMessageContent",
         "",
         "stateFlow",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "suppressAvatarHeader",
         "",
         "(Lkotlinx/coroutines/flow/StateFlow;Landroidx/compose/ui/Modifier;ZLandroidx/compose/runtime/Composer;II)V",
         "StreamingRevealBlock",
         "blockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "fadeTrailingEdge",
         "(Lio/intercom/android/sdk/survey/block/BlockRenderData;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "previewMetadata",
         "Lio/intercom/android/sdk/models/Metadata;",
         "StreamingMessageContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "state",
         "appeared",
         "appearAlpha",
         "",
         "edgeAlpha",
         "textLayout",
         "Landroidx/compose/ui/text/TextLayoutResult;"
   }
)
@SourceDebugExtension(
   "SMAP\nStreamingMessageContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/StreamingMessageContentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 6 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n+ 7 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 8 Size.kt\nandroidx/compose/ui/geometry/SizeKt\n+ 9 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,156:1\n1047#2,6:157\n1047#2,6:163\n1047#2,6:171\n1047#2,6:177\n1047#2,6:183\n1047#2,6:189\n75#3:169\n1#4:170\n85#5:195\n85#5:196\n117#5,2:197\n85#5:199\n85#5:200\n85#5:201\n117#5,2:202\n30#6:204\n53#7,3:205\n53#7,3:209\n33#8:208\n118#9:212\n*S KotlinDebug\n*F\n+ 1 StreamingMessageContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/StreamingMessageContentKt\n*L\n76#1:157,6\n77#1:163,6\n81#1:171,6\n84#1:177,6\n90#1:183,6\n124#1:189,6\n80#1:169\n50#1:195\n76#1:196\n76#1:197,2\n78#1:199\n79#1:200\n81#1:201\n81#1:202,2\n115#1:204\n115#1:205,3\n116#1:209,3\n116#1:208\n38#1:212\n*E\n"
)
public final class StreamingMessageContentKt {
   private static final float TrailingFadeWidth;
   @NotNull
   private static final io.intercom.android.sdk.models.Metadata previewMetadata = new io.intercom.android.sdk.models.Metadata(
      "Fin", "AI Agent", 1726738186L, CollectionsKt.listOf(new io.intercom.android.sdk.models.Avatar$Builder().withInitials("F"))
   );

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingMessageContent(@NotNull StateFlow var0, @Nullable Modifier var1, boolean var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var0, "stateFlow");
      Composer var13 = var3.startRestartGroup(1080335715);
      ComposerKt.sourceInformation(var13, "C(StreamingMessageContent)P(1)49@2099L16,52@2285L78,53@2368L264:StreamingMessageContent.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var13.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var13.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var13.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var13.getSkipping()) {
         var13.skipToGroupEnd();
      } else {
         if ((var5 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            var2 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1080335715, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContent (StreamingMessageContent.kt:48)"
            );
         }

         State var7 = SnapshotStateKt.collectAsState(var0, null, var13, 14 & var6, 1);
         StreamingMessageState var10 = StreamingMessageContent$lambda$0(var7);
         io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming var10000 = var10 instanceof io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming ? (io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming)var10 : null;
         if ((var10 instanceof io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming ? (io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming)var10 : null) == null) {
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var15 = var13.endRestartGroup();
            if (var15 != null) {
               final kotlinx.coroutines.flow.StateFlow recoveredExpressionCapture_785_0 = var0;
               final androidx.compose.ui.Modifier recoveredExpressionCapture_785_1 = var1;
               final boolean recoveredExpressionCapture_785_2 = var2;
               final int recoveredExpressionCapture_785_3 = var4;
               final int recoveredExpressionCapture_785_4 = var5;
               var15.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_785_0, recoveredExpressionArgument_785_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingMessageContent$lambda$1(recoveredExpressionCapture_785_0, recoveredExpressionCapture_785_1, recoveredExpressionCapture_785_2, recoveredExpressionCapture_785_3, recoveredExpressionCapture_785_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_785_0), ((java.lang.Integer)recoveredExpressionArgument_785_1).intValue())));
            }

            return;
         }

         io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming var8 = var10000;
         RevealedBlocks var9 = StreamingTextRevealKt.rememberRevealedBlocks(var8.getUuid(), var8.getBlocks(), var13, 0);
         List var14 = var9.getBlocks();
         StreamingPart var11 = var8.getStreamingPart();
         boolean var12 = var9.isCaughtUp();
         FinStreamingRowKt.FinStreamingRow(var14, var11, var1, true, var12, var2, var13, 3072 | 896 & var6 << 3 | 458752 & var6 << 9, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var16 = var13.endRestartGroup();
      if (var16 != null) {
         final kotlinx.coroutines.flow.StateFlow recoveredExpressionCapture_786_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_786_1 = var1;
         final boolean recoveredExpressionCapture_786_2 = var2;
         final int recoveredExpressionCapture_786_3 = var4;
         final int recoveredExpressionCapture_786_4 = var5;
         var16.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_786_0, recoveredExpressionArgument_786_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingMessageContent$lambda$2(recoveredExpressionCapture_786_0, recoveredExpressionCapture_786_1, recoveredExpressionCapture_786_2, recoveredExpressionCapture_786_3, recoveredExpressionCapture_786_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_786_0), ((java.lang.Integer)recoveredExpressionArgument_786_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingRevealBlock(
      @NotNull BlockRenderData var0, boolean var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "blockRenderData");
      Composer var36 = var3.startRestartGroup(1432460591);
      ComposerKt.sourceInformation(
         var36,
         "C(StreamingRevealBlock)75@3193L34,76@3253L19,76@3232L40,77@3296L87,78@3405L96,*79@3542L7,80@3602L52,83@3725L266,89@4021L1789,123@6046L19,81@3659L2413:StreamingMessageContent.kt#hr3bi5"
      );
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var36.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var36.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var36.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var36.getSkipping()) {
         var36.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1432460591, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingRevealBlock (StreamingMessageContent.kt:74)"
            );
         }

         var36.startReplaceGroup(1007028523);
         ComposerKt.sourceInformation(var36, "CC(remember):StreamingMessageContent.kt#9igjgp");
         Composer var9 = var36;
         boolean var10 = false;
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10000;
         if (var12 == Composer.Companion.getEmpty()) {
            int var14 = 0;
            Object var60 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var9.updateRememberedValue(var60);
            var10000 = var60;
         } else {
            var10000 = var12;
         }

         MutableState var8 = (MutableState)var10000;
         var36.endReplaceGroup();
         MutableState var7 = var8;
         Unit var86 = Unit.INSTANCE;
         var36.startReplaceGroup(1007030428);
         ComposerKt.sourceInformation(var36, "CC(remember):StreamingMessageContent.kt#9igjgp");
         Composer var39 = var36;
         boolean var41 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var39.rememberedValue();
         int var53/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var92;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Unit var20 = var86;
            int var61 = 0;
            Function2 var10001 = new StreamingMessageContentKt$StreamingRevealBlock$1$1(var7, null);
            var86 = var20;
            Object var15 = var10001;
            var39.updateRememberedValue(var15);
            var92 = var15;
         } else {
            var92 = it$ivx;
         }

         Function2 var37 = (Function2)var92;
         var36.endReplaceGroup();
         EffectsKt.LaunchedEffect(var86, var37, var36, 6);
         State var38 = AnimateAsStateKt.animateFloatAsState(
            StreamingRevealBlock$lambda$4(var7) ? 1.0F : 0.0F, null, 0.0F, "blockAppearAlpha", null, var36, 3072, 22
         );
         State var40 = AnimateAsStateKt.animateFloatAsState(var1 ? 1.0F : 0.0F, null, 0.0F, "trailingEdgeAlpha", null, var36, 3072, 22);
         CompositionLocal var44 = CompositionLocalsKt.getLocalDensity();
         int var47 = 0;
         int var54 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var62 = var36.consume(var44);
         ComposerKt.sourceInformationMarkerEnd(var36);
         Density var48 = (Density)var62;
         int var55 = 0;
         float var42 = var48.recovered$245d7c913f591981(TrailingFadeWidth);
         var36.startReplaceGroup(1007041629);
         ComposerKt.sourceInformation(var36, "CC(remember):StreamingMessageContent.kt#9igjgp");
         Composer var56 = var36;
         boolean var63 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var56.rememberedValue();
         int var17/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var87;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var18 = 0;
            Object var77 = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
            var56.updateRememberedValue(var77);
            var87 = var77;
         } else {
            var87 = it$ivxx;
         }

         MutableState var49 = (MutableState)var87;
         var36.endReplaceGroup();
         MutableState var45 = var49;
         Modifier var88 = var2;
         var36.startReplaceGroup(1007045779);
         ComposerKt.sourceInformation(var36, "CC(remember):StreamingMessageContent.kt#9igjgp");
         Composer var57 = var36;
         boolean var64 = var36.changed(var38);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var57.rememberedValue();
         int var74/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var94;
         if (!var64 && it$ivxxx != Composer.Companion.getEmpty()) {
            var94 = it$ivxxx;
         } else {
            Modifier var83 = var2;
            int var78 = 0;
            final androidx.compose.runtime.State recoveredExpressionCapture_788_0 = var38;
            Function1 var93 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_788_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingRevealBlock$lambda$14$lambda$13(recoveredExpressionCapture_788_0, (androidx.compose.ui.graphics.GraphicsLayerScope) ((androidx.compose.ui.graphics.GraphicsLayerScope)recoveredExpressionArgument_788_0)));
            var88 = var83;
            Object var19 = var93;
            var57.updateRememberedValue(var19);
            var94 = var19;
         }

         Function1 var50 = (Function1)var94;
         var36.endReplaceGroup();
         Modifier var89 = GraphicsLayerModifierKt.graphicsLayer(var88, var50);
         var36.startReplaceGroup(1007056774);
         ComposerKt.sourceInformation(var36, "CC(remember):StreamingMessageContent.kt#9igjgp");
         Composer var58 = var36;
         boolean var65 = var36.changed(var40) | var36.changed(var42);
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var58.rememberedValue();
         int var75/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var96;
         if (!var65 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var96 = it$ivxxxx;
         } else {
            Modifier var84 = var89;
            int var79 = 0;
            final float recoveredExpressionCapture_789_0 = var42;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_789_1 = var45;
            final androidx.compose.runtime.State recoveredExpressionCapture_789_2 = var40;
            Function1 var95 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_789_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingRevealBlock$lambda$16$lambda$15(recoveredExpressionCapture_789_0, recoveredExpressionCapture_789_1, recoveredExpressionCapture_789_2, (androidx.compose.ui.graphics.drawscope.ContentDrawScope) ((androidx.compose.ui.graphics.drawscope.ContentDrawScope)recoveredExpressionArgument_789_0)));
            var89 = var84;
            Object var81 = var95;
            var58.updateRememberedValue(var81);
            var96 = var81;
         }

         Function1 var51 = (Function1)var96;
         var36.endReplaceGroup();
         Modifier var90 = DrawModifierKt.drawWithContent(var89, var51);
         BlockRenderData var97 = var0;
         boolean var10002 = false;
         Object var10003 = null;
         boolean var10004 = false;
         Object var10005 = null;
         Object var10006 = null;
         long var10007 = 0L;
         Object var10008 = null;
         Object var10009 = null;
         boolean var10010 = false;
         Object var10011 = null;
         Object var10012 = null;
         boolean var10013 = false;
         var36.startReplaceGroup(1007119804);
         ComposerKt.sourceInformation(var36, "CC(remember):StreamingMessageContent.kt#9igjgp");
         Composer var59 = var36;
         boolean var66 = false;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var59.rememberedValue();
         int var76/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var10014;
         if (it$ivxxxxx == Composer.Companion.getEmpty()) {
            boolean var34 = false;
            Object var33 = null;
            Object var32 = null;
            boolean var31 = false;
            Object var30 = null;
            Object var29 = null;
            long var27 = 0L;
            Object var26 = null;
            Object var25 = null;
            boolean var24 = false;
            Object var23 = null;
            boolean var22 = false;
            BlockRenderData var21 = var0;
            Modifier var85 = var90;
            int var80 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_790_0 = var45;
            Function1 var35 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_790_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingRevealBlock$lambda$18$lambda$17(recoveredExpressionCapture_790_0, (androidx.compose.ui.text.TextLayoutResult) ((androidx.compose.ui.text.TextLayoutResult)recoveredExpressionArgument_790_0)));
            var90 = var85;
            var97 = var21;
            var10002 = var22;
            var10003 = var23;
            var10004 = var24;
            var10005 = var25;
            var10006 = var26;
            var10007 = var27;
            var10008 = var29;
            var10009 = var30;
            var10010 = var31;
            var10011 = var32;
            var10012 = var33;
            var10013 = var34;
            Object var82 = var35;
            var59.updateRememberedValue(var82);
            var10014 = var82;
         } else {
            var10014 = it$ivxxxxx;
         }

         Function1 var52 = (Function1)var10014;
         var36.endReplaceGroup();
         BlockViewKt.recovered$5709084683a88fd9(
            var90,
            var97,
            var10002,
            (SuffixText)var10003,
            var10004,
            (String)var10005,
            (ImageRenderType)var10006,
            var10007,
            (Function0)var10008,
            (Function1)var10009,
            var10010,
            (Function1)var10011,
            (Shape)var10012,
            var10013,
            var52,
            var36,
            112 & var6 << 3,
            24576,
            16380
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var91 = var36.endRestartGroup();
      if (var91 != null) {
         final io.intercom.android.sdk.survey.block.BlockRenderData recoveredExpressionCapture_791_0 = var0;
         final boolean recoveredExpressionCapture_791_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_791_2 = var2;
         final int recoveredExpressionCapture_791_3 = var4;
         final int recoveredExpressionCapture_791_4 = var5;
         var91.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_791_0, recoveredExpressionArgument_791_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingRevealBlock$lambda$19(recoveredExpressionCapture_791_0, recoveredExpressionCapture_791_1, recoveredExpressionCapture_791_2, recoveredExpressionCapture_791_3, recoveredExpressionCapture_791_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_791_0), ((java.lang.Integer)recoveredExpressionArgument_791_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void StreamingMessageContentPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-923198753);
      ComposerKt.sourceInformation(var2, "C(StreamingMessageContentPreview)137@6331L722:StreamingMessageContent.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -923198753,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentPreview (StreamingMessageContent.kt:136)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$StreamingMessageContentKt.INSTANCE.recovered$eac478aa65045e5d(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_787_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_787_0, recoveredExpressionArgument_787_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt.StreamingMessageContentPreview$lambda$20(recoveredExpressionCapture_787_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_787_0), ((java.lang.Integer)recoveredExpressionArgument_787_1).intValue())));
      }
   }

   private static final StreamingMessageState StreamingMessageContent$lambda$0(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (StreamingMessageState)var1.getValue();
   }

   private static final Unit StreamingMessageContent$lambda$1(StateFlow var0, Modifier var1, boolean var2, int var3, int var4, Composer var5, int var6) {
      StreamingMessageContent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageContent$lambda$2(StateFlow var0, Modifier var1, boolean var2, int var3, int var4, Composer var5, int var6) {
      StreamingMessageContent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final boolean StreamingRevealBlock$lambda$4(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void StreamingRevealBlock$lambda$5(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final float StreamingRevealBlock$lambda$7(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).floatValue();
   }

   private static final float StreamingRevealBlock$lambda$8(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).floatValue();
   }

   private static final TextLayoutResult StreamingRevealBlock$lambda$11(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (TextLayoutResult)var1.getValue();
   }

   private static final void StreamingRevealBlock$lambda$12(MutableState var0, TextLayoutResult var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit StreamingRevealBlock$lambda$14$lambda$13(State var0, GraphicsLayerScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$graphicsLayer");
      var1.recovered$80566e71d56b3eea(CompositingStrategy.Companion.recovered$9b43f134f95d5ae1());
      var1.setAlpha(StreamingRevealBlock$lambda$7(var0));
      return Unit.INSTANCE;
   }

   private static final Unit StreamingRevealBlock$lambda$16$lambda$15(float var0, MutableState var1, State var2, ContentDrawScope var3) {
      Intrinsics.checkNotNullParameter(var3, "$this$drawWithContent");
      var3.drawContent();
      TextLayoutResult var4 = StreamingRevealBlock$lambda$11(var1);
      if (StreamingRevealBlock$lambda$8(var2) > 0.0F && var4 != null && var4.getLineCount() > 0) {
         int var5 = var4.getLineCount() - 1;
         int var6 = TextLayoutResult.getLineEnd$default(var4, var5, false, 2, null);
         int var7 = RangesKt.coerceAtLeast(var6 - 1, 0);
         TrailingFade var8 = StreamingTextRevealKt.trailingFade(
            var4.getLineTop(var5),
            var4.getLineBottom(var5),
            var4.getHorizontalPosition(var6, false),
            var4.getBidiRunDirection(var7) == ResolvedTextDirection.Rtl,
            var0
         );
         DrawScope var10000 = var3;
         Companion var10001 = Brush.Companion;
         Pair[] var9 = new Pair[]{
            TuplesKt.to(0.0F, Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$95989221dad41c4d())),
            TuplesKt.to(
               1.0F,
               Color.recovered$f46ef195ad4a2363(
                  Color.recovered$41aa0ee03c2282fb(
                     Color.Companion.recovered$95989221dad41c4d(), 1.0F - StreamingRevealBlock$lambda$8(var2), 0.0F, 0.0F, 0.0F, 14, null
                  )
               )
            )
         };
         Brush var28 = Companion.recovered$271538606329224f(var10001, var9, var8.getSolidX(), var8.getCaretX(), 0, 8, null);
         float var19 = Math.min(var8.getSolidX(), var8.getCaretX());
         float var10 = var8.getTop();
         int var11 = 0;
         float var12 = var19;
         float var13 = var10;
         int var14 = 0;
         long var15 = Float.floatToRawIntBits(var12);
         long var17 = Float.floatToRawIntBits(var13);
         long var10002 = Offset.recovered$8c837f3e34208708(var15 << 32 | var17 & 4294967295L);
         float var20 = Math.abs(var8.getCaretX() - var8.getSolidX());
         float var21 = var8.getBottom() - var8.getTop();
         int var22 = 0;
         float var23 = var20;
         float val2$iv$ivx = var21;
         int $i$f$packFloatsx = 0;
         long v1$iv$ivx = Float.floatToRawIntBits(var23);
         long v2$iv$ivx = Float.floatToRawIntBits(val2$iv$ivx);
         DrawScope.recovered$9f2640ea279134d8(
            var10000,
            var28,
            var10002,
            Size.recovered$8c837f3e34208708(v1$iv$ivx << 32 | v2$iv$ivx & 4294967295L),
            0.0F,
            null,
            null,
            BlendMode.Companion.recovered$cf222fc9f824a08f(),
            56,
            null
         );
      }

      return Unit.INSTANCE;
   }

   private static final Unit StreamingRevealBlock$lambda$18$lambda$17(MutableState var0, TextLayoutResult var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      StreamingRevealBlock$lambda$12(var0, var1);
      return Unit.INSTANCE;
   }

   private static final Unit StreamingRevealBlock$lambda$19(BlockRenderData var0, boolean var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      StreamingRevealBlock(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageContentPreview$lambda$20(int var0, Composer var1, int var2) {
      StreamingMessageContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.models.Metadata access$getPreviewMetadata$p() {
      return previewMetadata;
   }

   // $VF: synthetic method
   public static final void access$StreamingRevealBlock$lambda$5(MutableState var0, boolean var1) {
      StreamingRevealBlock$lambda$5(var0, var1);
   }

   static {
      int var0 = 64;
      int var1 = 0;
      TrailingFadeWidth = Dp.recovered$95b3d103663bf060(var0);
   }
}
