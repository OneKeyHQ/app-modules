package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

@DebugMetadata(
   f = "StreamingMessageList.kt",
   l = 456,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$streamingAnswerItem$1$2$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class StreamingMessageListKt$streamingAnswerItem$1$2$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Function0 $isAtBottom;
   // $VF: synthetic field
   final MutableState $following$delegate;

   StreamingMessageListKt$streamingAnswerItem$1$2$1(Function0 var1, MutableState var2, Continuation var3) {
      super(2, var3);
      this.$isAtBottom = var1;
      this.$following$delegate = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_484_0 = ((io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$streamingAnswerItem$1$2$1) this).$isAtBottom;
            Flow var10000 = SnapshotStateKt.snapshotFlow(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$streamingAnswerItem$1$2$1.invokeSuspend$lambda$0(recoveredExpressionCapture_484_0)));
            FlowCollector var10001 = new StreamingMessageListKt$streamingAnswerItem$1$2$1$2(this.$following$delegate);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var2) {
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
      return new StreamingMessageListKt$streamingAnswerItem$1$2$1(this.$isAtBottom, this.$following$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((StreamingMessageListKt$streamingAnswerItem$1$2$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final boolean invokeSuspend$lambda$0(Function0 var0) {
      return (Boolean)var0.invoke();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
