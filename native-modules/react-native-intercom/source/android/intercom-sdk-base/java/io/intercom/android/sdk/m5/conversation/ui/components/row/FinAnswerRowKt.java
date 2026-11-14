package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.ProvidedValue;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt;
import io.intercom.android.sdk.m5.conversation.ui.components.MessageMetadataKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle$BubbleStyle;
import io.intercom.android.sdk.models.AiAnswerInfo;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000|\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001as\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0018\u0010\f\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e\u0012\u0004\u0012\u00020\u00010\r2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0013H\u0001¢\u0006\u0002\u0010\u0014\u001ae\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00132\u0018\u0010\f\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e\u0012\u0004\u0012\u00020\u00010\r2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00132\b\b\u0002\u0010\u001a\u001a\u00020\u0011H\u0003¢\u0006\u0002\u0010\u001b\u001aQ\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00132\u0018\u0010\f\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e\u0012\u0004\u0012\u00020\u00010\r2\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010 H\u0003¢\u0006\u0002\u0010!\u001a\u0015\u0010\"\u001a\u00020\u00182\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010#\u001au\u0010$\u001a\u00020\u00012\f\u0010%\u001a\b\u0012\u0004\u0012\u00020&0\u000e2\u0006\u0010'\u001a\u00020\t2\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010)2\n\b\u0002\u0010*\u001a\u0004\u0018\u00010+2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00132\b\b\u0002\u0010,\u001a\u00020\u0011H\u0001¢\u0006\u0002\u0010-\u001a\r\u0010.\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00100\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00101\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00102\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00103\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00104\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00105\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\u001a\r\u00106\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010/\"\u000e\u00107\u001a\u000208X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u00109\u001a\u00020:X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010;\u001a\u00020:X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006<²\u0006\n\u0010=\u001a\u00020\u0011X\u008a\u008e\u0002",
   d2 = {
         "FinAnswerRow",
         "",
         "conversationPart",
         "Lio/intercom/android/sdk/models/Part;",
         "groupingPosition",
         "Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "timestamp",
         "",
         "senderMetadata",
         "Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "onInlineSourcesClick",
         "Lkotlin/Function1;",
         "",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "showReportAiAnswerButton",
         "",
         "onReportAiAnswer",
         "Lkotlin/Function0;",
         "(Lio/intercom/android/sdk/models/Part;Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;Landroidx/compose/ui/Modifier;Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;Lkotlin/jvm/functions/Function1;ZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "SegmentedFinContent",
         "part",
         "finAnswerStyle",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;",
         "onClick",
         "suppressAvatarHeader",
         "(Lio/intercom/android/sdk/models/Part;Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;ZLkotlin/jvm/functions/Function0;ZLandroidx/compose/runtime/Composer;II)V",
         "FinBlockView",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "codeBlockShape",
         "Landroidx/compose/ui/graphics/Shape;",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/graphics/Shape;Landroidx/compose/runtime/Composer;II)V",
         "getFinRowStyle",
         "(Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/m5/conversation/ui/components/row/FinRowStyle;",
         "FinAnswerMetadata",
         "avatars",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "title",
         "suffix",
         "",
         "aiAnswerInfo",
         "Lio/intercom/android/sdk/models/AiAnswerInfo;",
         "compact",
         "(Ljava/util/List;Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/Long;Lio/intercom/android/sdk/models/AiAnswerInfo;ZLkotlin/jvm/functions/Function0;ZLandroidx/compose/runtime/Composer;II)V",
         "FinAnswerRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "StreamingSenderTimestampFinAnswerRowRtlPreview",
         "StreamingSenderTimestampFinAnswerRowPreview",
         "SegmentedFinAnswerRowPreview",
         "SegmentedFinAnswerRowCodeLeadingPreview",
         "SegmentedFinAnswerRowTextOnlyPreview",
         "SegmentedFinAnswerRowTableLeadingPreview",
         "SegmentedFinAnswerRowTableLeadingLongTitlePreview",
         "previewTableBlock",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "finPreviewMetadata",
         "Lio/intercom/android/sdk/models/Metadata;",
         "finPreviewMetadataLongTitle",
         "intercom-sdk-base_release",
         "showDialog"
   }
)
@SourceDebugExtension(
   "SMAP\nFinAnswerRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinAnswerRowKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,538:1\n1047#2,6:539\n1047#2,6:546\n1047#2,6:558\n1047#2,6:593\n1047#2,6:599\n1047#2,6:606\n75#3:545\n118#4:552\n118#4:553\n118#4:554\n118#4:555\n118#4:556\n118#4:557\n118#4:564\n118#4:605\n99#5,6:565\n106#5:615\n81#6,6:571\n88#6,6:586\n96#6:614\n402#7,9:577\n411#7:592\n412#7,2:612\n85#8:616\n117#8,2:617\n*S KotlinDebug\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinAnswerRowKt\n*L\n70#1:539,6\n127#1:546,6\n254#1:558,6\n272#1:593,6\n276#1:599,6\n283#1:606,6\n83#1:545\n217#1:552\n218#1:553\n222#1:554\n237#1:555\n239#1:556\n241#1:557\n259#1:564\n282#1:605\n257#1:565,6\n257#1:615\n257#1:571,6\n257#1:586,6\n257#1:614\n257#1:577,9\n257#1:592\n257#1:612,2\n272#1:616\n272#1:617,2\n*E\n"
)
public final class FinAnswerRowKt {
   @NotNull
   private static final io.intercom.android.sdk.blocks.lib.models.Block$Builder previewTableBlock;
   @NotNull
   private static final io.intercom.android.sdk.models.Metadata finPreviewMetadata = new io.intercom.android.sdk.models.Metadata(
      "Bot",
      "AI Agent",
      System.currentTimeMillis(),
      CollectionsKt.listOf(new io.intercom.android.sdk.models.Avatar$Builder().withInitials("BB").withShape(AvatarShape.SQUIRCLE))
   );
   @NotNull
   private static final io.intercom.android.sdk.models.Metadata finPreviewMetadataLongTitle = new io.intercom.android.sdk.models.Metadata(
      "This Fin Agent has a very long and boring name that overflows",
      "AI Agent",
      System.currentTimeMillis(),
      CollectionsKt.listOf(new io.intercom.android.sdk.models.Avatar$Builder().withInitials("BB").withShape(AvatarShape.SQUIRCLE))
   );

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinAnswerRow(
      @NotNull Part var0,
      @NotNull GroupingPosition var1,
      @Nullable Modifier var2,
      @Nullable String var3,
      @Nullable SenderMetadata var4,
      @NotNull Function1 var5,
      boolean var6,
      @Nullable Function0 var7,
      @Nullable Composer var8,
      int var9,
      int var10
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversationPart");
      Intrinsics.checkNotNullParameter(var1, "groupingPosition");
      Intrinsics.checkNotNullParameter(var5, "onInlineSourcesClick");
      Composer var27 = var8.startRestartGroup(-1309169645);
      ComposerKt.sourceInformation(var27, "C(FinAnswerRow)P(!3,7,5!1,6)69@3334L2,71@3367L32,90@4123L532,73@3405L1250:FinAnswerRow.kt#hr3bi5");
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var27.changedInstance(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var27.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var27.changed(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var27.changed(var3) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var27.changed(var4) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var27.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var10 & 64) != 0) {
         var11 |= 1572864;
      } else if ((var9 & 1572864) == 0) {
         var11 |= var27.changed(var6) ? 1048576 : 524288;
      }

      if ((var10 & 128) != 0) {
         var11 |= 12582912;
      } else if ((var9 & 12582912) == 0) {
         var11 |= var27.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 4793491) == 4793490 && var27.getSkipping()) {
         var27.skipToGroupEnd();
      } else {
         if ((var10 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var10 & 8) != 0) {
            var3 = null;
         }

         if ((var10 & 16) != 0) {
            var4 = null;
         }

         if ((var10 & 64) != 0) {
            var6 = true;
         }

         if ((var10 & 128) != 0) {
            var27.startReplaceGroup(1683370846);
            ComposerKt.sourceInformation(var27, "CC(remember):FinAnswerRow.kt#9igjgp");
            Composer var13 = var27;
            boolean var14 = false;
            int var15 = 0;
            Object var16 = var13.rememberedValue();
            int var17 = 0;
            Object var10000;
            if (var16 == Composer.Companion.getEmpty()) {
               int var18 = 0;
               Object var32 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerRow$lambda$1$lambda$0());
               var13.updateRememberedValue(var32);
               var10000 = var32;
            } else {
               var10000 = var16;
            }

            Function0 var12 = (Function0)var10000;
            var27.endReplaceGroup();
            var7 = var12;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1309169645, var11, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRow (FinAnswerRow.kt:70)");
         }

