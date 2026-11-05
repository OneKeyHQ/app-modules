package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "StreamingMessageList.kt",
   l = 463,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$streamingAnswerItem$1$3$1$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "height", ""}
)
final class StreamingMessageListKt$streamingAnswerItem$1$3$1$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   int I$0;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final MutableState $following$delegate;

   StreamingMessageListKt$streamingAnswerItem$1$3$1$2(LazyListState var1, MutableState var2, Continuation var3) {
      super(2, var3);
      this.$lazyListState = var1;
      this.$following$delegate = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            int var2 = this.I$0;
            if (var2 > 0 && StreamingMessageListKt$streamingAnswerItem$1.access$invoke$lambda$4(this.$following$delegate)) {
               LazyListState var10000 = this.$lazyListState;
               Continuation var10003 = this;
               this.label = 1;
               if (LazyListState.animateScrollToItem$default(var10000, Integer.MAX_VALUE, 0, var10003, 2, null) == var3) {
                  return var3;
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
      StreamingMessageListKt$streamingAnswerItem$1$3$1$2 var3 = new StreamingMessageListKt$streamingAnswerItem$1$3$1$2(
         this.$lazyListState, this.$following$delegate, var2
      );
      var3.I$0 = ((Number)var1).intValue();
      return var3;
   }

   public final Object invoke(int var1, Continuation var2) {
      return ((StreamingMessageListKt$streamingAnswerItem$1$3$1$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke(((Number)var1).intValue(), (Continuation)var2);
   }
}
