package io.intercom.android.sdk.survey.block;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.hapticfeedback.HapticFeedback;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.Clipboard;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import androidx.compose.ui.window.AndroidDialog_androidKt;
import androidx.compose.ui.window.DialogProperties;
import androidx.core.text.HtmlCompat;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded;
import io.intercom.android.sdk.survey.block.CodeBlockMode$Inline;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomSyntaxColors;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000p\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\u001a5\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0001¢\u0006\u0002\u0010\u0016\u001aC\u0010\u0017\u001a\u00020\r*\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020#H\u0003¢\u0006\u0004\b$\u0010%\u001a1\u0010&\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020#2\u0006\u0010\u001f\u001a\u00020 2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0003¢\u0006\u0004\b'\u0010(\u001a/\u0010)\u001a\u00020\r2\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020 2\u0006\u0010-\u001a\u00020 2\u0006\u0010\u0012\u001a\u00020\u0013H\u0003¢\u0006\u0004\b.\u0010/\u001a#\u00101\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u00102\u001a\b\u0012\u0004\u0012\u00020\r03H\u0001¢\u0006\u0002\u00104\u001a\r\u00105\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\u001a\r\u00107\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\u001a\r\u00108\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\u001a\r\u00109\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\u001a\r\u0010:\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\u001a\r\u0010;\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\u001a\r\u0010<\u001a\u00020\rH\u0001¢\u0006\u0002\u00106\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u0010\u0005\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u0010\u0006\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u000e\u0010\u0007\u001a\u00020\bX\u0082D¢\u0006\u0002\n\u0000\"\u0010\u0010\t\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u0010\n\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u0010\u000b\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u00100\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004¨\u0006=²\u0006\n\u0010>\u001a\u00020\u001aX\u008a\u008e\u0002",
   d2 = {
         "TWIG",
         "Lcom/intercom/twig/Twig;",
         "CODE_BLOCK_MAX_HEIGHT",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "CODE_BLOCK_CORNER_RADIUS",
         "CODE_BLOCK_EXPANDED_CORNER_RADIUS",
         "COPY_CONFIRMATION_DURATION_MS",
         "",
         "HEADER_ICON_SIZE",
         "HEADER_ICON_BUTTON_SIZE",
         "GUTTER_TO_CODE_SPACING",
         "EnhancedCodeBlock",
         "",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "mode",
         "Lio/intercom/android/sdk/survey/block/CodeBlockMode;",
         "shape",
         "Landroidx/compose/ui/graphics/Shape;",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/block/CodeBlockMode;Landroidx/compose/ui/graphics/Shape;Landroidx/compose/runtime/Composer;II)V",
         "CodeBlockBody",
         "Landroidx/compose/foundation/layout/ColumnScope;",
         "isExpanded",
         "",
         "lineCount",
         "",
         "blockContent",
         "Landroidx/compose/ui/text/AnnotatedString;",
         "textColor",
         "Landroidx/compose/ui/graphics/Color;",
         "gutterTextColor",
         "textStyle",
         "Landroidx/compose/ui/text/TextStyle;",
         "CodeBlockBody-wBJOh4Y",
         "(Landroidx/compose/foundation/layout/ColumnScope;ZILandroidx/compose/ui/text/AnnotatedString;JJLandroidx/compose/ui/text/TextStyle;Landroidx/compose/runtime/Composer;I)V",
         "LineNumberGutter",
         "LineNumberGutter-cf5BqRc",
         "(ILandroidx/compose/ui/text/TextStyle;JLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "CodeBlockHeader",
         "rawCode",
         "",
         "iconTint",
         "labelColor",
         "CodeBlockHeader-RIQooxk",
         "(Ljava/lang/String;JJLio/intercom/android/sdk/survey/block/CodeBlockMode;Landroidx/compose/runtime/Composer;I)V",
         "EXPANDED_DIALOG_MARGIN",
         "ExpandedCodeBlockDialog",
         "onDismiss",
         "Lkotlin/Function0;",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "EnhancedCodeBlockPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "EnhancedCodeBlockHighlightedPreview",
         "EnhancedCodeBlockLongPreview",
         "EnhancedCodeBlockNewlineDelimitedPreview",
         "EnhancedCodeBlockWidePreview",
         "EnhancedCodeBlockRtlPreview",
         "ExpandedCodeBlockPreview",
         "intercom-sdk-base_release",
         "copied"
   }
)
@SourceDebugExtension(
   "SMAP\nEnhancedCodeBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/EnhancedCodeBlockKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 7 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 8 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 9 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 10 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 11 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 12 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 13 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,532:1\n1#2:533\n75#3:534\n75#3:626\n75#3:627\n1047#4,6:535\n1047#4,6:614\n1047#4,6:620\n1047#4,3:635\n1050#4,3:639\n1047#4,6:642\n1047#4,6:648\n1047#4,6:684\n1047#4,6:690\n1047#4,6:696\n1047#4,6:702\n118#5:541\n118#5:654\n118#5:655\n118#5:715\n118#5:716\n118#5:717\n118#5:718\n118#5:719\n118#5:720\n118#5:721\n87#6:542\n84#6,9:543\n94#6:577\n81#7,6:552\n88#7,6:567\n96#7:576\n81#7,6:588\n88#7,6:603\n96#7:612\n81#7,6:662\n88#7,6:677\n96#7:710\n402#8,9:558\n411#8,3:573\n402#8,9:594\n411#8,3:609\n402#8,9:668\n411#8:683\n412#8,2:708\n70#9:578\n67#9,9:579\n77#9:613\n615#10:628\n612#10,6:629\n613#11:638\n99#12,6:656\n106#12:711\n85#13:712\n117#13,2:713\n*S KotlinDebug\n*F\n+ 1 EnhancedCodeBlock.kt\nio/intercom/android/sdk/survey/block/EnhancedCodeBlockKt\n*L\n140#1:534\n248#1:626\n249#1:627\n144#1:535,6\n228#1:614,6\n235#1:620,6\n250#1:635,3\n250#1:639,3\n251#1:642,6\n253#1:648,6\n278#1:684,6\n291#1:690,6\n312#1:696,6\n331#1:702,6\n158#1:541\n263#1:654\n264#1:655\n75#1:715\n76#1:716\n79#1:717\n81#1:718\n82#1:719\n85#1:720\n348#1:721\n153#1:542\n153#1:543,9\n153#1:577\n153#1:552,6\n153#1:567,6\n153#1:576\n194#1:588,6\n194#1:603,6\n194#1:612\n260#1:662,6\n260#1:677,6\n260#1:710\n153#1:558,9\n153#1:573,3\n194#1:594,9\n194#1:609,3\n260#1:668,9\n260#1:683\n260#1:708,2\n194#1:578\n194#1:579,9\n194#1:613\n250#1:628\n250#1:629,6\n250#1:638\n260#1:656,6\n260#1:711\n251#1:712\n251#1:713,2\n*E\n"
)
public final class EnhancedCodeBlockKt {
   @NotNull
   private static final Twig TWIG;
   private static final float CODE_BLOCK_MAX_HEIGHT;
   private static final float CODE_BLOCK_CORNER_RADIUS;
   private static final float CODE_BLOCK_EXPANDED_CORNER_RADIUS;
   private static final long COPY_CONFIRMATION_DURATION_MS;
   private static final float HEADER_ICON_SIZE;
   private static final float HEADER_ICON_BUTTON_SIZE;
   private static final float GUTTER_TO_CODE_SPACING;
   private static final float EXPANDED_DIALOG_MARGIN;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlock(
      @NotNull Block var0, @Nullable Modifier var1, @Nullable CodeBlockMode var2, @Nullable Shape var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "block");
      Composer var55 = var4.startRestartGroup(-1932486291);
      ComposerKt.sourceInformation(
         var55,
         "C(EnhancedCodeBlock)P(!1,2)119@5260L6,120@5323L6,121@5379L6,122@5439L6,123@5491L6,124@5546L6,125@5594L10,139@6354L7,143@6630L50,152@6919L691:EnhancedCodeBlock.kt#jxhu3x"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var55.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var55.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && ((var5 & 512) == 0 ? var55.changed(var2) : var55.changedInstance(var2)) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var55.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var55.getSkipping()) {
         var55.skipToGroupEnd();
      } else {
         var55.startDefaults();
         if ((var5 & 1) != 0 && !var55.getDefaultsInvalid()) {
            var55.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = new io.intercom.android.sdk.survey.block.CodeBlockMode$Inline(null, 1, null);
               var7 &= -897;
            }

            if ((var6 & 8) != 0) {
               var3 = null;
            }
         }