         FinRowStyle var28 = getFinRowStyle(var1, var27, 14 & var11 >> 3);
         Part var34 = var0;
         Modifier var10001 = var2;
         String var29 = var3;
         var27.startReplaceGroup(1683377777);
         ComposerKt.sourceInformation(var27, "");
         BottomMetadata var10002;
         if (var29 == null) {
            var10002 = null;
         } else {
            String var31 = var29;
            Modifier var25 = var2;
            Part var24 = var0;
            int var33 = 0;
            var27.startReplaceGroup(1683378325);
            ComposerKt.sourceInformation(var27, "82@3891L7");
            BottomMetadata var35;
            if (var4 != null) {
               String var37 = ClickableMessageRowKt.joinSenderLabelAndTimestamp(var4.getLabel(), var31);
               boolean var10004 = var4.getPinned();
               PaddingValues var10005 = var28.getBubbleStyle().getPadding();
               CompositionLocal var19 = CompositionLocalsKt.getLocalLayoutDirection();
               int var20 = 0;
               int var21 = 0;
               ComposerKt.sourceInformationMarkerStart(var27, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var22 = var27.consume(var19);
               ComposerKt.sourceInformationMarkerEnd(var27);
               var35 = new BottomMetadata(var37, 0.0F, var10004, PaddingKt.calculateStartPadding(var10005, (LayoutDirection)var22), 2, null);
            } else {
               var35 = new BottomMetadata(var31, 0.0F, false, 0.0F, 14, null);
            }

            BottomMetadata var23 = var35;
            var27.endReplaceGroup();
            BottomMetadata var26 = var23;
            var34 = var24;
            var10001 = var25;
            var10002 = var26;
         }

         BottomMetadata var30 = var10002;
         var27.endReplaceGroup();
         ClickableMessageRowKt.ClickableMessageRow(
            var34,
            var10001,
            var30,
            var28.getRowAlignment(),
            var28.getRowPadding(),
            null,
            ComposableLambdaKt.rememberComposableLambda(1035960646, true, new FinAnswerRowKt$FinAnswerRow$3(var4, var28, var5, var6, var7), var27, 54),
            var27,
            1572864 | 14 & var11 | 112 & var11 >> 3,
            32
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var36 = var27.endRestartGroup();
      if (var36 != null) {
         final io.intercom.android.sdk.models.Part recoveredExpressionCapture_718_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.GroupingPosition recoveredExpressionCapture_718_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_718_2 = var2;
         final java.lang.String recoveredExpressionCapture_718_3 = var3;
         final io.intercom.android.sdk.m5.conversation.states.SenderMetadata recoveredExpressionCapture_718_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_718_5 = var5;
         final boolean recoveredExpressionCapture_718_6 = var6;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_718_7 = var7;
         final int recoveredExpressionCapture_718_8 = var9;
         final int recoveredExpressionCapture_718_9 = var10;
         var36.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_718_0, recoveredExpressionArgument_718_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerRow$lambda$3(recoveredExpressionCapture_718_0, recoveredExpressionCapture_718_1, recoveredExpressionCapture_718_2, recoveredExpressionCapture_718_3, recoveredExpressionCapture_718_4, recoveredExpressionCapture_718_5, recoveredExpressionCapture_718_6, recoveredExpressionCapture_718_7, recoveredExpressionCapture_718_8, recoveredExpressionCapture_718_9, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_718_0), ((java.lang.Integer)recoveredExpressionArgument_718_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SegmentedFinContent(
      Part var0, FinRowStyle var1, Function0 var2, Function1 var3, boolean var4, Function0 var5, boolean var6, Composer var7, int var8, int var9
   ) {
      Composer var21 = var7.startRestartGroup(1418648640);
      ComposerKt.sourceInformation(
         var21, "C(SegmentedFinContent)P(4!3,5)126@5631L63,127@5754L683,149@6654L236,157@6918L283,144@6442L766:FinAnswerRow.kt#hr3bi5"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var21.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var21.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var21.changedInstance(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var21.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var21.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var21.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 64) != 0) {
         var10 |= 1572864;
      } else if ((var8 & 1572864) == 0) {
         var10 |= var21.changed(var6) ? 1048576 : 524288;
      }

      if ((var10 & 599187) == 599186 && var21.getSkipping()) {
         var21.skipToGroupEnd();
      } else {
         if ((var9 & 64) != 0) {
            var6 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1418648640, var10, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinContent (FinAnswerRow.kt:125)"
            );
         }

         List var12 = var0.getBlocks();
         var21.startReplaceGroup(1425999604);
         ComposerKt.sourceInformation(var21, "CC(remember):FinAnswerRow.kt#9igjgp");
         Composer var14 = var21;
         boolean var15 = var21.changed(var12);
         int var16 = 0;
         Object var17 = var14.rememberedValue();
         int var18 = 0;
         Object var23;
         if (!var15 && var17 != Composer.Companion.getEmpty()) {
            var23 = var17;
         } else {
            int var19 = 0;
            List var10000 = var0.getBlocks();
            Intrinsics.checkNotNullExpressionValue(var10000, "getBlocks(...)");
            Object var20 = ContentSegmentKt.segmentStandaloneBlocks(var10000);
            var14.updateRememberedValue(var20);
            var23 = var20;
         }

         List var13 = (List)var23;
         var21.endReplaceGroup();
         List var11 = var13;
         Function3 var22 = ComposableLambdaKt.rememberComposableLambda(
            -2012690107, true, new FinAnswerRowKt$SegmentedFinContent$metadata$1(var6, var0, var4, var5), var21, 54
         );
         SegmentedFinLayoutKt.SegmentedFinLayout(
            var11,
            var1,
            var0.getMetadata() != null && !var6,
            var22,
            ComposableLambdaKt.rememberComposableLambda(1519437155, true, new FinAnswerRowKt$SegmentedFinContent$1(var1, var2, var3), var21, 54),
            ComposableLambdaKt.rememberComposableLambda(214045783, true, new FinAnswerRowKt$SegmentedFinContent$2(var1, var2, var3), var21, 54),
            null,
            var21,
            224256 | 112 & var10,
            64
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var24 = var21.endRestartGroup();
      if (var24 != null) {
         final io.intercom.android.sdk.models.Part recoveredExpressionCapture_726_0 = var0;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle recoveredExpressionCapture_726_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_726_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_726_3 = var3;
         final boolean recoveredExpressionCapture_726_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_726_5 = var5;
         final boolean recoveredExpressionCapture_726_6 = var6;
         final int recoveredExpressionCapture_726_7 = var8;
         final int recoveredExpressionCapture_726_8 = var9;
         var24.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_726_0, recoveredExpressionArgument_726_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.SegmentedFinContent$lambda$5(recoveredExpressionCapture_726_0, recoveredExpressionCapture_726_1, recoveredExpressionCapture_726_2, recoveredExpressionCapture_726_3, recoveredExpressionCapture_726_4, recoveredExpressionCapture_726_5, recoveredExpressionCapture_726_6, recoveredExpressionCapture_726_7, recoveredExpressionCapture_726_8, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_726_0), ((java.lang.Integer)recoveredExpressionArgument_726_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FinBlockView(Block var0, FinRowStyle var1, Function0 var2, Function1 var3, Shape var4, Composer var5, int var6, int var7) {
      Composer var9 = var5.startRestartGroup(-121976830);
      ComposerKt.sourceInformation(var9, "C(FinBlockView)P(!1,2,3,4)186@7902L49,188@8076L6,182@7692L601:FinAnswerRow.kt#hr3bi5");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var9.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var9.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var9.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var9.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var9.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if ((var7 & 16) != 0) {
            var4 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-121976830, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinBlockView (FinAnswerRow.kt:181)");
         }

         BlockViewKt.recovered$5709084683a88fd9(
            var4 != null ? Modifier.Companion : ClipKt.clip(Modifier.Companion, var1.getContentShape()),
            new BlockRenderData(
               var0,
               Color.recovered$f46ef195ad4a2363(ColorSchemeKt.recovered$e73d735ab97f03b1(var1.getBubbleStyle().recovered$6f15fcf6c761ad06(), var9, 0)),
               null,
               null,
               BlockRenderTextStyle.recovered$58c9c7402a07ca08(
                  BlockRenderTextStyle.Companion.getParagraphDefault(),
                  0L,
                  null,
                  0L,
                  null,
                  Color.recovered$f46ef195ad4a2363(IntercomTheme.INSTANCE.getColors(var9, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d()),
                  null,
                  47,
                  null
               ),
               12,
               null
            ),
            false,
            null,
            false,
            null,
            null,
            0L,
            var2,
            null,
            true,
            var3,
            var4,
            false,
            null,
            var9,
            234881024 & var8 << 18,
            6 | 112 & var8 >> 6 | 896 & var8 >> 6,
            25340
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var9.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_720_0 = var0;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle recoveredExpressionCapture_720_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_720_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_720_3 = var3;
         final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_720_4 = var4;
         final int recoveredExpressionCapture_720_5 = var6;
         final int recoveredExpressionCapture_720_6 = var7;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_720_0, recoveredExpressionArgument_720_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinBlockView$lambda$6(recoveredExpressionCapture_720_0, recoveredExpressionCapture_720_1, recoveredExpressionCapture_720_2, recoveredExpressionCapture_720_3, recoveredExpressionCapture_720_4, recoveredExpressionCapture_720_5, recoveredExpressionCapture_720_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_720_0), ((java.lang.Integer)recoveredExpressionArgument_720_1).intValue())));
      }
   }

   @Composable
   @NotNull
   public static final FinRowStyle getFinRowStyle(@NotNull GroupingPosition var0, @Nullable Composer var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "groupingPosition");
      var1.startReplaceGroup(1658672574);
      ComposerKt.sourceInformation(var1, "C(getFinRowStyle)220@8926L6,236@9727L6:FinAnswerRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1658672574, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.getFinRowStyle (FinAnswerRow.kt:215)");
      }

      int var4 = 20;
      int var5 = 0;
      float var3 = Dp.recovered$95b3d103663bf060(var4);
      int $this$dp$ivx = 4;
      int $i$f$getDpx = 0;
      float var12 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
      long var10002 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$61a1caf6b6549f74();
      int $this$dp$ivxx = 16;
      int $i$f$getDpxx = 0;
      float var10003 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
      int $this$dp$ivxxx = 12;
      int $i$f$getDpxxx = 0;
      PaddingValues var28 = PaddingKt.recovered$4ebb4826aa57abc6(var10003, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
      float var17 = var0 != GroupingPosition.BOTTOM && var0 != GroupingPosition.MIDDLE ? var3 : var12;
      float var21 = var0 != GroupingPosition.TOP && var0 != GroupingPosition.MIDDLE ? var3 : var12;
      RoundedCornerShape var10004 = RoundedCornerShapeKt.recovered$e05142e8a28d423f(var17, var3, var3, var21);
      int $this$dp$ivxxxx = 1;
      int $i$f$getDpxxxx = 0;
      io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle$BubbleStyle var15 = new io.intercom.android.sdk.m5.conversation.ui.components.row.FinRowStyle$BubbleStyle(
         var10002,
         var28,
         var10004,
         BorderStrokeKt.recovered$91879a1bef2fb536(
            Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$8c831c6299defa27()
         ),
         null
      );
      int $this$dp$ivxxxxx = 16;
      int $i$f$getDpxxxxx = 0;
      float var10000 = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx);
      int $this$dp$ivxxxxxx = 16;
      int $i$f$getDpxxxxxx = 0;
      PaddingValues var19 = PaddingKt.recovered$0229d0d9fcc63c3d(var10000, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx), 0.0F, 10, null);
      Horizontal var25 = Alignment.Companion.getStart();
      int $this$dp$ivxxxxxxx = 8;
      int $i$f$getDpxxxxxxx = 0;
      RoundedCornerShape var27 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxx));
      FinRowStyle var14 = new FinRowStyle(var15, var25, var19, var27);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var14;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinAnswerMetadata(
      @NotNull List var0,
      @NotNull String var1,
      @Nullable Modifier var2,
      @Nullable String var3,
      @Nullable Long var4,
      @Nullable AiAnswerInfo var5,
      boolean var6,
      @Nullable Function0 var7,
      boolean var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      Intrinsics.checkNotNullParameter(var0, "avatars");
      Intrinsics.checkNotNullParameter(var1, "title");
      Composer var56 = var9.startRestartGroup(1339353339);
      ComposerKt.sourceInformation(var56, "C(FinAnswerMetadata)P(1,8,3,6,7!1,5,4)253@10232L2,256@10274L1556:FinAnswerRow.kt#hr3bi5");
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var56.changedInstance(var0) ? 4 : 2;
      }

      if ((var11 & 2) != 0) {
         var12 |= 48;
      } else if ((var10 & 48) == 0) {
         var12 |= var56.changed(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var56.changed(var2) ? 256 : 128;
      }

      if ((var11 & 8) != 0) {
         var12 |= 3072;
      } else if ((var10 & 3072) == 0) {
         var12 |= var56.changed(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var56.changed(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var56.changed(var5) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var56.changed(var6) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= var56.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 256) != 0) {
         var12 |= 100663296;
      } else if ((var10 & 100663296) == 0) {
         var12 |= var56.changed(var8) ? 67108864 : 33554432;
      }

      if ((var12 & 38347923) == 38347922 && var56.getSkipping()) {
         var56.skipToGroupEnd();
      } else {
         if ((var11 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var11 & 8) != 0) {
            var3 = null;
         }

         if ((var11 & 16) != 0) {
            var4 = null;
         }

         if ((var11 & 32) != 0) {
            var5 = null;
         }

         if ((var11 & 64) != 0) {
            var6 = false;
         }

         if ((var11 & 128) != 0) {
            var56.startReplaceGroup(1369357423);
            ComposerKt.sourceInformation(var56, "CC(remember):FinAnswerRow.kt#9igjgp");
            Composer var14 = var56;
            boolean var15 = false;
            int var16 = 0;
            Object var17 = var14.rememberedValue();
            int var18 = 0;
            Object var10000;
            if (var17 == Composer.Companion.getEmpty()) {
               int var19 = 0;
               Object var65 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerMetadata$lambda$8$lambda$7());
               var14.updateRememberedValue(var65);
               var10000 = var65;
            } else {
               var10000 = var17;
            }

            Function0 var13 = (Function0)var10000;
            var56.endReplaceGroup();
            var7 = var13;
         }

         if ((var11 & 256) != 0) {
            var8 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1339353339, var12, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerMetadata (FinAnswerRow.kt:255)"
            );
         }

         Modifier var57 = var8 ? var2 : SizeKt.fillMaxWidth$default(var2, 0.0F, 1, null);
         androidx.compose.foundation.layout.Arrangement.Horizontal var85;
         if (var8) {
            int var58 = 16;
            int var60 = 0;
            var85 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var58));
         } else {
            var85 = Arrangement.INSTANCE.getStart();
         }

         androidx.compose.foundation.layout.Arrangement.Horizontal var59 = var85;
         Vertical var61 = Alignment.Companion.getCenterVertically();
         short var62 = 384;
         int var63 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var56, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var64 = RowKt.rowMeasurePolicy(var59, var61, var56, 14 & var62 >> 3 | 112 & var62 >> 3);
         Modifier var66 = var57;
         MeasurePolicy var20 = var64;
         Composer var21 = var56;
         int var22 = 112 & var62 << 3;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var24 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var21, 0));
         CompositionLocalMap var25 = var21.getCurrentCompositionLocalMap();
         Modifier var26 = ComposedModifierKt.materializeModifier(var21, var66);
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
         ComposerKt.sourceInformationMarkerStart(var34, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var36 = 6 | 112 & var62 >> 6;
         Composer var37 = var34;
         RowScope var38 = RowScopeInstance.INSTANCE;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 271519934, "C261@10519L400:FinAnswerRow.kt#hr3bi5");
         Modifier var40 = var38.weight(Modifier.Companion, 1.0F, !var8);
         MessageMetadataKt.MessageMetadata(var0, var1, var40, var3, var4, var37, 14 & var12 | 112 & var12 | 7168 & var12 | 57344 & var12, 0);
         var37.startReplaceGroup(1671339806);
         ComposerKt.sourceInformation(var37, "*271@10978L34,282@11425L21,280@11338L476");
         if (var5 != null) {
            AiAnswerInfo var41 = var5;
            int var42 = 0;
            var37.startReplaceGroup(1468358733);
            ComposerKt.sourceInformation(var37, "CC(remember):FinAnswerRow.kt#9igjgp");
            Composer var43 = var37;
            boolean var44 = false;
            int var45 = 0;
            Object var46 = var43.rememberedValue();
            int var47 = 0;
            Object var86;
            if (var46 == Composer.Companion.getEmpty()) {
               int var48 = 0;
               Object var79 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
               var43.updateRememberedValue(var79);
               var86 = var79;
            } else {
               var86 = var46;
            }

            MutableState var49 = (MutableState)var86;
            var37.endReplaceGroup();
            MutableState var50 = var49;
            var37.startReplaceGroup(1671341880);
            ComposerKt.sourceInformation(var37, "275@11140L22,273@11059L252");
            if (FinAnswerMetadata$lambda$17$lambda$16$lambda$10(var50)) {
               AiAnswerInfo var87 = var41;
               boolean var10001 = var6;
               var37.startReplaceGroup(1468363905);
               ComposerKt.sourceInformation(var37, "CC(remember):FinAnswerRow.kt#9igjgp");
               Composer var67 = var37;
               boolean var70 = false;
               int $i$f$cachex = 0;
               Object it$ivx = var67.rememberedValue();
               int var77/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               Object var10002;
               if (it$ivx == Composer.Companion.getEmpty()) {
                  boolean var51 = var6;
                  AiAnswerInfo var52 = var41;
                  int var80 = 0;
                  final androidx.compose.runtime.MutableState recoveredExpressionCapture_716_0 = var50;
                  Function0 var53 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerMetadata$lambda$17$lambda$16$lambda$13$lambda$12(recoveredExpressionCapture_716_0));
                  var87 = var52;
                  var10001 = var51;
                  Object var54 = var53;
                  var67.updateRememberedValue(var54);
                  var10002 = var54;
               } else {
                  var10002 = it$ivx;
               }

               Function0 var82 = (Function0)var10002;
               var37.endReplaceGroup();
               AnswerInfoDialogKt.AnswerInfoDialog(var87, var10001, var82, var7, var37, 384 | 112 & var12 >> 15 | 7168 & var12 >> 12, 0);
            }

            var37.endReplaceGroup();
            Modifier var88 = Modifier.Companion;
            int var68 = 24;
            int var71 = 0;
            Modifier var83 = SizeKt.recovered$111ef94c93e78c04(var88, Dp.recovered$95b3d103663bf060(var68));
            var37.startReplaceGroup(1468373024);
            ComposerKt.sourceInformation(var37, "CC(remember):FinAnswerRow.kt#9igjgp");
            Composer var72 = var37;
            boolean var74 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var72.rememberedValue();
            int var81/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var89;
            if (it$ivx == Composer.Companion.getEmpty()) {
               int var84 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_713_0 = var50;
               Object var55 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerMetadata$lambda$17$lambda$16$lambda$15$lambda$14(recoveredExpressionCapture_713_0));
               var72.updateRememberedValue(var55);
               var89 = var55;
            } else {
               var89 = it$ivx;
            }

            Function0 var69 = (Function0)var89;
            var37.endReplaceGroup();
            IconButtonKt.IconButton(
               var69,
               var83,
               false,
               null,
               null,
               null,
               io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$eac478aa65045e5d(),
               var37,
               1572918,
               60
            );
         }

         var37.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var28.endNode();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var56);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var90 = var56.endRestartGroup();
      if (var90 != null) {
         final java.util.List recoveredExpressionCapture_714_0 = var0;
         final java.lang.String recoveredExpressionCapture_714_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_714_2 = var2;
         final java.lang.String recoveredExpressionCapture_714_3 = var3;
         final java.lang.Long recoveredExpressionCapture_714_4 = var4;
         final io.intercom.android.sdk.models.AiAnswerInfo recoveredExpressionCapture_714_5 = var5;
         final boolean recoveredExpressionCapture_714_6 = var6;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_714_7 = var7;
         final boolean recoveredExpressionCapture_714_8 = var8;
         final int recoveredExpressionCapture_714_9 = var10;
         final int recoveredExpressionCapture_714_10 = var11;
         var90.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_714_0, recoveredExpressionArgument_714_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerMetadata$lambda$18(recoveredExpressionCapture_714_0, recoveredExpressionCapture_714_1, recoveredExpressionCapture_714_2, recoveredExpressionCapture_714_3, recoveredExpressionCapture_714_4, recoveredExpressionCapture_714_5, recoveredExpressionCapture_714_6, recoveredExpressionCapture_714_7, recoveredExpressionCapture_714_8, recoveredExpressionCapture_714_9, recoveredExpressionCapture_714_10, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_714_0), ((java.lang.Integer)recoveredExpressionArgument_714_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FinAnswerRowPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1987882525);
      ComposerKt.sourceInformation(var2, "C(FinAnswerRowPreview)302@12203L1195:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1987882525, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowPreview (FinAnswerRow.kt:297)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(false);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_719_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_719_0, recoveredExpressionArgument_719_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.FinAnswerRowPreview$lambda$19(recoveredExpressionCapture_719_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_719_0), ((java.lang.Integer)recoveredExpressionArgument_719_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingSenderTimestampFinAnswerRowRtlPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(2116767728);
      ComposerKt.sourceInformation(var2, "C(StreamingSenderTimestampFinAnswerRowRtlPreview)338@13500L137:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2116767728,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingSenderTimestampFinAnswerRowRtlPreview (FinAnswerRow.kt:337)"
            );
         }

         CompositionLocalKt.CompositionLocalProvider(
            CompositionLocalsKt.getLocalLayoutDirection().provides(LayoutDirection.Rtl),
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$fd838d44303763da(),
            var2,
            48 | ProvidedValue.$stable
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_728_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_728_0, recoveredExpressionArgument_728_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.StreamingSenderTimestampFinAnswerRowRtlPreview$lambda$20(recoveredExpressionCapture_728_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_728_0), ((java.lang.Integer)recoveredExpressionArgument_728_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingSenderTimestampFinAnswerRowPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(574143266);
      ComposerKt.sourceInformation(var2, "C(StreamingSenderTimestampFinAnswerRowPreview)347@13770L1456:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               574143266,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingSenderTimestampFinAnswerRowPreview (FinAnswerRow.kt:345)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(false);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_727_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_727_0, recoveredExpressionArgument_727_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.StreamingSenderTimestampFinAnswerRowPreview$lambda$21(recoveredExpressionCapture_727_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_727_0), ((java.lang.Integer)recoveredExpressionArgument_727_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedFinAnswerRowPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-505373063);
      ComposerKt.sourceInformation(var2, "C(SegmentedFinAnswerRowPreview)389@15387L642:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -505373063, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinAnswerRowPreview (FinAnswerRow.kt:388)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$265b6f46b79d310a(),
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
         final int recoveredExpressionCapture_722_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_722_0, recoveredExpressionArgument_722_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.SegmentedFinAnswerRowPreview$lambda$22(recoveredExpressionCapture_722_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_722_0), ((java.lang.Integer)recoveredExpressionArgument_722_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedFinAnswerRowCodeLeadingPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1887899608);
      ComposerKt.sourceInformation(var2, "C(SegmentedFinAnswerRowCodeLeadingPreview)415@16374L622:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1887899608, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinAnswerRowCodeLeadingPreview (FinAnswerRow.kt:414)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$5a1850a8ff371bfb(),
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
         final int recoveredExpressionCapture_721_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_721_0, recoveredExpressionArgument_721_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.SegmentedFinAnswerRowCodeLeadingPreview$lambda$23(recoveredExpressionCapture_721_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_721_0), ((java.lang.Integer)recoveredExpressionArgument_721_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedFinAnswerRowTextOnlyPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1343491090);
      ComposerKt.sourceInformation(var2, "C(SegmentedFinAnswerRowTextOnlyPreview)440@17225L631:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1343491090, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinAnswerRowTextOnlyPreview (FinAnswerRow.kt:439)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$3faf0e22fa74c6a7(),
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
         final int recoveredExpressionCapture_725_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_725_0, recoveredExpressionArgument_725_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.SegmentedFinAnswerRowTextOnlyPreview$lambda$24(recoveredExpressionCapture_725_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_725_0), ((java.lang.Integer)recoveredExpressionArgument_725_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedFinAnswerRowTableLeadingPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1266815375);
      ComposerKt.sourceInformation(var2, "C(SegmentedFinAnswerRowTableLeadingPreview)464@17985L630:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1266815375,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinAnswerRowTableLeadingPreview (FinAnswerRow.kt:462)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(true);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$a427d7fccaa25a17(),
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
         final int recoveredExpressionCapture_724_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_724_0, recoveredExpressionArgument_724_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.SegmentedFinAnswerRowTableLeadingPreview$lambda$25(recoveredExpressionCapture_724_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_724_0), ((java.lang.Integer)recoveredExpressionArgument_724_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedFinAnswerRowTableLeadingLongTitlePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-693841197);
      ComposerKt.sourceInformation(var2, "C(SegmentedFinAnswerRowTableLeadingLongTitlePreview)488@18753L639:FinAnswerRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -693841197,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinAnswerRowTableLeadingLongTitlePreview (FinAnswerRow.kt:486)"
            );
         }

         ConfigurableIntercomThemeKt.setFinTableBlocksEnabled(true);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$FinAnswerRowKt.INSTANCE.recovered$db063fabb945fca0(),
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
         final int recoveredExpressionCapture_723_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_723_0, recoveredExpressionArgument_723_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt.SegmentedFinAnswerRowTableLeadingLongTitlePreview$lambda$26(recoveredExpressionCapture_723_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_723_0), ((java.lang.Integer)recoveredExpressionArgument_723_1).intValue())));
      }
   }

   private static final Unit FinAnswerRow$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit FinAnswerRow$lambda$3(
      Part var0,
      GroupingPosition var1,
      Modifier var2,
      String var3,
      SenderMetadata var4,
      Function1 var5,
      boolean var6,
      Function0 var7,
      int var8,
      int var9,
      Composer var10,
      int var11
   ) {
      FinAnswerRow(var0, var1, var2, var3, var4, var5, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinContent$lambda$5(
      Part var0, FinRowStyle var1, Function0 var2, Function1 var3, boolean var4, Function0 var5, boolean var6, int var7, int var8, Composer var9, int var10
   ) {
      SegmentedFinContent(var0, var1, var2, var3, var4, var5, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit FinBlockView$lambda$6(
      Block var0, FinRowStyle var1, Function0 var2, Function1 var3, Shape var4, int var5, int var6, Composer var7, int var8
   ) {
      FinBlockView(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit FinAnswerMetadata$lambda$8$lambda$7() {
      return Unit.INSTANCE;
   }

   private static final boolean FinAnswerMetadata$lambda$17$lambda$16$lambda$10(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void FinAnswerMetadata$lambda$17$lambda$16$lambda$11(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit FinAnswerMetadata$lambda$17$lambda$16$lambda$13$lambda$12(MutableState var0) {
      FinAnswerMetadata$lambda$17$lambda$16$lambda$11(var0, false);
      return Unit.INSTANCE;
   }

   private static final Unit FinAnswerMetadata$lambda$17$lambda$16$lambda$15$lambda$14(MutableState var0) {
      FinAnswerMetadata$lambda$17$lambda$16$lambda$11(var0, true);
      return Unit.INSTANCE;
   }

   private static final Unit FinAnswerMetadata$lambda$18(
      List var0,
      String var1,
      Modifier var2,
      String var3,
      Long var4,
      AiAnswerInfo var5,
      boolean var6,
      Function0 var7,
      boolean var8,
      int var9,
      int var10,
      Composer var11,
      int var12
   ) {
      FinAnswerMetadata(var0, var1, var2, var3, var4, var5, var6, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit FinAnswerRowPreview$lambda$19(int var0, Composer var1, int var2) {
      FinAnswerRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit StreamingSenderTimestampFinAnswerRowRtlPreview$lambda$20(int var0, Composer var1, int var2) {
      StreamingSenderTimestampFinAnswerRowRtlPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit StreamingSenderTimestampFinAnswerRowPreview$lambda$21(int var0, Composer var1, int var2) {
      StreamingSenderTimestampFinAnswerRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinAnswerRowPreview$lambda$22(int var0, Composer var1, int var2) {
      SegmentedFinAnswerRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinAnswerRowCodeLeadingPreview$lambda$23(int var0, Composer var1, int var2) {
      SegmentedFinAnswerRowCodeLeadingPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinAnswerRowTextOnlyPreview$lambda$24(int var0, Composer var1, int var2) {
      SegmentedFinAnswerRowTextOnlyPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinAnswerRowTableLeadingPreview$lambda$25(int var0, Composer var1, int var2) {
      SegmentedFinAnswerRowTableLeadingPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedFinAnswerRowTableLeadingLongTitlePreview$lambda$26(int var0, Composer var1, int var2) {
      SegmentedFinAnswerRowTableLeadingLongTitlePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableCell$Builder previewTableBlock$lambda$27$cell(String var0) {
      return new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder()
         .withContent(CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableRow$Builder previewTableBlock$lambda$27$row(
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder... var0
   ) {
      return new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.models.Metadata access$getFinPreviewMetadata$p() {
      return finPreviewMetadata;
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.blocks.lib.models.Block$Builder access$getPreviewTableBlock$p() {
      return previewTableBlock;
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.models.Metadata access$getFinPreviewMetadataLongTitle$p() {
      return finPreviewMetadataLongTitle;
   }

   // $VF: synthetic method
   public static final void access$SegmentedFinContent(
      Part var0, FinRowStyle var1, Function0 var2, Function1 var3, boolean var4, Function0 var5, boolean var6, Composer var7, int var8, int var9
   ) {
      SegmentedFinContent(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   // $VF: synthetic method
   public static final void access$FinBlockView(Block var0, FinRowStyle var1, Function0 var2, Function1 var3, Shape var4, Composer var5, int var6, int var7) {
      FinBlockView(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   static {
      int var0 = 0;
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.TABLE.getSerializedName());
      io.intercom.android.sdk.blocks.lib.models.TableRow$Builder[] var1 = new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder[3];
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var2 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         previewTableBlock$lambda$27$cell("Plan"), previewTableBlock$lambda$27$cell("Price")
      };
      var1[0] = previewTableBlock$lambda$27$row(var2);
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var3 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         previewTableBlock$lambda$27$cell("Starter"), previewTableBlock$lambda$27$cell("$29")
      };
      var1[1] = previewTableBlock$lambda$27$row(var3);
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var4 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         previewTableBlock$lambda$27$cell("Pro"), previewTableBlock$lambda$27$cell("$99")
      };
      var1[2] = previewTableBlock$lambda$27$row(var4);
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var5 = var10000.withRows(CollectionsKt.listOf(var1));
      Intrinsics.checkNotNullExpressionValue(var5, "run(...)");
      previewTableBlock = var5;
   }
}
