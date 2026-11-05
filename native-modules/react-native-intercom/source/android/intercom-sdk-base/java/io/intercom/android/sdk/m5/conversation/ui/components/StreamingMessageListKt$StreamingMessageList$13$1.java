package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import java.util.List;
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
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "StreamingMessageList.kt",
   l = 169,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$StreamingMessageList$13$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nStreamingMessageList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$StreamingMessageList$13$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,578:1\n49#2:579\n51#2:583\n46#3:580\n51#3:582\n105#4:581\n*S KotlinDebug\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$StreamingMessageList$13$1\n*L\n168#1:579\n168#1:583\n168#1:580\n168#1:582\n168#1:581\n*E\n"
)
final class StreamingMessageListKt$StreamingMessageList$13$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final MutableState $isListAtTheBottom$delegate;

   StreamingMessageListKt$StreamingMessageList$13$1(LazyListState var1, MutableState var2, Continuation var3) {
      super(2, var3);
      this.$lazyListState = var1;
      this.$isListAtTheBottom$delegate = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_479_0 = ((io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$StreamingMessageList$13$1) this).$lazyListState;
            Flow var2 = SnapshotStateKt.snapshotFlow(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$StreamingMessageList$13$1.invokeSuspend$lambda$0(recoveredExpressionCapture_479_0)));
            int var3 = 0;
            Flow var4 = var2;
            int var5 = 0;
            int var6 = 0;
            Flow var10000 = new StreamingMessageListKt$StreamingMessageList$13$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var4);
            Function2 var10001 = new StreamingMessageListKt$StreamingMessageList$13$1$3(this.$isListAtTheBottom$delegate, null);
            Continuation var10002 = this;
            this.label = 1;
            if (FlowKt.collectLatest(var10000, var10001, var10002) == var7) {
               return var7;
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
      return new StreamingMessageListKt$StreamingMessageList$13$1(this.$lazyListState, this.$isListAtTheBottom$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((StreamingMessageListKt$StreamingMessageList$13$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final List invokeSuspend$lambda$0(LazyListState var0) {
      return var0.getLayoutInfo().getVisibleItemsInfo();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
