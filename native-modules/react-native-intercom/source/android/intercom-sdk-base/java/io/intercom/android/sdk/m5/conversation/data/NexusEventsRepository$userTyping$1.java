package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.nexus.NexusEvent;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableSharedFlow;

@DebugMetadata(
   f = "NexusEventsRepository.kt",
   l = 40,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.data.NexusEventsRepository$userTyping$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class NexusEventsRepository$userTyping$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final NexusEventsRepository this$0;
   // $VF: synthetic field
   final String $conversationId;

   NexusEventsRepository$userTyping$1(NexusEventsRepository var1, String var2, Continuation var3) {
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
            MutableSharedFlow var10000 = NexusEventsRepository.access$getTypingEventsFlow$p(this.this$0);
            NexusEvent var10001 = NexusEvent.getUserIsTypingEvent(
               this.$conversationId, NexusEventsRepository.access$getUserIdentity$p(this.this$0).getIntercomId()
            );
            Intrinsics.checkNotNullExpressionValue(var10001, "getUserIsTypingEvent(...)");
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.emit(var10001, var10002) == var2) {
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
      return new NexusEventsRepository$userTyping$1(this.this$0, this.$conversationId, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((NexusEventsRepository$userTyping$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