         var55.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1932486291, var7, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlock (EnhancedCodeBlock.kt:118)");
         }

         long var8 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getContainer().recovered$94e2a6def685ccbe();
         long var10 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getBorder().recovered$e2b74d614aa09788();
         long var12 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         long var14 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         long var16 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122();
         IntercomSyntaxColors var18 = IntercomTheme.INSTANCE.getColors(var55, IntercomTheme.$stable).getSyntax();
         TextStyle var19 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var55, IntercomTheme.$stable).getType04(),
            0L,
            0L,
            null,
            null,
            null,
            FontFamily.Companion.getMonospace(),
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            0,
            0,
            0L,
            null,
            null,
            null,
            0,
            0,
            null,
            16777183,
            null
         );
         float var20 = var2 instanceof io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded ? CODE_BLOCK_EXPANDED_CORNER_RADIUS : CODE_BLOCK_CORNER_RADIUS;
         Shape var10000 = var3;
         if (var3 == null) {
            var10000 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(var20);
         }

         Shape var21;
         String var75;
         label123: {
            var21 = var10000;
            String var24 = var0.getHighlightedHtml();
            String var25 = var24;
            int var26 = 0;
            Intrinsics.checkNotNull(var25);
            String var23 = !StringsKt.isBlank(var25) ? var24 : null;
            if (var23 != null) {
               String var57 = CodeBlockSyntaxHighlightingKt.recovered$7de1aaa8180c861e(var23, var18, var12);
               if (var57 != null) {
                  var75 = var57;
                  break label123;
               }
            }

            var75 = var0.getText();
         }

         String var22 = var75;
         Intrinsics.checkNotNull(var22);
         Spanned var56 = HtmlCompat.fromHtml(BlockExtensionsKt.preserveCodeIndentation(var22), 0);
         Intrinsics.checkNotNull(var56);
         CharSequence var76 = var56;
         CompositionLocal var59 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var61 = 0;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var55, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var28 = var55.consume(var59);
         ComposerKt.sourceInformationMarkerEnd(var55);
         AnnotatedString var58 = BlockExtensionsKt.recovered$c71786fddfec5215(var76, (Context)var28, null, 0L, 6, null);
         String var60 = StringsKt.trimEnd(StringsKt.replace$default(var56.toString(), ' ', ' ', false, 4, null)).toString();
         var55.startReplaceGroup(-1744786853);
         ComposerKt.sourceInformation(var55, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var65 = var55;
         boolean var29 = var55.changed(var60);
         int var30 = 0;
         Object var31 = var65.rememberedValue();
         int var32 = 0;
         Object var77;
         if (!var29 && var31 != Composer.Companion.getEmpty()) {
            var77 = var31;
         } else {
            int var33 = 0;
            Object var34 = StringsKt.lines(var60).size();
            var65.updateRememberedValue(var34);
            var77 = var34;
         }

         int var63 = ((Number)var77).intValue();
         var55.endReplaceGroup();
         int var62 = var63;
         Modifier var64 = var2 instanceof io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded ? var1 : SizeKt.recovered$1c935d354e9159f0(var1, 0.0F, CODE_BLOCK_MAX_HEIGHT, 1, null);
         Modifier var78 = BackgroundKt.recovered$42a3174180b3fb7a(ClipKt.clip(SizeKt.fillMaxWidth$default(var64, 0.0F, 1, null), var21), var8, null, 2, null);
         int var66 = 1;
         int var68 = 0;
         Modifier var67 = BorderKt.recovered$1227359600bc50fd(var78, Dp.recovered$95b3d103663bf060(var66), var10, var21);
         byte var71 = 0;
         int var72 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var55, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var69 = Arrangement.INSTANCE.getTop();
         Horizontal var70 = Alignment.Companion.getStart();
         MeasurePolicy var73 = ColumnKt.columnMeasurePolicy(var69, var70, var55, 14 & var71 >> 3 | 112 & var71 >> 3);
         Modifier var74 = var67;
         MeasurePolicy var35 = var73;
         Composer var36 = var55;
         int var37 = 112 & var71 << 3;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var39 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var36, 0));
         CompositionLocalMap var40 = var36.getCurrentCompositionLocalMap();
         Modifier var41 = ComposedModifierKt.materializeModifier(var36, var74);
         Function0 var42 = ComposeUiNode.Companion.getConstructor();
         Composer var43 = var36;
         int var44 = 6 | 896 & var37 << 6;
         int var45 = 0;
         ComposerKt.sourceInformationMarkerStart(var43, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var43.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var43.startReusableNode();
         if (var43.getInserting()) {
            var43.createNode(var42);
         } else {
            var43.useNode();
         }

         Composer var46 = Updater.recovered$676aa17b44fd6280(var43);
         int var47 = 0;
         Updater.recovered$89682b199a142880(var46, var35, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var46, var40, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var46, var39, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var46, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var46, var41, ComposeUiNode.Companion.getSetModifier());
         int var48 = 14 & var44 >> 6;
         Composer var49 = var43;
         int var50 = 0;
         ComposerKt.sourceInformationMarkerStart(var49, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var51 = 6 | 112 & var71 >> 6;
         Composer var52 = var49;
         ColumnScope var53 = ColumnScopeInstance.INSTANCE;
         int var54 = 0;
         ComposerKt.sourceInformationMarkerStart(var52, 1973351005, "C159@7158L162,166@7330L274:EnhancedCodeBlock.kt#jxhu3x");
         recovered$1eb94a661870326e(var60, var16, var14, var2, var52, 7168 & var7 << 3);
         recovered$9296f843ee43af2d(var53, var2 instanceof io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded, var62, var58, var12, var14, var19, var52, 14 & var51);
         ComposerKt.sourceInformationMarkerEnd(var52);
         ComposerKt.sourceInformationMarkerEnd(var49);
         var43.endNode();
         ComposerKt.sourceInformationMarkerEnd(var43);
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var55);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var79 = var55.endRestartGroup();
      if (var79 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_1251_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1251_1 = var1;
         final io.intercom.android.sdk.survey.block.CodeBlockMode recoveredExpressionCapture_1251_2 = var2;
         final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_1251_3 = var3;
         final int recoveredExpressionCapture_1251_4 = var5;
         final int recoveredExpressionCapture_1251_5 = var6;
         var79.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1251_0, recoveredExpressionArgument_1251_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlock$lambda$3(recoveredExpressionCapture_1251_0, recoveredExpressionCapture_1251_1, recoveredExpressionCapture_1251_2, recoveredExpressionCapture_1251_3, recoveredExpressionCapture_1251_4, recoveredExpressionCapture_1251_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1251_0), ((java.lang.Integer)recoveredExpressionArgument_1251_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$9296f843ee43af2d(
      ColumnScope var0, boolean var1, int var2, AnnotatedString var3, long var4, long var6, TextStyle var8, Composer var9, int var10
   ) {
      Composer var40 = var9.startRestartGroup(-1913731767);
      ComposerKt.sourceInformation(
         var40, "C(CodeBlockBody)P(2,3!1,4:c#ui.graphics.Color,1:c#ui.graphics.Color)194@8149L21,193@8093L966:EnhancedCodeBlock.kt#jxhu3x"
      );
      int var11 = var10;
      if ((var10 & 6) == 0) {
         var11 |= var40.changed(var0) ? 4 : 2;
      }

      if ((var10 & 48) == 0) {
         var11 |= var40.changed(var1) ? 32 : 16;
      }

      if ((var10 & 384) == 0) {
         var11 |= var40.changed(var2) ? 256 : 128;
      }

      if ((var10 & 3072) == 0) {
         var11 |= var40.changed(var3) ? 2048 : 1024;
      }

      if ((var10 & 24576) == 0) {
         var11 |= var40.changed(var4) ? 16384 : 8192;
      }

      if ((var10 & 196608) == 0) {
         var11 |= var40.changed(var6) ? 131072 : 65536;
      }

      if ((var10 & 1572864) == 0) {
         var11 |= var40.changed(var8) ? 1048576 : 524288;
      }

      if ((var11 & 599187) == 599186 && var40.getSkipping()) {
         var40.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1913731767, var11, -1, "io.intercom.android.sdk.survey.block.CodeBlockBody (EnhancedCodeBlock.kt:185)");
         }

         Modifier var12 = var1
            ? ColumnScope.weight$default(var0, SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), 1.0F, false, 2, null)
            : var0.weight(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), 1.0F, false);
         Modifier var13 = ScrollKt.verticalScroll$default(var12, ScrollKt.rememberScrollState(0, var40, 0, 1), false, null, false, 14, null);
         byte var16 = 0;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var40, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var14 = Alignment.Companion.getTopStart();
         boolean var15 = false;
         MeasurePolicy var18 = BoxKt.maybeCachedBoxMeasurePolicy(var14, var15);
         Modifier var19 = var13;
         MeasurePolicy var20 = var18;
         Composer var21 = var40;
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
         ComposerKt.sourceInformationMarkerStart(var37, 460192003, "C196@8265L788,196@8189L864:EnhancedCodeBlock.kt#jxhu3x");
         CompositionLocalKt.CompositionLocalProvider(
            CompositionLocalsKt.getLocalLayoutDirection().provides(LayoutDirection.Ltr),
            ComposableLambdaKt.rememberComposableLambda(-111185777, true, new EnhancedCodeBlockKt$CodeBlockBody$1$1(var2, var8, var6, var3, var4), var37, 54),
            var37,
            48 | ProvidedValue.$stable
         );
         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var28.endNode();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var40);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var40.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.foundation.layout.ColumnScope recoveredExpressionCapture_1265_0 = var0;
         final boolean recoveredExpressionCapture_1265_1 = var1;
         final int recoveredExpressionCapture_1265_2 = var2;
         final androidx.compose.ui.text.AnnotatedString recoveredExpressionCapture_1265_3 = var3;
         final long recoveredExpressionCapture_1265_4 = var4;
         final long recoveredExpressionCapture_1265_5 = var6;
         final androidx.compose.ui.text.TextStyle recoveredExpressionCapture_1265_6 = var8;
         final int recoveredExpressionCapture_1265_7 = var10;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1265_0, recoveredExpressionArgument_1265_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.CodeBlockBody_wBJOh4Y$lambda$5(recoveredExpressionCapture_1265_0, recoveredExpressionCapture_1265_1, recoveredExpressionCapture_1265_2, recoveredExpressionCapture_1265_3, recoveredExpressionCapture_1265_4, recoveredExpressionCapture_1265_5, recoveredExpressionCapture_1265_6, recoveredExpressionCapture_1265_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1265_0), ((java.lang.Integer)recoveredExpressionArgument_1265_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$b0854c842d6b2b4c(int var0, TextStyle var1, long var2, Modifier var4, Composer var5, int var6, int var7) {
      Composer var23 = var5.startRestartGroup(-183031994);
      ComposerKt.sourceInformation(var23, "C(LineNumberGutter)P(!1,3,2:c#ui.graphics.Color)227@9281L107,234@9518L75,230@9393L207:EnhancedCodeBlock.kt#jxhu3x");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var23.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var23.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var23.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var23.changed(var4) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var23.getSkipping()) {
         var23.skipToGroupEnd();
      } else {
         if ((var7 & 8) != 0) {
            var4 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-183031994, var8, -1, "io.intercom.android.sdk.survey.block.LineNumberGutter (EnhancedCodeBlock.kt:225)");
         }

         int var9 = String.valueOf(var0).length();
         var23.startReplaceGroup(-61627804);
         ComposerKt.sourceInformation(var23, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var12 = var23;
         boolean var13 = (var8 & 14) == 4;
         int var14 = 0;
         Object var15 = var12.rememberedValue();
         int var16 = 0;
         Object var10000;
         if (!var13 && var15 != Composer.Companion.getEmpty()) {
            var10000 = var15;
         } else {
            int var17 = 0;
            final int recoveredExpressionCapture_1266_0 = var9;
            Object var18 = CollectionsKt.joinToString$default(
               new IntRange(1, var0), "\n", null, null, 0, null, ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1266_0) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.LineNumberGutter_cf5BqRc$lambda$7$lambda$6(recoveredExpressionCapture_1266_0, ((java.lang.Integer)recoveredExpressionArgument_1266_0).intValue())), 30, null
            );
            var12.updateRememberedValue(var18);
            var10000 = var18;
         }

         String var11 = (String)var10000;
         var23.endReplaceGroup();
         String var10 = var11;
         Modifier var32 = var4;
         boolean var10001 = false;
         var23.startReplaceGroup(-61620252);
         ComposerKt.sourceInformation(var23, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var26 = var23;
         boolean var27 = (var8 & 14) == 4;
         int $i$f$cachex = 0;
         Object it$ivx = var26.rememberedValue();
         int var30/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10002;
         if (!var27 && it$ivx != Composer.Companion.getEmpty()) {
            var10002 = it$ivx;
         } else {
            boolean var21 = false;
            Modifier var20 = var4;
            int var31 = 0;
            final int recoveredExpressionCapture_1267_0 = var0;
            Function1 var22 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1267_0) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.LineNumberGutter_cf5BqRc$lambda$9$lambda$8(recoveredExpressionCapture_1267_0, (androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1267_0)));
            var32 = var20;
            var10001 = var21;
            Object var19 = var22;
            var26.updateRememberedValue(var19);
            var10002 = var19;
         }

         Function1 var25 = (Function1)var10002;
         var23.endReplaceGroup();
         Modifier var24 = SemanticsModifierKt.semantics$default(var32, var10001, var25, 1, null);
         TextKt.recovered$5b114ab32a4f4190(
            var10, var24, var2, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var1, var23, 896 & var8, 29360128 & var8 << 18, 131064
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var33 = var23.endRestartGroup();
      if (var33 != null) {
         final int recoveredExpressionCapture_1268_0 = var0;
         final androidx.compose.ui.text.TextStyle recoveredExpressionCapture_1268_1 = var1;
         final long recoveredExpressionCapture_1268_2 = var2;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1268_3 = var4;
         final int recoveredExpressionCapture_1268_4 = var6;
         final int recoveredExpressionCapture_1268_5 = var7;
         var33.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1268_0, recoveredExpressionArgument_1268_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.LineNumberGutter_cf5BqRc$lambda$10(recoveredExpressionCapture_1268_0, recoveredExpressionCapture_1268_1, recoveredExpressionCapture_1268_2, recoveredExpressionCapture_1268_3, recoveredExpressionCapture_1268_4, recoveredExpressionCapture_1268_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1268_0), ((java.lang.Integer)recoveredExpressionArgument_1268_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$1eb94a661870326e(String var0, long var1, long var3, CodeBlockMode var5, Composer var6, int var7) {
      Composer var63 = var6.startRestartGroup(1135719998);
      ComposerKt.sourceInformation(
         var63,
         "C(CodeBlockHeader)P(3,0:c#ui.graphics.Color,1:c#ui.graphics.Color)247@9774L7,248@9827L7,249@9851L24,250@9894L34,252@9957L115,252@9934L138,259@10078L3315:EnhancedCodeBlock.kt#jxhu3x"
      );
      int var8 = var7;
      if ((var7 & 6) == 0) {
         var8 |= var63.changed(var0) ? 4 : 2;
      }

      if ((var7 & 48) == 0) {
         var8 |= var63.changed(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var8 |= var63.changed(var3) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var8 |= ((var7 & 4096) == 0 ? var63.changed(var5) : var63.changedInstance(var5)) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var63.getSkipping()) {
         var63.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1135719998, var8, -1, "io.intercom.android.sdk.survey.block.CodeBlockHeader (EnhancedCodeBlock.kt:246)");
         }

         CompositionLocal var10 = CompositionLocalsKt.getLocalClipboard();
         int var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var63, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var13 = var63.consume(var10);
         ComposerKt.sourceInformationMarkerEnd(var63);
         Clipboard var9 = (Clipboard)var13;
         CompositionLocal var65 = CompositionLocalsKt.getLocalHapticFeedback();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var63, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var14 = var63.consume(var65);
         ComposerKt.sourceInformationMarkerEnd(var63);
         HapticFeedback var64 = (HapticFeedback)var14;
         boolean var70 = false;
         int var75 = 0;
         ComposerKt.sourceInformationMarkerStart(var63, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var15 = var63;
         ComposerKt.sourceInformationMarkerStart(var63, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var16 = var63;
         boolean var17 = false;
         int var18 = 0;
         Object var19 = var16.rememberedValue();
         int var20 = 0;
         Object var10000;
         if (var19 == Composer.Companion.getEmpty()) {
            int var21 = 0;
            int var22 = 0;
            Object var112 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var15);
            var16.updateRememberedValue(var112);
            var10000 = var112;
         } else {
            var10000 = var19;
         }

         CoroutineScope var23 = (CoroutineScope)var10000;
         ComposerKt.sourceInformationMarkerEnd(var63);
         CoroutineScope var24 = var23;
         ComposerKt.sourceInformationMarkerEnd(var63);
         CoroutineScope var66 = var24;
         var63.startReplaceGroup(991538894);
         ComposerKt.sourceInformation(var63, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var76 = var63;
         boolean var81 = false;
         int $i$f$cachex = 0;
         Object var93 = var76.rememberedValue();
         int var99 = 0;
         Object var145;
         if (var93 == Composer.Companion.getEmpty()) {
            int var104 = 0;
            Object var105 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var76.updateRememberedValue(var105);
            var145 = var105;
         } else {
            var145 = var93;
         }

         MutableState var71 = (MutableState)var145;
         var63.endReplaceGroup();
         MutableState var68 = var71;
         Boolean var146 = CodeBlockHeader_RIQooxk$lambda$12(var68);
         var63.startReplaceGroup(991540991);
         ComposerKt.sourceInformation(var63, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var77 = var63;
         boolean var82 = false;
         int $i$f$cachexx = 0;
         Object it$ivx = var77.rememberedValue();
         int var100/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var155;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Boolean var62 = var146;
            int var106 = 0;
            Function2 var10001 = new EnhancedCodeBlockKt$CodeBlockHeader$1$1(var68, null);
            var146 = var62;
            Object var109 = var10001;
            var77.updateRememberedValue(var109);
            var155 = var109;
         } else {
            var155 = it$ivx;
         }

         Function2 var72 = (Function2)var155;
         var63.endReplaceGroup();
         EffectsKt.LaunchedEffect(var146, var72, var63, 0);
         Modifier var73 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var83 = 16;
         int var89 = 0;
         float var78 = Dp.recovered$95b3d103663bf060(var83);
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         float var84 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         int $this$dp$ivxx = 8;
         int $i$f$getDpxx = 0;
         float var91 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         float var97 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
         Modifier var74 = PaddingKt.recovered$f39cb96e94c47cf6(var73, var78, var91, var84, var97);
         int $this$dp$ivxxxx = 4;
         int $i$f$getDpxxxx = 0;
         androidx.compose.foundation.layout.Arrangement.Horizontal var80 = Arrangement.INSTANCE
            .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxxxx));
         androidx.compose.ui.Alignment.Vertical var86 = Alignment.Companion.getCenterVertically();
         short var92 = 432;
         int var98 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var63, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var103 = RowKt.rowMeasurePolicy(var80, var86, var63, 14 & var92 >> 3 | 112 & var92 >> 3);
         Modifier var108 = var74;
         MeasurePolicy var110 = var103;
         Composer var111 = var63;
         int var113 = 112 & var92 << 3;
         int var114 = 0;
         ComposerKt.sourceInformationMarkerStart(var111, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var115 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var111, 0));
         CompositionLocalMap var25 = var111.getCurrentCompositionLocalMap();
         Modifier var26 = ComposedModifierKt.materializeModifier(var111, var108);
         Function0 var27 = ComposeUiNode.Companion.getConstructor();
         Composer var28 = var111;
         int var29 = 6 | 896 & var113 << 6;
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
         Updater.recovered$89682b199a142880(var31, var110, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var31, var25, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var31, var115, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var31, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var31, var26, ComposeUiNode.Companion.getSetModifier());
         int var33 = 14 & var29 >> 6;
         Composer var34 = var28;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var36 = 6 | 112 & var92 >> 6;
         Composer var37 = var34;
         RowScope var38 = RowScopeInstance.INSTANCE;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var37, -173623815, "C*268@10487L50,270@10617L10,266@10348L413,277@10805L489,290@11402L111,294@11525L340,276@10771L1094:EnhancedCodeBlock.kt#jxhu3x"
         );
         String var147 = StringResources_androidKt.stringResource(string.intercom_code_block_label, var37, 0).toLowerCase(Locale.ROOT);
         Intrinsics.checkNotNullExpressionValue(var147, "toLowerCase(...)");
         String var40 = var147;
         TextStyle var41 = IntercomTheme.INSTANCE.getTypography(var37, IntercomTheme.$stable).getType05();
         int var42 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         Modifier var43 = RowScope.weight$default(var38, Modifier.Companion, 1.0F, false, 2, null);
         TextKt.recovered$5b114ab32a4f4190(
            var40, var43, var3, null, 0L, null, null, null, 0L, null, null, 0L, var42, false, 1, 0, null, var41, var37, 896 & var8, 24960, 110584
         );
         var37.startReplaceGroup(-1806704012);
         ComposerKt.sourceInformation(var37, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var119 = var37;
         boolean var122 = var63.changedInstance(var66) | var63.changedInstance(var9) | (var8 & 14) == 4 | var63.changedInstance(var64);
         int $i$f$cachexxx = 0;
         Object it$ivxx = var119.rememberedValue();
         int var45/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var148;
         if (!var122 && it$ivxx != Composer.Companion.getEmpty()) {
            var148 = it$ivxx;
         } else {
            int var46 = 0;
            final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1263_0 = var66;
            final androidx.compose.ui.platform.Clipboard recoveredExpressionCapture_1263_1 = var9;
            final java.lang.String recoveredExpressionCapture_1263_2 = var0;
            final androidx.compose.ui.hapticfeedback.HapticFeedback recoveredExpressionCapture_1263_3 = var64;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1263_4 = var68;
            Object var47 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.CodeBlockHeader_RIQooxk$lambda$24$lambda$16$lambda$15(recoveredExpressionCapture_1263_0, recoveredExpressionCapture_1263_1, recoveredExpressionCapture_1263_2, recoveredExpressionCapture_1263_3, recoveredExpressionCapture_1263_4));
            var119.updateRememberedValue(var47);
            var148 = var47;
         }

         Function0 var116 = (Function0)var148;
         var37.endReplaceGroup();
         Function0 var149 = var116;
         Modifier var156 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, HEADER_ICON_BUTTON_SIZE);
         boolean var10002 = false;
         var37.startReplaceGroup(-1806685286);
         ComposerKt.sourceInformation(var37, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
         Composer var120 = var37;
         boolean invalid$ivx = false;
         int $i$f$cachexxxx = 0;
         Object it$ivxxx = var120.rememberedValue();
         int var130/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var10003;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            boolean var48 = false;
            Modifier var49 = var156;
            Function0 var50 = var116;
            int var133 = 0;
            Function1 var51 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1264_0) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.CodeBlockHeader_RIQooxk$lambda$24$lambda$18$lambda$17((androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1264_0)));
            var149 = var50;
            var156 = var49;
            var10002 = var48;
            Object var134 = var51;
            var120.updateRememberedValue(var134);
            var10003 = var134;
         } else {
            var10003 = it$ivxxx;
         }

         Function1 var117 = (Function1)var10003;
         var37.endReplaceGroup();
         IconButtonKt.IconButton(
            var149,
            SemanticsModifierKt.semantics$default(var156, var10002, var117, 1, null),
            false,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1911786756, true, new EnhancedCodeBlockKt$CodeBlockHeader$2$3(var1, var68), var37, 54),
            var37,
            1572864,
            60
         );
         CodeBlockMode var118 = var5;
         if (var118 instanceof io.intercom.android.sdk.survey.block.CodeBlockMode$Inline) {
            var37.startReplaceGroup(-172114147);
            ComposerKt.sourceInformation(var37, "*311@12159L137,315@12316L311,307@11977L650");
            Function0 var150 = ((io.intercom.android.sdk.survey.block.CodeBlockMode$Inline)var5).getOnExpand();
            if (var150 == null) {
               Object var151 = null;
            } else {
               Function0 var131 = var150;
               int var135 = 0;
               Function0 var152 = var131;
               Modifier var157 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, HEADER_ICON_BUTTON_SIZE);
               boolean var159 = false;
               var37.startReplaceGroup(476267475);
               ComposerKt.sourceInformation(var37, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
               Composer var137 = var37;
               boolean invalid$ivxx = false;
               int $i$f$cachexxxxx = 0;
               Object it$ivxxxx = var137.rememberedValue();
               int var55/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
               Object var161;
               if (it$ivxxxx == Composer.Companion.getEmpty()) {
                  boolean var56 = false;
                  Modifier var57 = var157;
                  Function0 var58 = var131;
                  int var59 = 0;
                  Function1 var60 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1260_0) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.CodeBlockHeader_RIQooxk$lambda$24$lambda$21$lambda$20$lambda$19((androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1260_0)));
                  var152 = var58;
                  var157 = var57;
                  var159 = var56;
                  Object var144 = var60;
                  var137.updateRememberedValue(var144);
                  var161 = var144;
               } else {
                  var161 = it$ivxxxx;
               }

               Function1 var61 = (Function1)var161;
               var37.endReplaceGroup();
               IconButtonKt.IconButton(
                  var152,
                  SemanticsModifierKt.semantics$default(var157, var159, var61, 1, null),
                  false,
                  null,
                  null,
                  null,
                  ComposableLambdaKt.rememberComposableLambda(-538933775, true, new EnhancedCodeBlockKt$CodeBlockHeader$2$4$2(var1), var37, 54),
                  var37,
                  1572864,
                  60
               );
            }

            var37.endReplaceGroup();
         } else {
            if (!(var118 instanceof io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded)) {
               var37.startReplaceGroup(-1806668749);
               var37.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var37.startReplaceGroup(-171379230);
            ComposerKt.sourceInformation(var37, "330@12888L145,334@13053L310,326@12702L661");
            Function0 var153 = ((io.intercom.android.sdk.survey.block.CodeBlockMode$Expanded)var5).getOnClose();
            Modifier var158 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, HEADER_ICON_BUTTON_SIZE);
            boolean var160 = false;
            var37.startReplaceGroup(-1806637700);
            ComposerKt.sourceInformation(var37, "CC(remember):EnhancedCodeBlock.kt#9igjgp");
            Composer var124 = var37;
            boolean invalid$ivxx = false;
            int $i$f$cachexxxxx = 0;
            Object it$ivxxxx = var124.rememberedValue();
            int var136/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
            Object var162;
            if (it$ivxxxx == Composer.Companion.getEmpty()) {
               boolean var140 = false;
               Modifier var141 = var158;
               Function0 var142 = var153;
               int var138 = 0;
               Function1 var143 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1261_0) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.CodeBlockHeader_RIQooxk$lambda$24$lambda$23$lambda$22((androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1261_0)));
               var153 = var142;
               var158 = var141;
               var160 = var140;
               Object var139 = var143;
               var124.updateRememberedValue(var139);
               var162 = var139;
            } else {
               var162 = it$ivxxxx;
            }

            Function1 var121 = (Function1)var162;
            var37.endReplaceGroup();
            IconButtonKt.IconButton(
               var153,
               SemanticsModifierKt.semantics$default(var158, var160, var121, 1, null),
               false,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(-955926597, true, new EnhancedCodeBlockKt$CodeBlockHeader$2$6(var1), var37, 54),
               var37,
               1572864,
               60
            );
            var37.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var28.endNode();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var111);
         ComposerKt.sourceInformationMarkerEnd(var63);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var154 = var63.endRestartGroup();
      if (var154 != null) {
         final java.lang.String recoveredExpressionCapture_1262_0 = var0;
         final long recoveredExpressionCapture_1262_1 = var1;
         final long recoveredExpressionCapture_1262_2 = var3;
         final io.intercom.android.sdk.survey.block.CodeBlockMode recoveredExpressionCapture_1262_3 = var5;
         final int recoveredExpressionCapture_1262_4 = var7;
         var154.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1262_0, recoveredExpressionArgument_1262_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.CodeBlockHeader_RIQooxk$lambda$25(recoveredExpressionCapture_1262_0, recoveredExpressionCapture_1262_1, recoveredExpressionCapture_1262_2, recoveredExpressionCapture_1262_3, recoveredExpressionCapture_1262_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1262_0), ((java.lang.Integer)recoveredExpressionArgument_1262_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedCodeBlockDialog(@NotNull Block var0, @NotNull Function0 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "block");
      Intrinsics.checkNotNullParameter(var1, "onDismiss");
      Composer var5 = var2.startRestartGroup(-382178991);
      ComposerKt.sourceInformation(var5, "C(ExpandedCodeBlockDialog)357@13668L311,354@13544L435:EnhancedCodeBlock.kt#jxhu3x");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-382178991, var4, -1, "io.intercom.android.sdk.survey.block.ExpandedCodeBlockDialog (EnhancedCodeBlock.kt:353)");
         }

         AndroidDialog_androidKt.Dialog(
            var1,
            new DialogProperties(false, false, false, 3, null),
            ComposableLambdaKt.rememberComposableLambda(-851181400, true, new EnhancedCodeBlockKt$ExpandedCodeBlockDialog$1(var1, var0), var5, 54),
            var5,
            432 | 14 & var4 >> 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var5.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_1258_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1258_1 = var1;
         final int recoveredExpressionCapture_1258_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1258_0, recoveredExpressionArgument_1258_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.ExpandedCodeBlockDialog$lambda$26(recoveredExpressionCapture_1258_0, recoveredExpressionCapture_1258_1, recoveredExpressionCapture_1258_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1258_0), ((java.lang.Integer)recoveredExpressionArgument_1258_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1649627525);
      ComposerKt.sourceInformation(var2, "C(EnhancedCodeBlockPreview)372@14059L330:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1649627525, var1, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlockPreview (EnhancedCodeBlock.kt:371)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_1255_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1255_0, recoveredExpressionArgument_1255_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlockPreview$lambda$27(recoveredExpressionCapture_1255_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1255_0), ((java.lang.Integer)recoveredExpressionArgument_1255_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlockHighlightedPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1415112054);
      ComposerKt.sourceInformation(var2, "C(EnhancedCodeBlockHighlightedPreview)387@14480L683:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1415112054, var1, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlockHighlightedPreview (EnhancedCodeBlock.kt:386)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_1252_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1252_0, recoveredExpressionArgument_1252_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlockHighlightedPreview$lambda$28(recoveredExpressionCapture_1252_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1252_0), ((java.lang.Integer)recoveredExpressionArgument_1252_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlockLongPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(966689559);
      ComposerKt.sourceInformation(var2, "C(EnhancedCodeBlockLongPreview)408@15247L1316:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(966689559, var1, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlockLongPreview (EnhancedCodeBlock.kt:407)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$5a1850a8ff371bfb(),
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
         final int recoveredExpressionCapture_1253_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1253_0, recoveredExpressionArgument_1253_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlockLongPreview$lambda$29(recoveredExpressionCapture_1253_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1253_0), ((java.lang.Integer)recoveredExpressionArgument_1253_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlockNewlineDelimitedPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(231353184);
      ComposerKt.sourceInformation(var2, "C(EnhancedCodeBlockNewlineDelimitedPreview)446@17031L556:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               231353184, var1, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlockNewlineDelimitedPreview (EnhancedCodeBlock.kt:445)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$a427d7fccaa25a17(),
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
         final int recoveredExpressionCapture_1254_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1254_0, recoveredExpressionArgument_1254_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlockNewlineDelimitedPreview$lambda$30(recoveredExpressionCapture_1254_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1254_0), ((java.lang.Integer)recoveredExpressionArgument_1254_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlockWidePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-791698066);
      ComposerKt.sourceInformation(var2, "C(EnhancedCodeBlockWidePreview)467@17671L410:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-791698066, var1, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlockWidePreview (EnhancedCodeBlock.kt:466)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$abc66e26750555d7(),
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
         final int recoveredExpressionCapture_1257_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1257_0, recoveredExpressionArgument_1257_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlockWidePreview$lambda$31(recoveredExpressionCapture_1257_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1257_0), ((java.lang.Integer)recoveredExpressionArgument_1257_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EnhancedCodeBlockRtlPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1329193357);
      ComposerKt.sourceInformation(var2, "C(EnhancedCodeBlockRtlPreview)483@18164L456:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1329193357, var1, -1, "io.intercom.android.sdk.survey.block.EnhancedCodeBlockRtlPreview (EnhancedCodeBlock.kt:482)");
         }

         CompositionLocalKt.CompositionLocalProvider(
            CompositionLocalsKt.getLocalLayoutDirection().provides(LayoutDirection.Rtl),
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$5994e78f13d09dca(),
            var2,
            48 | ProvidedValue.$stable
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1256_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1256_0, recoveredExpressionArgument_1256_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.EnhancedCodeBlockRtlPreview$lambda$32(recoveredExpressionCapture_1256_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1256_0), ((java.lang.Integer)recoveredExpressionArgument_1256_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedCodeBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1541992808);
      ComposerKt.sourceInformation(var2, "C(ExpandedCodeBlockPreview)500@18700L1368:EnhancedCodeBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1541992808, var1, -1, "io.intercom.android.sdk.survey.block.ExpandedCodeBlockPreview (EnhancedCodeBlock.kt:499)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$EnhancedCodeBlockKt.INSTANCE.recovered$33b89cdc19118807(),
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
         final int recoveredExpressionCapture_1259_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1259_0, recoveredExpressionArgument_1259_1) -> io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt.ExpandedCodeBlockPreview$lambda$33(recoveredExpressionCapture_1259_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1259_0), ((java.lang.Integer)recoveredExpressionArgument_1259_1).intValue())));
      }
   }

   private static final Unit EnhancedCodeBlock$lambda$3(Block var0, Modifier var1, CodeBlockMode var2, Shape var3, int var4, int var5, Composer var6, int var7) {
      EnhancedCodeBlock(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit CodeBlockBody_wBJOh4Y$lambda$5(
      ColumnScope var0, boolean var1, int var2, AnnotatedString var3, long var4, long var6, TextStyle var8, int var9, Composer var10, int var11
   ) {
      recovered$9296f843ee43af2d(var0, var1, var2, var3, var4, var6, var8, var10, RecomposeScopeImplKt.updateChangedFlags(var9 | 1));
      return Unit.INSTANCE;
   }

   private static final CharSequence LineNumberGutter_cf5BqRc$lambda$7$lambda$6(int var0, int var1) {
      return StringsKt.padStart$default(String.valueOf(var1), var0, '\u0000', 2, null);
   }

   private static final Unit LineNumberGutter_cf5BqRc$lambda$9$lambda$8(int var0, SemanticsPropertyReceiver var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var1, "Line numbers 1 to " + var0);
      return Unit.INSTANCE;
   }

   private static final Unit LineNumberGutter_cf5BqRc$lambda$10(int var0, TextStyle var1, long var2, Modifier var4, int var5, int var6, Composer var7, int var8) {
      recovered$b0854c842d6b2b4c(var0, var1, var2, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final boolean CodeBlockHeader_RIQooxk$lambda$12(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void CodeBlockHeader_RIQooxk$lambda$13(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit CodeBlockHeader_RIQooxk$lambda$24$lambda$16$lambda$15(
      CoroutineScope var0, Clipboard var1, String var2, HapticFeedback var3, MutableState var4
   ) {
      BuildersKt.launch$default(var0, null, null, new EnhancedCodeBlockKt$CodeBlockHeader$2$1$1$1(var1, var2, var3, var4, null), 3, null);
      return Unit.INSTANCE;
   }

   private static final Unit CodeBlockHeader_RIQooxk$lambda$24$lambda$18$lambda$17(SemanticsPropertyReceiver var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var0, "Copy code");
      SemanticsPropertiesKt.recovered$fed06fdc960a8485(var0, Role.Companion.recovered$c675f3db0d521c2c());
      return Unit.INSTANCE;
   }

   private static final Unit CodeBlockHeader_RIQooxk$lambda$24$lambda$21$lambda$20$lambda$19(SemanticsPropertyReceiver var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var0, "Expand code");
      SemanticsPropertiesKt.recovered$fed06fdc960a8485(var0, Role.Companion.recovered$c675f3db0d521c2c());
      return Unit.INSTANCE;
   }

   private static final Unit CodeBlockHeader_RIQooxk$lambda$24$lambda$23$lambda$22(SemanticsPropertyReceiver var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var0, "Close expanded code");
      SemanticsPropertiesKt.recovered$fed06fdc960a8485(var0, Role.Companion.recovered$c675f3db0d521c2c());
      return Unit.INSTANCE;
   }

   private static final Unit CodeBlockHeader_RIQooxk$lambda$25(String var0, long var1, long var3, CodeBlockMode var5, int var6, Composer var7, int var8) {
      recovered$1eb94a661870326e(var0, var1, var3, var5, var7, RecomposeScopeImplKt.updateChangedFlags(var6 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedCodeBlockDialog$lambda$26(Block var0, Function0 var1, int var2, Composer var3, int var4) {
      ExpandedCodeBlockDialog(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EnhancedCodeBlockPreview$lambda$27(int var0, Composer var1, int var2) {
      EnhancedCodeBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EnhancedCodeBlockHighlightedPreview$lambda$28(int var0, Composer var1, int var2) {
      EnhancedCodeBlockHighlightedPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EnhancedCodeBlockLongPreview$lambda$29(int var0, Composer var1, int var2) {
      EnhancedCodeBlockLongPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EnhancedCodeBlockNewlineDelimitedPreview$lambda$30(int var0, Composer var1, int var2) {
      EnhancedCodeBlockNewlineDelimitedPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EnhancedCodeBlockWidePreview$lambda$31(int var0, Composer var1, int var2) {
      EnhancedCodeBlockWidePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EnhancedCodeBlockRtlPreview$lambda$32(int var0, Composer var1, int var2) {
      EnhancedCodeBlockRtlPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedCodeBlockPreview$lambda$33(int var0, Composer var1, int var2) {
      ExpandedCodeBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final float access$getEXPANDED_DIALOG_MARGIN$p() {
      return EXPANDED_DIALOG_MARGIN;
   }

   // $VF: synthetic method
   public static final void recovered$46f86118b70b7ca8(int var0, TextStyle var1, long var2, Modifier var4, Composer var5, int var6, int var7) {
      recovered$b0854c842d6b2b4c(var0, var1, var2, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static final float access$getGUTTER_TO_CODE_SPACING$p() {
      return GUTTER_TO_CODE_SPACING;
   }

   // $VF: synthetic method
   public static final boolean access$CodeBlockHeader_RIQooxk$lambda$12(MutableState var0) {
      return CodeBlockHeader_RIQooxk$lambda$12(var0);
   }

   // $VF: synthetic method
   public static final long access$getCOPY_CONFIRMATION_DURATION_MS$p() {
      return COPY_CONFIRMATION_DURATION_MS;
   }

   // $VF: synthetic method
   public static final void access$CodeBlockHeader_RIQooxk$lambda$13(MutableState var0, boolean var1) {
      CodeBlockHeader_RIQooxk$lambda$13(var0, var1);
   }

   // $VF: synthetic method
   public static final Twig access$getTWIG$p() {
      return TWIG;
   }

   // $VF: synthetic method
   public static final float access$getHEADER_ICON_SIZE$p() {
      return HEADER_ICON_SIZE;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      TWIG = var10000;
      int var0 = 416;
      int var1 = 0;
      CODE_BLOCK_MAX_HEIGHT = Dp.recovered$95b3d103663bf060(var0);
      int $this$dp$ivx = 8;
      int $i$f$getDpx = 0;
      CODE_BLOCK_CORNER_RADIUS = Dp.recovered$95b3d103663bf060($this$dp$ivx);
      int $this$dp$ivxx = 12;
      int $i$f$getDpxx = 0;
      CODE_BLOCK_EXPANDED_CORNER_RADIUS = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
      COPY_CONFIRMATION_DURATION_MS = 1500L;
      int $this$dp$ivxxx = 20;
      int $i$f$getDpxxx = 0;
      HEADER_ICON_SIZE = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
      int $this$dp$ivxxxx = 40;
      int $i$f$getDpxxxx = 0;
      HEADER_ICON_BUTTON_SIZE = Dp.recovered$95b3d103663bf060($this$dp$ivxxxx);
      int $this$dp$ivxxxxx = 20;
      int $i$f$getDpxxxxx = 0;
      GUTTER_TO_CODE_SPACING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx);
      int $this$dp$ivxxxxxx = 16;
      int $i$f$getDpxxxxxx = 0;
      EXPANDED_DIALOG_MARGIN = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx);
   }
}
