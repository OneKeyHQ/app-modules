package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.pager.PagerState;
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
   f = "PreviewRootScreen.kt",
   l = 171,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8$1$6$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PreviewRootScreenKt$PreviewRootScreenContent$8$1$6$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final PagerState $pagerState;
   // $VF: synthetic field
   final int $it;

   PreviewRootScreenKt$PreviewRootScreenContent$8$1$6$1$1(PagerState var1, int var2, Continuation var3) {
      super(2, var3);
      this.$pagerState = var1;
      this.$it = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            PagerState var10000 = this.$pagerState;
            int var10001 = this.$it;
            Continuation var10004 = this;
            this.label = 1;
            if (PagerState.animateScrollToPage$default(var10000, var10001, 0.0F, null, var10004, 6, null) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new PreviewRootScreenKt$PreviewRootScreenContent$8$1$6$1$1(this.$pagerState, this.$it, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PreviewRootScreenKt$PreviewRootScreenContent$8$1$6$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
