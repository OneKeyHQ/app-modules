package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.graphics.GraphicsLayerModifierKt;
import androidx.compose.ui.graphics.GraphicsLayerScope;
import androidx.compose.ui.input.pointer.PointerInputEventHandler;
import androidx.compose.ui.input.pointer.SuspendingPointerInputFilterKt;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.unit.Constraints;
import coil.ImageLoader;
import coil.compose.AsyncImageKt;
import coil.compose.AsyncImagePainter.State;
import coil.compose.AsyncImagePainter.State.Success;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nZoomableImage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt$ZoomableImage$5\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Offset.kt\nandroidx/compose/ui/geometry/Offset\n+ 5 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 6 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 7 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,277:1\n1047#2,6:278\n1047#2,6:284\n1047#2,6:290\n1047#2,6:297\n1#3:296\n65#4:303\n69#4:306\n65#4:313\n69#4:316\n60#5:304\n70#5:307\n53#5,3:310\n60#5:314\n70#5:317\n23#6:305\n23#6:308\n23#6:315\n23#6:318\n30#7:309\n*S KotlinDebug\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt$ZoomableImage$5\n*L\n89#1:278,6\n96#1:284,6\n131#1:290,6\n141#1:297,6\n84#1:303\n85#1:306\n134#1:313\n135#1:316\n84#1:304\n85#1:307\n83#1:310,3\n134#1:314\n135#1:317\n84#1:305\n85#1:308\n134#1:315\n135#1:318\n83#1:309\n*E\n"
)
final class ZoomableImageKt$ZoomableImage$5 implements Function3 {
   // $VF: synthetic field
   final MutableState $offset$delegate;
   // $VF: synthetic field
   final MutableFloatState $imageAspectRatio$delegate;
   // $VF: synthetic field
   final MutableFloatState $scale$delegate;
   // $VF: synthetic field
   final IntercomPreviewFile $file;
   // $VF: synthetic field
   final Context $context;

   ZoomableImageKt$ZoomableImage$5(MutableState var1, MutableFloatState var2, MutableFloatState var3, IntercomPreviewFile var4, Context var5) {
      this.$offset$delegate = var1;
      this.$imageAspectRatio$delegate = var2;
      this.$scale$delegate = var3;
      this.$file = var4;
      this.$context = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C88@3687L59,88@3621L125,95@3900L1760,130@5692L177,140@6131L428,92@3756L2814:ZoomableImage.kt#hhbc0c");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1281786833, var4, -1, "io.intercom.android.sdk.m5.preview.ui.ZoomableImage.<anonymous> (ZoomableImage.kt:71)");
         }

