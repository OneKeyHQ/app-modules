package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.lazy.LazyListState;
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
   f = "PreviewBottomBar.kt",
   l = 131,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt$ThumbnailList$2$1$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PreviewBottomBarKt$ThumbnailList$2$1$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final LazyListState $listState;
   // $VF: synthetic field
   final int $currentPage;

   PreviewBottomBarKt$ThumbnailList$2$1$2(LazyListState var1, int var2, Continuation var3) {
      super(2, var3);
      this.$listState = var1;
      this.$currentPage = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            LazyListState var10000 = this.$listState;
            int var10001 = this.$currentPage;
            Continuation var10003 = this;
            this.label = 1;
            if (LazyListState.animateScrollToItem$default(var10000, var10001, 0, var10003, 2, null) == var2) {
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
      return new PreviewBottomBarKt$ThumbnailList$2$1$2(this.$listState, this.$currentPage, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PreviewBottomBarKt$ThumbnailList$2$1$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
