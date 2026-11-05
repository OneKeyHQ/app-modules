package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.input.pointer.PointerInputEventHandler;
import androidx.compose.ui.input.pointer.PointerInputScope;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nZoomableImage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt$ZoomableImage$5$2$1\n+ 2 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n+ 3 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 4 Offset.kt\nandroidx/compose/ui/geometry/Offset\n*L\n1#1,277:1\n30#2:278\n30#2:283\n53#3,3:279\n53#3,3:284\n150#4:282\n*S KotlinDebug\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt$ZoomableImage$5$2$1\n*L\n104#1:278\n117#1:283\n104#1:279,3\n117#1:284,3\n107#1:282\n*E\n"
)
final class ZoomableImageKt$ZoomableImage$5$2$1 implements PointerInputEventHandler {
   // $VF: synthetic field
   final MutableFloatState $scale$delegate;
   // $VF: synthetic field
   final float $containerWidth;
   // $VF: synthetic field
   final float $containerHeight;
   // $VF: synthetic field
   final MutableState $offset$delegate;
   // $VF: synthetic field
   final MutableFloatState $imageAspectRatio$delegate;

   ZoomableImageKt$ZoomableImage$5$2$1(MutableFloatState var1, float var2, float var3, MutableState var4, MutableFloatState var5) {
      this.$scale$delegate = var1;
      this.$containerWidth = var2;
      this.$containerHeight = var3;
      this.$offset$delegate = var4;
      this.$imageAspectRatio$delegate = var5;
   }

   @Override
   public final Object invoke(PointerInputScope var1, Continuation var2) {
      final androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1183_0 = ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$scale$delegate;
      Object var10000 = ZoomableImageKt.access$detectZoomPanAndDoubleTap(
         var1,
         ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1.invoke$lambda$0(recoveredExpressionCapture_1183_0)),
         io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1.recoveredExpressionFactory_1181(((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$containerWidth, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$containerHeight, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$scale$delegate, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$offset$delegate, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$imageAspectRatio$delegate),
         io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1.recoveredExpressionFactory_1182(((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$containerWidth, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$containerHeight, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$scale$delegate, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$offset$delegate, ((io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1) this).$imageAspectRatio$delegate),
         var2
      );
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   private static final boolean invoke$lambda$0(MutableFloatState var0) {
      return ZoomableImageKt.access$ZoomableImage$lambda$4(var0) > 1.0F;
   }

   private static final Unit invoke$lambda$1(float var0, float var1, MutableFloatState var2, MutableState var3, MutableFloatState var4, Offset var5) {
      if (ZoomableImageKt.access$ZoomableImage$lambda$4(var2) > 1.0F) {
         ZoomableImageKt.access$ZoomableImage$lambda$5(var2, 1.0F);
         ZoomableImageKt.access$ZoomableImage$lambda$8(var3, Offset.Companion.recovered$86a8752c359fef5c());
      } else {
         float var8 = var0 / 2.0F;
         float var9 = var1 / 2.0F;
         int var10 = 0;
         float var11 = var8;
         float var12 = var9;
         int var13 = 0;
         long var14 = Float.floatToRawIntBits(var11);
         long var16 = Float.floatToRawIntBits(var12);
         long var6 = Offset.recovered$8c837f3e34208708(var14 << 32 | var16 & 4294967295L);
         long var18 = Offset.recovered$557cc49f074d875a(var5.recovered$45f9de7b85e0e0b5(), var6);
         ZoomableImageKt.access$ZoomableImage$lambda$5(var2, 2.5F);
         var10 = 0;
         ZoomableImageKt.access$ZoomableImage$lambda$8(
            var3,
            ZoomableImageKt$ZoomableImage$5.access$invoke$clampOffset(
               var0, var1, var4, Offset.recovered$f5dbbbe7fe26c3bd(Offset.recovered$8c837f3e34208708(var18 ^ -9223372034707292160L), 1.5F), 2.5F
            )
         );
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$2(
      float var0, float var1, MutableFloatState var2, MutableState var3, MutableFloatState var4, Offset var5, Offset var6, float var7
   ) {
      float var8 = ZoomableImageKt.access$ZoomableImage$lambda$4(var2);
      float var9 = RangesKt.coerceIn(var8 * var7, 1.0F, 5.0F);
      if (var9 == 1.0F) {
         ZoomableImageKt.access$ZoomableImage$lambda$5(var2, 1.0F);
         ZoomableImageKt.access$ZoomableImage$lambda$8(var3, Offset.Companion.recovered$86a8752c359fef5c());
      } else {
         float var12 = var0 / 2.0F;
         float var13 = var1 / 2.0F;
         int var14 = 0;
         float var15 = var12;
         float var16 = var13;
         int var17 = 0;
         long var18 = Float.floatToRawIntBits(var15);
         long var20 = Float.floatToRawIntBits(var16);
         long var10 = Offset.recovered$8c837f3e34208708(var18 << 32 | var20 & 4294967295L);
         long var22 = ZoomableImageKt.recovered$62301fc7b8456b9b(
            var5.recovered$45f9de7b85e0e0b5(), var6.recovered$45f9de7b85e0e0b5(), var10, ZoomableImageKt.access$ZoomableImage$lambda$7(var3), var8, var9
         );
         ZoomableImageKt.access$ZoomableImage$lambda$5(var2, var9);
         ZoomableImageKt.access$ZoomableImage$lambda$8(var3, ZoomableImageKt$ZoomableImage$5.access$invoke$clampOffset(var0, var1, var4, var22, var9));
      }

      return Unit.INSTANCE;
   }

   private static kotlin.jvm.functions.Function1 recoveredExpressionFactory_1181(float recoveredExpressionCapture_1181_0, float recoveredExpressionCapture_1181_1, androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1181_2, androidx.compose.runtime.MutableState recoveredExpressionCapture_1181_3, androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1181_4) {
      return ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1181_0) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1.invoke$lambda$1(recoveredExpressionCapture_1181_0, recoveredExpressionCapture_1181_1, recoveredExpressionCapture_1181_2, recoveredExpressionCapture_1181_3, recoveredExpressionCapture_1181_4, (androidx.compose.ui.geometry.Offset) ((androidx.compose.ui.geometry.Offset)recoveredExpressionArgument_1181_0)));
   }

   private static kotlin.jvm.functions.Function3 recoveredExpressionFactory_1182(float recoveredExpressionCapture_1182_0, float recoveredExpressionCapture_1182_1, androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1182_2, androidx.compose.runtime.MutableState recoveredExpressionCapture_1182_3, androidx.compose.runtime.MutableFloatState recoveredExpressionCapture_1182_4) {
      return ((kotlin.jvm.functions.Function3) (recoveredExpressionArgument_1182_0, recoveredExpressionArgument_1182_1, recoveredExpressionArgument_1182_2) -> io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$2$1.invoke$lambda$2(recoveredExpressionCapture_1182_0, recoveredExpressionCapture_1182_1, recoveredExpressionCapture_1182_2, recoveredExpressionCapture_1182_3, recoveredExpressionCapture_1182_4, (androidx.compose.ui.geometry.Offset) ((androidx.compose.ui.geometry.Offset)recoveredExpressionArgument_1182_0), (androidx.compose.ui.geometry.Offset) ((androidx.compose.ui.geometry.Offset)recoveredExpressionArgument_1182_1), ((java.lang.Float)recoveredExpressionArgument_1182_2).floatValue()));
   }
}
