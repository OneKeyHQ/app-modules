package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.gestures.TapGestureDetectorKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.input.pointer.AwaitPointerEventScope;
import androidx.compose.ui.input.pointer.PointerInputChange;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "ZoomableImage.kt",
   l = {248, 249},
   i = {0, 1},
   s = {"L$0", "L$0"},
   n = {"$this$withTimeoutOrNull", "secondDown"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Landroidx/compose/ui/geometry/Offset;", "Landroidx/compose/ui/input/pointer/AwaitPointerEventScope;"}
)
final class ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1 extends RestrictedSuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final float $touchSlop;

   ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1(float var1, Continuation var2) {
      super(2, var2);
      this.$touchSlop = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      AwaitPointerEventScope var2;
      Object var5;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var2 = (AwaitPointerEventScope)this.L$0;
            Continuation var10003 = this;
            this.L$0 = var2;
            this.label = 1;
            var5 = TapGestureDetectorKt.awaitFirstDown$default(var2, false, null, var10003, 2, null);
            if (var5 == var4) {
               return var4;
            }
            break;
         case 1:
            var2 = (AwaitPointerEventScope)this.L$0;
            ResultKt.throwOnFailure(var1);
            var5 = var1;
            break;
         case 2:
            PointerInputChange var3 = (PointerInputChange)this.L$0;
            ResultKt.throwOnFailure(var1);
            return (Boolean)var1 ? Offset.recovered$ae98585edc0e5b3c(var3.recovered$d4f261e011078de8()) : null;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      PointerInputChange var7 = (PointerInputChange)var5;
      long var10001 = var7.recovered$c6cceede56ec0b3a();
      float var10002 = this.$touchSlop;
      Continuation var6 = this;
      this.L$0 = var7;
      this.label = 2;
      var5 = ZoomableImageKt.recovered$407cb96d62b28197(var2, var10001, var10002, var6);
      if (var5 == var4) {
         return var4;
      } else {
         return (Boolean)var5 ? Offset.recovered$ae98585edc0e5b3c(var7.recovered$d4f261e011078de8()) : null;
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1 var3 = new ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1(
         this.$touchSlop, var2
      );
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(AwaitPointerEventScope var1, Continuation var2) {
      return ((ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((AwaitPointerEventScope)var1, (Continuation)var2);
   }
}