         float var5 = Constraints.recovered$700e12e6641fb7dc(var1.recovered$3f3fb3d4ce09b8ae());
         float var6 = Constraints.recovered$d4a6310cd710783c(var1.recovered$3f3fb3d4ce09b8ae());
         Float var10000 = ZoomableImageKt.access$ZoomableImage$lambda$10(this.$imageAspectRatio$delegate);
         Float var10001 = var5;
         Float var10002 = var6;
         var2.startReplaceGroup(-832814138);
         ComposerKt.sourceInformation(var2, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var8 = var2;
         boolean var9 = var2.changed(this.$offset$delegate)
            | var2.changed(this.$imageAspectRatio$delegate)
            | var2.changed(var5)
            | var2.changed(var6)
            | var2.changed(this.$scale$delegate);
         MutableState var10 = this.$offset$delegate;
         MutableFloatState var11 = this.$scale$delegate;
         MutableFloatState var12 = this.$imageAspectRatio$delegate;
         int var13 = 0;
         Object var14 = var8.rememberedValue();
         int var15 = 0;
         Object var10003;
         if (!var9 && var14 != Composer.Companion.getEmpty()) {
            var10003 = var14;
         } else {
            Float var22 = var10002;
            Float var21 = var10001;
            Float var20 = var10000;
            int var16 = 0;
            Function2 var23 = new ZoomableImageKt$ZoomableImage$5$1$1(var10, var11, var5, var6, var12, null);
            var10000 = var20;
            var10001 = var21;
            var10002 = var22;
            Object var17 = var23;
            var8.updateRememberedValue(var17);
            var10003 = var17;
         }

         Function2 var7 = (Function2)var10003;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var10001, var10002, var7, var2, 0);
         Modifier var69 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         Object[] var27 = new Object[]{this.$file, var5, var6};
         Object[] var72 = var27;
         var2.startReplaceGroup(-832805621);
         ComposerKt.sourceInformation(var2, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var31 = var2;
         boolean var34 = var2.changed(this.$scale$delegate)
            | var2.changed(this.$offset$delegate)
            | var2.changed(var5)
            | var2.changed(var6)
            | var2.changed(this.$imageAspectRatio$delegate);
         MutableFloatState var36 = this.$scale$delegate;
         MutableState var40 = this.$offset$delegate;
         MutableFloatState var44 = this.$imageAspectRatio$delegate;
         int $i$f$cachex = 0;
         Object it$ivx = var31.rememberedValue();
         int var54/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var76;
         if (!var34 && it$ivx != Composer.Companion.getEmpty()) {
            var76 = it$ivx;
         } else {
            Object[] var64 = var27;
            Modifier var61 = var69;
            int var57 = 0;
            PointerInputEventHandler var66 = new ZoomableImageKt$ZoomableImage$5$2$1(var36, var5, var6, var40, var44);
            var69 = var61;
            var72 = var64;
            Object var18 = var66;
            var31.updateRememberedValue(var18);
            var76 = var18;
         }

         PointerInputEventHandler var28 = (PointerInputEventHandler)var76;
         var2.endReplaceGroup();
         Modifier var70 = SuspendingPointerInputFilterKt.pointerInput(var69, var72, var28);
         var2.startReplaceGroup(-832749860);
         ComposerKt.sourceInformation(var2, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var32 = var2;
         boolean var35 = var2.changed(this.$scale$delegate) | var2.changed(this.$offset$delegate);
         MutableFloatState var37 = this.$scale$delegate;
         MutableState var41 = this.$offset$delegate;
         int $i$f$cachexx = 0;
         Object it$ivxx = var32.rememberedValue();
         int var52/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var74;
         if (!var35 && it$ivxx != Composer.Companion.getEmpty()) {
            var74 = it$ivxx;
         } else {
            Modifier var62 = var70;
            int var55 = 0;
            final androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1179_0 = var37;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1179_1 = var41;
            Function1 var73 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1179_0) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5.invoke$lambda$3$lambda$2(recoveredExpressionCapture_1179_0, recoveredExpressionCapture_1179_1, (androidx.compose.ui.graphics.GraphicsLayerScope) ((androidx.compose.ui.graphics.GraphicsLayerScope)recoveredExpressionArgument_1179_0)));
            var70 = var62;
            Object var58 = var73;
            var32.updateRememberedValue(var58);
            var74 = var58;
         }

