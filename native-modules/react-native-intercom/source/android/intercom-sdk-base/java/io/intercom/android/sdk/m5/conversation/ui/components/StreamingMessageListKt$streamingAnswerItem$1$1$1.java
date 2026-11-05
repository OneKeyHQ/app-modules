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
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

@DebugMetadata(
   f = "StreamingMessageList.kt",
   l = 452,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$streamingAnswerItem$1$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nStreamingMessageList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$streamingAnswerItem$1$1$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,578:1\n32#2:579\n17#2:580\n19#2:584\n46#3:581\n51#3:583\n105#4:582\n*S KotlinDebug\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$streamingAnswerItem$1$1$1\n*L\n451#1:579\n451#1:580\n451#1:584\n451#1:581\n451#1:583\n451#1:582\n*E\n"
)
final class StreamingMessageListKt$streamingAnswerItem$1$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final MutableState $following$delegate;

   StreamingMessageListKt$streamingAnswerItem$1$1$1(LazyListState var1, MutableState var2, Continuation var3) {
      super(2, var3);
      this.$lazyListState = var1;
      this.$following$delegate = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var2 = this.$lazyListState.getInteractionSource().getInteractions();
            int var3 = 0;
            Flow var4 = var2;
            int var5 = 0;
            Flow var6 = var4;
            int var7 = 0;
            int var8 = 0;
            Flow var10000 = new StreamingMessageListKt$streamingAnswerItem$1$1$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$filterIsInstance$1(var6);
            FlowCollector var10001 = new StreamingMessageListKt$streamingAnswerItem$1$1$1$1(this.$following$delegate);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var9) {
               return var9;
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
      return new StreamingMessageListKt$streamingAnswerItem$1$1$1(this.$lazyListState, this.$following$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((StreamingMessageListKt$streamingAnswerItem$1$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
