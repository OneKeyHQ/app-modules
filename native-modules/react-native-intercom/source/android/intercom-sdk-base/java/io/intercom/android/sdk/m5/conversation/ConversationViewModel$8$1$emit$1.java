package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 399,
   i = {0, 0},
   s = {"L$0", "L$1"},
   n = {"this", "newNetworkState"},
   m = "emit",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$8$1"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationViewModel$8$1$emit$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final ConversationViewModel$8$1 this$0;
   int label;

   ConversationViewModel$8$1$emit$1(ConversationViewModel$8$1 var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.emit((NetworkState)null, this);
   }
}
