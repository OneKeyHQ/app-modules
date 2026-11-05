package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.gestures.ScrollExtensionsKt;
import androidx.compose.foundation.gestures.ScrollableState;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
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
   f = "StreamingMessageList.kt",
   l = 162,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$StreamingMessageList$12$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class StreamingMessageListKt$StreamingMessageList$12$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final State $keyboardAsState$delegate;
   // $VF: synthetic field
   final MutableState $isListAtTheBottom$delegate;
   // $VF: synthetic field
   final MutableFloatState $keyboardScrollOffset$delegate;

   StreamingMessageListKt$StreamingMessageList$12$1(LazyListState var1, State var2, MutableState var3, MutableFloatState var4, Continuation var5) {
      super(2, var5);
      this.$lazyListState = var1;
      this.$keyboardAsState$delegate = var2;
      this.$isListAtTheBottom$delegate = var3;
      this.$keyboardScrollOffset$delegate = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (StreamingMessageListKt.access$StreamingMessageList$lambda$25(this.$keyboardAsState$delegate).isDismissed()
               && !StreamingMessageListKt.access$StreamingMessageList$lambda$31(this.$isListAtTheBottom$delegate)
               && StreamingMessageListKt.access$StreamingMessageList$lambda$27(this.$keyboardScrollOffset$delegate) > 0.0F) {
               ScrollableState var10000 = this.$lazyListState;
               float var10001 = -StreamingMessageListKt.access$StreamingMessageList$lambda$27(this.$keyboardScrollOffset$delegate);
               Continuation var10003 = this;
               this.label = 1;
               if (ScrollExtensionsKt.animateScrollBy$default(var10000, var10001, null, var10003, 2, null) == var2) {
                  return var2;
               }
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
      return new StreamingMessageListKt$StreamingMessageList$12$1(
         this.$lazyListState, this.$keyboardAsState$delegate, this.$isListAtTheBottom$delegate, this.$keyboardScrollOffset$delegate, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((StreamingMessageListKt$StreamingMessageList$12$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