         Function1 var29 = (Function1)var74;
         var2.endReplaceGroup();
         Modifier var26 = GraphicsLayerModifierKt.graphicsLayer(var70, var29);
         ContentScale var30 = ContentScale.Companion.getFit();
         ImageLoader var33 = IntercomImageLoaderKt.getImageLoader(this.$context);
         Builder var38 = new Builder(this.$context).data(this.$file.getUri());
         Builder var42 = var38;
         int var46 = 0;
         var42.crossfade(true);
         ImageRequest var71 = var38.build();
         String var75 = "Image";
         ImageLoader var77 = var33;
         Modifier var78 = var26;
         Object var10004 = null;
         var2.startReplaceGroup(-832735561);
         ComposerKt.sourceInformation(var2, "CC(remember):ZoomableImage.kt#9igjgp");
         Composer var43 = var2;
         boolean var47 = var2.changed(this.$imageAspectRatio$delegate);
         MutableFloatState var50 = this.$imageAspectRatio$delegate;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var43.rememberedValue();
         int var59/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var10005;
         if (!var47 && it$ivxxx != Composer.Companion.getEmpty()) {
            var10005 = it$ivxxx;
         } else {
            Object var24 = null;
            Modifier var68 = var26;
            ImageLoader var67 = var33;
            String var65 = "Image";
            ImageRequest var63 = var71;
            int var60 = 0;
            final androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1180_0 = var50;
            Function1 var25 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1180_0) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5.invoke$lambda$6$lambda$5(recoveredExpressionCapture_1180_0, (coil.compose.AsyncImagePainter.State) ((coil.compose.AsyncImagePainter.State)recoveredExpressionArgument_1180_0)));
            var71 = var63;
            var75 = var65;
            var77 = var67;
            var78 = var68;
            var10004 = var24;
            Object var19 = var25;
            var43.updateRememberedValue(var19);
            var10005 = var19;
         }

         Function1 var39 = (Function1)var10005;
         var2.endReplaceGroup();
         AsyncImageKt.recovered$b6a46e909c1fcc39(
            var71, var75, var77, var78, (Function1)var10004, var39, null, var30, 0.0F, null, 0, false, null, var2, 12582960, 0, 8016
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final long invoke$clampOffset(float var0, float var1, MutableFloatState var2, long var3, float var5) {
      Pair var6 = ZoomableImageKt.fittedContentSize(ZoomableImageKt.access$ZoomableImage$lambda$10(var2), var0, var1);
      float var7 = ((Number)var6.component1()).floatValue();
      float var8 = ((Number)var6.component2()).floatValue();
      float var9 = RangesKt.coerceAtLeast((var5 * var7 - var0) / 2.0F, 0.0F);
      float var10 = RangesKt.coerceAtLeast((var5 * var8 - var1) / 2.0F, 0.0F);
      int var11 = 0;
      long var12 = var3;
      int var14 = 0;
      int var15 = (int)(var12 >> 32);
      int var16 = 0;
      float var21 = RangesKt.coerceIn(Float.intBitsToFloat(var15), -var9, var9);
      int var22 = 0;
      long value$iv$ivx = var3;
      int var26 = 0;
      int bits$iv$iv$ivx = (int)(value$iv$ivx & 4294967295L);
      int $i$f$floatFromBitsx = 0;
      float var23 = RangesKt.coerceIn(Float.intBitsToFloat(bits$iv$iv$ivx), -var10, var10);
      int var24 = 0;
      float var25 = var21;
      float var27 = var23;
      int var29 = 0;
      long var30 = Float.floatToRawIntBits(var25);
      long var19 = Float.floatToRawIntBits(var27);
      return Offset.recovered$8c837f3e34208708(var30 << 32 | var19 & 4294967295L);
   }

   private static final Unit invoke$lambda$3$lambda$2(MutableFloatState var0, MutableState var1, GraphicsLayerScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$graphicsLayer");
      var2.setScaleX(ZoomableImageKt.access$ZoomableImage$lambda$4(var0));
      var2.setScaleY(ZoomableImageKt.access$ZoomableImage$lambda$4(var0));
      long var3 = ZoomableImageKt.access$ZoomableImage$lambda$7(var1);
      int var5 = 0;
      long var6 = var3;
      int var8 = 0;
      int var9 = (int)(var6 >> 32);
      int var10 = 0;
      var2.setTranslationX(Float.intBitsToFloat(var9));
      long arg0$ivx = ZoomableImageKt.access$ZoomableImage$lambda$7(var1);
      int var12 = 0;
      long value$iv$ivx = arg0$ivx;
      int var14 = 0;
      int bits$iv$iv$ivx = (int)(value$iv$ivx & 4294967295L);
      int $i$f$floatFromBitsx = 0;
      var2.setTranslationY(Float.intBitsToFloat(bits$iv$iv$ivx));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5(MutableFloatState var0, State var1) {
      Intrinsics.checkNotNullParameter(var1, "state");
      if (var1 instanceof Success) {
         Drawable var2 = ((Success)var1).getResult().getDrawable();
         int var3 = var2.getIntrinsicWidth();
         int var4 = var2.getIntrinsicHeight();
         if (var3 > 0 && var4 > 0) {
            ZoomableImageKt.access$ZoomableImage$lambda$11(var0, (float)var3 / var4);
         }
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final long access$invoke$clampOffset(float var0, float var1, MutableFloatState var2, long var3, float var5) {
      return invoke$clampOffset(var0, var1, var2, var3, var5);
   }
}
