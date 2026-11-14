package io.intercom.android.sdk.m5.inbox;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;

@DebugMetadata(f = "InboxViewModel.kt", l = 131, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.m5.inbox.InboxViewModel$4$1$2")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class InboxViewModel$4$1$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final InboxViewModel this$0;
   // $VF: synthetic field
   final String $conversationId;

   InboxViewModel$4$1$2(InboxViewModel var1, String var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$conversationId = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Continuation var10001 = this;
            this.label = 1;
            if (DelayKt.delay(5000L, var10001) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      InboxViewModel.access$clearTypingState(this.this$0, this.$conversationId);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new InboxViewModel$4$1$2(this.this$0, this.$conversationId, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((InboxViewModel$4$1$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
