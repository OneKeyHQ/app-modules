package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$AblyReconnectedWithoutResume;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationViewModel$7$1 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$7$1(ConversationViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ParsedNexusEvent$AblyReconnectedWithoutResume var1, Continuation var2) {
      ConversationViewModel.access$getTwig$p(this.this$0).d("Ably reconnected without resume - fetching conversation");
      Object var10000 = ConversationViewModel.access$getRefreshConversationUseCase$p(this.this$0)
         .invoke(ConversationViewModel.access$getClientState$p(this.this$0), GetConversationReason.NEXUS_CONNECTED, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$AblyReconnectedWithoutResume)var1, var2);
   }
}
