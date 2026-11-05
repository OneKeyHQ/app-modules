package io.intercom.android.sdk.m5.conversation.usecase;

import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "SendMessageUseCase.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.usecase.SendMessageUseCase$invoke$resolvedState$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "state", "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;"}
)
final class SendMessageUseCase$invoke$resolvedState$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   final String $clientUUID;

   SendMessageUseCase$invoke$resolvedState$1(String var1, Continuation var2) {
      super(2, var2);
      this.$clientUUID = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            ConversationClientState var2 = (ConversationClientState)this.L$0;
            if (var2.getConversationId() == null) {
               PendingMessage var10000 = (PendingMessage)var2.getPendingMessages().get(this.$clientUUID);
               if (var10000 != null ? !var10000.isFailed() : false) {
                  return Boxing.boxBoolean(false);
               }
            }

            return Boxing.boxBoolean(true);
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      SendMessageUseCase$invoke$resolvedState$1 var3 = new SendMessageUseCase$invoke$resolvedState$1(this.$clientUUID, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(ConversationClientState var1, Continuation var2) {
      return ((SendMessageUseCase$invoke$resolvedState$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((ConversationClientState)var1, (Continuation)var2);
   }
}
