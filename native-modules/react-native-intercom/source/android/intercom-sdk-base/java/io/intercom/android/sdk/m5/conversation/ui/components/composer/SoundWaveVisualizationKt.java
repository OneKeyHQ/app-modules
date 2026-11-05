package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.CanvasKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.FloatState;
import androidx.compose.runtime.LongState;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableLongState;
import androidx.compose.runtime.PrimitiveSnapshotStateKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotLongStateKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.saveable.ListSaverKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.runtime.saveable.Saver;
import androidx.compose.runtime.saveable.SaverScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.geometry.CornerRadius;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.geometry.Size;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
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
   d1 = "\u0000`\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0003\u001a\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0000\u001a\u0010\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002\u001a1\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0001¢\u0006\u0004\b\u0013\u0010\u0014\u001aI\u0010\u0015\u001a\u00020\f*\u00020\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u001eH\u0002¢\u0006\u0004\b\u001f\u0010 \u001a\r\u0010!\u001a\u00020\fH\u0003¢\u0006\u0002\u0010\"\u001a\r\u0010#\u001a\u00020\fH\u0003¢\u0006\u0002\u0010\"\u001a\r\u0010$\u001a\u00020\fH\u0003¢\u0006\u0002\u0010\"\"\u001a\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006%²\u0006\n\u0010&\u001a\u00020\u0007X\u008a\u0084\u0002²\u0006\n\u0010\u0006\u001a\u00020\u0007X\u008a\u008e\u0002²\u0006\n\u0010'\u001a\u00020(X\u008a\u008e\u0002²\u0006\n\u0010)\u001a\u00020(X\u008a\u008e\u0002²\u0006\n\u0010*\u001a\u00020\u0007X\u008a\u008e\u0002²\u0006\n\u0010+\u001a\u00020\u0007X\u008a\u008e\u0002",
   d2 = {
         "WaveformBufferSaver",
         "Landroidx/compose/runtime/saveable/Saver;",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformBuffer;",
         "",
         "calculateOptimalBufferSize",
         "",
         "canvasWidth",
         "",
         "elementSpacingPx",
         "scaleAmplitudeForVisualization",
         "rawAmplitude",
         "SoundWaveVisualization",
         "",
         "amplitudeLevel",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "SoundWaveVisualization-FNF3uiM",
         "(Lkotlinx/coroutines/flow/StateFlow;Landroidx/compose/ui/Modifier;JLandroidx/compose/runtime/Composer;II)V",
         "drawWaveformElements",
         "Landroidx/compose/ui/graphics/drawscope/DrawScope;",
         "elements",
         "",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformElement;",
         "constants",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/DrawingConstants;",
         "canvasHeight",
         "layoutDirection",
         "Landroidx/compose/ui/unit/LayoutDirection;",
         "drawWaveformElements-cf5BqRc",
         "(Landroidx/compose/ui/graphics/drawscope/DrawScope;Ljava/util/List;JLio/intercom/android/sdk/m5/conversation/ui/components/composer/DrawingConstants;FFLandroidx/compose/ui/unit/LayoutDirection;)V",
         "SoundWaveVisualizationPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SoundWaveVisualizationLowAmplitudePreview",
         "SoundWaveVisualizationRtlPreview",
         "intercom-sdk-base_release",
         "amplitude",
         "lastElementGenerationTime",
         "",
         "animationFrameCounter",
         "smoothedAmplitude",
         "lastRawAmplitude"
   }
)
@SourceDebugExtension(
   "SMAP\nSoundWaveVisualization.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/SoundWaveVisualizationKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 CornerRadius.kt\nandroidx/compose/ui/geometry/CornerRadiusKt\n+ 8 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 9 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n+ 10 Size.kt\nandroidx/compose/ui/geometry/SizeKt\n+ 11 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 12 SnapshotFloatState.kt\nandroidx/compose/runtime/PrimitiveSnapshotStateKt__SnapshotFloatStateKt\n+ 13 SnapshotLongState.kt\nandroidx/compose/runtime/SnapshotLongStateKt__SnapshotLongStateKt\n+ 14 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 15 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n*L\n1#1,591:1\n75#2:592\n75#2:594\n118#3:593\n118#3:599\n118#3:600\n118#3:601\n118#3:673\n1#4:595\n1047#5,3:596\n1050#5,3:602\n1047#5,6:605\n1047#5,6:611\n1047#5,6:617\n1047#5,6:623\n1047#5,6:629\n1047#5,6:635\n1047#5,6:641\n1047#5,6:647\n1047#5,6:653\n1047#5,6:659\n1047#5,3:665\n1050#5,3:670\n1047#5,6:674\n1869#6,2:668\n1869#6:684\n1870#6:697\n33#7:680\n53#8,3:681\n53#8,3:686\n53#8,3:690\n53#8,3:694\n60#8:715\n60#8:718\n70#8:721\n30#9:685\n30#9:689\n33#10:693\n85#11:698\n80#12:699\n113#12,2:700\n80#12:708\n113#12,2:709\n80#12:711\n113#12,2:712\n79#13:702\n112#13,2:703\n79#13:705\n112#13,2:706\n57#14:714\n57#14:717\n61#14:720\n23#15:716\n23#15:719\n23#15:722\n*S KotlinDebug\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/SoundWaveVisualizationKt\n*L\n321#1:592\n329#1:594\n328#1:593\n338#1:599\n339#1:600\n340#1:601\n462#1:673\n336#1:596,3\n336#1:602,3\n348#1:605,6\n351#1:611,6\n352#1:617,6\n353#1:623,6\n356#1:629,6\n367#1:635,6\n368#1:641,6\n371#1:647,6\n387#1:653,6\n414#1:659,6\n432#1:665,3\n432#1:670,3\n463#1:674,6\n438#1:668,2\n509#1:684\n509#1:697\n507#1:680\n507#1:681,3\n528#1:686,3\n540#1:690,3\n541#1:694,3\n465#1:715\n471#1:718\n472#1:721\n528#1:685\n540#1:689\n541#1:693\n320#1:698\n351#1:699\n351#1:700,2\n367#1:708\n367#1:709,2\n368#1:711\n368#1:712,2\n352#1:702\n352#1:703,2\n353#1:705\n353#1:706,2\n465#1:714\n471#1:717\n472#1:720\n465#1:716\n471#1:719\n472#1:722\n*E\n"
)
public final class SoundWaveVisualizationKt {
   @NotNull
   private static final Saver WaveformBufferSaver = ListSaverKt.listSaver(
      ((kotlin.jvm.functions.Function2) (recoveredArgument_638_0, recoveredArgument_638_1) -> SoundWaveVisualizationKt.WaveformBufferSaver$lambda$0((androidx.compose.runtime.saveable.SaverScope)recoveredArgument_638_0, (io.intercom.android.sdk.m5.conversation.ui.components.composer.WaveformBuffer)recoveredArgument_638_1)), ((kotlin.jvm.functions.Function1) (recoveredArgument_639_0) -> SoundWaveVisualizationKt.WaveformBufferSaver$lambda$2((java.util.List)recoveredArgument_639_0))
   );

