package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.geometry.Offset;
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
   c = "io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$ZoomableImage$4$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ZoomableImageKt$ZoomableImage$4$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final boolean $isCurrentPage;
   // $VF: synthetic field
   final MutableFloatState $scale$delegate;
   // $VF: synthetic field
   final MutableState $offset$delegate;

   ZoomableImageKt$ZoomableImage$4$1(boolean var1, MutableFloatState var2, MutableState var3, Continuation var4) {
      super(2, var4);
      this.$isCurrentPage = var1;
      this.$scale$delegate = var2;
      this.$offset$delegate = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (!this.$isCurrentPage) {
               ZoomableImageKt.access$ZoomableImage$lambda$5(this.$scale$delegate, 1.0F);
               ZoomableImageKt.access$ZoomableImage$lambda$8(this.$offset$delegate, Offset.Companion.recovered$86a8752c359fef5c());
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ZoomableImageKt$ZoomableImage$4$1(this.$isCurrentPage, this.$scale$delegate, this.$offset$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ZoomableImageKt$ZoomableImage$4$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
