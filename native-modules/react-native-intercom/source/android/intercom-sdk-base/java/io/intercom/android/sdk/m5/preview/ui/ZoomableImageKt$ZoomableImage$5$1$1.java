package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "ZoomableImage.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$5$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ZoomableImageKt$ZoomableImage$5$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MutableState $offset$delegate;
   // $VF: synthetic field
   final MutableFloatState $scale$delegate;
   // $VF: synthetic field
   final float $containerWidth;
   // $VF: synthetic field
   final float $containerHeight;
   // $VF: synthetic field
   final MutableFloatState $imageAspectRatio$delegate;

   ZoomableImageKt$ZoomableImage$5$1$1(MutableState var1, MutableFloatState var2, float var3, float var4, MutableFloatState var5, Continuation var6) {
      super(2, var6);
      this.$offset$delegate = var1;
      this.$scale$delegate = var2;
      this.$containerWidth = var3;
      this.$containerHeight = var4;
      this.$imageAspectRatio$delegate = var5;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            ZoomableImageKt.access$ZoomableImage$lambda$8(
               this.$offset$delegate,
               ZoomableImageKt$ZoomableImage$5.access$invoke$clampOffset(
                  this.$containerWidth,
                  this.$containerHeight,
                  this.$imageAspectRatio$delegate,
                  ZoomableImageKt.access$ZoomableImage$lambda$7(this.$offset$delegate),
                  ZoomableImageKt.access$ZoomableImage$lambda$4(this.$scale$delegate)
               )
            );
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ZoomableImageKt$ZoomableImage$5$1$1(
         this.$offset$delegate, this.$scale$delegate, this.$containerWidth, this.$containerHeight, this.$imageAspectRatio$delegate, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ZoomableImageKt$ZoomableImage$5$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
