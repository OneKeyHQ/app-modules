package io.intercom.android.sdk.m5.inbox;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent;
import io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$RefreshInbox;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxViewModel$2$2 implements FlowCollector {
   // $VF: synthetic field
   final InboxViewModel this$0;

   InboxViewModel$2$2(InboxViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ParsedNexusEvent$ConversationNexusEvent var1, Continuation var2) {
      Object var10000 = InboxViewModel.access$get_effect$p(this.this$0).emit(InboxUiEffects$RefreshInbox.INSTANCE, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$ConversationNexusEvent)var1, var2);
   }
}