   public static final int calculateOptimalBufferSize(float var0, float var1) {
      if (!(var0 <= 0.0F) && !(var1 <= 0.0F)) {
         int var2 = (int)(var0 / var1);
         float var3 = 1.5F;
         int var4 = (int)(var2 * var3);
         return RangesKt.coerceIn(var4, 50, 200);
      } else {
         return 70;
      }
   }

   private static final float scaleAmplitudeForVisualization(float var0) {
      float var1 = var0 * var0;
      float var2 = var1 / (var1 + 0.2F);
      return Math.min(1.0F, var2 * 1.6F);
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$92b4091f81df4daa(@NotNull StateFlow var0, @Nullable Modifier var1, long var2, @Nullable Composer var4, int var5, int var6) {
      Intrinsics.checkNotNullParameter(var0, "amplitudeLevel");
      Composer var52 = var4.startRestartGroup(2042015142);
      ComposerKt.sourceInformation(
         var52,
         "C(SoundWaveVisualization)P(!1,2,1:c#ui.graphics.Color)317@12553L6,319@12612L16,320@12676L7,*328@13132L7,335@13527L364,347@14029L20,347@13983L66,350@14116L36,351@14190L35,352@14259L35,355@14412L282,355@14384L310,366@14758L36,367@14823L36,370@14959L735,370@14933L761,386@15833L1033,386@15812L1054,413@16997L635,413@16976L656,431@17732L1145,462@18981L366,458@18883L464:SoundWaveVisualization.kt#yb6g9"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var52.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var52.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var52.changed(var2) ? 256 : 128;
      }

      if ((var7 & 147) == 146 && var52.getSkipping()) {
         var52.skipToGroupEnd();
      } else {
         var52.startDefaults();
         if ((var5 & 1) != 0 && !var52.getDefaultsInvalid()) {
            var52.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = IntercomTheme.INSTANCE.getColors(var52, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
               var7 &= -897;
            }
         }

         var52.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2042015142, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualization (SoundWaveVisualization.kt:318)"
            );
         }

         State var8 = SnapshotStateKt.collectAsState(var0, null, var52, 14 & var7, 1);
         CompositionLocal var10 = CompositionLocalsKt.getLocalLayoutDirection();
         int var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var52, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var13 = var52.consume(var10);
         ComposerKt.sourceInformationMarkerEnd(var52);
         LayoutDirection var9 = (LayoutDirection)var13;
         long var53 = 150L;
         long var54 = 16L;
         int var15 = 4;
         int var16 = 0;
         float var14 = Dp.recovered$95b3d103663bf060(var15);
         CompositionLocal var56 = CompositionLocalsKt.getLocalDensity();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var52, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var19 = var52.consume(var56);
         ComposerKt.sourceInformationMarkerEnd(var52);
         Density var58 = (Density)var19;
         int var60 = 0;
         float var55 = var58.recovered$245d7c913f591981(var14);
         float var57 = var55 / (float)var53;
         var52.startReplaceGroup(-1478331329);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var63 = var52;
         boolean var20 = false;
         int var21 = 0;
         Object var22 = var63.rememberedValue();
         int var23 = 0;
         Object var10000;
         if (var22 == Composer.Companion.getEmpty()) {
            int var24 = 0;
            int $this$dp$ivx = 2;
            int $i$f$getDpx = 0;
            float var10002 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            int $this$dp$ivxx = 1;
            int $i$f$getDpxx = 0;
            float var10003 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
            int $this$dp$ivxxx = 3;
            int $i$f$getDpxxx = 0;
            Object var86 = new DrawingConstants(var10002, var10003, Dp.recovered$95b3d103663bf060($this$dp$ivxxx), 0.05F, 0.02F, 0.3F, null);
            var63.updateRememberedValue(var86);
            var10000 = var86;
         } else {
            var10000 = var22;
         }

         DrawingConstants var61 = (DrawingConstants)var10000;
         var52.endReplaceGroup();
         DrawingConstants var59 = var61;
         Object[] var171 = new Object[0];
         Saver var10001 = WaveformBufferSaver;
         var52.startReplaceGroup(-1478315609);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var66 = var52;
         boolean var69 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var66.rememberedValue();
         int var87/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var196;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Saver var50 = var10001;
            Object[] var49 = var171;
            int var100 = 0;
            Function0 var51 = ((kotlin.jvm.functions.Function0) () -> SoundWaveVisualizationKt.SoundWaveVisualization_FNF3uiM$lambda$7$lambda$6());
            var171 = var49;
            var10001 = var50;
            Object var101 = var51;
            var66.updateRememberedValue(var101);
            var196 = var101;
         } else {
            var196 = it$ivx;
         }

