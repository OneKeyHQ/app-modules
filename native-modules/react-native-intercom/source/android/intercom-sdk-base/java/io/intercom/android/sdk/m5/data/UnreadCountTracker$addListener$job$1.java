package io.intercom.android.sdk.m5.data;

import io.intercom.android.sdk.UnreadConversationCountListener;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.StateFlow;

@DebugMetadata(
   f = "UnreadCountTracker.kt",
   l = 22,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.data.UnreadCountTracker$addListener$job$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class UnreadCountTracker$addListener$job$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final UnreadCountTracker this$0;
   // $VF: synthetic field
   final UnreadConversationCountListener $listener;

   UnreadCountTracker$addListener$job$1(UnreadCountTracker var1, UnreadConversationCountListener var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$listener = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            StateFlow var10000 = this.this$0.getDataLayer().getUnreadConversationIds();
            FlowCollector var10001 = new UnreadCountTracker$addListener$job$1$1(this.$listener);
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

      throw new KotlinNothingValueException();
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new UnreadCountTracker$addListener$job$1(this.this$0, this.$listener, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((UnreadCountTracker$addListener$job$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
