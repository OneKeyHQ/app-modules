package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import androidx.compose.foundation.gestures.ForEachGestureKt;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.FloatState;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.PrimitiveSnapshotStateKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.input.pointer.AwaitPointerEventScope;
import androidx.compose.ui.input.pointer.PointerEvent;
import androidx.compose.ui.input.pointer.PointerEventKt;
import androidx.compose.ui.input.pointer.PointerId;
import androidx.compose.ui.input.pointer.PointerInputChange;
import androidx.compose.ui.input.pointer.PointerInputScope;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgsKt;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
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
   d1 = "\u0000`\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001aN\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2#\b\u0002\u0010\f\u001a\u001d\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00050\rH\u0001¢\u0006\u0002\u0010\u0011\u001a,\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u0001H\u0000\u001a?\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u0001H\u0000¢\u0006\u0004\b\u001f\u0010 \u001a\u0081\u0001\u0010!\u001a\u00020\u0005*\u00020\"2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0#2\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00050\r2K\u0010%\u001aG\u0012\u0013\u0012\u00110\u0018¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0019\u0012\u0013\u0012\u00110\u0018¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u001a\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b('\u0012\u0004\u0012\u00020\u00050&H\u0082@¢\u0006\u0002\u0010(\u001a$\u0010)\u001a\u00020\u000b*\u00020*2\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0001H\u0082@¢\u0006\u0004\b.\u0010/\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u00060²\u0006\n\u00101\u001a\u00020\u0001X\u008a\u008e\u0002²\u0006\n\u00102\u001a\u00020\u0018X\u008a\u008e\u0002²\u0006\n\u00103\u001a\u00020\u0001X\u008a\u008e\u0002",
   d2 = {
         "MIN_SCALE",
         "",
         "MAX_SCALE",
         "DOUBLE_TAP_SCALE",
         "ZoomableImage",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "file",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "isCurrentPage",
         "",
         "onZoomChanged",
         "Lkotlin/Function1;",
         "Lkotlin/ParameterName;",
         "name",
         "isZoomed",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;ZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "fittedContentSize",
         "Lkotlin/Pair;",
         "aspectRatio",
         "containerWidth",
         "containerHeight",
         "focalZoomOffset",
         "Landroidx/compose/ui/geometry/Offset;",
         "centroid",
         "pan",
         "center",
         "currentOffset",
         "oldScale",
         "newScale",
         "focalZoomOffset-QmVSrF8",
         "(JJJJFF)J",
         "detectZoomPanAndDoubleTap",
         "Landroidx/compose/ui/input/pointer/PointerInputScope;",
         "Lkotlin/Function0;",
         "onDoubleTap",
         "onGesture",
         "Lkotlin/Function3;",
         "zoom",
         "(Landroidx/compose/ui/input/pointer/PointerInputScope;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "awaitTapCompletion",
         "Landroidx/compose/ui/input/pointer/AwaitPointerEventScope;",
         "pointerId",
         "Landroidx/compose/ui/input/pointer/PointerId;",
         "touchSlop",
         "awaitTapCompletion-jO51t88",
         "(Landroidx/compose/ui/input/pointer/AwaitPointerEventScope;JFLkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release",
         "scale",
         "offset",
         "imageAspectRatio"
   }
)
@SourceDebugExtension(
   "SMAP\nZoomableImage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 ListUtils.kt\nandroidx/compose/ui/util/ListUtilsKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 SnapshotFloatState.kt\nandroidx/compose/runtime/PrimitiveSnapshotStateKt__SnapshotFloatStateKt\n+ 7 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,277:1\n1047#2,6:278\n1047#2,6:285\n1047#2,6:291\n1047#2,6:297\n1047#2,6:303\n1047#2,6:309\n75#3:284\n103#4:315\n35#4,5:316\n104#4:321\n295#5,2:322\n80#6:324\n113#6,2:325\n80#6:330\n113#6,2:331\n85#7:327\n117#7,2:328\n*S KotlinDebug\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt\n*L\n50#1:278,6\n58#1:285,6\n59#1:291,6\n60#1:297,6\n62#1:303,6\n64#1:309,6\n52#1:284\n270#1:315\n270#1:316,5\n270#1:321\n271#1:322,2\n58#1:324\n58#1:325,2\n60#1:330\n60#1:331,2\n59#1:327\n59#1:328,2\n*E\n"
)
public final class ZoomableImageKt {
   private static final float MIN_SCALE = 1.0F;
   private static final float MAX_SCALE = 5.0F;
   private static final float DOUBLE_TAP_SCALE = 2.5F;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ZoomableImage(
      @Nullable Modifier var0, @NotNull IntercomPreviewFile var1, boolean var2, @Nullable Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "file");
      Composer var21 = var4.startRestartGroup(-684173863);
      ComposerKt.sourceInformation(
         var21,
         "C(ZoomableImage)P(2)49@2183L2,51@2222L7,57@2361L49,58@2429L46,59@2504L42,61@2586L36,61@2552L70,63@2658L110,63@2628L140,70@2843L3733,70@2774L3802:ZoomableImage.kt#hhbc0c"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var21.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var21.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var21.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var21.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var21.getSkipping()) {
         var21.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 4) != 0) {
            var2 = true;
         }

         if ((var6 & 8) != 0) {
            var21.startReplaceGroup(-1904547954);
            ComposerKt.sourceInformation(var21, "CC(remember):ZoomableImage.kt#9igjgp");
            Composer var9 = var21;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var43 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1176_0) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt.ZoomableImage$lambda$1$lambda$0(((java.lang.Boolean)recoveredExpressionArgument_1176_0).booleanValue()));
               var9.updateRememberedValue(var43);
               var10000 = var43;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var21.endReplaceGroup();
            var3 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-684173863, var7, -1, "io.intercom.android.sdk.m5.preview.ui.ZoomableImage (ZoomableImage.kt:50)");
         }

         CompositionLocal var23 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var25 = 0;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var32 = var21.consume(var23);
         ComposerKt.sourceInformationMarkerEnd(var21);
         Context var22 = (Context)var32;
         var21.startReplaceGroup(-1904546216);
         ComposerKt.sourceInformation(var21, "53@2271L50");
         if (!IntercomPreviewArgsKt.hasSafeScheme(var1)) {
            PreviewUriKt.ThumbnailPreview(var0, null, var1, var21, 14 & var7 | 896 & var7 << 3, 2);
            var21.endReplaceGroup();
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var72 = var21.endRestartGroup();
            if (var72 != null) {
               final androidx.compose.ui.Modifier recoveredExpressionCapture_1177_0 = var0;
               final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1177_1 = var1;
               final boolean recoveredExpressionCapture_1177_2 = var2;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1177_3 = var3;
               final int recoveredExpressionCapture_1177_4 = var5;
               final int recoveredExpressionCapture_1177_5 = var6;
               var72.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1177_0, recoveredExpressionArgument_1177_1) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt.ZoomableImage$lambda$2(recoveredExpressionCapture_1177_0, recoveredExpressionCapture_1177_1, recoveredExpressionCapture_1177_2, recoveredExpressionCapture_1177_3, recoveredExpressionCapture_1177_4, recoveredExpressionCapture_1177_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1177_0), ((java.lang.Integer)recoveredExpressionArgument_1177_1).intValue())));
            }

            return;
         }

         var21.endReplaceGroup();
         var21.startReplaceGroup(-1904542211);
         ComposerKt.sourceInformation(var21, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var29 = var21;
         boolean var33 = var21.changed(var1);
         int var38 = 0;
         Object var44 = var29.rememberedValue();
         int var15 = 0;
         Object var67;
         if (!var33 && var44 != Composer.Companion.getEmpty()) {
            var67 = var44;
         } else {
            int var16 = 0;
            Object var53 = PrimitiveSnapshotStateKt.mutableFloatStateOf(1.0F);
            var29.updateRememberedValue(var53);
            var67 = var53;
         }

         MutableFloatState var26 = (MutableFloatState)var67;
         var21.endReplaceGroup();
         MutableFloatState var24 = var26;
         var21.startReplaceGroup(-1904540038);
         ComposerKt.sourceInformation(var21, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var34 = var21;
         boolean invalid$ivx = var21.changed(var1);
         int $i$f$cachex = 0;
         Object it$ivx = var34.rememberedValue();
         int var54/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var68;
         if (!invalid$ivx && it$ivx != Composer.Companion.getEmpty()) {
            var68 = it$ivx;
         } else {
            int var17 = 0;
            Object var58 = SnapshotStateKt.mutableStateOf$default(
               Offset.recovered$ae98585edc0e5b3c(Offset.Companion.recovered$86a8752c359fef5c()), null, 2, null
            );
            var34.updateRememberedValue(var58);
            var68 = var58;
         }

         MutableState var30 = (MutableState)var68;
         var21.endReplaceGroup();
         MutableState var27 = var30;
         var21.startReplaceGroup(-1904537642);
         ComposerKt.sourceInformation(var21, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var40 = var21;
         boolean invalid$ivxx = var21.changed(var1);
         int $i$f$cachexx = 0;
         Object it$ivxx = var40.rememberedValue();
         int var59/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var69;
         if (!invalid$ivxx && it$ivxx != Composer.Companion.getEmpty()) {
            var69 = it$ivxx;
         } else {
            int var18 = 0;
            Object var62 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
            var40.updateRememberedValue(var62);
            var69 = var62;
         }

         MutableFloatState var35 = (MutableFloatState)var69;
         var21.endReplaceGroup();
         MutableFloatState var31 = var35;
         Boolean var70 = ZoomableImage$lambda$4(var24) > 1.0F;
         var21.startReplaceGroup(-1904535024);
         ComposerKt.sourceInformation(var21, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var41 = var21;
         boolean invalid$ivxxx = (var7 & 7168) == 2048 | var21.changed(var24);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var41.rememberedValue();
         int var60/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var74;
         if (!invalid$ivxxx && it$ivxxx != Composer.Companion.getEmpty()) {
            var74 = it$ivxxx;
         } else {
            Boolean var20 = var70;
            int var63 = 0;
            Function2 var10001 = new ZoomableImageKt$ZoomableImage$3$1(var3, var24, null);
            var70 = var20;
            Object var19 = var10001;
            var41.updateRememberedValue(var19);
            var74 = var19;
         }

         Function2 var36 = (Function2)var74;
         var21.endReplaceGroup();
         EffectsKt.LaunchedEffect(var70, var36, var21, 0);
         Boolean var71 = var2;
         var21.startReplaceGroup(-1904532646);
         ComposerKt.sourceInformation(var21, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var42 = var21;
         boolean invalid$ivxxxx = (var7 & 896) == 256 | var21.changed(var24) | var21.changed(var27);
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var42.rememberedValue();
         int var61/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var76;
         if (!invalid$ivxxxx && it$ivxxxx != Composer.Companion.getEmpty()) {
            var76 = it$ivxxxx;
         } else {
            Boolean var66 = var71;
            int var64 = 0;
            Function2 var75 = new ZoomableImageKt$ZoomableImage$4$1(var2, var24, var27, null);
            var71 = var66;
            Object var65 = var75;
            var42.updateRememberedValue(var65);
            var76 = var65;
         }

         Function2 var37 = (Function2)var76;
         var21.endReplaceGroup();
         EffectsKt.LaunchedEffect(var71, var37, var21, 14 & var7 >> 6);
         BoxWithConstraintsKt.BoxWithConstraints(
            ClipKt.clipToBounds(SizeKt.fillMaxSize$default(var0, 0.0F, 1, null)),
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(-1281786833, true, new ZoomableImageKt$ZoomableImage$5(var27, var31, var24, var1, var22), var21, 54),
            var21,
            3072,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var73 = var21.endRestartGroup();
      if (var73 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1178_0 = var0;
         final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1178_1 = var1;
         final boolean recoveredExpressionCapture_1178_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1178_3 = var3;
         final int recoveredExpressionCapture_1178_4 = var5;
         final int recoveredExpressionCapture_1178_5 = var6;
         var73.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1178_0, recoveredExpressionArgument_1178_1) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt.ZoomableImage$lambda$14(recoveredExpressionCapture_1178_0, recoveredExpressionCapture_1178_1, recoveredExpressionCapture_1178_2, recoveredExpressionCapture_1178_3, recoveredExpressionCapture_1178_4, recoveredExpressionCapture_1178_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1178_0), ((java.lang.Integer)recoveredExpressionArgument_1178_1).intValue())));
      }
   }

   @NotNull
   public static final Pair fittedContentSize(float var0, float var1, float var2) {
      if (!(var0 <= 0.0F) && !(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float var3 = var1 / var2;
         return var0 > var3 ? TuplesKt.to(var1, var1 / var0) : TuplesKt.to(var2 * var0, var2);
      } else {
         return TuplesKt.to(var1, var2);
      }
   }

   public static final long recovered$62301fc7b8456b9b(long var0, long var2, long var4, long var6, float var8, float var9) {
      long var10 = Offset.recovered$557cc49f074d875a(Offset.recovered$557cc49f074d875a(var0, var2), var4);
      return Offset.recovered$557cc49f074d875a(
         Offset.recovered$968713ee027f1595(var10, var2), Offset.recovered$f5dbbbe7fe26c3bd(Offset.recovered$557cc49f074d875a(var10, var6), var9 / var8)
      );
   }

   private static final Object detectZoomPanAndDoubleTap(PointerInputScope var0, Function0 var1, Function1 var2, Function3 var3, Continuation var4) {
      Object var10000 = ForEachGestureKt.awaitEachGesture(var0, new ZoomableImageKt$detectZoomPanAndDoubleTap$2(var1, var3, var2, null), var4);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   private static final Object recovered$69660d10a319560d(AwaitPointerEventScope var0, long var1, float var3, Continuation var4) {
      ZoomableImageKt$awaitTapCompletion$1 var19;
      label93: {
         if (var4 instanceof ZoomableImageKt$awaitTapCompletion$1) {
            var19 = (ZoomableImageKt$awaitTapCompletion$1)var4;
            if ((var19.label & -2147483648) != 0) {
               var19.label -= Integer.MIN_VALUE;
               break label93;
            }
         }

         var19 = new ZoomableImageKt$awaitTapCompletion$1(var4);
      }

      Object var18 = var19.result;
      Object var20 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      float var5;
      PointerEvent var6;
      List var9;
      int var11;
      int var12;
      switch (var19.label) {
         case 0:
            ResultKt.throwOnFailure(var18);
            var5 = 0.0F;
            var19.L$0 = var0;
            var19.J$0 = var1;
            var19.F$0 = var3;
            var19.F$1 = var5;
            var19.label = 1;
            Object var10000 = AwaitPointerEventScope.awaitPointerEvent$default(var0, null, var19, 1, null);
            if (var10000 == var20) {
               return var20;
            }

            var6 = (PointerEvent)var10000;
            List var21 = var6.getChanges();
            boolean var25 = false;
            var9 = var21;
            boolean var29 = false;
            var11 = 0;
            var12 = var9.size();
            break;
         case 1:
            var5 = var19.F$1;
            var3 = var19.F$0;
            var1 = var19.J$0;
            var0 = (AwaitPointerEventScope)var19.L$0;
            ResultKt.throwOnFailure(var18);
            var6 = (PointerEvent)var18;
            List var7 = var6.getChanges();
            boolean var8 = false;
            var9 = var7;
            boolean var10 = false;
            var11 = 0;
            var12 = var9.size();
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (true) {
         Iterator var33;
         if (var11 < var12) {
            Object var13 = var9.get(var11);
            Object var14 = var13;
            int var15 = 0;
            PointerInputChange var16 = (PointerInputChange)var14;
            int var17 = 0;
            if (!var16.isConsumed()) {
               var11++;
               continue;
            }

            if (true) {
               return Boxing.boxBoolean(false);
            }

            Iterable var27 = var6.getChanges();
            boolean var30 = false;
            var33 = var27.iterator();
         } else {
            if (false) {
               return Boxing.boxBoolean(false);
            }

            Iterable var28 = var6.getChanges();
            int var31 = 0;
            var33 = var28.iterator();
         }

         while (true) {
            if (var33.hasNext()) {
               Object var34 = var33.next();
               PointerInputChange var35 = (PointerInputChange)var34;
               int var36 = 0;
               if (!PointerId.recovered$9eab66ea8c46e11c(var35.recovered$c6cceede56ec0b3a(), var1)) {
                  continue;
               }

               PointerInputChange var37 = (PointerInputChange)var34;
               if ((PointerInputChange)var34 == null) {
                  return Boxing.boxBoolean(false);
               }

               PointerInputChange var22 = var37;
               var5 += Offset.recovered$404c0a0a35ea845b(PointerEventKt.positionChange(var22));
               if (var5 > var3) {
                  return Boxing.boxBoolean(false);
               }

               if (!var22.getPressed()) {
                  return Boxing.boxBoolean(true);
               }
            } else {
               PointerInputChange var38 = (PointerInputChange)null;
               if ((PointerInputChange)null == null) {
                  return Boxing.boxBoolean(false);
               }

               PointerInputChange var23 = var38;
               var5 += Offset.recovered$404c0a0a35ea845b(PointerEventKt.positionChange(var23));
               if (var5 > var3) {
                  return Boxing.boxBoolean(false);
               }

               if (!var23.getPressed()) {
                  return Boxing.boxBoolean(true);
               }
            }

            var19.L$0 = var0;
            var19.J$0 = var1;
            var19.F$0 = var3;
            var19.F$1 = var5;
            var19.label = 1;
            Object var39 = AwaitPointerEventScope.awaitPointerEvent$default(var0, null, var19, 1, null);
            if (var39 == var20) {
               return var20;
            }

            var6 = (PointerEvent)var39;
            List var24 = var6.getChanges();
            int var26 = 0;
            var9 = var24;
            int var32 = 0;
            var11 = 0;
            var12 = var9.size();
            break;
         }
      }
   }

   private static final Unit ZoomableImage$lambda$1$lambda$0(boolean var0) {
      return Unit.INSTANCE;
   }

   private static final Unit ZoomableImage$lambda$2(
      Modifier var0, IntercomPreviewFile var1, boolean var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      ZoomableImage(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final float ZoomableImage$lambda$4(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void ZoomableImage$lambda$5(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final long ZoomableImage$lambda$7(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Offset)var1.getValue()).recovered$45f9de7b85e0e0b5();
   }

   private static final void ZoomableImage$lambda$8(MutableState var0, long var1) {
      MutableState var3 = var0;
      Object var4 = null;
      Object var5 = null;
      Object var6 = Offset.recovered$ae98585edc0e5b3c(var1);
      int var7 = 0;
      var3.setValue(var6);
   }

   private static final float ZoomableImage$lambda$10(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void ZoomableImage$lambda$11(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final Unit ZoomableImage$lambda$14(
      Modifier var0, IntercomPreviewFile var1, boolean var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      ZoomableImage(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final float access$ZoomableImage$lambda$10(MutableFloatState var0) {
      return ZoomableImage$lambda$10(var0);
   }

   // $VF: synthetic method
   public static final void access$ZoomableImage$lambda$8(MutableState var0, long var1) {
      ZoomableImage$lambda$8(var0, var1);
   }

   // $VF: synthetic method
   public static final long access$ZoomableImage$lambda$7(MutableState var0) {
      return ZoomableImage$lambda$7(var0);
   }

   // $VF: synthetic method
   public static final float access$ZoomableImage$lambda$4(MutableFloatState var0) {
      return ZoomableImage$lambda$4(var0);
   }

   // $VF: synthetic method
   public static final Object access$detectZoomPanAndDoubleTap(PointerInputScope var0, Function0 var1, Function1 var2, Function3 var3, Continuation var4) {
      return detectZoomPanAndDoubleTap(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static final void access$ZoomableImage$lambda$5(MutableFloatState var0, float var1) {
      ZoomableImage$lambda$5(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$ZoomableImage$lambda$11(MutableFloatState var0, float var1) {
      ZoomableImage$lambda$11(var0, var1);
   }

   // $VF: synthetic method
   public static final Object recovered$407cb96d62b28197(AwaitPointerEventScope var0, long var1, float var3, Continuation var4) {
      return recovered$69660d10a319560d(var0, var1, var3, var4);
   }
}