         Function0 var64 = (Function0)var196;
         var52.endReplaceGroup();
         WaveformBuffer var62 = (WaveformBuffer)RememberSaveableKt.rememberSaveable(var171, var10001, var64, var52, 384);
         var52.startReplaceGroup(-1478312809);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var70 = var52;
         boolean var74 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var70.rememberedValue();
         int var102/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var172;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var116 = 0;
            Object var117 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
            var70.updateRememberedValue(var117);
            var172 = var117;
         } else {
            var172 = it$ivxx;
         }

         MutableFloatState var67 = (MutableFloatState)var172;
         var52.endReplaceGroup();
         MutableFloatState var65 = var67;
         var52.startReplaceGroup(-1478310442);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var75 = var52;
         boolean var81 = false;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var75.rememberedValue();
         int var118/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var173;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            int var27 = 0;
            Object var129 = SnapshotLongStateKt.mutableLongStateOf(0L);
            var75.updateRememberedValue(var129);
            var173 = var129;
         } else {
            var173 = it$ivxxx;
         }

         MutableLongState var71 = (MutableLongState)var173;
         var52.endReplaceGroup();
         MutableLongState var68 = var71;
         var52.startReplaceGroup(-1478308234);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var82 = var52;
         boolean var90 = false;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var82.rememberedValue();
         int var130/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var174;
         if (it$ivxxxx == Composer.Companion.getEmpty()) {
            int var28 = 0;
            Object var138 = SnapshotLongStateKt.mutableLongStateOf(0L);
            var82.updateRememberedValue(var138);
            var174 = var138;
         } else {
            var174 = it$ivxxxx;
         }

         MutableLongState var76 = (MutableLongState)var174;
         var52.endReplaceGroup();
         MutableLongState var72 = var76;
         Float var175 = SoundWaveVisualization_FNF3uiM$lambda$9(var65);
         var52.startReplaceGroup(-1478303091);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var83 = var52;
         boolean invalid$ivx = var52.changed(var55) | var52.changedInstance(var62);
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var83.rememberedValue();
         int var131/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var187;
         if (!invalid$ivx && it$ivxxxxx != Composer.Companion.getEmpty()) {
            var187 = it$ivxxxxx;
         } else {
            Float var166 = var175;
            int var139 = 0;
            Function2 var186 = new SoundWaveVisualizationKt$SoundWaveVisualization$1$1(var55, var62, var65, null);
            var175 = var166;
            Object var29 = var186;
            var83.updateRememberedValue(var29);
            var187 = var29;
         }

         Function2 var77 = (Function2)var187;
         var52.endReplaceGroup();
         EffectsKt.LaunchedEffect(var175, var77, var52, 0);
         var52.startReplaceGroup(-1478292265);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var92 = var52;
         boolean var106 = false;
         int $i$f$cachexxxxxx = 0;
         Object it$ivxxxxxx = var92.rememberedValue();
         int var140/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxx */ = 0;
         Object var176;
         if (it$ivxxxxxx == Composer.Companion.getEmpty()) {
            int var147 = 0;
            Object var148 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
            var92.updateRememberedValue(var148);
            var176 = var148;
         } else {
            var176 = it$ivxxxxxx;
         }

         MutableFloatState var84 = (MutableFloatState)var176;
         var52.endReplaceGroup();
         MutableFloatState var78 = var84;
         var52.startReplaceGroup(-1478290185);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var107 = var52;
         boolean var122 = false;
         int $i$f$cachexxxxxxx = 0;
         Object it$ivxxxxxxx = var107.rememberedValue();
         int var149/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxx */ = 0;
         Object var177;
         if (it$ivxxxxxxx == Composer.Companion.getEmpty()) {
            int var30 = 0;
            Object var155 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
            var107.updateRememberedValue(var155);
            var177 = var155;
         } else {
            var177 = it$ivxxxxxxx;
         }

         MutableFloatState var93 = (MutableFloatState)var177;
         var52.endReplaceGroup();
         MutableFloatState var85 = var93;
         Float var178 = SoundWaveVisualization_FNF3uiM$lambda$3(var8);
         var52.startReplaceGroup(-1478285134);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var108 = var52;
         boolean invalid$ivxx = var52.changed(var8);
         int $i$f$cachexxxxxxxx = 0;
         Object it$ivxxxxxxxx = var108.rememberedValue();
         int var150/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
         Object var189;
         if (!invalid$ivxx && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
            var189 = it$ivxxxxxxxx;
         } else {
            Float var167 = var178;
            int var156 = 0;
            Function2 var188 = new SoundWaveVisualizationKt$SoundWaveVisualization$2$1(var59, var8, var85, var78, null);
            var178 = var167;
            Object var31 = var188;
            var108.updateRememberedValue(var31);
            var189 = var31;
         }

         Function2 var94 = (Function2)var189;
         var52.endReplaceGroup();
         EffectsKt.LaunchedEffect(var178, var94, var52, 0);
         Unit var179 = Unit.INSTANCE;
         var52.startReplaceGroup(-1478256868);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var109 = var52;
         boolean invalid$ivxxx = var52.changedInstance(var62);
         int $i$f$cachexxxxxxxxx = 0;
         Object it$ivxxxxxxxxx = var109.rememberedValue();
         int var151/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxx */ = 0;
         Object var191;
         if (!invalid$ivxxx && it$ivxxxxxxxxx != Composer.Companion.getEmpty()) {
            var191 = it$ivxxxxxxxxx;
         } else {
            Unit var168 = var179;
            int var157 = 0;
            Function2 var190 = new SoundWaveVisualizationKt$SoundWaveVisualization$3$1(var53, var62, var65, var68, var78, null);
            var179 = var168;
            Object var161 = var190;
            var109.updateRememberedValue(var161);
            var191 = var161;
         }

         Function2 var95 = (Function2)var191;
         var52.endReplaceGroup();
         EffectsKt.LaunchedEffect(var179, var95, var52, 6);
         Unit var180 = Unit.INSTANCE;
         var52.startReplaceGroup(-1478220018);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var110 = var52;
         boolean invalid$ivxxxx = var52.changed(var55) | var52.changed(var57) | var52.changedInstance(var62);
         int $i$f$cachexxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxx = var110.rememberedValue();
         int var152/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxx */ = 0;
         Object var193;
         if (!invalid$ivxxxx && it$ivxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var193 = it$ivxxxxxxxxxx;
         } else {
            Unit var169 = var180;
            int var158 = 0;
            Function2 var192 = new SoundWaveVisualizationKt$SoundWaveVisualization$4$1(var55, var57, var62, var54, var65, var72, null);
            var180 = var169;
            Object var162 = var192;
            var110.updateRememberedValue(var162);
            var193 = var162;
         }

         Function2 var96 = (Function2)var193;
         var52.endReplaceGroup();
         EffectsKt.LaunchedEffect(var180, var96, var52, 6);
         float var111 = SoundWaveVisualization_FNF3uiM$lambda$9(var65);
         long var126 = SoundWaveVisualization_FNF3uiM$lambda$15(var72);
         var52.startReplaceGroup(-1478195988);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var153 = var52;
         boolean invalid$ivxxxxx = var52.changed(var111) | var52.changed(var126);
         int $i$f$cachexxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxx = var153.rememberedValue();
         int var33/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxx */ = 0;
         Object var182;
         if (!invalid$ivxxxxx && it$ivxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var182 = it$ivxxxxxxxxxxx;
         } else {
            int var34 = 0;
            List var181;
            if (SoundWaveVisualization_FNF3uiM$lambda$9(var65) > 0.0F) {
               long var35 = System.currentTimeMillis();
               List var37 = new ArrayList();
               Iterable var38 = var62.getAllElements();
               int var39 = 0;

               for (Object var41 : var38) {
                  WaveformElement var42 = (WaveformElement)var41;
                  int var43 = 0;
                  long var44 = var35 - var42.getTimestamp();
                  float var46 = (float)var44 * var57;
                  boolean var47 = var46 <= SoundWaveVisualization_FNF3uiM$lambda$9(var65) + var55;
                  if (var47) {
                     var37.add(WaveformElement.copy$default(var42, var46, 0.0F, 0L, 6, null));
                  }
               }

               var181 = var37;
            } else {
               var181 = CollectionsKt.emptyList();
            }

            Object var48 = var181;
            var153.updateRememberedValue(var48);
            var182 = var48;
         }

         List var145 = (List)var182;
         var52.endReplaceGroup();
         List var97 = var145;
         Modifier var183 = SizeKt.fillMaxWidth$default(var1, 0.0F, 1, null);
         int $this$dp$ivx = 24;
         int $i$f$getDpx = 0;
         Modifier var184 = SizeKt.recovered$3f6c6a76aa292717(var183, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         var52.startReplaceGroup(-1478156799);
         ComposerKt.sourceInformation(var52, "CC(remember):SoundWaveVisualization.kt#9igjgp");
         Composer var128 = var52;
         boolean var137 = var52.changedInstance(var97) | ((var7 & 896 ^ 384) > 256 && var52.changed(var2) || (var7 & 384) == 256) | var52.changed(var9);
         int $i$f$cachexxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxx = var128.rememberedValue();
         int var160/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxx */ = 0;
         Object var195;
         if (!var137 && it$ivxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var195 = it$ivxxxxxxxxxxxx;
         } else {
            Modifier var170 = var184;
            int var164 = 0;
            final java.util.List recoveredCapture_643_0 = var97;
            final long recoveredCapture_643_1 = var2;
            final io.intercom.android.sdk.m5.conversation.ui.components.composer.DrawingConstants recoveredCapture_643_2 = var59;
            final androidx.compose.ui.unit.LayoutDirection recoveredCapture_643_3 = var9;
            final androidx.compose.runtime.MutableFloatState recoveredCapture_643_4 = var65;
            Function1 var194 = ((kotlin.jvm.functions.Function1) (recoveredArgument_643_0) -> SoundWaveVisualizationKt.SoundWaveVisualization_FNF3uiM$lambda$30$lambda$29(recoveredCapture_643_0, recoveredCapture_643_1, recoveredCapture_643_2, recoveredCapture_643_3, recoveredCapture_643_4, (androidx.compose.ui.graphics.drawscope.DrawScope)recoveredArgument_643_0));
            var184 = var170;
            Object var165 = var194;
            var128.updateRememberedValue(var165);
            var195 = var165;
         }

         Function1 var113 = (Function1)var195;
         var52.endReplaceGroup();
         CanvasKt.Canvas(var184, var113, var52, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var185 = var52.endRestartGroup();
      if (var185 != null) {
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_644_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_644_1 = var1;
         final long recoveredCapture_644_2 = var2;
         final int recoveredCapture_644_3 = var5;
         final int recoveredCapture_644_4 = var6;
         var185.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_644_0, recoveredArgument_644_1) -> SoundWaveVisualizationKt.SoundWaveVisualization_FNF3uiM$lambda$31(recoveredCapture_644_0, recoveredCapture_644_1, recoveredCapture_644_2, recoveredCapture_644_3, recoveredCapture_644_4, (androidx.compose.runtime.Composer)recoveredArgument_644_0, ((java.lang.Integer)recoveredArgument_644_1).intValue())));
      }
   }

   private static final void recovered$bca64fed1f445d0a(
      DrawScope var0, List var1, long var2, DrawingConstants var4, float var5, float var6, LayoutDirection var7
   ) {
      if (!var1.isEmpty()) {
         DrawingConstantsPx var8 = var4.toPx(var0.getDensity());
         float var9 = var6 * 0.5F;
         float var10 = var6;
         float var13 = var8.getBarWidth() * 0.5F;
         float var14 = var8.getBarWidth() * 0.5F;
         int var15 = 0;
         float var16 = var13;
         float var17 = var14;
         int var18 = 0;
         long var19 = Float.floatToRawIntBits(var16);
         long var21 = Float.floatToRawIntBits(var17);
         long var11 = CornerRadius.recovered$8c837f3e34208708(var19 << 32 | var21 & 4294967295L);
         Iterable var35 = var1;
         int var36 = 0;

         for (Object var38 : var35) {
            WaveformElement var39 = (WaveformElement)var38;
            int var40 = 0;
            float var10000;
            switch (var7) {
               case Ltr:
                  var10000 = var5 - var39.getX();
                  break;
               case Rtl:
                  var10000 = var39.getX();
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }

            float var41 = var10000;
            if (!(var41 < -var8.getBarWidth()) && !(var41 > var5 + var8.getBarWidth())) {
               if (var39.getAmplitude() < var8.getAmplitudeThreshold()) {
                  float var10002 = var8.getDotRadius();
                  int var20 = 0;
                  float var43 = var41;
                  float val2$iv$ivx = var9;
                  int $i$f$packFloatsx = 0;
                  long v1$iv$ivx = Float.floatToRawIntBits(var43);
                  long v2$iv$ivx = Float.floatToRawIntBits(val2$iv$ivx);
                  DrawScope.recovered$34dce0a89fdcb963(
                     var0, var2, var10002, Offset.recovered$8c837f3e34208708(v1$iv$ivx << 32 | v2$iv$ivx & 4294967295L), 0.0F, null, null, 0, 120, null
                  );
               } else {
                  float var42 = Math.max(var8.getMinBarHeight(), Math.min(var10, var39.getAmplitude() * var10));
                  float var44 = var9 - var42 * 0.5F;
                  float var45 = var41 - var8.getBarWidth() * 0.5F;
                  int var47 = 0;
                  float var28 = var45;
                  float val2$iv$ivx = var44;
                  int $i$f$packFloatsx = 0;
                  long v1$iv$ivx = Float.floatToRawIntBits(var28);
                  long v2$iv$ivx = Float.floatToRawIntBits(val2$iv$ivx);
                  long var54 = Offset.recovered$8c837f3e34208708(v1$iv$ivx << 32 | v2$iv$ivx & 4294967295L);
                  float var46 = var8.getBarWidth();
                  int var48 = 0;
                  float var49 = var46;
                  float val2$iv$ivxx = var42;
                  int $i$f$packFloatsxx = 0;
                  long v1$iv$ivxx = Float.floatToRawIntBits(var49);
                  long v2$iv$ivxx = Float.floatToRawIntBits(val2$iv$ivxx);
                  DrawScope.recovered$c054642d30762511(
                     var0, var2, var54, Size.recovered$8c837f3e34208708(v1$iv$ivxx << 32 | v2$iv$ivxx & 4294967295L), var11, null, 0.0F, null, 0, 240, null
                  );
               }
            }
         }
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SoundWaveVisualizationPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(888011189);
      ComposerKt.sourceInformation(var2, "C(SoundWaveVisualizationPreview)550@22258L268:SoundWaveVisualization.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               888011189,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationPreview (SoundWaveVisualization.kt:549)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$SoundWaveVisualizationKt.INSTANCE.recovered$eac478aa65045e5d(),
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
         final int recoveredCapture_641_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_641_0, recoveredArgument_641_1) -> SoundWaveVisualizationKt.SoundWaveVisualizationPreview$lambda$33(recoveredCapture_641_0, (androidx.compose.runtime.Composer)recoveredArgument_641_0, ((java.lang.Integer)recoveredArgument_641_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SoundWaveVisualizationLowAmplitudePreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1762579162);
      ComposerKt.sourceInformation(var2, "C(SoundWaveVisualizationLowAmplitudePreview)564@22622L269:SoundWaveVisualization.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1762579162,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationLowAmplitudePreview (SoundWaveVisualization.kt:563)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$SoundWaveVisualizationKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredCapture_640_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_640_0, recoveredArgument_640_1) -> SoundWaveVisualizationKt.SoundWaveVisualizationLowAmplitudePreview$lambda$34(recoveredCapture_640_0, (androidx.compose.runtime.Composer)recoveredArgument_640_0, ((java.lang.Integer)recoveredArgument_640_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SoundWaveVisualizationRtlPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-436763649);
      ComposerKt.sourceInformation(var2, "C(SoundWaveVisualizationRtlPreview)578@22978L388:SoundWaveVisualization.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -436763649,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.SoundWaveVisualizationRtlPreview (SoundWaveVisualization.kt:577)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$SoundWaveVisualizationKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredCapture_642_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_642_0, recoveredArgument_642_1) -> SoundWaveVisualizationKt.SoundWaveVisualizationRtlPreview$lambda$35(recoveredCapture_642_0, (androidx.compose.runtime.Composer)recoveredArgument_642_0, ((java.lang.Integer)recoveredArgument_642_1).intValue())));
      }
   }

   private static final List WaveformBufferSaver$lambda$0(SaverScope var0, WaveformBuffer var1) {
      Intrinsics.checkNotNullParameter(var0, "$this$listSaver");
      Intrinsics.checkNotNullParameter(var1, "buffer");
      long var2 = System.currentTimeMillis();
      List var4 = var1.getAllElements();
      Serializable[] var5 = new Serializable[4];
      var5[0] = var1.getCapacity();
      int var6 = 0;
      int var7 = var4.size();
      float[] var8 = new float[var7];
      byte var11 = 1;
      Serializable[] var10 = var5;

      while (var6 < var7) {
         int var9 = var6;
         var8[var9] = ((WaveformElement)var4.get(var9)).getX();
         var6++;
      }

      var10[var11] = var8;
      int var12 = 0;
      int var14 = var4.size();
      float[] var16 = new float[var14];
      byte var22 = 2;
      Serializable[] var20 = var5;

      while (var12 < var14) {
         int var18 = var12;
         var16[var18] = ((WaveformElement)var4.get(var18)).getAmplitude();
         var12++;
      }

      var20[var22] = var16;
      int var13 = 0;
      int var15 = var4.size();
      long[] var17 = new long[var15];
      byte var23 = 3;
      Serializable[] var21 = var5;

      while (var13 < var15) {
         int var19 = var13;
         var17[var19] = var2 - ((WaveformElement)var4.get(var19)).getTimestamp();
         var13++;
      }

      var21[var23] = var17;
      return CollectionsKt.listOf(var5);
   }

   private static final WaveformBuffer WaveformBufferSaver$lambda$2(List var0) {
      Intrinsics.checkNotNullParameter(var0, "saved");
      long var1 = System.currentTimeMillis();
      Object var10000 = var0.get(0);
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.Int");
      int var3 = (Integer)var10000;
      Object var12 = var0.get(1);
      Intrinsics.checkNotNull(var12, "null cannot be cast to non-null type kotlin.FloatArray");
      float[] var4 = (float[])var12;
      Object var13 = var0.get(2);
      Intrinsics.checkNotNull(var13, "null cannot be cast to non-null type kotlin.FloatArray");
      float[] var5 = (float[])var13;
      Object var14 = var0.get(3);
      Intrinsics.checkNotNull(var14, "null cannot be cast to non-null type kotlin.LongArray");
      long[] var6 = (long[])var14;
      WaveformBuffer var7 = new WaveformBuffer(var3);
      WaveformBuffer var8 = var7;
      int var9 = 0;
      int var10 = 0;

      for (int var11 = var4.length; var10 < var11; var10++) {
         var8.addElement(new WaveformElement(var4[var10], var5[var10], var1 - var6[var10]));
      }

      return var7;
   }

   private static final float SoundWaveVisualization_FNF3uiM$lambda$3(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).floatValue();
   }

   private static final WaveformBuffer SoundWaveVisualization_FNF3uiM$lambda$7$lambda$6() {
      return new WaveformBuffer(0, 1, null);
   }

   private static final float SoundWaveVisualization_FNF3uiM$lambda$9(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void SoundWaveVisualization_FNF3uiM$lambda$10(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final long SoundWaveVisualization_FNF3uiM$lambda$12(MutableLongState var0) {
      LongState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getLongValue();
   }

   private static final void SoundWaveVisualization_FNF3uiM$lambda$13(MutableLongState var0, long var1) {
      MutableLongState var3 = var0;
      Object var4 = null;
      KProperty var5 = null;
      int var6 = 0;
      var3.setLongValue(var1);
   }

   private static final long SoundWaveVisualization_FNF3uiM$lambda$15(MutableLongState var0) {
      LongState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getLongValue();
   }

   private static final void SoundWaveVisualization_FNF3uiM$lambda$16(MutableLongState var0, long var1) {
      MutableLongState var3 = var0;
      Object var4 = null;
      KProperty var5 = null;
      int var6 = 0;
      var3.setLongValue(var1);
   }

   private static final float SoundWaveVisualization_FNF3uiM$lambda$19(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void SoundWaveVisualization_FNF3uiM$lambda$20(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final float SoundWaveVisualization_FNF3uiM$lambda$22(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void SoundWaveVisualization_FNF3uiM$lambda$23(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final Unit SoundWaveVisualization_FNF3uiM$lambda$30$lambda$29(
      List var0, long var1, DrawingConstants var3, LayoutDirection var4, MutableFloatState var5, DrawScope var6
   ) {
      Intrinsics.checkNotNullParameter(var6, "$this$Canvas");
      long var7 = var6.recovered$d1ee80c43f6fa69a();
      int var9 = 0;
      long var10 = var7;
      int var12 = 0;
      int var13 = (int)(var10 >> 32);
      int var14 = 0;
      SoundWaveVisualization_FNF3uiM$lambda$10(var5, Float.intBitsToFloat(var13));
      long arg0$ivx = var6.recovered$d1ee80c43f6fa69a();
      int var17/* $VF was: $i$f$getWidth-implx */ = 0;
      long value$iv$ivx = arg0$ivx;
      int $i$f$unpackFloat1x = 0;
      int bits$iv$iv$ivx = (int)(value$iv$ivx >> 32);
      int $i$f$floatFromBitsx = 0;
      float var10004 = Float.intBitsToFloat(bits$iv$iv$ivx);
      long arg0$ivxx = var6.recovered$d1ee80c43f6fa69a();
      int var18 = 0;
      long value$iv$ivxx = arg0$ivxx;
      int var22 = 0;
      int bits$iv$iv$ivxx = (int)(value$iv$ivxx & 4294967295L);
      int $i$f$floatFromBitsxx = 0;
      recovered$bca64fed1f445d0a(var6, var0, var1, var3, var10004, Float.intBitsToFloat(bits$iv$iv$ivxx), var4);
      return Unit.INSTANCE;
   }

   private static final Unit SoundWaveVisualization_FNF3uiM$lambda$31(StateFlow var0, Modifier var1, long var2, int var4, int var5, Composer var6, int var7) {
      recovered$92b4091f81df4daa(var0, var1, var2, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit SoundWaveVisualizationPreview$lambda$33(int var0, Composer var1, int var2) {
      SoundWaveVisualizationPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SoundWaveVisualizationLowAmplitudePreview$lambda$34(int var0, Composer var1, int var2) {
      SoundWaveVisualizationLowAmplitudePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SoundWaveVisualizationRtlPreview$lambda$35(int var0, Composer var1, int var2) {
      SoundWaveVisualizationRtlPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final float access$SoundWaveVisualization_FNF3uiM$lambda$9(MutableFloatState var0) {
      return SoundWaveVisualization_FNF3uiM$lambda$9(var0);
   }

   // $VF: synthetic method
   public static final float access$SoundWaveVisualization_FNF3uiM$lambda$3(State var0) {
      return SoundWaveVisualization_FNF3uiM$lambda$3(var0);
   }

   // $VF: synthetic method
   public static final float access$SoundWaveVisualization_FNF3uiM$lambda$22(MutableFloatState var0) {
      return SoundWaveVisualization_FNF3uiM$lambda$22(var0);
   }

   // $VF: synthetic method
   public static final void access$SoundWaveVisualization_FNF3uiM$lambda$20(MutableFloatState var0, float var1) {
      SoundWaveVisualization_FNF3uiM$lambda$20(var0, var1);
   }

   // $VF: synthetic method
   public static final float access$SoundWaveVisualization_FNF3uiM$lambda$19(MutableFloatState var0) {
      return SoundWaveVisualization_FNF3uiM$lambda$19(var0);
   }

   // $VF: synthetic method
   public static final void access$SoundWaveVisualization_FNF3uiM$lambda$23(MutableFloatState var0, float var1) {
      SoundWaveVisualization_FNF3uiM$lambda$23(var0, var1);
   }

   // $VF: synthetic method
   public static final long access$SoundWaveVisualization_FNF3uiM$lambda$12(MutableLongState var0) {
      return SoundWaveVisualization_FNF3uiM$lambda$12(var0);
   }

   // $VF: synthetic method
   public static final float access$scaleAmplitudeForVisualization(float var0) {
      return scaleAmplitudeForVisualization(var0);
   }

   // $VF: synthetic method
   public static final void access$SoundWaveVisualization_FNF3uiM$lambda$13(MutableLongState var0, long var1) {
      SoundWaveVisualization_FNF3uiM$lambda$13(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$SoundWaveVisualization_FNF3uiM$lambda$16(MutableLongState var0, long var1) {
      SoundWaveVisualization_FNF3uiM$lambda$16(var0, var1);
   }
}
