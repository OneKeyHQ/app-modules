package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function3;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$isFinStreamingActive$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u0012\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\n",
   d2 = {
         "<anonymous>",
         "",
         "streamState",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "state",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;"
   }
)
final class ConversationViewModel$isFinStreamingActive$1 extends SuspendLambda implements Function3 {
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   Object L$1;

   ConversationViewModel$isFinStreamingActive$1(Continuation var1) {
      super(3, var1);
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            StreamingMessageState var2 = (StreamingMessageState)this.L$0;
            ConversationClientState var3 = (ConversationClientState)this.L$1;
            return Boxing.boxBoolean(
               var2 instanceof StreamingMessageState$Streaming
                  && !var3.committedPartUuids$intercom_sdk_base_release().contains(((StreamingMessageState$Streaming)var2).getUuid())
            );
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   public final Object invoke(StreamingMessageState var1, ConversationClientState var2, Continuation var3) {
      ConversationViewModel$isFinStreamingActive$1 var4 = new ConversationViewModel$isFinStreamingActive$1(var3);
      var4.L$0 = var1;
      var4.L$1 = var2;
      return var4.invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      return this.invoke((StreamingMessageState)var1, (ConversationClientState)var2, (Continuation)var3);
   }
}
