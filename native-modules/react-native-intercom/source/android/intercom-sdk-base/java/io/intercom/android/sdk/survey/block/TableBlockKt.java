package io.intercom.android.sdk.survey.block;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.core.Animatable;
import androidx.compose.animation.core.AnimatableKt;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.EasingKt;
import androidx.compose.animation.core.InfiniteRepeatableSpec;
import androidx.compose.animation.core.InfiniteTransition;
import androidx.compose.animation.core.InfiniteTransitionKt;
import androidx.compose.animation.core.MutableTransitionState;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
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
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.graphics.BlendMode;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.GraphicsLayerModifierKt;
import androidx.compose.ui.graphics.GraphicsLayerScope;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.graphics.drawscope.ContentDrawScope;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.window.AndroidDialog_androidKt;
import androidx.compose.ui.window.DialogProperties;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.block.TableBlockMode$Expanded;
import io.intercom.android.sdk.survey.block.TableBlockMode$Inline;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
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
   d1 = "\u0000p\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0007\u001a?\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00182\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001eH\u0001¢\u0006\u0002\u0010\u001f\u001a-\u0010 \u001a\u00020\u00142\u0006\u0010!\u001a\u00020\"2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010#\u001a\u00020\u001e2\u0006\u0010\u001d\u001a\u00020\u001eH\u0003¢\u0006\u0002\u0010$\u001a=\u0010%\u001a\u00020\u00142\u0006\u0010&\u001a\u00020\u00102\u0006\u0010'\u001a\u00020\"2\u0006\u0010(\u001a\u00020)2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00140+2\u0006\u0010,\u001a\u00020\u0001H\u0003¢\u0006\u0004\b-\u0010.\u001a'\u0010/\u001a\u00020\u00142\u0006\u00100\u001a\u0002012\u0006\u00102\u001a\u00020\u001e2\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0003¢\u0006\u0002\u00103\u001a\u0017\u00104\u001a\u00020\u00142\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0003¢\u0006\u0002\u00105\u001a/\u00106\u001a\u00020\u00142\u0006\u00100\u001a\u0002012\u0006\u00107\u001a\u00020\u00102\u0006\u00108\u001a\u0002092\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0003¢\u0006\u0002\u0010:\u001a'\u0010;\u001a\u00020\u00142\u0006\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020?2\u0006\u0010@\u001a\u00020)H\u0003¢\u0006\u0004\bA\u0010B\u001a#\u0010C\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00140+H\u0001¢\u0006\u0002\u0010E\u001a\r\u0010F\u001a\u00020\u0014H\u0001¢\u0006\u0002\u0010G\u001a\r\u0010H\u001a\u00020\u0014H\u0001¢\u0006\u0002\u0010G\u001a\r\u0010I\u001a\u00020\u0014H\u0001¢\u0006\u0002\u0010G\u001a\r\u0010J\u001a\u00020\u0014H\u0001¢\u0006\u0002\u0010G\u001a\r\u0010K\u001a\u00020\u0014H\u0001¢\u0006\u0002\u0010G\u001a\b\u0010L\u001a\u00020\u0016H\u0002\u001a\b\u0010M\u001a\u00020\u0016H\u0002\u001a\b\u0010N\u001a\u00020\u0016H\u0002\u001a\b\u0010O\u001a\u00020\u0016H\u0002\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0003\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0004\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0005\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0006\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0007\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\b\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\t\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\n\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u000b\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\f\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\r\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u000e\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002\"\u000e\u0010\u000f\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0011\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000\"\u0010\u0010\u0012\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002¨\u0006P²\u0006\n\u0010Q\u001a\u00020RX\u008a\u0084\u0002",
   d2 = {
         "TABLE_MAX_HEIGHT",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "TABLE_CORNER_RADIUS",
         "TABLE_EXPANDED_CORNER_RADIUS",
         "TABLE_MAX_COLUMN_WIDTH",
         "TABLE_COLUMN_SPACING",
         "TABLE_CELL_VERTICAL_PADDING",
         "TABLE_HORIZONTAL_PADDING",
         "TABLE_BOTTOM_PADDING",
         "TABLE_DIVIDER_HEIGHT",
         "HEADER_ICON_SIZE",
         "HEADER_ICON_BUTTON_SIZE",
         "TABLE_HEADER_TOP_PADDING",
         "EXPANDED_DIALOG_MARGIN",
         "TABLE_REVEAL_DURATION_MS",
         "",
         "TABLE_SHIMMER_DURATION_MS",
         "TABLE_GENERATING_PLACEHOLDER_BOTTOM_PADDING",
         "TableBlock",
         "",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "mode",
         "Lio/intercom/android/sdk/survey/block/TableBlockMode;",
         "shape",
         "Landroidx/compose/ui/graphics/Shape;",
         "isStreaming",
         "",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/block/TableBlockMode;Landroidx/compose/ui/graphics/Shape;ZLandroidx/compose/runtime/Composer;II)V",
         "TableHeader",
         "title",
         "",
         "showExpandAffordance",
         "(Ljava/lang/String;Lio/intercom/android/sdk/survey/block/TableBlockMode;ZZLandroidx/compose/runtime/Composer;I)V",
         "TableHeaderAction",
         "iconRes",
         "description",
         "tint",
         "Landroidx/compose/ui/graphics/Color;",
         "onClick",
         "Lkotlin/Function0;",
         "layoutHeight",
         "TableHeaderAction-u6trifg",
         "(ILjava/lang/String;JLkotlin/jvm/functions/Function0;FLandroidx/compose/runtime/Composer;I)V",
         "TableBody",
         "model",
         "Lio/intercom/android/sdk/survey/block/TableModel;",
         "isExpanded",
         "(Lio/intercom/android/sdk/survey/block/TableModel;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "GeneratingTablePlaceholder",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TableGrid",
         "availableWidthPx",
         "verticalScrollState",
         "Landroidx/compose/foundation/ScrollState;",
         "(Lio/intercom/android/sdk/survey/block/TableModel;ILandroidx/compose/foundation/ScrollState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TableCellContent",
         "cell",
         "Lio/intercom/android/sdk/survey/block/Cell;",
         "style",
         "Landroidx/compose/ui/text/TextStyle;",
         "color",
         "TableCellContent-XO-JAsU",
         "(Lio/intercom/android/sdk/survey/block/Cell;Landroidx/compose/ui/text/TextStyle;JLandroidx/compose/runtime/Composer;I)V",
         "ExpandedTableBlockDialog",
         "onDismiss",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "TableBlockPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TallTableBlockPreview",
         "ExpandedTableBlockPreview",
         "GeneratingTableBlockPreview",
         "CaptionlessTableBlockPreview",
         "sampleTableBlock",
         "captionlessSampleTableBlock",
         "tallSampleTableBlock",
         "headerOnlyTableBlock",
         "intercom-sdk-base_release",
         "progress",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nTableBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/TableBlockKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 8 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 9 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 10 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 11 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 12 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 13 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 14 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 15 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 16 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 17 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,747:1\n1047#2,6:748\n1047#2,6:788\n1047#2,6:794\n1047#2,6:800\n1047#2,6:845\n1047#2,6:886\n1047#2,6:896\n1047#2,6:902\n1047#2,6:909\n118#3:754\n118#3:813\n118#3:814\n118#3:815\n118#3:816\n118#3:964\n118#3:965\n118#3:966\n118#3:967\n118#3:968\n118#3:969\n118#3:970\n118#3:971\n118#3:972\n118#3:973\n118#3:974\n118#3:975\n118#3:976\n118#3:977\n87#4:755\n84#4,9:756\n94#4:809\n81#5,6:765\n88#5,6:780\n96#5:808\n81#5,6:823\n88#5,6:838\n96#5:853\n81#5,6:864\n88#5,6:879\n96#5:894\n81#5,6:915\n88#5,6:930\n96#5:944\n402#6,9:771\n411#6:786\n412#6,2:806\n402#6,9:829\n411#6:844\n412#6,2:851\n402#6,9:870\n411#6:885\n412#6,2:892\n402#6,9:921\n411#6:936\n412#6,2:942\n1#7:787\n75#8:810\n75#8:908\n60#9:811\n49#9:812\n99#10,6:817\n106#10:854\n70#11:855\n68#11,8:856\n77#11:895\n1878#12,2:937\n1869#12,2:939\n1880#12:941\n1563#12:945\n1634#12,3:946\n85#13:949\n57#14:950\n57#14:957\n60#15:951\n53#15,3:954\n60#15:958\n53#15,3:961\n23#16:952\n23#16:959\n30#17:953\n30#17:960\n*S KotlinDebug\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/TableBlockKt\n*L\n145#1:748,6\n195#1:788,6\n196#1:794,6\n206#1:800,6\n269#1:845,6\n316#1:886,6\n384#1:896,6\n418#1:902,6\n455#1:909,6\n169#1:754\n241#1:813\n242#1:814\n244#1:815\n246#1:816\n79#1:964\n80#1:965\n83#1:966\n84#1:967\n85#1:968\n86#1:969\n87#1:970\n88#1:971\n89#1:972\n90#1:973\n91#1:974\n96#1:975\n97#1:976\n108#1:977\n164#1:755\n164#1:756,9\n164#1:809\n164#1:765,6\n164#1:780,6\n164#1:808\n236#1:823,6\n236#1:838,6\n236#1:853\n308#1:864,6\n308#1:879,6\n308#1:894\n436#1:915,6\n436#1:930,6\n436#1:944\n164#1:771,9\n164#1:786\n164#1:806,2\n236#1:829,9\n236#1:844\n236#1:851,2\n308#1:870,9\n308#1:885\n308#1:892,2\n436#1:921,9\n436#1:936\n436#1:942,2\n229#1:810\n428#1:908\n235#1:811\n235#1:812\n236#1:817,6\n236#1:854\n308#1:855\n308#1:856,8\n308#1:895\n439#1:937,2\n441#1:939,2\n439#1:941\n727#1:945\n727#1:946,3\n361#1:949\n386#1:950\n391#1:957\n386#1:951\n390#1:954,3\n391#1:958\n391#1:961,3\n386#1:952\n391#1:959\n390#1:953\n391#1:960\n*E\n"
)
public final class TableBlockKt {
   private static final float TABLE_MAX_HEIGHT;
   private static final float TABLE_CORNER_RADIUS;
   private static final float TABLE_EXPANDED_CORNER_RADIUS;
   private static final float TABLE_MAX_COLUMN_WIDTH;
   private static final float TABLE_COLUMN_SPACING;
   private static final float TABLE_CELL_VERTICAL_PADDING;
   private static final float TABLE_HORIZONTAL_PADDING;
   private static final float TABLE_BOTTOM_PADDING;
   private static final float TABLE_DIVIDER_HEIGHT;
   private static final float HEADER_ICON_SIZE;
   private static final float HEADER_ICON_BUTTON_SIZE;
   private static final float TABLE_HEADER_TOP_PADDING;
   private static final float EXPANDED_DIALOG_MARGIN;
   private static final int TABLE_REVEAL_DURATION_MS = 500;
   private static final int TABLE_SHIMMER_DURATION_MS = 1200;
   private static final float TABLE_GENERATING_PLACEHOLDER_BOTTOM_PADDING;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TableBlock(
      @NotNull Block var0,
      @Nullable Modifier var1,
      @Nullable TableBlockMode var2,
      @Nullable Shape var3,
      boolean var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "block");
      Composer var56 = var5.startRestartGroup(-743549096);
      ComposerKt.sourceInformation(var56, "C(TableBlock)P(!1,3,2,4)144@6855L40,167@7688L6,168@7772L6,163@7552L2219:TableBlock.kt#jxhu3x");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var56.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var56.changed(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var8 |= (var7 & 4) == 0 && ((var6 & 512) == 0 ? var56.changed(var2) : var56.changedInstance(var2)) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var56.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var56.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var56.getSkipping()) {
         var56.skipToGroupEnd();
      } else {
         var56.startDefaults();
         if ((var6 & 1) != 0 && !var56.getDefaultsInvalid()) {
            var56.skipToGroupEnd();
            if ((var7 & 4) != 0) {
               var8 &= -897;
            }
         } else {
            if ((var7 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var7 & 4) != 0) {
               var2 = new io.intercom.android.sdk.survey.block.TableBlockMode$Inline(null, 1, null);
               var8 &= -897;
            }

            if ((var7 & 8) != 0) {
               var3 = null;
            }

            if ((var7 & 16) != 0) {
               var4 = false;
            }
         }

         var56.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-743549096, var8, -1, "io.intercom.android.sdk.survey.block.TableBlock (TableBlock.kt:143)");
         }

         var56.startReplaceGroup(-1358182552);
         ComposerKt.sourceInformation(var56, "CC(remember):TableBlock.kt#9igjgp");
         Composer var11 = var56;
         boolean var12 = var56.changed(var0);
         int var13 = 0;
         Object var14 = var11.rememberedValue();
         int var15 = 0;
         Object var10000;
         if (!var12 && var14 != Composer.Companion.getEmpty()) {
            var10000 = var14;
         } else {
            int var16 = 0;
            Object var17 = TableModelKt.toTableModel(var0);
            var11.updateRememberedValue(var17);
            var10000 = var17;
         }

         TableModel var10 = (TableModel)var10000;
         var56.endReplaceGroup();
         TableModel var9 = var10;
         if (var9.getColumnCount() == 0) {
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var93 = var56.endRestartGroup();
            if (var93 != null) {
               final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_1288_0 = var0;
               final androidx.compose.ui.Modifier recoveredExpressionCapture_1288_1 = var1;
               final io.intercom.android.sdk.survey.block.TableBlockMode recoveredExpressionCapture_1288_2 = var2;
               final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_1288_3 = var3;
               final boolean recoveredExpressionCapture_1288_4 = var4;
               final int recoveredExpressionCapture_1288_5 = var6;
               final int recoveredExpressionCapture_1288_6 = var7;
               var93.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1288_0, recoveredExpressionArgument_1288_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableBlock$lambda$1(recoveredExpressionCapture_1288_0, recoveredExpressionCapture_1288_1, recoveredExpressionCapture_1288_2, recoveredExpressionCapture_1288_3, recoveredExpressionCapture_1288_4, recoveredExpressionCapture_1288_5, recoveredExpressionCapture_1288_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1288_0), ((java.lang.Integer)recoveredExpressionArgument_1288_1).intValue())));
            }

            return;
         }

         boolean var57 = var4 && var9.getRows().isEmpty();
         float var58 = var2 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Expanded ? TABLE_EXPANDED_CORNER_RADIUS : TABLE_CORNER_RADIUS;
         Shape var87 = var3;
         if (var3 == null) {
            var87 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(var58);
         }

         Shape var59 = var87;
         Modifier var60 = var2 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Expanded ? var1 : SizeKt.recovered$1c935d354e9159f0(var1, 0.0F, TABLE_MAX_HEIGHT, 1, null);
         Modifier var88 = BackgroundKt.recovered$42a3174180b3fb7a(
            ClipKt.clip(SizeKt.fillMaxWidth$default(var60, 0.0F, 1, null), var59),
            IntercomTheme.INSTANCE.getColors(var56, IntercomTheme.$stable).getContainer().recovered$94e2a6def685ccbe(),
            null,
            2,
            null
         );
         int var61 = 1;
         int var63 = 0;
         Modifier var62 = BorderKt.recovered$1227359600bc50fd(
            var88,
            Dp.recovered$95b3d103663bf060(var61),
            IntercomTheme.INSTANCE.getColors(var56, IntercomTheme.$stable).getBorder().recovered$e2b74d614aa09788(),
            var59
         );
         byte var66 = 0;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var56, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var64 = Arrangement.INSTANCE.getTop();
         Horizontal var65 = Alignment.Companion.getStart();
         MeasurePolicy var19 = ColumnKt.columnMeasurePolicy(var64, var65, var56, 14 & var66 >> 3 | 112 & var66 >> 3);
         Modifier var20 = var62;
         MeasurePolicy var21 = var19;
         Composer var22 = var56;
         int var23 = 112 & var66 << 3;
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
         int var37 = 6 | 112 & var66 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 1434215063, "C171@7923L168:TableBlock.kt#jxhu3x");
         var38.startReplaceGroup(-2031946025);
         ComposerKt.sourceInformation(var38, "*170@7868L44");
         CharSequence var41 = var0.getCaption();
         CharSequence var89;
         if (StringsKt.isBlank(var41)) {
            int var42 = 0;
            var89 = StringResources_androidKt.stringResource(string.intercom_data_table, var38, 0);
         } else {
            var89 = var41;
         }

         String var43 = (String)var89;
         var38.endReplaceGroup();
         String var44 = var43;
         Intrinsics.checkNotNull(var44);
         TableHeader(var44, var2, !var57, var4, var38, 112 & var8 >> 3 | 7168 & var8 >> 3);
         Modifier var71 = var2 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Expanded
            ? ColumnScope.weight$default(var39, Modifier.Companion, 1.0F, false, 2, null)
            : var39.weight(Modifier.Companion, 1.0F, false);
         if (var57) {
            var38.startReplaceGroup(1434916344);
            ComposerKt.sourceInformation(var38, "188@8611L51");
            GeneratingTablePlaceholder(var71, var38, 0, 0);
            var38.endReplaceGroup();
         } else {
            var38.startReplaceGroup(1435028068);
            ComposerKt.sourceInformation(var38, "194@9086L52,195@9172L170,195@9151L191,205@9711L29,200@9355L400");
            var38.startReplaceGroup(-2031906733);
            ComposerKt.sourceInformation(var38, "CC(remember):TableBlock.kt#9igjgp");
            Composer var45 = var38;
            boolean var46 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var45.rememberedValue();
            int var49/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var90;
            if (it$ivx == Composer.Companion.getEmpty()) {
               int var50 = 0;
               Object var51 = AnimatableKt.Animatable$default(var4 ? 0.0F : 1.0F, 0.0F, 2, null);
               var45.updateRememberedValue(var51);
               var90 = var51;
            } else {
               var90 = it$ivx;
            }

            Animatable var68 = (Animatable)var90;
            var38.endReplaceGroup();
            Animatable var67 = var68;
            Unit var91 = Unit.INSTANCE;
            var38.startReplaceGroup(-2031903863);
            ComposerKt.sourceInformation(var38, "CC(remember):TableBlock.kt#9igjgp");
            Composer var72 = var38;
            boolean invalid$ivx = (var8 & 57344) == 16384 | var56.changedInstance(var67);
            int $i$f$cachexx = 0;
            Object it$ivxx = var72.rememberedValue();
            int var80/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var95;
            if (!invalid$ivx && it$ivxx != Composer.Companion.getEmpty()) {
               var95 = it$ivxx;
            } else {
               Unit var52 = var91;
               int var82 = 0;
               Function2 var10001 = new TableBlockKt$TableBlock$2$1$1(var4, var67, null);
               var91 = var52;
               Object var84 = var10001;
               var72.updateRememberedValue(var84);
               var95 = var84;
            }

            Function2 var69 = (Function2)var95;
            var38.endReplaceGroup();
            EffectsKt.LaunchedEffect(var91, var69, var38, 6);
            TableModel var92 = var9;
            boolean var96 = var2 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Expanded;
            Modifier var10002 = var71;
            var38.startReplaceGroup(-2031886756);
            ComposerKt.sourceInformation(var38, "CC(remember):TableBlock.kt#9igjgp");
            Composer var73 = var38;
            boolean invalid$ivxx = var38.changedInstance(var67);
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var73.rememberedValue();
            int var81/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var10003;
            if (!invalid$ivxx && it$ivxxx != Composer.Companion.getEmpty()) {
               var10003 = it$ivxxx;
            } else {
               Modifier var53 = var71;
               boolean var54 = var96;
               TableModel var86 = var9;
               int var83 = 0;
               final androidx.compose.animation.core.Animatable recoveredExpressionCapture_1286_0 = var67;
               Function1 var55 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1286_0) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableBlock$lambda$7$lambda$6$lambda$5(recoveredExpressionCapture_1286_0, (androidx.compose.ui.graphics.GraphicsLayerScope) ((androidx.compose.ui.graphics.GraphicsLayerScope)recoveredExpressionArgument_1286_0)));
               var92 = var86;
               var96 = var54;
               var10002 = var53;
               Object var85 = var55;
               var73.updateRememberedValue(var85);
               var10003 = var85;
            }

            Function1 var70 = (Function1)var10003;
            var38.endReplaceGroup();
            TableBody(var92, var96, GraphicsLayerModifierKt.graphicsLayer(var10002, var70), var38, 0, 0);
            var38.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var56);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var94 = var56.endRestartGroup();
      if (var94 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_1287_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1287_1 = var1;
         final io.intercom.android.sdk.survey.block.TableBlockMode recoveredExpressionCapture_1287_2 = var2;
         final androidx.compose.ui.graphics.Shape recoveredExpressionCapture_1287_3 = var3;
         final boolean recoveredExpressionCapture_1287_4 = var4;
         final int recoveredExpressionCapture_1287_5 = var6;
         final int recoveredExpressionCapture_1287_6 = var7;
         var94.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1287_0, recoveredExpressionArgument_1287_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableBlock$lambda$8(recoveredExpressionCapture_1287_0, recoveredExpressionCapture_1287_1, recoveredExpressionCapture_1287_2, recoveredExpressionCapture_1287_3, recoveredExpressionCapture_1287_4, recoveredExpressionCapture_1287_5, recoveredExpressionCapture_1287_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1287_0), ((java.lang.Integer)recoveredExpressionArgument_1287_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TableHeader(String var0, TableBlockMode var1, boolean var2, boolean var3, Composer var4, int var5) {
      Composer var54 = var4.startRestartGroup(-178707764);
      ComposerKt.sourceInformation(var54, "C(TableHeader)P(3,1,2)225@10873L10,235@11453L2962:TableBlock.kt#jxhu3x");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var54.changed(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= ((var5 & 64) == 0 ? var54.changed(var1) : var54.changedInstance(var1)) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var54.changed(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var54.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var54.getSkipping()) {
         var54.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-178707764, var6, -1, "io.intercom.android.sdk.survey.block.TableHeader (TableBlock.kt:223)");
         }

         boolean var7 = var1 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Expanded;
         TextStyle var8 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var54, IntercomTheme.$stable).getType04Point5(),
            0L,
            0L,
            FontWeight.Companion.getSemiBold(),
            null,
            null,
            null,
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
            16777211,
            null
         );
         var54.startReplaceGroup(388982242);
         ComposerKt.sourceInformation(var54, "*228@11122L7");
         float var10000;
         if (var7) {
            CompositionLocal var11 = CompositionLocalsKt.getLocalDensity();
            int var12 = 0;
            int var13 = 0;
            ComposerKt.sourceInformationMarkerStart(var54, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var14 = var54.consume(var11);
            ComposerKt.sourceInformationMarkerEnd(var54);
            Density var60 = (Density)var14;
            int var68 = 0;
            var10000 = var60.recovered$f7556ea49f9f71e9(var8.recovered$17d1bb629b307c0f());
         } else {
            var10000 = HEADER_ICON_BUTTON_SIZE;
         }

         float var10 = var10000;
         var54.endReplaceGroup();
         float var9 = var10;
         float var56 = TABLE_HEADER_TOP_PADDING;
         int var61 = 2;
         int var69 = 0;
         float var62 = Dp.recovered$95b3d103663bf060(var9 / var61);
         int var70 = 0;
         float var57 = Dp.recovered$95b3d103663bf060(var56 + var62);
         float var63 = HEADER_ICON_BUTTON_SIZE;
         int other$ivx = 2;
         int var77/* $VF was: $i$f$div-u2uoSUMx */ = 0;
         float var64 = Dp.recovered$95b3d103663bf060(var63 / other$ivx);
         int var72/* $VF was: $i$f$plus-5rwHm24x */ = 0;
         float var55 = Dp.recovered$95b3d103663bf060(var57 + var64);
         Modifier var58 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null)
            .then(var7 ? SizeKt.recovered$1c935d354e9159f0(Modifier.Companion, var55, 0.0F, 2, null) : Modifier.Companion);
         int var73 = 16;
         int var78 = 0;
         float var65 = Dp.recovered$95b3d103663bf060(var73);
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         float var74 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         float var80 = TABLE_HEADER_TOP_PADDING;
         int $this$dp$ivxx = 8;
         int $i$f$getDpxx = 0;
         float var82 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         Modifier var59 = PaddingKt.recovered$f39cb96e94c47cf6(var58, var65, var80, var74, var82);
         int $this$dp$ivxxx = 4;
         int $i$f$getDpxxx = 0;
         androidx.compose.foundation.layout.Arrangement.Horizontal var67 = Arrangement.INSTANCE
            .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
         androidx.compose.ui.Alignment.Vertical var76 = var7 ? Alignment.Companion.getTop() : Alignment.Companion.getCenterVertically();
         byte var81 = 48;
         int var83 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var54, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var84 = RowKt.rowMeasurePolicy(var67, var76, var54, 14 & var81 >> 3 | 112 & var81 >> 3);
         Modifier var85 = var59;
         MeasurePolicy var18 = var84;
         Composer var19 = var54;
         int var20 = 112 & var81 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var85);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var18, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var34 = 6 | 112 & var81 >> 6;
         Composer var35 = var32;
         RowScope var36 = RowScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 491776596, "C250@12021L6,248@11955L318,257@12312L6:TableBlock.kt#jxhu3x");
         long var38 = IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         int var40 = var7 ? Integer.MAX_VALUE : 1;
         int var41 = var7 ? TextOverflow.Companion.recovered$96c38bb84ea02354() : TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         Modifier var42 = RowScope.weight$default(var36, Modifier.Companion, 1.0F, false, 2, null);
         TextKt.recovered$5b114ab32a4f4190(
            var0, var42, var38, null, 0L, null, null, null, 0L, null, null, 0L, var41, false, var40, 0, null, var8, var35, 14 & var6, 0, 110584
         );
         long var86 = IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122();
         TableBlockMode var87 = var1;
         if (var87 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Inline) {
            var35.startReplaceGroup(492200985);
            ComposerKt.sourceInformation(var35, "*268@13223L121,276@13728L345,272@13425L648");
            Function0 var88 = ((io.intercom.android.sdk.survey.block.TableBlockMode$Inline)var1).getOnExpand();
            if (var88 == null) {
               Object var89 = null;
            } else {
               Function0 var43 = var88;
               int var44 = 0;
               var35.startReplaceGroup(-1937292943);
               ComposerKt.sourceInformation(var35, "CC(remember):TableBlock.kt#9igjgp");
               Composer var45 = var35;
               boolean var46 = false;
               int var47 = 0;
               Object var48 = var45.rememberedValue();
               int var49 = 0;
               Object var90;
               if (var48 == Composer.Companion.getEmpty()) {
                  int var50 = 0;
                  Object var51 = new MutableTransitionState(var3 ? false : var2);
                  var45.updateRememberedValue(var51);
                  var90 = var51;
               } else {
                  var90 = var48;
               }

               MutableTransitionState var52 = (MutableTransitionState)var90;
               var35.endReplaceGroup();
               MutableTransitionState var53 = var52;
               var53.setTargetState(var2);
               AnimatedVisibilityKt.AnimatedVisibility(
                  var36,
                  var53,
                  null,
                  EnterExitTransitionKt.fadeIn$default(AnimationSpecKt.tween$default(500, 0, EasingKt.getLinearEasing(), 2, null), 0.0F, 2, null),
                  EnterExitTransitionKt.fadeOut$default(AnimationSpecKt.tween$default(500, 0, EasingKt.getLinearEasing(), 2, null), 0.0F, 2, null),
                  null,
                  ComposableLambdaKt.rememberComposableLambda(-2123260731, true, new TableBlockKt$TableHeader$1$1$1(var86, var43, var9), var35, 54),
                  var35,
                  1572864 | 14 & var34 | MutableTransitionState.$stable << 3,
                  18
               );
            }

            var35.endReplaceGroup();
         } else {
            if (!(var87 instanceof io.intercom.android.sdk.survey.block.TableBlockMode$Expanded)) {
               var35.startReplaceGroup(2094085616);
               var35.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var35.startReplaceGroup(2094141189);
            ComposerKt.sourceInformation(var35, "287@14131L268");
            recovered$a3377ee0e3851243(drawable.intercom_ic_close, "Close expanded table", var86, ((io.intercom.android.sdk.survey.block.TableBlockMode$Expanded)var1).getOnClose(), var9, var35, 48);
            var35.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var54);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var91 = var54.endRestartGroup();
      if (var91 != null) {
         final java.lang.String recoveredExpressionCapture_1292_0 = var0;
         final io.intercom.android.sdk.survey.block.TableBlockMode recoveredExpressionCapture_1292_1 = var1;
         final boolean recoveredExpressionCapture_1292_2 = var2;
         final boolean recoveredExpressionCapture_1292_3 = var3;
         final int recoveredExpressionCapture_1292_4 = var5;
         var91.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1292_0, recoveredExpressionArgument_1292_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableHeader$lambda$13(recoveredExpressionCapture_1292_0, recoveredExpressionCapture_1292_1, recoveredExpressionCapture_1292_2, recoveredExpressionCapture_1292_3, recoveredExpressionCapture_1292_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1292_0), ((java.lang.Integer)recoveredExpressionArgument_1292_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$a3377ee0e3851243(int var0, String var1, long var2, Function0 var4, float var5, Composer var6, int var7) {
      Composer var48 = var6.startRestartGroup(-1323371292);
      ComposerKt.sourceInformation(var48, "C(TableHeaderAction)P(1!1,4:c#ui.graphics.Color,3,2:c#ui.unit.Dp)307@14687L629:TableBlock.kt#jxhu3x");
      int var8 = var7;
      if ((var7 & 6) == 0) {
         var8 |= var48.changed(var0) ? 4 : 2;
      }

      if ((var7 & 48) == 0) {
         var8 |= var48.changed(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var8 |= var48.changed(var2) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var8 |= var48.changedInstance(var4) ? 2048 : 1024;
      }

      if ((var7 & 24576) == 0) {
         var8 |= var48.changed(var5) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var48.getSkipping()) {
         var48.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1323371292, var8, -1, "io.intercom.android.sdk.survey.block.TableHeaderAction (TableBlock.kt:305)");
         }

         Modifier var9 = SizeKt.recovered$3f6c6a76aa292717(Modifier.Companion, var5);
         Alignment var10 = Alignment.Companion.getCenter();
         byte var12 = 48;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var48, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var11 = false;
         MeasurePolicy var14 = BoxKt.maybeCachedBoxMeasurePolicy(var10, var11);
         Modifier var15 = var9;
         MeasurePolicy var16 = var14;
         Composer var17 = var48;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         BoxScope var34 = BoxScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 1518445817, "C315@14960L111,319@15083L227,311@14803L507:TableBlock.kt#jxhu3x");
         Function0 var10000 = var4;
         Modifier var10001 = SizeKt.recovered$6d4b1248a51ec9fa(Modifier.Companion, HEADER_ICON_BUTTON_SIZE);
         boolean var10002 = false;
         var33.startReplaceGroup(-1059391906);
         ComposerKt.sourceInformation(var33, "CC(remember):TableBlock.kt#9igjgp");
         Composer var36 = var33;
         boolean var37 = (var8 & 112) == 32;
         int var38 = 0;
         Object var39 = var36.rememberedValue();
         int var40 = 0;
         Object var10003;
         if (!var37 && var39 != Composer.Companion.getEmpty()) {
            var10003 = var39;
         } else {
            boolean var41 = false;
            Modifier var42 = var10001;
            Function0 var43 = var4;
            int var44 = 0;
            final java.lang.String recoveredExpressionCapture_1295_0 = var1;
            Function1 var45 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1295_0) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableHeaderAction_u6trifg$lambda$16$lambda$15$lambda$14(recoveredExpressionCapture_1295_0, (androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1295_0)));
            var10000 = var43;
            var10001 = var42;
            var10002 = var41;
            Object var46 = var45;
            var36.updateRememberedValue(var46);
            var10003 = var46;
         }

         Function1 var47 = (Function1)var10003;
         var33.endReplaceGroup();
         IconButtonKt.IconButton(
            var10000,
            SemanticsModifierKt.semantics$default(var10001, var10002, var47, 1, null),
            false,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(297993664, true, new TableBlockKt$TableHeaderAction$1$2(var0, var2), var33, 54),
            var33,
            1572864 | 14 & var8 >> 9,
            60
         );
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var48);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var49 = var48.endRestartGroup();
      if (var49 != null) {
         final int recoveredExpressionCapture_1296_0 = var0;
         final java.lang.String recoveredExpressionCapture_1296_1 = var1;
         final long recoveredExpressionCapture_1296_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1296_3 = var4;
         final float recoveredExpressionCapture_1296_4 = var5;
         final int recoveredExpressionCapture_1296_5 = var7;
         var49.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1296_0, recoveredExpressionArgument_1296_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableHeaderAction_u6trifg$lambda$17(recoveredExpressionCapture_1296_0, recoveredExpressionCapture_1296_1, recoveredExpressionCapture_1296_2, recoveredExpressionCapture_1296_3, recoveredExpressionCapture_1296_4, recoveredExpressionCapture_1296_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1296_0), ((java.lang.Integer)recoveredExpressionArgument_1296_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TableBody(TableModel var0, boolean var1, Modifier var2, Composer var3, int var4, int var5) {
      Composer var8 = var3.startRestartGroup(1297045568);
      ComposerKt.sourceInformation(var8, "C(TableBody)P(1)341@16030L21,344@16162L519,342@16056L625:TableBlock.kt#jxhu3x");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var8.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var8.changed(var2) ? 256 : 128;
      }

      if ((var6 & 131) == 130 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1297045568, var6, -1, "io.intercom.android.sdk.survey.block.TableBody (TableBlock.kt:340)");
         }

         ScrollState var7 = ScrollKt.rememberScrollState(0, var8, 0, 1);
         BoxWithConstraintsKt.BoxWithConstraints(
            ScrollKt.verticalScroll$default(SizeKt.fillMaxWidth$default(var2, 0.0F, 1, null), var7, false, null, false, 14, null),
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(218029994, true, new TableBlockKt$TableBody$1(var0, var7), var8, 54),
            var8,
            3072,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var8.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.survey.block.TableModel recoveredExpressionCapture_1290_0 = var0;
         final boolean recoveredExpressionCapture_1290_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1290_2 = var2;
         final int recoveredExpressionCapture_1290_3 = var4;
         final int recoveredExpressionCapture_1290_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1290_0, recoveredExpressionArgument_1290_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableBody$lambda$18(recoveredExpressionCapture_1290_0, recoveredExpressionCapture_1290_1, recoveredExpressionCapture_1290_2, recoveredExpressionCapture_1290_3, recoveredExpressionCapture_1290_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1290_0), ((java.lang.Integer)recoveredExpressionArgument_1290_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void GeneratingTablePlaceholder(Modifier var0, Composer var1, int var2, int var3) {
      Composer var23 = var1.startRestartGroup(557213196);
      ComposerKt.sourceInformation(
         var23,
         "C(GeneratingTablePlaceholder)359@16790L60,360@16882L275,368@17192L6,369@17249L6,371@17294L50,373@17403L10,383@17810L467,370@17273L1011:TableBlock.kt#jxhu3x"
      );
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var23.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var23.getSkipping()) {
         var23.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(557213196, var4, -1, "io.intercom.android.sdk.survey.block.GeneratingTablePlaceholder (TableBlock.kt:358)");
         }

         InfiniteTransition var5 = InfiniteTransitionKt.rememberInfiniteTransition("TableGeneratingShimmer", var23, 6, 0);
         State var6 = InfiniteTransitionKt.animateFloat(
            var5,
            0.0F,
            1.0F,
            AnimationSpecKt.recovered$bec71f8ce21a903d(AnimationSpecKt.tween$default(1200, 0, EasingKt.getLinearEasing(), 2, null), null, 0L, 6, null),
            "TableGeneratingShimmerProgress",
            var23,
            25008 | InfiniteTransition.$stable | InfiniteRepeatableSpec.$stable << 9,
            0
         );
         long var7 = IntercomTheme.INSTANCE.getColors(var23, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         long var9 = IntercomTheme.INSTANCE.getColors(var23, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         String var11 = StringResources_androidKt.stringResource(string.intercom_generating_table, var23, 0);
         TextStyle var12 = IntercomTheme.INSTANCE.getTypography(var23, IntercomTheme.$stable).getType04Point5();
         Modifier var14 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         float var15 = TABLE_HORIZONTAL_PADDING;
         float var16 = TABLE_HORIZONTAL_PADDING;
         float var17 = TABLE_CELL_VERTICAL_PADDING;
         float var18 = TABLE_GENERATING_PLACEHOLDER_BOTTOM_PADDING;
         Modifier var10000 = GraphicsLayerModifierKt.recovered$848800fa81de7116(
            PaddingKt.recovered$f39cb96e94c47cf6(var14, var15, var17, var16, var18),
            0.0F,
            0.0F,
            0.99F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0L,
            null,
            false,
            null,
            0L,
            0L,
            0,
            0,
            null,
            524283,
            null
         );
         var23.startReplaceGroup(-402299157);
         ComposerKt.sourceInformation(var23, "CC(remember):TableBlock.kt#9igjgp");
         Composer var25 = var23;
         boolean var26 = var23.changed(var6) | var23.changed(var7) | var23.changed(var9);
         int var27 = 0;
         Object var28 = var25.rememberedValue();
         int var19 = 0;
         Object var30;
         if (!var26 && var28 != Composer.Companion.getEmpty()) {
            var30 = var28;
         } else {
            Modifier var22 = var10000;
            int var20 = 0;
            final long recoveredExpressionCapture_1284_0 = var7;
            final long recoveredExpressionCapture_1284_1 = var9;
            final androidx.compose.runtime.State recoveredExpressionCapture_1284_2 = var6;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1284_0) -> io.intercom.android.sdk.survey.block.TableBlockKt.GeneratingTablePlaceholder$lambda$21$lambda$20(recoveredExpressionCapture_1284_0, recoveredExpressionCapture_1284_1, recoveredExpressionCapture_1284_2, (androidx.compose.ui.graphics.drawscope.ContentDrawScope) ((androidx.compose.ui.graphics.drawscope.ContentDrawScope)recoveredExpressionArgument_1284_0)));
            var10000 = var22;
            Object var21 = var10001;
            var25.updateRememberedValue(var21);
            var30 = var21;
         }

         Function1 var24 = (Function1)var30;
         var23.endReplaceGroup();
         Modifier var13 = DrawModifierKt.drawWithContent(var10000, var24);
         TextKt.recovered$5b114ab32a4f4190(var11, var13, var7, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var12, var23, 0, 0, 131064);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var29 = var23.endRestartGroup();
      if (var29 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1285_0 = var0;
         final int recoveredExpressionCapture_1285_1 = var2;
         final int recoveredExpressionCapture_1285_2 = var3;
         var29.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1285_0, recoveredExpressionArgument_1285_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.GeneratingTablePlaceholder$lambda$22(recoveredExpressionCapture_1285_0, recoveredExpressionCapture_1285_1, recoveredExpressionCapture_1285_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1285_0), ((java.lang.Integer)recoveredExpressionArgument_1285_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TableGrid(TableModel var0, int var1, ScrollState var2, Modifier var3, Composer var4, int var5, int var6) {
      Composer var58 = var4.startRestartGroup(-2059684709);
      ComposerKt.sourceInformation(
         var58,
         "C(TableGrid)P(1!1,3)417@19218L53,421@19406L10,422@19466L6,423@19518L6,424@19575L6,425@19643L6,427@19702L7,454@20954L4280,435@20197L5037:TableBlock.kt#jxhu3x"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var58.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var58.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var58.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var58.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var58.getSkipping()) {
         var58.skipToGroupEnd();
      } else {
         if ((var6 & 8) != 0) {
            var3 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2059684709, var7, -1, "io.intercom.android.sdk.survey.block.TableGrid (TableBlock.kt:416)");
         }

         var58.startReplaceGroup(2044964040);
         ComposerKt.sourceInformation(var58, "CC(remember):TableBlock.kt#9igjgp");
         Composer var10 = var58;
         boolean var11 = var58.changed(var0);
         int var12 = 0;
         Object var13 = var10.rememberedValue();
         int var14 = 0;
         Object var10000;
         if (!var11 && var13 != Composer.Companion.getEmpty()) {
            var10000 = var13;
         } else {
            int var15 = 0;
            Object var16 = CollectionsKt.plus(CollectionsKt.listOf(var0.getHeader()), var0.getRows());
            var10.updateRememberedValue(var16);
            var10000 = var16;
         }

         List var9 = (List)var10000;
         var58.endReplaceGroup();
         List var8 = var9;
         int var59 = var0.getColumnCount();
         int var60 = RangesKt.coerceAtLeast(var8.size() - 1, 0);
         TextStyle var61 = IntercomTheme.INSTANCE.getTypography(var58, IntercomTheme.$stable).getType04Point5();
         long var62 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         long var63 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         long var64 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getBorder().recovered$e2b74d614aa09788();
         long var18 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getContainer().recovered$94e2a6def685ccbe();
         CompositionLocal var21 = CompositionLocalsKt.getLocalDensity();
         int var22 = 0;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var24 = var58.consume(var21);
         ComposerKt.sourceInformationMarkerEnd(var58);
         Density var20 = (Density)var24;
         Density var66 = var20;
         int var68 = 0;
         int var65 = var66.recovered$308ab30a2dba1f24(TABLE_MAX_COLUMN_WIDTH);
         Density var69 = var20;
         int var71 = 0;
         int var67 = var69.recovered$308ab30a2dba1f24(TABLE_COLUMN_SPACING);
         Density var72 = var20;
         int var25 = 0;
         int var70 = var72.recovered$308ab30a2dba1f24(TABLE_HORIZONTAL_PADDING);
         Density var74 = var20;
         int var26 = 0;
         int var73 = var74.recovered$308ab30a2dba1f24(TABLE_CELL_VERTICAL_PADDING);
         Density var76 = var20;
         int var27 = 0;
         int var75 = var76.recovered$308ab30a2dba1f24(TABLE_BOTTOM_PADDING);
         Density var78 = var20;
         int var28 = 0;
         int var77 = var78.recovered$308ab30a2dba1f24(TABLE_DIVIDER_HEIGHT);
         var58.startReplaceGroup(2045023819);
         ComposerKt.sourceInformation(var58, "CC(remember):TableBlock.kt#9igjgp");
         Composer var29 = var58;
         boolean var30 = var58.changedInstance(var8)
            | var58.changed(var59)
            | var58.changed(var60)
            | var58.changed(var65)
            | var58.changed(var73)
            | var58.changed(var67)
            | var58.changed(var70)
            | (var7 & 112) == 32
            | var58.changed(var75)
            | var58.changed(var77)
            | (var7 & 896) == 256;
         int $i$f$cachex = 0;
         Object it$ivx = var29.rememberedValue();
         int var33/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var91;
         if (!var30 && it$ivx != Composer.Companion.getEmpty()) {
            var91 = it$ivx;
         } else {
            int var34 = 0;
            Object var35 = new TableBlockKt$TableGrid$1$1(var8, var59, var60, var65, var67, var70, var1, var75, var73, var77, var2);
            var29.updateRememberedValue(var35);
            var91 = var35;
         }

         MeasurePolicy var80 = (MeasurePolicy)var91;
         var58.endReplaceGroup();
         MeasurePolicy var79 = var80;
         int var81 = 112 & var7 >> 6;
         int var82 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var83 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var58, 0));
         CompositionLocalMap var84 = var58.getCurrentCompositionLocalMap();
         Modifier var85 = ComposedModifierKt.materializeModifier(var58, var3);
         Function0 var86 = ComposeUiNode.Companion.getConstructor();
         Composer var87 = var58;
         int var88 = 6 | 896 & var81 << 6;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var87, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var87.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var87.startReusableNode();
         if (var87.getInserting()) {
            var87.createNode(var86);
         } else {
            var87.useNode();
         }

         Composer var37 = Updater.recovered$676aa17b44fd6280(var87);
         int var38 = 0;
         Updater.recovered$89682b199a142880(var37, var79, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var37, var84, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var37, var83, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var37, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var37, var85, ComposeUiNode.Companion.getSetModifier());
         int var39 = 14 & var88 >> 6;
         Composer var40 = var87;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(var40, -2012715437, "C452@20878L58:TableBlock.kt#jxhu3x");
         var40.startReplaceGroup(489262978);
         ComposerKt.sourceInformation(var40, "");
         Iterable var42 = var8;
         int var43 = 0;
         int var44 = 0;

         for (Object var46 : var42) {
            int var47 = var44++;
            if (var47 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            List var48 = (List)var46;
            int var49 = var47;
            int var50 = 0;
            boolean var51 = var49 == 0;
            var40.startReplaceGroup(489266126);
            ComposerKt.sourceInformation(var40, "*441@20414L196");
            Iterable var52 = var48;
            int var53 = 0;

            for (Object var55 : var52) {
               Cell var56 = (Cell)var55;
               int var57 = 0;
               recovered$23bc0d1361839f65(var56, var61, var51 ? var62 : var63, var40, 0);
            }

            var40.endReplaceGroup();
         }

         var40.endReplaceGroup();
         var40.startReplaceGroup(489274904);
         ComposerKt.sourceInformation(var40, "*449@20694L49");

         for (int var89 = 0; var89 < var60; var89++) {
            int var90 = 0;
            BoxKt.Box(BackgroundKt.recovered$42a3174180b3fb7a(Modifier.Companion, var64, null, 2, null), var40, 0);
         }

         var40.endReplaceGroup();
         BoxKt.Box(BackgroundKt.recovered$42a3174180b3fb7a(Modifier.Companion, var18, null, 2, null), var40, 0);
         ComposerKt.sourceInformationMarkerEnd(var40);
         var87.endNode();
         ComposerKt.sourceInformationMarkerEnd(var87);
         ComposerKt.sourceInformationMarkerEnd(var58);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var93 = var58.endRestartGroup();
      if (var93 != null) {
         final io.intercom.android.sdk.survey.block.TableModel recoveredExpressionCapture_1291_0 = var0;
         final int recoveredExpressionCapture_1291_1 = var1;
         final androidx.compose.foundation.ScrollState recoveredExpressionCapture_1291_2 = var2;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1291_3 = var3;
         final int recoveredExpressionCapture_1291_4 = var5;
         final int recoveredExpressionCapture_1291_5 = var6;
         var93.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1291_0, recoveredExpressionArgument_1291_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableGrid$lambda$35(recoveredExpressionCapture_1291_0, recoveredExpressionCapture_1291_1, recoveredExpressionCapture_1291_2, recoveredExpressionCapture_1291_3, recoveredExpressionCapture_1291_4, recoveredExpressionCapture_1291_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1291_0), ((java.lang.Integer)recoveredExpressionArgument_1291_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$23bc0d1361839f65(Cell var0, TextStyle var1, long var2, Composer var4, int var5) {
      Composer var7 = var4.startRestartGroup(-1275445468);
      ComposerKt.sourceInformation(var7, "C(TableCellContent)P(!1,2,1:c#ui.graphics.Color)550@25329L83:TableBlock.kt#jxhu3x");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var7.changed(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var7.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1275445468, var6, -1, "io.intercom.android.sdk.survey.block.TableCellContent (TableBlock.kt:549)");
         }

         TextKt.recovered$5b114ab32a4f4190(
            var0.getText(),
            null,
            var2,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            var1,
            var7,
            896 & var6,
            29360128 & var6 << 18,
            131066
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var7.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.survey.block.Cell recoveredExpressionCapture_1294_0 = var0;
         final androidx.compose.ui.text.TextStyle recoveredExpressionCapture_1294_1 = var1;
         final long recoveredExpressionCapture_1294_2 = var2;
         final int recoveredExpressionCapture_1294_3 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1294_0, recoveredExpressionArgument_1294_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableCellContent_XO_JAsU$lambda$36(recoveredExpressionCapture_1294_0, recoveredExpressionCapture_1294_1, recoveredExpressionCapture_1294_2, recoveredExpressionCapture_1294_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1294_0), ((java.lang.Integer)recoveredExpressionArgument_1294_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedTableBlockDialog(@NotNull Block var0, @NotNull Function0 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "block");
      Intrinsics.checkNotNullParameter(var1, "onDismiss");
      Composer var5 = var2.startRestartGroup(986146247);
      ComposerKt.sourceInformation(var5, "C(ExpandedTableBlockDialog)570@25888L305,567@25764L429:TableBlock.kt#jxhu3x");
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
            ComposerKt.traceEventStart(986146247, var4, -1, "io.intercom.android.sdk.survey.block.ExpandedTableBlockDialog (TableBlock.kt:566)");
         }

         AndroidDialog_androidKt.Dialog(
            var1,
            new DialogProperties(false, false, false, 3, null),
            ComposableLambdaKt.rememberComposableLambda(399575646, true, new TableBlockKt$ExpandedTableBlockDialog$1(var1, var0), var5, 54),
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
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_1281_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1281_1 = var1;
         final int recoveredExpressionCapture_1281_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1281_0, recoveredExpressionArgument_1281_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.ExpandedTableBlockDialog$lambda$37(recoveredExpressionCapture_1281_0, recoveredExpressionCapture_1281_1, recoveredExpressionCapture_1281_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1281_0), ((java.lang.Integer)recoveredExpressionArgument_1281_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TableBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1822360807);
      ComposerKt.sourceInformation(var2, "C(TableBlockPreview)585@26266L192:TableBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1822360807, var1, -1, "io.intercom.android.sdk.survey.block.TableBlockPreview (TableBlock.kt:584)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.INSTANCE.recovered$1400af5e34386102(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1289_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1289_0, recoveredExpressionArgument_1289_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TableBlockPreview$lambda$38(recoveredExpressionCapture_1289_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1289_0), ((java.lang.Integer)recoveredExpressionArgument_1289_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TallTableBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1767359444);
      ComposerKt.sourceInformation(var2, "C(TallTableBlockPreview)600@26732L196:TableBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1767359444, var1, -1, "io.intercom.android.sdk.survey.block.TallTableBlockPreview (TableBlock.kt:599)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.INSTANCE.recovered$54bdcf11cafa5012(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1293_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1293_0, recoveredExpressionArgument_1293_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.TallTableBlockPreview$lambda$39(recoveredExpressionCapture_1293_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1293_0), ((java.lang.Integer)recoveredExpressionArgument_1293_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedTableBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-889776224);
      ComposerKt.sourceInformation(var2, "C(ExpandedTableBlockPreview)613@27009L244:TableBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-889776224, var1, -1, "io.intercom.android.sdk.survey.block.ExpandedTableBlockPreview (TableBlock.kt:612)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.INSTANCE.recovered$5a1850a8ff371bfb(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1282_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1282_0, recoveredExpressionArgument_1282_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.ExpandedTableBlockPreview$lambda$40(recoveredExpressionCapture_1282_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1282_0), ((java.lang.Integer)recoveredExpressionArgument_1282_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GeneratingTableBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1151614393);
      ComposerKt.sourceInformation(var2, "C(GeneratingTableBlockPreview)627@27336L232:TableBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1151614393, var1, -1, "io.intercom.android.sdk.survey.block.GeneratingTableBlockPreview (TableBlock.kt:626)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.INSTANCE.recovered$a427d7fccaa25a17(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1283_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1283_0, recoveredExpressionArgument_1283_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.GeneratingTableBlockPreview$lambda$41(recoveredExpressionCapture_1283_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1283_0), ((java.lang.Integer)recoveredExpressionArgument_1283_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CaptionlessTableBlockPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-532295754);
      ComposerKt.sourceInformation(var2, "C(CaptionlessTableBlockPreview)642@27755L203:TableBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-532295754, var1, -1, "io.intercom.android.sdk.survey.block.CaptionlessTableBlockPreview (TableBlock.kt:641)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$TableBlockKt.INSTANCE.recovered$abc66e26750555d7(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1280_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1280_0, recoveredExpressionArgument_1280_1) -> io.intercom.android.sdk.survey.block.TableBlockKt.CaptionlessTableBlockPreview$lambda$42(recoveredExpressionCapture_1280_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1280_0), ((java.lang.Integer)recoveredExpressionArgument_1280_1).intValue())));
      }
   }

   private static final Block sampleTableBlock() {
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
         .withType(BlockType.TABLE.getSerializedName())
         .withCaption("Miyazaki feature films ranked by worldwide IMDb rating and critical acclaim");
      io.intercom.android.sdk.blocks.lib.models.TableRow$Builder[] var0 = new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder[4];
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var1 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         sampleTableBlock$cell("Title"), sampleTableBlock$cell("Year"), sampleTableBlock$cell("IMDb rating"), sampleTableBlock$cell("Notes")
      };
      var0[0] = sampleTableBlock$row(var1);
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var2 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         sampleTableBlock$cell("Spirited Away"),
         sampleTableBlock$cell("2001"),
         sampleTableBlock$cell("8.6"),
         sampleTableBlock$cell("Often his most acclaimed film worldwide; Oscar winner.")
      };
      var0[1] = sampleTableBlock$row(var2);
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var3 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         sampleTableBlock$cell("Princess Mononoke"),
         sampleTableBlock$cell("1997"),
         sampleTableBlock$cell("8.3"),
         sampleTableBlock$cell("Darker, more violent fantasy epic; consistently near the top.")
      };
      var0[2] = sampleTableBlock$row(var3);
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var4 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         sampleTableBlock$cell("My Neighbor Totoro"),
         sampleTableBlock$cell("1988"),
         sampleTableBlock$cell("8.1"),
         sampleTableBlock$cell("Gentle childhood fantasy that became Ghibli's mascot.")
      };
      var0[3] = sampleTableBlock$row(var4);
      Block var5 = var10000.withRows(CollectionsKt.listOf(var0)).build();
      Intrinsics.checkNotNullExpressionValue(var5, "build(...)");
      return var5;
   }

   private static final Block captionlessSampleTableBlock() {
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.TABLE.getSerializedName());
      io.intercom.android.sdk.blocks.lib.models.TableRow$Builder[] var0 = new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder[2];
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var1 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         captionlessSampleTableBlock$cell$43("Title"), captionlessSampleTableBlock$cell$43("Year"), captionlessSampleTableBlock$cell$43("IMDb rating")
      };
      var0[0] = captionlessSampleTableBlock$row$44(var1);
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var2 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         captionlessSampleTableBlock$cell$43("Spirited Away"), captionlessSampleTableBlock$cell$43("2001"), captionlessSampleTableBlock$cell$43("8.6")
      };
      var0[1] = captionlessSampleTableBlock$row$44(var2);
      Block var3 = var10000.withRows(CollectionsKt.listOf(var0)).build();
      Intrinsics.checkNotNullExpressionValue(var3, "build(...)");
      return var3;
   }

   private static final Block tallSampleTableBlock() {
      List[] var1 = new List[10];
      String[] var2 = new String[]{"Spirited Away", "2001", "8.6", "Often his most acclaimed film worldwide; Oscar winner."};
      var1[0] = CollectionsKt.listOf(var2);
      String[] var19 = new String[]{"Princess Mononoke", "1997", "8.3", "Darker, more violent fantasy epic."};
      var1[1] = CollectionsKt.listOf(var19);
      String[] var20 = new String[]{"My Neighbor Totoro", "1988", "8.1", "Gentle childhood fantasy; Ghibli's mascot."};
      var1[2] = CollectionsKt.listOf(var20);
      String[] var21 = new String[]{"Howl's Moving Castle", "2004", "8.2", "Wartime fantasy adapted from Diana Wynne Jones."};
      var1[3] = CollectionsKt.listOf(var21);
      String[] var22 = new String[]{"Castle in the Sky", "1986", "8.0", "The first official Studio Ghibli feature."};
      var1[4] = CollectionsKt.listOf(var22);
      String[] var23 = new String[]{"Kiki's Delivery Service", "1989", "7.8", "Coming-of-age tale of a young witch."};
      var1[5] = CollectionsKt.listOf(var23);
      String[] var24 = new String[]{"Porco Rosso", "1992", "7.7", "A seaplane-flying pilot cursed with a pig's face."};
      var1[6] = CollectionsKt.listOf(var24);
      String[] var25 = new String[]{"The Wind Rises", "2013", "7.7", "Biographical drama about an aircraft designer."};
      var1[7] = CollectionsKt.listOf(var25);
      String[] var26 = new String[]{"Ponyo", "2008", "7.6", "A goldfish princess who longs to become human."};
      var1[8] = CollectionsKt.listOf(var26);
      String[] var27 = new String[]{"The Boy and the Heron", "2023", "7.5", "Late-career fantasy; won the Oscar for Best Animated Feature."};
      var1[9] = CollectionsKt.listOf(var27);
      List var0 = CollectionsKt.listOf(var1);
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
         .withType(BlockType.TABLE.getSerializedName())
         .withCaption("Miyazaki feature films ranked by worldwide IMDb rating and critical acclaim");
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var28 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         tallSampleTableBlock$cell$45("Title"),
         tallSampleTableBlock$cell$45("Year"),
         tallSampleTableBlock$cell$45("IMDb rating"),
         tallSampleTableBlock$cell$45("Notes")
      };
      Collection var10001 = CollectionsKt.listOf(tallSampleTableBlock$row$46(var28));
      Iterable var29 = var0;
      Collection var17 = var10001;
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var16 = var10000;
      int var3 = 0;
      Iterable var4 = var29;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var29, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         List var9 = (List)var8;
         Collection var18 = var5;
         int var10 = 0;
         String var11 = (String)var9.get(0);
         String var12 = (String)var9.get(1);
         String var13 = (String)var9.get(2);
         String var14 = (String)var9.get(3);
         io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var15 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
            tallSampleTableBlock$cell$45(var11), tallSampleTableBlock$cell$45(var12), tallSampleTableBlock$cell$45(var13), tallSampleTableBlock$cell$45(var14)
         };
         var18.add(tallSampleTableBlock$row$46(var15));
      }

      List var30 = (List)var5;
      Block var31 = var16.withRows(CollectionsKt.plus(var17, var30)).build();
      Intrinsics.checkNotNullExpressionValue(var31, "build(...)");
      return var31;
   }

   private static final Block headerOnlyTableBlock() {
      io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
         .withType(BlockType.TABLE.getSerializedName())
         .withCaption("Miyazaki feature films ranked by worldwide IMDb rating and critical acclaim");
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[] var0 = new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder[]{
         headerOnlyTableBlock$cell$48("Title"),
         headerOnlyTableBlock$cell$48("Year"),
         headerOnlyTableBlock$cell$48("IMDb rating"),
         headerOnlyTableBlock$cell$48("Notes")
      };
      Block var1 = var10000.withRows(CollectionsKt.listOf(headerOnlyTableBlock$row$49(var0))).build();
      Intrinsics.checkNotNullExpressionValue(var1, "build(...)");
      return var1;
   }

   private static final Unit TableBlock$lambda$1(
      Block var0, Modifier var1, TableBlockMode var2, Shape var3, boolean var4, int var5, int var6, Composer var7, int var8
   ) {
      TableBlock(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit TableBlock$lambda$7$lambda$6$lambda$5(Animatable var0, GraphicsLayerScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$graphicsLayer");
      var1.setAlpha(((Number)var0.getValue()).floatValue());
      return Unit.INSTANCE;
   }

   private static final Unit TableBlock$lambda$8(
      Block var0, Modifier var1, TableBlockMode var2, Shape var3, boolean var4, int var5, int var6, Composer var7, int var8
   ) {
      TableBlock(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit TableHeader$lambda$13(String var0, TableBlockMode var1, boolean var2, boolean var3, int var4, Composer var5, int var6) {
      TableHeader(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TableHeaderAction_u6trifg$lambda$16$lambda$15$lambda$14(String var0, SemanticsPropertyReceiver var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var1, var0);
      SemanticsPropertiesKt.recovered$fed06fdc960a8485(var1, Role.Companion.recovered$c675f3db0d521c2c());
      return Unit.INSTANCE;
   }

   private static final Unit TableHeaderAction_u6trifg$lambda$17(
      int var0, String var1, long var2, Function0 var4, float var5, int var6, Composer var7, int var8
   ) {
      recovered$a3377ee0e3851243(var0, var1, var2, var4, var5, var7, RecomposeScopeImplKt.updateChangedFlags(var6 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TableBody$lambda$18(TableModel var0, boolean var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      TableBody(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final float GeneratingTablePlaceholder$lambda$19(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).floatValue();
   }

   private static final Unit GeneratingTablePlaceholder$lambda$21$lambda$20(long var0, long var2, State var4, ContentDrawScope var5) {
      Intrinsics.checkNotNullParameter(var5, "$this$drawWithContent");
      var5.drawContent();
      float var10000 = GeneratingTablePlaceholder$lambda$19(var4) * 2.0F - 1.0F;
      long var7 = var5.recovered$d1ee80c43f6fa69a();
      int var9 = 0;
      long var10 = var7;
      int var12 = 0;
      int var13 = (int)(var10 >> 32);
      int var14 = 0;
      float var6 = var10000 * Float.intBitsToFloat(var13);
      DrawScope var36 = var5;
      Companion var10001 = Brush.Companion;
      Color[] var17 = new Color[]{Color.recovered$f46ef195ad4a2363(var0), Color.recovered$f46ef195ad4a2363(var2), Color.recovered$f46ef195ad4a2363(var0)};
      List var10002 = CollectionsKt.listOf(var17);
      float var18 = 0.0F;
      int var8 = 0;
      float var22 = var6;
      float var25 = var18;
      int var11 = 0;
      long var29 = Float.floatToRawIntBits(var22);
      long var34 = Float.floatToRawIntBits(var25);
      long var10003 = Offset.recovered$8c837f3e34208708(var29 << 32 | var34 & 4294967295L);
      long arg0$ivx = var5.recovered$d1ee80c43f6fa69a();
      int var23/* $VF was: $i$f$getWidth-implx */ = 0;
      long value$iv$ivx = arg0$ivx;
      int $i$f$unpackFloat1x = 0;
      int bits$iv$iv$ivx = (int)(value$iv$ivx >> 32);
      int $i$f$floatFromBitsx = 0;
      float var20 = var6 + Float.intBitsToFloat(bits$iv$iv$ivx);
      float y$ivx = 0.0F;
      int $i$f$Offsetx = 0;
      float var27 = var20;
      float val2$iv$ivx = y$ivx;
      int $i$f$packFloatsx = 0;
      long v1$iv$ivx = Float.floatToRawIntBits(var27);
      long v2$iv$ivx = Float.floatToRawIntBits(val2$iv$ivx);
      DrawScope.recovered$9f2640ea279134d8(
         var36,
         Companion.recovered$363f709e34bf7346(
            var10001, var10002, var10003, Offset.recovered$8c837f3e34208708(v1$iv$ivx << 32 | v2$iv$ivx & 4294967295L), 0, 8, null
         ),
         0L,
         0L,
         0.0F,
         null,
         null,
         BlendMode.Companion.recovered$b3ccc12aaa794328(),
         62,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit GeneratingTablePlaceholder$lambda$22(Modifier var0, int var1, int var2, Composer var3, int var4) {
      GeneratingTablePlaceholder(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit TableGrid$lambda$35(TableModel var0, int var1, ScrollState var2, Modifier var3, int var4, int var5, Composer var6, int var7) {
      TableGrid(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit TableCellContent_XO_JAsU$lambda$36(Cell var0, TextStyle var1, long var2, int var4, Composer var5, int var6) {
      recovered$23bc0d1361839f65(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTableBlockDialog$lambda$37(Block var0, Function0 var1, int var2, Composer var3, int var4) {
      ExpandedTableBlockDialog(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TableBlockPreview$lambda$38(int var0, Composer var1, int var2) {
      TableBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TallTableBlockPreview$lambda$39(int var0, Composer var1, int var2) {
      TallTableBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTableBlockPreview$lambda$40(int var0, Composer var1, int var2) {
      ExpandedTableBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GeneratingTableBlockPreview$lambda$41(int var0, Composer var1, int var2) {
      GeneratingTableBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CaptionlessTableBlockPreview$lambda$42(int var0, Composer var1, int var2) {
      CaptionlessTableBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableCell$Builder sampleTableBlock$cell(String var0) {
      return new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder()
         .withContent(CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableRow$Builder sampleTableBlock$row(
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder... var0
   ) {
      return new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableCell$Builder captionlessSampleTableBlock$cell$43(String var0) {
      return new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder()
         .withContent(CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableRow$Builder captionlessSampleTableBlock$row$44(
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder... var0
   ) {
      return new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableCell$Builder tallSampleTableBlock$cell$45(String var0) {
      return new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder()
         .withContent(CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableRow$Builder tallSampleTableBlock$row$46(
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder... var0
   ) {
      return new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableCell$Builder headerOnlyTableBlock$cell$48(String var0) {
      return new io.intercom.android.sdk.blocks.lib.models.TableCell$Builder()
         .withContent(CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withText(var0).withType(BlockType.PARAGRAPH.getSerializedName())));
   }

   private static final io.intercom.android.sdk.blocks.lib.models.TableRow$Builder headerOnlyTableBlock$row$49(
      io.intercom.android.sdk.blocks.lib.models.TableCell$Builder... var0
   ) {
      return new io.intercom.android.sdk.blocks.lib.models.TableRow$Builder().withCells(ArraysKt.toList(var0));
   }

   // $VF: synthetic method
   public static final Block access$sampleTableBlock() {
      return sampleTableBlock();
   }

   // $VF: synthetic method
   public static final Block access$tallSampleTableBlock() {
      return tallSampleTableBlock();
   }

   // $VF: synthetic method
   public static final Block access$headerOnlyTableBlock() {
      return headerOnlyTableBlock();
   }

   // $VF: synthetic method
   public static final Block access$captionlessSampleTableBlock() {
      return captionlessSampleTableBlock();
   }

   // $VF: synthetic method
   public static final void access$TableGrid(TableModel var0, int var1, ScrollState var2, Modifier var3, Composer var4, int var5, int var6) {
      TableGrid(var0, var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static final float access$getEXPANDED_DIALOG_MARGIN$p() {
      return EXPANDED_DIALOG_MARGIN;
   }

   // $VF: synthetic method
   public static final void recovered$1d9343dec5b3a765(int var0, String var1, long var2, Function0 var4, float var5, Composer var6, int var7) {
      recovered$a3377ee0e3851243(var0, var1, var2, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static final float access$getHEADER_ICON_SIZE$p() {
      return HEADER_ICON_SIZE;
   }

   static {
      int var0 = 370;
      int var1 = 0;
      TABLE_MAX_HEIGHT = Dp.recovered$95b3d103663bf060(var0);
      int $this$dp$ivx = 8;
      int $i$f$getDpx = 0;
      TABLE_CORNER_RADIUS = Dp.recovered$95b3d103663bf060($this$dp$ivx);
      int $this$dp$ivxx = 12;
      int $i$f$getDpxx = 0;
      TABLE_EXPANDED_CORNER_RADIUS = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
      int $this$dp$ivxxx = 220;
      int $i$f$getDpxxx = 0;
      TABLE_MAX_COLUMN_WIDTH = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
      int $this$dp$ivxxxx = 16;
      int $i$f$getDpxxxx = 0;
      TABLE_COLUMN_SPACING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxx);
      int $this$dp$ivxxxxx = 8;
      int $i$f$getDpxxxxx = 0;
      TABLE_CELL_VERTICAL_PADDING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx);
      int $this$dp$ivxxxxxx = 16;
      int $i$f$getDpxxxxxx = 0;
      TABLE_HORIZONTAL_PADDING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx);
      int $this$dp$ivxxxxxxx = 8;
      int $i$f$getDpxxxxxxx = 0;
      TABLE_BOTTOM_PADDING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxx);
      int $this$dp$ivxxxxxxxx = 1;
      int $i$f$getDpxxxxxxxx = 0;
      TABLE_DIVIDER_HEIGHT = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxxx);
      int $this$dp$ivxxxxxxxxx = 20;
      int $i$f$getDpxxxxxxxxx = 0;
      HEADER_ICON_SIZE = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxxxx);
      int $this$dp$ivxxxxxxxxxx = 40;
      int $i$f$getDpxxxxxxxxxx = 0;
      HEADER_ICON_BUTTON_SIZE = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxxxxx);
      int $this$dp$ivxxxxxxxxxxx = 16;
      int $i$f$getDpxxxxxxxxxxx = 0;
      TABLE_HEADER_TOP_PADDING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxxxxxx);
      int $this$dp$ivxxxxxxxxxxxx = 16;
      int $i$f$getDpxxxxxxxxxxxx = 0;
      EXPANDED_DIALOG_MARGIN = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxxxxxxx);
      int $this$dp$ivxxxxxxxxxxxxx = 16;
      int $i$f$getDpxxxxxxxxxxxxx = 0;
      TABLE_GENERATING_PLACEHOLDER_BOTTOM_PADDING = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxxxxxxxx);
   }
}
